package persistence.dao;

import java.util.HashMap;
import java.util.List;

import model.Checkpoint_File;
import model.File;

public interface Checkpoint_FileDao extends Dao{

	public void save(Checkpoint_File checkpointFile);

	public Checkpoint_File findByPrimaryKey(Long id);

	public HashMap<Long, Checkpoint_File> findAll();
	
	public HashMap<Long, Checkpoint_File> find(Long checkpointID);

	public void update(Checkpoint_File checkpointFile);

	public void delete(Long checkpointId);
	
	public void deleteFromFile(Long fileId);
	
	public void deleteFromPackage(Long packageId);
	
	public List<File> findString(Long fileId, String text);
	
	public List<File> findByFileId(Long fileId);
	
	public HashMap<Long, File> findLast(Long projectId);
}
