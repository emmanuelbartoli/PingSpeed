package manu.speed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *  MY SQL statements for speedtest.
 *  
 * @author actarus78 01/09/2017
 * 
 * TABLES
 *
 * CREATE TABLE SPEEDTEST_RESULT (
 * SPD_DOWNLOAD int(10) NOT NULL COMMENT 'Download speed in Mbits/s',
 * SPD_UPLOAD int(10) NOT NULL COMMENT 'Upload speed in Mbits/s',
 * SPD_DATE datetime NOT NULL COMMENT 'Date'
 * ) ;
 *
 */
public class SpeedDAO {

	/**
	 * INSERT_SPEED
	 */
	private final static String INSERT_SPEED =
			"insert into SPEEDTEST_RESULT(SPD_DOWNLOAD,SPD_UPLOAD,SPD_DATE) "+
	        "values (?,?,NOW())";
	
	/**
	 * INSERT_SPEED
	 */
	private final static String GET_SPEEDS =
			"select SPD_DOWNLOAD,SPD_UPLOAD,DATE_FORMAT(SPD_DATE,'%d %b %H:%i') " +
	        "from SPEEDTEST_RESULT " +
	        "where SPD_DATE >= DATE_SUB(NOW(), INTERVAL #INTERVAL#) "+
	        "order by SPD_DATE asc";
	
	/**
	 * 
	 * @param connection
	 * @param speed
	 * @throws SQLException
	 */
	public static void insertSpeed(Connection connection,Speed speed)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		pstmt = connection.prepareStatement(INSERT_SPEED);
			pstmt.setFloat(1,speed.getDownload());
			pstmt.setFloat(2,speed.getUpload());
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

	} // end of insertSpeed
	
	/**
	 * 
	 * @param connection
	 * @param interval
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<Speed> getSpeeds(Connection connection,String interval) throws SQLException {
		ArrayList<Speed> speeds = new ArrayList<Speed>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(GET_SPEEDS.replace("#INTERVAL#", interval));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				speeds.add( new Speed(
						rs.getFloat(1), 
						rs.getFloat(2),
						rs.getString(3)));
			}
			return speeds;
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
	} // getSpeeds
}
