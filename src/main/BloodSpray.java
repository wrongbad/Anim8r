package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class BloodSpray
{
	Vect3D dir;
	Vect3D loc;
	Vect3D[] particles = new Vect3D[50];
	Vect3D[] speeds = new Vect3D[50];
	long start=0;
	int life=1500;
	boolean dead=false;
	
	public BloodSpray(Vect3D start,Vect3D direction)
	{
		dir=direction.clone();
		loc=start.clone();
		for(int i=0;i<particles.length;i++)
		{
			particles[i]=loc.clone();
//			speeds[i]=dir.getScaled(0.000001f);
			speeds[i]=Vect3D.add( dir.getScaled((float)Math.random()*0.5f+0.5f) , new Vect3D((float)Math.random()-0.5f,(float)Math.random()-0.5f,(float)Math.random()-0.5f).getScaled(0.5f) ).getScaled(3.5f);
//			System.out.println(speeds[i]);
		}
//		speeds[0]=new Vect3D(0,0,0.1f);
//		speeds[1]=new Vect3D(-0.1f,0,0);
	}

	public void update(float dt)
	{
		if(start==0) start=System.currentTimeMillis();
		if(!dead)
		for(int i=0;i<particles.length;i++)
		{
//			System.out.println(speeds[i]);
			particles[i].move(speeds[i].getScaled(dt));
//			System.out.println(i+"    "+particles[i]+"    "+speeds[i]);//.getScaled(dt));
//			System.out.println(dt);
			speeds[i].move(new Vect3D(0,0,-1f*dt));
		}
		if(System.currentTimeMillis()-start>life) dead=true;
	}
	public void paint(Graphics g,Camera cam,int w,int h)
	{
		if(!dead)
		for(int i=0;i<particles.length;i++)
		{
			g.setColor(new Color((int)(Math.random()*192+64),0,0));
			Point p = new Point(cam.get2D(cam.getRelative(particles[i]),w,h));
//			System.out.println(particles[i]);
//			System.out.println(i+"  "+particles[i]);
			if(System.currentTimeMillis()-start<life/2)
			g.drawLine(p.x, p.y, p.x+1, p.y+1);
			else
			g.drawLine(p.x, p.y, p.x, p.y);
		}
	}
	public void paintEdit(Graphics g,Camera cam,int w,int h)
	{
		
	}
}
