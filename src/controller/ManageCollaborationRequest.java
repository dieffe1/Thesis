package controller;

import java.io.IOException;
import java.util.List;

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

@SuppressWarnings("serial")
public class ManageCollaborationRequest extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute("user");
		
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();

		String accepted = req.getParameter("accepted");
		Long projectId = Long.parseLong(req.getParameter("id"));
		Collaborator collaborator = collaboratorDao.findByPrimaryKey(user.getUsername(), projectId);

		if(accepted.equalsIgnoreCase("true")){
			user.addOtherProject(collaborator.getProject());
			collaboratorDao.updateStatus(user.getUsername(), projectId);
		}
		else
			collaboratorDao.delete(user.getUsername(), projectId);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute("user");
		
		if(user == null)
			return;
		
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();
		List<Project> projects = collaboratorDao.findPendingRequest(user.getUsername());
		
		for (Project project : projects)
			resp.getWriter().print(project.getId() + " " + project.getName() + " ");
	}
}
