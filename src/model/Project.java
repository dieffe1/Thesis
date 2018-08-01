package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Project {
	
	private Long id;
	private String name;
	private User creator;
	private Project master;
	private HashMap<Long, Package> packages = new HashMap<>();
	private List<User> collaborators = new ArrayList<>();
	private List<Checkpoint> checkpoints = new LinkedList<>();
	private List<Message> chat = new ArrayList<>();
	
	public Project() { }
	
	public Project(String name, User user) {
		this.name = name;
		this.creator = user;
		this.master = null;
		
	}
	
	public Project(String name, User user, Project project) {
		this.name = name;
		this.creator = user;
		this.master = project;
		
	}
	

	public Project getMaster() {
		return master;
	}

	public void setMaster(Project master) {
		this.master = master;
	}

	public String getName() {
		return name;
	}

	public void rename(String name) {
		this.name = name;
	}

	public void addCollaborator(User user) {
		collaborators.add(user);
	}
	
	public void removeCollaborator(User user) {
		collaborators.remove(user);
	}
	
	public List<User> getCollaborators() {
		return collaborators;
	}

	public List<Checkpoint> getCheckpoints() {
		return checkpoints;
	}

	public List<Message> getChat() {
		return chat;
	}

	public void setChat(List<Message> chat) {
		this.chat = chat;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public void setCheckpoints(List<Checkpoint> checkpoints) {
		this.checkpoints = checkpoints;
	}
	
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "project: " + name + ",   created by " + creator;
	}

	public void addPackage(Package pack) {
		packages.put(pack.getId(), pack);
	}	

	public void removePackage(Package pack)
	{
		packages.remove(pack.getId());
	}
	
	public void createCheckpoint(Checkpoint checkpoint) {
		checkpoints.add(0, checkpoint);
	}

	public HashMap<Long, Package> getPackages() {
		return packages;
	}

	public void setPackages(HashMap<Long, Package> packages) {
		this.packages = packages;
	}
}
