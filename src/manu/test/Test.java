package manu.test;

import manu.log.LogService;

public class Test {

	/**
	 * 
	 */
	private static LogService log = new LogService(
			Test.class.getName(),
			Test.class.getSimpleName());
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "10 packets transmitted, 10 received, 0.3% packet loss, time 9014ms";

		s = s
		.substring(s.indexOf("received,")+"received,".length(), s.indexOf("%"))
		.replace(" ","");
		
		log.info(">"+s+"<");
		
		log.info(0%7);
		log.info(2%7);


	}

}
