package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.Achat;
import bj.restaurant.couts.Entities.LigneAchat;
import jakarta.persistence.EntityGraph;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Subgraph;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@ApplicationScoped
public class AchatDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Achat> rechercher(String recherche) {
		String jpql = "SELECT DISTINCT a FROM Achat a ";
		if (recherche == null || recherche.isBlank()) {
			return entityManager.createQuery(jpql + "ORDER BY a.dateAchat DESC, a.id DESC", Achat.class)
					.setHint("jakarta.persistence.fetchgraph", grapheChargement())
					.getResultList();
		}

		return entityManager.createQuery(jpql
				+ "WHERE LOWER(a.numeroFacture) LIKE :terme "
				+ "OR LOWER(a.fournisseur.nom) LIKE :terme "
				+ "OR EXISTS (SELECT ligne.id FROM LigneAchat ligne "
				+ "WHERE ligne.achat = a AND LOWER(ligne.ingredient.nom) LIKE :terme) "
				+ "ORDER BY a.dateAchat DESC, a.id DESC", Achat.class)
				.setParameter("terme", "%" + recherche.trim().toLowerCase() + "%")
				.setHint("jakarta.persistence.fetchgraph", grapheChargement())
				.getResultList();
	}

	public Optional<Achat> trouverParId(Integer id) {
		List<Achat> achats = entityManager.createQuery(
				"SELECT DISTINCT a FROM Achat a WHERE a.id = :id",
				Achat.class)
				.setHint("jakarta.persistence.fetchgraph", grapheChargement())
				.setParameter("id", id)
				.getResultList();
		return achats.stream().findFirst();
	}

	private EntityGraph<Achat> grapheChargement() {
		EntityGraph<Achat> graphe = entityManager.createEntityGraph(Achat.class);
		graphe.addAttributeNodes("fournisseur");
		Subgraph<?> lignes = graphe.addSubgraph("lignes");
		lignes.addAttributeNodes("ingredient");
		return graphe;
	}

	public long compterTous() {
		return entityManager.createQuery("SELECT COUNT(a) FROM Achat a", Long.class).getSingleResult();
	}

	public BigDecimal calculerMontantTotal() {
		return entityManager.createQuery(
				"SELECT SUM(l.quantite * l.prixUnitaire) FROM LigneAchat l", BigDecimal.class)
				.getSingleResult();
	}

	public boolean numeroFactureExiste(String numeroFacture) {
		return entityManager.createQuery(
				"SELECT COUNT(a) FROM Achat a WHERE LOWER(a.numeroFacture) = :numero", Long.class)
				.setParameter("numero", numeroFacture.trim().toLowerCase())
				.getSingleResult() > 0;
	}

	public List<LigneAchat> achatsCompatibles(Integer ingredientId, String unite, LocalDate dateLimite) {
		EntityGraph<LigneAchat> graphe = entityManager.createEntityGraph(LigneAchat.class);
		graphe.addAttributeNodes("achat");
		return entityManager.createQuery(
				"SELECT l FROM LigneAchat l JOIN l.achat a "
						+ "WHERE l.ingredient.id = :ingredientId "
						+ "AND LOWER(l.unite) = :unite "
						+ "AND a.dateAchat <= :dateLimite",
				LigneAchat.class)
				.setParameter("ingredientId", ingredientId)
				.setParameter("unite", unite.trim().toLowerCase())
				.setParameter("dateLimite", dateLimite)
				.setHint("jakarta.persistence.fetchgraph", graphe)
				.getResultList();
	}

	public Achat sauvegarder(Achat achat) {
		entityManager.persist(achat);
		return achat;
	}
}