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
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productions")
public class Production {

	@Column(name = "date_production", nullable = false)
	private LocalDateTime dateProduction = LocalDateTime.now();

	@Column(name = "date_cloture")
	private LocalDate dateCloture;

	@Column(name = "portions_prevues", nullable = false)
	private Integer portionsPrevues;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_production")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_recette", nullable = false)
	private Recette recette;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utilisateur")
	private Utilisateur utilisateur;


	@Column(name = "portions_reelles")
	private Integer portionsReelles;

	@Column(nullable = false, length = 20)
	private String statut = "BROUILLON";

	@OneToMany(mappedBy = "production", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LigneProduction> lignes = new ArrayList<>();

	@OneToMany(mappedBy = "production", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChargeProduction> charges = new ArrayList<>();

	@OneToMany(mappedBy = "production")
	private List<HistoriqueCout> historiqueCouts = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Recette getRecette() { return recette; }
	public void setRecette(Recette recette) { this.recette = recette; }
	public Utilisateur getUtilisateur() { return utilisateur; }
	public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
	public LocalDateTime getDateProduction() { return dateProduction; }
	public void setDateProduction(LocalDateTime dateProduction) { this.dateProduction = dateProduction; }
	public LocalDate getDateCloture() { return dateCloture; }
	public void setDateCloture(LocalDate dateCloture) { this.dateCloture = dateCloture; }
	public Integer getPortionsPrevues() { return portionsPrevues; }
	public void setPortionsPrevues(Integer portionsPrevues) { this.portionsPrevues = portionsPrevues; }
	public Integer getPortionsReelles() { return portionsReelles; }
	public void setPortionsReelles(Integer portionsReelles) { this.portionsReelles = portionsReelles; }
	public String getStatut() { return statut; }
	public void setStatut(String statut) { this.statut = statut; }
	public boolean isBrouillon() { return "BROUILLON".equals(statut); }
	public boolean isTerminee() { return "TERMINEE".equals(statut); }
	public List<LigneProduction> getLignes() { return lignes; }
	public List<ChargeProduction> getCharges() { return charges; }
	public List<HistoriqueCout> getHistoriqueCouts() { return historiqueCouts; }

}