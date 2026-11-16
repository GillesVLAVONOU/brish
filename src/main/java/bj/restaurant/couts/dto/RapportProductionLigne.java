package bj.restaurant.couts.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RapportProductionLigne {
	private final Integer productionId;
	private final LocalDateTime dateProduction;
	private final String metNom;
	private final Integer versionRecette;
	private final Integer portionsReelles;
	private final BigDecimal coutMatiere;
	private final BigDecimal coutCharges;
	private final BigDecimal coutTotal;
	private final BigDecimal coutUnitaire;

	public RapportProductionLigne(Integer productionId, LocalDateTime dateProduction, String metNom,
			Integer versionRecette, Integer portionsReelles, BigDecimal coutMatiere,
			BigDecimal coutCharges, BigDecimal coutTotal, BigDecimal coutUnitaire) {
		this.productionId = productionId;
		this.dateProduction = dateProduction;
		this.metNom = metNom;
		this.versionRecette = versionRecette;
		this.portionsReelles = portionsReelles;
		this.coutMatiere = coutMatiere;
		this.coutCharges = coutCharges;
		this.coutTotal = coutTotal;
		this.coutUnitaire = coutUnitaire;
	}

	public Integer getProductionId() { return productionId; }
	public LocalDateTime getDateProduction() { return dateProduction; }
	public String getDateAffichee() {
		return dateProduction.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
	}
	public String getMetNom() { return metNom; }
	public Integer getVersionRecette() { return versionRecette; }
	public Integer getPortionsReelles() { return portionsReelles; }
	public BigDecimal getCoutMatiere() { return coutMatiere; }
	public BigDecimal getCoutCharges() { return coutCharges; }
	public BigDecimal getCoutTotal() { return coutTotal; }
	public BigDecimal getCoutUnitaire() { return coutUnitaire; }
}
