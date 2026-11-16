package bj.restaurant.couts.dto;

public class ProductionConsommationForm {
	private Integer ingredientId;
	private String ingredientNom = "";
	private String unite = "";
	private String quantiteRecette = "";
	private String quantiteReelle = "";

	public Integer getIngredientId() { return ingredientId; }
	public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }
	public String getIngredientNom() { return ingredientNom; }
	public void setIngredientNom(String ingredientNom) { this.ingredientNom = ingredientNom; }
	public String getUnite() { return unite; }
	public void setUnite(String unite) { this.unite = unite; }
	public String getQuantiteRecette() { return quantiteRecette; }
	public void setQuantiteRecette(String quantiteRecette) { this.quantiteRecette = quantiteRecette; }
	public String getQuantiteReelle() { return quantiteReelle; }
	public void setQuantiteReelle(String quantiteReelle) { this.quantiteReelle = quantiteReelle; }
}
