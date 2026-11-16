package bj.restaurant.couts.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_fournisseur")
	private Integer id;

	@Column(nullable = false, length = 150)
	private String nom;

	@Column(length = 30)
	private String telephone;

	@Column(unique = true, length = 150)
	private String email;

	@Column(length = 255)
	private String adresse;

	@OneToMany(mappedBy = "fournisseur")
	private List<Achat> achats = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public String getTelephone() { return telephone; }
	public void setTelephone(String telephone) { this.telephone = telephone; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getAdresse() { return adresse; }
	public void setAdresse(String adresse) { this.adresse = adresse; }
	public List<Achat> getAchats() { return achats; }

}