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
@Table(name = "mets")
public class Met {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_met")
	private Integer id;

	@Column(nullable = false, length = 120)
	private String nom;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "prix_vente_actuel", nullable = false, precision = 10, scale = 2)
	private BigDecimal prixVenteActuel;

	@Column(nullable = false)
	private boolean actif = true;

	@OneToMany(mappedBy = "met")
	private List<Recette> recettes = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public BigDecimal getPrixVenteActuel() { return prixVenteActuel; }
	public void setPrixVenteActuel(BigDecimal prixVenteActuel) { this.prixVenteActuel = prixVenteActuel; }
	public boolean isActif() { return actif; }
	public void setActif(boolean actif) { this.actif = actif; }
	public List<Recette> getRecettes() { return recettes; }
	public Recette getRecetteActive() {
		return recettes.stream().filter(Recette::isActive).findFirst().orElse(null);
	}

}