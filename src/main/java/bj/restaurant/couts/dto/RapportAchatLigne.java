package bj.restaurant.couts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RapportAchatLigne {
	private final Integer achatId;
	private final LocalDate dateAchat;
	private final String numeroFacture;
	private final String fournisseurNom;
	private final Long nombreLignes;
	private final BigDecimal montantTotal;

	public RapportAchatLigne(Integer achatId, LocalDate dateAchat, String numeroFacture,
			String fournisseurNom, Long nombreLignes, BigDecimal montantTotal) {
		this.achatId = achatId;
		this.dateAchat = dateAchat;
		this.numeroFacture = numeroFacture;
		this.fournisseurNom = fournisseurNom;
		this.nombreLignes = nombreLignes;
		this.montantTotal = montantTotal;
	}

	public Integer getAchatId() { return achatId; }
	public LocalDate getDateAchat() { return dateAchat; }
	public String getDateAffichee() { return dateAchat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
	public String getNumeroFacture() { return numeroFacture; }
	public String getFournisseurNom() { return fournisseurNom; }
	public Long getNombreLignes() { return nombreLignes; }
	public BigDecimal getMontantTotal() { return montantTotal; }
}
