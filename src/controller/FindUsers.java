package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import model.Project;
import persistence.DAOFactory;
import persistence.dao.UserDao;

@SuppressWarnings("serial")
public class FindUsers extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		UserDao userDao = DAOFactory.getInstance().getUserDao();
		
		if(project==null)
			return;
		
		List<String> list = userDao.findAll(project);
		
		String files = (new JSONArray(list).toString());
		resp.getWriter().print(files);
	}
}
