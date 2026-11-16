package bj.restaurant.couts.Entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "achats")
public class Achat {

	@Column(name = "date_achat", nullable = false)
	private LocalDate dateAchat;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_achat")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_fournisseur", nullable = false)
	private Fournisseur fournisseur;


	@Column(name = "numero_facture", nullable = false, unique = true, length = 80)
	private String numeroFacture;

	@OneToMany(mappedBy = "achat", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LigneAchat> lignes = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Fournisseur getFournisseur() { return fournisseur; }
	public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }
	public LocalDate getDateAchat() { return dateAchat; }
	public void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }
	public String getNumeroFacture() { return numeroFacture; }
	public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }
	public List<LigneAchat> getLignes() { return lignes; }

	public BigDecimal getMontantTotal() {
		return lignes.stream()
				.map(ligne -> ligne.getQuantite().multiply(ligne.getPrixUnitaire()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}