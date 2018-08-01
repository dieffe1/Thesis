package persistence.dao;

import java.util.HashMap;
import java.util.List;

import model.Collaborator;
import model.Project;
import model.User;

public interface CollaboratorDao extends Dao{

	public void save(Collaborator collaborator);

	public HashMap<Long,Project> find(String username);
	
	public List<User> find(Long projectID);
	
	public List<Collaborator> findCollaborator(Long projectID);
	
	public List<Project> findPendingRequest(String username);
	
	public Collaborator findByPrimaryKey(String username, Long project);
	
	public void updateStatus(String username, Long projectId);
	
	public void update(Collaborator collaborator);

	public void delete(String username, Long projectId);

	public Project findProject(String username, String projectName);
}
