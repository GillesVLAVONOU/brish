package bj.restaurant.couts.controller;

import bj.restaurant.couts.dto.ProductionVendable;
import bj.restaurant.couts.service.VenteService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ventes")
public class VenteServlet extends HttpServlet {
	private static final String JETON_CSRF = "jetonVentes";
	private static final SecureRandom ALEA = new SecureRandom();

	@Inject
	private VenteService venteService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		afficher(request, response, List.of(), Map.of(), Map.of());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!verifierJetonCsrf(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		List<String> erreurs = new ArrayList<>();
		Map<Integer, Integer> portions = new LinkedHashMap<>();
		Map<Integer, BigDecimal> prix = new LinkedHashMap<>();
		Map<Integer, String> portionsSaisies = new LinkedHashMap<>();
		Map<Integer, String> prixSaisis = new LinkedHashMap<>();
		LocalDate dateVente = lireDate(valeur(request, "dateVente"), erreurs);
		String[] ids = request.getParameterValues("productionId");
		String[] quantites = request.getParameterValues("portionsVendues");
		String[] prixUnitaires = request.getParameterValues("prixUnitaire");
		int lignes = Math.max(ids == null ? 0 : ids.length,
				Math.max(quantites == null ? 0 : quantites.length, prixUnitaires == null ? 0 : prixUnitaires.length));

		for (int index = 0; index < lignes; index++) {
			String idSaisi = valeurTableau(ids, index);
			String quantiteSaisie = valeurTableau(quantites, index);
			String prixSaisi = valeurTableau(prixUnitaires, index);
			if (idSaisi.isBlank()) {
				erreurs.add("Une production sélectionnée est invalide.");
				continue;
			}
			Integer id = lireId(idSaisi);
			if (id == null) {
				erreurs.add("Une production sélectionnée est invalide.");
				continue;
			}
			portionsSaisies.put(id, quantiteSaisie);
			prixSaisis.put(id, prixSaisi);
			if (quantiteSaisie.isBlank()) {
				continue;
			}

			Integer nombrePortions;
			try {
				nombrePortions = Integer.valueOf(quantiteSaisie);
			} catch (NumberFormatException exception) {
				erreurs.add("Saisissez un nombre entier valide pour chaque quantité vendue.");
				continue;
			}
			if (nombrePortions < 0) {
				erreurs.add("Le nombre de portions vendues ne peut pas être négatif.");
				continue;
			}
			if (nombrePortions == 0) {
				continue;
			}
			if (portions.containsKey(id)) {
				erreurs.add("Une production ne peut apparaître qu'une fois dans une même vente.");
				continue;
			}
			if (prixSaisi.isBlank()) {
				erreurs.add("Saisissez le prix unitaire des productions vendues.");
				continue;
			}
			try {
				portions.put(id, nombrePortions);
				prix.put(id, new BigDecimal(prixSaisi.replace(',', '.')));
			} catch (NumberFormatException exception) {
				erreurs.add("Saisissez un prix unitaire valide.");
			}
		}

		if (erreurs.isEmpty()) {
			try {
				Object utilisateurSession = request.getSession(false).getAttribute("utilisateurId");
				Integer utilisateurId = utilisateurSession instanceof Number
						? ((Number) utilisateurSession).intValue() : null;
				venteService.enregistrer(dateVente, utilisateurId, portions, prix);
				response.sendRedirect(request.getContextPath() + "/ventes?enregistre=1");
				return;
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}
		afficher(request, response, erreurs, portionsSaisies, prixSaisis);
	}

	private void afficher(HttpServletRequest request, HttpServletResponse response,
			List<String> erreurs, Map<Integer, String> portionsSaisies, Map<Integer, String> prixSaisis)
			throws ServletException, IOException {
		request.setAttribute("productionsVendables", venteService.productionsVendables());
		request.setAttribute("dateAujourdHui", LocalDate.now());
		request.setAttribute("dateVente", valeur(request, "dateVente").isBlank()
				? LocalDate.now() : valeur(request, "dateVente"));
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("portionsSaisies", portionsSaisies);
		request.setAttribute("prixSaisis", prixSaisis);
		request.setAttribute("enregistre", "1".equals(request.getParameter("enregistre")));
		request.setAttribute("jetonCsrf", jetonCsrf(request.getSession(true)));
		request.getRequestDispatcher("/views/ventes/index.jsp").forward(request, response);
	}

	private LocalDate lireDate(String valeur, List<String> erreurs) {
		try {
			return LocalDate.parse(valeur);
		} catch (DateTimeParseException exception) {
			erreurs.add("Saisissez une date de vente valide.");
			return null;
		}
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

	private boolean verifierJetonCsrf(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String attendu = session == null ? null : (String) session.getAttribute(JETON_CSRF);
		String recu = request.getParameter("jetonCsrf");
		return attendu != null && recu != null
				&& MessageDigest.isEqual(attendu.getBytes(java.nio.charset.StandardCharsets.UTF_8),
						recu.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}

	private Integer lireId(String valeur) {
		try {
			int id = Integer.parseInt(valeur);
			return id > 0 ? id : null;
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}

	private String valeurTableau(String[] valeurs, int index) {
		return valeurs == null || index >= valeurs.length || valeurs[index] == null
				? "" : valeurs[index].trim();
	}
}
