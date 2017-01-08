package main;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

public class Editor3D implements MouseListener, MouseMotionListener, ActionListener
{
	Canvas c;
	PreviewCanvas preview;
	Animator fullPreview=new Animator();
	FrameBrowser filmStrip;
	Point last = null;
	boolean left;
	boolean right;
	Updater u;
//	Painter p;
	JButton[] tools;
	LinkedList<Frame> frames = new LinkedList<Frame>();
	float focDist=15;
	JFileChooser jf;
	JFileChooser jf2;
	JDialog j;
	JDialog yesno;
	JComboBox modeBox;
	JComboBox viewBox;
	Point startDrag;
	Filter myFilter = new Filter();
	/**
	 * 0=normal
	 * 1=point moving
	 * 2=drag selecting
	 * 3=point making
	 * 4=segment building
	 * 5=surface building
	 */
	int mode=0;
	/**
	 * 0=perspective
	 * 1=front
	 * 2=right
	 * 3=top
	 * 4=back
	 * 5=left
	 * 6=bottom
	 */
	int view=0;
	
	boolean pMoving=false;
	
	public Editor3D()
	{
//		System.out.println(Surface.lineIntersect(new Point(12,10),new Point(-8,-10),new Point(6,8),new Point(10,8)));
		c=new Canvas();
		c.setBorder(new Outline(c));
		c.stickFigure(0,-2,0);
		c.stickFigure(0,2,0);
		c.makeBlock(0,0,-3.2f);
//		c.bloods.add(new BloodSpray(new Vect3D(0,0,0),new Vect3D(1,0,1)));
//		for(int i=0;i<25;i++)
//		{
//			c.makeBlock((i%5)*30,(i/5)*30,-3.25f);
//		}
		
//		c.faces.get(1).findPoly(c.cam, 600, 450);
//		c.faces.get(3).findPoly(c.cam, 600, 450);
//		System.out.println(c.faces.get(1).isBehind(c.faces.get(3)));
		preview = c.pc;
		preview.setBorder(new Outline(preview));
		filmStrip = new FrameBrowser();
		filmStrip.addMouseMotionListener(this);
		filmStrip.addMouseListener(this);
		c.addMouseMotionListener(this);
		c.addMouseListener(this);
		preview.addMouseMotionListener(this);
		preview.addMouseListener(this);
//		c.addMouseMotionListener(this);
		JPanel toolBar = new JPanel();
		toolBar.setBorder(null);
		toolBar.setLayout(new GridLayout(14,1,0,0));
		toolBar.setBackground(Color.BLACK);
		tools = new JButton[6];
		tools[0] = new JButton("Save...");
		tools[1] = new JButton("Open...");
		tools[2] = new JButton("New Frame");
		tools[3] = new JButton("Delete Frame");
		tools[4] = new JButton("Preview");
//		tools[5] = new JButton("Delete");
		tools[5] = new JButton("Reset Cam");
		for(int i = 0;i<tools.length;i++)
		{
			tools[i].setBackground(Color.GRAY);
			tools[i].setForeground(new Color(0,64,0));
			tools[i].addActionListener(this);
			tools[i].setBorder(new Outline(tools[i]));
			toolBar.add(tools[i]);
		}
		modeBox = new JComboBox(new String[]{"Normal","Point Move","Drag Select","Add Points","Add Segments","Add Surface"});
		modeBox.setBackground(Color.DARK_GRAY);
		modeBox.setForeground(new Color(0,255,0));
		modeBox.addActionListener(this);
		modeBox.setActionCommand("mode change");
		modeBox.setBorder(new Outline(modeBox));
		viewBox = new JComboBox(new String[]{"Perspective","2D Front","2D Right","2D Top","2D Back","2D Left","2D Bottom"});
		viewBox.setBackground(Color.DARK_GRAY);
		viewBox.setForeground(new Color(0,255,0));
		viewBox.addActionListener(this);
		viewBox.setActionCommand("view change");
		viewBox.setBorder(new Outline(modeBox));
		toolBar.add(modeBox);
		toolBar.add(viewBox);
		JSplitPane jsp0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,preview,filmStrip);
		JSplitPane jsp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,toolBar,jsp0);
		JSplitPane jsp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,c,jsp1);
		jsp0.setDividerLocation(390);
		jsp0.setDividerSize(0);
		jsp0.setBorder(null);
		jsp1.setDividerLocation(100);
		jsp1.setDividerSize(0);
		jsp1.setBorder(null);
		jsp2.setDividerLocation(600);
		jsp2.setDividerSize(0);
		jsp2.setBorder(null);
		JFrame frame = new JFrame("Echo3D");
		frame.setSize(1228,484);
//		frame.setUndecorated(true);
		frame.setContentPane(jsp2);
		frame.setVisible(true);
		frame.addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent we) { System.exit(0); }
			}
		);
		filmStrip.setTotalFrames(1);
        filmStrip.repaint();
        u=new Updater();
//		p=new Painter();
        frames.add(c.getFrame());
	}
	public static void main(String[] args)
	{
		Editor3D e = new Editor3D();
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand()=="New Frame")
		{
			frames.add(filmStrip.selectedFrame,frames.get(frames.size()-1).clone());
			filmStrip.setTotalFrames(frames.size());
			filmStrip.repaint();
		}
		else if(e.getActionCommand()=="Delete Frame")
		{
			frames.remove(filmStrip.selectedFrame);
			filmStrip.setTotalFrames(frames.size());
			filmStrip.scroll(0);
			filmStrip.selectedFrame=filmStrip.selectedFrame-1;
			c.apply(frames.get(filmStrip.selectedFrame));
			filmStrip.repaint();
		}
		else if(e.getActionCommand()=="Preview")
		{
			frames.set(filmStrip.selectedFrame,c.getFrame());
			fullPreview.show(frames);
		}
		else if(e.getActionCommand()=="Save...")
		{
			promptSave();
		}
		else if(e.getActionCommand()=="Open...")
		{
			promptOpen();
		}
		else if(e.getActionCommand()=="Add Figure")
		{
			c.stickFigure(0,0,0);
			c.selectNone();
			for(int i=0;i<12;i++)
			{
//				System.out.println(c.vects.get(c.vects.size()-i));
				c.vects.get(c.vects.size()-i-1).selected=1;
				c.selection.add(c.vects.get(c.vects.size()-i-1));
			}
		}
		else if(e.getActionCommand()=="Reset Cam")
		{
			c.cam.reset();
		}
		else if(e.getActionCommand()=="mode change")
		{
			mode=modeBox.getSelectedIndex();
		}
		else if(e.getActionCommand()=="view change")
		{
			view=viewBox.getSelectedIndex();
			if(view==0)
				c.cam.perspective=true;
			else
				c.cam.perspective=false;
			
			switch(view)
			{
			case 1:
				c.cam.loc=new Vect3D(0,-20,0);
				c.cam.look=new Vect3D(0,1,0);
				c.cam.up=new Vect3D(0,0,1);
				break;
			case 2:
				c.cam.loc=new Vect3D(20,0,0);
				c.cam.look=new Vect3D(-1,0,0);
				c.cam.up=new Vect3D(0,0,1);
				break;
			case 3:
				c.cam.loc=new Vect3D(0,0,20);
				c.cam.look=new Vect3D(0,0,-1);
				c.cam.up=new Vect3D(0,1,0);
				break;
			case 4:
				c.cam.loc=new Vect3D(0,20,0);
				c.cam.look=new Vect3D(0,-1,0);
				c.cam.up=new Vect3D(0,0,1);
				break;
			case 5:
				c.cam.loc=new Vect3D(-20,0,0);
				c.cam.look=new Vect3D(1,0,0);
				c.cam.up=new Vect3D(0,0,1);
				break;
			case 6:
				c.cam.loc=new Vect3D(0,0,-20);
				c.cam.look=new Vect3D(0,0,1);
				c.cam.up=new Vect3D(0,-1,0);
				break;
			}
		}
		else if(e.getActionCommand()=="CancelSelection")
		{
			j.dispose();
		}
		else if(e.getActionCommand()=="ApproveSelection")
		{
			if(((JFileChooser)e.getSource()).getApproveButtonText()=="Save")
			{
				File f = jf.getSelectedFile();
				if( !f.getName().endsWith(".txt") && f.getName().split(".").length==0 )
					f=new File(f.getPath()+f.getName()+".txt");
				jf.setSelectedFile(f);
				if(f.exists())
				{
					makeYN();
				}
				else
				{
					saveTo(f);
					j.dispose();
				}
			}
			else
			{
				File f = jf2.getSelectedFile();
				load(f);
				j.dispose();
			}
		}
		else if(e.getActionCommand()=="Yes")
		{
			File f = jf.getSelectedFile();
			saveTo(f);
			j.dispose();
			yesno.dispose();
		}
		else if(e.getActionCommand()=="No")
		{
			yesno.dispose();
		}
	}
	public void promptSave()
	{
		jf = new JFileChooser("c:/");
		jf.setDialogType(JFileChooser.SAVE_DIALOG);
		j = new JDialog();
		j.setTitle("Save As...");
		j.getContentPane().add(jf);
		j.setSize(600,500);
		j.setVisible(true);
		j.setAlwaysOnTop(true);
		jf.setApproveButtonText("Save");
		jf.addActionListener(this);
		jf.setFileFilter(myFilter);
	}
	public void makeYN()
	{
		yesno = new JDialog();
		yesno.setAlwaysOnTop(true);
		JButton yes = new JButton("Yes");
		yes.addActionListener(this);
		yes.setBackground(Color.BLACK);
		yes.setForeground(Color.GREEN);
		JButton no = new JButton("No");
		no.addActionListener(this);
		no.setBackground(Color.BLACK);
		no.setForeground(Color.GREEN);
		JSplitPane jsp1=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,no,yes);
		jsp1.setDividerLocation(175);
		jsp1.setDividerSize(0);
		jsp1.setBorder(null);
		JLabel jlab = new JLabel("Are you sure you want to overwrite the previous file?");
		JPanel jpan = new JPanel();
		jpan.setBorder(null);
		jpan.setBackground(Color.BLACK);
		jlab.setForeground(Color.GREEN);
		jpan.add(jlab);
		JSplitPane jsp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jpan,jsp1);
		jsp2.setDividerLocation(30);
		jsp2.setDividerSize(0);
		yesno.setContentPane(jsp2);
		yesno.setSize(350, 100);
		yesno.setVisible(true);
	}
	public void promptOpen()
	{
		jf2 = new JFileChooser("c:/");
		jf2.setDialogType(JFileChooser.OPEN_DIALOG);
		j = new JDialog();
		j.setTitle("Open File...");
		j.getContentPane().add(jf2);
		j.setSize(600,500);
		j.setVisible(true);
		j.setAlwaysOnTop(true);
		jf2.setApproveButtonText("Open");
		jf2.addActionListener(this);
		jf2.setFileFilter(myFilter);
	}
	public void mouseClicked(MouseEvent e)
	{
		if(e.getComponent()==c)
		{
			if(e.getButton()==e.BUTTON1)
			{
				c.processClick(e.getPoint());
			}
			else
				c.processRtClick(e.getPoint());
//			System.out.println(e.getButton());
//	        c.update();
//	        c.repaint();
			if(mode==2)
			{
				c.selectNone();
			}
		}
		if(e.getComponent()==filmStrip)
		{
			int f = filmStrip.whichFrame(e.getPoint());
			if(f!=-1 && f<frames.size())
			{
				frames.set(filmStrip.selectedFrame,c.getFrame());
				filmStrip.selectFrame(f);
				c.apply(frames.get(f));
			}
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseDragged(MouseEvent e)
		{
			if(last==null)
			{
				last=e.getPoint();
				return;
			}
			float x = .01f*(e.getX()-last.x);
			float y = -.01f*(e.getY()-last.y);
			if(e.getComponent().equals(c))
			{
				if(mode==1 && c.selection.size()>0)
				{
					for(int i=0;i<c.selection.size();i++)
					{
						Vect3D right = Vect3D.crossProduct(c.cam.look, c.cam.up);
						c.selection.get(i).move(Vect3D.add(c.cam.up.getScaled(y), right.getScaled(x)));
						c.lengths();
					}
				}
				else if(mode==2)
				{
					if(left && !right)
						c.dragRect=new Rectangle(startDrag.x,startDrag.y,e.getX()-startDrag.x,e.getY()-startDrag.y);
					else if(!left && right)
					{
						for(int i=0;i<c.selection.size();i++)
						{
							Vect3D right = Vect3D.crossProduct(c.cam.look, c.cam.up);
							c.selection.get(i).move(Vect3D.add(c.cam.up.getScaled(y), right.getScaled(x)));
							c.lengths();
						}
					}
				}
				else if(view==0)
				{
					moveCam(c.cam,x,y);
				}
				else
				{
					if(left)
					{
						float newy = -Vect3D.dotProduct(c.cam.loc,c.cam.look)-(y*10);
						if(newy > 2)
						{
							c.cam.loc.move(c.cam.look.getScaled(y*10));
						}
						else
						{
							c.cam.loc=c.cam.loc.getAxis(c.cam.getRight(),c.cam.look,c.cam.up);
							c.cam.loc.move(new Vect3D(0,-2-c.cam.loc.y,0));
							c.cam.loc=c.cam.loc.getUnAxis(c.cam.getRight(),c.cam.look,c.cam.up);
						}
					}
					else
					{
						Vect3D right = Vect3D.crossProduct(c.cam.look, c.cam.up);
						c.cam.loc.move(Vect3D.add(c.cam.up.getScaled(y*10), right.getScaled(x*10)));
					}
				}
			}
			else if(e.getComponent().equals(preview))
			{
				moveCam(c.realCam,x,y);
			}
			if(e.getComponent().equals(filmStrip))
			{
				if(filmStrip.barSelected)
				{
					filmStrip.scroll(e.getX()-(int)last.getX());
					filmStrip.repaint();
				}
			}
			last = e.getPoint();
		}
	public void moveCam(Camera cam,float x, float y)
	{
		if(left && right)
		{
			Vect3D right = Vect3D.crossProduct(cam.look, cam.up);
			cam.loc.move(Vect3D.add(cam.up.getScaled(y*10), right.getScaled(x*10)));
		}
		else if(left)
		{
			Vect3D right = Vect3D.crossProduct(cam.look, new Vect3D(0,0,1));
			cam.loc.move(Vect3D.subtract(cam.look,new Vect3D(0,0,cam.look.z)).getScaled(y*10));
			cam.look.move(right.getScaled(x));
			cam.look.setMagnitude(1);
			cam.up=Vect3D.crossProduct(right,cam.look);
			cam.up.setMagnitude(1);
		}
		else
		{
			Vect3D right = Vect3D.crossProduct(cam.look, new Vect3D(0,0,1));
//					Vect3D look = c.cam.look.clone();
			cam.look.move(Vect3D.add(cam.up.getScaled(y), right.getScaled(x)));
			cam.look.setMagnitude(1);
			right = Vect3D.crossProduct(cam.look, new Vect3D(0,0,1));
			cam.up=Vect3D.crossProduct(right,cam.look);
			cam.up.setMagnitude(1);
		}
	}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e)
	{
		if(e.getComponent()==c && mode==2)
		{
			startDrag=e.getPoint();
		}
		if(e.getComponent()==preview)
		{
			preview.crosshair=true;
		}
		if(e.getComponent()==filmStrip)//&&filmStrip.isOnBar(e.getPoint()))
		{
			if(filmStrip.isOnBar(e.getPoint()))
			{
				filmStrip.barSelected=true;
				filmStrip.repaint();
			}
		}
		if(e.getButton()==e.BUTTON1)
			left=true;
		else if(e.getButton()==e.BUTTON3)
			right=true;
	}
	public void mouseReleased(MouseEvent e)
	{
		filmStrip.barSelected=false;
		filmStrip.repaint();
		last=null;
		if(e.getComponent()==preview)
		{
			preview.crosshair=false;
		}
		if(e.getComponent()==c && mode==2)
		{
			c.selectRect();
		}
		if(e.getButton()==e.BUTTON1)
			left=false;
		else if(e.getButton()==e.BUTTON3)
			right=false;
	}
	public void mouseMoved(MouseEvent e) {}
	public class Updater implements Runnable
	{
		public Updater()
		{
			Thread t = new Thread(this);
			t.setName("Updater");
			t.start();
		}
		public void run()
		{
			long last=System.currentTimeMillis();
			while(true)
			{
				try
				{
					Thread.sleep(17);
					c.repaint();
					preview.repaint();
					Thread.sleep(7);
					c.update((System.currentTimeMillis()-last)/1000f);
					last=System.currentTimeMillis();
					preview.update();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
//	public class Painter implements Runnable
//	{
//		public Painter()
//		{
//			Thread t = new Thread(this);
//			t.setName("Painter");
//			t.start();
//		}
//		public void run()
//		{
//			while(true)
//			{
//				try
//				{
//					Thread.sleep(30);
////					c.repaint();
////					preview.repaint();
//				}
//				catch(Exception ex)
//				{
//					ex.printStackTrace();
//				}
//			}
//		}
//	}
	public class Filter extends FileFilter
	{
		public boolean accept(File pathname)
		{
			if(pathname.isDirectory() || pathname.getName().endsWith(".txt"))
				return true;
			return false;
		}

		public String getDescription()
		{
			return "Text Document";
		}
	}
	public void saveTo(File f)
	{
		try
		{
			frames.set(filmStrip.selectedFrame,c.getFrame());
			Frame.writeFrames(new PrintStream(f), frames.toArray(new Frame[frames.size()]));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public void load(File f)
	{
		try
		{
//			File f = new File("C:/echo.txt");
			frames = Frame.readFrames(new Scanner(f));
//			System.out.println(frames.get(0).points);
			filmStrip.setTotalFrames(frames.size());
			filmStrip.repaint();
			filmStrip.selectFrame(0);
			c.apply(frames.get(0));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
