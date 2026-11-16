package bj.restaurant.couts.service;

import bj.restaurant.couts.dto.RapportAchatLigne;
import bj.restaurant.couts.dto.RapportProductionLigne;
import bj.restaurant.couts.dto.RapportMargeLigne;
import bj.restaurant.couts.dao.VenteDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class RapportService {

	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;
	@Inject
	private VenteDAO venteDAO;

	public List<RapportMargeLigne> marges(LocalDate debut, LocalDate fin) {
		validerPeriode(debut, fin);
		return venteDAO.marges(debut, fin);
	}

	public List<RapportProductionLigne> productionsTerminees(LocalDate debut, LocalDate fin) {
		validerPeriode(debut, fin);
		return entityManager.createQuery(
				"SELECT new bj.restaurant.couts.dto.RapportProductionLigne("
						+ "p.id, p.dateProduction, m.nom, r.version, p.portionsReelles, "
						+ "h.coutMatiere, h.coutCharges, h.coutTotal, h.coutUnitaire) "
						+ "FROM HistoriqueCout h JOIN h.production p "
						+ "JOIN p.recette r JOIN r.met m "
						+ "WHERE p.statut = 'TERMINEE' "
						+ "AND h.id = (SELECT MAX(h2.id) FROM HistoriqueCout h2 WHERE h2.production = p) "
						+ "AND p.dateProduction >= :debut AND p.dateProduction < :finExclue "
						+ "ORDER BY p.dateProduction DESC, p.id DESC",
				RapportProductionLigne.class)
				.setParameter("debut", debut.atStartOfDay())
				.setParameter("finExclue", fin.plusDays(1).atStartOfDay())
				.getResultList();
	}

	public List<RapportAchatLigne> achats(LocalDate debut, LocalDate fin) {
		validerPeriode(debut, fin);
		return entityManager.createQuery(
				"SELECT new bj.restaurant.couts.dto.RapportAchatLigne("
						+ "a.id, a.dateAchat, a.numeroFacture, f.nom, COUNT(l), "
						+ "SUM(l.quantite * l.prixUnitaire)) "
						+ "FROM Achat a JOIN a.fournisseur f JOIN a.lignes l "
						+ "WHERE a.dateAchat >= :debut AND a.dateAchat <= :fin "
						+ "GROUP BY a.id, a.dateAchat, a.numeroFacture, f.nom "
						+ "ORDER BY a.dateAchat DESC, a.id DESC",
				RapportAchatLigne.class)
				.setParameter("debut", debut)
				.setParameter("fin", fin)
				.getResultList();
	}

	private void validerPeriode(LocalDate debut, LocalDate fin) {
		if (debut == null || fin == null) {
			throw new IllegalArgumentException("Sélectionnez une date de début et une date de fin.");
		}
		if (fin.isBefore(debut)) {
			throw new IllegalArgumentException("La date de fin doit être égale ou postérieure à la date de début.");
		}
	}
}