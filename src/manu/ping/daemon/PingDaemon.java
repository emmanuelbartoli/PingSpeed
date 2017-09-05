package manu.ping.daemon;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.lang3.SystemUtils;

import manu.log.LogService;
import manu.ping.Ping;
import manu.ping.PingDAO;
import manu.ping.daemon.Daemon.Status;
import manu.sql.MySqlConManager;

/**
 * 
 *  Ping daemon
 *  
 * @author Actarus78
 * @version 1.0 18/08/2017
 *
 */
public class PingDaemon {
	
	/**
	 * 
	 */
	private static LogService log = new LogService(
			PingDaemon.class.getName(),
			PingDaemon.class.getSimpleName());
	
	/**
	 * 
	 */
	private String confFilePath;
	
	/**
	 * 
	 */
	private static DateFormat MINUTE_FORMAT = new SimpleDateFormat("mm");
	
	/**
	 * 
	 * @param confFilePath
	 */
	public PingDaemon(String confFilePath) {
		this.confFilePath = confFilePath;
	} // end of PingDaemon
	
	/**
	 * 
	 */
	public void run()  {
		
		// The final return code
		int returnCode=0;
		
		// The mySql connection manager
		MySqlConManager connection = null;
		
		try {
			// Log
			log.info("");
			log.info("+-------------+");
			log.info("|    START    |");
			log.info("+-------------+");
			
			// Get the configuration file
			log.info("");
			log.info("Read the configuration file "+confFilePath);
			FileInputStream fileIntputStream 		= new FileInputStream(confFilePath);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileIntputStream);
			ResourceBundle resource 				= new PropertyResourceBundle(bufferedInputStream);
			
			// Read the parameters for connecting to the database
			String mysqlServer   = resource.getString("mysql.server");
			String mysqlPort     = resource.getString("mysql.port");
			String mysqlDatabase = resource.getString("mysql.database");
			String mysqlUser     = resource.getString("mysql.user");
			String mysqlPassword = resource.getString("mysql.password");
			
			// Log
			log.info("mysql.server   = " + mysqlServer);
			log.info("mysql.port     = " + mysqlPort);
			log.info("mysql.database = " + mysqlDatabase);
			log.info("mysql.user     = " + mysqlUser);
			
			// Close the streams
			bufferedInputStream.close();   
			fileIntputStream.close();
			
			// Database connection
			connection = new MySqlConManager(
						mysqlServer,
						mysqlPort,
						mysqlDatabase,
						mysqlUser,
						mysqlPassword);
			
			log.info("");
			log.info("Connected to the database.");
			
			
			// Check if the daemon is ready to start
			Daemon daemon = PingDAO.getDaemonParameters(connection.getConnection());
			// The daemon is ready to start
			if (daemon.getStatus() == Status.TO_START) {
				PingDAO.setDaemonStatus(connection.getConnection(), Status.STARTED);
				daemon.setStatus(Status.STARTED);
			} 
			// The daemon status does not comply with the start
			else {
				connection.close();
				log.error("The daemon is not ready to start, its status is different from TO_START !");
				System.exit(1);
			}
			
			//////////////
			//   PING   //
			/////////////
			log.info("Entering into the main loop.");
			Ping ping;
			boolean exit = false;
			boolean isTimeToPing;
			int curMinutes;
			int lastMinutes=-1;
			while (true) {
				
				// Read the daemon parameters in the database
				daemon = PingDAO.getDaemonParameters(connection.getConnection());

				// Decide to break the main loop or continue
				switch (daemon.getStatus()) {
					case STARTED :
						PingDAO.setDaemonStatus(connection.getConnection(), Status.RUNNING);
						daemon.setStatus(Status.RUNNING);
						exit = false;
						break;
					case RUNNING :
						exit = false;
						break;
					case TO_STOP :
						exit = true;
						PingDAO.setDaemonStatus(connection.getConnection(), Status.STOPPED);
						daemon.setStatus(Status.STOPPED);
						break;
					case STOPPED :
						exit = true;
						break;
					default : 
						exit = false;
						break;
				} // end of switch
				
				// Log
				//log.info(daemon);
				
				// Break the loop
				if (exit) {
					break;
				}
				

				// Test the result of division of the current minutes of the current date and the pings interval
				// If the result is 0, it's time to ping
				curMinutes = Integer.parseInt(MINUTE_FORMAT.format(new Date()));
				// To avoid to ping many times for the current minutes
				if (curMinutes != lastMinutes) {
					isTimeToPing =  (curMinutes%daemon.getPingInterval()==0);
					lastMinutes  = curMinutes;
				} else {
					isTimeToPing = false;
				}
				// log.info("Is time to ping ? "+isTimeToPing);
				
				if (isTimeToPing) {
					// Is the server reachable ?
					boolean isServerReachable = isReachable(daemon.getServerToPing(),80,3000);
					// log.info("Is "+daemon.getServerToPing()+" reachable ? "+isServerReachable);
					if (isServerReachable) {
						// Ping the server
						ping = ping(daemon.getServerToPing(),daemon.getPingPackets());
						ping.setStatus(Ping.Status.OK);
						log.info(ping);
					}
					else {
						log.info(daemon.getServerToPing()+" is not reachable !");
						ping = new Ping(daemon.getServerToPing(), -1, -1, -1, 100, Ping.Status.KO);
					}
					
					// Store the ping result into the database
					PingDAO.insertPing(connection.getConnection(), ping);
					
				} // end of if (isTimeToPing) 
				
				// Sleep in ms
			    // log.info("Sleep ...");
				Thread.sleep(1000*daemon.getSleepValue());
				
			} // end of while (true) 
			
			returnCode = 0;
			
		} catch (Exception e) {
			try {
				if (connection != null) {
					PingDAO.setDaemonStatus(connection.getConnection(), Status.ERROR);
				}
			} catch (SQLException e1) {
				log.printStackTrace(e1);
			}
			log.printStackTrace(e);
			returnCode = 1;
		} 
		finally {
			// Close the database connection
			if (connection != null) {
				connection.close();
			}
			log.info("Disconnected from the database.");
			log.info("");
			log.info("+-------------+");
			log.info("|     STOP    |");
			log.info("+-------------+");
			log.info("");
			log.info("Return code = "+returnCode);
			log.info("");
			
			System.exit(returnCode);
		}
		
	} // end of run
	
	/**
	 * Is a server reachable ?
	 * 
	 * @param addr
	 * @param openPort
	 * @param timeOutMillis
	 * @return
	 */
	private boolean isReachable(String addr, int openPort, int timeOutMillis) {
	    // Any Open port on other machine
	    // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
	    try {
	        try (Socket soc = new Socket()) {
	            soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
	        }
	        return true;
	    } catch (IOException ex) {
	        return false;
	    }
	}
	
	/**
	 * 
	 * @param addr
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isReachable(String addr) {
		try {
			// make a URL to a known source
			URL url = new URL("http://"+addr);

			// open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.getContent();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	
	/**
	 * Ping a server with the system command
	 * 
	 * @param serverToPing
	 * @param pingsNumber
	 * @throws IOException
	 */
	private Ping ping(String serverToPing, int pingsNumber) throws Exception {

		Ping ping      = new Ping(serverToPing);
		String line    = null;
		String command = null;

		if (SystemUtils.IS_OS_LINUX) {
			command = "ping -c " + pingsNumber + " " + serverToPing;
		} else if (SystemUtils.IS_OS_WINDOWS) {
			command = "ping -n " + pingsNumber + " " + serverToPing;
		} else {
			throw new Exception("OS NOT SUPPORTED :-( !");
		}

		// Run the command using the Runtime exec method:
		Process p = Runtime.getRuntime().exec(command);

		// Read the command result
		BufferedReader reader      = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		// Get the ping statistics
		StringTokenizer st;
		while ((line = reader.readLine()) != null) {
			// For Linux
			if (SystemUtils.IS_OS_LINUX) {
				// Get the minimum,maximum and average values
				// rtt min/avg/max/mdev = 8.903/9.715/10.449/0.557 ms
				if (line.contains("min/avg/max")){
					line = line
							.replace("rtt min/avg/max/mdev = ", "")
							.replace("ms", "")
							.replace("s", "")
							.replace(" ", "");
					st = new StringTokenizer(line,"/");
					ping.setMinimum((int)Math.round(Double.parseDouble(st.nextToken())));
					ping.setAverage((int)Math.round(Double.parseDouble(st.nextToken())));
					ping.setMaximum((int)Math.round(Double.parseDouble(st.nextToken())));
				}
				
				// Get the loss value
				// Example of line (Raspbian  4.9.35)
				// 10 packets transmitted, 10 received, 0% packet loss, time 9014ms
				else if (line.contains("packet loss")) {
					ping.setLoss((int)Math.round(Double.parseDouble(
							line
							.substring(line.indexOf("received,")+"received,".length(), line.indexOf("%"))
							.replace(" ",""))));			
				}
			} // end of if (SystemUtils.IS_OS_LINUX)
			
			// For Windows
			else if (SystemUtils.IS_OS_WINDOWS) {
				// Get the minimum,maximum and average values
				// Example of line (Windows 10)
				// Minimum = 20ms, Maximum = 62ms, Moyenne = 30ms
				if (    (line.contains("Minimum ="))
					 && (line.contains("Maximum ="))
					 && (line.contains("Moyenne ="))){

					// Remove all the unnecessary characters,let only numbers separated by ,
					line = line
							.replace("Minimum", "")
							.replace("Maximum", "")
							.replace("Moyenne", "")
							.replace("=", "")
							.replace("ms", "")
							.replace("s", "")
							.replace(" ", "");
					st = new StringTokenizer(line,",");
					ping.setMinimum(Integer.parseInt(st.nextToken()));
					ping.setMaximum(Integer.parseInt(st.nextToken()));
					ping.setAverage(Integer.parseInt(st.nextToken()));
				}
				
				// Get the loss value
				// Example of line
				// Paquets: envoyes = 10, re‡us = 10, perdus = 0 (perte 0%),
				else if (line.contains("perte")) {
					ping.setLoss((int)Math.round(Double.parseDouble(
							line
							.substring(line.indexOf("(")+1, line.indexOf(")"))
							.replace("perte ","")
							.replace("%","")
							.replace(" ","")
							.replace(",","."))));
				}
			} // end of if (SystemUtils.IS_OS_WINDOWS) 
		}// end of while()
		
		// Close the reader
		reader.close();

		// Read any errors from the attempted command
		while ((line = errorReader.readLine()) != null) {
		  log.error(line);
		}
		errorReader.close();

		// With IOUtils and French charset for French Windows
		// s = IOUtils.toString(p.getInputStream(),"Cp850"); log.info(s); 
		// IOUtils.toString(p.getErrorStream(), "Cp850"); log.info(s);

		return ping;

	} // end of ping
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args)  {
		
		// Read the parameters
		String confFilePath = args[0];
		
		// Run the daemon
		new PingDaemon(confFilePath).run();
	
	} // end of main

}
