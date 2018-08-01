package model;

import java.sql.Timestamp;
import java.util.Calendar;

public class Message {

	private String text;
	private Long id;
	private Project project;
	private User user;
	private Timestamp date;

	public Message(Project project, String text, User user) {
		this.text = text;
		this.project = project;
		this.user = user;
		date = new Timestamp(Calendar.getInstance().getTime().getTime());
	}

	public Message() {
	}

	public String getText() {
		return text;
	}

	public Long getId() {
		return id;
	}

	public Project getProject() {
		return project;
	}

	public User getUser() {
		return user;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return user.getUsername() + " ha scritto \" " + text + " \" su " + project.getName() + "  il " + date;
	}

}
