package recipes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import recipes.entity.Recipe;
import recipes.exception.DbException;
import provided.util.DaoBase;
import java.sql.Statement;
import java.time.LocalTime;

public class RecipeDao extends DaoBase {// tables
	private static final String CATEGORY_TABLE = "category";
	private static final String INGREDIENT_TABLE = "ingredient";
	private static final String RECIPE_TABLE = "recipe";
	private static final String RECIPE_CATEGORY = "recipe_category";
	private static final String STEP_TABLE = "step";
	private static final String UNIT_TABLE = "unit";

	public Recipe insertRecipe(Recipe recipe) {// workflow: obtain connection, create prepared statement, set
												// parameters, call execute update method on the prepared statement
		//@formatter:off
		String sql = ""
				
			+ "INSERT INTO " + RECIPE_TABLE + " "
			+ "(recipe_name, notes, num_servings, prep_time, cook_time) "
			+ "VALUES "
			+ "(?, ?, ?, ?, ?)"; 
			
		//@formatter:on
		
		try(Connection conn = DbConnection.getConnection()){//try with resources
			startTransaction(conn);
			
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, recipe.getRecipeName(), String.class);
			setParameter(stmt, 2, recipe.getNotes(), String.class);
			setParameter(stmt, 3, recipe.getNumServings(), Integer.class);
			setParameter(stmt, 4, recipe.getPrepTime(), LocalTime.class);
			setParameter(stmt, 5, recipe.getCookTime(), LocalTime.class);
			
			stmt.executeUpdate();
			//allow us to get the generated recipe id
			Integer recipeId = getLastInsertId(conn, RECIPE_TABLE);//look at this method later, sends a query to MySQL to get the last insert id/autocommand value
			
			commitTransaction(conn);
			recipe.setRecipeId(recipeId);
			return recipe;
		}
		catch(Exception e) {
			rollbackTransaction(conn);
			throw new DbException(e);
		}
		} catch (SQLException e) {
			throw new DbException(e);
		}
			

	}

	public void executeBatch(List<String> sqlBatch) {
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (Statement stmt = conn.createStatement()) {
				for (String sql : sqlBatch) {
					stmt.addBatch(sql);
				}
				stmt.executeBatch();
				commitTransaction(conn);
			} catch (Exception e) {// rollback inside the inner catch block
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

}
