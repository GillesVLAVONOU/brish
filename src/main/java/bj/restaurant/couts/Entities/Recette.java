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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recettes", uniqueConstraints = @UniqueConstraint(columnNames = {"id_met", "version"}))
public class Recette {

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "rendement_portions", nullable = false)
    private Integer rendementPortions;

    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;

    @Column(nullable = false)
    private boolean active = true;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_recette")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_met", nullable = false)
	private Met met;

	@OneToMany(mappedBy = "recette", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LigneRecette> lignes = new ArrayList<>();

	@OneToMany(mappedBy = "recette")
	private List<Production> productions = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Met getMet() { return met; }
	public void setMet(Met met) { this.met = met; }
	public Integer getVersion() { return version; }
	public void setVersion(Integer version) { this.version = version; }
	public Integer getRendementPortions() { return rendementPortions; }
	public void setRendementPortions(Integer rendementPortions) { this.rendementPortions = rendementPortions; }
	public LocalDate getDateEffet() { return dateEffet; }
	public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	public List<LigneRecette> getLignes() { return lignes; }
	public List<Production> getProductions() { return productions; }

}