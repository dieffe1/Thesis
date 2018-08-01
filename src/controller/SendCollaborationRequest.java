package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import model.Collaborator;
import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.CollaboratorDao;
import persistence.dao.UserDao;

@SuppressWarnings("serial")
public class SendCollaborationRequest extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Gson gson = new Gson();
		String[] usernames = gson.fromJson(req.getParameter("names"), String[].class);
		
		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		UserDao userDao = DAOFactory.getInstance().getUserDao();
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();

		for (int i = 0; i < usernames.length; i++) {

			String name = usernames[i];
			User user = userDao.findByPrimaryKey(name);

			collaboratorDao.save(new Collaborator(user, project));
		}
		
	}
}
