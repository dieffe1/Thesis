package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.File;
import model.Package;
import model.Project;
import persistence.DAOFactory;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;

@SuppressWarnings("serial")
public class AddFile extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute("project");
		
		String name = req.getParameter("name");
		String packageName = req.getParameter("packageName");

		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		FileDao fileDao = DAOFactory.getInstance().getFileDao();

		Package pack = packageDao.findByName(project.getCreator().getUsername(), project.getName(), packageName);
		if(fileDao.exist(name, pack.getId()))
		{
			resp.getWriter().print("exist");
			return;
		}
		String code = "package " + pack.getName() +";\n\npublic class " + name + "{\n\n\n}"; 
		File file = new File(name, pack, code);
		fileDao.save(file);

		session.setAttribute("file", file);
		
		resp.getWriter().print(file.getId());
	}
}
