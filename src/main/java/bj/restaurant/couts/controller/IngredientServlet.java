package bj.restaurant.couts.controller;

import bj.restaurant.couts.Entities.Ingredient;
import bj.restaurant.couts.service.IngredientService;
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

@WebServlet("/ingredients")
public class IngredientServlet extends HttpServlet {
	@Inject
	private IngredientService ingredientService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("nouveau".equals(action)) {
			preparerFormulaire(request, response, new Ingredient());
			return;
		}
		if ("modifier".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Ingredient ingredient = ingredientService.trouverParId(id).orElse(null);
			if (ingredient == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			preparerFormulaire(request, response, ingredient);
			return;
		}

		afficherListe(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		if ("desactiver".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			if (!ingredientService.desactiver(id)) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			response.sendRedirect(request.getContextPath() + "/ingredients");
			return;
		}
		if (!"enregistrer".equals(action)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		Ingredient ingredient = new Ingredient();
		List<String> erreurs = new ArrayList<>();
		String idParametre = request.getParameter("id");
		if (idParametre != null && !idParametre.isBlank()) {
			Integer id = lireId(idParametre);
			if (id == null) {
				erreurs.add("Identifiant d'ingrédient invalide.");
			} else {
				ingredient.setId(id);
			}
		}

		String nom = valeur(request, "nom");
		String uniteBase = valeur(request, "uniteBase");
		if (nom.isBlank()) erreurs.add("Le nom est obligatoire.");
		if (nom.length() > 120) erreurs.add("Le nom ne peut pas dépasser 120 caractères.");
		if (uniteBase.isBlank()) erreurs.add("L'unité de base est obligatoire.");
		if (uniteBase.length() > 20) erreurs.add("L'unité ne peut pas dépasser 20 caractères.");
		ingredient.setNom(nom);
		ingredient.setUniteBase(uniteBase);
		ingredient.setSeuilAlerte(lireNombre(request, "seuilAlerte", "Le seuil d'alerte", erreurs));

		if (erreurs.isEmpty()) {
			try {
				ingredientService.enregistrer(ingredient);
				response.sendRedirect(request.getContextPath() + "/ingredients");
				return;
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}

		request.setAttribute("erreurs", erreurs);
		preparerFormulaire(request, response, ingredient);
	}

	private void afficherListe(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String recherche = request.getParameter("recherche");
		List<Ingredient> ingredients = ingredientService.rechercher(recherche);

		request.setAttribute("ingredients", ingredients);
		request.setAttribute("recherche", recherche == null ? "" : recherche);
		request.setAttribute("nombreIngredients", ingredientService.compterTous());
		request.setAttribute("nombreActifs", ingredientService.compterActifs());
		request.setAttribute("nombreUnitesBase", ingredientService.compterUnitesBase());
		request.getRequestDispatcher("/views/ingredients/liste.jsp").forward(request, response);
	}

	private void preparerFormulaire(HttpServletRequest request, HttpServletResponse response, Ingredient ingredient)
			throws ServletException, IOException {
		request.setAttribute("formMode", true);
		request.setAttribute("ingredient", ingredient);
		request.getRequestDispatcher("/views/ingredients/liste.jsp").forward(request, response);
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}

	private BigDecimal lireNombre(HttpServletRequest request, String nom, String libelle, List<String> erreurs) {
		String valeur = valeur(request, nom);
		if (valeur.isBlank()) {
			return BigDecimal.ZERO;
		}
		try {
			BigDecimal nombre = new BigDecimal(valeur.replace(',', '.'));
			if (nombre.signum() < 0) {
				erreurs.add(libelle + " ne peut pas être négatif.");
			}
			return nombre;
		} catch (NumberFormatException exception) {
			erreurs.add(libelle + " doit être un nombre valide.");
			return BigDecimal.ZERO;
		}
	}

	private Integer lireId(String valeur) {
		if (valeur == null || valeur.isBlank()) return null;
		try {
			return Integer.valueOf(valeur);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}