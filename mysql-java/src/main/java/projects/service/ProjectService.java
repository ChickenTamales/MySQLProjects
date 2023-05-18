package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();// instance variable. now we can access the Dao from the service

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
		
	}

	/*
	 * Create method fetchProjectById(). It returns a Project object and takes an 
	 * Integer projectId as a parameter.
	 * 
	 * Temporarily assign a variable of type Optional<Project> to the results of 
	 * calling projectDao.fetchProjectById(). Pass the project ID to the method.
	 * 
	 * This temporary assignment will cause Eclipse to create the correct 
	 * return value (Optional<Project>) in ProjectService.java.
	 * 
	 * Let Eclipse create the method for you in the ProjectDao class. The editor 
	 * will display ProjectDao.java. Return to ProjectService.java. Save all files.
	 * 
	 * Replace the variable and assignment with a return statement. This will cause 
	 * a compilation error, which you will correct next.
	 * 
	 * Add a method call to .orElseThrow() just inside the semicolon at the end of 
	 * the method call to projectDao.fetchProjectById(). Use a zero-argument Lambda expression 
	 * inside the call to .orElseThrow() to create and return a new NoSuchElementException with 
	 * the custom message, "Project with project ID=" + projectId + " does not exist.". */
	 
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
				"Project with ID=" + projectId + " does not exist. "));
		
		
	}
}