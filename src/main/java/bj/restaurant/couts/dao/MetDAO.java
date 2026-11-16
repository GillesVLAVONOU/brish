package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.Met;
import bj.restaurant.couts.Entities.Recette;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MetDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Met> rechercher(String recherche) {
		String jpql = "SELECT DISTINCT m FROM Met m "
				+ "LEFT JOIN FETCH m.recettes ";
		if (recherche == null || recherche.isBlank()) {
			return chargerLignesRecettes(entityManager.createQuery(
					jpql + "ORDER BY m.nom", Met.class).getResultList());
		}
		return chargerLignesRecettes(entityManager.createQuery(jpql
				+ "WHERE LOWER(m.nom) LIKE :terme OR LOWER(m.description) LIKE :terme "
				+ "ORDER BY m.nom", Met.class)
				.setParameter("terme", "%" + recherche.trim().toLowerCase() + "%")
				.getResultList());
	}

	public Optional<Met> trouverParId(Integer id) {
		List<Met> mets = entityManager.createQuery(
				"SELECT DISTINCT m FROM Met m "
						+ "LEFT JOIN FETCH m.recettes "
						+ "WHERE m.id = :id",
				Met.class)
				.setParameter("id", id)
				.getResultList();
		chargerLignesRecettes(mets);
		return mets.stream().findFirst();
	}

	private List<Met> chargerLignesRecettes(List<Met> mets) {
		if (mets.isEmpty()) {
			return mets;
		}

		List<Integer> metIds = mets.stream().map(Met::getId).toList();
		entityManager.createQuery(
				"SELECT DISTINCT r FROM Recette r LEFT JOIN FETCH r.lignes "
						+ "WHERE r.met.id IN :metIds",
				Recette.class)
				.setParameter("metIds", metIds)
				.getResultList();
		return mets;
	}

	public long compterTous() {
		return entityManager.createQuery("SELECT COUNT(m) FROM Met m", Long.class).getSingleResult();
	}

	public long compterActifs() {
		return entityManager.createQuery("SELECT COUNT(m) FROM Met m WHERE m.actif = true", Long.class)
				.getSingleResult();
	}

	public boolean nomExiste(String nom, Integer idExclu) {
		String jpql = "SELECT COUNT(m) FROM Met m WHERE LOWER(m.nom) = :nom";
		var query = entityManager.createQuery(jpql + (idExclu == null ? "" : " AND m.id <> :id"), Long.class)
				.setParameter("nom", nom.trim().toLowerCase());
		if (idExclu != null) {
			query.setParameter("id", idExclu);
		}
		return query.getSingleResult() > 0;
	}

	public Met sauvegarder(Met met) {
		if (met.getId() == null) {
			entityManager.persist(met);
			return met;
		}
		return entityManager.merge(met);
	}
}