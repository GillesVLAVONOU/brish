package bj.restaurant.couts.security;

import java.io.Serializable;
import java.security.Principal;

public final class BrishRolePrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;

    public BrishRolePrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
