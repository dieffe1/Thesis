package controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.CollaboratorDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

@SuppressWarnings("serial")
public class LoginAPI extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("login post");
		
		HttpSession session = req.getSession();
		String email = req.getParameter("email");
		String username = req.getParameter("name");
		String image = req.getParameter("image");

		UserDao userDao = DAOFactory.getInstance().getUserDao();
		User user = new User(username, email, image);
		userDao.save(user);
				
		if(session.getAttribute("user") == null)
			session.setAttribute("user", user);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		String email = req.getParameter("email");
		
		UserDao userDao = DAOFactory.getInstance().getUserDao();
		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();

		User user = userDao.findByMail(email);
		if(user == null){
			resp.getWriter().print("register");
			return;
		}
		
		HashMap<Long, Project> projects = projectDao.find(user.getUsername());
		user.setProjects(projects);

		HashMap<Long, Project> otherProjects = collaboratorDao.find(user.getUsername());
		user.setOtherProjects(otherProjects);

		session.setAttribute("user", user);
		resp.getWriter().print("logged");
	}
}
