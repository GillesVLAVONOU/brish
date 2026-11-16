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

@Entity
@Table(name = "ventes")
public class Vente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_vente")
	private Integer id;

	@Column(name = "date_vente", nullable = false)
	private LocalDate dateVente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utilisateur")
	private Utilisateur utilisateur;

	@OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LigneVente> lignes = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public LocalDate getDateVente() { return dateVente; }
	public void setDateVente(LocalDate dateVente) { this.dateVente = dateVente; }
	public Utilisateur getUtilisateur() { return utilisateur; }
	public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
	public List<LigneVente> getLignes() { return lignes; }
}
