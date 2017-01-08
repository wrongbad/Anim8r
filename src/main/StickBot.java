package main;

import java.util.TreeMap;

public class StickBot
{
//	0 torso;
//	1 neck;
//	2 lelbow;
//	3 relbow;
//	4 rhand;
//	5 lhand;
//	6 hip;
//	7 rknee;
//	8 lknee;
//	9 rFoot;
//	10 lfoot;
//	11 head;
	int[] pts;
	static float legout=0.3f;
	static float armout=0.4f;
	Vect3D walk=new Vect3D(0,-0.5f,0);
	//planted foot, 0=right 1=left
	int pFoot=0;
	boolean upSwing=false;
	
	public StickBot(int[] points)
	{
		pts=points;
	}
	public void prep(TreeMap<Integer,Vect3D> v)
	{
		for(int i=0;i<7;i++)
			v.get(pts[i]).move(new Vect3D(0,0,-0.25f));
		v.get(pts[11]).move(new Vect3D(0,0,-0.25f));
	}
	public void update(float dt, TreeMap<Integer,Vect3D> v)
	{
//		float speed=walk.getMagnitude()*dt;
		for(int i=0;i<7;i++)
			v.get(pts[i]).move(walk.getScaled(dt));
		v.get(pts[11]).move(walk.getScaled(dt));
		if(pFoot==0)
		{
			if(upSwing)
			{
				v.get(pts[10]).move(new Vect3D(0,0,0.6f*dt));
			}
			else
			{
				v.get(pts[10]).move(new Vect3D(0,0,-0.2f*dt));
			}	
			v.get(pts[10]).move(walk.getScaled(2.0f*dt));
		}
		else
		{
			if(upSwing)
			{
				v.get(pts[9]).move(new Vect3D(0,0,1.0f*dt));
			}
			else
			{
				v.get(pts[9]).move(new Vect3D(0,0,-0.2f*dt));
			}	
			v.get(pts[9]).move(walk.getScaled(2.0f*dt));
		}
		if(Vect3D.distanceSq(v.get(pts[10]),v.get(pts[9]))>4+legout*legout && !upSwing)
		{
			pFoot=1-pFoot;
			upSwing=true;
		}
		float max=-2.4f;
		float min=-3;
		if(v.get(pts[9]).z>max && upSwing)
		{
//			pFoot=1-pFoot;
			v.get(pts[9]).move(new Vect3D(0,0,(max-v.get(pts[9]).z)*4/3));
			upSwing=false;
		}
		if(v.get(pts[10]).z>max && upSwing)
		{
//			pFoot=1-pFoot;
			v.get(pts[10]).move(new Vect3D(0,0,(max-v.get(pts[10]).z)*4/3));
			upSwing=false;
		}
		if(v.get(pts[9]).z<min && !upSwing)
		{
//			pFoot=1-pFoot;
			v.get(pts[9]).move(new Vect3D(0,0,(min-v.get(pts[9]).z)));
//			upSwing=true;
		}
		if(v.get(pts[10]).z<min && !upSwing)
		{
//			pFoot=1-pFoot;
			v.get(pts[10]).move(new Vect3D(0,0,(min-v.get(pts[10]).z)));
//			upSwing=true;
		}
//		System.out.println(Math.abs(v.get(pts[9]).z-v.get(pts[9]).z));
		fixKnees(v);
	}
	public void fixKnees(TreeMap<Integer,Vect3D> v)
	{
		Vect3D wNorm = walk.getScaled(1/walk.getMagnitude());
		Vect3D hip=v.get(pts[6]);
		Vect3D rknee=v.get(pts[7]);
		Vect3D lknee=v.get(pts[8]);
		Vect3D rfoot=v.get(pts[9]);
		Vect3D lfoot=v.get(pts[10]);
		Vect3D z=new Vect3D(0,0,1);
		Vect3D x=Vect3D.crossProduct(z, wNorm);
		Vect3D newHip=hip.getAxis(x, wNorm, z);
		
		Vect3D newfoot=rfoot.getAxis(x, wNorm, z);
		newfoot.move(new Vect3D(-legout,0,0));
		Vect3D mid = Vect3D.midPoint(newfoot, newHip);
		float dist=Vect3D.distance(newfoot, newHip);
		Vect3D knee =  Vect3D.crossProduct(x, Vect3D.subtract(newfoot, newHip));
		if(dist<2)
			knee.setMagnitude((float)Math.sqrt(4-dist*dist)*0.5f);
		else
			knee=new Vect3D(0,0,0);
		knee.move(mid);
		knee.move(new Vect3D(legout,0,0));
		knee=knee.getUnAxis(x, wNorm, z);
		rknee.set(knee);
		
		newfoot=lfoot.getAxis(x, wNorm, z);
		newfoot.move(new Vect3D(legout,0,0));
		mid = Vect3D.midPoint(newfoot, newHip);
		dist=Vect3D.distance(newfoot, newHip);
		knee =  Vect3D.crossProduct(x, Vect3D.subtract(newfoot, newHip));
		if(dist<2)
			knee.setMagnitude((float)Math.sqrt(4-dist*dist)*0.5f);
		else
			knee=new Vect3D(0,0,0);
		knee.move(mid);
		knee.move(new Vect3D(-legout,0,0));
		knee=knee.getUnAxis(x, wNorm, z);
		lknee.set(knee);

	}
}