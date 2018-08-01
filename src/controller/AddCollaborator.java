package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Collaborator;
import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.CollaboratorDao;
import persistence.dao.UserDao;

@SuppressWarnings("serial")
public class AddCollaborator extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		System.out.println("servlet add collaborator");
		
		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		
		UserDao userDao = DAOFactory.getInstance().getUserDao();
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();

		String name = req.getParameter("name");
		User user = userDao.findByPrimaryKey(name);

		Collaborator collaborator = collaboratorDao.findByPrimaryKey(name, project.getId());

		if(user != null && collaborator == null)
		{			
			project.addCollaborator(user);
			collaboratorDao.save(new Collaborator(user, project));
		}
	}
}
