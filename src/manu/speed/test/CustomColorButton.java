package manu.speed.test;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

/**
 * CustomColorButton
 */
public class CustomColorButton 
	extends JButton 
	implements ActionListener,MouseListener {

	private static final long serialVersionUID = 1L;
	private boolean hovered = false;
	private boolean clicked = false;

	private Color normalColor = null;
	private Color lightColor = null;
	private Color darkColor = null;

	public CustomColorButton(Color normalColor, Color fontColor) {
		setForeground(fontColor);

		this.normalColor = normalColor;
		this.lightColor = normalColor.brighter();
		this.darkColor = normalColor.darker();

		addActionListener(this);
		addMouseListener(this);
		setContentAreaFilled(false);
	}

	/**
	 * Overpainting component, so it can have different colors
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		/*
		 * GradientPaint gp = null; if (clicked) gp = new GradientPaint(0, 0,
		 * darkColor, 0, getHeight(), darkColor.darker()); else if (hovered) gp
		 * = new GradientPaint(0, 0, lightColor, 0, getHeight(),
		 * lightColor.darker()); else gp = new GradientPaint(0, 0, normalColor,
		 * 0, getHeight(), normalColor.darker()); g2d.setPaint(gp);
		 */

		Color color = null;
		 if (!isEnabled())
				color = Color.LIGHT_GRAY;
		else if (clicked)
			color = darkColor;
		else if (hovered)
			color = lightColor;
		else
			color = normalColor;
		g2d.setColor(color);

		// Draws the rounded opaque panel with borders
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON); // For High quality
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);

		// g2d.setColor(darkColor.darker().darker());
		// g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 0, 0);

		super.paintComponent(g);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		hovered = true;
		clicked = false;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		hovered = false;
		clicked = false;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		hovered = false;
		clicked = true;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		hovered = false;
		clicked = false;
		repaint();
	}
	
	public void setNormalColor(Color normalColor) {
		this.normalColor = normalColor;
		this.lightColor = normalColor.brighter();
		this.darkColor = normalColor.darker();
	}
}