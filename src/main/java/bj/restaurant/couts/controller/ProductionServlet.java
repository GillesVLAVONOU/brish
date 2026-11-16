package bj.restaurant.couts.controller;

import bj.restaurant.couts.dto.ProductionConsommationForm;
import bj.restaurant.couts.Entities.HistoriqueCout;
import bj.restaurant.couts.Entities.LigneProduction;
import bj.restaurant.couts.Entities.LigneRecette;
import bj.restaurant.couts.Entities.Production;
import bj.restaurant.couts.Entities.Recette;
import bj.restaurant.couts.service.ProductionService;
import bj.restaurant.couts.service.RecetteService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/productions")
public class ProductionServlet extends HttpServlet {
	@Inject
	private ProductionService productionService;
	@Inject
	private RecetteService recetteService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("nouveau".equals(action)) {
			preparerFormulaire(request, response, "", "1", List.of());
			return;
		}
		if ("details".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Production production = productionService.trouverParId(id).orElse(null);
			if (production == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			afficherDetails(request, response, production, null, List.of());
			return;
		}

		afficherListe(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = valeur(request, "action");
		if ("demarrer".equals(action)) {
			Integer recetteId = lireId(valeur(request, "recetteId"));
			Integer portionsPrevues = lireEntier(valeur(request, "portionsPrevues"));
			List<String> erreurs = new ArrayList<>();
			if (recetteId == null) erreurs.add("Sélectionnez une fiche recette active.");
			if (portionsPrevues == null || portionsPrevues <= 0) erreurs.add("Les portions prévues doivent être supérieures à zéro.");
			if (erreurs.isEmpty()) {
				try {
					var session = request.getSession(false);
					Object utilisateurIdSession = session == null ? null : session.getAttribute("utilisateurId");
					Integer utilisateurId = utilisateurIdSession instanceof Number
							? ((Number) utilisateurIdSession).intValue() : null;
					Production production = productionService.demarrer(recetteId, portionsPrevues, utilisateurId);
					response.sendRedirect(request.getContextPath() + "/productions?action=details&id=" + production.getId());
					return;
				} catch (IllegalArgumentException exception) {
					erreurs.add(exception.getMessage());
				}
			}
			preparerFormulaire(request, response, valeur(request, "recetteId"),
					valeur(request, "portionsPrevues"), erreurs);
			return;
		}

		if ("terminer".equals(action)) {
			Integer id = lireId(valeur(request, "id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Production production = productionService.trouverParId(id).orElse(null);
			if (production == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			Integer portionsReelles = lireEntier(valeur(request, "portionsReelles"));
			List<String> erreurs = new ArrayList<>();
			if (portionsReelles == null || portionsReelles <= 0) {
				erreurs.add("Les portions réellement produites doivent être supérieures à zéro.");
			}

			List<LigneRecette> lignesRecette = productionService.chargerLignesRecette(production.getRecette().getId());
			List<ProductionConsommationForm> lignesFormulaire = lireLignesFormulaire(request, lignesRecette);
			Map<Integer, BigDecimal> consommations = new LinkedHashMap<>();
			String[] ingredientIds = request.getParameterValues("ingredientId");
			String[] quantites = request.getParameterValues("quantiteReelle");
			int nombreLignes = Math.max(ingredientIds == null ? 0 : ingredientIds.length,
					quantites == null ? 0 : quantites.length);
			for (int index = 0; index < nombreLignes; index++) {
				Integer ingredientId = lireId(valeurTableau(ingredientIds, index));
				String quantiteValeur = valeurTableau(quantites, index);
				if (ingredientId == null) {
					erreurs.add("Ligne " + (index + 1) + " : ingrédient invalide.");
					continue;
				}
				if (quantiteValeur.isBlank()) {
					erreurs.add("Ligne " + (index + 1) + " : saisissez la quantité réellement consommée (0 si non consommée).");
					continue;
				}
				try {
					BigDecimal quantite = new BigDecimal(quantiteValeur.replace(',', '.'));
					if (quantite.signum() < 0 || !respectePrecision(quantite, 10, 3)) {
						erreurs.add("Ligne " + (index + 1) + " : la quantité doit être positive ou nulle et comporter au plus 3 décimales.");
						continue;
					}
					if (consommations.putIfAbsent(ingredientId, quantite) != null) {
						erreurs.add("Un ingrédient ne peut apparaître qu'une fois dans les consommations.");
					}
				} catch (NumberFormatException exception) {
					erreurs.add("Ligne " + (index + 1) + " : quantité invalide.");
				}
			}

			if (erreurs.isEmpty()) {
				try {
					productionService.terminer(id, portionsReelles, consommations);
					response.sendRedirect(request.getContextPath() + "/productions?action=details&id=" + id);
					return;
				} catch (IllegalArgumentException exception) {
					erreurs.add(exception.getMessage());
				}
			}
			afficherDetails(request, response, production, lignesFormulaire, erreurs);
			return;
		}

		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	private void afficherListe(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String recherche = request.getParameter("recherche");
		request.setAttribute("productions", productionService.rechercher(recherche));
		request.setAttribute("nombreProductions", productionService.compterTous());
		request.setAttribute("nombreBrouillons", productionService.compterBrouillons());
		request.setAttribute("portionsTerminees", productionService.compterPortionsTerminees());
		request.setAttribute("coutTotalTermine", productionService.calculerCoutTotalTermine());
		request.setAttribute("recherche", recherche == null ? "" : recherche);
		request.getRequestDispatcher("/views/productions/liste.jsp").forward(request, response);
	}

	private void preparerFormulaire(HttpServletRequest request, HttpServletResponse response,
			String recetteId, String portionsPrevues, List<String> erreurs)
			throws ServletException, IOException {
		request.setAttribute("recettes", recetteService.rechercherActives(null));
		request.setAttribute("recetteId", recetteId);
		request.setAttribute("portionsPrevues", portionsPrevues);
		request.setAttribute("erreurs", erreurs);
		request.getRequestDispatcher("/views/productions/formulaire.jsp").forward(request, response);
	}

	private void afficherDetails(HttpServletRequest request, HttpServletResponse response, Production production,
			List<ProductionConsommationForm> saisies, List<String> erreurs)
			throws ServletException, IOException {
		List<LigneProduction> lignesProduction = productionService.chargerLignes(production.getId());
		List<LigneRecette> lignesRecette = productionService.chargerLignesRecette(production.getRecette().getId());
		List<HistoriqueCout> historiqueCouts = productionService.chargerHistoriqueCouts(production.getId());
		Map<Integer, String> quantitesSaisies = new LinkedHashMap<>();
		if (saisies != null) {
			saisies.stream()
					.filter(ligne -> ligne.getIngredientId() != null)
					.forEach(ligne -> quantitesSaisies.putIfAbsent(ligne.getIngredientId(), ligne.getQuantiteReelle()));
		}
		List<ProductionConsommationForm> lignesFormulaire = lignesRecette.stream().map(ligne -> {
					ProductionConsommationForm form = new ProductionConsommationForm();
					form.setIngredientId(ligne.getIngredient().getId());
					form.setIngredientNom(ligne.getIngredient().getNom());
					form.setUnite(ligne.getIngredient().getUniteBase());
					form.setQuantiteRecette(ligne.getQuantite()
							.multiply(BigDecimal.valueOf(production.getPortionsPrevues()))
							.divide(BigDecimal.valueOf(production.getRecette().getRendementPortions()),
									3, RoundingMode.HALF_UP)
							.toPlainString());
					LigneProduction existante = lignesProduction.stream()
							.filter(item -> item.getIngredient().getId().equals(form.getIngredientId()))
							.findFirst().orElse(null);
					if (quantitesSaisies.containsKey(form.getIngredientId())) {
						form.setQuantiteReelle(quantitesSaisies.get(form.getIngredientId()));
					} else if (existante != null) {
						form.setQuantiteReelle(existante.getQuantiteReelle().toPlainString());
					}
					return form;
				}).toList();
		request.setAttribute("production", production);
		request.setAttribute("lignesFormulaire", lignesFormulaire);
		request.setAttribute("lignesProduction", lignesProduction);
		request.setAttribute("historiqueCouts", historiqueCouts);
		request.setAttribute("dernierCout", historiqueCouts.isEmpty() ? null : historiqueCouts.get(0));
		request.setAttribute("dateAffichee",
				production.getDateProduction().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
		request.setAttribute("erreurs", erreurs);
		request.getRequestDispatcher("/views/productions/details.jsp").forward(request, response);
	}

	private List<ProductionConsommationForm> lireLignesFormulaire(HttpServletRequest request,
			List<LigneRecette> lignesRecette) {
		String[] ingredientIds = request.getParameterValues("ingredientId");
		String[] quantites = request.getParameterValues("quantiteReelle");
		List<ProductionConsommationForm> lignes = new ArrayList<>();
		int nombre = Math.max(ingredientIds == null ? 0 : ingredientIds.length,
				quantites == null ? 0 : quantites.length);
		for (int index = 0; index < nombre; index++) {
			ProductionConsommationForm ligne = new ProductionConsommationForm();
			Integer id = lireId(valeurTableau(ingredientIds, index));
			ligne.setIngredientId(id);
			ligne.setQuantiteReelle(valeurTableau(quantites, index));
			if (id != null) {
							lignesRecette.stream()
									.filter(item -> item.getIngredient().getId().equals(id))
									.findFirst()
									.ifPresent(item -> {
										ligne.setIngredientNom(item.getIngredient().getNom());
										ligne.setUnite(item.getIngredient().getUniteBase());
									});
			}
			lignes.add(ligne);
		}
		return lignes;
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