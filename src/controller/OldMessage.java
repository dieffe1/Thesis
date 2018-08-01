package controller;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import model.Message;
import model.Project;
import model.User;
import persistence.DAOFactory;
import persistence.dao.MessageDao;

@SuppressWarnings("serial")
public class OldMessage extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		MessageDao messageDao = DAOFactory.getInstance().getMessageDao();

		User user = (User) session.getAttribute("user");
		Project project = (Project) session.getAttribute("project");

		if (user == null || project == null)
			return;

		List<Message> chat = new ArrayList<>();
		
		
		chat.add(messageDao.findOlder(project.getId(), (Integer) session.getAttribute("firstMessage")));
		
		session.setAttribute("firstMessage", (Integer) session.getAttribute("firstMessage") + 1);

		String files = (new JSONArray(chat).toString());
		resp.getWriter().print(files);
	}

}
