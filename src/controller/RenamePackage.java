package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Project;
import persistence.DAOFactory;
import persistence.dao.PackageDao;

@SuppressWarnings("serial")
public class RenamePackage extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		
		String oldName = req.getParameter("packageName");
		String name = req.getParameter("name");

		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		if(packageDao.exist(name, project.getId())) {
			resp.getWriter().print("exist");
			return;
		}

		packageDao.update(project.getId(), oldName, name);
	}

}
