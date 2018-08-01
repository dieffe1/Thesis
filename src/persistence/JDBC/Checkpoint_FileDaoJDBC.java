package persistence.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import model.Checkpoint;
import model.Checkpoint_File;
import model.File;
import model.Package;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.DAOFactory;
import persistence.dao.CheckpointDao;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.FileDao;
import persistence.dao.PackageDao;
import persistence.dao.UserDao;

public class Checkpoint_FileDaoJDBC implements Checkpoint_FileDao {

	private DataSource dataSource = null;

	public Checkpoint_FileDaoJDBC(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	@Override
	public void save(Checkpoint_File checkpointFile) {

		Connection connection = dataSource.getConnection();
		try {
			Long id = IdBroker.getID(connection, "checkpointFileID");
			checkpointFile.setId(id); 
			String insert = "insert into checkpointFile(id, text, checkpoint, file, package, date, creator,description) values (?,?,?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, checkpointFile.getId());
			statement.setString(2, checkpointFile.getText());

			if(checkpointFile.getCheckpoint() != null)
				statement.setLong(3, checkpointFile.getCheckpoint().getId());
			else 
				statement.setNull(3, 0);

			if(checkpointFile.getFile() != null)
				statement.setLong(4, checkpointFile.getFile().getId());
			else
				statement.setNull(4, 0);

			statement.setLong(5, checkpointFile.getPackage().getId());
			statement.setTimestamp(6, checkpointFile.getDate());
			statement.setString(7, checkpointFile.getCreator().getUsername());
			statement.setString(8, checkpointFile.getDescription());

			statement.executeUpdate();
		}  catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
	public Checkpoint_File findByPrimaryKey(Long id) {
		Connection connection = dataSource.getConnection();
		Checkpoint_File checkpointFile = null;

		try {
			PreparedStatement statement;
			String query = "select * from checkpointFile where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				checkpointFile = new Checkpoint_File();
				checkpointFile.setId(result.getLong("id"));
				checkpointFile.setText((String) result.getObject("text"));
				checkpointFile.setDate(result.getTimestamp("date"));

				FileDao fileDao = DAOFactory.getInstance().getFileDao();
				File file = fileDao.findByPrimaryKey(result.getLong("file"));
				checkpointFile.setFile(file);

				CheckpointDao checkpointDao = DAOFactory.getInstance().getCheckpointDao();
				Checkpoint checkpoint = checkpointDao.findByPrimaryKey(result.getLong("checkpoint"));
				checkpointFile.setCheckpoint(checkpoint);

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				checkpointFile.setPackage(pack);

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				checkpointFile.setCreator(userDao.findByPrimaryKey(result.getString("creator")));
				
				checkpointFile.setDescription(result.getString("description"));
			}
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
		return checkpointFile;
	}

	@Override
	public HashMap<Long, Checkpoint_File> find(Long checkpointID) {
		Connection connection = dataSource.getConnection();
		HashMap<Long, Checkpoint_File> checkpointsFile = new HashMap<>();

		try {
			Checkpoint_File checkpointFile;
			PreparedStatement statement;
			String query = "select * from checkpointFile where checkpoint = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, checkpointID);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				checkpointFile = new Checkpoint_File();
				checkpointFile.setId(result.getLong("id"));
				checkpointFile.setText((String) result.getObject("text"));
				checkpointFile.setDate(result.getTimestamp("date"));

				FileDao fileDao = DAOFactory.getInstance().getFileDao();
				File file = fileDao.findByPrimaryKey(result.getLong("file"));
				checkpointFile.setFile(file);

				CheckpointDao checkpointDao = DAOFactory.getInstance().getCheckpointDao();
				Checkpoint checkpoint = checkpointDao.findByPrimaryKey(result.getLong("checkpoint"));
				checkpointFile.setCheckpoint(checkpoint);

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				checkpointFile.setPackage(pack);

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				checkpointFile.setCreator(userDao.findByPrimaryKey(result.getString("creator")));
				
				checkpointFile.setDescription(result.getString("description"));
				
				checkpointsFile.put(file.getId(), checkpointFile);
			}
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
		return checkpointsFile;
	}


	@Override
	public HashMap<Long, Checkpoint_File> findAll() {
		Connection connection = dataSource.getConnection();
		HashMap<Long, Checkpoint_File> checkpointsFile = new HashMap<>();

		try {
			Checkpoint_File checkpointFile;
			PreparedStatement statement;
			String query = "select * from checkpointFile";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				checkpointFile = new Checkpoint_File();
				checkpointFile.setId(result.getLong("id"));
				checkpointFile.setText((String) result.getObject("text"));
				checkpointFile.setDate(result.getTimestamp("date"));

				FileDao fileDao = DAOFactory.getInstance().getFileDao();
				File file = fileDao.findByPrimaryKey(result.getLong("file"));
				checkpointFile.setFile(file);

				CheckpointDao checkpointDao = DAOFactory.getInstance().getCheckpointDao();
				Checkpoint checkpoint = checkpointDao.findByPrimaryKey(result.getLong("checkpoint"));
				checkpointFile.setCheckpoint(checkpoint);

				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				Package pack = packageDao.findByPrimaryKey(result.getLong("package"));
				checkpointFile.setPackage(pack);

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				checkpointFile.setCreator(userDao.findByPrimaryKey(result.getString("creator")));

				checkpointFile.setDescription(result.getString("description"));
				
				checkpointsFile.put(file.getId(),checkpointFile);
			}
		}  catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
		return checkpointsFile;
	}

	@Override
	public void update(Checkpoint_File checkpointFile) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update checkpointFile SET id = ?, text = ?, checkpoint = ?, file = ? WHERE id=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, checkpointFile.getId());
			statement.setString(2, checkpointFile.getText());
			statement.setLong(3, checkpointFile.getCheckpoint().getId());
			statement.setLong(4, checkpointFile.getFile().getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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

	public void delete(Long checkpointId) {
		Connection connection = dataSource.getConnection();
		try {
			String delete = "delete FROM checkpointFile WHERE checkpoint = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, checkpointId);
			statement.executeUpdate();
		}  catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
	
	public void deleteFromFile(Long fileId) {
		Connection connection = dataSource.getConnection();
		try {
			String delete = "delete FROM checkpointFile WHERE file = ?";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, fileId);
			statement.executeUpdate();
		}  catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
	
	public void deleteFromPackage(Long packageId) {
		Connection connection = dataSource.getConnection();
		try {
			String delete = "delete FROM checkpointFile WHERE package = ?";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, packageId);
			statement.executeUpdate();
		}  catch (SQLException e) { 
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
	public List<File> findString(Long fileId, String text) {

		Connection connection = dataSource.getConnection();
		List<File> files = new LinkedList<>();
		try {
			File file;
			PreparedStatement statement;
			String query = "select * from checkpointFile where file = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, fileId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				if(result.getString("text").contains(text)) {
					file = new File();
					file.setId(fileId);
					file.setName(result.getString("description"));		
					file.setCode(result.getString("text"));

					files.add(file);
				}
			}
		}  catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
	public List<File> findByFileId(Long fileId) {
		Connection connection = dataSource.getConnection();
		List<File> files = new LinkedList<>();
		try {
			File file;
			PreparedStatement statement;
			String query = "select * from checkpointFile where file=?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, fileId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
					file = new File();
					file.setId(result.getLong("id"));
					file.setName(result.getString("description"));		
					file.setCode(result.getString("text"));

					files.add(file);
			}
		}  catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
	
	public HashMap<Long, File> findLast(Long projectId)
	{
		Connection connection = dataSource.getConnection();
		HashMap<Long, File> texts = new HashMap<>();
		try {
			PreparedStatement statement;
			String query = "select text,file,package from checkpointFile where checkpoint IN (select MAX(id) as ID from checkpoints where project = ?)";
			statement = connection.prepareStatement(query);
			statement.setLong(1, projectId);
			ResultSet result = statement.executeQuery();
			while(result.next())
			{
				File file = new File();
				file.setId(result.getLong("file"));
				file.setCode(result.getString("text"));
				
				PackageDao packageDao = DAOFactory.getInstance().getPackageDao();
				file.setPackage(packageDao.findByPrimaryKey(result.getLong("package")));
				
				texts.put(file.getId(), file);
			}
		}  catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
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
		return texts;	
	}
}