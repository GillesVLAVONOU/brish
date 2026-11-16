package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.AchatDAO;
import bj.restaurant.couts.dao.FournisseurDAO;
import bj.restaurant.couts.dao.IngredientDAO;
import bj.restaurant.couts.Entities.Achat;
import bj.restaurant.couts.Entities.Fournisseur;
import bj.restaurant.couts.Entities.Ingredient;
import bj.restaurant.couts.Entities.LigneAchat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AchatService {
	@Inject
	private AchatDAO achatDAO;
	@Inject
	private FournisseurDAO fournisseurDAO;
	@Inject
	private IngredientDAO ingredientDAO;

	public List<Achat> rechercher(String recherche) {
		return achatDAO.rechercher(recherche);
	}

	public long compterTous() {
		return achatDAO.compterTous();
	}

	public BigDecimal calculerMontantTotal() {
		BigDecimal total = achatDAO.calculerMontantTotal();
		return total == null ? BigDecimal.ZERO : total;
	}

	public Optional<Achat> trouverParId(Integer id) {
		return achatDAO.trouverParId(id);
	}

	@Transactional
	public Achat enregistrer(Achat achat) {
		if (achat.getFournisseur() == null || achat.getFournisseur().getId() == null) {
			throw new IllegalArgumentException("Un fournisseur doit être sélectionné.");
		}
		if (achat.getDateAchat() == null) {
			throw new IllegalArgumentException("La date de l'achat est obligatoire.");
		}
		if (achat.getNumeroFacture() == null || achat.getNumeroFacture().isBlank()) {
			throw new IllegalArgumentException("Le numéro de facture est obligatoire.");
		}
		achat.setNumeroFacture(achat.getNumeroFacture().trim());
		if (achat.getNumeroFacture().length() > 80) {
			throw new IllegalArgumentException("Le numéro de facture ne peut pas dépasser 80 caractères.");
		}
		if (achatDAO.numeroFactureExiste(achat.getNumeroFacture())) {
			throw new IllegalArgumentException("Ce numéro de facture est déjà enregistré.");
		}

		Fournisseur fournisseur = fournisseurDAO.trouverParId(achat.getFournisseur().getId())
				.orElseThrow(() -> new IllegalArgumentException("Le fournisseur sélectionné n'existe pas."));
		achat.setFournisseur(fournisseur);
		if (achat.getLignes().isEmpty()) {
			throw new IllegalArgumentException("Ajoutez au moins une ligne à l'achat.");
		}

		for (LigneAchat ligne : achat.getLignes()) {
			if (ligne.getIngredient() == null || ligne.getIngredient().getId() == null) {
				throw new IllegalArgumentException("Chaque ligne doit avoir un ingrédient.");
			}
			Ingredient ingredient = ingredientDAO.trouverParId(ligne.getIngredient().getId())
					.orElseThrow(() -> new IllegalArgumentException("Un ingrédient sélectionné n'existe pas."));
			if (!ingredient.isActif()) {
				throw new IllegalArgumentException("Un ingrédient désactivé ne peut pas être ajouté à un achat.");
			}
			if (ligne.getQuantite() == null || ligne.getQuantite().signum() <= 0
					|| !respectePrecision(ligne.getQuantite(), 10, 3)) {
				throw new IllegalArgumentException("La quantité doit être positive et comporter au plus 3 décimales.");
			}
			if (ligne.getPrixUnitaire() == null || ligne.getPrixUnitaire().signum() < 0
					|| !respectePrecision(ligne.getPrixUnitaire(), 10, 2)) {
				throw new IllegalArgumentException("Le prix unitaire doit être positif ou nul et comporter au plus 2 décimales.");
			}
			if (ligne.getUnite() == null || ligne.getUnite().isBlank() || ligne.getUnite().trim().length() > 20) {
				throw new IllegalArgumentException("L'unité est obligatoire et ne peut pas dépasser 20 caractères.");
			}
			ligne.setIngredient(ingredient);
			ligne.setUnite(ligne.getUnite().trim());
			ligne.setAchat(achat);
		}

		return achatDAO.sauvegarder(achat);
	}

	private boolean respectePrecision(BigDecimal valeur, int precisionMax, int scaleMax) {
		BigDecimal normalisee = valeur.stripTrailingZeros();
		return normalisee.scale() <= scaleMax && normalisee.precision() - normalisee.scale() <= precisionMax - scaleMax;
	}
}