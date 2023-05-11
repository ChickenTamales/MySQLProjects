package recipes;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.dao.DbConnection;
import recipes.entity.Recipe;
import recipes.exception.DbException;
import recipes.service.RecipeService;

public class Recipes {
	private Scanner scanner = new Scanner(System.in);// instead of console output, it's console input from user
	private RecipeService recipeService = new RecipeService();
//@formatter:off
 private List<String> operations = List.of(
		 "1) Create and populate all tables",
		 "2) Add a recipe"
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
				case 1:
					createTables();
					break;

				case 2:
					addRecipe();
					break;

				default:
					System.out.println("\n" + operation + " is not valid. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}

	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name");// data type and name = method("prompt")
		String notes = getStringInput("Enter recipe notes");
		Integer numServings = getIntInput("Enter number of servings");
		Integer prepMinutes = getIntInput("Enter prep time in minutes");
		Integer cookMinutes = getIntInput("Enter cook time minutes");

		LocalTime prepTime = minutesToLocalTime(prepMinutes);// turns prepMinutes into local time
		LocalTime cookTime = minutesToLocalTime(cookMinutes);

		Recipe recipe = new Recipe();

		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);

		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe:\n" + dbRecipe);
	}

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