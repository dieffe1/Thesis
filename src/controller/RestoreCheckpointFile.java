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

import model.Checkpoint_File;
import model.File;
import model.User;
import persistence.DAOFactory;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class RestoreCheckpointFile extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		List<File> list = new LinkedList<>();

		Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();

		File file = (File) session.getAttribute("file");
		System.out.println("id: " +file.getId());
		list = checkpointFileDao.findByFileId(file.getId());
		System.out.println(list);
		String files = (new JSONArray(list).toString());
		resp.getWriter().print(files);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		File file = (File) session.getAttribute("file");

		Long checkFileId = Long.parseLong(req.getParameter("fileConsulted"));

		Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();
		FileDao fileDao = DAOFactory.getInstance().getFileDao();

		Checkpoint_File checkFile = checkpointFileDao.findByPrimaryKey(checkFileId);
		
		if(user != file.getUser()) {
			resp.getWriter().print("not");
			return;
		}
		
		resp.getWriter().print(checkFile.getText());
		fileDao.updateText(file.getId(), checkFile.getText());
		
		session.setAttribute("file", file);
	}
}
