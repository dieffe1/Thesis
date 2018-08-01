package persistence.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.Checkpoint;
import model.Project;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.DAOFactory;
import persistence.dao.CheckpointDao;
import persistence.dao.Checkpoint_FileDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

public class CheckpointDaoJDBC implements CheckpointDao {

	private DataSource dataSource = null;

	public CheckpointDaoJDBC(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public void save(Checkpoint checkpoint) {
		Connection connection = dataSource.getConnection();
		try {
			Long id = IdBroker.getID(connection, "checkpointID");
			checkpoint.setId(id); 
			String insert = "insert into checkpoints(id, description, project, username, date) values (?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, checkpoint.getId());
			statement.setString(2, checkpoint.getDescription());
			statement.setLong(3, checkpoint.getProject().getId());
			statement.setString(4, checkpoint.getCreator().getUsername());
			statement.setTimestamp(5, checkpoint.getDate());
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

	public Checkpoint findByPrimaryKey(Long id) {
		Connection connection = dataSource.getConnection();
		Checkpoint checkpoint = null;
		try {
			PreparedStatement statement;
			String query = "select * from checkpoints where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				checkpoint = new Checkpoint();
				checkpoint.setId(result.getLong("id"));
				checkpoint.setDescription(result.getString("description"));
				checkpoint.setDate(result.getTimestamp("date"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				checkpoint.setCreator(userDao.findByPrimaryKey(result.getString("username")));

				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				Project project = projectDao.findByPrimaryKey(result.getLong("project"));
				checkpoint.setProject(project);

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
		return checkpoint;
	}

	@Override
	public List<Checkpoint> find(Long projectID)
	{
		Connection connection = dataSource.getConnection();
		List<Checkpoint> checkpoints = new LinkedList<>();
		try {
			Checkpoint checkpoint;
			PreparedStatement statement;
			String query = "select * from checkpoints where project = ? ORDER BY id DESC";
			statement = connection.prepareStatement(query);
			statement.setLong(1, projectID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				checkpoint = new Checkpoint();
				checkpoint.setId(result.getLong("id"));
				checkpoint.setDescription(result.getString("description"));	
				checkpoint.setDate(result.getTimestamp("date"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				checkpoint.setCreator(userDao.findByPrimaryKey(result.getString("username")));

				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				Project project = projectDao.findByPrimaryKey(result.getLong("project"));
				checkpoint.setProject(project);

				checkpoints.add(checkpoint);
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}	 finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return checkpoints;
	}

	public void update(Checkpoint checkpoint) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update checkpoints SET id = ?, description = ?, project = ?, date = ?, username = ? WHERE id=?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, checkpoint.getId());
			statement.setString(2, checkpoint.getDescription());
			statement.setLong(3, checkpoint.getProject().getId());
			statement.setTimestamp(4, checkpoint.getDate());
			statement.setString(5, checkpoint.getCreator().getUsername());
			
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
//			Checkpoint_FileDao checkpointFileDao = DAOFactory.getInstance().getCheckpointFileDao();
//			checkpointFileDao.delete(checkpointId);

			String delete = "delete FROM checkpoints WHERE id = ? ";
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
}
