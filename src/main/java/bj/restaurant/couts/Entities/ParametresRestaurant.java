package bj.restaurant.couts.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parametres_restaurant")
public class ParametresRestaurant {

	@Id
	@Column(name = "id_parametres")
	private Integer id = 1;

	@Column(name = "nom_restaurant", nullable = false, length = 120)
	private String nomRestaurant;

	@Column(length = 255)
	private String adresse;

	@Column(length = 30)
	private String telephone;

	@Column(name = "code_devise", nullable = false, length = 3)
	private String codeDevise = "XOF";

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public String getNomRestaurant() { return nomRestaurant; }
	public void setNomRestaurant(String nomRestaurant) { this.nomRestaurant = nomRestaurant; }
	public String getAdresse() { return adresse; }
	public void setAdresse(String adresse) { this.adresse = adresse; }
	public String getTelephone() { return telephone; }
	public void setTelephone(String telephone) { this.telephone = telephone; }
	public String getCodeDevise() { return codeDevise; }
	public void setCodeDevise(String codeDevise) { this.codeDevise = codeDevise; }

	public String getLibelleDevise() {
		return "XOF".equals(codeDevise) ? "FCFA" : codeDevise;
	}

}
