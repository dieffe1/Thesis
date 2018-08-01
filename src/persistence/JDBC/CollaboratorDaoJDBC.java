package persistence.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import model.Collaborator;
import model.Project;
import model.User;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.DAOFactory;
import persistence.dao.CollaboratorDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

public class CollaboratorDaoJDBC implements CollaboratorDao {

	private DataSource dataSource = null;

	public CollaboratorDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(Collaborator collaborator) {
		if (collaborator.getUser().getUsername().equals(collaborator.getProject().getCreator().getUsername()))
			return;

		Connection connection = dataSource.getConnection();
		try {
			collaborator.setId(IdBroker.getID(connection, "collaboratorID"));
			String insert = "insert into collaborator(id,username, project, status) values (?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, collaborator.getId());
			statement.setString(2, collaborator.getUser().getUsername());
			statement.setLong(3, collaborator.getProject().getId());
			statement.setBoolean(4, collaborator.getStatus());
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
	public HashMap<Long, Project> find(String username) {
		Connection connection = dataSource.getConnection();
		HashMap<Long, Project> projects = new HashMap<>();
		try {
			PreparedStatement statement;
			String query = "select * from collaborator where username = ? AND status = 'true'";
			statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			while (result.next()) {

				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				Project project = projectDao.findByPrimaryKey(result.getLong("project"));
				if(project.getMaster() == null)
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

	public Project findProject(String username, String projectName) {
		Connection connection = dataSource.getConnection();
		Project project = null;
		try {
			PreparedStatement statement;
			String query = "select * from collaborator where username = ? AND status = 'true' AND project = (select id from project where name = ?)";
			statement = connection.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, projectName);
			ResultSet result = statement.executeQuery();
			while (result.next()) {

				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				project = projectDao.findByPrimaryKey(result.getLong("project"));
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
	public List<User> find(Long projectID) {
		Connection connection = dataSource.getConnection();
		List<User> users = new LinkedList<>();
		try {
			PreparedStatement statement;
			String query = "select * from collaborator where project = ? AND status = 'true'";
			statement = connection.prepareStatement(query);
			statement.setLong(1, projectID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				users.add(user);
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
		return users;
	}

	@Override
	public List<Collaborator> findCollaborator(Long projectID) {
		Connection connection = dataSource.getConnection();
		List<Collaborator> users = new LinkedList<>();
		try {
			PreparedStatement statement;
			String query = "select * from collaborator where project = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, projectID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Collaborator collaborator = new Collaborator();

				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				collaborator.setProject(projectDao.findByPrimaryKey(projectID));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				collaborator.setUser(userDao.findByPrimaryKey(result.getString("username")));

				collaborator.setStatus(result.getBoolean("status"));

				users.add(collaborator);
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
		return users;
	}

	@Override
	public List<Project> findPendingRequest(String username) {
		Connection connection = dataSource.getConnection();
		List<Project> projects = new ArrayList<>();
		try {
			PreparedStatement statement;
			String query = "select * from collaborator where username = ? AND status = 'false'";
			statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			while (result.next()) {

				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				Project project = projectDao.findByPrimaryKey(result.getLong("project"));
				projects.add(project);
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

	@Override
	public Collaborator findByPrimaryKey(String username, Long project) {
		Connection connection = dataSource.getConnection();
		Collaborator collaborator = null;
		try {
			PreparedStatement statement;
			String query = "select * from collaborator where username = ? AND project = ? ";
			statement = connection.prepareStatement(query);
			statement.setString(1, username);
			statement.setLong(2, project);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				collaborator = new Collaborator();
				collaborator.setId(result.getLong("id"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				collaborator.setUser(user);

				ProjectDao ProjectDao = DAOFactory.getInstance().getProjectDao();
				Project Project = ProjectDao.findByPrimaryKey(result.getLong("project"));
				collaborator.setProject(Project);

				collaborator.setStatus(result.getBoolean("status"));
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
		return collaborator;
	}

	public List<Collaborator> findAll() {
		Connection connection = dataSource.getConnection();
		List<Collaborator> collaborators = new LinkedList<>();
		try {
			Collaborator collaborator;
			PreparedStatement statement;
			String query = "select * from collaborator";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				collaborator = new Collaborator();
				collaborator.setId(result.getLong("id"));
				collaborator.setStatus(result.getBoolean("status"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				collaborator.setUser(user);

				ProjectDao ProjectDao = DAOFactory.getInstance().getProjectDao();
				Project Project = ProjectDao.findByPrimaryKey(result.getLong("Project"));
				collaborator.setProject(Project);

				collaborators.add(collaborator);
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
		return collaborators;
	}

	public void updateStatus(String username, Long projectId) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update collaborator SET status = 'true' WHERE username = ? AND project = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, username);
			statement.setLong(2, projectId);
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

	public void update(Collaborator collaborator) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update collaborator SET id= ?, username = ?, Project = ? WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, collaborator.getId());
			statement.setString(2, collaborator.getUser().getUsername());
			statement.setLong(3, collaborator.getProject().getId());
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

	public void delete(String username, Long projectId) {
		Connection connection = dataSource.getConnection();
		try {
			String delete = "delete FROM collaborator WHERE username = ? AND project = ?";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setString(1, username);
			statement.setLong(2, projectId);
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
