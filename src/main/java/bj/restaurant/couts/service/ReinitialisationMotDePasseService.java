package bj.restaurant.couts.service;

import bj.restaurant.couts.util.DatabaseConnection;
import bj.restaurant.couts.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ReinitialisationMotDePasseService {
	private static final Logger LOGGER = Logger.getLogger(ReinitialisationMotDePasseService.class.getName());
	private static final SecureRandom ALEA = new SecureRandom();
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
	private static final Pattern JETON_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{43}$");
	private static final int LONGUEUR_MINIMALE_MOT_DE_PASSE = 12;

	public void demanderLien(String adresse) {
		ConfigurationCourriel configuration = ConfigurationCourriel.charger();
		Transport transport = null;
		String hashJetonEnregistre = null;
		try {
			transport = configuration.session().getTransport("smtp");
			transport.connect(configuration.hote(), configuration.port(),
					configuration.identifiant(), configuration.motDePasse());

			String email = adresse == null ? "" : adresse.trim().toLowerCase(Locale.ROOT);
			if (email.length() > 150 || !EMAIL_PATTERN.matcher(email).matches()) {
				return;
			}

			try (Connection connexion = DatabaseConnection.getConnection()) {
				connexion.setAutoCommit(false);
				try {
					Integer utilisateurId = trouverUtilisateurActif(connexion, email);
					if (utilisateurId == null || demandeRecente(connexion, utilisateurId)) {
						connexion.commit();
						return;
					}

					invaliderAnciensJetons(connexion, utilisateurId);
					String jeton = nouveauJeton();
					String hashJeton = hacherJeton(jeton);
					enregistrerJeton(connexion, utilisateurId, hashJeton);
					connexion.commit();
					hashJetonEnregistre = hashJeton;

					try {
						envoyerLien(transport, configuration, email, jeton);
					} catch (MessagingException exception) {
						invaliderJetonApresEchec(hashJetonEnregistre, exception);
						LOGGER.log(Level.SEVERE, "Impossible d'envoyer un lien de réinitialisation.", exception);
					}
					hashJetonEnregistre = null;
				} catch (SQLException exception) {
					annuler(connexion, exception);
					throw exception;
				}
			}
		} catch (SQLException | MessagingException exception) {
			if (hashJetonEnregistre != null) {
				invaliderJetonApresEchec(hashJetonEnregistre, exception);
			}
			throw new IllegalStateException("Le service de réinitialisation est indisponible.", exception);
		} finally {
			if (transport != null && transport.isConnected()) {
				try {
					transport.close();
				} catch (MessagingException exception) {
					LOGGER.log(Level.WARNING, "Impossible de fermer la connexion SMTP.", exception);
				}
			}
		}
	}

	public boolean jetonValide(String jeton) {
		if (!estJetonValide(jeton)) {
			return false;
		}
		String sql = """
				SELECT 1
				FROM reinitialisations_mot_de_passe
				WHERE jeton_hash = ? AND utilise_le IS NULL AND expire_le > CURRENT_TIMESTAMP
				""";
		try (Connection connexion = DatabaseConnection.getConnection();
				PreparedStatement requete = connexion.prepareStatement(sql)) {
			requete.setString(1, hacherJeton(jeton));
			try (ResultSet resultat = requete.executeQuery()) {
				return resultat.next();
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossible de vérifier le lien de réinitialisation.", exception);
		}
	}

	public boolean reinitialiser(String jeton, char[] motDePasse) {
		if (!estJetonValide(jeton) || motDePasse == null || motDePasse.length < LONGUEUR_MINIMALE_MOT_DE_PASSE) {
			return false;
		}

		char[] secret = Arrays.copyOf(motDePasse, motDePasse.length);
		try {
			String hashMotDePasse = PasswordUtil.hasher(secret);
			String hashJeton = hacherJeton(jeton);
			try (Connection connexion = DatabaseConnection.getConnection()) {
				connexion.setAutoCommit(false);
				try {
					Integer utilisateurId = consommerJeton(connexion, hashJeton);
					if (utilisateurId == null) {
						connexion.rollback();
						return false;
					}
					try (PreparedStatement requete = connexion.prepareStatement(
							"UPDATE utilisateurs SET mot_de_passe_hash = ? "
									+ "WHERE id_utilisateur = ? AND actif = TRUE")) {
						requete.setString(1, hashMotDePasse);
						requete.setInt(2, utilisateurId);
						if (requete.executeUpdate() != 1) {
							connexion.rollback();
							return false;
						}
					}
					try (PreparedStatement requete = connexion.prepareStatement(
							"UPDATE reinitialisations_mot_de_passe SET utilise_le = CURRENT_TIMESTAMP "
									+ "WHERE id_utilisateur = ? AND utilise_le IS NULL")) {
						requete.setInt(1, utilisateurId);
						requete.executeUpdate();
					}
					connexion.commit();
					return true;
				} catch (SQLException exception) {
					annuler(connexion, exception);
					throw exception;
				}
			} catch (SQLException exception) {
				throw new IllegalStateException("Impossible d'enregistrer le nouveau mot de passe.", exception);
			}
		} catch (GeneralSecurityException exception) {
			throw new IllegalStateException("Impossible de sécuriser le nouveau mot de passe.", exception);
		} finally {
			Arrays.fill(secret, '\0');
		}
	}

	private Integer trouverUtilisateurActif(Connection connexion, String email) throws SQLException {
		try (PreparedStatement requete = connexion.prepareStatement(
				"SELECT id_utilisateur FROM utilisateurs WHERE LOWER(email) = ? AND actif = TRUE FOR UPDATE")) {
			requete.setString(1, email);
			try (ResultSet resultat = requete.executeQuery()) {
				return resultat.next() ? resultat.getInt(1) : null;
			}
		}
	}

	private boolean demandeRecente(Connection connexion, int utilisateurId) throws SQLException {
		try (PreparedStatement requete = connexion.prepareStatement(
				"SELECT 1 FROM reinitialisations_mot_de_passe "
						+ "WHERE id_utilisateur = ? AND utilise_le IS NULL "
						+ "AND expire_le > CURRENT_TIMESTAMP "
						+ "AND cree_le > CURRENT_TIMESTAMP - INTERVAL '2 minutes' LIMIT 1")) {
			requete.setInt(1, utilisateurId);
			try (ResultSet resultat = requete.executeQuery()) {
				return resultat.next();
			}
		}
	}

	private void invaliderAnciensJetons(Connection connexion, int utilisateurId) throws SQLException {
		try (PreparedStatement requete = connexion.prepareStatement(
				"UPDATE reinitialisations_mot_de_passe SET utilise_le = CURRENT_TIMESTAMP "
						+ "WHERE id_utilisateur = ? AND utilise_le IS NULL")) {
			requete.setInt(1, utilisateurId);
			requete.executeUpdate();
		}
	}

	private void enregistrerJeton(Connection connexion, int utilisateurId, String hashJeton) throws SQLException {
		String sql = "INSERT INTO reinitialisations_mot_de_passe "
				+ "(id_utilisateur, jeton_hash, expire_le) VALUES (?, ?, ?)";
		try (PreparedStatement requete = connexion.prepareStatement(sql)) {
			requete.setInt(1, utilisateurId);
			requete.setString(2, hashJeton);
			requete.setTimestamp(3, Timestamp.from(Instant.now().plus(30, ChronoUnit.MINUTES)));
			requete.executeUpdate();
		}
	}

	private Integer consommerJeton(Connection connexion, String hashJeton) throws SQLException {
		String sql = """
				UPDATE reinitialisations_mot_de_passe
				SET utilise_le = CURRENT_TIMESTAMP
				WHERE jeton_hash = ? AND utilise_le IS NULL AND expire_le > CURRENT_TIMESTAMP
				RETURNING id_utilisateur
				""";
		try (PreparedStatement requete = connexion.prepareStatement(sql)) {
			requete.setString(1, hashJeton);
			try (ResultSet resultat = requete.executeQuery()) {
				return resultat.next() ? resultat.getInt(1) : null;
			}
		}
	}

	private void envoyerLien(Transport transport, ConfigurationCourriel configuration, String destinataire,
			String jeton) throws MessagingException {
		String lien = configuration.urlPublique() + "/reinitialiser-mot-de-passe?jeton="
				+ URLEncoder.encode(jeton, StandardCharsets.UTF_8);
		MimeMessage message = new MimeMessage(configuration.session());
		message.setFrom(new InternetAddress(configuration.expediteur()));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire, false));
		message.setSubject("Réinitialisation de votre mot de passe Brish", StandardCharsets.UTF_8.name());
		message.setText("Une demande de réinitialisation du mot de passe de votre compte Brish a été effectuée.\n\n"
				+ "Pour choisir un nouveau mot de passe, ouvrez ce lien dans les 30 prochaines minutes :\n"
				+ lien + "\n\nSi vous n'êtes pas à l'origine de cette demande, ignorez ce message.",
				StandardCharsets.UTF_8.name());
		transport.sendMessage(message, message.getAllRecipients());
	}

	private void invaliderJetonApresEchec(String hashJeton, Exception cause) {
		try (Connection connexion = DatabaseConnection.getConnection();
				PreparedStatement requete = connexion.prepareStatement(
						"UPDATE reinitialisations_mot_de_passe SET utilise_le = CURRENT_TIMESTAMP "
								+ "WHERE jeton_hash = ? AND utilise_le IS NULL")) {
			requete.setString(1, hashJeton);
			requete.executeUpdate();
		} catch (SQLException exception) {
			cause.addSuppressed(exception);
			LOGGER.log(Level.SEVERE, "Impossible d'invalider un lien non envoyé.", cause);
		}
	}

	private void annuler(Connection connexion, Exception cause) {
		try {
			connexion.rollback();
		} catch (SQLException exception) {
			cause.addSuppressed(exception);
		}
	}

	private String nouveauJeton() {
		byte[] octets = new byte[32];
		ALEA.nextBytes(octets);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
	}

	private String hacherJeton(String jeton) {
		try {
			byte[] empreinte = MessageDigest.getInstance("SHA-256")
					.digest(jeton.getBytes(StandardCharsets.UTF_8));
			return java.util.HexFormat.of().formatHex(empreinte);
		} catch (GeneralSecurityException exception) {
			throw new IllegalStateException("L'algorithme de hachage du jeton est indisponible.", exception);
		}
	}

	private boolean estJetonValide(String jeton) {
		return jeton != null && JETON_PATTERN.matcher(jeton).matches();
	}

	private record ConfigurationCourriel(String hote, int port, String identifiant, String motDePasse,
			String expediteur, String urlPublique, Session session) {
		private static ConfigurationCourriel charger() {
			String hote = variable("BRISH_SMTP_HOST");
			String portTexte = variable("BRISH_SMTP_PORT");
			String identifiant = variable("BRISH_SMTP_USERNAME");
			String motDePasse = variable("BRISH_SMTP_PASSWORD");
			String expediteur = variable("BRISH_SMTP_FROM");
			String url = variable("BRISH_PUBLIC_BASE_URL");
			if (hote == null || portTexte == null || identifiant == null || motDePasse == null
					|| expediteur == null || url == null) {
				throw new IllegalStateException("Configurez les variables SMTP et BRISH_PUBLIC_BASE_URL.");
			}
			int port;
			try {
				port = Integer.parseInt(portTexte);
				if (port < 1 || port > 65535) throw new NumberFormatException();
			} catch (NumberFormatException exception) {
				throw new IllegalStateException("BRISH_SMTP_PORT doit être un port valide.", exception);
			}
			String urlPublique = validerUrlPublique(url);
			Properties proprietes = new Properties();
			proprietes.setProperty("mail.smtp.host", hote);
			proprietes.setProperty("mail.smtp.port", Integer.toString(port));
			proprietes.setProperty("mail.smtp.auth", "true");
			proprietes.setProperty("mail.smtp.starttls.enable", "true");
			proprietes.setProperty("mail.smtp.starttls.required", "true");
			proprietes.setProperty("mail.smtp.ssl.checkserveridentity", "true");
			proprietes.setProperty("mail.smtp.connectiontimeout", "10000");
			proprietes.setProperty("mail.smtp.timeout", "10000");
			proprietes.setProperty("mail.smtp.writetimeout", "10000");
			Session session = Session.getInstance(proprietes, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(identifiant, motDePasse);
				}
			});
			return new ConfigurationCourriel(hote, port, identifiant, motDePasse,
					expediteur, urlPublique, session);
		}

		private static String variable(String nom) {
			String valeur = System.getenv(nom);
			return valeur == null || valeur.isBlank() ? null : valeur.trim();
		}

		private static String validerUrlPublique(String valeur) {
			try {
				URI uri = URI.create(valeur.trim()).normalize();
				String scheme = uri.getScheme();
				String host = uri.getHost();
				boolean local = "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host);
				if (scheme == null || host == null || uri.getUserInfo() != null
						|| uri.getRawQuery() != null || uri.getRawFragment() != null
						|| (!"https".equalsIgnoreCase(scheme) && !("http".equalsIgnoreCase(scheme) && local))) {
					throw new IllegalArgumentException();
				}
				String chemin = uri.getPath() == null ? "" : uri.getPath().replaceAll("/+$", "");
				return scheme.toLowerCase(Locale.ROOT) + "://" + uri.getRawAuthority() + chemin;
			} catch (IllegalArgumentException exception) {
				throw new IllegalStateException(
						"BRISH_PUBLIC_BASE_URL doit être une URL HTTPS (HTTP est permis sur localhost).", exception);
			}
		}
	}
}
