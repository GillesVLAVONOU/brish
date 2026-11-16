package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.MetDAO;
import bj.restaurant.couts.Entities.Met;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MetService {
	@Inject
	private MetDAO metDAO;

	@Transactional
	public List<Met> rechercher(String recherche) {
		return metDAO.rechercher(recherche);
	}

	public long compterTous() {
		return metDAO.compterTous();
	}

	public long compterActifs() {
		return metDAO.compterActifs();
	}

	@Transactional
	public Optional<Met> trouverParId(Integer id) {
		return metDAO.trouverParId(id);
	}

	@Transactional
	public Met enregistrer(Met met) {
		if (met.getNom() == null || met.getNom().isBlank()) {
			throw new IllegalArgumentException("Le nom du met est obligatoire.");
		}
		met.setNom(met.getNom().trim());
		if (met.getNom().length() > 120) {
			throw new IllegalArgumentException("Le nom ne peut pas dépasser 120 caractères.");
		}
		if (metDAO.nomExiste(met.getNom(), met.getId())) {
			throw new IllegalArgumentException("Un met porte déjà ce nom.");
		}
		if (met.getPrixVenteActuel() == null || met.getPrixVenteActuel().signum() < 0
				|| !respectePrecision(met.getPrixVenteActuel(), 10, 2)) {
			throw new IllegalArgumentException("Le prix de vente doit être positif ou nul et comporter au plus 2 décimales.");
		}

		if (met.getId() != null) {
			Met existant = metDAO.trouverParId(met.getId())
					.orElseThrow(() -> new IllegalArgumentException("Le met demandé n'existe pas."));
			existant.setNom(met.getNom());
			existant.setDescription(met.getDescription());
			existant.setPrixVenteActuel(met.getPrixVenteActuel());
			return metDAO.sauvegarder(existant);
		}
		return metDAO.sauvegarder(met);
	}

	@Transactional
	public boolean desactiver(Integer id) {
		Met met = metDAO.trouverParId(id).orElse(null);
		if (met == null) {
			return false;
		}
		met.setActif(false);
		met.getRecettes().stream().filter(recette -> recette.isActive()).forEach(recette -> recette.setActive(false));
		return true;
	}

	@Transactional
	public boolean activer(Integer id) {
		Met met = metDAO.trouverParId(id).orElse(null);
		if (met == null) {
			return false;
		}
		met.setActif(true);
		return true;
	}

	private boolean respectePrecision(BigDecimal valeur, int precisionMax, int scaleMax) {
		BigDecimal normalisee = valeur.stripTrailingZeros();
		return normalisee.scale() <= scaleMax
				&& normalisee.precision() - normalisee.scale() <= precisionMax - scaleMax;
	}
}
