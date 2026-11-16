/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bj.restaurant.couts.config;

import bj.restaurant.couts.Entities.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author Dell
 */
@Entity
@Table(name = "autorisations_utilisateur")
@NamedQueries({
    @NamedQuery(name = "AutorisationsUtilisateur.findAll", query = "SELECT a FROM AutorisationsUtilisateur a"),
    @NamedQuery(name = "AutorisationsUtilisateur.findByIdUtilisateur", query = "SELECT a FROM AutorisationsUtilisateur a WHERE a.autorisationsUtilisateurPK.idUtilisateur = :idUtilisateur"),
    @NamedQuery(name = "AutorisationsUtilisateur.findByModule", query = "SELECT a FROM AutorisationsUtilisateur a WHERE a.autorisationsUtilisateurPK.module = :module"),
    @NamedQuery(name = "AutorisationsUtilisateur.findByPeutConsulter", query = "SELECT a FROM AutorisationsUtilisateur a WHERE a.peutConsulter = :peutConsulter"),
    @NamedQuery(name = "AutorisationsUtilisateur.findByPeutModifier", query = "SELECT a FROM AutorisationsUtilisateur a WHERE a.peutModifier = :peutModifier")})
public class AutorisationsUtilisateur implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AutorisationsUtilisateurPK autorisationsUtilisateurPK;
    @Column(name = "peut_consulter")
    private Boolean peutConsulter;
    @Column(name = "peut_modifier")
    private Boolean peutModifier;
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Utilisateur utilisateur;

    public AutorisationsUtilisateur() {
    }

    public AutorisationsUtilisateur(AutorisationsUtilisateurPK autorisationsUtilisateurPK) {
        this.autorisationsUtilisateurPK = autorisationsUtilisateurPK;
    }

    public AutorisationsUtilisateur(int idUtilisateur, String module) {
        this.autorisationsUtilisateurPK = new AutorisationsUtilisateurPK(idUtilisateur, module);
    }

    public AutorisationsUtilisateurPK getAutorisationsUtilisateurPK() {
        return autorisationsUtilisateurPK;
    }

    public void setAutorisationsUtilisateurPK(AutorisationsUtilisateurPK autorisationsUtilisateurPK) {
        this.autorisationsUtilisateurPK = autorisationsUtilisateurPK;
    }

    public Boolean getPeutConsulter() {
        return peutConsulter;
    }

    public void setPeutConsulter(Boolean peutConsulter) {
        this.peutConsulter = peutConsulter;
    }

    public Boolean getPeutModifier() {
        return peutModifier;
    }

    public void setPeutModifier(Boolean peutModifier) {
        this.peutModifier = peutModifier;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (autorisationsUtilisateurPK != null ? autorisationsUtilisateurPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutorisationsUtilisateur)) {
            return false;
        }
        AutorisationsUtilisateur other = (AutorisationsUtilisateur) object;
        if ((this.autorisationsUtilisateurPK == null && other.autorisationsUtilisateurPK != null) || (this.autorisationsUtilisateurPK != null && !this.autorisationsUtilisateurPK.equals(other.autorisationsUtilisateurPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bj.restaurant.couts.config.AutorisationsUtilisateur[ autorisationsUtilisateurPK=" + autorisationsUtilisateurPK + " ]";
    }
    
}
