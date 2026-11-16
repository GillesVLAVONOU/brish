package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.Production;
import bj.restaurant.couts.Entities.Utilisateur;
import bj.restaurant.couts.Entities.Vente;
import bj.restaurant.couts.dto.RapportMargeLigne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class VenteDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Production> productionsEligibles() {
		return entityManager.createQuery(
				"SELECT p FROM Production p JOIN FETCH p.recette JOIN FETCH p.recette.met "
						+ "WHERE p.statut = 'TERMINEE' AND p.portionsReelles IS NOT NULL "
						+ "AND EXISTS (SELECT h.id FROM HistoriqueCout h WHERE h.production = p) "
						+ "ORDER BY p.dateProduction DESC, p.id DESC",
				Production.class)
				.getResultList();
	}

	public List<Object[]> quantitesVenduesParProduction() {
		return entityManager.createQuery(
				"SELECT l.production.id, SUM(l.portionsVendues) FROM LigneVente l "
						+ "GROUP BY l.production.id",
				Object[].class)
				.getResultList();
	}

	public Production verrouillerProduction(Integer productionId) {
		Production production = entityManager.find(Production.class, productionId, LockModeType.PESSIMISTIC_WRITE);
		if (production != null) {
			entityManager.refresh(production, LockModeType.PESSIMISTIC_WRITE);
		}
		return production;
	}

	public long compterPortionsVendues(Integer productionId) {
		Long total = entityManager.createQuery(
				"SELECT SUM(l.portionsVendues) FROM LigneVente l "
						+ "WHERE l.production.id = :productionId",
				Long.class)
				.setParameter("productionId", productionId)
				.getSingleResult();
		return total == null ? 0L : total;
	}

	public boolean possedeUnCout(Integer productionId) {
		return entityManager.createQuery(
				"SELECT COUNT(h) FROM HistoriqueCout h WHERE h.production.id = :productionId",
				Long.class)
				.setParameter("productionId", productionId)
				.getSingleResult() > 0;
	}

	public Utilisateur utilisateur(Integer id) {
		return id == null ? null : entityManager.getReference(Utilisateur.class, id);
	}

	public void sauvegarder(Vente vente) {
		entityManager.persist(vente);
	}

	public List<RapportMargeLigne> marges(LocalDate debut, LocalDate fin) {
		return entityManager.createQuery(
				"SELECT new bj.restaurant.couts.dto.RapportMargeLigne("
						+ "v.dateVente, m.nom, p.id, l.portionsVendues, "
						+ "l.prixUnitaireApplique, h.coutUnitaire) "
						+ "FROM LigneVente l JOIN l.vente v JOIN l.production p "
						+ "JOIN p.recette r JOIN r.met m, HistoriqueCout h "
						+ "WHERE v.dateVente >= :debut AND v.dateVente <= :fin "
						+ "AND h.production = p "
						+ "AND h.id = (SELECT MAX(h2.id) FROM HistoriqueCout h2 WHERE h2.production = p) "
						+ "ORDER BY v.dateVente DESC, v.id DESC, l.id",
				RapportMargeLigne.class)
				.setParameter("debut", debut)
				.setParameter("fin", fin)
				.getResultList();
	}
}
