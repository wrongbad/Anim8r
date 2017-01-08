package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.border.Border;

public class Outline implements Border,MouseListener
{
	public boolean light;
	public boolean activated;
	
	public Outline(Component c)
	{
		c.addMouseListener(this);
	}
	public Insets getBorderInsets(Component arg0) {return new Insets(1,1,1,1);}
	public boolean isBorderOpaque() {return true;}
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if(activated)
			g.setColor(Color.GREEN);
		else if(light)
			g.setColor(Color.RED);
		else
			g.setColor(Color.DARK_GRAY);
		g.drawRect(x, y, width-1, height-1);
	}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent e) {light=true;}
	public void mouseExited(MouseEvent arg0) {light=false;}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

}
