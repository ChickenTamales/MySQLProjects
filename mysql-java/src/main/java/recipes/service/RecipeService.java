package recipes.service;

import recipes.exception.DbException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import recipes.dao.RecipeDao;
import recipes.entity.Category;
import recipes.entity.Ingredient;
import recipes.entity.Recipe;
import recipes.entity.Step;
import recipes.entity.Unit;

public class RecipeService {
	private static final String SCHEMA_FILE = "recipe_schema.sql";// reads in the file recipe_schema.sql
	private static final String DATA_FILE = "recipe_data.sql";
	private RecipeDao recipeDao = new RecipeDao();// now we can access the Dao from the service

	public Recipe fetchRecipeById(Integer recipeId) {
		return recipeDao.fetchRecipeById(recipeId)
				.orElseThrow(() -> new NoSuchElementException(
						"Recipe with ID=" + recipeId + " does not exist. "));
	}

	public void createAndPopulateTables() {
		loadFromFile(SCHEMA_FILE);// it's going to try to load the schema file from the jar
		loadFromFile(DATA_FILE);
	}

	private void loadFromFile(String fileName) {
		String content = readFileContent(fileName);// read the file content, then convert
		List<String> sqlStatements = convertContentToSqlStatements(content);// convert to sql statements

		recipeDao.executeBatch(sqlStatements);
	}

	private List<String> convertContentToSqlStatements(String content) {
		content = removeComments(content);
		content = replaceWhiteSpaceSquencesWithSinglSpaces(content);

		return extractLinesFromContent(content);

	}

	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<>();

		while (!content.isEmpty()) {// while the String is not empty
			int semicolon = content.indexOf(";");

			if (semicolon == -1) {
				if (!content.isBlank()) {// if the content isn't blank
					lines.add(content);

				}
				content = "";// will cause us to exit the loop
			} else {
				lines.add(content.substring(0, semicolon).trim());
				content = content.substring(semicolon + 1);
			}
		}

		return lines;
	}

	private String replaceWhiteSpaceSquencesWithSinglSpaces(String content) {

		return content.replaceAll("\\s+", " ");
	}

	private String removeComments(String content) {
		StringBuilder builder = new StringBuilder(content);
		int commentPos = 0;// counter to loop through the content, comment to comment, keeping track of
							// position

		while ((commentPos = builder.indexOf("-- ", commentPos)) != -1) { // will return either a position or -1
			int eolPos = builder.indexOf("\n", commentPos + 1);

			if (eolPos == -1) {
				builder.replace(commentPos, builder.length(), "");// at commentPos, take builder.length(), replace with
																	// nothing
			} else {
				builder.replace(commentPos, eolPos + 1, "");// replace comment with nothing
			}
		}

		return builder.toString();
	}

	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());//
			return Files.readString(path);
		} catch (Exception e) {
			throw new DbException(e);

		} // will return the recipe Service class

	}

	public Recipe addRecipe(Recipe recipe) {

		return recipeDao.insertRecipe(recipe);
	}

	public List<Recipe> fetchRecipes() {
		return recipeDao.fetchAllRecipes();
	}

	public List<Unit> fetchUnits() {
		return recipeDao.fetchAllUnits();
	}

	public void addIngredient(Ingredient ingredient) {
		recipeDao.addIngredientToRecipe(ingredient);
		
	}

	public void addStep(Step step) {
		recipeDao.addStepToRecipe(step);
		
	}

	public List<Category> fetchCategories() {
		return recipeDao.fetchAllCategories();
	}

	public void addCategoryToRecipe(Integer recipeId, String category) {
		recipeDao.addCategoryToRecipe(recipeId, category);		
	}

	public List<Step> fetchSteps(Integer recipeId) {
		return recipeDao.fetchRecipeSteps(recipeId);
	}

	public void modifyStep(Step step) {
		if(!recipeDao.modifyRecipeStep(step)) {
			throw new DbException("Step with ID=" + step.getStepId() + " does not exist.");
		}
	}

	public void deleteRecipe(Integer recipeId) {
		if(!recipeDao.deleteRecipe(recipeId)) {
			throw new DbException("Recipe with ID=" + recipeId + " does not exist.");
		}
		
	}

}
