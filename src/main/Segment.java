package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.TreeMap;

public class Segment
{
	public int end1;
	public int end2;
//	public Vect3D midpoint;
	public float jRad1 = .2f;
	public float jRad2 = .2f;
	public float length = 0;
//	static float wScale=1;
//	public static Canvas owner;
	
	public Segment(int a, int b)
	{
		end1=a;
		end2=b;
//		end1.neigh.add(end2);
//		end2.neigh.add(end1);
	}
	public Segment(int a, int b,float rad1,float rad2)
	{
		end1=a;
		end2=b;
//		end1.neigh.add(end2);
//		end2.neigh.add(end1);
		jRad1=rad1;
		jRad2=rad2;
	}
	public boolean isLegit(TreeMap<Integer,Vect3D> v)
	{
		if(v.get(end1)==null || v.get(end2)==null)
			return false;
		return true;
	}
	public void paint(Graphics gr,Camera cam,TreeMap<Integer,Vect3D> v,int width, int height)
	{
		if(v.get(end1)==null || v.get(end2)==null)
			return;
		Point p1 = cam.get2D(cam.getRelative(v.get(end1)),width,height);
		Point p2 = cam.get2D(cam.getRelative(v.get(end2)),width,height);
		float rad1 = cam.getScale(cam.getRelative(v.get(end1)),width);
		float rad2 = cam.getScale(cam.getRelative(v.get(end2)),width);
//		float rad1 = .5f;
//		float rad2 = .5f;
//		System.out.println(cam);
		if(p1==null || p2==null) return;
		float l = (float)p1.distance(p2);
		float xDir = (p1.y-p2.y)/l;
		float yDir = (p2.x-p1.x)/l;
		int[] xx = new int[4];
		int[] yy = new int[4];
		xx[0] = (int)(p2.x+xDir*jRad2*rad2+.5);
		xx[1] = (int)(p1.x+xDir*jRad1*rad1+.5);
		xx[2] = (int)(p1.x-xDir*jRad1*rad1+.5);
		xx[3] = (int)(p2.x-xDir*jRad2*rad2+.5);
		yy[0] = (int)(p2.y+yDir*jRad2*rad2+.5);
		yy[1] = (int)(p1.y+yDir*jRad1*rad1+.5);
		yy[2] = (int)(p1.y-yDir*jRad1*rad1+.5);
		yy[3] = (int)(p2.y-yDir*jRad2*rad2+.5);
		
		Graphics2D g = (Graphics2D)gr;
		RenderingHints hints = new RenderingHints(null);
        hints.put(RenderingHints.KEY_ANTIALIASING,
        		RenderingHints.VALUE_ANTIALIAS_ON);
        g.addRenderingHints(hints);
        g.setColor(Color.DARK_GRAY);
        g.fillPolygon(xx,yy,4);
//        g.drawLine(xx[0],yy[0],xx[1],yy[1]);
//        g.drawLine(xx[2],yy[2],xx[3],yy[3]);
        g.setColor(Color.GRAY);
        g.drawLine(p1.x,p1.y,p2.x,p2.y);
//        g.setColor(Color.GRAY);
//        g.drawPolygon(xx,yy,4);
        int rad = (int)(jRad1*rad1+.5);
        int radb = (int)(jRad2*rad2+.5);
        
        if(v.get(end1).selected==1)
        	g.setColor(new Color(0,128,0));
        else if(v.get(end1).selected==2)
        	g.setColor(new Color(255,0,0));
        else if(v.get(end1).selected==3)
        	g.setColor(new Color(255,192,0));
        else
        	g.setColor(Vect3D.getColor(v.get(end1).spColor));
        g.fillOval(p1.x-rad,p1.y-rad,rad*2,rad*2);
        
        if(v.get(end2).selected==1)
        	g.setColor(new Color(0,128,0));
        else if(v.get(end2).selected==2)
        	g.setColor(new Color(255,0,0));
        else if(v.get(end2).selected==3)
        	g.setColor(new Color(255,192,0));
        else
        	g.setColor(Vect3D.getColor(v.get(end2).spColor));
        g.fillOval(p2.x-radb,p2.y-radb,radb*2,radb*2);
        
        v.get(end1).paint(g, cam, width, height, .1f);
        v.get(end2).paint(g, cam, width, height, .1f);
	}
//	public void updateForces(float dt)
//	{
//		Vect3D diff=Vect3D.subtract(end1, end2);
////		System.out.println(diff.getMagnitude());
//		diff=diff.getScaled(length/diff.getMagnitude());
//		end1.set(Vect3D.midPoint(Vect3D.add(end2, diff),end1));
//		end2.set(Vect3D.subtract(end1, diff));
////		end1.velocity=Vect3D.add(end1.velocity,diff.getScaled(dt*.2f*(1-)));
////		end2.velocity=Vect3D.add(end2.velocity,diff.getScaled(dt*.2f*(diff.getMagnitude()/length-1)));
//	}
	public String toString()
	{
		return "("+end1+","+end2+","+jRad1+","+jRad2+")";
	}
	public static Segment parseSeg(String s)
	{
		String[] st = s.substring(1,s.length()-1).split(",");
		return new Segment(Integer.parseInt(st[0]),Integer.parseInt(st[1]),Float.parseFloat(st[2]),Float.parseFloat(st[3]));
	}
	public void fixLength(TreeMap<Integer,Vect3D> v)
	{

		int a = Math.min(end1, end2);
		int b = Math.max(end1, end2);
		Vect3D d = Vect3D.subtract(v.get(b), v.get(a));
		float diff=d.getMagnitude();
		if(length==0)
		{
			length=diff;
			return;
		}
		if(length==diff)
			return;
		d=d.getScaled((0.5f - length/diff/2));
		v.get(b).move(d.getScaled(-1));
		v.get(a).move(d);
	}
	public void fixLength2(TreeMap<Integer,Vect3D> v)
	{

		int a = Math.min(end1, end2);
		int b = Math.max(end1, end2);
		Vect3D d = Vect3D.subtract(v.get(b), v.get(a));
		float diff=d.getMagnitude();
		if(length==0)
		{
			length=diff;
			return;
		}
		d=d.getScaled(1 - length/diff);
		v.get(b).move(d);
	}
}
