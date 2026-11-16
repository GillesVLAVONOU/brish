package bj.restaurant.couts.service;

import bj.restaurant.couts.Entities.LigneVente;
import bj.restaurant.couts.Entities.Production;
import bj.restaurant.couts.Entities.Vente;
import bj.restaurant.couts.dao.VenteDAO;
import bj.restaurant.couts.dto.ProductionVendable;
import bj.restaurant.couts.dto.RapportMargeLigne;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class VenteService {
	@Inject
	private VenteDAO venteDAO;

	public List<ProductionVendable> productionsVendables() {
		Map<Integer, Long> vendues = new HashMap<>();
		for (Object[] ligne : venteDAO.quantitesVenduesParProduction()) {
			vendues.put((Integer) ligne[0], (Long) ligne[1]);
		}
		List<ProductionVendable> resultat = new ArrayList<>();
		for (Production production : venteDAO.productionsEligibles()) {
			long portionsVendues = vendues.getOrDefault(production.getId(), 0L);
			if (production.getPortionsReelles() != null
					&& portionsVendues < production.getPortionsReelles()) {
				resultat.add(new ProductionVendable(production, portionsVendues));
			}
		}
		return resultat;
	}

	@Transactional
	public Vente enregistrer(LocalDate dateVente, Integer utilisateurId,
			Map<Integer, Integer> portionsParProduction,
			Map<Integer, BigDecimal> prixParProduction) {
		if (dateVente == null || dateVente.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("La date de vente est obligatoire et ne peut pas être dans le futur.");
		}
		if (portionsParProduction == null || portionsParProduction.isEmpty()
				|| prixParProduction == null || !portionsParProduction.keySet().equals(prixParProduction.keySet())) {
			throw new IllegalArgumentException("Ajoutez au moins une ligne de vente complète.");
		}

		Vente vente = new Vente();
		vente.setDateVente(dateVente);
		vente.setUtilisateur(venteDAO.utilisateur(utilisateurId));

		for (Integer productionId : portionsParProduction.keySet().stream()
				.sorted(Comparator.naturalOrder()).toList()) {
			Production production = venteDAO.verrouillerProduction(productionId);
			if (production == null || !production.isTerminee() || production.getPortionsReelles() == null) {
				throw new IllegalArgumentException("Une production sélectionnée n'existe pas ou n'est pas terminée.");
			}
			LocalDate dateProduction = production.getDateCloture() != null
					? production.getDateCloture()
					: production.getDateProduction().toLocalDate();
			if (dateVente.isBefore(dateProduction)) {
				throw new IllegalArgumentException("La vente ne peut pas précéder la clôture de la production.");
			}
			if (!venteDAO.possedeUnCout(productionId)) {
				throw new IllegalArgumentException("La production #" + productionId
						+ " n'a pas d'instantané de coût et ne peut pas être vendue.");
			}

			Integer portions = portionsParProduction.get(productionId);
			BigDecimal prix = prixParProduction.get(productionId);
			if (portions == null || portions <= 0) {
				throw new IllegalArgumentException("Le nombre de portions vendues doit être supérieur à zéro.");
			}
			if (prix == null || prix.signum() < 0 || !respectePrecision(prix, 10, 2)) {
				throw new IllegalArgumentException("Le prix unitaire doit être positif ou nul et comporter au plus 2 décimales.");
			}
			long dejaVendues = venteDAO.compterPortionsVendues(productionId);
			if (dejaVendues + portions > production.getPortionsReelles()) {
				throw new IllegalArgumentException("La vente dépasse les portions encore disponibles pour "
						+ production.getRecette().getMet().getNom() + " (production #" + productionId + ").");
			}

			LigneVente ligne = new LigneVente();
			ligne.setVente(vente);
			ligne.setProduction(production);
			ligne.setPortionsVendues(portions);
			ligne.setPrixUnitaireApplique(prix);
			vente.getLignes().add(ligne);
		}
		venteDAO.sauvegarder(vente);
		return vente;
	}

	public List<RapportMargeLigne> marges(LocalDate debut, LocalDate fin) {
		if (debut == null || fin == null) {
			throw new IllegalArgumentException("Sélectionnez une date de début et une date de fin.");
		}
		if (fin.isBefore(debut)) {
			throw new IllegalArgumentException("La date de fin doit être égale ou postérieure à la date de début.");
		}
		return venteDAO.marges(debut, fin);
	}

	private boolean respectePrecision(BigDecimal valeur, int precisionMax, int scaleMax) {
		BigDecimal normalisee = valeur.stripTrailingZeros();
		return normalisee.scale() <= scaleMax
				&& normalisee.precision() - normalisee.scale() <= precisionMax - scaleMax;
	}
}
