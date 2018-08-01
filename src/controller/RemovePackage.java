package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Package;
import persistence.DAOFactory;
import persistence.dao.PackageDao;

@SuppressWarnings("serial")
public class RemovePackage extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		
		String[] hash = req.getParameter("hash").split("/");
		
		Package pack = packageDao.findByName(hash[0].substring(1), hash[1], hash[2]);
		
		if(packageDao.onlineCollaborators(pack.getId())){
			resp.getWriter().print("online");
			return;
		}
		
		packageDao.remove(pack.getId());
	}
}
