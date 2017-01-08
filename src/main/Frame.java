package main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeMap;

public class Frame
{
	TreeMap<Integer,Vect3D> points = new TreeMap<Integer,Vect3D>();
	ArrayList<Segment> segments = new ArrayList<Segment>();
	ArrayList<Surface> faces = new ArrayList<Surface>();
	ArrayList<BloodSpray> bs = new ArrayList<BloodSpray>();
	Camera camera;
	int time;
	
	public Frame(TreeMap<Integer,Vect3D> pts,ArrayList<Segment> segs,ArrayList<Surface> sf,Camera cam,int t)
	{
		for(int i=0;i<pts.size();i++)
		{
			points.put((Integer)(pts.keySet().toArray()[i]), (pts.get((pts.keySet().toArray()[i]))).clone());
		}
		for(int i=0;i<segs.size();i++)
		{
			segments.add(segs.get(i));
		}		
		for(int i=0;i<sf.size();i++)
		{
			faces.add(sf.get(i));
		}
		camera=cam;
		time=t;
	}
	public Frame clone()
	{
		TreeMap<Integer,Vect3D> p = new TreeMap<Integer,Vect3D>();
		ArrayList<Segment> s = new ArrayList<Segment>();
		ArrayList<Surface> f = new ArrayList<Surface>();
		for(int i=0;i<points.size();i++)
		{
			p.put((Integer)(points.keySet().toArray()[i]), (points.get((points.keySet().toArray()[i]))).clone());
		}
		for(int i=0;i<segments.size();i++)
		{
			s.add(segments.get(i));
		}		
		for(int i=0;i<faces.size();i++)
		{
			f.add(faces.get(i));
		}
		return new Frame(p,s,f,camera.clone(),time);
	}
	public static LinkedList<Frame> readFrames(Scanner in)
	{
		LinkedList<Frame> frames = new LinkedList<Frame>();
		try
		{
			ArrayList<Segment> segs = new ArrayList<Segment>();
			ArrayList<Surface> faces = new ArrayList<Surface>();
			TreeMap<Integer,Vect3D> vects = new TreeMap<Integer,Vect3D>();
			in.reset();
			String s="";
//			Frame f;
			int stage=0;
			int time=0;
			Camera cam=null;
			while(in.hasNextLine())
			{
				s=in.nextLine();
				if(s.trim().length()==0)
				{
					stage=(stage+1)%5;
//					System.out.println(s);
					if(stage==0)
					{
						Frame f = new Frame( vects,	segs, faces, cam, time);
						frames.add(f);
//						System.out.println(vects);
//						System.out.println(f.points);
						vects.clear();
						segs.clear();
						faces.clear();
					}
				}
				else
				{
					if(stage==0)
					{
						Vect3D v = Vect3D.parseVect(s);
						vects.put(v.index,v);
					}
					if(stage==1)
					{
						segs.add(Segment.parseSeg(s));
					}
					if(stage==2)
					{
						faces.add(Surface.parseFace(s, vects));
					}
					if(stage==3)
					{
						cam=Camera.parseCam(s);
					}
					if(stage==4)
					{
						time=Integer.parseInt(s);
					}
				}
			}
			in.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
//		System.out.println(frames.get(0).points);
		return frames;
	}
	
	
	public static void writeFrames(PrintStream p,Frame[] frames)
	{
//		p.println(frames.length);
		for(int i = 0;i<frames.length;i++)
		{
			Frame f = frames[i];
			for(int j = 0;j<f.points.size();j++)
			{
//				if(i>20 && j>11 && j<24)
//					p.println(frames[20].points.get((frames[20].points.keySet().toArray()[j])));
//				else
					p.println(f.points.get((f.points.keySet().toArray()[j])));
			}
			p.println();
			for(int j = 0;j<f.segments.size();j++)
			{
				if(f.segments.get(j).isLegit(f.points));
					p.println(f.segments.get(j));
			}
			p.println();
			for(int j = 0;j<f.faces.size();j++)
			{
				p.println(f.faces.get(j));
			}
			p.println();
//			if(i>60 && i<100)
//			{
//				Frame f1=frames[60];
//				Frame f2=frames[frames.length-1];
//				float bias=(i-60)/(float)(100-61);
//				Vect3D camLoc=Vect3D.midPointWt(f1.camera.loc, f2.camera.loc, bias);
//				Vect3D camLook=Vect3D.midPointWt(f1.camera.look, f2.camera.look, bias);
//				camLook=camLook.getScaled(1/camLook.getMagnitude());
//				Vect3D camUp=Vect3D.midPointWt(f1.camera.up, f2.camera.up, bias);
//				Vect3D right = Vect3D.crossProduct(camLook, camUp);
//				camUp = Vect3D.crossProduct(right,camLook);
//				camUp=camUp.getScaled(1/camUp.getMagnitude());
//				
//				p.println(new Camera(camUp,camLook,camLoc));
//			}
//			else if(i>60)
//			{
//				Frame f2=frames[frames.length-1];
//				p.println(f2.camera);
//			}
//			else
			Vect3D loc=new Vect3D(-10,13.76f,2);
			Vect3D look=new Vect3D(0.5f,-0.688f,0);
			p.println(new Camera(new Vect3D(0,0,1),look,loc));
//				p.println(f.camera);
			
			p.println();
			p.println(f.time);
			p.println();
		}
		p.close();
	}
}
