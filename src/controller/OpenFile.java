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
public class OpenFile extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		/*
		 * DA ELIMINARE
		 * 
		 */
		
		System.out.println("ELIMINARE ??");
		
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");

		Long fileId = Long.parseLong(req.getParameter("fileId"));
		String mode = req.getParameter("mode");

		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		File file = fileDao.findByPrimaryKey(fileId);

		if(mode.equals("write")) {
			if(file.getUser() == null) {
				file.setUser(user);
				fileDao.enableWrite(user.getUsername(), fileId);
			}
			else 
				resp.getWriter().print("lock");
		}

		session.setAttribute("file", file);
	}
}
