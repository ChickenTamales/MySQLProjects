package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();// instance variable. now we can access the Dao from the service

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}
}