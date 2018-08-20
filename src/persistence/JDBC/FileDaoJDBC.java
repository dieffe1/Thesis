package persistence.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import model.Checkpoint_File;
import model.File;
import model.Package;
import model.User;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.DAOFactory;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.Dao;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;
import persistence.dao.UserDao;

public class FileDaoJDBC implements FileDao {

	private DataSource dataSource = null;

	public FileDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(File file) {
		Connection connection = dataSource.getConnection();
		try {
			Long id = IdBroker.getID(connection, "fileID");
			file.setId(id);
			String insert = "insert into file(id,name, package, code,username) values (?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, file.getId());
			statement.setString(2, file.getName());
			statement.setLong(3, file.getPackage().getId());
			statement.setString(4, file.getCode());
			statement.setNull(5, 0);
			statement.executeUpdate();

			Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();
			checkpointFileDao.save(new Checkpoint_File(file.getCode(), null, file,
					file.getPackage().getProject().getCreator(), "Initial"));
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

	public File findByPrimaryKey(Long id) {
		Connection connection = dataSource.getConnection();
		File file = null;
		try {
			PreparedStatement statement;
			String query = "select * from file where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				file = new File();
				file.setId(result.getLong("id"));
				file.setName(result.getString("name"));

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				file.setPackage(pack);

				file.setCode((String) result.getObject("code"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				file.setUser(user);

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
		return file;
	}

	public HashMap<Long, File> find(Long packageId) {
		Connection connection = dataSource.getConnection();
		HashMap<Long, File> files = new HashMap<>();
		try {
			File file;
			PreparedStatement statement;
			String query = "select * from file where package = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, packageId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				file = new File();
				file.setId(result.getLong("id"));
				file.setName(result.getString("name"));
				file.setCode(result.getString("code"));

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				file.setPackage(pack);

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				file.setUser(user);

				files.put(file.getId(), file);
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

	public HashMap<Long, File> findByProject(Long projectId) {
		Connection connection = dataSource.getConnection();
		HashMap<Long, File> files = new HashMap<>();
		try {
			File file;
			PreparedStatement statement;
			String query = "select * from file as F where F.package in (select p.id from package as p where p.project = ?)";
			statement = connection.prepareStatement(query);
			statement.setLong(1, projectId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				file = new File();
				file.setId(result.getLong("id"));
				file.setName(result.getString("name"));
				file.setCode(result.getString("code"));

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				file.setPackage(pack);

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				file.setUser(user);

				files.put(file.getId(), file);
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

	public File findByName(String username, String projectName, String packageName, String fileName) {
		Connection connection = dataSource.getConnection();
		File file = null;
		try {
			String query = "select * from file where name = ? and package = (select id from package where name = ? and "
					+ "	(project = ( select id from project where name = ? and creator = ?)))";
					
			PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, fileName);
			statement.setString(2, packageName);
			statement.setString(3, projectName);
			statement.setString(4, username);
			ResultSet result = statement.executeQuery();
			
			if(!result.next()){
				query =  "select * from file where name = ? and package = (select id from package where name = ? and project ="
						+ "(select project from collaborator where username = ? and project = (select id from project where name = ?)))";

				statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				statement.setString(1, fileName);
				statement.setString(2, packageName);
				statement.setString(3, username);
				statement.setString(4, projectName);
				result = statement.executeQuery();
			}

			result.beforeFirst();
			
			if (result.next()) {
				file = new File();
				file.setId(result.getLong("id"));
				file.setName(result.getString("name"));

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				file.setPackage(pack);

				file.setCode((String) result.getObject("code"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				file.setUser(user);

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
		return file;
	}

	public HashMap<Long, File> findByName(String username, String projectName, String packageName) {
		Connection connection = dataSource.getConnection();
		HashMap<Long, File> files = new HashMap<>();
		try {
			File file;
			PreparedStatement statement;
			String query = "select * from file where package = (select id from package where name = ? and "
					+ "	(project = ( select id from project where name = ? and creator = ?)))";
					
			statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, packageName);
			statement.setString(2, projectName);
			statement.setString(3, username);
			ResultSet result = statement.executeQuery();
			
			if(!result.next()){
				query =  "select * from file where package = (select id from package where name = ? and project ="
						+ "(select project from collaborator where username = ? and project = (select id from project where name = ?)))";
						
				statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				statement.setString(1, packageName);
				statement.setString(2, username);
				statement.setString(3, projectName);
				result = statement.executeQuery();
			}
			
			result.beforeFirst();
			
			while (result.next()) {
				file = new File();
				file.setId(result.getLong("id"));
				file.setName(result.getString("name"));
				file.setCode(result.getString("code"));

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				file.setPackage(pack);

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				file.setUser(user);

				files.put(file.getId(), file);
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
	
	@Override
	public void merge(Long branchPackageId, Long masterPackageId) {
		Connection connection = dataSource.getConnection();
		try {
			File file;
			PreparedStatement statement;
			String query = "select * from file where package = ? and id not in (select f1.id from file as f1, file as f2\n" + 
					"where f1.name=f2.name and f1.package = ? and f2.package = ?)";
					
			statement = connection.prepareStatement(query);
			statement.setLong(1, branchPackageId);
			statement.setLong(2, branchPackageId);
			statement.setLong(3, masterPackageId);
			ResultSet result = statement.executeQuery();
			
			PackageDao packDao = DAOFactory.getInstance().getPackageDao();

			while(result.next()) {
				file = new File(result.getString("name"), packDao.findByPrimaryKey(masterPackageId), result.getString("code"));
				save(file);
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
	}

	public void update(File file) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file SET id = ?, name = ?, package = ?,  code = ?, username = ? WHERE id=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, file.getId());
			statement.setString(2, file.getName());
			statement.setLong(3, file.getPackage().getId());
			statement.setString(4, file.getCode());
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
	public void restore(Long checkpointId) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file as F set code = CF.text, package = CF.package from ( select * from checkpointfile where checkpoint = ? ) as CF "
					+ "where CF.file = F.id";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, checkpointId);
			statement.executeUpdate();

			update = "update file as F set package = NULL where F.id NOT IN (select CF.file from checkpointfile as CF where CF.checkpoint = ? )";
			statement = connection.prepareStatement(update);
			statement.setLong(1, checkpointId);
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
	public void updateText(Long id, String text) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file SET code = ? WHERE id=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, text);
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

	public void delete(Long fileId) {
		Connection connection = dataSource.getConnection();
		try {
//			Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();
//			checkpointFileDao.deleteFromFile(fileId);

			String delete = "delete FROM file WHERE id = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, fileId);
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
	public void remove(Long id) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file SET package = NULL WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(update);
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
	public boolean exist(String name, Long packageId) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "select * FROM file WHERE name = ? AND package = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, name);
			statement.setLong(2, packageId);
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
	public List<File> findString(Long projectId, String text) {

		Connection connection = dataSource.getConnection();
		List<File> files = new LinkedList<>();
		try {
			File file;
			PreparedStatement statement;
			String query = "select * from file as F where F.package in (select p.id from package as p where p.project = ?)";
			statement = connection.prepareStatement(query);
			statement.setLong(1, projectId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				if (result.getString("code").contains(text)) {

					file = new File();
					file.setId(result.getLong("id"));
					file.setName(result.getString("name"));
					file.setCode(result.getString("code"));

					PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
					Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
					file.setPackage(pack);

					files.add(file);
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
		return files;
	}

	@Override
	public void enableWrite(String username, Long fileId) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file SET username = ? WHERE id=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, username);

			statement.setLong(2, fileId);

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
	public void disableWrite(String username) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file SET username = NULL WHERE username = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, username);

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
	public void disableWrite(String username, Long fileId) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file SET username = NULL WHERE username = ? and id = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, username);
			statement.setLong(2, fileId);
			
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
	public void rename(String name, Long fileId) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update file SET name = ? WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, name);
			statement.setLong(2, fileId);

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
}