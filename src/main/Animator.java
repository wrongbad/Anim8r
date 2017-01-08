package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class Animator implements ActionListener,MouseMotionListener
{
	private Updater u;
//	private Painter p;
	private Canvas c;
	private LinkedList<Frame> frames;
	private JFrame frame;
	private Thread tu;
//	private Thread tp;
	public JButton close;
	int time=200;
	
	public Animator()
	{
		c=new Canvas();
		c.addMouseMotionListener(this);
		close = new JButton("CLOSE");
		close.setBackground(Color.BLACK);
		close.setForeground(Color.WHITE);
		close.setBorder(new Outline(close));
		close.addActionListener(this);
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,c,close);
		jsp.setBorder(null);
		jsp.setDividerLocation(450);
		jsp.setDividerSize(0);
		frame = new JFrame("Animation Preview");
		frame.setSize(600,480);
		frame.setContentPane(jsp);
		frame.addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent we) { frame.setVisible(false); }
			}
		);
		//frame.setUndecorated(true);
		u = new Updater();
//		p = new Painter();
		tu = new Thread(u);
//		tp = new Thread(p);
		tu.setName("Updater");
//		tp.setName("Painter");
		tu.start();
//		tp.start();
	}
	public void show(LinkedList<Frame> anim)
	{
		frames=anim;
		frame.setVisible(true);
	}
	public void hide()
	{
//		u.stop=true;
//		p.stop=true;
		frame.setVisible(false);
	}
	public class Updater implements Runnable
	{
		public boolean stop=false;
		
		public void run()
		{
			long start=System.currentTimeMillis();
			long last=start;
			int f=0;
			while(!stop)
			{
				try
				{
					Thread.sleep(10);
					if(frames!=null)
					{
						int fl=frames.size();
	//					long absStart=start;
						while(System.currentTimeMillis()-start>time)
						{
							f=(f+1)%fl;
							start+=time;
						}
	//					System.out.println();
						if(frame.isVisible())
						{
							c.repaint();
							Graphics g=c.getGraphics();
							g.setColor(Color.WHITE);
							g.drawString(""+time, 3, c.getHeight()-3);
							c.apply(frames.get(f), frames.get((f+1)%fl), (System.currentTimeMillis()-start)/(float)time);
							c.update((System.currentTimeMillis()-last)/1000f);
						}
					}
					else
						start=System.currentTimeMillis();
					last=System.currentTimeMillis();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			stop=false;
		}
	}
//	public class Painter implements Runnable
//	{
//		public boolean stop=false;
//		
//		public void run()
//		{
//			while(!stop)
//			{
//				try
//				{
//					Thread.sleep(20);
//					if(frame.isVisible())
//						c.repaint();
//				}
//				catch(Exception ex)
//				{
//					ex.printStackTrace();
//				}
//			}
//			stop=false;
//		}
//	}
	public void actionPerformed(ActionEvent e)
	{
		hide();
	}
	public void mouseDragged(MouseEvent e)
	{
		time=(800*e.getX()/c.getWidth());
	}
	public void mouseMoved(MouseEvent arg0) {}
}
