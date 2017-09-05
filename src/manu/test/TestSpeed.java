package manu.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;

import it.sauronsoftware.ftp4j.FTPClient;

public class TestSpeed {

	public static void main(String[] args) throws Exception {

		long totalDownload = 0; // total bytes downloaded
		final int BUFFER_SIZE = 1024; // size of the buffer
		byte[] data = new byte[BUFFER_SIZE]; // buffer
		BufferedInputStream in = new BufferedInputStream(new URL(
				// "http://speedtest.wdc01.softlayer.com/downloads/test100.zip"
				"http://actarus78.free.fr/test200.zip").openStream());
		int dataRead = 0; // data read in each try
		long startTime = System.nanoTime(); // starting time of download
		while ((dataRead = in.read(data, 0, BUFFER_SIZE)) > 0) {
			totalDownload += dataRead; // adding data downloaded to total data
		}
		long endTime = System.nanoTime();
		in.close();


		BigDecimal bytesPerSec = new BigDecimal(totalDownload)
					.divide(new BigDecimal(endTime).subtract(new BigDecimal(startTime)), MathContext.DECIMAL128)
					.multiply(new BigDecimal(1000000000));
		BigDecimal kbPerSec = bytesPerSec.divide(new BigDecimal(1024), MathContext.DECIMAL128);
		BigDecimal mbPerSec = new BigDecimal(8).multiply(kbPerSec).divide(new BigDecimal(1024), MathContext.DECIMAL128);
		int downloadSpeed = mbPerSec.setScale(0, RoundingMode.HALF_UP).intValueExact();
		//System.out.println("Download : " + mbPerSec.toString() + " Mbps ");
		System.out.println("Download : " + downloadSpeed + " Mbps ");

		FTPClient client = new FTPClient();
		client.connect("ftpperso.free.fr", 21);
		client.login("actarus78", "5t4d4f3z");

		File file = new File("C:\\Users\\emman\\Documents\\speedtest\\test20.zip");
		long totalUpload = file.length();

		startTime = System.nanoTime();
		client.upload(file);
		endTime = System.nanoTime();
		client.disconnect(true);

		bytesPerSec = new BigDecimal(totalUpload)
						.divide(new BigDecimal(endTime).subtract(new BigDecimal(startTime)), MathContext.DECIMAL128)
						.multiply(new BigDecimal(1000000000));
		kbPerSec = bytesPerSec.divide(new BigDecimal(1024), MathContext.DECIMAL128);
		mbPerSec = new BigDecimal(8).multiply(kbPerSec).divide(new BigDecimal(1024), MathContext.DECIMAL128);
		int uploadSpeed = mbPerSec.setScale(0, RoundingMode.HALF_UP).intValueExact();
		//System.out.println("Upload   : " + mbPerSec + " Mbps ");
		System.out.println("Upload   : " + uploadSpeed + " Mbps ");

	}

}
