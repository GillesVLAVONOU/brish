package bj.restaurant.couts.Entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingredients")
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ingredient")
	private Integer id;

	@Column(nullable = false, unique = true, length = 120)
	private String nom;

	@Column(name = "unite_base", nullable = false, length = 20)
	private String uniteBase;

	@Column(name = "seuil_alerte", precision = 10, scale = 3)
	private BigDecimal seuilAlerte;

	@Column(nullable = false)
	private boolean actif = true;

	@OneToMany(mappedBy = "ingredient")
	private List<LigneRecette> lignesRecette = new ArrayList<>();

	@OneToMany(mappedBy = "ingredient")
	private List<LigneAchat> lignesAchat = new ArrayList<>();

	@OneToMany(mappedBy = "ingredient")
	private List<LigneProduction> lignesProduction = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public String getUniteBase() { return uniteBase; }
	public void setUniteBase(String uniteBase) { this.uniteBase = uniteBase; }
	public BigDecimal getSeuilAlerte() { return seuilAlerte; }
	public void setSeuilAlerte(BigDecimal seuilAlerte) { this.seuilAlerte = seuilAlerte; }
	public boolean isActif() { return actif; }
	public void setActif(boolean actif) { this.actif = actif; }
	public List<LigneRecette> getLignesRecette() { return lignesRecette; }
	public List<LigneAchat> getLignesAchat() { return lignesAchat; }
	public List<LigneProduction> getLignesProduction() { return lignesProduction; }

}