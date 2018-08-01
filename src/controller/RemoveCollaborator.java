package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Project;
import persistence.DAOFactory;
import persistence.dao.CollaboratorDao;

@SuppressWarnings("serial")
public class RemoveCollaborator extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

		String name = req.getParameter("name");
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();

		Project project = (Project) session.getAttribute("project");
		collaboratorDao.delete(name, project.getId());
	}
}
