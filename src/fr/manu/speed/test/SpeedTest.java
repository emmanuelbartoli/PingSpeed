package fr.manu.speed.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import org.apache.commons.lang3.StringUtils;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fr.bmartel.speedtest.model.SpeedTestMode;
import fr.manu.log.LogService;
import fr.manu.speed.Speed;
import fr.manu.speed.SpeedDAO;
import fr.manu.speed.test.MySpeedTestSocket.Status;
import fr.manu.sql.MySqlConManager;

/**
 * Speed test
 * @author actarus78  01/09/2017
 *
 */
public class SpeedTest {

	/**
	 * 
	 */
	private static LogService log = new LogService(
			SpeedTest.class.getName(),
			SpeedTest.class.getSimpleName());
	
	
	/**
	 * 
	 */
    @SuppressWarnings("unused")
	private final static String SPEED_TEST_SERVER_URI_DL = "http://2.testdebit.info/500M.iso";
    
    
    /**
     * spedd examples server uri.
     */
    @SuppressWarnings("unused")
    private static final String SPEED_TEST_SERVER_URI_UL = "http://2.testdebit.info/";
    
    /**
     * default upload size = 20M
     */
    private static final int UPLOAD_SIZE_MB = 20;
    
    /**
     * socket timeout used in ms.
     */
    private final static int SOCKET_TIMEOUT = 5000;
	

    /**
     * default scale for BigDecimal.
     */
    private static final int DEFAULT_SCALE = 2;
    

    /**
     * default rounding mode for BigDecimal.
     */
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;


    /**
     * conversion const for M per second value.
     */
    private static final BigDecimal MEGA_VALUE_PER_SECONDS = new BigDecimal(1000000);
	
	/**
	 * 
	 */
	private String confFilePath;
	
	/**
	 * 
	 */
	private float downloadSpeed;
	
	/**
	 * 
	 */
	private float uploadSpeed;
	
	/**
	 * 
	 */
	private JLabel jLblDownload;
	
	/**
	 * 
	 */
	private JLabel jLblUpload;
	
	
	/**
	 * 
	 * @param confFilePath
	 * @param jLblDownload in the UI
	 * @param jLblUpload in the UI
	 */
	public SpeedTest(String confFilePath,JLabel jLblDownload,JLabel jLblUpload) {
		this.confFilePath = confFilePath;
		this.jLblDownload = jLblDownload;
		this.jLblUpload   = jLblUpload;
	}
	
	/**
	 * 
	 * @param confFilePath
	 */
	public SpeedTest(String confFilePath) {
		this(confFilePath,null,null);
	}
	
	/**
	 * 
	 * @return 0 if OK, 1 if ERROR
	 */
	public int execute() {
		
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
			
			////////////////////////////////
			// Get the configuration file //
			////////////////////////////////
			log.info("");
			log.debug("Read the configuration file "+confFilePath);
			FileInputStream fileIntputStream 		= new FileInputStream(confFilePath);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileIntputStream);
			ResourceBundle resource 				= new PropertyResourceBundle(bufferedInputStream);
			
			// Read the parameters for connecting to the database
			String mysqlServer   = resource.getString("mysql.server");
			String mysqlPort     = resource.getString("mysql.port");
			String mysqlDatabase = resource.getString("mysql.database");
			String mysqlUser     = resource.getString("mysql.user");
			String mysqlPassword = resource.getString("mysql.password");
			
			// Speed test files
			String downloadURI = resource.getString("speed.download.uri");
			String uploadURI   = resource.getString("speed.upload.uri");
			int uploadSizeMB   = Integer.valueOf(resource.getString("speed.upload.size.mb")) ;
			
			
			// Log
			log.info("speed.download.uri   = " + downloadURI);
			log.info("speed.upload.uri     = " + uploadURI);
			log.info("speed.upload.size.mb = " + uploadSizeMB);
			log.debug("mysql.server         = " + mysqlServer);
			log.debug("mysql.port           = " + mysqlPort);
			log.debug("mysql.database       = " + mysqlDatabase);
			log.debug("mysql.user           = " + mysqlUser);
			
			// Close the streams
			bufferedInputStream.close();   
			fileIntputStream.close();
			
			////////////////////
			// Download speed //
			////////////////////
			log.info("Looking for the download speed...");
			downloadSpeed = doTest(downloadURI,SpeedTestMode.DOWNLOAD);
			
			////////////////////
			//  Upload speed  //
			////////////////////
			log.info("Looking for the upload speed...");
			uploadSpeed = doTest(uploadURI,uploadSizeMB,SpeedTestMode.UPLOAD);
			
			////////////////////
			//  Speed results //
			////////////////////
			Speed speed = new Speed(downloadSpeed,uploadSpeed);
			DecimalFormat df = new DecimalFormat("##0.00");
			log.info("+------------------------+");
			log.info("| Download : " + StringUtils.rightPad(df.format(speed.getDownload()) , 6)  + " Mbps |");
			log.info("| Upload   : " + StringUtils.rightPad(df.format(speed.getUpload())   , 6)  + " Mbps |");
			log.info("+------------------------+");
			
			// Database connection
			connection = new MySqlConManager(
						mysqlServer,
						mysqlPort,
						mysqlDatabase,
						mysqlUser,
						mysqlPassword);
			log.debug("Connected to the database.");
		
			// Store the speed result into the database
			log.debug("Insert the speed result into the database");
			SpeedDAO.insertSpeed(connection.getConnection(), speed);
			
		} catch (Exception e) {
			log.printStackTrace(e);
			returnCode = 1;
		} 
		finally {
			
			// Close the database connection
			if (connection != null) {
				connection.close();
			}
			log.debug("Disconnected from the database.");
			log.info("");
			log.info("+-------------+");
			log.info("|     STOP    |");
			log.info("+-------------+");
			log.info("");
			log.info("Return code = "+returnCode);
			log.info("");
		}
		
		return returnCode;
		
	} // end of execute()
	
	/**
	 * 
	 * @param uri
	 * @param mode
	 * @return
	 */
	private float doTest(String uri,SpeedTestMode mode) {
		return doTest(uri,UPLOAD_SIZE_MB,mode);
	}
	
	/**
	 * 
	 * @param uri
	 * @param upload size in MB
	 * @param mode DOWNLOAD/UPLOAD
	 * @return speed expresses with Mbits/second
	 */
	private float doTest(String uri,int uploadSizeMB,SpeedTestMode mode) {
		
		// Init. the speed
		float speed = Speed.ZERO;

		// Instantiate speed examples
		final MySpeedTestSocket speedTestSocket = new MySpeedTestSocket(Status.ON_START);
		
        // Set timeout for download
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);
        
        // Add a listener to wait for speed examples completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
   
            @Override
            public void onCompletion(final SpeedTestReport report) {
                speedTestSocket.setStatus(Status.ON_COMPLETION);
            }

            @Override
            public void onError(final SpeedTestError speedTestError, final String errorMessage) {
            	log.error(speedTestError + " : " + errorMessage);
            	speedTestSocket.setStatus(Status.ON_ERROR);
            }

            @Override
            public void onProgress(final float percent, final SpeedTestReport downloadReport) {
             	speedTestSocket.setStatus(Status.ON_PROGRESS);
            }
        });
		
        // Start the test
		switch (mode) {
		case DOWNLOAD:
			speedTestSocket.startDownload(uri);
			break;
		case UPLOAD:
			speedTestSocket.startUpload(uri, uploadSizeMB*1024*1024);
			break;
		case NONE:
		default:
			break;
		}

        // Wait for test completion
		loop: while (true) {
			switch (speedTestSocket.getStatus()) {
			case ON_START:
			case ON_PROGRESS:
				
				// Update the progress in the UI
				if ((jLblDownload != null) && (jLblUpload != null)) {
					switch (mode) {
					case DOWNLOAD:
						jLblDownload.setText(Math.round(speedTestSocket.getLiveReport().getProgressPercent())+ " %");
						break;
					case UPLOAD:
						jLblUpload.setText(Math.round(speedTestSocket.getLiveReport().getProgressPercent())+ " %");
						break;
					case NONE:
					default:
						break;
					} // end of switch
				}
				
				// Sleep
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.printStackTrace(e);
				}
				break;
			case ON_ERROR:
			case ON_COMPLETION:
				break loop;
			} // end of switch
		} // end of while

		// Expressed the speed with Mbits/second
		speed = bitBigDecToMbFloat(speedTestSocket.getLiveReport().getTransferRateBit());
		
		// Display the result
		DecimalFormat df = new DecimalFormat("##0.00");
		if ((jLblDownload != null) && (jLblUpload != null)) {
			switch (mode) {
			case DOWNLOAD:
				jLblDownload.setText(df.format(speed) + " Mbps");
				break;
			case UPLOAD:
				jLblUpload.setText(df.format(speed) + " Mbps");
				break;
			case NONE:
			default:
				break;
			} // end of switch
		}
        log.debug("========= "+mode+" [ OK ]  =============");
        log.debug("Transfer rate  : " +  df.format(speed) +" Mbits/second");
        log.debug("======================================");
        
		// SpeedTestReport report = speedTestSocket.getLiveReport();
        speedTestSocket.clearListeners();
        speedTestSocket.closeSocket();
		
		// return the speed
		return speed;
				
	} // end of test
	
	
	/*
	 * 
	 */
	private float bitBigDecToMbFloat(BigDecimal bit) {
		return (bit.divide(MEGA_VALUE_PER_SECONDS)).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE).floatValue();
	} 
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Read the parameters
		String confFilePath = args[0];
		
		// Run the daemon
		SpeedTest speedTest = new SpeedTest(confFilePath);
		int status = speedTest.execute();
		
		System.exit(status);

	}

	public float getDownloadSpeed() {
		return downloadSpeed;
	}

	public float getUploadSpeed() {
		return uploadSpeed;
	}

}
