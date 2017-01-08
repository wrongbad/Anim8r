package main;

import java.awt.Point;

public class Camera
{
	//toward top of screen
	Vect3D up;
	//normal to view plane/center of sight line
	Vect3D look;
	//location
	Vect3D loc;
	//the proportion of projected screen width/distance 
	public static float tanFOV=.4f;
	boolean perspective = true;

	
	public Camera()
	{
		reset();
	}
	public Camera clone()
	{
		return new Camera(up,look,loc);
	}
	public void reset()
	{
		up=new Vect3D(0,0,1);
		look=new Vect3D(0,1,0);
		loc=new Vect3D(0,-15,0);
	}
	public Vect3D getRight()
	{
		return Vect3D.crossProduct(look, up);
	}
	public Camera(Vect3D u,Vect3D l,Vect3D lo)
	{
		up=u;
		look=l;
		loc=lo;
	}

	public Point get2D(Vect3D p,int width, int height)
	{
		if(p.y<=0.001)
			p.y=0.001f;
		Vect3D v;
		if(perspective)
			v=p.getScaled(1/Math.abs(p.y*tanFOV)); 
		else
		{
			float y = -Vect3D.dotProduct(loc,look);
			v=p.getScaled(1/Math.abs(y*tanFOV));
		}
		int offY = (width-height)/2;
		return new Point((int)((v.x+1)/2*width)  ,  (int)((-v.z+1)/2*width)-offY );
	}

	public Vect3D getProjection(Point p,int width,int height)
	{
		if(perspective)
		{
			int offY = (width-height)/2;
			float x=p.x/(float)width*2-1;
			float z=-((p.y+offY)/(float)width*2)+1;
			Vect3D v=new Vect3D(x*tanFOV,1,z*tanFOV);
			v.setMagnitude(1);
			v=v.getUnAxis(getRight(), look, up);
			return v;
		}
		else
		{
			return new Vect3D(0,1,0);
		}
	}
	public Vect3D getParallelOffset(Point p,int width,int height)
	{
		if(perspective)
			return loc;
		
		int offY = (width-height)/2;
		float x=p.x/(float)width*2-1;
		float z=-((p.y+offY)/(float)width*2)+1;
		Vect3D v=new Vect3D(x,0,z);
		float y = -Vect3D.dotProduct(loc,look);
		v=v.getScaled(Math.abs(y));
		v=v.getUnAxis(getRight(), look, up);
		return Vect3D.add(v,loc);
	}
	public Vect3D getRelative(Vect3D v)
	{
		Vect3D p = Vect3D.subtract(v, loc);
		Vect3D right = Vect3D.crossProduct(look, up);
		float x = Vect3D.dotProduct(p, right);
		float y = Vect3D.dotProduct(p, look);		
		float z = Vect3D.dotProduct(p, up);
		return new Vect3D(x,y,z);
	}
	public Vect3D getUnRelative(Vect3D v)
	{
		Vect3D right = Vect3D.crossProduct(look, up);
		return Vect3D.add(loc,Vect3D.add(Vect3D.add(up.getScaled(-v.z),look.getScaled(v.y)),right.getScaled(v.x)));
	}
	//determines how much smaller or bigger an object at v is in perspective
	public float getScale(Vect3D v,int width)
	{
		if(perspective)
		{
			float s=width/Math.abs(tanFOV*v.y*2);
//			System.out.println(s+"  "+width+"  "+v);
			return s;
		}
		else
		{
			float y = -Vect3D.dotProduct(loc,look);
			return width/Math.abs(tanFOV*y*2);
		}
	}
	public String toString()
	{
		return "cam:"+up.x+","+up.y+","+up.z+","+look.x+","+look.y+","+look.z+","+loc.x+","+loc.y+","+loc.z;
	}
	public static Camera parseCam(String s)
	{
		String[] st = s.substring(4,s.length()).split(",");
		return new Camera(new Vect3D(Float.parseFloat(st[0]),Float.parseFloat(st[1]),Float.parseFloat(st[2])),
						  new Vect3D(Float.parseFloat(st[3]),Float.parseFloat(st[4]),Float.parseFloat(st[5])),
						  new Vect3D(Float.parseFloat(st[6]),Float.parseFloat(st[7]),Float.parseFloat(st[8])));
	}
}
