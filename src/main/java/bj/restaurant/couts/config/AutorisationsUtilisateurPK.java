/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bj.restaurant.couts.config;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author Dell
 */
@Embeddable
public class AutorisationsUtilisateurPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_utilisateur")
    private int idUtilisateur;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "module")
    private String module;

    public AutorisationsUtilisateurPK() {
    }

    public AutorisationsUtilisateurPK(int idUtilisateur, String module) {
        this.idUtilisateur = idUtilisateur;
        this.module = module;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idUtilisateur;
        hash += (module != null ? module.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutorisationsUtilisateurPK)) {
            return false;
        }
        AutorisationsUtilisateurPK other = (AutorisationsUtilisateurPK) object;
        if (this.idUtilisateur != other.idUtilisateur) {
            return false;
        }
        if ((this.module == null && other.module != null) || (this.module != null && !this.module.equals(other.module))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bj.restaurant.couts.config.AutorisationsUtilisateurPK[ idUtilisateur=" + idUtilisateur + ", module=" + module + " ]";
    }
    
}
