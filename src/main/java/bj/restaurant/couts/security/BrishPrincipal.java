package bj.restaurant.couts.security;

import java.io.Serializable;
import java.security.Principal;

public final class BrishPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String nom;
    private final String email;
    private final String role;

    public BrishPrincipal(Long id, String nom, String email, String role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
    }

    @Override
    public String getName() {
        return email;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
