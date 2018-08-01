package persistence.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import model.Message;
import model.Project;
import model.User;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.DAOFactory;
import persistence.dao.MessageDao;
import persistence.dao.ProjectDao;
import persistence.dao.UserDao;

public class MessageDaoJDBC implements MessageDao {

	private DataSource dataSource = null;

	public MessageDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(Message message) {
		Connection connection = dataSource.getConnection();
		try {
			Long id = IdBroker.getID(connection, "messageID");
			message.setId(id);
			String insert = "insert into message(id, project , text, username, date) values (?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, message.getId());
			statement.setLong(2, message.getProject().getId());
			statement.setString(3, message.getText());
			statement.setString(4, message.getUser().getUsername());
			statement.setTimestamp(5, message.getDate());
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

	public Message findByPrimaryKey(Long id) {
		Connection connection = dataSource.getConnection();
		Message message = null;
		try {
			PreparedStatement statement;
			String query = "select * from message where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				message = new Message();

				message.setId(result.getLong("id"));
				message.setText(result.getString("text"));
				message.setDate(result.getTimestamp("date"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				message.setUser(user);
/*
				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				Project project = projectDao.findByPrimaryKey(result.getLong("project"));
				message.setProject(project);*/
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
		return message;
	}

	public List<Message> find(Long ProjectID) {
		Connection connection = dataSource.getConnection();
		List<Message> messages = new LinkedList<>();
		try {
			Message message;
			PreparedStatement statement;
			String query = "select * from message where Project = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, ProjectID);
			ResultSet result = statement.executeQuery();

			while (result.next()) {

				message = new Message();

				message.setId(result.getLong("id"));
				message.setText(result.getString("text"));
				message.setDate(result.getTimestamp("date"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				message.setUser(user);
/*
				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				Project project = projectDao.findByPrimaryKey(result.getLong("project"));
				message.setProject(project);*/

				messages.add(message);
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
		return messages;

	}

	public Message findOlder(Long ProjectID, int firstMessage) {

		Connection connection = dataSource.getConnection();
		Message message = null;
		try {

			PreparedStatement statement;
			String query = "select * from message where Project = ? order by date desc";
			statement = connection.prepareStatement(query);
			statement.setLong(1, ProjectID);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				
				if (firstMessage > 0)
					firstMessage--;
				
				else {
					message = new Message();

					message.setId(result.getLong("id"));
					message.setText(result.getString("text"));
					message.setDate(result.getTimestamp("date"));

					UserDao userDao = DAOFactory.getInstance().getUserDao();
					User user = userDao.findByPrimaryKey(result.getString("username"));
					message.setUser(user);

					break;
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
		return message;

	}

	public List<Message> findLastMessage(Long ProjectID) {
		Connection connection = dataSource.getConnection();
		List<Message> messages = new LinkedList<>();
		try {
			Message message;
			PreparedStatement statement;
			String query = "select * from message where Project = ? order by date desc";
			statement = connection.prepareStatement(query);
			statement.setLong(1, ProjectID);
			ResultSet result = statement.executeQuery();

			int messageCounter = 10;

			while (result.next() && messageCounter > 0) {

				messageCounter--;
				message = new Message();

				message.setId(result.getLong("id"));
				message.setText(result.getString("text"));
				message.setDate(result.getTimestamp("date"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				message.setUser(user);

				messages.add(message);
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
		return messages;

	}

	public List<Message> findForTime(Timestamp date, Long projectID) {

		Connection connection = dataSource.getConnection();
		List<Message> messages = new LinkedList<>();
		date.setTime(date.getTime() - 5000);
		try {
			Message message;
			PreparedStatement statement;
			String query = "select * from message where Project = ? AND date >= ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, projectID);
			statement.setTimestamp(2, date);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				message = new Message();

				message.setId(result.getLong("id"));
				message.setText(result.getString("text"));
				message.setDate(result.getTimestamp("date"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				message.setUser(user);

				messages.add(message);
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
		return messages;
	}

	public List<Message> findAll() {
		Connection connection = dataSource.getConnection();
		List<Message> messages = new LinkedList<>();
		try {
			Message message;
			PreparedStatement statement;
			String query = "select * from message";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				message = new Message();

				message.setId(result.getLong("id"));
				message.setText(result.getString("text"));
				message.setDate(result.getTimestamp("date"));

				UserDao userDao = DAOFactory.getInstance().getUserDao();
				User user = userDao.findByPrimaryKey(result.getString("username"));
				message.setUser(user);

				ProjectDao projectDao = DAOFactory.getInstance().getProjectDao();
				Project project = projectDao.findByPrimaryKey(result.getLong("project"));
				message.setProject(project);

				messages.add(message);
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
		return messages;
	}

	public void update(Message message) {
		Connection connection = dataSource.getConnection();
		try {
			String update = "update message SET	id = ?, project = ?, text = ?, username = ?, date = ? WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, message.getId());
			statement.setLong(2, message.getProject().getId());
			statement.setString(3, message.getText());
			statement.setString(4, message.getUser().getUsername());
			statement.setTimestamp(5, message.getDate());
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

	public void delete(Message message) {
		Connection connection = dataSource.getConnection();
		try {
			String delete = "delete FROM message WHERE id = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, message.getId());
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
