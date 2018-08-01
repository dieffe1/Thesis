package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import persistence.DAOFactory;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class UpdateFile extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		Long id = Long.parseLong(req.getParameter("id"));
		String text = req.getParameter("code");
		
		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		fileDao.updateText(id, text);
	}
	
}
