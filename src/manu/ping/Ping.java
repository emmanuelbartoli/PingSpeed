package manu.ping;

public class Ping {
	
	/**
	 * 
	 */
	public enum Status {
		OK,KO;
	}
	
	
	/**
	 * 
	 */
	private String server;
	
	/**
	 * 
	 */
	private int minimum;
	
	/**
	 * 
	 */
	private int maximum;
	
	/**
	 * 
	 */
	private int average;
	
	/**
	 * 
	 */
	private int loss;
	
	/**
	 * 
	 */
	private String date;
	
	/**
	 * 
	 */
	private Status status;

	/**
	 * 
	 */
	public Ping() {
	}
	
	
	/**
	 * 
	 * @param server
	 * @param minimum
	 * @param maximum
	 * @param average
	 * @param loss
	 * @param date %d %b %H:%i
	 * @param status
	 */
	public Ping(
			String server, 
			int minimum, 
			int maximum, 
			int average, 
			int loss, 
			String date, 
			Status status) {
		super();
		this.server = server;
		this.minimum = minimum;
		this.maximum = maximum;
		this.average = average;
		this.loss = loss;
		this.date = date;
		this.status = status;
	}
	
	/**
	 * 
	 * @param server
	 * @param minimum
	 * @param maximum
	 * @param average
	 * @param loss
	 * @param status
	 */
	public Ping(
			String server, 
			int minimum, 
			int maximum, 
			int average, 
			int loss, 
			Status status) {
		this(server,minimum,maximum,average,loss,null,status);
	}

	/**
	 * 
	 * @param server
	 */
	public Ping(String server) {
		this.server = server;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return new StringBuffer()
				.append("Minimum = ").append(minimum).append("ms, ")
				.append("Maximum = ").append(maximum).append("ms, ")
				.append("Average = ").append(average).append("ms, ")
				.append("Loss = ").append(loss).append("%")
				.toString();
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getAverage() {
		return average;
	}

	public void setAverage(int average) {
		this.average = average;
	}

	public int getLoss() {
		return loss;
	}

	public void setLoss(int loss) {
		this.loss = loss;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	


	

	
	
}
