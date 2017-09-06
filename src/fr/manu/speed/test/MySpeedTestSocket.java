package fr.manu.speed.test;

import fr.bmartel.speedtest.SpeedTestSocket;

public class MySpeedTestSocket extends SpeedTestSocket {
	
	/**
	 * 
	 * @author actarus78
	 *
	 */
    public enum Status {
    	ON_START,ON_PROGRESS,ON_COMPLETION,ON_ERROR;
    }
    
    /**
     * 
     */
    private Status status;
    
    public MySpeedTestSocket(Status status) {
    	super();
    	this.status = status;
    }

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
    
    

}
