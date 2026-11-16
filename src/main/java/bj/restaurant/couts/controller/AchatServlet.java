package bj.restaurant.couts.controller;

import bj.restaurant.couts.dto.AchatLigneForm;
import bj.restaurant.couts.Entities.Achat;
import bj.restaurant.couts.Entities.Fournisseur;
import bj.restaurant.couts.Entities.Ingredient;
import bj.restaurant.couts.Entities.LigneAchat;
import bj.restaurant.couts.service.AchatService;
import bj.restaurant.couts.service.FournisseurService;
import bj.restaurant.couts.service.IngredientService;
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

@WebServlet("/achats")
public class AchatServlet extends HttpServlet {
	@Inject
	private AchatService achatService;
	@Inject
	private FournisseurService fournisseurService;
	@Inject
	private IngredientService ingredientService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("nouveau".equals(action)) {
			preparerFormulaire(request, response, "", LocalDate.now().toString(),
					"", List.of(new AchatLigneForm()), List.of());
			return;
		}
		if ("details".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Achat achat = achatService.trouverParId(id).orElse(null);
			if (achat == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			request.setAttribute("achat", achat);
			request.getRequestDispatcher("/views/achats/details.jsp").forward(request, response);
			return;
		}

		afficherListe(request, response);
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
		String fournisseurIdParam = valeur(request, "fournisseurId");
		String numeroFacture = valeur(request, "numeroFacture");
		String dateParam = valeur(request, "dateAchat");
		Integer fournisseurId = lireId(fournisseurIdParam);
		if (fournisseurId == null) erreurs.add("Sélectionnez un fournisseur.");
		if (numeroFacture.isBlank()) erreurs.add("Le numéro de facture est obligatoire.");
		if (numeroFacture.length() > 80) erreurs.add("Le numéro de facture ne peut pas dépasser 80 caractères.");

		LocalDate dateAchat = null;
		try {
			dateAchat = LocalDate.parse(dateParam);
		} catch (DateTimeParseException exception) {
			erreurs.add("La date de l'achat est invalide.");
		}

		List<AchatLigneForm> lignesFormulaire = lireLignesFormulaire(request);
		String[] ingredientIds = request.getParameterValues("ingredientId");
		String[] quantites = request.getParameterValues("quantite");
		String[] unites = request.getParameterValues("unite");
		String[] prixUnitaires = request.getParameterValues("prixUnitaire");
		int nombreLignes = longueurMaximale(ingredientIds, quantites, unites, prixUnitaires);
		Achat achat = new Achat();
		achat.setNumeroFacture(numeroFacture);
		achat.setDateAchat(dateAchat);
		if (fournisseurId != null) {
			Fournisseur fournisseur = new Fournisseur();
			fournisseur.setId(fournisseurId);
			achat.setFournisseur(fournisseur);
		}

		int lignesValides = 0;
		for (int index = 0; index < nombreLignes; index++) {
			String ingredientIdValeur = valeurTableau(ingredientIds, index);
			String quantiteValeur = valeurTableau(quantites, index);
			String uniteValeur = valeurTableau(unites, index);
			String prixValeur = valeurTableau(prixUnitaires, index);
			if (ingredientIdValeur.isBlank() && quantiteValeur.isBlank()
					&& uniteValeur.isBlank() && prixValeur.isBlank()) {
				continue;
			}

			int erreursAvantLigne = erreurs.size();
			Integer ingredientId = lireId(ingredientIdValeur);
			if (ingredientId == null) erreurs.add("Ligne " + (index + 1) + " : sélectionnez un ingrédient.");

			BigDecimal quantite = lireDecimal(quantiteValeur, "Ligne " + (index + 1) + " : quantité", erreurs);
			if (quantite != null) {
				if (quantite.signum() <= 0) erreurs.add("Ligne " + (index + 1) + " : la quantité doit être supérieure à zéro.");
				if (!respectePrecision(quantite, 10, 3)) erreurs.add("Ligne " + (index + 1) + " : maximum 3 décimales et 7 chiffres entiers pour la quantité.");
			}

			if (uniteValeur.isBlank()) erreurs.add("Ligne " + (index + 1) + " : l'unité est obligatoire.");
			if (uniteValeur.length() > 20) erreurs.add("Ligne " + (index + 1) + " : l'unité ne peut pas dépasser 20 caractères.");

			BigDecimal prixUnitaire = lireDecimal(prixValeur, "Ligne " + (index + 1) + " : prix unitaire", erreurs);
			if (prixUnitaire != null) {
				if (prixUnitaire.signum() < 0) erreurs.add("Ligne " + (index + 1) + " : le prix unitaire ne peut pas être négatif.");
				if (!respectePrecision(prixUnitaire, 10, 2)) erreurs.add("Ligne " + (index + 1) + " : maximum 2 décimales et 8 chiffres entiers pour le prix.");
			}

			if (erreurs.size() == erreursAvantLigne) {
				Ingredient ingredient = new Ingredient();
				ingredient.setId(ingredientId);
				LigneAchat ligne = new LigneAchat();
				ligne.setIngredient(ingredient);
				ligne.setQuantite(quantite);
				ligne.setUnite(uniteValeur);
				ligne.setPrixUnitaire(prixUnitaire);
				achat.getLignes().add(ligne);
				lignesValides++;
			}
		}
		if (lignesValides == 0) erreurs.add("Ajoutez au moins une ligne d'achat complète.");

		if (erreurs.isEmpty()) {
			try {
				Achat achatEnregistre = achatService.enregistrer(achat);
				response.sendRedirect(request.getContextPath() + "/achats?action=details&id=" + achatEnregistre.getId());
				return;
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}

		preparerFormulaire(request, response, fournisseurIdParam, dateParam,
				numeroFacture, lignesFormulaire, erreurs);
	}

	private void afficherListe(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String recherche = request.getParameter("recherche");
		request.setAttribute("achats", achatService.rechercher(recherche));
		request.setAttribute("nombreAchats", achatService.compterTous());
		request.setAttribute("montantTotalAchats", achatService.calculerMontantTotal());
		request.setAttribute("recherche", recherche == null ? "" : recherche);
		request.getRequestDispatcher("/views/achats/liste.jsp").forward(request, response);
	}

	private void preparerFormulaire(HttpServletRequest request, HttpServletResponse response,
			String fournisseurId, String dateAchat, String numeroFacture,
			List<AchatLigneForm> lignes, List<String> erreurs)
			throws ServletException, IOException {
		request.setAttribute("fournisseurs", fournisseurService.rechercher(null));
		request.setAttribute("ingredientsActifs", ingredientService.rechercherActifs());
		request.setAttribute("fournisseurId", fournisseurId);
		request.setAttribute("dateAchat", dateAchat);
		request.setAttribute("numeroFacture", numeroFacture);
		request.setAttribute("lignesFormulaire", lignes.isEmpty() ? List.of(new AchatLigneForm()) : lignes);
		request.setAttribute("erreurs", erreurs);
		request.getRequestDispatcher("/views/achats/formulaire.jsp").forward(request, response);
	}

	private List<AchatLigneForm> lireLignesFormulaire(HttpServletRequest request) {
		String[] ingredientIds = request.getParameterValues("ingredientId");
		String[] quantites = request.getParameterValues("quantite");
		String[] unites = request.getParameterValues("unite");
		String[] prixUnitaires = request.getParameterValues("prixUnitaire");
		List<AchatLigneForm> lignes = new ArrayList<>();
		for (int index = 0, nombre = longueurMaximale(ingredientIds, quantites, unites, prixUnitaires);
				index < nombre; index++) {
			AchatLigneForm ligne = new AchatLigneForm();
			ligne.setIngredientId(valeurTableau(ingredientIds, index));
			ligne.setQuantite(valeurTableau(quantites, index));
			ligne.setUnite(valeurTableau(unites, index));
			ligne.setPrixUnitaire(valeurTableau(prixUnitaires, index));
			lignes.add(ligne);
		}
		return lignes;
	}

	private int longueurMaximale(String[]... valeurs) {
		int longueur = 0;
		for (String[] tableau : valeurs) {
			if (tableau != null) longueur = Math.max(longueur, tableau.length);
		}
		return longueur;
	}

	private String valeurTableau(String[] valeurs, int index) {
		return valeurs == null || index >= valeurs.length || valeurs[index] == null
				? "" : valeurs[index].trim();
	}

	private BigDecimal lireDecimal(String valeur, String libelle, List<String> erreurs) {
		if (valeur.isBlank()) {
			erreurs.add(libelle + " est obligatoire.");
			return null;
		}
		try {
			return new BigDecimal(valeur.replace(',', '.'));
		} catch (NumberFormatException exception) {
			erreurs.add(libelle + " doit être un nombre valide.");
			return null;
		}
	}

	private boolean respectePrecision(BigDecimal valeur, int precisionMax, int scaleMax) {
		BigDecimal normalisee = valeur.stripTrailingZeros();
		return normalisee.scale() <= scaleMax && normalisee.precision() - normalisee.scale() <= precisionMax - scaleMax;
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