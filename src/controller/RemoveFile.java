package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.File;
import model.User;
import persistence.DAOFactory;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class RemoveFile extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		File file = (File)session.getAttribute("file");

		FileDao fileDao = DAOFactory.getInstance().getFileDao();

		if(file.getUser() == user) {
			fileDao.remove(file.getId());
			session.setAttribute("file", null);
			resp.getWriter().print("yes");
		}
		else
			resp.getWriter().print("no");
	}
}
