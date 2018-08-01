package model;

import java.util.HashMap;

public class User {

	protected String username;
	protected String mail;
	protected String image;
	private HashMap<Long, Project> projects = new HashMap<>();
	private HashMap<Long, Project> otherProjects = new HashMap<>();
	
	public User() { }

	public User(String username, String mail) {
		this.username = username;
		this.mail = mail;
		this.image = "null";
	}
	
	public User(String username, String mail, String image) {
		this(username, mail);
		this.image = image;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public HashMap<Long, Project> getProjects() {
		return projects;
	}

	public void setProjects(HashMap<Long, Project> projects) {
		this.projects = projects;
	}

	public HashMap<Long, Project> getOtherProjects() {
		return otherProjects;
	}

	public void setOtherProjects(HashMap<Long, Project> otherProjects) {
		this.otherProjects = otherProjects;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}

	public void addProject(Project project) {
		projects.put(project.getId(), project);
	}
	
	public void addOtherProject(Project project) {
		otherProjects.put(project.getId(), project);
	}
	
	public void removeProject(Long id)
	{
		projects.remove(id);
	}
	
	public void removeOtherProject(Project project)
	{
		otherProjects.remove(project.getId());
	}
	
	@Override
	public String toString() {
		return "user " + username;
	}

}
