package model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

public class Checkpoint {

	private Long id;
	private String description;
	private Project project;
	private User creator;
	private Timestamp date;
	private HashMap<Long, Checkpoint_File> files = new HashMap<>();

	public Checkpoint(String description, Project project, User creator) {
		this.creator = creator;
		this.project = project;
		this.date = new Timestamp(Calendar.getInstance().getTime().getTime());
		this.description = description;
		
		if(description == "")
			this.description = date.toString().substring(0, date.toString().length()-4);
	}

	public Checkpoint() {
	}

	public Project getProject() {
		return project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		return "checkpoint " + id + " del progetto " + project.getName();
	}

	public HashMap<Long, Checkpoint_File> getCheckpointFiles() {
		return files;
	}

	public void setCheckpointFiles(HashMap<Long, Checkpoint_File> checkpointFiles) {
		this.files = checkpointFiles;
	}

	public void add(Checkpoint_File checkFile) {
		files.put(checkFile.getFile().getId(), checkFile);
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

}
