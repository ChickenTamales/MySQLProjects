package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

/*
 * 1. create a ProjectDao class in the projects.dao package. Make sure that ProjectDao extends 
 * 		DaoBase from the provided.util package.
 */
@SuppressWarnings("unused")
public class ProjectDao extends DaoBase {// tables
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY = "project_category";
	private static final String STEP_TABLE = "step";

/*
 * 3. Wave the mouse over insertProject() and select "Create method 'insertProject (Project)' in type      *ProjectDao
 */

	public Project insertProject(Project project) {// workflow: obtain connection, create prepared statement, //set parameters, call execute update method on the prepared statement

/*
 * 1. Write the SQL statement that will insert the values from the Project object passed to the 
 * 		insertProject() method. Add the fields project_name, estimated_hours, actual_hours, difficulty, 
 * 		and notes. 
 */

//@formatter:off

String sql = ""

+ "INSERT INTO " + PROJECT_TABLE + " "
+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
+ "VALUES "
+ "(?, ?, ?, ?, ?)"; // ? used as placeholders

//@formatter:on

/*
 * 2. Obtain a connection from DbConnection.getConnection(). Assign it a variable of type Connection named 
 * conn in a try-with-resource statement. Catch the SQLException in a catch block added to the try-with-
 * resource. From within the catch block, throw a new DbException. The DbException constructor should take 
 * the SQLException object passed into the catch block.
 */

		try (Connection conn = DbConnection.getConnection()) {// try with resources

/*
 * 3. Start a transaction. Inside the try block, start a transaction by calling startTransaction() and 
 * passing in the Connection object.
 */

			startTransaction(conn);

/*
 * 4a. Pass the SQL statement as a parameter to conn.prepareStatement(). 
 */
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
/*
 * 6. Perform the insert by calling executeUpdate() on the PreparedStatement object.
 */
				stmt.executeUpdate();
				// allow us to get the generated project id

/*
 * 7. Obtain the project ID (primary key) by calling the convenience method in DaoBase, getLastInsertId(). 
 * 	Pass the Connection object and the constant PROJECT_TABLE to getLastInsertId(). Assign the return 
 *		value to an Integer variable named projectId.
 */
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);// look at this method later, sends 
 //a query to MySQL to get the last insert id/auto command value

/*
 * 8. Commit the transaction by calling the convenience method in DaoBase, commitTransaction(). Pass the 
 * Connection object to commitTransaction() as a parameter.
 */

				commitTransaction(conn);

/*
 * Set the projectId on the Project object that was passed into insertProject and return it. 
 */
				project.setProjectId(projectId);
				return project;

/*
 * 4b. Add a catch block to the inner try block that catches Exception. In the catch block, roll back the 
 * transaction and throw a DbException initialized with the Exception object passed into the catch block.
 */
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
}
