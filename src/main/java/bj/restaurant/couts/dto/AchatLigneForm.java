package bj.restaurant.couts.dto;

public class AchatLigneForm {
	private String ingredientId = "";
	private String quantite = "";
	private String unite = "";
	private String prixUnitaire = "";

	public String getIngredientId() { return ingredientId; }
	public void setIngredientId(String ingredientId) { this.ingredientId = ingredientId; }
	public String getQuantite() { return quantite; }
	public void setQuantite(String quantite) { this.quantite = quantite; }
	public String getUnite() { return unite; }
	public void setUnite(String unite) { this.unite = unite; }
	public String getPrixUnitaire() { return prixUnitaire; }
	public void setPrixUnitaire(String prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}
