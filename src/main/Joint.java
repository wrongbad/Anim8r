package main;

import java.util.ArrayList;

public class Joint extends Vect3D
{
//	public Vect3D loc;
	public int drawRad;
	public int actRad = 30;
	//cosine of preffered Anlge
	public float prefAngleC = 0f;
	public Vect3D velocity;
	ArrayList<Joint> neigh = new ArrayList<Joint>();
//	int ball=2;

	
	public Joint(Vect3D location)
	{
		set(location.x,location.y,location.z);
		velocity = new Vect3D(0,0,0);
	}
	public void set(Vect3D v)
	{
		set(v.x,v.y,v.z);
	}
	public void forceAngle()
	{
		if(neigh.size()>2) forceAngle3();
		if(neigh.size()!=2) return;
		Joint j1 = neigh.get(0);
		Joint j2 = neigh.get(1);
		Vect3D diff1 = Vect3D.subtract(j1, this);
		Vect3D diff2 = Vect3D.subtract(j2, this);
		float mag1 = diff1.getMagnitude();
		float mag2 = diff2.getMagnitude();
		diff1=diff1.getScaled(1/mag1);
		diff2=diff2.getScaled(1/mag2);
		Vect3D a = diff1.clone();
		diff1.move(diff2.getScaled(-.2f));
		diff2.move(a.getScaled(-.2f));
		j1.set(Vect3D.add(diff1.getScaled(mag1),this));
		j2.set(Vect3D.add(diff2.getScaled(mag2),this));
	}
	public void forceAngle3()
	{
//		if(neigh.size()!=3) return;
		Joint j1 = neigh.get(0);
		Joint j2 = neigh.get(1);
		Joint j3 = neigh.get(2);
		Vect3D diff1 = Vect3D.subtract(j1, this);
		Vect3D diff2 = Vect3D.subtract(j2, this);
		Vect3D diff3 = Vect3D.subtract(j3, this);
		float mag1 = diff1.getMagnitude();
		float mag2 = diff2.getMagnitude();
		float mag3 = diff2.getMagnitude();
		diff1=diff1.getScaled(1/mag1);
		diff2=diff2.getScaled(1/mag2);
		diff3=diff3.getScaled(1/mag3);
		Vect3D a = diff1.clone();
		Vect3D b = diff2.clone();
		float scale = .5f;
		diff1.move(diff2.getScaled(-scale));
		diff1.move(diff3.getScaled(-scale));
		diff2.move(a.getScaled(-scale));
		diff2.move(diff3.getScaled(-scale));
		diff3.move(a.getScaled(-scale));
		diff3.move(b.getScaled(-scale));
		j1.set(Vect3D.add(diff1.getScaled(mag1),this));
		j2.set(Vect3D.add(diff2.getScaled(mag2),this));
		j3.set(Vect3D.add(diff3.getScaled(mag3),this));
	}
//	public void forceAngle2()
//	{
//		if(neigh.size()!=2) return;
//		Joint j1 = neigh.get(0);
//		Joint j2 = neigh.get(1);
//		Vect3D diff1 = Vect3D.subtract(j1, this);
//		Vect3D diff2 = Vect3D.subtract(j2, this);
//		diff1=diff1.getScaled(1/diff1.getMagnitude());
//		diff2=diff2.getScaled(1/diff2.getMagnitude());
//		float angc = Vect3D.dotProduct(diff1, diff2);
//		Vect3D cross = Vect3D.crossProduct(diff1, diff2);
//		cross=cross.getScaled(1/cross.getMagnitude());
//		diff1=Vect3D.crossProduct(cross, diff1);
//		diff2=Vect3D.crossProduct(diff2, cross);
//		diff1=diff1.getScaled(1/diff1.getMagnitude());
//		diff2=diff2.getScaled(1/diff2.getMagnitude());
//		j1.velocity.move(diff1.getScaled(.005f*(angc-prefAngleC)));
//		j2.velocity.move(diff2.getScaled(.005f*(angc-prefAngleC)));
//
//	}
}
