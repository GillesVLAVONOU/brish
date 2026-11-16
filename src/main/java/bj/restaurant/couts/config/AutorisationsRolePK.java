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
public class AutorisationsRolePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "role")
    private String role;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "module")
    private String module;

    public AutorisationsRolePK() {
    }

    public AutorisationsRolePK(String role, String module) {
        this.role = role;
        this.module = module;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
        hash += (role != null ? role.hashCode() : 0);
        hash += (module != null ? module.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutorisationsRolePK)) {
            return false;
        }
        AutorisationsRolePK other = (AutorisationsRolePK) object;
        if ((this.role == null && other.role != null) || (this.role != null && !this.role.equals(other.role))) {
            return false;
        }
        if ((this.module == null && other.module != null) || (this.module != null && !this.module.equals(other.module))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bj.restaurant.couts.config.AutorisationsRolePK[ role=" + role + ", module=" + module + " ]";
    }
    
}
