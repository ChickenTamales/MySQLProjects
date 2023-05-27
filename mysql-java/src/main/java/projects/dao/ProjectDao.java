package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

/* create a ProjectDao class in the projects.dao package. Make sure that ProjectDao extends 
 * DaoBase from the provided.util package. */

@SuppressWarnings("unused")
public class ProjectDao extends DaoBase {// tables
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	/*
	 * Wave the mouse over insertProject() and select "Create method 'insertProject
	 * (Project)' in type *ProjectDao
	 */
	/*
	 * Write the SQL statement that will insert the values from the Project object
	 * passed to the insertProject() method. Add the fields project_name,
	 * estimated_hours, actual_hours, difficulty, and notes.
	 */
	/*
	 * Obtain a connection from DbConnection.getConnection(). Assign it a variable
	 * of type Connection named conn in a try-with-resource statement. Catch the
	 * SQLException in a catch block added to the try-with- resource. From within
	 * the catch block, throw a new DbException. The DbException constructor should
	 * take the SQLException object passed into the catch block.
	 */
	/*
	 * Start a transaction. Inside the try block, start a transaction by calling
	 * startTransaction() and passing in the Connection object.
	 */
	/* Pass the SQL statement as a parameter to conn.prepareStatement(). */
	/*
	 * Perform the insert by calling executeUpdate() on the PreparedStatement
	 * object.
	 */
	// allow us to get the generated project id

	/*
	 * Obtain the project ID (primary key) by calling the convenience method in
	 * DaoBase, getLastInsertId(). Pass the Connection object and the constant
	 * PROJECT_TABLE to getLastInsertId(). Assign the return value to an Integer
	 * variable named projectId.
	 */
	/*
	 * Commit the transaction by calling the convenience method in DaoBase,
	 * commitTransaction(). Pass the Connection object to commitTransaction() as a
	 * parameter.
	 */
	/*
	 * Set the projectId on the Project object that was passed into insertProject
	 * and return it.
	 */
	/*
	 * Add a catch block to the inner try block that catches Exception. In the catch
	 * block, roll back the transaction and throw a DbException initialized with the
	 * Exception object passed into the catch block.
	 */

	public Project insertProject(Project project) {// workflow: obtain connection, create prepared statement, //set
													// parameters, call execute update method on the prepared statement

//@formatter:off

String sql = ""

+ "INSERT INTO " + PROJECT_TABLE + " "
+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
+ "VALUES "
+ "(?, ?, ?, ?, ?)"; // ? used as placeholders

//@formatter:on

		try (Connection conn = DbConnection.getConnection()) {// try with resources

			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				stmt.executeUpdate();

				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);// look at this method later, sends
				// a query to MySQL to get the last insert id/auto command value

				commitTransaction(conn);

				project.setProjectId(projectId);
				return project;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	/*
	 * Have Eclipse create the method fetchAllProjects() in ProjectDao.java or
	 * create it yourself. It takes no parameters and returns a List of Projects.
	 *
	 * Write the SQL statement to return all projects not including materials,
	 * steps, or categories. Order the results by project name.
	 *
	 * Add a try-with-resource statement to obtain the Connection object.
	 *
	 * Inside the try block, start a new transaction.
	 *
	 * Add an inner try-with-resource statement to obtain the PreparedStatement from
	 * the Connection object.
	 *
	 * Inside the (currently) innermost try-with-resource statement, add a
	 * try-with-resource statement to obtain a ResultSet from the PreparedStatement.
	 * Include the import statement for ResultSet. It is in the java.sql package.
	 *
	 * Inside the new innermost try-with-resource, create and return a List of
	 * Projects.
	 *
	 * Loop through the result set. Create and assign each result row to a new
	 * Project object. Add the Project object to the List of Projects. You can do
	 * this by calling the extract method:
	 *
	 * Rollback the transaction and throw a new DbException, passing in the
	 * Exception object as the cause.
	 *
	 * Catch the SQLException in a catch block and rethrow a new DbException,
	 * passing in the SQLException object.
	 */

	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {

				try (ResultSet rs = stmt.executeQuery()) {

					List<Project> projects = new LinkedList<>();

					while (rs.next()) {

						projects.add(extract(rs, Project.class));
					}
					return projects;
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	/*
	 * In the method fetchProjectById():
	 * 
	 * Write the SQL statement to return all columns from the project table in the
	 * row that matches the given projectId. Make sure to use the parameter
	 * placeholder "?" in the SQL statement.
	 *
	 * Obtain a Connection object in a try-with-resource statement. Add the catch
	 * block to handle the SQLException. In the catch block throw a new DbException
	 * passing the SQLException object as a parameter.
	 * 
	 * Start a transaction inside the try-with-resource statement.
	 * 
	 * Below the method call to startTransaction(), add an inner try/catch. The
	 * catch block should handle Exception. Inside the catch block, rollback the
	 * transaction and throw a new DbException that takes the Exception object as a
	 * parameter.
	 * 
	 * Inside the try block, create a variable of type Project and set it to null.
	 * Return the Project object as an Optional object using Optional.ofNullable().
	 * Save the file. You should have no compilation errors at this point but you
	 * may see some warnings. This is OK.
	 * 
	 * Inside the inner try block, obtain a PreparedStatement from the Connection
	 * object in a try-with-resource statement. Pass the SQL statement in the method
	 * call to prepareStatement(). Add the projectId method parameter as a parameter
	 * to the PreparedStatement.
	 * 
	 * Obtain a ResultSet in a try-with-resource statement. If the ResultSet has a
	 * row in it (rs.next()) set the Project variable to a new Project object and
	 * set all fields from values in the ResultSet. You can call the extract()
	 * method for this.
	 * 
	 * Below the try-with-resource statement that obtains the PreparedStatement but
	 * inside the try block that manages the rollback, add three method calls to
	 * obtain the list of materials, steps, and categories. Since each method
	 * returns a List of the appropriate type, you can call addAll() to add the
	 * entire List to the List in the Project object:
	 * 
	 * Commit the transaction.
	 * 
	 */

	public Optional<Project> fetchProjectById(Integer projectId) {

		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try {
				Project project = null;

				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				commitTransaction(conn);
				return Optional.ofNullable(project);
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {

		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}

		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {

		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}

	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {

		// formatter: off

		String sql = "" + "SELECT c.* FROM " + CATEGORY_TABLE + " c " + "JOIN " + PROJECT_CATEGORY_TABLE
				+ " pc USING (category_id) " + "WHERE project_id = ?";

		// formatter: on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}

	}

	public boolean modifyProjectDetails(Project project) {
		/*
		 * / In modifyProjectDetails(), write the SQL statement to modify the project
		 * details. Do not update the project ID – it should be part of the WHERE
		 * clause. Remember to use question marks as parameter placeholders.
		 */
		// @formatter: off
		String sql = "" 
				+ "UPDATE " + PROJECT_TABLE + " SET " 
				+ "project_name = ?, " 
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, " 
				+ "difficulty = ?, " 
				+ "notes = ? " 
				+ "WHERE project_id = ?";
		// @formatter: on

		/*
		 * Obtain the Connection and PreparedStatement using the appropriate
		 * try-with-resource and catch blocks. Start and rollback a transaction as
		 * usual. Throw a DbException from each catch block.
		 */
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			/*
			 * Set all parameters on the PreparedStatement. Call executeUpdate() and check
			 * if the return value is 1. Save the result in a variable.
			 */
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);

				/*
				 * Call executeUpdate() and check if the return value is 1. Save the result in a
				 * variable. Commit the transaction and return the result from executeUpdate()
				 * as a boolean. At this point there should be no compilation errors.
				 */
				boolean modified = stmt.executeUpdate() == 1;
				commitTransaction(conn);

				return modified;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean deleted = stmt.executeUpdate() == 1;
				
				commitTransaction(conn);
				return deleted;				
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
		

	}
}