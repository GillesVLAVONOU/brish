package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.UtilisateurDAO;
import bj.restaurant.couts.model.Role;
import bj.restaurant.couts.Entities.Utilisateur;
import bj.restaurant.couts.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@ApplicationScoped
public class UtilisateurService {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
	private static final int LONGUEUR_MINIMALE_MOT_DE_PASSE = 12;

	@Inject
	private UtilisateurDAO utilisateurDAO;

	public List<Utilisateur> rechercher(String recherche) {
		return utilisateurDAO.rechercher(recherche);
	}

	public Optional<Utilisateur> trouverParId(Integer id) {
		return utilisateurDAO.trouverParId(id);
	}

	public Optional<Utilisateur> trouverActifParId(Integer id) {
		return utilisateurDAO.trouverActifParId(id);
	}

	public long compterTous() {
		return utilisateurDAO.compterTous();
	}

	public long compterActifs() {
		return utilisateurDAO.compterActifs();
	}

	public long compterActifs(Role role) {
		return utilisateurDAO.compterActifs(role);
	}

	@Transactional
	public Utilisateur enregistrer(Utilisateur saisie, String motDePasse, Integer administrateurId)
			throws GeneralSecurityException {
		if (saisie == null) {
			throw new IllegalArgumentException("Les informations de l'utilisateur sont obligatoires.");
		}
		String nom = saisie.getNom() == null ? "" : saisie.getNom().trim();
		String email = saisie.getEmail() == null ? "" : saisie.getEmail().trim().toLowerCase();
		if (nom.isBlank() || nom.length() > 100) {
			throw new IllegalArgumentException("Le nom est obligatoire et ne peut pas dépasser 100 caractères.");
		}
		if (email.length() > 150 || !EMAIL_PATTERN.matcher(email).matches()) {
			throw new IllegalArgumentException("Saisissez une adresse e-mail valide de 150 caractères maximum.");
		}
		if (saisie.getRole() == null) {
			throw new IllegalArgumentException("Sélectionnez un rôle valide.");
		}

		Utilisateur utilisateur;
		boolean creation = saisie.getId() == null;
		if (creation) {
			utilisateur = new Utilisateur();
			if (utilisateurDAO.emailExiste(email, null)) {
				throw new IllegalArgumentException("Cette adresse e-mail est déjà utilisée.");
			}
			utilisateur.setActif(true);
			mettreAJourMotDePasse(utilisateur, motDePasse, true);
		} else {
			utilisateur = utilisateurDAO.trouverParId(saisie.getId())
					.orElseThrow(() -> new IllegalArgumentException("L'utilisateur demandé n'existe pas."));
			if (utilisateurDAO.emailExiste(email, utilisateur.getId())) {
				throw new IllegalArgumentException("Cette adresse e-mail est déjà utilisée.");
			}
			verrouillerDernierAdministrateur(utilisateur,
					utilisateur.isActif() && saisie.getRole() != Role.ADMINISTRATEUR);
			mettreAJourMotDePasse(utilisateur, motDePasse, false);
		}

		if (utilisateur.getId() != null && utilisateur.getId().equals(administrateurId)
				&& saisie.getRole() != Role.ADMINISTRATEUR) {
			throw new IllegalArgumentException("Vous ne pouvez pas retirer votre propre rôle administrateur.");
		}
		utilisateur.setNom(nom);
		utilisateur.setEmail(email);
		utilisateur.setRole(saisie.getRole());
		return utilisateurDAO.sauvegarder(utilisateur);
	}

	@Transactional
	public void changerStatut(Integer utilisateurId, boolean actif, Integer administrateurId) {
		Utilisateur utilisateur = utilisateurDAO.trouverParId(utilisateurId)
				.orElseThrow(() -> new IllegalArgumentException("L'utilisateur demandé n'existe pas."));
		if (!actif && utilisateur.getId().equals(administrateurId)) {
			throw new IllegalArgumentException("Vous ne pouvez pas désactiver votre propre compte.");
		}
		verrouillerDernierAdministrateur(utilisateur, !actif);
		utilisateur.setActif(actif);
	}

	private void verrouillerDernierAdministrateur(Utilisateur utilisateur, boolean seraRetire) {
		if (seraRetire && utilisateur.isActif() && utilisateur.getRole() == Role.ADMINISTRATEUR) {
			long administrateursActifs = utilisateurDAO.verrouillerEtCompterAdministrateursActifs();
			if (administrateursActifs <= 1) {
				throw new IllegalArgumentException("Le dernier administrateur actif ne peut pas être désactivé ni rétrogradé.");
			}
		}
	}

	private void mettreAJourMotDePasse(Utilisateur utilisateur, String motDePasse, boolean obligatoire)
			throws GeneralSecurityException {
		String valeur = motDePasse == null ? "" : motDePasse;
		if (valeur.isEmpty()) {
			if (obligatoire) {
				throw new IllegalArgumentException("Un mot de passe provisoire est requis pour créer un utilisateur.");
			}
			return;
		}
		if (valeur.length() < LONGUEUR_MINIMALE_MOT_DE_PASSE) {
			throw new IllegalArgumentException("Le mot de passe provisoire doit contenir au moins 12 caractères.");
		}
		char[] secret = valeur.toCharArray();
		try {
			utilisateur.setMotDePasseHash(PasswordUtil.hasher(secret));
		} finally {
			java.util.Arrays.fill(secret, '\0');
		}
	}
}
