package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;
import persistence.PostgresDAOFactory;
import persistence.dao.UserDao;

@SuppressWarnings("serial")
public class UpdateUserCredential extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		
		String type = req.getParameter("type");
		String value = req.getParameter("value");
		
		UserDao dao = PostgresDAOFactory.getInstance().getUserDao();
		
		switch(type) {
		case "password":
			dao.setPassword(user, value);
			break;
		case "mail" :
			user.setMail(value);
			dao.updateMail(user);
			break;
		case "image":
			user.setImage(value);
			dao.updateImage(user);
			break;
		}
	}
}
