package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.File;
import persistence.DAOFactory;
import persistence.dao.FileDao;

@SuppressWarnings("serial")
public class RenameFile extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		File file = (File) session.getAttribute("file");
		String name = req.getParameter("name");
		
		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		
		if(fileDao.exist(name, file.getPackage().getId())) {
			resp.getWriter().print("exist");
			return;
		}
		
		fileDao.rename(name,file.getId());
		file.setName(name);
		session.setAttribute("file", file);
		
	}
}
