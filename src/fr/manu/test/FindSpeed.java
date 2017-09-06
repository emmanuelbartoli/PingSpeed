package fr.manu.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import fr.manu.log.LogService;

/**
 * 
 * @author actarus78
 *
 */
public class FindSpeed {

	/**
	 * 
	 */
	private static LogService log = new LogService(FindSpeed.class.getName(), FindSpeed.class.getSimpleName());

	/**
	 * 
	 */
	private static String MBPS="Mbps";
	
	/**
	 * 
	 */
	public FindSpeed() {
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void execute() throws IOException {


		URL url = new URL("http://monmodem");
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		int idx1 ;
		int idx2 ;
		String speed="";
		//Example of line to find
		//  <td align="left"><script type="text/javascript">document.write("200 Mbps");</script>200 Mbps</td>
		//  <td align="left"><script type="text/javascript">document.write("20 Mbps");</script>20 Mbps</td>
		while ((line = reader.readLine()) != null)
			if (line.contains(MBPS)) {
				//log.info(line);
				idx1 = line.indexOf(MBPS);
				idx2 = line.substring(0, idx1).lastIndexOf("\"");
				speed = speed + line.substring(idx2+1,idx1+MBPS.length())+" / ";
				//log.info(">"+line.substring(idx2+1,idx1+MBPS.length())+"<");
				
			}
		reader.close();
		// Remove the final /
		speed = speed.substring(0, speed.length()-3);
		log.info(">"+speed+"<");
	}

	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new FindSpeed().execute();
	}

}
