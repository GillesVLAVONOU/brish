package bj.restaurant.couts.Entities;

import bj.restaurant.couts.model.Role;

import bj.restaurant.couts.config.AutorisationsUtilisateur;
import bj.restaurant.couts.config.ReinitialisationsMotDePasse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUtilisateur")
    private Collection<ReinitialisationsMotDePasse> reinitialisationsMotDePasseCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "utilisateur")
    private Collection<AutorisationsUtilisateur> autorisationsUtilisateurCollection;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_utilisateur")
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private Role role = Role.GESTIONNAIRE;

	@Column(nullable = false, length = 100)
	private String nom;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(name = "mot_de_passe_hash", nullable = false, length = 255)
	private String motDePasseHash;

	@Column(nullable = false)
	private boolean actif = true;

	@OneToMany(mappedBy = "utilisateur")
	private List<Production> productions = new ArrayList<>();

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Role getRole() { return role; }
	public void setRole(Role role) { this.role = role; }
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getMotDePasseHash() { return motDePasseHash; }
	public void setMotDePasseHash(String motDePasseHash) { this.motDePasseHash = motDePasseHash; }
	public boolean isActif() { return actif; }
	public void setActif(boolean actif) { this.actif = actif; }
	public List<Production> getProductions() { return productions; }

    public Collection<ReinitialisationsMotDePasse> getReinitialisationsMotDePasseCollection() {
        return reinitialisationsMotDePasseCollection;
    }

    public void setReinitialisationsMotDePasseCollection(Collection<ReinitialisationsMotDePasse> reinitialisationsMotDePasseCollection) {
        this.reinitialisationsMotDePasseCollection = reinitialisationsMotDePasseCollection;
    }

    public Collection<AutorisationsUtilisateur> getAutorisationsUtilisateurCollection() {
        return autorisationsUtilisateurCollection;
    }

    public void setAutorisationsUtilisateurCollection(Collection<AutorisationsUtilisateur> autorisationsUtilisateurCollection) {
        this.autorisationsUtilisateurCollection = autorisationsUtilisateurCollection;
    }
}