package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.AchatDAO;
import bj.restaurant.couts.dao.ProductionDAO;
import bj.restaurant.couts.dao.RecetteDAO;
import bj.restaurant.couts.Entities.ChargeProduction;
import bj.restaurant.couts.Entities.HistoriqueCout;
import bj.restaurant.couts.Entities.LigneProduction;
import bj.restaurant.couts.Entities.LigneRecette;
import bj.restaurant.couts.Entities.Production;
import bj.restaurant.couts.Entities.Recette;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDate;

@ApplicationScoped
public class ProductionService {
	private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);

	@Inject
	private ProductionDAO productionDAO;
	@Inject
	private RecetteDAO recetteDAO;
	@Inject
	private AchatDAO achatDAO;

	public List<Production> rechercher(String recherche) {
		return productionDAO.rechercher(recherche);
	}

	public long compterTous() {
		return productionDAO.compterTous();
	}

	public long compterBrouillons() {
		return productionDAO.compterBrouillons();
	}

	public long compterPortionsTerminees() {
		return productionDAO.compterPortionsTerminees();
	}

	public BigDecimal calculerCoutTotalTermine() {
		return productionDAO.calculerCoutTotalTermine();
	}

	public Optional<Production> trouverParId(Integer id) {
		return productionDAO.trouverParId(id);
	}

	public List<LigneProduction> chargerLignes(Integer id) {
		return productionDAO.chargerLignes(id);
	}

	public List<LigneRecette> chargerLignesRecette(Integer recetteId) {
		return recetteDAO.chargerLignes(recetteId);
	}

	public List<HistoriqueCout> chargerHistoriqueCouts(Integer id) {
		return productionDAO.chargerHistoriqueCouts(id);
	}

	@Transactional
	public Production demarrer(Integer recetteId, Integer portionsPrevues, Integer utilisateurId) {
		if (portionsPrevues == null || portionsPrevues <= 0) {
			throw new IllegalArgumentException("Le nombre de portions prévues doit être supérieur à zéro.");
		}
		Recette recette = recetteDAO.trouverRecetteActive(recetteId)
				.orElseThrow(() -> new IllegalArgumentException("La recette n'existe pas ou n'est plus active."));
		List<LigneRecette> lignesRecette = recetteDAO.chargerLignes(recetteId);
		if (lignesRecette.isEmpty()) {
			throw new IllegalArgumentException("La recette sélectionnée ne contient aucun ingrédient.");
		}
		Set<Integer> ingredientIds = new HashSet<>();
		if (lignesRecette.stream().anyMatch(ligne -> !ingredientIds.add(ligne.getIngredient().getId()))) {
			throw new IllegalArgumentException("La recette contient plusieurs lignes pour un même ingrédient; corrigez-la avant de démarrer.");
		}

		Production production = new Production();
		production.setRecette(recette);
		production.setUtilisateur(productionDAO.utilisateur(utilisateurId));
		production.setPortionsPrevues(portionsPrevues);
		production.setStatut("BROUILLON");
		productionDAO.sauvegarder(production);
		return production;
	}

	@Transactional
	public Production terminer(Integer productionId, Integer portionsReelles,
			Map<Integer, BigDecimal> consommations) {
		Production production = productionDAO.trouverParId(productionId)
				.orElseThrow(() -> new IllegalArgumentException("La production demandée n'existe pas."));
		if (!production.isBrouillon()) {
			throw new IllegalArgumentException("Seule une production en brouillon peut être clôturée.");
		}
		if (portionsReelles == null || portionsReelles <= 0) {
			throw new IllegalArgumentException("Le nombre de portions réellement produites doit être supérieur à zéro.");
		}

		List<LigneRecette> lignesRecette = recetteDAO.chargerLignes(production.getRecette().getId());
		Set<Integer> ingredientsAttendus = new HashSet<>();
		for (LigneRecette ligne : lignesRecette) {
			if (!ingredientsAttendus.add(ligne.getIngredient().getId())) {
				throw new IllegalArgumentException("La recette contient plusieurs lignes pour un même ingrédient; corrigez-la avant de clôturer.");
			}
		}
		if (consommations == null || !consommations.keySet().equals(ingredientsAttendus)) {
			throw new IllegalArgumentException("Saisissez une quantité réellement consommée pour chaque ingrédient de la recette.");
		}

		BigDecimal coutMatieres = ZERO;
		for (LigneRecette ligneRecette : lignesRecette) {
			Integer ingredientId = ligneRecette.getIngredient().getId();
			BigDecimal quantite = consommations.get(ingredientId);
			if (quantite == null || quantite.signum() < 0 || !respectePrecision(quantite, 10, 3)) {
				throw new IllegalArgumentException("Chaque quantité consommée doit être positive ou nulle et comporter au plus 3 décimales.");
			}

			BigDecimal coutUnitaire = quantite.signum() == 0 ? ZERO
					: calculerCoutUnitaire(ligneRecette, production);
			BigDecimal coutTotal = quantite.multiply(coutUnitaire).setScale(2, RoundingMode.HALF_UP);
			if (!respectePrecision(coutTotal, 10, 2)) {
				throw new IllegalArgumentException("Le coût total calculé dépasse la capacité de stockage.");
			}
			LigneProduction ligneProduction = new LigneProduction();
			ligneProduction.setProduction(production);
			ligneProduction.setIngredient(ligneRecette.getIngredient());
			ligneProduction.setQuantiteReelle(quantite);
			ligneProduction.setUnite(ligneRecette.getIngredient().getUniteBase());
			ligneProduction.setCoutUnitaireApplique(coutUnitaire);
			ligneProduction.setCoutTotal(coutTotal);
			production.getLignes().add(ligneProduction);
			coutMatieres = coutMatieres.add(coutTotal);
		}

		BigDecimal coutCharges = production.getCharges().stream()
				.map(ChargeProduction::getMontant)
				.reduce(ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.HALF_UP);
		BigDecimal coutTotal = coutMatieres.add(coutCharges).setScale(2, RoundingMode.HALF_UP);
		if (!respectePrecision(coutMatieres, 10, 2) || !respectePrecision(coutCharges, 10, 2)
				|| !respectePrecision(coutTotal, 10, 2)) {
			throw new IllegalArgumentException("Le coût calculé dépasse la capacité de stockage.");
		}
		HistoriqueCout historique = new HistoriqueCout();
		historique.setProduction(production);
		historique.setCoutMatiere(coutMatieres);
		historique.setCoutCharges(coutCharges);
		historique.setCoutTotal(coutTotal);
		historique.setCoutUnitaire(coutTotal.divide(BigDecimal.valueOf(portionsReelles), 2, RoundingMode.HALF_UP));
		production.setPortionsReelles(portionsReelles);
		production.setStatut("TERMINEE");
		production.setDateCloture(LocalDate.now());
		productionDAO.sauvegarder(historique);
		return production;
	}

	private BigDecimal calculerCoutUnitaire(LigneRecette ligneRecette, Production production) {
		List<bj.restaurant.couts.Entities.LigneAchat> achats = achatDAO.achatsCompatibles(
				ligneRecette.getIngredient().getId(),
				ligneRecette.getIngredient().getUniteBase(),
				production.getDateProduction().toLocalDate());
		BigDecimal quantiteAchetee = BigDecimal.ZERO;
		BigDecimal montantAchete = BigDecimal.ZERO;
		for (bj.restaurant.couts.Entities.LigneAchat achat : achats) {
			quantiteAchetee = quantiteAchetee.add(achat.getQuantite());
			montantAchete = montantAchete.add(achat.getMontantTotal());
		}
		if (quantiteAchetee.signum() <= 0) {
			throw new IllegalArgumentException("Aucun achat antérieur de "
					+ ligneRecette.getIngredient().getNom() + " dans son unité de base ("
					+ ligneRecette.getIngredient().getUniteBase() + "). La clôture est impossible.");
		}
		return montantAchete.divide(quantiteAchetee, 2, RoundingMode.HALF_UP);
	}

	private boolean respectePrecision(BigDecimal valeur, int precisionMax, int scaleMax) {
		BigDecimal normalisee = valeur.stripTrailingZeros();
		return normalisee.scale() <= scaleMax
				&& normalisee.precision() - normalisee.scale() <= precisionMax - scaleMax;
	}
}