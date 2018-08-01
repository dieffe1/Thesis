package controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import model.Project;
import model.User;
import persistence.PostgresDAOFactory;
import persistence.dao.CollaboratorDao;
import persistence.dao.ProjectDao;

@SuppressWarnings("serial")
public class TakeBranch extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");

		ProjectDao projectDao = PostgresDAOFactory.getInstance().getProjectDao();
		CollaboratorDao collaboratorDao = PostgresDAOFactory.getInstance().getCollaboratorDao();


		HashMap<Long, Project> hash = new HashMap<>();
		String projectName = (String) req.getParameter("nameProject");
		Project project = new Project();

		HashMap<Long, Project> userProjects = projectDao.find(user.getUsername());
		HashMap<Long, Project> userOtherProject = collaboratorDao.find(user.getUsername());

		int count = 0;
		for (Project projectHash : userProjects.values()) {
			if(projectHash.getName().equals(projectName)) {
				project = projectHash;
				count = 1;
				break;
			}
		}
		if(count == 0) {
			for (Project projectHash : userOtherProject.values()) {
				if(projectHash.getName().equals(projectName)) {
					project = projectHash;
					break;
				}
			}
		}
		hash = projectDao.findBranch(project.getId());
		
		if(hash.size() == 0) {
			resp.getWriter().print("onlyMaster");
			return;
		}
		
		hash.put(project.getId(), project);

		String branch = (new JSONArray(hash.values()).toString());
		resp.getWriter().print(branch);


	}
}
