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
@Table(name = "lignes_production")
public class LigneProduction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ligne_production")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_production", nullable = false)
	private Production production;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_ingredient", nullable = false)
	private Ingredient ingredient;

	@Column(name = "quantite_reelle", nullable = false, precision = 10, scale = 3)
	private BigDecimal quantiteReelle;

	@Column(nullable = false, length = 20)
	private String unite;

	@Column(name = "cout_unitaire_applique", nullable = false, precision = 10, scale = 2)
	private BigDecimal coutUnitaireApplique;

	@Column(name = "cout_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal coutTotal;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Production getProduction() { return production; }
	public void setProduction(Production production) { this.production = production; }
	public Ingredient getIngredient() { return ingredient; }
	public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
	public BigDecimal getQuantiteReelle() { return quantiteReelle; }
	public void setQuantiteReelle(BigDecimal quantiteReelle) { this.quantiteReelle = quantiteReelle; }
	public String getUnite() { return unite; }
	public void setUnite(String unite) { this.unite = unite; }
	public BigDecimal getCoutUnitaireApplique() { return coutUnitaireApplique; }
	public void setCoutUnitaireApplique(BigDecimal coutUnitaireApplique) { this.coutUnitaireApplique = coutUnitaireApplique; }
	public BigDecimal getCoutTotal() { return coutTotal; }
	public void setCoutTotal(BigDecimal coutTotal) { this.coutTotal = coutTotal; }

    public LigneProduction() {
    }
}