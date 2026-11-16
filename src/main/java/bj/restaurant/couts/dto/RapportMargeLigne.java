package bj.restaurant.couts.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RapportMargeLigne {
	private final LocalDate dateVente;
	private final String metNom;
	private final Integer productionId;
	private final Integer portionsVendues;
	private final BigDecimal prixUnitaire;
	private final BigDecimal coutUnitaire;

	public RapportMargeLigne(LocalDate dateVente, String metNom, Integer productionId,
			Integer portionsVendues, BigDecimal prixUnitaire, BigDecimal coutUnitaire) {
		this.dateVente = dateVente;
		this.metNom = metNom;
		this.productionId = productionId;
		this.portionsVendues = portionsVendues;
		this.prixUnitaire = prixUnitaire;
		this.coutUnitaire = coutUnitaire;
	}

	public LocalDate getDateVente() { return dateVente; }
	public String getDateAffichee() { return dateVente.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
	public String getMetNom() { return metNom; }
	public Integer getProductionId() { return productionId; }
	public Integer getPortionsVendues() { return portionsVendues; }
	public BigDecimal getPrixUnitaire() { return prixUnitaire; }
	public BigDecimal getCoutUnitaire() { return coutUnitaire; }
	public BigDecimal getChiffreAffaires() {
		return prixUnitaire.multiply(BigDecimal.valueOf(portionsVendues)).setScale(2, RoundingMode.HALF_UP);
	}
	public BigDecimal getCoutAssocie() {
		return coutUnitaire.multiply(BigDecimal.valueOf(portionsVendues)).setScale(2, RoundingMode.HALF_UP);
	}
	public BigDecimal getMargeBrute() { return getChiffreAffaires().subtract(getCoutAssocie()); }
	public BigDecimal getTauxMarge() {
		BigDecimal chiffreAffaires = getChiffreAffaires();
		return chiffreAffaires.signum() == 0 ? BigDecimal.ZERO
				: getMargeBrute().multiply(BigDecimal.valueOf(100))
						.divide(chiffreAffaires, 2, RoundingMode.HALF_UP);
	}
}
