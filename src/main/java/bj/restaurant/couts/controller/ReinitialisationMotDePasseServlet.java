package bj.restaurant.couts.controller;

import bj.restaurant.couts.service.ReinitialisationMotDePasseService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@WebServlet(urlPatterns = {"/mot-de-passe-oublie", "/reinitialiser-mot-de-passe"})
public class ReinitialisationMotDePasseServlet extends HttpServlet {
	private static final String JETON_CSRF = "csrfReinitialisation";
	private static final SecureRandom ALEA = new SecureRandom();

	@Inject
	private ReinitialisationMotDePasseService reinitialisationService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		protegerReponse(response);
		if (request.getServletPath().equals("/reinitialiser-mot-de-passe")) {
			String jeton = request.getParameter("jeton");
			request.setAttribute("jeton", jeton == null ? "" : jeton);
			try {
				request.setAttribute("jetonValide", reinitialisationService.jetonValide(jeton));
			} catch (IllegalStateException exception) {
				getServletContext().log("Échec de la vérification d'un lien de réinitialisation.", exception);
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				request.setAttribute("erreur",
						"Le service de réinitialisation est temporairement indisponible. Réessayez plus tard.");
			}
			preparerFormulaire(request, response, "/views/reinitialiser-mot-de-passe.jsp");
			return;
		}
		preparerFormulaire(request, response, "/views/mot-de-passe-oublie.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		protegerReponse(response);
		if (!verifierCsrf(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if (request.getServletPath().equals("/mot-de-passe-oublie")) {
			try {
				reinitialisationService.demanderLien(request.getParameter("email"));
				request.setAttribute("message",
						"Si un compte actif est associé à cette adresse, suivez le lien reçu par e-mail. "
								+ "Si rien n'arrive, réessayez plus tard ou contactez l'administrateur.");
				preparerFormulaire(request, response, "/views/mot-de-passe-oublie.jsp");
			} catch (IllegalStateException exception) {
				getServletContext().log("Échec du traitement d'une demande de réinitialisation.", exception);
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				request.setAttribute("erreur",
						"Le service de réinitialisation est temporairement indisponible. Réessayez plus tard.");
				preparerFormulaire(request, response, "/views/mot-de-passe-oublie.jsp");
			}
			return;
		}

		String jeton = request.getParameter("jeton");
		String nouveauMotDePasse = request.getParameter("nouveauMotDePasse");
		String confirmation = request.getParameter("confirmation");
		if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 12) {
			request.setAttribute("erreur", "Le mot de passe doit contenir au moins 12 caractères.");
			request.setAttribute("jeton", jeton == null ? "" : jeton);
			request.setAttribute("jetonValide", verifierJetonPourAffichage(jeton, request, response));
			preparerFormulaire(request, response, "/views/reinitialiser-mot-de-passe.jsp");
			return;
		}
		if (!nouveauMotDePasse.equals(confirmation)) {
			request.setAttribute("erreur", "Les deux mots de passe ne correspondent pas.");
			request.setAttribute("jeton", jeton == null ? "" : jeton);
			request.setAttribute("jetonValide", verifierJetonPourAffichage(jeton, request, response));
			preparerFormulaire(request, response, "/views/reinitialiser-mot-de-passe.jsp");
			return;
		}

		char[] motDePasse = nouveauMotDePasse.toCharArray();
		try {
			if (reinitialisationService.reinitialiser(jeton, motDePasse)) {
				response.sendRedirect(request.getContextPath() + "/login?motDePasseReinitialise=1");
				return;
			}
			request.setAttribute("erreur",
					"Ce lien est invalide ou a expiré. Demandez un nouveau lien de réinitialisation.");
		} catch (IllegalStateException exception) {
			getServletContext().log("Échec de l'enregistrement d'un nouveau mot de passe.", exception);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			request.setAttribute("erreur",
					"Le service de réinitialisation est temporairement indisponible. Réessayez plus tard.");
		} finally {
			Arrays.fill(motDePasse, '\0');
		}
		request.setAttribute("jeton", jeton == null ? "" : jeton);
		request.setAttribute("jetonValide", verifierJetonPourAffichage(jeton, request, response));
		preparerFormulaire(request, response, "/views/reinitialiser-mot-de-passe.jsp");
	}

	private boolean verifierJetonPourAffichage(String jeton, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			return reinitialisationService.jetonValide(jeton);
		} catch (IllegalStateException exception) {
			getServletContext().log("Échec de la vérification d'un lien de réinitialisation.", exception);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			request.setAttribute("erreur",
					"Le service de réinitialisation est temporairement indisponible. Réessayez plus tard.");
			return false;
		}
	}

	private void preparerFormulaire(HttpServletRequest request, HttpServletResponse response, String vue)
			throws ServletException, IOException {
		request.setAttribute("jetonCsrf", jetonCsrf(request.getSession(true)));
		request.getRequestDispatcher(vue).forward(request, response);
	}

	private String jetonCsrf(HttpSession session) {
		String jeton = (String) session.getAttribute(JETON_CSRF);
		if (jeton == null) {
			byte[] octets = new byte[32];
			ALEA.nextBytes(octets);
			jeton = Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
			session.setAttribute(JETON_CSRF, jeton);
		}
		return jeton;
	}

	private boolean verifierCsrf(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String attendu = session == null ? null : (String) session.getAttribute(JETON_CSRF);
		String recu = request.getParameter("jetonCsrf");
		return attendu != null && recu != null
				&& MessageDigest.isEqual(attendu.getBytes(StandardCharsets.UTF_8),
						recu.getBytes(StandardCharsets.UTF_8));
	}

	private void protegerReponse(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Referrer-Policy", "no-referrer");
		response.setHeader("X-Content-Type-Options", "nosniff");
	}
}
