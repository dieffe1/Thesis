package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Checkpoint;
import model.Checkpoint_File;
import model.File;
import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.CheckpointDao;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class CreateCheckpoint extends HttpServlet {

	FileDao fileDao = DAOFactory.getInstance().getFileDao();
	CheckpointDao checkpointDao = DAOFactory.getInstance().getCheckpointDao();
	Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		Project project = (Project) session.getAttribute("project");

		String description = req.getParameter("name");
		HashMap<Long, File> files = fileDao.findByProject(project.getId());
		
		Checkpoint checkpoint = new Checkpoint(description, project, user);
		checkpointDao.save(checkpoint);

		Set<Long> filesId = files.keySet();
		for (Long fileId : filesId) {
			Checkpoint_File checkFile = new Checkpoint_File(files.get(fileId).getCode(), checkpoint, files.get(fileId), user, description);
			checkpointFileDao.save(checkFile);
		}
		
		project.createCheckpoint(checkpoint);

		session.setAttribute("project", project);
	}
}
