package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.HistoriqueCout;
import bj.restaurant.couts.Entities.LigneProduction;
import bj.restaurant.couts.Entities.Production;
import bj.restaurant.couts.Entities.Utilisateur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductionDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Production> rechercher(String recherche) {
		String jpql = "SELECT p FROM Production p JOIN p.recette r JOIN r.met m ";
		if (recherche == null || recherche.isBlank()) {
			return entityManager.createQuery(jpql + "ORDER BY p.dateProduction DESC, p.id DESC", Production.class)
					.setHint("jakarta.persistence.fetchgraph", grapheChargement())
					.getResultList();
		}
		return entityManager.createQuery(jpql
				+ "WHERE LOWER(m.nom) LIKE :terme OR LOWER(p.statut) LIKE :terme "
				+ "ORDER BY p.dateProduction DESC, p.id DESC", Production.class)
				.setParameter("terme", "%" + recherche.trim().toLowerCase() + "%")
				.setHint("jakarta.persistence.fetchgraph", grapheChargement())
				.getResultList();
	}

	public Optional<Production> trouverParId(Integer id) {
		return entityManager.createQuery(
				"SELECT p FROM Production p WHERE p.id = :id",
				Production.class)
				.setHint("jakarta.persistence.fetchgraph", grapheChargement())
				.setParameter("id", id)
				.getResultList().stream().findFirst();
	}

	private EntityGraph<Production> grapheChargement() {
		EntityGraph<Production> graphe = entityManager.createEntityGraph(Production.class);
		graphe.addSubgraph("recette").addAttributeNodes("met");
		return graphe;
	}

	public List<LigneProduction> chargerLignes(Integer productionId) {
		return entityManager.createQuery(
				"SELECT l FROM LigneProduction l JOIN FETCH l.ingredient "
						+ "WHERE l.production.id = :id ORDER BY l.ingredient.nom",
				LigneProduction.class)
				.setParameter("id", productionId)
				.getResultList();
	}

	public List<HistoriqueCout> chargerHistoriqueCouts(Integer productionId) {
		return entityManager.createQuery(
				"SELECT h FROM HistoriqueCout h WHERE h.production.id = :id ORDER BY h.dateCalcul DESC",
				HistoriqueCout.class)
				.setParameter("id", productionId)
				.getResultList();
	}

	public long compterTous() {
		return entityManager.createQuery("SELECT COUNT(p) FROM Production p", Long.class).getSingleResult();
	}

	public long compterBrouillons() {
		return entityManager.createQuery(
				"SELECT COUNT(p) FROM Production p WHERE p.statut = 'BROUILLON'", Long.class)
				.getSingleResult();
	}

	public long compterPortionsTerminees() {
		Long total = entityManager.createQuery(
				"SELECT SUM(p.portionsReelles) FROM Production p WHERE p.statut = 'TERMINEE'", Long.class)
				.getSingleResult();
		return total == null ? 0 : total;
	}

	public java.math.BigDecimal calculerCoutTotalTermine() {
		java.math.BigDecimal total = entityManager.createQuery(
				"SELECT SUM(h.coutTotal) FROM HistoriqueCout h WHERE h.production.statut = 'TERMINEE'",
				java.math.BigDecimal.class)
				.getSingleResult();
		return total == null ? java.math.BigDecimal.ZERO : total;
	}

	public void sauvegarder(Production production) {
		entityManager.persist(production);
	}

	public Utilisateur utilisateur(Integer id) {
		return id == null ? null : entityManager.getReference(Utilisateur.class, id);
	}

	public void sauvegarder(HistoriqueCout historiqueCout) {
		entityManager.persist(historiqueCout);
	}
}