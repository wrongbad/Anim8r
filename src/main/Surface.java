package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.TreeMap;

import sun.awt.geom.Curve;

public class Surface
{
	//size 3 please
	Vect3D[] vects;
	float shade;
	Area poly;
	Color color;
	
	public Surface(Vect3D[] vts,Color c)
	{
		vects = vts;
		color=c;
	}
	public void findPoly(Camera cam, int w, int h)
	{
		int[] x = new int[vects.length];
		int[] y = new int[vects.length];
		boolean good=false;
		for(int i=0;i<vects.length;i++)
		{
			Point p = cam.get2D(cam.getRelative(vects[i]),w,h);
//			if p==null
			x[i]=p.x;
			y[i]=p.y;
			if(p.x<w || p.x>0 || p.y<h || p.y>0)
				good=true;
		}
		poly = new Area(new Polygon(x,y,vects.length));
		if(good==false)// && !poly.intersects(new Rectangle(0,0,w,h)))
		{
			poly=null;
			return;
		}
		Vect3D v1 = Vect3D.subtract(vects[0], vects[1]);
		Vect3D v2 = Vect3D.subtract(vects[2], vects[1]);
		Vect3D orth = Vect3D.crossProduct(v2, v1);
		orth.setMagnitude(1);
		Vect3D dir;
		if(cam.perspective)
		{
			dir  = Vect3D.subtract(cam.loc,Vect3D.average(vects));
			dir.setMagnitude(1);
		}
		else
			dir=cam.look.getScaled(-1);
//		System.out.println(shade);
		shade = Vect3D.dotProduct(orth,dir);
        if(shade>0)
        	shade=shade*0.6f+0.35f;
//        shade=Math.abs(shade);
	}
	public void paint(Graphics2D g)
	{
        if(shade>0 && poly!=null)// && !poly.isEmpty())
        {
    		RenderingHints hints = new RenderingHints(null);
            hints.put(RenderingHints.KEY_ANTIALIASING,
            		RenderingHints.VALUE_ANTIALIAS_ON);
            g.addRenderingHints(hints);
			g.setColor(new Color((int)(color.getRed()*shade),(int)(color.getGreen()*shade),(int)(color.getBlue()*shade)));
			g.fill(poly);
//			vects[1].paint((Graphics)g, cam, w, h, .1f);
        }
	}
	public static Point avgPoint(Area a,int xx)
	{
		double x=0;
		double y=0;
		int i=0;
		if(!a.isEmpty())
		{
			PathIterator p = a.getPathIterator(null);
			double coords[] = new double[23];
			
			while(!p.isDone())
			{
			    switch (p.currentSegment(coords))
			    {
				    case PathIterator.SEG_MOVETO:
				    	x+=coords[0];
				    	y+=coords[1];
						i++;
					break;
				    case PathIterator.SEG_LINETO:
						x+=coords[0];
						y+=coords[1];
						i++;
					break;
				    case PathIterator.SEG_QUADTO:
						x+=coords[2];
						y+=coords[3];
						i++;
					break;
				    case PathIterator.SEG_CUBICTO:
						x+=coords[4];
						y+=coords[5];
						i++;
					break;
				    case PathIterator.SEG_CLOSE:
					break;
			    }
				p.next();
			}
			x/=i;
			y/=i;
		}
		Point p=new Point((int)(x+0.5),(int)(y+0.5));
//		System.out.println(p);
		return p;
	}
	public static Point avgPoint(Area a)
	{
		double x=0;
		double y=0;
		int i=0;
		if(!a.isEmpty())
		{
			PathIterator p = a.getPathIterator(null);
			double coords[] = new double[23];
			
			while(!p.isDone())
			{
			    switch (p.currentSegment(coords))
			    {
				    case PathIterator.SEG_MOVETO:
				    	x+=coords[0];
				    	y+=coords[1];
				    	i++;
					break;
				    case PathIterator.SEG_LINETO:
						x+=coords[0];
						y+=coords[1];
				    	i++;
					break;
				    case PathIterator.SEG_QUADTO:
						x+=coords[2];
						y+=coords[3];
				    	i++;
					break;
				    case PathIterator.SEG_CUBICTO:
						x+=coords[4];
						y+=coords[5];
				    	i++;
					break;
				    case PathIterator.SEG_CLOSE:
					break;
			    }
				p.next();
			}
			x/=i;
			y/=i;
		}
		Point p=new Point((int)(x+0.5),(int)(y+0.5));
		return p;
	}
	public boolean isBehind(Surface other,int w,int h,Camera cam)
	{
		if(other.shade<0 || other.poly==null || poly==null)
			return false;
		Area a1=new Area(poly);
		Area a2=new Area(other.poly);
		a1.intersect(a2);
		if(!a1.isEmpty())
		{
			Point pt=avgPoint(a1);
//			System.out.println(x+" "+y);
			Vect3D dir=cam.getProjection(pt, w, h);
//			dir=dir.getScaled(1/dir.getMagnitude());
			Vect3D start;
			if(cam.perspective)
				start=cam.loc;
			else
				start=cam.getParallelOffset(pt, w, h);
			Vect3D p1=rayIntersect(start,dir);
			Vect3D p2=other.rayIntersect(start,dir);
			p1=cam.getRelative(p1);
			p2=cam.getRelative(p2);
			if(p1.getMagnitudeSQ()>p2.getMagnitudeSQ())
				return true;
		}
		return false;
	}
	
	//translates everythiong so surface is flat on xy plane and lined up w/ x axis so math is easy
	public Vect3D rayIntersect(Vect3D start, Vect3D ray)
	{
		Vect3D v1 = Vect3D.subtract(vects[0], vects[1]);
		Vect3D x = Vect3D.subtract(vects[2], vects[1]);
		Vect3D z = Vect3D.crossProduct(x, v1);
		if(Vect3D.dotProduct(Vect3D.subtract(start, vects[1]),z )<0 )
			z=z.getScaled(-1);
		x.setMagnitude(1);
		z.setMagnitude(1);
		Vect3D y=Vect3D.crossProduct(z, x);
//		System.out.println(x+" "+y+" "+z);
		Vect3D st=start.clone();
		Vect3D r=ray.clone();
		st=Vect3D.subtract(st, vects[1]);
//		System.out.println(r);
		st=st.getAxis(x, y, z);
		r=r.getAxis(x, y, z);
//		System.out.println(st);
		r=r.getScaled(-st.z/r.z);
		Vect3D inter=Vect3D.add(st,r);
		inter=inter.getUnAxis(x, y, z);
		inter=Vect3D.add(inter, vects[1]);
//		System.out.println(inter);
		return inter;
	}
	//line from p1-p2 and line from p3-p4
	public static Point lineIntersect(Point p1,Point p2,Point p3,Point p4)
	{
		int x0,x1,x2,x3;
		int y0,y1,y2,y3;
		if(p1.x>p2.x)
		{
			x0=p2.x;
			y0=p2.y;
			x1=p1.x;
			y1=p1.y;
		}
		else
		{
			x0=p1.x;
			y0=p1.y;
			x1=p2.x;
			y1=p2.y;
		}
		if(p3.x>p4.x)
		{
			x2=p4.x;
			y2=p4.y;
			x3=p3.x;
			y3=p3.y;
		}
		else
		{
			x2=p3.x;
			y2=p3.y;
			x3=p4.x;
			y3=p4.y;
		}
		if(Math.min(p3.x, p4.x)>Math.max(p1.x, p2.x)
		 ||Math.max(p3.x, p4.x)<Math.min(p1.x, p2.x)
		 ||Math.min(p3.y, p4.y)>Math.max(p1.y, p2.y)
		 ||Math.max(p3.y, p4.y)<Math.min(p1.y, p2.y))
			return null;
		if(x1-x0==0 && x3-x2==0)
			return null;
		//y=mx+b
		if(x1-x0==0)
		{
			float m1=(y3-y2)/(float)(x3-x2);
			float b1=y2-m1*x2;
			int y=(int)(m1*x0+b1+0.5);
			if(y>=Math.min(y2,y3) && y<=Math.max(y2,y3))
				return new Point(x0,y);
		}
		else if(x3-x2==0)
		{
			float m0=(y1-y0)/(float)(x1-x0);
			float b0=y0-m0*x0;
			int y=(int)(m0*x2+b0+0.5);
			if(y>=Math.min(y2,y3) && y<=Math.max(y2,y3))
				return new Point(x2,y);
		}
		else
		{
			float m0=(y1-y0)/(float)(x1-x0);
			float b0=y0-m0*x0;
			float m1=(y3-y2)/(float)(x3-x2);
			float b1=y2-m1*x2;
			if(m0==m1)
				return null;
			int x=(int)((b0-b1)/(m1-m0)+0.5);
			int y=(int)(m0*x+b0);
//			System.out.println(x+" "+y);
			if(x>=Math.min(x0,x1) && x<=Math.max(x0,x1) &&
				x>=Math.min(x2,x3) && x<=Math.max(x2,x3))
				return new Point(x,y);
		}
		return null;
	}
	
	public String toString()
	{
		String s = "";
		for(int i = 0;i<vects.length;i++)
		{
			s+=vects[i].index+",";
		}
		s+=color.getRGB();
		return s;
	}
	public static Surface parseFace(String s,TreeMap<Integer,Vect3D> vects)
	{
		String[] st = s.split(",");
		Vect3D[] vt = new Vect3D[st.length-1];
		for(int i=0;i<st.length-1;i++)
		{
			vt[i] = vects.get(Integer.parseInt(st[i]));
		}
		return new Surface(vt,new Color(Integer.parseInt(st[st.length-1])));
	}
}
