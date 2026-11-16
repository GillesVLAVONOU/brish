/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bj.restaurant.couts.config;

import bj.restaurant.couts.Entities.Utilisateur;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Dell
 */
@Entity
@Table(name = "reinitialisations_mot_de_passe")
@NamedQueries({
    @NamedQuery(name = "ReinitialisationsMotDePasse.findAll", query = "SELECT r FROM ReinitialisationsMotDePasse r"),
    @NamedQuery(name = "ReinitialisationsMotDePasse.findByIdReinitialisation", query = "SELECT r FROM ReinitialisationsMotDePasse r WHERE r.idReinitialisation = :idReinitialisation"),
    @NamedQuery(name = "ReinitialisationsMotDePasse.findByJetonHash", query = "SELECT r FROM ReinitialisationsMotDePasse r WHERE r.jetonHash = :jetonHash"),
    @NamedQuery(name = "ReinitialisationsMotDePasse.findByCreeLe", query = "SELECT r FROM ReinitialisationsMotDePasse r WHERE r.creeLe = :creeLe"),
    @NamedQuery(name = "ReinitialisationsMotDePasse.findByExpireLe", query = "SELECT r FROM ReinitialisationsMotDePasse r WHERE r.expireLe = :expireLe"),
    @NamedQuery(name = "ReinitialisationsMotDePasse.findByUtiliseLe", query = "SELECT r FROM ReinitialisationsMotDePasse r WHERE r.utiliseLe = :utiliseLe")})
public class ReinitialisationsMotDePasse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_reinitialisation")
    private Long idReinitialisation;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "jeton_hash")
    private String jetonHash;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cree_le")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creeLe;
    @Basic(optional = false)
    @NotNull
    @Column(name = "expire_le")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireLe;
    @Column(name = "utilise_le")
    @Temporal(TemporalType.TIMESTAMP)
    private Date utiliseLe;
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur")
    @ManyToOne(optional = false)
    private Utilisateur idUtilisateur;

    public ReinitialisationsMotDePasse() {
    }

    public ReinitialisationsMotDePasse(Long idReinitialisation) {
        this.idReinitialisation = idReinitialisation;
    }

    public ReinitialisationsMotDePasse(Long idReinitialisation, String jetonHash, Date creeLe, Date expireLe) {
        this.idReinitialisation = idReinitialisation;
        this.jetonHash = jetonHash;
        this.creeLe = creeLe;
        this.expireLe = expireLe;
    }

    public Long getIdReinitialisation() {
        return idReinitialisation;
    }

    public void setIdReinitialisation(Long idReinitialisation) {
        this.idReinitialisation = idReinitialisation;
    }

    public String getJetonHash() {
        return jetonHash;
    }

    public void setJetonHash(String jetonHash) {
        this.jetonHash = jetonHash;
    }

    public Date getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(Date creeLe) {
        this.creeLe = creeLe;
    }

    public Date getExpireLe() {
        return expireLe;
    }

    public void setExpireLe(Date expireLe) {
        this.expireLe = expireLe;
    }

    public Date getUtiliseLe() {
        return utiliseLe;
    }

    public void setUtiliseLe(Date utiliseLe) {
        this.utiliseLe = utiliseLe;
    }

    public Utilisateur getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Utilisateur idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idReinitialisation != null ? idReinitialisation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReinitialisationsMotDePasse)) {
            return false;
        }
        ReinitialisationsMotDePasse other = (ReinitialisationsMotDePasse) object;
        if ((this.idReinitialisation == null && other.idReinitialisation != null) || (this.idReinitialisation != null && !this.idReinitialisation.equals(other.idReinitialisation))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bj.restaurant.couts.config.ReinitialisationsMotDePasse[ idReinitialisation=" + idReinitialisation + " ]";
    }
    
}
