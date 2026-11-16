package bj.restaurant.couts.dao;

import bj.restaurant.couts.model.DroitsPersonnalises;
import bj.restaurant.couts.model.DroitsRole;
import bj.restaurant.couts.model.ModuleApplication;
import bj.restaurant.couts.model.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AutorisationDAO {
	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public Map<ModuleApplication, DroitsRole> chargerDroitsRole(Role role) {
		List<?> lignes = entityManager.createNativeQuery(
				"SELECT module, peut_consulter, peut_modifier FROM autorisations_role WHERE role = :role")
				.setParameter("role", role.name())
				.getResultList();
		Map<ModuleApplication, DroitsRole> droits = new EnumMap<>(ModuleApplication.class);
		for (Object resultat : lignes) {
			Object[] ligne = (Object[]) resultat;
			droits.put(ModuleApplication.valueOf((String) ligne[0]),
					new DroitsRole((Boolean) ligne[1], (Boolean) ligne[2]));
		}
		return droits;
	}

	public Map<ModuleApplication, DroitsPersonnalises> chargerDroitsPersonnalises(Integer utilisateurId) {
		List<?> lignes = entityManager.createNativeQuery(
				"SELECT module, peut_consulter, peut_modifier FROM autorisations_utilisateur "
						+ "WHERE id_utilisateur = :utilisateurId")
				.setParameter("utilisateurId", utilisateurId)
				.getResultList();
		Map<ModuleApplication, DroitsPersonnalises> droits = new EnumMap<>(ModuleApplication.class);
		for (Object resultat : lignes) {
			Object[] ligne = (Object[]) resultat;
			droits.put(ModuleApplication.valueOf((String) ligne[0]),
					new DroitsPersonnalises((Boolean) ligne[1], (Boolean) ligne[2]));
		}
		return droits;
	}

	public DroitsRole chargerDroitEffectif(Integer utilisateurId, Role role, ModuleApplication module) {
		List<?> lignes = entityManager.createNativeQuery(
				"SELECT COALESCE(u.peut_consulter, r.peut_consulter, FALSE), "
						+ "COALESCE(u.peut_modifier, r.peut_modifier, FALSE) "
						+ "FROM autorisations_role r "
						+ "LEFT JOIN autorisations_utilisateur u "
						+ "ON u.id_utilisateur = :utilisateurId AND u.module = r.module "
						+ "WHERE r.role = :role AND r.module = :module")
				.setParameter("utilisateurId", utilisateurId)
				.setParameter("role", role.name())
				.setParameter("module", module.name())
				.getResultList();
		if (lignes.isEmpty()) {
			return new DroitsRole(false, false);
		}
		Object[] ligne = (Object[]) lignes.get(0);
		return new DroitsRole((Boolean) ligne[0], (Boolean) ligne[1]);
	}

	public void enregistrerDroitsRole(Role role, ModuleApplication module, DroitsRole droits) {
		entityManager.createNativeQuery(
				"INSERT INTO autorisations_role (role, module, peut_consulter, peut_modifier) "
						+ "VALUES (:role, :module, :consultation, :modification) "
						+ "ON CONFLICT (role, module) DO UPDATE SET "
						+ "peut_consulter = EXCLUDED.peut_consulter, "
						+ "peut_modifier = EXCLUDED.peut_modifier")
				.setParameter("role", role.name())
				.setParameter("module", module.name())
				.setParameter("consultation", droits.consultation())
				.setParameter("modification", droits.modification())
				.executeUpdate();
	}

	public void enregistrerDroitsPersonnalises(
			Integer utilisateurId, ModuleApplication module, DroitsPersonnalises droits) {
		if (droits.consultation() == null && droits.modification() == null) {
			entityManager.createNativeQuery(
					"DELETE FROM autorisations_utilisateur WHERE id_utilisateur = :utilisateurId AND module = :module")
					.setParameter("utilisateurId", utilisateurId)
					.setParameter("module", module.name())
					.executeUpdate();
			return;
		}
		entityManager.createNativeQuery(
				"INSERT INTO autorisations_utilisateur "
						+ "(id_utilisateur, module, peut_consulter, peut_modifier) "
						+ "VALUES (:utilisateurId, :module, :consultation, :modification) "
						+ "ON CONFLICT (id_utilisateur, module) DO UPDATE SET "
						+ "peut_consulter = EXCLUDED.peut_consulter, "
						+ "peut_modifier = EXCLUDED.peut_modifier")
				.setParameter("utilisateurId", utilisateurId)
				.setParameter("module", module.name())
				.setParameter("consultation", droits.consultation())
				.setParameter("modification", droits.modification())
				.executeUpdate();
	}
}
