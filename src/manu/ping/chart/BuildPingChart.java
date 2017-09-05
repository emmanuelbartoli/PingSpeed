package manu.ping.chart;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import it.sauronsoftware.ftp4j.FTPClient;
import manu.log.LogService;
import manu.ping.Ping;
import manu.ping.PingDAO;
import manu.sql.MySqlConManager;

/**
 * 
 * Draw a ping chart
 * 
 * @author Actarus78
 * @version 1.0 18/08/2017
 *
 */
public class BuildPingChart {

	/**
	 * 
	 */
	private static LogService log = new LogService(
			BuildPingChart.class.getName(),
			BuildPingChart.class.getSimpleName());
	
	/**
	 * 
	 */
	private static String MBPS="Mbps";
	
	/**
	 * 
	 */
	private String confFilePath;
	
	/**
	 * 
	 */
	private String MAIN_HTML="manu/ping/chart/ping.html";
	
	/**
	 * 
	 */
	private String PING_JS="manu/ping/chart/ping.js";
	
	/**
	 * 
	 */
	private String BODY_HTML="manu/ping/chart/body.html";
	
	/**
	 * Intervals displayed in the chart
	 */
	private static enum Interval {
		TWENTY_FOUR_HOURS("24 hours", "24 HOUR"), 
		THREE_DAYS("three days", "3 DAY"), 
		ONE_WEEK("one week", "1 WEEK"), 
		TWO_WEEKS("two weeks", "2 WEEK"), 
		ONE_MONTH("one month","1 MONTH"), 
		THREE_MONTHS("three months","3 MONTH"), 
		//SIX_MONTHS("six months", "6 MONTH"), 
		//ONE_YEAR("one year", "1 YEAR")
		;
		private String label;
		private String sql;
		private Interval(String label, String sql) {
			this.label = label;
			this.sql = sql;
		}
		public String getLabel() {
			return label;
		}
		public String getSql() {
			return sql;
		}
	} // end of Interval
	

	/**
	 * 
	 * @param confFilePath
	 */
	public BuildPingChart(String confFilePath) {
		this.confFilePath = confFilePath;
	}
	
	/**
	 * 
	 */
	public void execute() {
		
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
			
			// Ping html chart file
			String pingFilePath  = resource.getString("ping.html.chart.file");
			
			// Ftp attributes
			String ftpServer   = resource.getString("ftp.server");
			String ftpUser     = resource.getString("ftp.user");
			String ftpPassword = resource.getString("ftp.password");
			
			// Log
			log.info("ftp.server     = " + ftpServer);
			log.info("ftp.password   = " + ftpUser);
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
			
			log.info("Connected to the database.");
			
			// Draw charts for the all intervals
			ArrayList<Ping> pings;
			StringBuffer minMaxAvg;
			StringBuffer loss;
			String minMaxAvgHtml;
			String lossHtml;
	        String mainHtml;
	        String javascript;
	        String bodyHtml;
	        StringBuffer javascriptTotal = new StringBuffer();
	        StringBuffer bodyHtmlTotal   = new StringBuffer();
	        
	        log.info("Build the html Google combo chart for presenting the results...");
			for (Interval interval : Interval.values()) {
			
				log.info("Build the chart for "+interval.getLabel());
				log.info(getJVM());
				
				//////////////////////////////////////////
				// Read the ping list from the database //
				//////////////////////////////////////////
				pings 	  = PingDAO.getPings(connection.getConnection(),interval.getSql());
				minMaxAvg = new StringBuffer();
				loss      = new StringBuffer();
				
				/////////////////////////////////////////////////////////////////////////////
				// Build the Google combo chart for presenting the results                 //
				// https://developers.google.com/chart/interactive/docs/gallery/combochart //
				/////////////////////////////////////////////////////////////////////////////
				
				// Javascript Code for the ping values
				for (Ping ping:pings) {
					// The min/max/average values
					minMaxAvg
					.append("[")
					.append("'").append(ping.getDate()).append("'").append(",")
					.append(ping.getMinimum()).append(",")
					.append(ping.getMaximum()).append(",")
					.append(ping.getAverage())
					.append("]").append(",");
					
					// The packet loss values
					loss
					.append("[")
					.append("'").append(ping.getDate()).append("'").append(",")
					.append(ping.getLoss())
					.append("]").append(",");
				}
				minMaxAvgHtml = minMaxAvg.toString();
				lossHtml      = loss.toString();
				
		        // The javascript template
		        javascript = getTemplate(PING_JS);
		        
		        // The html body
		        bodyHtml = getTemplate(BODY_HTML);
		        
		        // Update the javascrit
		        // https://www.textfixer.com/html/compress-html-compression.php
		        // http://jsbeautifier.org/
		        javascript = javascript
		        		.replace("#TYPE#"		    , interval.name())
		     			.replace("#MINMAXAVG#"		, minMaxAvgHtml)
		     			.replace("#LOSS#"     		, lossHtml)
		     			.replace("#SERVER_TO_PING#" , pings.get(pings.size()-1).getServer() + 
		     					                      " ("+interval.getLabel()+")");
		        
		        // Add the javascript to the total
		        javascriptTotal.append(javascript);
		        
		        // Update the html body
		        bodyHtml = bodyHtml.replace("#TYPE#", interval.name());
		        
		        // Add the body html to the total
		        bodyHtmlTotal.append(bodyHtml);
	        
			} // end of for (Interval interval : Interval.values());
			
			/////////////////////////////////////////////////////
			// Read the connection speed in the box home page //
			////////////////////////////////////////////////////
			URL url = new URL("http://monmodem");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			int idx1 ;
			int idx2 ;
			String speed="";
			//Lines to find
			//<tr class="fontestableau1">
			//  <td align="right">Débit Descendant maximum :&nbsp;</td>
			//  <td align="left"><script type="text/javascript">document.write("200 Mbps");</script></td>  ====> D/L HERE
			//</tr>
			//<tr class="fontestableau1">
			//  <td align="right">Débit Montant maximum :&nbsp;</td>
			//  <td align="left"><script type="text/javascript">document.write("20 Mbps");</script></td>   ====> U/L HERE
			//</tr>
			// Target result : xxx Mbps / xxx Mbps
			while ((line = reader.readLine()) != null)
				if (line.contains(MBPS)) {
					//log.info(line);
					idx1 = line.indexOf(MBPS);
					idx2 = line.substring(0, idx1).lastIndexOf("\"");
					speed = speed + line.substring(idx2+1,idx1+MBPS.length())+" / ";
				}
			reader.close();
			// Remove the last " / "
			speed = speed.substring(0, speed.length()-3);
			//log.info("Speed : "+speed);
			
			///////////////////////////
			// Build the HTML page  //
			//////////////////////////
			
			// Get the templace of the main html 
	        mainHtml = getTemplate(MAIN_HTML);
	        		
	        
	        // Update the template with date and google charts code
	        mainHtml = mainHtml
	        		.replace("#DATE#",new SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.ENGLISH).format(new Date()))
	        		.replace("#SPEED#",speed)
	        		.replace("#JAVASCRIPT#",javascriptTotal.toString())
	        		.replace("#BODY#",bodyHtmlTotal.toString());
	        
	        ////////////////////////////////
	     	// Write the output ping file //
	        ////////////////////////////////
	        File pingFile=new File(pingFilePath);
	     	FileUtils.writeStringToFile(pingFile, mainHtml,"UTF-8");
	     	
	     	//log
	     	log.info("The file "+pingFilePath+" has been created successfully.");
	     	
	     	///////////////////////////
	     	// Send the file by FTP  //
	     	//////////////////////////
	     	if (isReachable(ftpServer,21,3000)) {
		     	FTPClient client = new FTPClient();
		     	client.connect(ftpServer,21);
		        client.login(ftpUser,ftpPassword);
		        client.upload(pingFile);
		        client.disconnect(true);
		        log.info("The file "+pingFile.getName()+" has been uploaded successfully to "+ftpUser+"@"+ftpServer+".");
	     	}
	     	else {
	     		log.error("Impossible to reach "+ftpServer+" ! Please chek your Internet connection.");
	     	}
			
			// The final return code
			returnCode = 0;
			
		} catch (Exception e) {
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
	} // end of execute()
	
	/**
	 * 
	 * @param templateFile
	 * @return
	 * @throws IOException
	 */
	private String getTemplate(String templateFile) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(templateFile);
        String templateString = IOUtils.toString(is,"UTF-8");
        is.close();
        return templateString;
	}
	
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
	 * @return
	 */
	private String getJVM() {
		// expressed with megabytes
		int mb = 1024 * 1024;
		Runtime runtime  = Runtime.getRuntime();
		StringBuffer jvm = new StringBuffer();
		jvm.append("JVM(Mb) Used:")
				.append((runtime.totalMemory()-runtime.freeMemory())/mb)
				.append(" Free:")  .append(runtime.freeMemory()/mb)
				.append(" Total:") .append(runtime.totalMemory()/mb)
				.append(" Max:")   .append(runtime.maxMemory()/mb);
		return jvm.toString();
	} // end of getJVM()
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	
		// Read the parameters
		String confFilePath = args[0];
		
		// Run the daemon
		new BuildPingChart(confFilePath).execute();
	}
	
}
