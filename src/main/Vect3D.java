package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Vect3D
{
	protected float x;
	protected float y;
	protected float z;
	protected int index = -1;
	protected byte selected=0;
	/**
	 * 0=none
	 * 1=red
	 * 2=green
	 * 3=blue
	 * 4=cyan
	 * 6=magenta
	 * 5=yellow
	 */
	int spColor=0;
	
	public Vect3D(float xa,float ya,float za)
	{
		x=xa;
		y=ya;
		z=za;
	}
	public Vect3D(float xa,float ya,float za,int i)
	{
		x=xa;
		y=ya;
		z=za;
		index = i;
	}
	public void set(Vect3D v)
	{
		x=v.x;
		y=v.y;
		z=v.z;
	}
	public static Vect3D midPoint(Vect3D v1, Vect3D v2)
	{
		return new Vect3D((v1.x+v2.x)/2,(v1.y+v2.y)/2,(v1.z+v2.z)/2);
	}
	//the mid point that ain't in the middle, bias is 0-1, 0=1st point 1=2nd point
	public static Vect3D midPointWt(Vect3D v1, Vect3D v2, float bias)
	{
		return new Vect3D((v1.x*(1-bias)+v2.x*bias),(v1.y*(1-bias)+v2.y*bias),(v1.z*(1-bias)+v2.z*bias));
	}
	public static Vect3D average(Vect3D[] v)
	{
		Vect3D p = new Vect3D(0,0,0);
		for(int i = 0;i<v.length;i++)
		{
			p.move(v[i]);
		}
		return p.getScaled(1f/v.length);
	}
	public Vect3D clone()
	{
		Vect3D p = new Vect3D();
		p.x=x;
		p.y=y;
		p.z=z;
		p.index=index;
		p.spColor=spColor;
		return p;
	}
	
	public Vect3D()
	{
	}
	public void set(float xa,float ya,float za)
	{
		setX(xa);
		setY(ya);
		setZ(za);
	}
	public float getX()
	{
		return x;
	}
	public float getY()
	{
		return y;
	}
	public float getZ()
	{
		return z;
	}
	public void setX(float xa)
	{
		if(xa==-0) xa=0;
		x=xa;
	}
	public void setY(float ya)
	{
		if(ya==-0) ya=0;
		y=ya;
	}
	public void setZ(float za)
	{
		if(za==-0) za=0;
		z=za;
	}
	public void setMagnitude(float m)
	{
		float a=getMagnitude()/m;
		x/=a;
		y/=a;
		z/=a;
	}
	public Vect3D getAxis(Vect3D x,Vect3D y,Vect3D z)
	{
		return new Vect3D(dotProduct(x,this),dotProduct(y,this),dotProduct(z,this));
	}
	public Vect3D getUnAxis(Vect3D x,Vect3D y,Vect3D z)
	{
		return Vect3D.add(x.getScaled(this.x),y.getScaled(this.y),z.getScaled(this.z));
	}
	public float getMagnitude()
	{
		return (float)Math.sqrt((x*x)+(y*y)+(z*z));
	}
	public float getMagnitudeSQ()
	{
		return ((x*x)+(y*y)+(z*z));
	}
	public String toString()
	{
		String s = "<"+x+","+y+","+z+","+index+","+spColor+">";
		return s;
	}
	public static Vect3D parseVect(String s)
	{
		String[] s2 = s.substring(1,s.length()-1).split(",");
		Vect3D v = new Vect3D(Float.parseFloat(s2[0]),Float.parseFloat(s2[1]),Float.parseFloat(s2[2]));
		v.index = Integer.parseInt(s2[3]);
		if(s2.length>4)
		{
			v.spColor = Integer.parseInt(s2[4]);
//			System.out.println(v.spColor);
		}
		return v;
	}
	public boolean equals(Object o)
	{
		if(o.getClass()==this.getClass())
		{
			Vect3D p = (Vect3D)o;
			return( Math.abs(p.x-x)<.0001 && Math.abs(p.y-y)<.0001 && Math.abs(p.z-z)<.0001 );
		}
		else
			return false;
	}
	public void move(Vect3D p)
	{
		setX(x+p.x);
		setY(y+p.y);
		setZ(z+p.z);
	}
	public static Vect3D add(Vect3D p1, Vect3D p2, Vect3D p3)
	{
		return new Vect3D(p1.getX()+p2.getX()+p3.getX(),p1.getY()+p2.getY()+p3.getY(),p1.getZ()+p2.getZ()+p3.getZ());
	}	
	public static Vect3D add(Vect3D p1, Vect3D p2)
	{
		return new Vect3D(p1.getX()+p2.getX(),p1.getY()+p2.getY(),p1.getZ()+p2.getZ());
	}
	public static Vect3D subtract(Vect3D p1, Vect3D p2)
	{
		return new Vect3D(p1.getX()-p2.getX(),p1.getY()-p2.getY(),p1.getZ()-p2.getZ());
	}
	public static Vect3D crossProduct(Vect3D p1, Vect3D p2)
	{
		Vect3D v = new Vect3D((p1.y*p2.z)-(p1.z*p2.y),
							  (p1.z*p2.x)-(p1.x*p2.z),
							  (p1.x*p2.y)-(p1.y*p2.x));
//		if(Math.abs(p1.getMagnitudeSQ()*p2.getMagnitudeSQ()-1)>.00001)
//		if(v.getMagnitudeSQ()>0)
//			v=v.getScaled(1/v.getMagnitude());
		return v;
	}
	public static float dotProduct(Vect3D p1, Vect3D p2)
	{
		return (p1.x*p2.x+p1.y*p2.y+p1.z*p2.z);
	}
	public static float[] rotate2D(float xa,float ya,float cos,float sin)
	{
//		float x=xa;
//		float y=ya;
//		x=(float)  (  xa*cos - ya*sin );
//		y=(float)  (  xa*sin + ya*cos );
		return new float[]{(  xa*cos - ya*sin ),(  xa*sin + ya*cos )};
	}
	public Vect3D getScaled(float scale)
	{
		return new Vect3D(getX()*scale,getY()*scale,getZ()*scale);
	}
//	public void getScaled(Vect3D scale)
//	{
//		setX(getX()*scale.getX());
//		setY(getY()*scale.getY());
//		setZ(getZ()*scale.getZ());
//	}
	public static float distanceSq(Vect3D p1,Vect3D p2)
	{
		Vect3D p3 = Vect3D.subtract(p1, p2);
//		System.out.println(p3);
		return ((p3.getX()*p3.getX())+(p3.getY()*p3.getY())+(p3.getZ()*p3.getZ()));
	}
	public static float distance(Vect3D p1,Vect3D p2)
	{
		return (float)Math.sqrt(distanceSq(p1,p2));
	}
	public static Color getColor(int i)
	{
		if(i==1)
			return new Color(128,0,0);
		if(i==2)
			return new Color(0,96,0);
		if(i==3)
			return new Color(0,0,128);
		if(i==4)
			return new Color(0,128,128);
		if(i==5)
			return new Color(128,0,128);
		if(i==6)
			return new Color(128,128,0);
		else
			return Color.DARK_GRAY;
	}
	public void paint(Graphics g,Camera cam,int width,int height,float rad)
	{
		Vect3D v = cam.getRelative(this);
		float rad1 = cam.getScale(v,width);
		Point p = cam.get2D(v,width,height);
		if(p==null)
			return;
        if(selected==1)
        	g.setColor(new Color(0,128,0));
        else if(selected==2)
        	g.setColor(new Color(255,0,0));
        else if(selected==3)
        	g.setColor(new Color(255,192,0));
        else
        	g.setColor(new Color(96,96,96));
        int radb = (int)(rad*rad1+.5);
        g.drawOval(p.x-radb,p.y-radb,radb*2,radb*2);
//        g.setColor(Color.WHITE);
//        g.drawString(index+"",p.x-radb,p.y-radb);
	}
}
