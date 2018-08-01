package model;

public class Collaborator {
	
	private Long id;
	private User user;
	private Project project;
	private boolean status = false;
	
	public Collaborator(User user, Project project) {
		this.user = user;
		this.project = project;
	}
	
	public Collaborator() {	}

	public User getUser() {
		return user;
	}
	public Project getProject() {
		return project;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return user + " collaborate to " + project;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
