package bj.restaurant.couts.model;

public enum Role {
    ADMINISTRATEUR,
    GESTIONNAIRE,
    CUISINIER;

    public String getLibelle() {
        return switch (this) {
            case ADMINISTRATEUR -> "Administrateur";
            case GESTIONNAIRE -> "Gestionnaire";
            case CUISINIER -> "Cuisinier";
        };
    }
}