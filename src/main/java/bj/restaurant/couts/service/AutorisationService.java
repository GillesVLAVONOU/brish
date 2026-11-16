package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.AutorisationDAO;
import bj.restaurant.couts.model.DroitsModule;
import bj.restaurant.couts.model.DroitsPersonnalises;
import bj.restaurant.couts.model.DroitsRole;
import bj.restaurant.couts.model.ModuleApplication;
import bj.restaurant.couts.model.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class AutorisationService {
	private static final String HERITER = "HERITER";
	private static final String AUTORISER = "AUTORISER";
	private static final String REFUSER = "REFUSER";

	@Inject
	private AutorisationDAO autorisationDAO;

	public boolean estAutorise(Integer utilisateurId, Role role, ModuleApplication module, String methode) {
		if (role == Role.ADMINISTRATEUR) {
			return true;
		}
		DroitsRole droitsRole = autorisationDAO.chargerDroitsRole(role).getOrDefault(module,
				new DroitsRole(module.consultationParDefaut(role), module.modificationParDefaut(role)));
		DroitsModule droits = creerVue(module, droitsRole,
				autorisationDAO.chargerDroitsPersonnalises(utilisateurId).get(module));
		if ("GET".equalsIgnoreCase(methode) || "HEAD".equalsIgnoreCase(methode)) {
			return droits.isConsultationEffective();
		}
		if ("POST".equalsIgnoreCase(methode)) {
			return droits.isModificationEffective();
		}
		return false;
	}

	public String cheminAccueil(Integer utilisateurId, Role role) {
		return cheminAccueilAutorise(chargerModulesConsultables(utilisateurId, role));
	}

	public String cheminAccueilAutorise(Set<String> modulesAutorises) {
		ModuleApplication[] ordre = {
			ModuleApplication.DASHBOARD,
			ModuleApplication.PRODUCTIONS,
			ModuleApplication.RECETTES,
			ModuleApplication.INGREDIENTS,
			ModuleApplication.METS,
			ModuleApplication.FOURNISSEURS,
			ModuleApplication.ACHATS,
			ModuleApplication.VENTES,
			ModuleApplication.RAPPORTS,
			ModuleApplication.PARAMETRES
		};
		for (ModuleApplication module : ordre) {
			if (modulesAutorises.contains(module.name())) {
				return module.getCheminAccueil();
			}
		}
		return "/acces-refuse";
	}

	public Set<String> chargerModulesConsultables(Integer utilisateurId, Role role) {
		Set<String> modulesAutorises = new HashSet<>();
		if (role == Role.ADMINISTRATEUR) {
			for (ModuleApplication module : ModuleApplication.values()) {
				modulesAutorises.add(module.name());
			}
			return modulesAutorises;
		}
		for (DroitsModule droits : chargerDroitsUtilisateur(utilisateurId, role)) {
			if (droits.isConsultationEffective()) {
				modulesAutorises.add(droits.getModule().name());
			}
		}
		return modulesAutorises;
	}

	public List<DroitsModule> chargerDroitsRole(Role role) {
		Map<ModuleApplication, DroitsRole> droitsRole = autorisationDAO.chargerDroitsRole(role);
		List<DroitsModule> resultat = new ArrayList<>();
		for (ModuleApplication module : ModuleApplication.values()) {
			DroitsRole droits = droitsRole.getOrDefault(module,
					new DroitsRole(module.consultationParDefaut(role), module.modificationParDefaut(role)));
			resultat.add(creerVue(module, droits, null));
		}
		return resultat;
	}

	public List<DroitsModule> chargerDroitsUtilisateur(Integer utilisateurId, Role role) {
		Map<ModuleApplication, DroitsRole> droitsRole = autorisationDAO.chargerDroitsRole(role);
		Map<ModuleApplication, DroitsPersonnalises> droitsPersonnalises =
				autorisationDAO.chargerDroitsPersonnalises(utilisateurId);
		List<DroitsModule> resultat = new ArrayList<>();
		for (ModuleApplication module : ModuleApplication.values()) {
			DroitsRole droitsHerites = droitsRole.getOrDefault(module,
					new DroitsRole(module.consultationParDefaut(role), module.modificationParDefaut(role)));
			resultat.add(creerVue(module, droitsHerites, droitsPersonnalises.get(module)));
		}
		return resultat;
	}

	@Transactional
	public void enregistrerDroitsRole(Role role, Map<ModuleApplication, DroitsRole> droits) {
		if (role == null || role == Role.ADMINISTRATEUR) {
			throw new IllegalArgumentException("Les droits de l'administrateur sont toujours complets.");
		}
		if (droits.size() != ModuleApplication.values().length) {
			throw new IllegalArgumentException("Les droits de tous les modules doivent être renseignés.");
		}
		for (ModuleApplication module : ModuleApplication.values()) {
			DroitsRole valeur = droits.get(module);
			if (valeur == null) {
				throw new IllegalArgumentException("Droits manquants pour le module " + module.getLibelle() + ".");
			}
			autorisationDAO.enregistrerDroitsRole(role, module, valeur.avecModification());
		}
	}

	@Transactional
	public void enregistrerDroitsUtilisateur(
			Integer utilisateurId, Role role, Map<ModuleApplication, DroitsPersonnalises> droits) {
		if (utilisateurId == null || role == null) {
			throw new IllegalArgumentException("L'utilisateur et son rôle sont obligatoires.");
		}
		if (role == Role.ADMINISTRATEUR) {
			throw new IllegalArgumentException("Les administrateurs disposent toujours de tous les droits.");
		}
		if (droits.size() != ModuleApplication.values().length) {
			throw new IllegalArgumentException("Les droits de tous les modules doivent être renseignés.");
		}
		Map<ModuleApplication, DroitsRole> droitsRole = autorisationDAO.chargerDroitsRole(role);
		for (ModuleApplication module : ModuleApplication.values()) {
			DroitsPersonnalises personnalises = droits.get(module);
			if (personnalises == null) {
				throw new IllegalArgumentException("Droits manquants pour le module " + module.getLibelle() + ".");
			}
			DroitsRole herites = droitsRole.getOrDefault(module,
					new DroitsRole(module.consultationParDefaut(role), module.modificationParDefaut(role)));
			Boolean consultation = personnalises.consultation();
			Boolean modification = personnalises.modification();
			boolean lectureEffective = consultation == null ? herites.consultation() : consultation;
			boolean ecritureEffective = modification == null ? herites.modification() : modification;
			if (ecritureEffective && !lectureEffective) {
				consultation = true;
			}
			autorisationDAO.enregistrerDroitsPersonnalises(
					utilisateurId, module, new DroitsPersonnalises(consultation, modification));
		}
	}

	private DroitsModule creerVue(
			ModuleApplication module, DroitsRole droitsRole, DroitsPersonnalises personnalises) {
		DroitsModule vue = new DroitsModule();
		vue.setModule(module);
		vue.setConsultationRole(droitsRole.consultation());
		vue.setModificationRole(droitsRole.modification());
		Boolean consultation = personnalises == null ? null : personnalises.consultation();
		Boolean modification = personnalises == null ? null : personnalises.modification();
		vue.setConsultationPersonnalisee(valeurInterface(consultation));
		vue.setModificationPersonnalisee(valeurInterface(modification));
		boolean consultationEffective = consultation == null ? droitsRole.consultation() : consultation;
		boolean modificationEffective = modification == null ? droitsRole.modification() : modification;
		vue.setConsultationEffective(consultationEffective || modificationEffective);
		vue.setModificationEffective(modificationEffective);
		return vue;
	}

	private String valeurInterface(Boolean valeur) {
		if (valeur == null) return HERITER;
		return valeur ? AUTORISER : REFUSER;
	}

	public Map<ModuleApplication, DroitsPersonnalises> lireDroitsPersonnalises(
			Map<String, String> valeurs) {
		Map<ModuleApplication, DroitsPersonnalises> resultat = new EnumMap<>(ModuleApplication.class);
		for (ModuleApplication module : ModuleApplication.values()) {
			resultat.put(module, new DroitsPersonnalises(
					lireOverride(valeurs.get("consultation_" + module.name())),
					lireOverride(valeurs.get("modification_" + module.name()))));
		}
		return resultat;
	}

	private Boolean lireOverride(String valeur) {
		if (HERITER.equals(valeur)) return null;
		if (AUTORISER.equals(valeur)) return true;
		if (REFUSER.equals(valeur)) return false;
		throw new IllegalArgumentException("Sélectionnez un choix valide pour chaque autorisation.");
	}
}
