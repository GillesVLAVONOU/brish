package bj.restaurant.couts.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_achat")
public class LigneAchat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ligne_achat")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_achat", nullable = false)
	private Achat achat;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_ingredient", nullable = false)
	private Ingredient ingredient;

	@Column(nullable = false, precision = 10, scale = 3)
	private BigDecimal quantite;

	@Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
	private BigDecimal prixUnitaire;

	@Column(nullable = false, length = 20)
	private String unite;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Achat getAchat() { return achat; }
	public void setAchat(Achat achat) { this.achat = achat; }
	public Ingredient getIngredient() { return ingredient; }
	public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
	public BigDecimal getQuantite() { return quantite; }
	public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
	public BigDecimal getPrixUnitaire() { return prixUnitaire; }
	public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
	public String getUnite() { return unite; }
	public void setUnite(String unite) { this.unite = unite; }
	public BigDecimal getMontantTotal() { return quantite.multiply(prixUnitaire); }

}