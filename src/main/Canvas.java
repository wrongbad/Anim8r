package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JPanel;

public class Canvas extends JPanel
{

	ArrayList<Segment> segs = new ArrayList<Segment>();
	TreeMap<Integer,Vect3D> vects = new TreeMap<Integer,Vect3D>();
	ArrayList<Integer> newPts = new ArrayList<Integer>();
//	Joint[] vects;
	Camera cam = new Camera();
	Camera realCam = new Camera();
	BufferedImage scr;
	BufferedImage livescr;
	boolean paint=true;
	int frameTime=0;
	//height/width
	public static final float scale=.75f;
	public ArrayList<Vect3D> selection = new ArrayList<Vect3D>();
	//1 more than the highest index of any vects
	int index=0;
	PreviewCanvas pc;
	Vect3D pivot;
	int st=0;
	ArrayList<Surface> faces = new ArrayList<Surface>();
	Rectangle dragRect=null;
	public boolean useRealCam=false;
	ArrayList<StickBot> sb = new ArrayList<StickBot>();
	ArrayList<BloodSpray> bloods = new ArrayList<BloodSpray>();
	
	public Canvas()
	{
		pc=new PreviewCanvas(this);
	}
	public void makeBlock(float xc, float yc, float top)
	{
		Vect3D[] vts = new Vect3D[8];
		vts[0]=new Vect3D( 10+xc, 10+yc,top);
		vts[1]=new Vect3D( 10+xc,-10+yc,top);
		vts[2]=new Vect3D(-10+xc,-10+yc,top);
		vts[3]=new Vect3D(-10+xc, 10+yc,top);

		vts[4]=new Vect3D( 10+xc, 10+yc,top-50);
		vts[5]=new Vect3D( 10+xc,-10+yc,top-50);
		vts[6]=new Vect3D(-10+xc,-10+yc,top-50);
		vts[7]=new Vect3D(-10+xc, 10+yc,top-50);
		for(int i = 0;i<vts.length;i++)
		{
			vts[i].index=index+i;
			vects.put(vts[i].index,vts[i]);
		}
		
		faces.add(new Surface(new Vect3D[]{vts[0],vts[1],vts[5],vts[4]},new Color(0,128,255)));
		faces.add(new Surface(new Vect3D[]{vts[1],vts[2],vts[6],vts[5]},new Color(0,128,255)));
		faces.add(new Surface(new Vect3D[]{vts[2],vts[3],vts[7],vts[6]},new Color(0,128,255)));
		faces.add(new Surface(new Vect3D[]{vts[3],vts[0],vts[4],vts[7]},new Color(0,128,255)));	
		faces.add(new Surface(new Vect3D[]{vts[0],vts[3],vts[2],vts[1]},new Color(0,128,255)));
		index+=vts.length;
	}
	public void stickFigure(float x,float y,float z)
	{
		st++;
		Vect3D[] vts = new Vect3D[12];
		vts[0] = (new Vect3D(0,0,0f));
		vts[1] = (new Vect3D(0,0,1.0f));
		vts[2] = (new Vect3D(StickBot.armout,0,0f));
		vts[3] = (new Vect3D(-StickBot.armout,0,0f));
		vts[4] = (new Vect3D(StickBot.armout,0,-1));
		vts[5] = (new Vect3D(-StickBot.armout,0,-1));
		vts[6] = (new Vect3D(0,0,-1));
		vts[7] = (new Vect3D(StickBot.legout,0,-2));
		vts[8] = (new Vect3D(-StickBot.legout,0,-2));
		vts[9] = (new Vect3D(StickBot.legout,0,-3));
		vts[10]= (new Vect3D(-StickBot.legout,0,-3));
		vts[11]= (new Vect3D(0,0,2f));
		int[] ind=new int[12];
		for(int i = 0;i<vts.length;i++)
		{
			vts[i] =Vect3D.add(vts[i],new Vect3D(x,y,z));
			vts[i].index=index+i;
			ind[i]=index+i;
			vects.put(vts[i].index,vts[i]);
		}
//		StickBot s = new StickBot(ind);
//		s.prep(vects);
//		sb.add(s);
		vts[11].spColor=st%6+1;
		segs.add(new Segment(index+3,index+1));
		segs.add(new Segment(index+1,index+2));
		segs.add(new Segment(index+3,index+5));
		segs.add(new Segment(index+2,index+4));
		segs.add(new Segment(index+1,index+11));
		segs.add(new Segment(index+0,index+6));
		segs.add(new Segment(index+6,index+7));
		segs.add(new Segment(index+6,index+8));
		segs.add(new Segment(index+7,index+9));
		segs.add(new Segment(index+8,index+10));
		segs.add(new Segment(index+1,index+0));
		segs.get(segs.size()-7).jRad2=1f;
		index+=vts.length;
	}

	public void processClick(Point p)
	{
		selectNone();
		Vect3D v = getVect(p);
		if(v!=null)
		{
			v.selected++;
			selection.add(v);
		}
	}
	public void processRtClick(Point p)
	{
		Vect3D v = getVect(p);
		if(v!=null)
		{
			if(selection.contains(v))
			{
				v.selected--;
				selection.remove(v);
			}
			else
			{
				v.selected++;
				selection.add(v);
			}
		}
//		else
//			changeView(p);
	}
	public void selectRect()
	{
		if(dragRect==null)
			return;
		for (int i = 0; i < vects.size(); i++)
		{
			Vect3D v=vects.get(i);
			Point p2 = cam.get2D(cam.getRelative(v),getWidth(),getHeight());
			if(dragRect.contains(p2))
			{
				if(!selection.contains(v))
				{
					v.selected++;
					selection.add(v);
				}
			}
		}
		dragRect=null;
	}
	public void selectNone()
	{
		for (int i = 0; i < vects.size(); i++)
		{
			Vect3D v=vects.get(i);
			if(selection.contains(v))
			{
				v.selected=0;
			}
		}
		selection.clear();
	}
	public Vect3D getVect(Point p)
	{
		float dist=0;
		int best=0;
		for (int i = 0; i < vects.size(); i++)
		{
			Point p2 = cam.get2D(cam.getRelative(vects.get(i)),getWidth(),getHeight());
			int rad = (int)(cam.getScale(cam.getRelative(vects.get(i)),getWidth())*.25+.5);
//			System.out.println(rad+" "+i);
			if(p2!=null && p.distanceSq(p2)<=rad*rad)
			{
				if(dist==0||cam.getRelative(vects.get(i)).y<dist)
				{
					best=i;
					dist=cam.getRelative(vects.get(i)).y;
				}
			}
		}
		if(dist>0)
		{
			return vects.get(best);
		}
		return null;
	}
	public void lengths()
	{
		for(int i = 0;i<vects.size();i++)
		{
			for(int j=0;j<vects.size();j++)
			{
				if(i!=j)
				{
					Vect3D a=vects.get(i);
					Vect3D b=vects.get(j);
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
		for(int i = 0;i<segs.size()*3;i++)
		{
			segs.get(i%(segs.size())).fixLength(vects);
		}
	}
	public void changeView(Point p)
	{
		Vect3D right = Vect3D.crossProduct(cam.look, cam.up);

		if(p.x<Math.min(p.y,getHeight()-p.y)&&p.x<getWidth()/4)
		{
			cam.look=right;
			cam.loc=cam.look.getScaled(-15);
		}
		else if(p.y<Math.min(p.x,getWidth()-p.x)&&p.y<getHeight()/4)
		{
//			System.out.println(cam.loc);
//			cam.loc=Vect3D.crossProduct(cam.loc,right);
//			System.out.println(cam.loc);
			cam.look=cam.up.getScaled(-1);
			cam.up=Vect3D.crossProduct(right,cam.look);
			cam.loc=cam.look.getScaled(-15);
		}
		else if(p.x>Math.max(p.y,getHeight()-p.y)&&p.x>3*getWidth()/4)
		{
//			cam.loc=Vect3D.crossProduct(cam.loc, cam.up);
			cam.look=right.getScaled(-1);
			cam.loc=cam.look.getScaled(-15);
		}
		else if(p.y>Math.max(p.x,getWidth()-p.x)&&p.y>3*getHeight()/4)
		{
//			cam.loc=Vect3D.crossProduct(cam.loc,right);
			cam.look=cam.up.clone();
			cam.up=Vect3D.crossProduct(right,cam.look);
			cam.loc=cam.look.getScaled(-15);
		}
		else
		{
			cam.loc=cam.loc.getScaled(-1);
			cam.look=cam.look.getScaled(-1);
		}
	}
	public void update(float dt)
	{
		try
		{
			paint=false;
	//		float dt = (time-lastTime)/1000f;
			int w = getWidth();
			int h = getHeight();
	//		System.out.println(w+" "+h);
			if(getHeight()>0 && getWidth()>0 && (scr==null || scr.getWidth()!=getWidth() || scr.getHeight()!=getHeight()))
				scr = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
			if(scr!=null)
			{
				Graphics g=scr.getGraphics();
				Graphics2D gr=(Graphics2D)g;
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, w, h);
//				for(int i=0;i<sb.size();i++)
//				{
//					sb.get(i).update(dt, vects);
//				}
				TreeMap<Double,Segment> orderSegs = new TreeMap<Double,Segment>();
				for(int i=0;i<faces.size();i++)
				{
					faces.get(i).findPoly(cam,w,h);
				}
				//(find face intersections here)
				for(int i=0;i<faces.size();i++)
				{
					for(int j=0;j<i;j++)
					{
						if(faces.get(i).isBehind(faces.get(j),w,h,cam))
						{
							faces.get(i).poly.subtract(faces.get(j).poly);
	//						System.out.println(i+"  "+j);
						}
					}
				}
				for(int i=0;i<faces.size();i++)
				{
					faces.get(i).paint(gr);
				}
				
				for(int i =0;i<segs.size();i++)
				{
	//					segs[i].midpoint = ;
	//				System.out.println(segs.get(i).end1+" "+segs.get(i).end2);
					Vect3D camLoc = cam.getRelative(Vect3D.midPoint(vects.get(segs.get(i).end1), vects.get(segs.get(i).end2)));
					orderSegs.put(-camLoc.y+camLoc.z/1000.0+camLoc.x/1000.0,segs.get(i));
				}
				Object[] keys = orderSegs.keySet().toArray();
				for(int i =0;i<keys.length;i++)
				{
					orderSegs.get(keys[i]).paint((Graphics2D)g,cam,vects,getWidth(),getHeight());
				}
				for(int i=0;i<bloods.size();i++)
				{
					bloods.get(i).update(dt);
					bloods.get(i).paint(g, cam, w, h);
				}
				if(dragRect!=null)
				{
					g.setColor(Color.WHITE);
					g.drawRect(dragRect.x, dragRect.y, dragRect.width, dragRect.height);
					g.setColor(Color.BLACK);
					g.drawRect(dragRect.x+1, dragRect.y+1, dragRect.width-2, dragRect.height-2);
				}
	//			g.setColor(Color.BLACK);
	//			g.setFont(new Font("Dialog",Font.BOLD,12));
	//			g.drawString("Epsilon Animator", 2, getHeight()-2);
	//			g.setColor(Color.DARK_GRAY);
	//			g.drawString("Epsilon Animator", 3, getHeight()-3);
				paintAxes(g);
				
				BufferedImage tmp = livescr;
				livescr = scr;
				scr = tmp;
			}
			paint=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void paintAxes(Graphics g)
	{
		int h = getHeight();
		Point c = new Point(40,h-40);
		Vect3D vx = new Vect3D(45,0,0);
		Vect3D vy = new Vect3D(0,45,0);
		Vect3D vz = new Vect3D(0,0,45);
		vx=vx.getAxis(cam.getRight(), cam.look, cam.up);
		vy=vy.getAxis(cam.getRight(), cam.look, cam.up);
		vz=vz.getAxis(cam.getRight(), cam.look, cam.up);
		Point xx=new Point((int)vx.x+40,(int)vx.z+40);
		Point yy=new Point((int)vy.x+40,(int)vy.z+40);
		Point zz=new Point((int)vz.x+40,(int)vz.z+40);
		g.setColor(new Color(0,0,0,128));
		g.fillRect(0, h-14, 28, 14);
		g.setColor(new Color(255,0,0));
		g.drawString("X", 3, h-3);
		g.drawLine(c.x,c.y,xx.x,h-xx.y);
		g.setColor(Color.GREEN);
		g.drawString("Y", 11, h-3);
		g.drawLine(c.x,c.y,yy.x,h-yy.y);
		g.setColor(new Color(0,64,255));
		g.drawString("Z", 19, h-3);
		g.drawLine(c.x,c.y,zz.x,h-zz.y);
	}
	//bias (0-1) is percent between frames
	public void apply(Frame f1,Frame f2,float bias)
	{
		vects.clear();
		segs.clear();
		faces.clear();
//		System.out.println(f.points);
		for(int i=0;i<f1.points.size();i++)
		{
//			System.out.println((Vect3D)f.points.get(f.points.keySet().toArray()[i]));
			int a=(Integer)f1.points.keySet().toArray()[i];
			int b=(Integer)f2.points.keySet().toArray()[i];
			Vect3D v = Vect3D.midPointWt((Vect3D)f1.points.get(a),(Vect3D)f2.points.get(b),bias);
			v.spColor=((Vect3D)f1.points.get(a)).spColor;
			vects.put((Integer)f1.points.keySet().toArray()[i],v);
		}
		lengths();
		for(int i=0;i<f1.segments.size();i++)
		{
			segs.add(f1.segments.get(i));
		}
		for(int i=0;i<f1.faces.size();i++)
		{
			faces.add(f1.faces.get(i));
		}
		Vect3D camLoc=Vect3D.midPointWt(f1.camera.loc, f2.camera.loc, bias);
		Vect3D camLook=Vect3D.midPointWt(f1.camera.look, f2.camera.look, bias);
		camLook.setMagnitude(1);
		Vect3D camUp=Vect3D.midPointWt(f1.camera.up, f2.camera.up, bias);
		Vect3D right = Vect3D.crossProduct(camLook, camUp);
		camUp = Vect3D.crossProduct(right,camLook);
		camUp.setMagnitude(1);
		realCam=new Camera(camUp,camLook,camLoc);
		cam = new Camera(camUp,camLook,camLoc);
//		frameTime=f.time;
	}
	public void apply(Frame f)
	{
		vects.clear();
		segs.clear();
		faces.clear();
//		System.out.println(f.points);
		for(int i=0;i<f.points.size();i++)
		{
//			System.out.println((Vect3D)f.points.get(f.points.keySet().toArray()[i]));
			vects.put((Integer)f.points.keySet().toArray()[i],(Vect3D)f.points.get(f.points.keySet().toArray()[i]));
		}
		for(int i=0;i<f.segments.size();i++)
		{
			segs.add(f.segments.get(i));
		}
		for(int i=0;i<f.faces.size();i++)
		{
			faces.add(f.faces.get(i));
		}
		realCam=f.camera.clone();
//		cam=realCam.clone();
		frameTime=f.time;
//		System.out.println("apply: "+vects.get(0));
	}
	public Frame getFrame()
	{
		TreeMap<Integer,Vect3D> p = new TreeMap<Integer,Vect3D>();
		ArrayList<Segment> s = new ArrayList<Segment>();
		ArrayList<Surface> f = new ArrayList<Surface>();
		for(int i=0;i<vects.size();i++)
		{
			p.put((Integer)(vects.keySet().toArray()[i]), (vects.get((vects.keySet().toArray()[i]))).clone());
		}
		for(int i=0;i<segs.size();i++)
		{
			s.add(segs.get(i));
		}		
		for(int i=0;i<faces.size();i++)
		{
			f.add(faces.get(i));
		}
		
		return new Frame(p,s,f,realCam.clone(),frameTime);
	}
	public void paintComponent(Graphics g)
	{
		if(g!=null && getHeight()!=0 && getWidth()!=0 && livescr!=null)
		{
			g.drawImage(livescr,0,0,null);
		}
	}
}
