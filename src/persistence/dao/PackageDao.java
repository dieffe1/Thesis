package persistence.dao;

import java.util.HashMap;

import model.Package;

public interface PackageDao extends Dao{

	public void save(Package name);

	public Package findByPrimaryKey(Long id);

	public HashMap<Long, Package> find(Long projectId);
	
	public HashMap<Long, Package> findByName(String creator, String projectName);
	
	public Package findByName(String creator, String projectName, String packageName);
	
	public void update(Package pack);
	
	public void update(Long projectId, String oldName, String name);

	public boolean exist(String name, Long projectId); 
	
	public void restore(Long checkpointId);
	
	public void remove(Long id);
	
	public void delete(Long id);
	
	public boolean onlineCollaborators(Long packageId);
	
	public void mergePackage(Long masterid, Long branchid);
}
