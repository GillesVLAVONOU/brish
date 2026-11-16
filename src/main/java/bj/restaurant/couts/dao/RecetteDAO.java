package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.Recette;
import bj.restaurant.couts.Entities.LigneRecette;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RecetteDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Recette> rechercherActives(String recherche) {
		String jpql = "SELECT DISTINCT r FROM Recette r "
				+ "WHERE r.active = true AND r.met.actif = true ";
		if (recherche != null && !recherche.isBlank()) {
			jpql += "AND (LOWER(r.met.nom) LIKE :terme OR LOWER(r.met.description) LIKE :terme) ";
		}
		var requete = entityManager.createQuery(jpql + "ORDER BY r.id", Recette.class)
				.setHint("jakarta.persistence.fetchgraph", grapheChargement());
		if (recherche != null && !recherche.isBlank()) {
			requete.setParameter("terme", "%" + recherche.trim().toLowerCase() + "%");
		}
		List<Recette> recettes = requete.getResultList();
		recettes.sort(Comparator.comparing(
				(Recette recette) -> recette.getMet().getNom(),
				String.CASE_INSENSITIVE_ORDER));
		return recettes;
	}

	public Optional<Recette> trouverParId(Integer id) {
		List<Recette> recettes = entityManager.createQuery(
				"SELECT DISTINCT r FROM Recette r WHERE r.id = :id",
				Recette.class)
				.setHint("jakarta.persistence.fetchgraph", grapheChargement())
				.setParameter("id", id)
				.getResultList();
		return recettes.stream().findFirst();
	}

	public List<Recette> historiqueParMet(Integer metId) {
		return entityManager.createQuery(
				"SELECT DISTINCT r FROM Recette r "
						+ "WHERE r.met.id = :metId ORDER BY r.version DESC",
				Recette.class)
				.setHint("jakarta.persistence.fetchgraph", grapheChargement())
				.setParameter("metId", metId)
				.getResultList();
	}

	private EntityGraph<Recette> grapheChargement() {
		EntityGraph<Recette> graphe = entityManager.createEntityGraph(Recette.class);
		graphe.addAttributeNodes("met");
		graphe.addSubgraph("lignes").addAttributeNodes("ingredient");
		return graphe;
	}

	public int prochaineVersion(Integer metId) {
		return entityManager.createQuery(
				"SELECT COALESCE(MAX(r.version), 0) + 1 FROM Recette r WHERE r.met.id = :metId",
				Integer.class)
				.setParameter("metId", metId)
				.getSingleResult();
	}

	public List<Recette> recettesActives(Integer metId) {
		return entityManager.createQuery(
				"SELECT r FROM Recette r WHERE r.met.id = :metId AND r.active = true",
				Recette.class)
				.setParameter("metId", metId)
				.getResultList();
	}

	public Optional<Recette> trouverRecetteActive(Integer recetteId) {
		return entityManager.createQuery(
				"SELECT r FROM Recette r JOIN FETCH r.met "
						+ "WHERE r.id = :id AND r.active = true AND r.met.actif = true",
				Recette.class)
				.setParameter("id", recetteId)
				.getResultList().stream().findFirst();
	}

	public List<LigneRecette> chargerLignes(Integer recetteId) {
		return entityManager.createQuery(
				"SELECT l FROM LigneRecette l JOIN FETCH l.ingredient "
						+ "WHERE l.recette.id = :id ORDER BY l.ingredient.nom",
				LigneRecette.class)
				.setParameter("id", recetteId)
				.getResultList();
	}

	public long compterActives() {
		return entityManager.createQuery(
				"SELECT COUNT(r) FROM Recette r WHERE r.active = true AND r.met.actif = true", Long.class)
				.getSingleResult();
	}

	public void sauvegarder(Recette recette) {
		entityManager.persist(recette);
	}
}