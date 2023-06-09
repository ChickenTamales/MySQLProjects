package recipes;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.dao.DbConnection;
import recipes.entity.Category;
import recipes.entity.Ingredient;
import recipes.entity.Recipe;
import recipes.entity.Unit;
import recipes.entity.Step;
import recipes.exception.DbException;
import recipes.service.RecipeService;

public class Recipes {
	private Scanner scanner = new Scanner(System.in);// instead of console output, it's console input from user
	private Recipe curRecipe;
	private RecipeService recipeService = new RecipeService();

//@formatter:off
 private List<String> operations = List.of(
		 "1) Create and populate all tables",
		 "2) Add a recipe",
		 "3) List recipes",
		 "4) Select current recipe",
		 "5) Add ingredient to current recipe",
		 "6) Add step to current recipe",
		 "7) Add category to current recipe",
		 "8) Modify step in current recipe",
		 "9) Delete recipe"
		 );
//@formatter:on

	public static void main(String[] args) {
		new Recipes().displayMenu();

	}

	private void displayMenu() {
		boolean done = false;

		while (!done) {

			try {
				int operation = getOperation();

				switch (operation) {
				case -1:
					done = exitMenu();
					break;// will return true which indicates that we're done

				/*
				 * Operation "1)Create and populate all tables" will do the following:
				 */
				case 1:
					createTables();
					break;

				/*
				 * Operation "2) Add a recipe" will do the following:
				 */
				case 2:
					addRecipe();
					break;

				/*
				 * Operation "3) List recipes" will do the following:
				 */
				case 3:
					listRecipes();
					break;

				/*
				 * Operation "4) Select working recipe" will do the following:
				 */
				case 4:
					setCurrentRecipe();
					break;
					
				case 5:
					addIngredientToCurrentRecipe();
						break;
						
				case 6:
					addStepToCurrentRecipe();
					break;
					
				case 7:
					addCategoryToCurrentRecipe();
					break;
					
				case 8:
					modifyStepInCurrentRecipe();
					break;
					
				case 9:
					deleteRecipe();
					break;
					

				default:
					System.out.println("\n" + operation 
							+ " is not valid. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}
	}

	private void deleteRecipe() {
		listRecipes();
		
		Integer recipeId = getIntInput("Enter recipe ID to be deleted");
		
		if(Objects.nonNull(recipeId)) {
			recipeService.deleteRecipe(recipeId);
			
			if(Objects.nonNull(curRecipe) && curRecipe.getRecipeId().equals(recipeId)){
				curRecipe = null;
			}
		}
	}

	private void modifyStepInCurrentRecipe() {
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe");
			return;
		}
		List<Step> steps = recipeService.fetchSteps(curRecipe.getRecipeId());
		
		System.out.println("\nSteps for current recipe");
		steps.forEach(step -> System.out.println("   " + step));
		
		Integer stepId = getIntInput("Enter step ID of step to modify");
		
		if(Objects.nonNull(stepId)) {
			String stepText = getStringInput("Enter new step text");
			
			if(Objects.nonNull(stepText)) {
				Step step = new Step();
				
				step.setStepId(stepId);
				step.setStepText(stepText);
				
				recipeService.modifyStep(step);
				curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipeId());
			}
		}
	}

	private void addCategoryToCurrentRecipe() {
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe");
		return;
		}
		
		List<Category> categories = recipeService.fetchCategories();
		
		categories.forEach(
				category -> System.out.println("   " + category.getCategoryName()));
		
		String category = getStringInput("Enter the category to add");
		
		if(Objects.nonNull(category)) {
			recipeService.addCategoryToRecipe(curRecipe.getRecipeId(), category);
			
			curRecipe = recipeService.fetchRecipeById(curRecipe.getRecipeId());
		}
	}

	private void addStepToCurrentRecipe() {
		//check to see if curRecipe is null
		if(Objects.isNull(curRecipe)) {
			//if curRecipe is null, ask user to select a recipe
			System.out.println("\nPlease select a recipe");
			return;
		}
		String stepText = getStringInput("Enter the step text");
		
		if(Objects.nonNull(stepText)) {
			Step step = new Step();
			
			step.setRecipeId(curRecipe.getRecipeId());
			step.setStepText(stepText);
			
			recipeService.addStep(step);
			curRecipe = recipeService.fetchRecipeById(step.getRecipeId());
		}
	}

	private void addIngredientToCurrentRecipe() {
		//what if there is no current recipe?
		if(Objects.isNull(curRecipe)) {
			System.out.println("\nPlease select a recipe");
			return;
		}
		//now that a recipe has been selected
		String name = getStringInput("Enter the ingredient name");
		String instruction = getStringInput("Enter any instructions: (like \"chopped\")");
		Double inputAmount = getDoubleInput("Enter amount of ingredient ( like .25)");
		List<Unit> units = recipeService.fetchUnits();
		
		BigDecimal amount = 
				Objects.isNull(inputAmount) ? null 
						: new BigDecimal(inputAmount).setScale(2);
		
		System.out.println("Units:");
		units.forEach(unit -> System.out.println("   " + unit.getUnitId() + ": " 
		+ unit.getUnitNameSingular() + "(" + unit.getUnitNamePlural() + ")" ));
		
		Integer unitId = getIntInput("Enter a unit ID (press Enter for none)");
		
		Unit unit = new Unit();
		unit.setUnitId(unitId);
		
		Ingredient ingredient = new Ingredient();
		ingredient.setRecipeId(curRecipe.getRecipeId());
		ingredient.setUnit(unit);
		ingredient.setIngredientName(name);
		ingredient.setInstruction(instruction);
		ingredient.setAmount(amount);
		
		recipeService.addIngredient(ingredient);
		curRecipe = recipeService.fetchRecipeById(ingredient.getRecipeId());
	}

	private void setCurrentRecipe() {
		// show recipes first
		List<Recipe> recipes = listRecipes();

		Integer recipeId = getIntInput("Select a recipe ID");

		curRecipe = null;
		for (Recipe recipe : recipes) {
			if (recipe.getRecipeId().equals(recipeId)) {
				curRecipe = recipeService.fetchRecipeById(recipeId);
				break;
			}
		}
		// if we don't find a match
		if (Objects.isNull(curRecipe)) {
			System.out.println("\nInvalid recipe selected.");
		}

	}

	/**
	 * Fetch the list of recipes, print the recipe IDs and names on the console, and
	 * return the list.
	 * 
	 * @return The list of recipes
	 */
	private List<Recipe> listRecipes() {
		List<Recipe> recipes = recipeService.fetchRecipes();

		System.out.println("\nRecipes:");

		/* Print the list of recipes using a Lambda expression. */
		recipes.forEach(recipe -> System.out.println("    " + recipe.getRecipeId() + ": " + recipe.getRecipeName()));

		/* This will print the list of recipes using an enhances for loop. */
// for (Recipe recipe : recipes) {
// System.out.println(
// " " + recipe.getRecipeId() + ": " + recipe.getRecipeName());
// }

		return recipes;
	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name");// data type and name = method("prompt")
		String notes = getStringInput("Enter recipe notes");
		Integer numServings = getIntInput("Enter number of servings");
		Integer prepMinutes = getIntInput("Enter prep time in minutes");
		Integer cookMinutes = getIntInput("Enter cook time minutes");

		LocalTime prepTime = minutesToLocalTime(prepMinutes);// turns prepMinutes into local time
		LocalTime cookTime = minutesToLocalTime(cookMinutes);

		/* Create a recipe object from the user input. */
		Recipe recipe = new Recipe();

		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);

		/*
		 * Add the recipe to the recipe table. This will throw an unchecked exception if
		 * there is an error. The exception is picked up by the exception handler in
		 * displayMenu(). This keeps the code very clean and readable.
		 */
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe:\n" + dbRecipe);

		curRecipe = recipeService.fetchRecipeById(dbRecipe.getRecipeId());
	}

	/**
	 * Convert from an Integer minute value to a LocalTime object.
	 * 
	 * @param numMinutes The number of minutes. This may be {@code null}, in which
	 *                   case the number of minutes is set to zero.
	 * @return A LocalTime object containing the number of hours and minutes.
	 */
	private LocalTime minutesToLocalTime(Integer numMinutes) {
		int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
		int hours = min / 60;
		int minutes = min % 60;

		return LocalTime.of(hours, minutes);
	}

	private void createTables() {// will create tables for us or throw an exception
		recipeService.createAndPopulateTables();
		System.out.println("\nTables created and populated!");

	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. TTFN!");
		return true;
	}

	private int getOperation() {
		printOperations();

		Integer op = getIntInput("\nEnter an operation number (press Enter to quit)");// can return null if it's not a
																						// valid number
		return Objects.isNull(op) ? -1 : op;// if it does return null it will return -1
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");

		operations.forEach(op -> System.out.println("   " + op));

		if (Objects.isNull(curRecipe)) {
			System.out.println("\nYou are not working with a recipe.");
		} else {
			System.out.println("\nYou are working with recipe " + curRecipe);
		}

	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);// call getStringInput

		if (Objects.isNull(input)) {// if input is null it returns null
			return null;
		}
		try {
			return Integer.parseInt(input);// otherwise try to do a partseInt
		} catch (NumberFormatException e) {
			throw new DbException(input + "is not a valid number.");// if throws exception, reformatted, throw new
																	// exception
		}
	}

	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + "is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();// How to get input from the scanner

		return line.isBlank() ? null : line.trim();// return null if the next line is blank
	}
}