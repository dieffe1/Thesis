package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
public class Chat extends HttpServlet {

	private MessageDao messageDao = DAOFactory.getInstance().getMessageDao();

	Project project = null;
	HttpSession session = null;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		session = req.getSession();
		User user = (User) session.getAttribute("user");
		project = (Project) session.getAttribute("project");

		String text = req.getParameter("text");

		Message message = new Message(project, text, user);
		messageDao.save(message);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		session = req.getSession();
		project = (Project) session.getAttribute("project");
		
		if (project == null || session.getAttribute("firstLoad") == null)
			return;

		Boolean firstLoad = (boolean) session.getAttribute("firstLoad");
				
		if(firstLoad) {
			Timestamp date = new Timestamp(Calendar.getInstance().getTime().getTime());
			
			List<Message> chat = messageDao.findForTime(date, project.getId());

			session.setAttribute("firstMessage", (Integer) session.getAttribute("firstMessage") + chat.size());
			session.setAttribute("firstLoad", true);
			String files = (new JSONArray(chat).toString());
			resp.getWriter().print(files);
		} else {

			List<Message> chat = messageDao.findLastMessage(project.getId());
			Collections.reverse(chat);

			session.setAttribute("firstMessage", chat.size());

			String files = (new JSONArray(chat).toString());
			resp.getWriter().print(files);
		}
	}
}
