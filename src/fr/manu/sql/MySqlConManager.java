package fr.manu.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import fr.manu.log.LogService;

public class MySqlConManager {

	
	/**
	 * 
	 */
	private static LogService log = new LogService(
			MySqlConManager.class.getName(),
			MySqlConManager.class.getSimpleName());
	
	
	/**
	 * DRIVER
	 */
	private final static String DRIVER = "com.mysql.jdbc.Driver"; 
	
	
	/**
	 * Connection
	 */
	private Connection connection = null;
	
	/**
	 * 
	 */
	private String database;
	
	
	/**
	 * 
	 * @param server
	 * @param port
	 * @param database
	 * @param user
	 * @param password
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public MySqlConManager(
			String server, 
			String port, 
			String database, 
			String user, 
			String password)
			throws ClassNotFoundException, SQLException {
		this.database = database;
		log.debug("Attempt to connect to "+database);
		Class.forName(DRIVER);
			connection = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + database, user,
				password);
		connection.setAutoCommit(true);
		log.debug("Connected to "+database);
	}
	
	/**
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		if (connection != null) {
			connection.commit();
		}
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		if (connection != null) {
			connection.rollback();
		}
	}
	
	/**
	 *
	 * @throws SQLException
	 */
	public void close() {
		if (connection != null) {
			try {
				connection.close();
				log.debug("Disconnected from "+database);
			} catch (SQLException e) {
				log.printStackTrace(e);
			}
		}
	}
	
}
