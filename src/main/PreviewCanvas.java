package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TreeMap;

import javax.swing.JPanel;

public class PreviewCanvas extends JPanel
{
	Canvas c;
	BufferedImage scr;
	BufferedImage livescr;
	boolean paint=true;
	boolean crosshair;
	
	public PreviewCanvas(Canvas canvas)
	{
		c=canvas;
//		this.setDoubleBuffered(true);
	}
	public void update()//long time)
	{
//		paint=false;
//		float dt = (time-lastTime)/1000f;
		if(getHeight()>0 && getWidth()>0 && (scr==null || scr.getWidth()!=getWidth() || scr.getHeight()!=getHeight()))
			scr = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		if(scr!=null)
		{
//			System.out.println("h");
			Graphics g=scr.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
//			g.setColor(Color.DARK_GRAY);
//			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
//			for(int i=0;i<c.faces.size();i++)
//			{
//				c.faces.get(i).paint((Graphics2D)g, c.realCam,getWidth(),getHeight());
//			}
			TreeMap<Double,Segment> orderSegs = new TreeMap<Double,Segment>();
			for(int i=0;i<c.faces.size();i++)
			{
				c.faces.get(i).findPoly(c.realCam,getWidth(),getHeight());
			}
			//(find face intersections here)
			for(int i=0;i<c.faces.size();i++)
			{
				for(int j=0;j<i;j++)
				{
					if(c.faces.get(i).isBehind(c.faces.get(j),getWidth(),getHeight(),c.realCam))
					{
						c.faces.get(i).poly.subtract(c.faces.get(j).poly);
//						System.out.println(i+"  "+j);
					}
				}
			}
			for(int i=0;i<c.faces.size();i++)
			{
				c.faces.get(i).paint((Graphics2D)g);
			}
			for(int i =0;i<c.segs.size();i++)
			{
//					segs[i].midpoint = ;
				Vect3D camLoc = c.realCam.getRelative(Vect3D.midPoint(c.vects.get(c.segs.get(i).end1), c.vects.get(c.segs.get(i).end2)));
				orderSegs.put(-camLoc.y+camLoc.z/1000.0+camLoc.x/1000.0,c.segs.get(i));
			}
			Object[] keys = orderSegs.keySet().toArray();
			for(int i =0;i<keys.length;i++)
			{
				orderSegs.get(keys[i]).paint((Graphics2D)g,c.realCam,c.vects,getWidth(),getHeight());
			}
			if(crosshair)
			{
				int xc= getWidth()/2;
				int yc=getHeight()/2;
				g.setColor(Color.BLACK);
				g.drawLine(xc-20,yc+1,xc+20,yc+1);
				g.drawLine(xc+1,yc-20,xc+1,yc+20);
				g.setColor(Color.GREEN);
				g.drawLine(xc-20,yc,xc+20,yc);
				g.drawLine(xc,yc-20,xc,yc+20);
			}
//				g.setColor(Color.BLACK);
//				g.setFont(new Font("Dialog",Font.BOLD,12));
//				g.drawString("Epsilon Animator", 2, getHeight()-2);
//				g.setColor(Color.DARK_GRAY);
//				g.drawString("Epsilon Animator", 3, getHeight()-3);
			BufferedImage tmp = livescr;
			livescr = scr;
			scr = tmp;
		}
		paint=true;
	}
	public void lengths()
	{
		for(int i = 0;i<c.vects.size();i++)
		{
			for(int j=0;j<c.vects.size();j++)
			{
				if(i!=j)
				{
					Vect3D a=c.vects.get(i);
					Vect3D b=c.vects.get(j);
					Vect3D d=Vect3D.subtract(a,b);
					if(d.getMagnitudeSQ()<.16f)
					{
						d=d.getScaled( (.4f/d.getMagnitude()-1) / 4 );
						a.move(d);
						b.move(d.getScaled(-1));
					}
				}
			}
		}
		for(int i = 0;i<c.segs.size()*3;i++)
		{
			c.segs.get(i%(c.segs.size())).fixLength2(c.vects);
		}
	}
	public void paintComponent(Graphics g)
	{
		if(g!=null && getHeight()!=0 && getWidth()!=0 && livescr!=null)
		{
			g.drawImage(livescr,0,0,null);
		}
	}
}
