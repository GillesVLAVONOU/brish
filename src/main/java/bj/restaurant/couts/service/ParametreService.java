package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.ParametresRestaurantDAO;
import bj.restaurant.couts.Entities.ParametresRestaurant;
import bj.restaurant.couts.Entities.Achat;
import bj.restaurant.couts.Entities.ChargeProduction;
import bj.restaurant.couts.Entities.HistoriqueCout;
import bj.restaurant.couts.Entities.Met;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Set;

@ApplicationScoped
public class ParametreService {
	private static final Set<String> DEVISES_AUTORISEES = Set.of("XOF", "XAF", "NGN", "GHS", "EUR", "USD");

	@Inject
	private ParametresRestaurantDAO parametresDAO;

	@PersistenceContext(unitName = "BrishPU")
	private EntityManager entityManager;

	public ParametresRestaurant charger() {
		return parametresDAO.trouver().orElseThrow(
				() -> new IllegalStateException("Les paramètres du restaurant sont absents. Appliquez la migration de la base."));
	}

	public boolean deviseModifiable() {
		return entityManager.createQuery("SELECT COUNT(a) FROM Achat a", Long.class).getSingleResult() == 0
				&& entityManager.createQuery("SELECT COUNT(m) FROM Met m", Long.class).getSingleResult() == 0
				&& entityManager.createQuery("SELECT COUNT(h) FROM HistoriqueCout h", Long.class).getSingleResult() == 0
				&& entityManager.createQuery("SELECT COUNT(c) FROM ChargeProduction c", Long.class).getSingleResult() == 0;
	}

	@Transactional
	public void mettreAJour(String nom, String adresse, String telephone, String codeDevise) {
		ParametresRestaurant parametres = charger();
		String nomNormalise = nom == null ? "" : nom.trim();
		String adresseNormalisee = adresse == null ? "" : adresse.trim();
		String telephoneNormalise = telephone == null ? "" : telephone.trim();
		String deviseNormalisee = codeDevise == null ? "" : codeDevise.trim().toUpperCase();

		if (nomNormalise.isBlank() || nomNormalise.length() > 120) {
			throw new IllegalArgumentException("Le nom du restaurant est obligatoire et ne peut pas dépasser 120 caractères.");
		}
		if (adresseNormalisee.length() > 255) {
			throw new IllegalArgumentException("L'adresse ne peut pas dépasser 255 caractères.");
		}
		if (telephoneNormalise.length() > 30) {
			throw new IllegalArgumentException("Le téléphone ne peut pas dépasser 30 caractères.");
		}
		if (!DEVISES_AUTORISEES.contains(deviseNormalisee)) {
			throw new IllegalArgumentException("Sélectionnez une devise prise en charge.");
		}
		if (!deviseNormalisee.equals(parametres.getCodeDevise()) && !deviseModifiable()) {
			throw new IllegalArgumentException("La devise ne peut plus être modifiée après l'enregistrement de prix, d'achats ou de coûts. Une conversion explicite sera nécessaire.");
		}

		parametres.setNomRestaurant(nomNormalise);
		parametres.setAdresse(adresseNormalisee.isEmpty() ? null : adresseNormalisee);
		parametres.setTelephone(telephoneNormalise.isEmpty() ? null : telephoneNormalise);
		parametres.setCodeDevise(deviseNormalisee);
		entityManager.merge(parametres);
	}
}
