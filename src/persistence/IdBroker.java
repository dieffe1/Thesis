package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IdBroker {

	public static void init(Connection connection) {
		try {
			String[] names = { "checkpointID", "messageID", "fileID", "packageID", "collaboratorID", "checkpointFileID",
					"projectID" };

			PreparedStatement statement;
			for (int i = 0; i < names.length; i++) {
				String insert = "insert into idTable(name, value) values (?,?)";
				statement = connection.prepareStatement(insert);
				statement.setString(1, names[i]);
				statement.setLong(2, 1l);
				statement.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Long getID(Connection connection, String name) {
		Long value = null;
		try {
			String query = "select value from idTable where name = ? ";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, name);
			ResultSet result = statement.executeQuery();
			result.next();
			value = result.getLong("value");

			String updateQuery = "update idTable SET value = ? where name = ? ";
			statement = connection.prepareStatement(updateQuery);
			statement.setLong(1, value + 1);
			statement.setString(2, name);
			statement.executeUpdate();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			}
		}
		return value;
	}
}
