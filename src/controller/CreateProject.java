package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Checkpoint;
import model.Checkpoint_File;
import model.File;
import model.Package;
import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.CheckpointDao;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;
import persistence.dao.ProjectDao;

@SuppressWarnings("serial")
public class CreateProject extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");

		String projectName = req.getParameter("projectName");
		String type = req.getParameter("type");

		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
		
		if(projectDao.exist(projectName, user.getUsername())) {
			resp.getWriter().print("exist");
			return;
		}
		
		Project project = new Project(projectName, user);
		projectDao.save(project);
		
		CheckpointDao checkpointDao = DAOFactory.getInstance().getCheckpointDao();
		Checkpoint checkpoint = new Checkpoint("Initial checkpoint", project, user); 
		checkpointDao.save(checkpoint);
		
		if(type.equals("hello")) {
			Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();
			PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
			FileDao fileDao = DAOFactory.getInstance().getFileDao();

			String code = "package _default;\n\npublic class HelloWorld {\n" + "	public static void main(String[] args) {\n"
					+ "		System.out.println(\"Hello World!\");\n" + "	}\n" + "}\n";
			Package pack = new Package("_default", project);
			File file = new File("HelloWorld", pack, code);
			
			packageDao.save(pack);
			fileDao.save(file);
			checkpointFileDao.save(new Checkpoint_File(code, checkpoint, file, user, "Initial checkpoint"));
		}
	
		user.addProject(project);
	}
	
}
