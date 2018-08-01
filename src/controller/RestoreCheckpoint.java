package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistence.DAOFactory;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;

@SuppressWarnings("serial")
public class RestoreCheckpoint extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		FileDao fileDao = DAOFactory.getInstance().getFileDao();
		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		
		Long checkpointID = Long.parseLong(req.getParameter("checkpointId"));
		
		fileDao.restore(checkpointID);
		packageDao.restore(checkpointID);
	}
}
