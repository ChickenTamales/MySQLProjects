package recipes.entity;

import java.math.BigDecimal;
import java.util.Objects;

import provided.entity.EntityBase;

public class Ingredient extends EntityBase {//created from recipe_schema.sql
	private Integer ingredientId;//using Integer instead of int because the values might be null
	private Integer recipeId;
	private Unit unit;//created new class/object called unit, used the variables in the recipe_schema
	private String ingredientName;
	private String instruction;
	private Integer ingredientOrder;
	private BigDecimal amount;//BigDecimal instead of 
	public Integer getIngredientId() {
		return ingredientId;
	}
	public void setIngredientId(Integer ingredientId) {
		this.ingredientId = ingredientId;
	}
	public Integer getRecipeId() {
		return recipeId;
	}
	public void setRecipeId(Integer recipeId) {
		this.recipeId = recipeId;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public Integer getIngredientOrder() {
		return ingredientOrder;
	}
	public void setIngredientOrder(Integer ingredientOrder) {
		this.ingredientOrder = ingredientOrder;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	/*
	 * print like: ID=5: 1/4 cup carrots, thinly sliced.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();//StringBuilder used when the String needs to be dynamic and change a lot
		
		b.append("ID=").append(ingredientId).append(": ");
		b.append(toFraction(amount));//will convert a decimal value to a fraction which is more suitable for recipes
		if(Objects.nonNull(unit) && Objects.nonNull(unit.getUnitId())) {
			String singular = unit.getUnitNameSingular();
			String plural = unit.getUnitNamePlural();
			String word = amount.compareTo(BigDecimal.ONE) > 0 ? plural : singular;// if is greater than 1, will select plural, if not, select singular
			
			b.append(word).append(" ");
		}
		
		b.append(ingredientName);
		
		if(Objects.nonNull(instruction)) {
			b.append(", ").append(instruction);
		}
		return b.toString();
	}

}
