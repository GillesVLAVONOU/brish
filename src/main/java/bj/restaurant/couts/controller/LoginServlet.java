package bj.restaurant.couts.controller;

import bj.restaurant.couts.security.BrishPrincipal;
import bj.restaurant.couts.service.AdminBootstrapService;
import bj.restaurant.couts.service.AutorisationService;
import bj.restaurant.couts.service.AuthentificationService;
import bj.restaurant.couts.model.Role;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.login.LoginException;

/**
 *
 * @author Dell
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	@Inject
	private AdminBootstrapService adminBootstrapService;

	@Inject
	private AuthentificationService authentificationService;

	@Inject
	private AutorisationService autorisationService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!adminBootstrapService.administrateurExiste()) {
			response.sendRedirect(request.getContextPath() + "/setup-admin");
			return;
		}
		request.getRequestDispatcher("/views/login.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String email = request.getParameter("email");
		String passwordValue = request.getParameter("password");

		if (email == null || email.isBlank() || passwordValue == null || passwordValue.isBlank()) {
			request.setAttribute("erreur", "Saisissez votre adresse e-mail et votre mot de passe.");
			request.getRequestDispatcher("/views/login.jsp").forward(request, response);
			return;
		}

		char[] password = passwordValue.toCharArray();
		try {
			BrishPrincipal principal = authentificationService.authentifier(email, password).orElse(null);
			if (principal == null) {
				request.setAttribute("erreur", "Adresse e-mail ou mot de passe invalide.");
				request.getRequestDispatcher("/views/login.jsp").forward(request, response);
				return;
			}

			HttpSession ancienneSession = request.getSession(false);
			if (ancienneSession != null) ancienneSession.invalidate();
			HttpSession session = request.getSession(true);
			request.changeSessionId();
			session.setAttribute("authentifie", Boolean.TRUE);
			session.setAttribute("utilisateurId", principal.getId());
			session.setAttribute("nomUtilisateur", principal.getNom());
			session.setAttribute("emailUtilisateur", principal.getEmail());
			session.setAttribute("roleUtilisateur", principal.getRole());
			String accueil = autorisationService.cheminAccueil(
					principal.getId().intValue(), Role.valueOf(principal.getRole()));
			response.sendRedirect(request.getContextPath() + accueil);
		} catch (LoginException exception) {
			getServletContext().log("Échec de l'authentification JAAS", exception);
			request.setAttribute("erreur", exception.getMessage().contains("Identifiants invalides")
					? "Adresse e-mail ou mot de passe invalide."
					: "Connexion momentanément indisponible. Réessayez plus tard.");
			request.getRequestDispatcher("/views/login.jsp").forward(request, response);
		} finally {
			Arrays.fill(password, '\0');
		}
	}
}