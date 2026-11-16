package bj.restaurant.couts.controller;

import bj.restaurant.couts.model.Role;
import bj.restaurant.couts.model.DroitsModule;
import bj.restaurant.couts.model.DroitsPersonnalises;
import bj.restaurant.couts.model.DroitsRole;
import bj.restaurant.couts.model.ModuleApplication;
import bj.restaurant.couts.Entities.Utilisateur;
import bj.restaurant.couts.service.AutorisationService;
import bj.restaurant.couts.service.UtilisateurService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map;

@WebServlet("/utilisateurs")
public class UtilisateurServlet extends HttpServlet {
	private static final String JETON_CSRF = "jetonAdministration";
	private static final SecureRandom ALEA = new SecureRandom();
	private static final Map<String, String> MESSAGES = Map.of(
			"cree", "L'utilisateur a été créé.",
			"modifie", "Les informations de l'utilisateur ont été enregistrées.",
			"active", "Le compte utilisateur a été activé.",
			"desactive", "Le compte utilisateur a été désactivé.",
			"droits-compte", "Les autorisations du compte ont été enregistrées.",
			"droits-role", "Les autorisations par rôle ont été enregistrées.");

	@Inject
	private UtilisateurService utilisateurService;
	@Inject
	private AutorisationService autorisationService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!estAdministrateur(request, response)) return;
		String action = valeur(request, "action");
		if ("droits".equals(action)) {
			Integer id = lireId(valeur(request, "id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Utilisateur utilisateur = utilisateurService.trouverParId(id).orElse(null);
			if (utilisateur == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			afficherDroitsUtilisateur(request, response, utilisateur, List.of());
			return;
		}
		if ("droits-role".equals(action)) {
			Role role = lireRole(valeur(request, "role"));
			if (role == null || role == Role.ADMINISTRATEUR) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			afficherDroitsRole(request, response, role, List.of());
			return;
		}
		if ("nouveau".equals(action)) {
			afficherFormulaire(request, response, new Utilisateur(), List.of());
			return;
		}
		if ("modifier".equals(action)) {
			Integer id = lireId(valeur(request, "id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Utilisateur utilisateur = utilisateurService.trouverParId(id).orElse(null);
			if (utilisateur == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			afficherFormulaire(request, response, utilisateur, List.of());
			return;
		}
		afficherListe(request, response, List.of());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!estAdministrateur(request, response)) return;
		if (!verifierJetonCsrf(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer administrateurId = lireId(String.valueOf(request.getSession(false).getAttribute("utilisateurId")));
		String action = valeur(request, "action");
		if ("enregistrerDroitsCompte".equals(action)) {
			Integer id = lireId(valeur(request, "id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Utilisateur utilisateur = utilisateurService.trouverParId(id).orElse(null);
			if (utilisateur == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			try {
				Map<String, String> valeurs = new java.util.HashMap<>();
				for (ModuleApplication module : ModuleApplication.values()) {
					valeurs.put("consultation_" + module.name(), valeur(request, "consultation_" + module.name()));
					valeurs.put("modification_" + module.name(), valeur(request, "modification_" + module.name()));
				}
				Map<ModuleApplication, DroitsPersonnalises> droits =
						autorisationService.lireDroitsPersonnalises(valeurs);
				autorisationService.enregistrerDroitsUtilisateur(utilisateur.getId(), utilisateur.getRole(), droits);
				response.sendRedirect(request.getContextPath() + "/utilisateurs?message=droits-compte");
			} catch (IllegalArgumentException exception) {
				afficherDroitsUtilisateur(request, response, utilisateur, List.of(exception.getMessage()));
			}
			return;
		}
		if ("enregistrerDroitsRole".equals(action)) {
			Role role = lireRole(valeur(request, "role"));
			if (role == null || role == Role.ADMINISTRATEUR) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Map<ModuleApplication, DroitsRole> droits = new EnumMap<>(ModuleApplication.class);
			for (ModuleApplication module : ModuleApplication.values()) {
				boolean consultation = request.getParameter("consultation_" + module.name()) != null;
				boolean modification = request.getParameter("modification_" + module.name()) != null;
				droits.put(module, new DroitsRole(consultation, modification));
			}
			try {
				autorisationService.enregistrerDroitsRole(role, droits);
				response.sendRedirect(request.getContextPath()
						+ "/utilisateurs?action=droits-role&role=" + role.name() + "&enregistre=1");
			} catch (IllegalArgumentException exception) {
				afficherDroitsRole(request, response, role, List.of(exception.getMessage()));
			}
			return;
		}
		if ("activer".equals(action) || "desactiver".equals(action)) {
			Integer id = lireId(valeur(request, "id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			try {
				utilisateurService.changerStatut(id, "activer".equals(action), administrateurId);
				response.sendRedirect(request.getContextPath() + "/utilisateurs?message=" + action);
			} catch (IllegalArgumentException exception) {
				afficherListe(request, response, List.of(exception.getMessage()));
			}
			return;
		}
		if (!"enregistrer".equals(action)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		Utilisateur saisie = new Utilisateur();
		String idSaisi = valeur(request, "id");
		if (!idSaisi.isBlank()) {
			Integer id = lireId(idSaisi);
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			saisie.setId(id);
		}
		saisie.setNom(valeur(request, "nom"));
		saisie.setEmail(valeur(request, "email"));
		saisie.setRole(lireRole(valeur(request, "role")));
		String motDePasse = valeur(request, "motDePasse");
		String confirmation = valeur(request, "confirmationMotDePasse");
		List<String> erreurs = new ArrayList<>();
		if (!motDePasse.equals(confirmation)) {
			erreurs.add("La confirmation du mot de passe ne correspond pas.");
		}
		if (erreurs.isEmpty()) {
			try {
				boolean creation = saisie.getId() == null;
				utilisateurService.enregistrer(saisie, motDePasse, administrateurId);
				if (saisie.getId() != null && saisie.getId().equals(administrateurId)) {
					HttpSession session = request.getSession(false);
					session.setAttribute("nomUtilisateur", saisie.getNom().trim());
					session.setAttribute("emailUtilisateur", saisie.getEmail().trim().toLowerCase());
				}
				response.sendRedirect(request.getContextPath() + "/utilisateurs?message="
						+ (creation ? "cree" : "modifie"));
				return;
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			} catch (GeneralSecurityException exception) {
				throw new ServletException("Impossible de préparer le mot de passe de l'utilisateur.", exception);
			}
		}
		afficherFormulaire(request, response, saisie, erreurs);
	}

	private void afficherListe(HttpServletRequest request, HttpServletResponse response, List<String> erreurs)
			throws ServletException, IOException {
		String recherche = valeur(request, "recherche");
		request.setAttribute("utilisateurs", utilisateurService.rechercher(recherche));
		request.setAttribute("nombreUtilisateurs", utilisateurService.compterTous());
		request.setAttribute("nombreActifs", utilisateurService.compterActifs());
		request.setAttribute("nombreAdministrateurs", utilisateurService.compterActifs(Role.ADMINISTRATEUR));
		request.setAttribute("nombreGESTIONNAIREs", utilisateurService.compterActifs(Role.GESTIONNAIRE));
		request.setAttribute("nombreCuisiniers", utilisateurService.compterActifs(Role.CUISINIER));
		request.setAttribute("recherche", recherche);
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("message", MESSAGES.get(valeur(request, "message")));
		ajouterJetonCsrf(request);
		request.getRequestDispatcher("/views/utilisateurs/liste.jsp").forward(request, response);
	}

	private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response,
			Utilisateur utilisateur, List<String> erreurs) throws ServletException, IOException {
		request.setAttribute("utilisateur", utilisateur);
		request.setAttribute("roles", Role.values());
		request.setAttribute("erreurs", erreurs);
		ajouterJetonCsrf(request);
		request.getRequestDispatcher("/views/utilisateurs/formulaire.jsp").forward(request, response);
	}

	private void afficherDroitsUtilisateur(HttpServletRequest request, HttpServletResponse response,
			Utilisateur utilisateur, List<String> erreurs) throws ServletException, IOException {
		request.setAttribute("utilisateur", utilisateur);
		request.setAttribute("droits", autorisationService.chargerDroitsUtilisateur(
				utilisateur.getId(), utilisateur.getRole()));
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("message", "1".equals(request.getParameter("enregistre"))
				? "Les autorisations du compte ont été enregistrées." : null);
		ajouterJetonCsrf(request);
		request.getRequestDispatcher("/views/utilisateurs/droits-compte.jsp").forward(request, response);
	}

	private void afficherDroitsRole(HttpServletRequest request, HttpServletResponse response,
			Role role, List<String> erreurs) throws ServletException, IOException {
		request.setAttribute("role", role);
		request.setAttribute("rolesModifiables", List.of(Role.GESTIONNAIRE, Role.CUISINIER));
		request.setAttribute("droits", autorisationService.chargerDroitsRole(role));
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("message", "1".equals(request.getParameter("enregistre"))
				? "Les autorisations par rôle ont été enregistrées." : null);
		ajouterJetonCsrf(request);
		request.getRequestDispatcher("/views/utilisateurs/droits-role.jsp").forward(request, response);
	}

	private boolean estAdministrateur(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null || !Boolean.TRUE.equals(session.getAttribute("authentifie"))) {
			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		if (!Role.ADMINISTRATEUR.name().equals(session.getAttribute("roleUtilisateur"))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
		return true;
	}

	private void ajouterJetonCsrf(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String jeton = (String) session.getAttribute(JETON_CSRF);
		if (jeton == null) {
			byte[] octets = new byte[32];
			ALEA.nextBytes(octets);
			jeton = Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
			session.setAttribute(JETON_CSRF, jeton);
		}
		request.setAttribute("jetonCsrf", jeton);
	}

	private boolean verifierJetonCsrf(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String attendu = session == null ? null : (String) session.getAttribute(JETON_CSRF);
		String recu = request.getParameter("jetonCsrf");
		if (attendu == null || recu == null
				|| !MessageDigest.isEqual(attendu.getBytes(java.nio.charset.StandardCharsets.UTF_8),
						recu.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
			return false;
		}
		session.removeAttribute(JETON_CSRF);
		return true;
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}

	private Integer lireId(String valeur) {
		try {
			int id = Integer.parseInt(valeur);
			return id > 0 ? id : null;
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	private Role lireRole(String valeur) {
		try {
			return Role.valueOf(valeur);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}
}
