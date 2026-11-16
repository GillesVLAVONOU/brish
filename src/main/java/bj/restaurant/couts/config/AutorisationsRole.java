/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bj.restaurant.couts.config;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author Dell
 */
@Entity
@Table(name = "autorisations_role")
@NamedQueries({
    @NamedQuery(name = "AutorisationsRole.findAll", query = "SELECT a FROM AutorisationsRole a"),
    @NamedQuery(name = "AutorisationsRole.findByRole", query = "SELECT a FROM AutorisationsRole a WHERE a.autorisationsRolePK.role = :role"),
    @NamedQuery(name = "AutorisationsRole.findByModule", query = "SELECT a FROM AutorisationsRole a WHERE a.autorisationsRolePK.module = :module"),
    @NamedQuery(name = "AutorisationsRole.findByPeutConsulter", query = "SELECT a FROM AutorisationsRole a WHERE a.peutConsulter = :peutConsulter"),
    @NamedQuery(name = "AutorisationsRole.findByPeutModifier", query = "SELECT a FROM AutorisationsRole a WHERE a.peutModifier = :peutModifier")})
public class AutorisationsRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AutorisationsRolePK autorisationsRolePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "peut_consulter")
    private boolean peutConsulter;
    @Basic(optional = false)
    @NotNull
    @Column(name = "peut_modifier")
    private boolean peutModifier;

    public AutorisationsRole() {
    }

    public AutorisationsRole(AutorisationsRolePK autorisationsRolePK) {
        this.autorisationsRolePK = autorisationsRolePK;
    }

    public AutorisationsRole(AutorisationsRolePK autorisationsRolePK, boolean peutConsulter, boolean peutModifier) {
        this.autorisationsRolePK = autorisationsRolePK;
        this.peutConsulter = peutConsulter;
        this.peutModifier = peutModifier;
    }

    public AutorisationsRole(String role, String module) {
        this.autorisationsRolePK = new AutorisationsRolePK(role, module);
    }

    public AutorisationsRolePK getAutorisationsRolePK() {
        return autorisationsRolePK;
    }

    public void setAutorisationsRolePK(AutorisationsRolePK autorisationsRolePK) {
        this.autorisationsRolePK = autorisationsRolePK;
    }

    public boolean getPeutConsulter() {
        return peutConsulter;
    }

    public void setPeutConsulter(boolean peutConsulter) {
        this.peutConsulter = peutConsulter;
    }

    public boolean getPeutModifier() {
        return peutModifier;
    }

    public void setPeutModifier(boolean peutModifier) {
        this.peutModifier = peutModifier;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (autorisationsRolePK != null ? autorisationsRolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutorisationsRole)) {
            return false;
        }
        AutorisationsRole other = (AutorisationsRole) object;
        if ((this.autorisationsRolePK == null && other.autorisationsRolePK != null) || (this.autorisationsRolePK != null && !this.autorisationsRolePK.equals(other.autorisationsRolePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bj.restaurant.couts.config.AutorisationsRole[ autorisationsRolePK=" + autorisationsRolePK + " ]";
    }
    
}
