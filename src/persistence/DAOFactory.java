package persistence;

import persistence.JDBC.UtilDao;
import persistence.dao.CheckpointDao;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.CollaboratorDao;
import persistence.dao.FileDao;
import persistence.dao.MessageDao;
import persistence.dao.PackageDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

public abstract class DAOFactory {

	private static DAOFactory instance = null;
	
	public static DAOFactory getInstance() {
		if(instance == null)
			instance = new PostgresDAOFactory();
		
		return instance;
	}
	
	public abstract UtilDao getUtilDao();

	public abstract UserDao getUserDao();

	public abstract CheckpointDao getCheckpointDao();

	public abstract FileDao getFileDao();

	public abstract CollaboratorDao getCollaboratorDao();

	public abstract MessageDao getMessageDao();

	public abstract PackageDao getPackageDao();

	public abstract ProjectDao getProjectDao();

	public abstract Checkpoint_FileDao getCheckpointFileDao();
}
