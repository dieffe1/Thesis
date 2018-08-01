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
public class RemoveProject extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute("user");
		Long projectId = ((Project) session.getAttribute("project")).getId();
		
		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();

		if(projectDao.onlineCollaborators(projectId))
		{
			resp.getWriter().print("online");
			return;
		}

		projectDao.delete(projectId);
//		user.removeProject(projectId);
		user.setProjects(projectDao.find(user.getUsername()));
		session.setAttribute("project", null);
	}
}
