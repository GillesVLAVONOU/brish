package bj.restaurant.couts.controller;

import bj.restaurant.couts.dto.RecetteLigneForm;
import bj.restaurant.couts.Entities.Ingredient;
import bj.restaurant.couts.Entities.LigneRecette;
import bj.restaurant.couts.Entities.Met;
import bj.restaurant.couts.Entities.Recette;
import bj.restaurant.couts.service.IngredientService;
import bj.restaurant.couts.service.MetService;
import bj.restaurant.couts.service.RecetteService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/recettes")
public class RecetteServlet extends HttpServlet {
	@Inject
	private RecetteService recetteService;
	@Inject
	private MetService metService;
	@Inject
	private IngredientService ingredientService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("nouveau".equals(action)) {
			Integer metId = lireId(request.getParameter("metId"));
			if (metId == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Met met = metService.trouverParId(metId).orElse(null);
			if (met == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			if (!met.isActif()) {
				response.sendError(HttpServletResponse.SC_CONFLICT, "Réactivez le met pour créer sa recette.");
				return;
			}
			preparerFormulaire(request, response, met, null, 1, LocalDate.now().toString(),
					List.of(new RecetteLigneForm()), List.of());
			return;
		}
		if ("modifier".equals(action) || "details".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Recette recette = recetteService.trouverParId(id).orElse(null);
			if (recette == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			if ("details".equals(action)) {
				request.setAttribute("recette", recette);
				request.setAttribute("historiqueRecettes",
						recetteService.historiqueParMet(recette.getMet().getId()));
				request.getRequestDispatcher("/views/recettes/details.jsp").forward(request, response);
			} else {
				if (!recette.getMet().isActif()) {
					response.sendError(HttpServletResponse.SC_CONFLICT, "Réactivez le met pour modifier sa recette.");
					return;
				}
				List<RecetteLigneForm> lignes = recette.getLignes().stream().map(ligne -> {
					RecetteLigneForm form = new RecetteLigneForm();
					form.setIngredientId(ligne.getIngredient().getId().toString());
					form.setQuantite(ligne.getQuantite().toPlainString());
					form.setUnite(ligne.getUnite());
					return form;
				}).toList();
				preparerFormulaire(request, response, recette.getMet(), recette.getId(),
						recette.getRendementPortions(), recette.getDateEffet().toString(), lignes, List.of());
			}
			return;
		}

		String recherche = request.getParameter("recherche");
		request.setAttribute("recettes", recetteService.rechercherActives(recherche));
		request.setAttribute("nombreRecettes", recetteService.compterActives());
		request.setAttribute("recherche", recherche == null ? "" : recherche);
		request.getRequestDispatcher("/views/recettes/liste.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!"enregistrer".equals(request.getParameter("action"))) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		List<String> erreurs = new ArrayList<>();
		Integer metId = lireId(valeur(request, "metId"));
		Integer sourceId = lireId(valeur(request, "recetteSourceId"));
		if (!valeur(request, "recetteSourceId").isBlank() && sourceId == null) {
			erreurs.add("Identifiant de recette invalide.");
		}
		if (metId == null) erreurs.add("Le met associé à la recette est invalide.");
		Integer rendement = lireEntier(valeur(request, "rendementPortions"));
		if (rendement == null || rendement <= 0) erreurs.add("Le rendement doit être un nombre entier supérieur à zéro.");
		LocalDate dateEffet = null;
		try {
			dateEffet = LocalDate.parse(valeur(request, "dateEffet"));
		} catch (DateTimeParseException exception) {
			erreurs.add("La date d'effet est invalide.");
		}

		String[] ingredientIds = request.getParameterValues("ingredientId");
		String[] quantites = request.getParameterValues("quantite");
		List<RecetteLigneForm> lignesFormulaire = lireLignesFormulaire(ingredientIds, quantites);
		List<LigneRecette> lignes = new ArrayList<>();
		int nombreLignes = Math.max(ingredientIds == null ? 0 : ingredientIds.length,
				quantites == null ? 0 : quantites.length);
		for (int index = 0; index < nombreLignes; index++) {
			String ingredientIdValeur = valeurTableau(ingredientIds, index);
			String quantiteValeur = valeurTableau(quantites, index);
			if (ingredientIdValeur.isBlank() && quantiteValeur.isBlank()) continue;

			int erreursAvantLigne = erreurs.size();
			Integer ingredientId = lireId(ingredientIdValeur);
			if (ingredientId == null) erreurs.add("Ligne " + (index + 1) + " : sélectionnez un ingrédient.");
			BigDecimal quantite = lireDecimal(quantiteValeur, index, erreurs);
			if (quantite != null) {
				if (quantite.signum() <= 0) erreurs.add("Ligne " + (index + 1) + " : la quantité doit être supérieure à zéro.");
				if (!respectePrecision(quantite, 10, 3)) erreurs.add("Ligne " + (index + 1) + " : maximum 3 décimales et 7 chiffres entiers.");
			}
			if (ingredientId != null) {
				Ingredient ingredient = new Ingredient();
				ingredient.setId(ingredientId);
				LigneRecette ligne = new LigneRecette();
				ligne.setIngredient(ingredient);
				ligne.setQuantite(quantite);
				ligne.setUnite(lignesFormulaire.get(index).getUnite());
				if (erreurs.size() == erreursAvantLigne) lignes.add(ligne);
			}
		}
		if (lignes.isEmpty()) erreurs.add("Ajoutez au moins un ingrédient à la recette.");

		if (erreurs.isEmpty()) {
			try {
				Recette recette = recetteService.enregistrerVersion(metId, sourceId, rendement, dateEffet, lignes);
				response.sendRedirect(request.getContextPath() + "/recettes?action=details&id=" + recette.getId());
				return;
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}

		Met met = metId == null ? null : metService.trouverParId(metId).orElse(null);
		if (met == null && sourceId != null) {
			met = recetteService.trouverParId(sourceId).map(Recette::getMet).orElse(null);
		}
		if (met == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		preparerFormulaire(request, response, met, sourceId,
				rendement == null ? 1 : rendement, valeur(request, "dateEffet"), lignesFormulaire, erreurs);
	}

	private void preparerFormulaire(HttpServletRequest request, HttpServletResponse response, Met met,
			Integer sourceId, Integer rendement, String dateEffet, List<RecetteLigneForm> lignes,
			List<String> erreurs) throws ServletException, IOException {
		request.setAttribute("met", met);
		request.setAttribute("recetteSourceId", sourceId);
		request.setAttribute("rendementPortions", rendement);
		request.setAttribute("dateEffet", dateEffet);
		request.setAttribute("dateEffetMax", LocalDate.now().toString());
		request.setAttribute("ingredientsActifs", ingredientService.rechercherActifs());
		request.setAttribute("lignesFormulaire", lignes.isEmpty() ? List.of(new RecetteLigneForm()) : lignes);
		request.setAttribute("erreurs", erreurs);
		request.getRequestDispatcher("/views/recettes/formulaire.jsp").forward(request, response);
	}

	private List<RecetteLigneForm> lireLignesFormulaire(String[] ingredientIds, String[] quantites) {
		List<RecetteLigneForm> lignes = new ArrayList<>();
		int nombre = Math.max(ingredientIds == null ? 0 : ingredientIds.length,
				quantites == null ? 0 : quantites.length);
		for (int index = 0; index < nombre; index++) {
			RecetteLigneForm ligne = new RecetteLigneForm();
			ligne.setIngredientId(valeurTableau(ingredientIds, index));
			ligne.setQuantite(valeurTableau(quantites, index));
			Integer id = lireId(ligne.getIngredientId());
			if (id != null) {
				ingredientService.trouverParId(id).ifPresent(ingredient -> ligne.setUnite(ingredient.getUniteBase()));
			}
			lignes.add(ligne);
		}
		return lignes;
	}

	private BigDecimal lireDecimal(String valeur, int index, List<String> erreurs) {
		if (valeur.isBlank()) {
			erreurs.add("Ligne " + (index + 1) + " : la quantité est obligatoire.");
			return null;
		}
		try {
			return new BigDecimal(valeur.replace(',', '.'));
		} catch (NumberFormatException exception) {
			erreurs.add("Ligne " + (index + 1) + " : la quantité doit être un nombre valide.");
			return null;
		}
	}

	private boolean respectePrecision(BigDecimal valeur, int precisionMax, int scaleMax) {
		BigDecimal normalisee = valeur.stripTrailingZeros();
		return normalisee.scale() <= scaleMax
				&& normalisee.precision() - normalisee.scale() <= precisionMax - scaleMax;
	}

	private Integer lireEntier(String valeur) {
		try {
			return Integer.valueOf(valeur);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}

	private String valeurTableau(String[] valeurs, int index) {
		return valeurs == null || index >= valeurs.length || valeurs[index] == null ? "" : valeurs[index].trim();
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