package persistence.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.util.Pair;
import model.Checkpoint;
import model.File;
import model.Message;
import model.Package;
import model.Project;
import model.User;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.DAOFactory;
import persistence.dao.CheckpointDao;
import persistence.dao.CollaboratorDao;
import persistence.dao.MessageDao;
import persistence.dao.PackageDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

public class ProjectDaoJDBC implements ProjectDao {

	private DataSource dataSource = null;

	public ProjectDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(Project project) {
		Connection connection = dataSource.getConnection();
		try {
			Long id = IdBroker.getID(connection, "projectID");
			project.setId(id);
			String insert = "insert into project(id,name,creator,master) values (?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, id);
			statement.setString(2, project.getName());
			statement.setString(3, project.getCreator().getUsername());

			if(project.getMaster() != null)
				statement.setLong(4, project.getMaster().getId());
			else
				statement.setNull(4, 0);

			statement.executeUpdate();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	public Project findByPrimaryKey(Long id) {
		Connection connection = dataSource.getConnection();
		Project project = null;
		try {
			PreparedStatement statement;
			String query = "select * from project where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				project = new Project();
				project.setId(result.getLong("id"));
				project.setName(result.getString("name"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("creator"));
				project.setCreator(user);

				Long masterId = result.getLong("master");

				if(masterId != null) {
					ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
					Project master = projectDao.findByPrimaryKey(result.getLong("master"));
					project.setMaster(master);
				}

			}
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return project;
	}

	public Project findByName(String creator, String projectName) {
		Connection connection = dataSource.getConnection();
		Project project = null;
		try {
			PreparedStatement statement;
			String query = "select * from project where name = ? and creator = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, projectName);
			statement.setString(2, creator);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				project = new Project();
				project.setId(result.getLong("id"));
				project.setName(result.getString("name"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("creator"));
				project.setCreator(user);

				Long masterId = result.getLong("master");

				if(masterId != null) {
					ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
					Project master = projectDao.findByPrimaryKey(result.getLong("master"));
					project.setMaster(master);
				}
			}
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return project;
	}
	
	@Override
	public List<Pair<File,File>> findFilesInConflict(Long masterid, Long branchid){
		Connection connection = dataSource.getConnection();
		List<Pair<File,File>> files = new ArrayList<>();
		PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
		try {
			PreparedStatement statement;
			String query = "select f1.id as id1, f1.name as name1, f1.code as code1, f1.package as pack1, "
					+ "f2.id as id2, f2.name as name2, f2.package as pack2, f2.code as code2\n" + 
					"from package as pack1, package as pack2, file as f1, file as f2 \n" + 
					"where pack1.project = ? and pack2.project = ? and pack1.name=pack2.name and \n" + 
					"f1.package=pack1.id and f2.package=pack2.id and f1.name=f2.name and f1.code<>f2.code";
			statement = connection.prepareStatement(query);
			statement.setLong(1, masterid);
			statement.setLong(2, branchid);
			Pair<File,File> pair;
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				File file1 = new File();
				file1.setId(result.getLong("id1"));
				file1.setName(result.getString("name1"));
				file1.setCode(result.getString("code1"));
				Package pack1 = packageDao.findByPrimaryKey(result.getLong("pack1"));
				file1.setPackage(pack1);
				
				File file2 = new File();
				file2.setId(result.getLong("id2"));
				file2.setName(result.getString("name2"));
				file2.setCode(result.getString("code2"));
				Package pack2 = packageDao.findByPrimaryKey(result.getLong("pack2"));
				file2.setPackage(pack2);
				
				pair = new Pair<File, File>(file1, file2);
				files.add(pair);
				
			}
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return files;
	}

	public HashMap<Long, Project> find(String username) {
		Connection connection = dataSource.getConnection();
		HashMap<Long, Project> projects = new HashMap<>();
		try {
			Project project;
			String query = "select * from project where creator = ? AND master is null";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				project = new Project();
				project.setId(result.getLong("id"));
				project.setName(result.getString("name"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("creator"));
				project.setCreator(user);

				Long masterId = result.getLong("master");

				if(masterId != null) {
					ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
					Project master = projectDao.findByPrimaryKey(result.getLong("master"));
					project.setMaster(master);
				}

				projects.put(project.getId(), project);
			}
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return projects;
	}

	public void update(Project project) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update project SET name = ?, creator = ? WHERE id=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, project.getName());
			statement.setString(2, project.getCreator().getUsername());
			statement.setLong(3, project.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	public void update(Long id, String name) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update project SET name = ? WHERE id=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, name);
			statement.setLong(2, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	public void delete(Long id) {
		Connection connection = dataSource.getConnection();
		try {

//			CheckpointDao checkpointDao = DAOFactory.getInstance().getCheckpointDao();
//			List<Checkpoint> checkpoints = checkpointDao.find(id);
//
//			for (Checkpoint checkpoint : checkpoints)
//				checkpointDao.delete(checkpoint.getId());
//
//			PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
//			HashMap<Long, Package> packages = packageDao.find(id);
//
//			for (Long packageId : packages.keySet())
//				packageDao.delete(packageId);
//
//			MessageDao messageDao = DAOFactory.getInstance().getMessageDao();
//			List<Message> messages = messageDao.find(id);
//
//			for (Message message : messages)
//				messageDao.delete(message);
//
//			CollaboratorDao collaboratorDao = DAOFactory.getInstance().getCollaboratorDao();
//			List<User> collaborators = collaboratorDao.find(id);
//			for (User collaborator : collaborators)
//				collaboratorDao.delete(collaborator.getUsername(), id);
//			
//			HashMap<Long, Project> branch = findBranch(id);
//			for (Long projectId : branch.keySet()) {
//				delete(projectId);
//				
//			}
			
			String delete = "delete FROM project WHERE id = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	@Override
	public boolean exist(String name, String username) {
		Connection connection = dataSource.getConnection();
		try {
			String find = "select * FROM project WHERE name = ? AND creator = ?";
			PreparedStatement statement = connection.prepareStatement(find);
			statement.setString(1, name);
			statement.setString(2, username);
			ResultSet result = statement.executeQuery();

			return result.next();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return false;
	}

	@Override
	public boolean onlineCollaborators(Long projectId) {
		Connection connection = dataSource.getConnection();
		try {
			String find = "select * from file as F where F.package in (select p.id from package as p where p.project = ?)";
			PreparedStatement statement = connection.prepareStatement(find);
			statement.setLong(1, projectId);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				String name = result.getString("username");
				if (name != null)
					return true;
			}

			return false;
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return false;
	}

	@Override
	public HashMap<Long, Project> findBranch(Long projectId) {
		Connection connection = dataSource.getConnection();
		HashMap<Long, Project> projects = new HashMap<>();
		try {
			Project project;
			String query = "select * from project where master = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, projectId);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				project = new Project();
				project.setId(result.getLong("id"));
				project.setName(result.getString("name"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("creator"));
				project.setCreator(user);

				Long masterId = result.getLong("master");

				if(masterId != null) {
					ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
					Project master = projectDao.findByPrimaryKey(result.getLong("master"));
					project.setMaster(master);
				}

				projects.put(project.getId(), project);
			}
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return projects;
	}
}