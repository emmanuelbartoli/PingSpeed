package fr.manu.ping.daemon;

public class Daemon {
	/**
	 * 
	 */
	public enum Status {
		TO_START,STARTED,RUNNING,TO_STOP,STOPPED,ERROR}
	
	/**
	 * 
	 */
	private String serverToPing;
	
	/**
	 * 
	 */
	private int pingPackets;
	
	/**
	 * 
	 */
	private int pingInterval;
	
	/**
	 * 
	 */
	private int sleepValue;
	
	/**
	 * 
	 */
	private Status status;
	
	
	/**
	 * 
	 * @param serverToPing
	 * @param pingPackets
	 * @param pingInterval
	 * @param sleepValue
	 * @param status
	 */
	public Daemon(
			String serverToPing, 
			int pingPackets, 
			int pingInterval,
			int sleepValue, 
			Status status) {
		super();
		this.serverToPing  = serverToPing;
		this.pingPackets   = pingPackets;
		this.pingInterval = pingInterval;
		this.sleepValue    = sleepValue;
		this.status        = status;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return new StringBuffer()
				.append("Server to ping      : ").append(serverToPing).append("\n")
				.append("Ping packets        : ").append(pingPackets) .append("\n")
				.append("Ping interval (min) : ").append(pingInterval).append("\n")
				.append("Sleep value (s)     : ").append(sleepValue)  .append("\n")
				.append("Status              : ").append(status)
				.toString();
	}

	public String getServerToPing() {
		return serverToPing;
	}

	public void setServerToPing(String serverToPing) {
		this.serverToPing = serverToPing;
	}

	public int getPingPackets() {
		return pingPackets;
	}

	public void setPingPackets(int pingPackets) {
		this.pingPackets = pingPackets;
	}

	public int getSleepValue() {
		return sleepValue;
	}

	public void setSleepValue(int sleepValue) {
		this.sleepValue = sleepValue;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getPingInterval() {
		return pingInterval;
	}

	public void setPingInterval(int pingInterval) {
		this.pingInterval = pingInterval;
	}
	
}
