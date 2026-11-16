package bj.restaurant.couts.dao;

import bj.restaurant.couts.model.Role;
import bj.restaurant.couts.Entities.Utilisateur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UtilisateurDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public List<Utilisateur> rechercher(String recherche) {
		String jpql = "SELECT u FROM Utilisateur u ";
		if (recherche == null || recherche.isBlank()) {
			return entityManager.createQuery(jpql + "ORDER BY LOWER(u.nom), u.id", Utilisateur.class)
					.getResultList();
		}
		return entityManager.createQuery(jpql
				+ "WHERE LOWER(u.nom) LIKE :terme OR LOWER(u.email) LIKE :terme "
				+ "ORDER BY LOWER(u.nom), u.id", Utilisateur.class)
				.setParameter("terme", "%" + recherche.trim().toLowerCase() + "%")
				.getResultList();
	}

	public Optional<Utilisateur> trouverParId(Integer id) {
		return Optional.ofNullable(entityManager.find(Utilisateur.class, id));
	}

	public Optional<Utilisateur> trouverActifParId(Integer id) {
		List<Utilisateur> utilisateurs = entityManager.createQuery(
				"SELECT u FROM Utilisateur u WHERE u.id = :id AND u.actif = true",
				Utilisateur.class)
				.setParameter("id", id)
				.getResultList();
		return utilisateurs.stream().findFirst();
	}

	public long compterTous() {
		return entityManager.createQuery("SELECT COUNT(u) FROM Utilisateur u", Long.class).getSingleResult();
	}

	public long compterActifs() {
		return entityManager.createQuery("SELECT COUNT(u) FROM Utilisateur u WHERE u.actif = true", Long.class)
				.getSingleResult();
	}

	public long compterActifs(Role role) {
		return entityManager.createQuery(
				"SELECT COUNT(u) FROM Utilisateur u WHERE u.actif = true AND u.role = :role", Long.class)
				.setParameter("role", role)
				.getSingleResult();
	}

	public boolean emailExiste(String email, Integer idExclu) {
		String jpql = "SELECT COUNT(u) FROM Utilisateur u WHERE LOWER(u.email) = :email";
		var requete = entityManager.createQuery(
				jpql + (idExclu == null ? "" : " AND u.id <> :id"), Long.class)
				.setParameter("email", email.trim().toLowerCase());
		if (idExclu != null) {
			requete.setParameter("id", idExclu);
		}
		return requete.getSingleResult() > 0;
	}

	public long verrouillerEtCompterAdministrateursActifs() {
		List<Utilisateur> administrateurs = entityManager.createQuery(
				"SELECT u FROM Utilisateur u WHERE u.role = :role AND u.actif = true ORDER BY u.id",
				Utilisateur.class)
				.setParameter("role", Role.ADMINISTRATEUR)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.getResultList();
		return administrateurs.size();
	}

	public Utilisateur sauvegarder(Utilisateur utilisateur) {
		if (utilisateur.getId() == null) {
			entityManager.persist(utilisateur);
			return utilisateur;
		}
		return entityManager.merge(utilisateur);
	}
}
