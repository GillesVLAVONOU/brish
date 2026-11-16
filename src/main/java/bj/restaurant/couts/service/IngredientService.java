package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.IngredientDAO;
import bj.restaurant.couts.Entities.Ingredient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class IngredientService {
	@Inject
	private IngredientDAO ingredientDAO;

	public List<Ingredient> rechercher(String recherche) {
		return ingredientDAO.rechercher(recherche);
	}

	public List<Ingredient> rechercherActifs() {
		return ingredientDAO.rechercherActifs();
	}

	public long compterTous() {
		return ingredientDAO.compterTous();
	}

	public long compterActifs() {
		return ingredientDAO.compterActifs();
	}

	public long compterUnitesBase() {
		return ingredientDAO.compterUnitesBase();
	}

	public Optional<Ingredient> trouverParId(Integer id) {
		return ingredientDAO.trouverParId(id);
	}

	@Transactional
	public Ingredient enregistrer(Ingredient ingredient) {
		if (ingredientDAO.nomExiste(ingredient.getNom(), ingredient.getId())) {
			throw new IllegalArgumentException("Un ingrédient porte déjà ce nom.");
		}

		if (ingredient.getId() != null) {
			Ingredient existant = ingredientDAO.trouverParId(ingredient.getId())
					.orElseThrow(() -> new IllegalArgumentException("L'ingrédient demandé n'existe pas."));
			existant.setNom(ingredient.getNom().trim());
			existant.setUniteBase(ingredient.getUniteBase().trim());
			existant.setSeuilAlerte(ingredient.getSeuilAlerte());
			return ingredientDAO.sauvegarder(existant);
		}

		ingredient.setNom(ingredient.getNom().trim());
		ingredient.setUniteBase(ingredient.getUniteBase().trim());
		return ingredientDAO.sauvegarder(ingredient);
	}

	@Transactional
	public boolean desactiver(Integer id) {
		Optional<Ingredient> ingredient = ingredientDAO.trouverParId(id);
		if (ingredient.isEmpty()) {
			return false;
		}
		ingredient.get().setActif(false);
		return true;
	}
}