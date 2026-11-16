package bj.restaurant.couts.controller;

import bj.restaurant.couts.Entities.Met;
import bj.restaurant.couts.service.MetService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/mets")
public class MetServlet extends HttpServlet {
	@Inject
	private MetService metService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("nouveau".equals(action)) {
			preparerFormulaire(request, response, new Met(), List.of());
			return;
		}
		if ("modifier".equals(action) || "details".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Met met = metService.trouverParId(id).orElse(null);
			if (met == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			if ("details".equals(action)) {
				request.setAttribute("met", met);
				request.setAttribute("historiqueRecettes", met.getRecettes());
				request.getRequestDispatcher("/views/mets/details.jsp").forward(request, response);
			} else {
				preparerFormulaire(request, response, met, List.of());
			}
			return;
		}

		String recherche = request.getParameter("recherche");
		request.setAttribute("mets", metService.rechercher(recherche));
		request.setAttribute("nombreMets", metService.compterTous());
		request.setAttribute("nombreMetsActifs", metService.compterActifs());
		request.setAttribute("recherche", recherche == null ? "" : recherche);
		request.getRequestDispatcher("/views/mets/liste.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		if ("desactiver".equals(action) || "activer".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			boolean modifie = "desactiver".equals(action)
					? metService.desactiver(id) : metService.activer(id);
			if (!modifie) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			response.sendRedirect(request.getContextPath() + "/mets");
			return;
		}
		if (!"enregistrer".equals(action)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		Met met = new Met();
		List<String> erreurs = new ArrayList<>();
		String idValeur = valeur(request, "id");
		if (!idValeur.isBlank()) {
			Integer id = lireId(idValeur);
			if (id == null) erreurs.add("Identifiant de met invalide.");
			else met.setId(id);
		}
		String nom = valeur(request, "nom");
		String description = request.getParameter("description");
		String prixValeur = valeur(request, "prixVenteActuel");
		if (nom.isBlank()) erreurs.add("Le nom du met est obligatoire.");
		if (nom.length() > 120) erreurs.add("Le nom ne peut pas dépasser 120 caractères.");
		met.setNom(nom);
		met.setDescription(description == null || description.isBlank() ? null : description.trim());

		try {
			met.setPrixVenteActuel(new BigDecimal(prixValeur.replace(',', '.')));
		} catch (NumberFormatException exception) {
			erreurs.add("Le prix de vente doit être un nombre valide.");
		}

		if (erreurs.isEmpty()) {
			try {
				metService.enregistrer(met);
				response.sendRedirect(request.getContextPath() + "/mets");
				return;
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}
		preparerFormulaire(request, response, met, erreurs);
	}

	private void preparerFormulaire(HttpServletRequest request, HttpServletResponse response,
			Met met, List<String> erreurs) throws ServletException, IOException {
		request.setAttribute("met", met);
		request.setAttribute("erreurs", erreurs);
		request.getRequestDispatcher("/views/mets/formulaire.jsp").forward(request, response);
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}

	private Integer lireId(String valeur) {
		if (valeur == null || valeur.isBlank()) return null;
		try {
			int id = Integer.parseInt(valeur);
			return id > 0 ? id : null;
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}