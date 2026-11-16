package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.Ingredient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class IngredientDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Ingredient> rechercher(String recherche) {
		if (recherche == null || recherche.isBlank()) {
			return entityManager.createQuery("SELECT i FROM Ingredient i ORDER BY i.nom", Ingredient.class)
					.getResultList();
		}

		return entityManager.createQuery(
				"SELECT i FROM Ingredient i "
						+ "WHERE LOWER(i.nom) LIKE :terme "
						+ "OR LOWER(i.uniteBase) LIKE :terme "
						+ "ORDER BY i.nom",
				Ingredient.class)
				.setParameter("terme", "%" + recherche.trim().toLowerCase() + "%")
				.getResultList();
	}

	public List<Ingredient> rechercherActifs() {
		return entityManager.createQuery(
				"SELECT i FROM Ingredient i WHERE i.actif = true ORDER BY i.nom", Ingredient.class)
				.getResultList();
	}

	public long compterTous() {
		return entityManager.createQuery("SELECT COUNT(i) FROM Ingredient i", Long.class).getSingleResult();
	}

	public long compterActifs() {
		return entityManager.createQuery(
				"SELECT COUNT(i) FROM Ingredient i WHERE i.actif = true", Long.class)
				.getSingleResult();
	}

	public long compterUnitesBase() {
		return entityManager.createQuery(
				"SELECT COUNT(DISTINCT i.uniteBase) FROM Ingredient i WHERE i.uniteBase IS NOT NULL",
				Long.class)
				.getSingleResult();
	}

	public Optional<Ingredient> trouverParId(Integer id) {
		return Optional.ofNullable(entityManager.find(Ingredient.class, id));
	}

	public boolean nomExiste(String nom, Integer idExclu) {
		String jpql = "SELECT COUNT(i) FROM Ingredient i WHERE LOWER(i.nom) = :nom";
		var query = entityManager.createQuery(jpql + (idExclu == null ? "" : " AND i.id <> :id"), Long.class)
				.setParameter("nom", nom.trim().toLowerCase());
		if (idExclu != null) {
			query.setParameter("id", idExclu);
		}
		return query.getSingleResult() > 0;
	}

	public Ingredient sauvegarder(Ingredient ingredient) {
		if (ingredient.getId() == null) {
			entityManager.persist(ingredient);
			return ingredient;
		}
		return entityManager.merge(ingredient);
	}
}