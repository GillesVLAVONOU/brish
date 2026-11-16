package bj.restaurant.couts.model;

import java.util.Arrays;
import java.util.Optional;

public enum ModuleApplication {
	DASHBOARD("Tableau de bord", "/dashboard", "/views/dashboard.jsp"),
	INGREDIENTS("Ingrédients", "/ingredients", "/views/ingredients/"),
	FOURNISSEURS("Fournisseurs", "/fournisseurs", "/views/fournisseurs/"),
	ACHATS("Achats", "/achats", "/views/achats/"),
	METS("Mets", "/mets", "/views/mets/"),
	RECETTES("Recettes et calcul des coûts", "/recettes", "/views/recettes/", "/couts/calcul", "/views/couts/"),
	PRODUCTIONS("Productions", "/productions", "/views/productions/"),
	VENTES("Ventes", "/ventes", "/views/ventes/"),
	RAPPORTS("Rapports", "/rapports", "/views/rapports/"),
	PARAMETRES("Paramètres", "/parametres", "/views/parametres.jsp");

	private final String libelle;
	private final String[] chemins;

	ModuleApplication(String libelle, String... chemins) {
		this.libelle = libelle;
		this.chemins = chemins;
	}

	public String getLibelle() {
		return libelle;
	}

	public String getCheminAccueil() {
		return chemins[0];
	}

	public static Optional<ModuleApplication> depuisChemin(String chemin) {
		return Arrays.stream(values())
				.filter(module -> Arrays.stream(module.chemins)
						.anyMatch(racine -> chemin.equals(racine)
								|| (racine.endsWith("/") && chemin.startsWith(racine))
								|| (!racine.endsWith("/") && chemin.startsWith(racine + "/"))))
				.findFirst();
	}

	public boolean consultationParDefaut(Role role) {
		return switch (role) {
			case ADMINISTRATEUR, GESTIONNAIRE -> true;
			case CUISINIER -> this == INGREDIENTS || this == METS || this == RECETTES || this == PRODUCTIONS;
		};
	}

	public boolean modificationParDefaut(Role role) {
		return switch (role) {
			case ADMINISTRATEUR, GESTIONNAIRE -> true;
			case CUISINIER -> this == PRODUCTIONS;
		};
	}
}
