package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.ProjectDao;

@SuppressWarnings("serial")
public class RenameProject extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		Project project = (Project) session.getAttribute("project");
		
		String name = req.getParameter("name");
		
		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
		if(projectDao.exist(name, user.getUsername())) {
			resp.getWriter().print("exist");
			return;
		}
		
		projectDao.update(project.getId(),name);
		project.rename(name);
		user.addProject(project);
		
		session.setAttribute("user", user);
		session.setAttribute("project", project);
	}
}
