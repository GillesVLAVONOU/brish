package bj.restaurant.couts.service;

import bj.restaurant.couts.model.Role;
import bj.restaurant.couts.Entities.Utilisateur;
import bj.restaurant.couts.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.security.GeneralSecurityException;

@ApplicationScoped
public class AdminBootstrapService {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public boolean administrateurExiste() {
		return entityManager.createQuery(
				"SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role",
				Long.class)
				.setParameter("role", Role.ADMINISTRATEUR)
				.getSingleResult() > 0;
	}

	@Transactional
	public boolean creerPremierAdministrateur(String nom, String email, char[] motDePasse)
			throws GeneralSecurityException {
		if (administrateurExiste()) return false;

		Utilisateur administrateur = new Utilisateur();
		administrateur.setNom(nom.trim());
		administrateur.setEmail(email.trim().toLowerCase());
		administrateur.setRole(Role.ADMINISTRATEUR);
		administrateur.setMotDePasseHash(PasswordUtil.hasher(motDePasse));
		administrateur.setActif(true);
		entityManager.persist(administrateur);
		return true;
	}
}