package fr.manu.ping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import fr.manu.log.LogService;
import fr.manu.ping.daemon.Daemon;
import fr.manu.ping.daemon.Daemon.Status;

/**
 * 
 * MY SQL statements for ping toolkit.
 * 
 * @author actarus78
 * 
 * 
 * TABLES 
 * 
 * CREATE TABLE `DAEMON_PARAMETERS` (
 * `DAP_SERVER_TO_PING` varchar(20) NOT NULL COMMENT 'Server to ping',
 * `DAP_PING_PACKETS` int(2) NOT NULL COMMENT 'Number of ping packets',
 * `DAP_PING_INTERVAL` int(2) NOT NULL COMMENT 'Interval in minutes between two ping packets',
 * `DAP_SLEEP` int(2) NOT NULL COMMENT 'Sleep value in seconds for the ping daemon',
 * `DAP_STATUS` varchar(10) NOT NULL COMMENT 'Daemon status : TO_START,STARTED,RUNNING,TO_STOP,STOPPED'
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 * 
 * CREATE TABLE `PING_RESULT` (
 * `PIR_SERVER` varchar(20) NOT NULL COMMENT 'Pinged server',
 * `PIR_MINIMUM` int(10) NOT NULL COMMENT 'Maximum ping value in ms',
 * `PIR_MAXIMUM` int(10) NOT NULL COMMENT 'Minimum ping value in ms',
 * `PIR_AVERAGE` int(10) NOT NULL COMMENT 'Average ping value in ms',
 * `PIR_LOSS` int(3) NOT NULL COMMENT '% loss ',
 * `PIR_DATE` datetime NOT NULL COMMENT 'Date',
 * `PIR_STATUS` varchar(2) NOT NULL COMMENT 'Status : OK or KO'
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8
 *
 */
public class PingDAO {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static LogService log = new LogService(
			PingDAO.class.getName(),
			PingDAO.class.getSimpleName());
	
	
	/**
	 * GET_DAEMON_PARAMETERS
	 */
	private final static String GET_DAEMON_PARAMETERS = 
			"select DAP_SERVER_TO_PING,DAP_PING_PACKETS,DAP_PING_INTERVAL,DAP_SLEEP,DAP_STATUS from DAEMON_PARAMETERS";
	
	/**
	 * SET_DAEMON_STATUS
	 */
	private final static String SET_DAEMON_STATUS =
			"update DAEMON_PARAMETERS set DAP_STATUS=?";
	
	/**
	 * INSERT_PING
	 */
	private final static String INSERT_PING =
			"insert into PING_RESULT(PIR_SERVER,PIR_MINIMUM,PIR_MAXIMUM,PIR_AVERAGE,PIR_LOSS,PIR_DATE,PIR_STATUS) "+
	        "values (?,?,?,?,?,NOW(),?)";
	/**
	 * GET_PINGS
	 * '%d/%m/%Y %H:%i:%s
	 * 
	 */
	private final static String GET_PINGS = 
			"select PIR_SERVER,PIR_MINIMUM,PIR_MAXIMUM,PIR_AVERAGE,PIR_LOSS,DATE_FORMAT(PIR_DATE,'%d %b %H:%i'),PIR_STATUS "+
			"from PING_RESULT " + 
		    "where PIR_DATE >= DATE_SUB(NOW(), INTERVAL #INTERVAL#) "+
	        "order by PIR_DATE asc";
	/**
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static Daemon getDaemonParameters(Connection connection) throws SQLException {
		Daemon daemon = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(GET_DAEMON_PARAMETERS);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				daemon = new Daemon(
						rs.getString(1), 
						rs.getInt(2), 
						rs.getInt(3), 
						rs.getInt(4), 
						getEnumFromString(Daemon.Status.class,rs.getString(5)));
			}
			return daemon;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException uncatched) {
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException uncatched) {
				}
		}

	} // end of getDaemonParameters
	
	/**
	 * 
	 * @param connection
	 * @param status
	 * @throws SQLException
	 */
	public static void setDaemonStatus(Connection connection,Status status)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		pstmt = connection.prepareStatement(SET_DAEMON_STATUS);
			pstmt.setString(1,status.name());
			pstmt.executeUpdate();
	
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException uncatched) {

				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException uncatched) {
				}
		}

	} // end of setDaemonStatus
	
	/**
	 * 
	 * @param connection
	 * @param ping
	 * @throws SQLException
	 */
	public static void insertPing(Connection connection,Ping ping)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		pstmt = connection.prepareStatement(INSERT_PING);
			pstmt.setString(1,ping.getServer());
			pstmt.setInt   (2,ping.getMinimum());
			pstmt.setInt   (3,ping.getMaximum());
			pstmt.setInt   (4,ping.getAverage());
			pstmt.setInt   (5,ping.getLoss());
			pstmt.setString(6,ping.getStatus().name());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException uncatched) {

				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException uncatched) {
				}
		}

	} // end of insertPing
	
	/**
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<Ping> getPings(Connection connection,String interval) throws SQLException {
		ArrayList<Ping> pings = new ArrayList<Ping>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(GET_PINGS.replace("#INTERVAL#", interval));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				pings.add( new Ping(
						rs.getString(1), 
						rs.getInt(2), 
						rs.getInt(3), 
						rs.getInt(4), 
						rs.getInt(5),
						rs.getString(6),
						getEnumFromString(Ping.Status.class,rs.getString(7))));
			}
			return pings;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException uncatched) {
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException uncatched) {
				}
		}

	} // getPings
	
	/**
	 * 
	 * @param c
	 * @param string
	 * @return
	 */
	private static <T extends Enum<T>> T getEnumFromString(
			Class<T> c,
			String string) {
		if (c != null && string != null) {
			try {
				return Enum.valueOf(c, string.trim().toUpperCase());
			} catch (IllegalArgumentException ex) {
				return null;
			}
		} else {
			return null;
		}
	} // end of getEnumFromString()
	
}
