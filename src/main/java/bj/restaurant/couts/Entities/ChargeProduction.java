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
@Table(name = "charges_production")
public class ChargeProduction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_charge_production")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_production", nullable = false)
	private Production production;

	@Column(nullable = false, length = 120)
	private String libelle;

	@Column(name = "methode_affectation", nullable = false, length = 30)
	private String methodeAffectation;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal montant;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Production getProduction() { return production; }
	public void setProduction(Production production) { this.production = production; }
	public String getLibelle() { return libelle; }
	public void setLibelle(String libelle) { this.libelle = libelle; }
	public String getMethodeAffectation() { return methodeAffectation; }
	public void setMethodeAffectation(String methodeAffectation) { this.methodeAffectation = methodeAffectation; }
	public BigDecimal getMontant() { return montant; }
	public void setMontant(BigDecimal montant) { this.montant = montant; }

}