package persistence;

import java.util.HashMap;

import persistence.JDBC.CheckpointDaoJDBC;
import persistence.JDBC.Checkpoint_FileDaoJDBC;
import persistence.JDBC.CollaboratorDaoJDBC;
import persistence.JDBC.FileDaoJDBC;
import persistence.JDBC.MessageDaoJDBC;
import persistence.JDBC.PackageDaoJDBC;
import persistence.JDBC.ProjectDaoJDBC;
import persistence.JDBC.UserDaoJDBC;
import persistence.JDBC.UtilDao;
import persistence.dao.CheckpointDao;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.CollaboratorDao;
import persistence.dao.Dao;
import persistence.dao.FileDao;
import persistence.dao.MessageDao;
import persistence.dao.PackageDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

public class PostgresDAOFactory extends DAOFactory {
	
	private static DataSource dataSource = null;
	private HashMap<String, Dao> dao = new HashMap<>();
	
	static {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			
			dataSource = new DataSource("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
			
			/* Amazon */
//			dataSource = new DataSource("jdbc:postgresql://horton.elephantsql.com:5432/xrweijkl", "xrweijkl", "QArprXwwit2MTXqABcv9sv7S9zN6ek6q");
			
			/* Azure */
//			dataSource = new DataSource("jdbc:postgresql://dumbo.db.elephantsql.com:5432/rpwskxsh", "rpwskxsh", "_6Ed-FQ51m_nwDS6XZilaYQALSPNczXo");
			
		} catch (Exception e) {
			System.err.println("PostgresDAOFactory.class: failed to load MySQL JDBC driver\n" + e);
			e.printStackTrace();
		}
	}
	
	public UtilDao getUtilDao() {
		if(dao.get("utilDao") == null)
			dao.put("utilDao", new UtilDao(dataSource));
		
		return (UtilDao) dao.get("utilDao");
	}

	public UserDao getUserDao() {
		if(dao.get("userDao") == null)
			dao.put("userDao", new UserDaoJDBC(dataSource));
		
		return (UserDao) dao.get("userDao");
	}

	public CheckpointDao getCheckpointDao() {
		if(dao.get("checkpointDao") == null)
			dao.put("checkpointDao", new CheckpointDaoJDBC(dataSource));
		
		return (CheckpointDao) dao.get("checkpointDao");
	}
	
	public FileDao getFileDao() {
		if(dao.get("fileDao") == null)
			dao.put("fileDao", new FileDaoJDBC(dataSource));
		
		return (FileDao) dao.get("fileDao");
	}

	public CollaboratorDao getCollaboratorDao() {
		if(dao.get("collaboratorDao") == null)
			dao.put("collaboratorDao", new CollaboratorDaoJDBC(dataSource));
		
		return (CollaboratorDao) dao.get("collaboratorDao");
	}

	public MessageDao getMessageDao() {
		if(dao.get("messageDao") == null)
			dao.put("messageDao", new MessageDaoJDBC(dataSource));
		
		return (MessageDao) dao.get("messageDao");
	}

	public PackageDao getPackageDao() {
		if(dao.get("PackageDao") == null)
			dao.put("PackageDao", new PackageDaoJDBC(dataSource));
		
		return (PackageDao) dao.get("PackageDao");
	}

	public ProjectDao getProjectDao() {
		if(dao.get("ProjectDao") == null)
			dao.put("ProjectDao", new ProjectDaoJDBC(dataSource));
		
		return (ProjectDao) dao.get("ProjectDao");
	}

	public Checkpoint_FileDao getCheckpointFileDao() {
		if(dao.get("Checkpoint_FileDao") == null)
			dao.put("Checkpoint_FileDao", new Checkpoint_FileDaoJDBC(dataSource));
		
		return (Checkpoint_FileDao) dao.get("Checkpoint_FileDao");
	}
}
