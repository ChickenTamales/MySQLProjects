package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

/*
 *     1. In order to display a list of menu options you must store them somewhere. In this step you will 
 *     write the code that holds the list of operations. 
 *     
 *     a. Add a private instance variable named "operations". The type is List<String>. Initialize it using 
 *     List.of with the following value: "1) Add a project". To prevent the Eclipse formatter from 
 *     reformatting the list, surround the variable declaration with // @formatter:off and // @formatter:on
 */

public class ProjectsApp {

/*
 * In this step you will use a Scanner to obtain input from a user from the Java console.
 */

	private Scanner scanner = new Scanner(System.in);// instead of console output, it's console input from user

	private ProjectService projectService = new ProjectService();
	
	private Project curProject;
	
	

//@formatter:off
	
 private List<String> operations = List.of(
		 "1) Add a project",
		 "2) List projects",
		 "3) Select a project"
		 );
//@formatter:on

/*
 * In this step you will call the method that processes the menu. In the main() method, create a new 
 * ProjectsApp object and call the method:
 * processUserSelections() method.
 */

	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}

/*
 * Now you can create the processUserSelections()
 */

	private void processUserSelections() {
		boolean done = false;

		while (!done) {// while not done
			try {
				int selection = getUserSelection();

/*
 * Now we want to add code that will process the user's selection.
 */

				switch (selection) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;
					
				case 2:
					listProjects();
					break;
					
				case 3:
					selectProject();
					break;

				default:
					System.out.println("\n" + selection + " is not valid. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}
	}
/*
 * Add a new method named selectProject(). It takes no parameters and returns nothing.
 * 
 * Call listProjects() to print a List of Projects.
 * 
 * Collect a project ID from the user and assign it to an Integer variable named projectId. 
 * Prompt the user with "Enter a project ID to select a project". 
 * 
 * Set the instance variable curProject to null to unselect any currently selected project. 
 * This is done in case the call to the service results in an exception being thrown. 
 * Rather than leave the current project selected in that case, it is unselected first.
 * 
 * Call a new method, fetchProjectById() on the projectService object. 
 * The method should take a single parameter, the project ID input by the user. 
 * It should return a Project object. Assign the returned Project object to 
 * the instance variable curProject. Note that if an invalid project ID is entered, 
 * projectService.fetchProjectById() will throw a NoSuchElementException, 
 * which is handled by the catch block in processUserSelections().
 * 
 * At the end of the method, add a check to see if curProject is null. 
 * If so, print "Invalid project ID selected." on the console. 
 */
private void selectProject() {
	listProjects();
	
	Integer projectId = getIntInput("Enter a project ID to select a project.");
	
	curProject = null;
	
	curProject = projectService.fetchProjectById(projectId);
	
	if (Objects.isNull(curProject)) {
		System.out.println("\nThat is not a valid project.");
	}
}

private void listProjects() {
	List<Project> projects = projectService.fetchAllProjects();
	
	curProject = null;
	
	System.out.println("\nProjects:");
	
/*
 * For each Project, print the ID and name separated by ": ". Indent each line with a couple of spaces.
 */
	
	projects.forEach(project -> System.out.println("   " 
	+ project.getProjectId() + ": " + project.getProjectName()));
}

/*
 * Create the method createProject().
 */
	
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

/*
 * 4. In this step you will create the method getDecimalInput().
 */
	
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			// create a new BidDecimal object and set the decimal places to 2.
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the Menu. TTFN!");
		return true;
	}

/*
 * 5. Create the method getUserSelection().
 */

	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection.");

		return Objects.isNull(input) ? -1 : input;
	}

/*
 * 6. Create the method printOperations().
 */

	private void printOperations() {
		System.out.println();
		System.out.println("\nThese are the available selections. Press enter to quit.");

		operations.forEach(line -> System.out.println("   " + line));// print the operations
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}else {
			System.out.println("\nYou are working with " + curProject);
		}
	}

//7. Create the method getIntInput.

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
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
			throw new DbException(input + " is not a valid number.");
		}
	}

//8. Now create the method that really prints the prompt and gets the input from the user.

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	}
}