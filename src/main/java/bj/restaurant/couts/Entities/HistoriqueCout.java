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
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_couts")
public class HistoriqueCout {

	@Column(name = "date_calcul", nullable = false)
	private LocalDateTime dateCalcul;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_historique")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_production", nullable = false)
	private Production production;


	@Column(name = "cout_matiere", nullable = false, precision = 10, scale = 2)
	private BigDecimal coutMatiere;

	@Column(name = "cout_charges", nullable = false, precision = 10, scale = 2)
	private BigDecimal coutCharges;

	@Column(name = "cout_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal coutTotal;

	@Column(name = "cout_unitaire", nullable = false, precision = 10, scale = 2)
	private BigDecimal coutUnitaire;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Production getProduction() { return production; }
	public void setProduction(Production production) { this.production = production; }
	public LocalDateTime getDateCalcul() { return dateCalcul; }
	public void setDateCalcul(LocalDateTime dateCalcul) { this.dateCalcul = dateCalcul; }
	public BigDecimal getCoutMatiere() { return coutMatiere; }
	public void setCoutMatiere(BigDecimal coutMatiere) { this.coutMatiere = coutMatiere; }
	public BigDecimal getCoutCharges() { return coutCharges; }
	public void setCoutCharges(BigDecimal coutCharges) { this.coutCharges = coutCharges; }
	public BigDecimal getCoutTotal() { return coutTotal; }
	public void setCoutTotal(BigDecimal coutTotal) { this.coutTotal = coutTotal; }
	public BigDecimal getCoutUnitaire() { return coutUnitaire; }
	public void setCoutUnitaire(BigDecimal coutUnitaire) { this.coutUnitaire = coutUnitaire; }

    public HistoriqueCout() {
    }

}