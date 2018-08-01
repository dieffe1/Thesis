package controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import model.File;
import model.Project;
import persistence.DAOFactory;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class FindString extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		List<File> list = new LinkedList<>();
		
		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();
		Project project = (Project) session.getAttribute("project");
		
		String text = req.getParameter("text");
		String type = req.getParameter("type");
		
		if(type.equals("project"))
			list = fileDao.findString(project.getId(), text);
		
		else if(type.equals("checkpoint")) {
			File file = (File) session.getAttribute("file");
			list = checkpointFileDao.findString(file.getId(), text);
		}
		
		if(list.isEmpty())
		{
			resp.getWriter().print("empty");
			return;
		}
		String files = (new JSONArray(list).toString());
		resp.getWriter().print(files);
	
	}
}
