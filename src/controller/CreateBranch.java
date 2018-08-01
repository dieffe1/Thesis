package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Collaborator;
import model.File;
import model.Package;
import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.CollaboratorDao;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

@SuppressWarnings("serial")
public class CreateBranch extends HttpServlet {


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		Project project = (Project) session.getAttribute("project");
		
		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
		CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();
		UserDao userDao = DAOFactory.getInstance().getUserDao();
		
		List<Collaborator> findCollaborator = new ArrayList<>();

		System.out.println("User: " + user.getUsername() + " crea branch su: " + project.getName());
		
		findCollaborator = collaboratorDao.findCollaborator(project.getId());
		
		if(!project.getCreator().getUsername().equals(user.getUsername())) {
			User userColl = userDao.findByPrimaryKey(project.getCreator().getUsername());
			Collaborator coll = new Collaborator(userColl, project);
			coll.setStatus(true);
			findCollaborator.add(coll);
		}
		
		
		
		String name = req.getParameter("name");
		
		if(project.getName().equals(name)) {
			resp.getWriter().print("equalMaster");
			return;
		}
		
		HashMap<Long, Project> branches = projectDao.findBranch(project.getId());
		
		for (Project projectBranch : branches.values()) {
			if(projectBranch.getName().equals(name)) {
				resp.getWriter().print("equalBranch");
				return;
			}
		}
		
		Project branch = new Project(name, user, project);
		projectDao.save(branch);
		
		for (Collaborator collaborator : findCollaborator) {
			collaborator.setProject(branch);
			collaboratorDao.save(collaborator);
		}

		HashMap<Long, Package> packages = packageDao.find(project.getId());

		for (Long packageId : packages.keySet()) {
			Package pack = packages.get(packageId);
			pack.setProject(branch);
			packageDao.save(pack);
			HashMap<Long, File> files = fileDao.find(packageId);
			for(Long fileId : files.keySet()) {
				File file = new File(files.get(fileId).getName(), pack, files.get(fileId).getCode());
				fileDao.save(file);
			}
		}
		//				user.addProject(branch);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		Project project = (Project) session.getAttribute("project");
		
		if(project.getMaster() != null) {
			resp.getWriter().print("noMaster");
			return;
		}
		resp.getWriter().print("ok");

	}
}
