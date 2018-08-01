package model;

import java.util.HashMap;

public class Package 
{
	private Long id;
	private String name;
	private Project project;
	private HashMap<Long, File> files = new HashMap<>();

	public Package() { }
	
	public Package(String name, Project project) {
		this.name = name;
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public Project getProject() {
		return project;
	}

	public void setName(String name) {
		this.name = name;
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

	public HashMap<Long, File> getFiles() {
		return files;
	}

	public void setFiles(HashMap<Long, File> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "id: " + id + ", name: " + name + "   ";
	}

	public void addFile(File file)
	{
		files.put(file.getId(), file);
	}

	public void removeFile(File file)
	{
		files.remove(file.getId());
	}
	
	public void rename(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object p) {
		return this.id.equals(((Package) p).getId());
	}
}
