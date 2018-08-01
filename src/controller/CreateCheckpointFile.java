package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Checkpoint_File;
import model.File;
import model.User;
import persistence.DAOFactory;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class CreateCheckpointFile extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		File file = (File) session.getAttribute("file");

		Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();
		
		String description = req.getParameter("description");

		checkpointFileDao.save(new Checkpoint_File(file.getCode(), file, user, description));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		File file = (File) session.getAttribute("file");

		FileDao fileDao = DAOFactory.getInstance().getFileDao();

		if(fileDao.findByPrimaryKey(file.getId()).getUser().getUsername().equals(user.getUsername())) 
			resp.getWriter().print("yes");
		else
			resp.getWriter().print("no");
	}
}
