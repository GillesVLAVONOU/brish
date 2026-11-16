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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_vente", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id_vente", "id_production"})
})
public class LigneVente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ligne_vente")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_vente", nullable = false)
	private Vente vente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_production", nullable = false)
	private Production production;

	@Column(name = "portions_vendues", nullable = false)
	private Integer portionsVendues;

	@Column(name = "prix_unitaire_applique", nullable = false, precision = 10, scale = 2)
	private BigDecimal prixUnitaireApplique;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Vente getVente() { return vente; }
	public void setVente(Vente vente) { this.vente = vente; }
	public Production getProduction() { return production; }
	public void setProduction(Production production) { this.production = production; }
	public Integer getPortionsVendues() { return portionsVendues; }
	public void setPortionsVendues(Integer portionsVendues) { this.portionsVendues = portionsVendues; }
	public BigDecimal getPrixUnitaireApplique() { return prixUnitaireApplique; }
	public void setPrixUnitaireApplique(BigDecimal prixUnitaireApplique) { this.prixUnitaireApplique = prixUnitaireApplique; }
}
