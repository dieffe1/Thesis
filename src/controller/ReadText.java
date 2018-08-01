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
public class ReadText extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		File file = (File) session.getAttribute("file");
		User user = (User) session.getAttribute("user");
		FileDao fileDao = DAOFactory.getInstance().getFileDao();

		if(file == null || user == null)
			return;

		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");

		if(file.getUser() == null || !file.getUser().getUsername().equals(user.getUsername()))
		{
			if(fileDao.findByPrimaryKey(file.getId()).getPackage() == null) {
				resp.getWriter().print("removed");
				return;
			}
			
			File file1 = fileDao.findByPrimaryKey(file.getId());
			resp.getWriter().print("lock" + file1.getCode());
			return;
		}		
	}
}
