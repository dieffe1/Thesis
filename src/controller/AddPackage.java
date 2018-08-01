package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Package;
import model.Project;
import persistence.DAOFactory;
import persistence.dao.PackageDao;

@SuppressWarnings("serial")
public class AddPackage extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		System.out.println("servlet add package");

		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		
		String name = req.getParameter("name");
		
		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();

		if(packageDao.exist(name, project.getId()))
		{
			resp.getWriter().print("exist");
			return;
		}
		
		Package pack = new Package(name, project);
		packageDao.save(pack);
		
		resp.getWriter().print(pack.getId()+ "/" + pack.getName());
	}
		
}
