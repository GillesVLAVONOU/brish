package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.IngredientDAO;
import bj.restaurant.couts.dao.MetDAO;
import bj.restaurant.couts.dao.RecetteDAO;
import bj.restaurant.couts.Entities.Ingredient;
import bj.restaurant.couts.Entities.LigneRecette;
import bj.restaurant.couts.Entities.Met;
import bj.restaurant.couts.Entities.Recette;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class RecetteService {
	@Inject
	private RecetteDAO recetteDAO;
	@Inject
	private MetDAO metDAO;
	@Inject
	private IngredientDAO ingredientDAO;

	public List<Recette> rechercherActives(String recherche) {
		return recetteDAO.rechercherActives(recherche);
	}

	public long compterActives() {
		return recetteDAO.compterActives();
	}

	public Optional<Recette> trouverParId(Integer id) {
		return recetteDAO.trouverParId(id);
	}

	public List<Recette> historiqueParMet(Integer metId) {
		return recetteDAO.historiqueParMet(metId);
	}

	@Transactional
	public Recette enregistrerVersion(Integer metId, Integer recetteSourceId, Integer rendementPortions,
			LocalDate dateEffet, List<LigneRecette> lignes) {
		if (recetteSourceId != null) {
			Recette source = recetteDAO.trouverParId(recetteSourceId)
					.orElseThrow(() -> new IllegalArgumentException("La recette à modifier n'existe pas."));
			if (metId != null && !source.getMet().getId().equals(metId)) {
				throw new IllegalArgumentException("Le met ne peut pas être modifié lors de la création d'une version.");
			}
			metId = source.getMet().getId();
		}

		Met met = metDAO.trouverParId(metId).orElseThrow(
				() -> new IllegalArgumentException("Le met sélectionné n'existe pas."));
		if (!met.isActif()) {
			throw new IllegalArgumentException("Réactivez le met avant de créer ou modifier sa recette.");
		}
		if (rendementPortions == null || rendementPortions <= 0) {
			throw new IllegalArgumentException("Le rendement doit être supérieur à zéro.");
		}
		if (dateEffet == null) {
			throw new IllegalArgumentException("La date d'effet est obligatoire.");
		}
		if (dateEffet.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("La date d'effet ne peut pas être dans le futur.");
		}
		if (lignes == null || lignes.isEmpty()) {
			throw new IllegalArgumentException("Ajoutez au moins un ingrédient à la recette.");
		}

		Set<Integer> ingredientsDejaAjoutes = new HashSet<>();
		for (LigneRecette ligne : lignes) {
			if (ligne.getIngredient() == null || ligne.getIngredient().getId() == null) {
				throw new IllegalArgumentException("Chaque ligne doit avoir un ingrédient.");
			}
			Ingredient ingredient = ingredientDAO.trouverParId(ligne.getIngredient().getId())
					.orElseThrow(() -> new IllegalArgumentException("Un ingrédient sélectionné n'existe pas."));
			if (!ingredient.isActif()) {
				throw new IllegalArgumentException("Un ingrédient désactivé ne peut pas être ajouté à une recette.");
			}
			if (!ingredientsDejaAjoutes.add(ingredient.getId())) {
				throw new IllegalArgumentException("Un ingrédient ne peut apparaître qu'une fois dans une recette.");
			}
			if (ligne.getQuantite() == null || ligne.getQuantite().signum() <= 0
					|| !respectePrecision(ligne.getQuantite(), 10, 3)) {
				throw new IllegalArgumentException("Chaque quantité doit être positive et comporter au plus 3 décimales.");
			}
			if (ligne.getUnite() == null || !ingredient.getUniteBase().equalsIgnoreCase(ligne.getUnite().trim())) {
				throw new IllegalArgumentException("Chaque ligne doit utiliser l'unité de base de son ingrédient ("
						+ ingredient.getNom() + " : " + ingredient.getUniteBase() + ").");
			}
			ligne.setIngredient(ingredient);
			ligne.setUnite(ingredient.getUniteBase());
		}

		recetteDAO.recettesActives(metId).forEach(recetteActive -> recetteActive.setActive(false));
		Recette nouvelleVersion = new Recette();
		nouvelleVersion.setMet(met);
		nouvelleVersion.setVersion(recetteDAO.prochaineVersion(metId));
		nouvelleVersion.setRendementPortions(rendementPortions);
		nouvelleVersion.setDateEffet(dateEffet);
		nouvelleVersion.setActive(true);
		for (LigneRecette ligne : lignes) {
			ligne.setRecette(nouvelleVersion);
			nouvelleVersion.getLignes().add(ligne);
		}
		recetteDAO.sauvegarder(nouvelleVersion);
		return nouvelleVersion;
	}

	private boolean respectePrecision(BigDecimal valeur, int precisionMax, int scaleMax) {
		BigDecimal normalisee = valeur.stripTrailingZeros();
		return normalisee.scale() <= scaleMax
				&& normalisee.precision() - normalisee.scale() <= precisionMax - scaleMax;
	}
}