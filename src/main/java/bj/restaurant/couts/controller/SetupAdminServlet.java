package bj.restaurant.couts.controller;

import bj.restaurant.couts.service.AdminBootstrapService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

@WebServlet("/setup-admin")
public class SetupAdminServlet extends HttpServlet {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
	private static final SecureRandom RANDOM = new SecureRandom();

	@Inject
	private AdminBootstrapService adminBootstrapService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isLocalRequest(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (adminBootstrapService.administrateurExiste()) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		byte[] token = new byte[32];
		RANDOM.nextBytes(token);
		request.getSession(true).setAttribute("adminSetupToken", Base64.getUrlEncoder().withoutPadding().encodeToString(token));
		request.getRequestDispatcher("/views/setup-admin.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!isLocalRequest(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		HttpSession session = request.getSession(false);
		String expectedToken = session == null ? null : (String) session.getAttribute("adminSetupToken");
		String submittedToken = request.getParameter("token");
		if (expectedToken == null || submittedToken == null || !expectedToken.equals(submittedToken)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		session.removeAttribute("adminSetupToken");
		if (adminBootstrapService.administrateurExiste()) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		String nom = value(request, "nom");
		String email = value(request, "email").toLowerCase();
		String passwordValue = request.getParameter("password");
		String confirmationValue = request.getParameter("confirmation");
		char[] password = passwordValue == null ? new char[0] : passwordValue.toCharArray();
		char[] confirmation = confirmationValue == null ? new char[0] : confirmationValue.toCharArray();
		try {
			if (nom.isBlank() || nom.length() > 100) {
				request.setAttribute("erreur", "Le nom est obligatoire et limité à 100 caractères.");
			} else if (!EMAIL_PATTERN.matcher(email).matches() || email.length() > 150) {
				request.setAttribute("erreur", "Saisissez une adresse e-mail valide.");
			} else if (password.length < 12) {
				request.setAttribute("erreur", "Le mot de passe doit contenir au moins 12 caractères.");
			} else if (!Arrays.equals(password, confirmation)) {
				request.setAttribute("erreur", "Les deux mots de passe ne correspondent pas.");
			} else if (!adminBootstrapService.creerPremierAdministrateur(nom, email, password)) {
				response.sendRedirect(request.getContextPath() + "/login");
				return;
			} else {
				response.sendRedirect(request.getContextPath() + "/login?adminInitialise=1");
				return;
			}
		} catch (GeneralSecurityException exception) {
			throw new ServletException("Impossible de préparer le mot de passe administrateur.", exception);
		} finally {
			Arrays.fill(password, '\0');
			Arrays.fill(confirmation, '\0');
		}

		byte[] token = new byte[32];
		RANDOM.nextBytes(token);
		session.setAttribute("adminSetupToken", Base64.getUrlEncoder().withoutPadding().encodeToString(token));
		request.setAttribute("nom", nom);
		request.setAttribute("email", email);
		request.getRequestDispatcher("/views/setup-admin.jsp").forward(request, response);
	}

	private boolean isLocalRequest(HttpServletRequest request) {
		try {
			return InetAddress.getByName(request.getRemoteAddr()).isLoopbackAddress();
		} catch (IOException exception) {
			return false;
		}
	}

	private String value(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		return value == null ? "" : value.trim();
	}
}