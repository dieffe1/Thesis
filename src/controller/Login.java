package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import persistence.DAOFactory;
import persistence.UserCredential;
import persistence.dao.CollaboratorDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

@SuppressWarnings("serial")
public class Login extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String username = req.getParameter("username");
		String password = req.getParameter("password");

		HttpSession session = req.getSession();

		UserDao userDao = DAOFactory.getInstance().getUserDao();
		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();

		UserCredential user = userDao.findByPrimaryKeyCredential(username);

		if (user == null) {
			resp.getWriter().print("user");
			return;
		} else if (!user.getPassword().equals(password)) {
			resp.getWriter().print("password");
			return;
		}

		user.setProjects(projectDao.find(username));
		user.setOtherProjects(collaboratorDao.find(username));

		session.setAttribute("user", user);

	}
}