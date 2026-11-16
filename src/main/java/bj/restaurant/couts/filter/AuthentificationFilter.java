package bj.restaurant.couts.filter;

import bj.restaurant.couts.Entities.ParametresRestaurant;
import bj.restaurant.couts.model.ModuleApplication;
import bj.restaurant.couts.model.Role;
import bj.restaurant.couts.Entities.Utilisateur;
import bj.restaurant.couts.service.AutorisationService;
import bj.restaurant.couts.service.ParametreService;
import bj.restaurant.couts.service.UtilisateurService;
import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@WebFilter(urlPatterns = "/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class AuthentificationFilter implements Filter {
	private static final String JETON_DECONNEXION = "jetonDeconnexion";
	private static final SecureRandom ALEA = new SecureRandom();

	@Inject
	private ParametreService parametreService;
	@Inject
	private UtilisateurService utilisateurService;
	@Inject
	private AutorisationService autorisationService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

		if (path.equals("/login") || path.equals("/setup-admin")
				|| path.equals("/mot-de-passe-oublie") || path.equals("/reinitialiser-mot-de-passe")
				|| path.equals("/views/login.jsp") || path.equals("/views/setup-admin.jsp")
				|| path.equals("/views/mot-de-passe-oublie.jsp")
				|| path.equals("/views/reinitialiser-mot-de-passe.jsp")
				|| path.startsWith("/assets/")) {
			chain.doFilter(request, response);
			return;
		}

		HttpSession session = httpRequest.getSession(false);
		if (session != null && Boolean.TRUE.equals(session.getAttribute("authentifie"))) {
			if (httpRequest.getAttribute("utilisateurVerifie") == null) {
				Object utilisateurId = session.getAttribute("utilisateurId");
				Utilisateur utilisateur = utilisateurId instanceof Number
						? utilisateurService.trouverActifParId(((Number) utilisateurId).intValue()).orElse(null)
						: null;
				if (utilisateur == null) {
					session.invalidate();
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
					return;
				}
				session.setAttribute("utilisateurId", utilisateur.getId());
				session.setAttribute("nomUtilisateur", utilisateur.getNom());
				session.setAttribute("emailUtilisateur", utilisateur.getEmail());
				session.setAttribute("roleUtilisateur", utilisateur.getRole().name());
				httpRequest.setAttribute("utilisateurVerifie", Boolean.TRUE);

				ParametresRestaurant parametres = parametreService.charger();
				httpRequest.setAttribute("nomRestaurantHtml", echapperHtml(parametres.getNomRestaurant()));
				String adresse = parametres.getAdresse();
				httpRequest.setAttribute("localisationRestaurantHtml",
						adresse == null || adresse.isBlank()
								? "Restaurant" : "Restaurant · " + echapperHtml(adresse));
				httpRequest.setAttribute("devise", parametres.getLibelleDevise());
			}
			String jetonDeconnexion = (String) session.getAttribute(JETON_DECONNEXION);
			if (jetonDeconnexion == null) {
				byte[] octets = new byte[32];
				ALEA.nextBytes(octets);
				jetonDeconnexion = Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
				session.setAttribute(JETON_DECONNEXION, jetonDeconnexion);
			}
			httpRequest.setAttribute("jetonDeconnexion", jetonDeconnexion);
			httpRequest.setAttribute("roleUtilisateurLibelle",
					Role.valueOf((String) session.getAttribute("roleUtilisateur")).getLibelle());
			httpRequest.setAttribute("initialesUtilisateur",
					initiales((String) session.getAttribute("nomUtilisateur")));
			Role role = Role.valueOf((String) session.getAttribute("roleUtilisateur"));
			if (estRouteAdministration(path) && role != Role.ADMINISTRATEUR) {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			Optional<ModuleApplication> module = ModuleApplication.depuisChemin(path);
			if (module.isPresent()
					&& !autorisationService.estAutorise(
							((Number) session.getAttribute("utilisateurId")).intValue(),
							role, module.get(), httpRequest.getMethod())) {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			if (httpRequest.getAttribute("modulesAutorises") == null) {
				Set<String> modulesAutorises = autorisationService.chargerModulesConsultables(
						((Number) session.getAttribute("utilisateurId")).intValue(), role);
				httpRequest.setAttribute("modulesAutorises", modulesAutorises);
				httpRequest.setAttribute("cheminAccueilAutorise",
						autorisationService.cheminAccueilAutorise(modulesAutorises));
			}
			if (path.startsWith("/views/") && module.isEmpty() && !estRouteAdministration(path)) {
				httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			chain.doFilter(request, response);
			return;
		}

		httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
	}

	private String echapperHtml(String valeur) {
		return valeur == null ? "" : valeur.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	private String initiales(String nom) {
		if (nom == null || nom.isBlank()) {
			return "?";
		}
		String[] parties = nom.trim().split("\\s+");
		StringBuilder initiales = new StringBuilder();
		initiales.append(parties[0].substring(0, 1).toUpperCase(Locale.ROOT));
		if (parties.length > 1) {
			initiales.append(parties[parties.length - 1].substring(0, 1).toUpperCase(Locale.ROOT));
		}
		return initiales.toString();
	}

	private boolean estRouteAdministration(String path) {
		return path.equals("/utilisateurs") || path.startsWith("/views/utilisateurs/");
	}
}