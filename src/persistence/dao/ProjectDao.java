package persistence.dao;

import java.util.HashMap;
import java.util.List;

import javafx.util.Pair;
import model.File;
import model.Project;

public interface ProjectDao extends Dao{

	public void save(Project project);
	
	public Project findByPrimaryKey(Long projectId);

	public HashMap<Long, Project> find(String username);
	
	public Project findByName(String creator, String projectName);
	
	public void update(Project project);

	public void update(Long projectId, String name);
	
	public void delete(Long projectId);

	public boolean exist(String name, String username);
	
	public boolean onlineCollaborators(Long projectId);
	
	public HashMap<Long, Project> findBranch(Long projectId);
	
	public List<Pair<File,File>> findFilesInConflict(Long masterid, Long branchid);
}
