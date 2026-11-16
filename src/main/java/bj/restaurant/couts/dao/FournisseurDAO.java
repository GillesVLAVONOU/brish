package bj.restaurant.couts.dao;

import bj.restaurant.couts.Entities.Fournisseur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FournisseurDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Fournisseur> rechercher(String recherche) {
		if (recherche == null || recherche.isBlank()) {
			return entityManager.createQuery("SELECT f FROM Fournisseur f ORDER BY f.nom", Fournisseur.class)
					.getResultList();
		}

		return entityManager.createQuery(
				"SELECT f FROM Fournisseur f "
						+ "WHERE LOWER(f.nom) LIKE :terme "
						+ "OR LOWER(f.telephone) LIKE :terme "
						+ "OR LOWER(f.email) LIKE :terme "
						+ "OR LOWER(f.adresse) LIKE :terme "
						+ "ORDER BY f.nom",
				Fournisseur.class)
				.setParameter("terme", "%" + recherche.trim().toLowerCase() + "%")
				.getResultList();
	}

	public long compterTous() {
		return entityManager.createQuery("SELECT COUNT(f) FROM Fournisseur f", Long.class).getSingleResult();
	}

	public Optional<Fournisseur> trouverParId(Integer id) {
		return Optional.ofNullable(entityManager.find(Fournisseur.class, id));
	}

	public boolean emailExiste(String email, Integer idExclu) {
		if (email == null) {
			return false;
		}
		String jpql = "SELECT COUNT(f) FROM Fournisseur f WHERE LOWER(f.email) = :email";
		var query = entityManager.createQuery(jpql + (idExclu == null ? "" : " AND f.id <> :id"), Long.class)
				.setParameter("email", email.trim().toLowerCase());
		if (idExclu != null) {
			query.setParameter("id", idExclu);
		}
		return query.getSingleResult() > 0;
	}

	public Fournisseur sauvegarder(Fournisseur fournisseur) {
		if (fournisseur.getId() == null) {
			entityManager.persist(fournisseur);
			return fournisseur;
		}
		return entityManager.merge(fournisseur);
	}
}
