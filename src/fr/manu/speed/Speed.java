package fr.manu.speed;

public class Speed {
	
	/**
     *
     */
	public static final float ZERO = (float)0.0;
   
	/**
	 *  expressed with Mbits/s
	 */
	private float download;
	
	/**
	 * expressed with Mbits/s
	 */
	private float upload;
	
	/**
	 * 
	 */
	private String date;

	/**
	 * 
	 * @param download
	 * @param upload
	 * @param date
	 */
	public Speed(float download, float upload, String date) {
		super();
		this.download = download;
		this.upload = upload;
		this.date = date;
	}
	
	/**
	 * 
	 * @param download
	 * @param upload
	 */
	public Speed(float download, float upload) {
		this(download,upload,null);
	}
	
	/**
	 * 
	 */
	public Speed() {
		this(ZERO,ZERO);
	}
	


	public float getDownload() {
		return download;
	}


	public void setDownload(float download) {
		this.download = download;
	}


	public float getUpload() {
		return upload;
	}


	public void setUpload(float upload) {
		this.upload = upload;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}
	
}
