package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.FournisseurDAO;
import bj.restaurant.couts.Entities.Fournisseur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FournisseurService {
	@Inject
	private FournisseurDAO fournisseurDAO;

	public List<Fournisseur> rechercher(String recherche) {
		return fournisseurDAO.rechercher(recherche);
	}

	public long compterTous() {
		return fournisseurDAO.compterTous();
	}

	public Optional<Fournisseur> trouverParId(Integer id) {
		return fournisseurDAO.trouverParId(id);
	}

	@Transactional
	public Fournisseur enregistrer(Fournisseur fournisseur) {
		fournisseur.setNom(normaliserObligatoire(fournisseur.getNom(), "Le nom du fournisseur est obligatoire."));
		fournisseur.setTelephone(normaliserOptionnel(fournisseur.getTelephone()));
		fournisseur.setEmail(normaliserOptionnel(fournisseur.getEmail()));
		fournisseur.setAdresse(normaliserOptionnel(fournisseur.getAdresse()));

		if (fournisseur.getEmail() != null
				&& fournisseurDAO.emailExiste(fournisseur.getEmail(), fournisseur.getId())) {
			throw new IllegalArgumentException("Un fournisseur utilise déjà cette adresse e-mail.");
		}

		if (fournisseur.getId() != null) {
			Fournisseur existant = fournisseurDAO.trouverParId(fournisseur.getId())
					.orElseThrow(() -> new IllegalArgumentException("Le fournisseur demandé n'existe pas."));
			existant.setNom(fournisseur.getNom());
			existant.setTelephone(fournisseur.getTelephone());
			existant.setEmail(fournisseur.getEmail());
			existant.setAdresse(fournisseur.getAdresse());
			return fournisseurDAO.sauvegarder(existant);
		}

		return fournisseurDAO.sauvegarder(fournisseur);
	}

	private String normaliserObligatoire(String valeur, String message) {
		if (valeur == null || valeur.isBlank()) {
			throw new IllegalArgumentException(message);
		}
		return valeur.trim();
	}

	private String normaliserOptionnel(String valeur) {
		return valeur == null || valeur.isBlank() ? null : valeur.trim();
	}
}
