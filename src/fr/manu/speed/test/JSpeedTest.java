package fr.manu.speed.test;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;

import fr.manu.log.LogService;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;

public class JSpeedTest extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1924657468874892560L;
	
	
	/**
	 * 
	 */
	private static LogService log = new LogService(
			JSpeedTest.class.getName(),
			JSpeedTest.class.getSimpleName());
	
	/**
	 * 
	 */
	private static int X = 20;
	
	/**
	 * 
	 */
	private static int X1 = 20;
	
	/**
	 * 
	 */
	private static int Y = 20;
	
	/**
	 * 
	 */
	private static  int Y1=20;
	
	/**
	 * 
	 */
	private static  int Y2=60;
	
	
	
	
	/**
	 * 
	 */
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Read the parameters
		String confFilePath = args[0];
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JSpeedTest frame = new JSpeedTest(confFilePath);
					frame.setVisible(true);
					frame.setLocation(700, 300);
				} catch (Exception e) {
					log.printStackTrace(e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JSpeedTest(String confFilePath) {
		try {
			
			
			setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 377, 274);
			
			////////////////
			// MAIN PANEL //
			////////////////
			contentPane = new JPanel();
			contentPane.setBackground(Color.WHITE);
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			////////////
			// TITLE //
			///////////
			JLabel lblTitle = new JLabel("Speed Test", SwingConstants.CENTER);
			lblTitle.setVerticalAlignment(SwingConstants.TOP);
			lblTitle.setFont(new Font("Arial", Font.PLAIN, 30));
			lblTitle.setForeground(Color.BLUE);
			lblTitle.setBorder(new LineBorder(Color.BLUE));
			lblTitle.setBounds(X, Y, 328, 39);
			
			//////////////////
			// RESULT PANEL //
			//////////////////
			JPanel panelResult = new JPanel();
			panelResult.setBorder(new LineBorder(Color.BLUE));
			panelResult.setBackground(Color.WHITE);
			panelResult.setBounds(X, Y+50, 328, 108);
			panelResult.setLayout(null);
			
			JLabel lblDownload = new JLabel("Download");
			lblDownload.setForeground(Color.BLUE);
			lblDownload.setFont(new Font("Arial", Font.PLAIN, 25));
			lblDownload.setBounds(X1+0, Y1, 124, 24);
			panelResult.add(lblDownload);
			
			JLabel labelSm1 = new JLabel(":");
			labelSm1.setForeground(Color.BLUE);
			labelSm1.setFont(new Font("Arial", Font.PLAIN, 25));
			labelSm1.setBounds(X1+118, Y1, 18, 24);
			panelResult.add(labelSm1);
			
			JLabel donwload = new JLabel("0 %");
			donwload.setFont(new Font("Arial", Font.PLAIN, 25));
			donwload.setForeground(Color.BLUE);
			donwload.setBounds(X1+130,Y1, 159, 24);
			panelResult.add(donwload);
			
			JLabel lblUpload = new JLabel("Upload");
			lblUpload.setForeground(Color.BLUE);
			lblUpload.setFont(new Font("Arial", Font.PLAIN, 25));
			lblUpload.setBounds(X1, Y2, 124, 24);
			panelResult.add(lblUpload);
			
			JLabel labelSM2 = new JLabel(":");
			labelSM2.setForeground(Color.BLUE);
			labelSM2.setFont(new Font("Arial", Font.PLAIN, 25));
			labelSM2.setBounds(X1+118, Y2, 18, 24);
			panelResult.add(labelSM2);
			
			JLabel upload = new JLabel("0 %");
			upload.setForeground(Color.BLUE);
			upload.setFont(new Font("Arial", Font.PLAIN, 25));
			upload.setBounds(X1+130, Y2, 159, 24);
			panelResult.add(upload);
			
			
			//////////////////
			// Stop / Exit //
			/////////////////
			CustomColorButton btnStopExit = new CustomColorButton( Color.BLUE, Color.WHITE);
			btnStopExit.setBounds(X, Y + 170, 330, 36);
			btnStopExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			btnStopExit.setText("Stop");
			btnStopExit.setBorder(null);
			btnStopExit.setFocusable(false);
			btnStopExit.setFont(new Font("Tahoma", Font.PLAIN, 25));
			
			/////////////////////////////////
			// Add the panels to the frame //
			/////////////////////////////////
			contentPane.add(lblTitle);
			contentPane.add(panelResult);
			contentPane.add(btnStopExit);
			
		
			/////////////////
			// Speed test  //
			////////////////
			Thread speedThread = new Thread() {
				public void run() {
					// Execute the speed test
					SpeedTest speedTest = new SpeedTest(confFilePath,donwload,upload);
					speedTest.execute();
					btnStopExit.setText("Exit");
				}
			};
			speedThread.start();

			
		} catch (Exception e) {
			log.printStackTrace(e);
			setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(contentPane, getError(e),
					"Fatal error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * 
	 * @param oThrowable
	 * @return
	 */
    private String getError(Throwable oThrowable) {
    	String error="";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        oThrowable.printStackTrace(pw);
        String[] stacks = StringUtils.split(sw.toString(), "\r\n");
        error = stacks[0];
        return error;
    } // end of getError()
}
