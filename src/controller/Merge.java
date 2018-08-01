package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import javafx.util.Pair;
import model.File;
import model.Project;
import persistence.DAOFactory;
import persistence.dao.PackageDao;
import persistence.dao.ProjectDao;

@SuppressWarnings("serial")
public class Merge extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

		Project branch = (Project) session.getAttribute("project");

		ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		
		if(projectDao.findByPrimaryKey(branch.getId()).getMaster() == null) {
			resp.getWriter().print("master");
			return;
		} 
		
		packageDao.mergePackage(branch.getMaster().getId(), branch.getId());
		
		List<Pair<File, File>> conflitti = projectDao.findFilesInConflict(branch.getMaster().getId(), branch.getId());
			
		if(conflitti.size()==0) {
			resp.getWriter().print("zero");
		} else {
			resp.getWriter().print(new JSONArray(conflitti).toString());
		}
	}
}
