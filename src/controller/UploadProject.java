package controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import model.Checkpoint;
import model.Checkpoint_File;
import model.File;
import model.Package;
import model.Project;
import model.User;
import persistence.PersistenceException;
import persistence.DAOFactory;
import persistence.dao.CheckpointDao;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;
import persistence.dao.ProjectDao;

@MultipartConfig
@SuppressWarnings("serial")
public class UploadProject extends HttpServlet {

	@SuppressWarnings("resource")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		User user = ((User) session.getAttribute("user"));

		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		Project project = new Project("Uploaded", user);
		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
		CheckpointDao checkpointDao = DAOFactory.getInstance().getCheckpointDao();
		Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();

		Package pack = new Package("_default", project);

		projectDao.save(project);
		
		Checkpoint checkpoint = new Checkpoint("Initial Checkpoint", project, user);
		checkpointDao.save(checkpoint);
		
		packageDao.save(pack);

//		List<Part> fileParts = req.getParts().stream()/*.filter(part -> "files".equals(part.getName()))*/
//				.collect(Collectors.toList());
		List<Part> fileParts = req.getParts().stream().collect(Collectors.toList());
		
		for (Part filePart : fileParts) {
			System.out.println(Paths.get(filePart.getSubmittedFileName())); 
			String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
			String name = fileName.split("\\.")[0];
			InputStream fileContent = filePart.getInputStream();
			Scanner s = new Scanner(fileContent, "UTF-8").useDelimiter("\\A");
			String code = s.hasNext() ? s.next().toString() : "";
			String[] split = code.split("package.*;");

			if(split.length > 1)
				code = split[0] + "\npackage _default;\n" + split[1];
			else code = "\npackage _default;\n" + code;
			
//			System.out.println(fileName + "\n" + result);
			
			try {
				File file = new File(name, pack, code);
				fileDao.save(file);
				checkpointFileDao.save(new Checkpoint_File(code, checkpoint, file, user, "Initial CheckpointFile"));
				
			} catch (PersistenceException e) {
				projectDao.delete(project.getId());
				resp.getWriter().print("error");
				return;
			}
		}

		user.addProject(project);
		resp.getWriter().print(project.getId());
		session.setAttribute("user", user);
		session.setAttribute("project", project);
	}
}
