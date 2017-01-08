package main;

import javax.swing.*;
import java.awt.*;

//the scrolling "film strip" class
public class FrameBrowser extends JPanel
{
	//leftmost pixel
	float offset = 0;
	int maxOffset = 0;
	int totalFrames = 0;
	int frameWidth=80;
	int barWidth=10;
	int width;
	int barPos;
	int selectedFrame=0;
	boolean barSelected=false;
	Rectangle bar = new Rectangle(0,0,0,0);
	
//	public FrameBrowser()
//	{
//		
//	}
	public void scroll(int movement)
	{
		maxOffset=Math.max(0,(totalFrames*frameWidth)-getWidth()-1);
		offset+=((float)((totalFrames*frameWidth)-getWidth())/(float)(getWidth()-width))*movement;
//		offset+=movement*10;
//		System.out.println(offset);
		if(offset<0) offset=0;
		if(offset>maxOffset) offset=maxOffset;
	}
	public boolean isOnBar(Point p)
	{
		return bar.contains(p);
	}
	public int whichFrame(Point p)
	{
		int f = -1;
		if(p.getY()<getHeight()-barWidth)
		{
			f=(p.x+(int)offset)/frameWidth;
		}
		return f;
	}
	public void selectFrame(int frame)
	{
//		if(selectedFrame==frame)
//			selectedFrame=0;
//		else
			selectedFrame=frame;
	}
	public void setTotalFrames(int frames)
	{
		totalFrames=frames;
	}
	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(0,64,64));
		g.fillRect(0, 0, getWidth(), getHeight());
		frameWidth=(int)((getHeight()-barWidth)/Canvas.scale)+1;
		int startFrame = (int)(offset/frameWidth);
		int numFrames = Math.min((getWidth()/frameWidth)+2,totalFrames-startFrame);
		int realnumFrames = Math.min((getWidth()/frameWidth)+2,totalFrames);
		g.setFont(new Font("OCR A Extended",Font.PLAIN,14));
		for(int i = 0;i<numFrames;i++)
		{
			int frame=startFrame+i;
			int start = (int)(((startFrame+i)*frameWidth)-offset);
			if(selectedFrame==frame)
				g.setColor(Color.GREEN);
			else
				g.setColor(Color.DARK_GRAY);
			g.fillRect(start,0,frameWidth-1,getHeight()-barWidth);
			g.setColor(Color.BLACK);
			g.fillRect(start+10,10,frameWidth-21,getHeight()-barWidth-20);
			g.setColor(Color.DARK_GRAY);
			g.drawString((frame+1)+"", start+frameWidth/2-20, (getHeight()-barWidth)/2+8);
		}
		
		width = (int)Math.max(50,((realnumFrames/(float)totalFrames)*(getWidth()-1)));
		barPos = (int)(offset/(float)((totalFrames*frameWidth)-getWidth())*(getWidth()-width));
		g.setColor(new Color(32,32,32));
		g.fillRect(0,getHeight()-barWidth,getWidth(),barWidth);
		if(barSelected)
			g.setColor(Color.DARK_GRAY);
		else
			g.setColor(Color.BLACK);
		g.fillRect( (int)(barPos) , getHeight()-barWidth, width, barWidth);
		g.setColor(Color.GRAY);
		bar = new Rectangle( (int)(barPos) , getHeight()-barWidth, width, barWidth);
		g.drawRect( (int)(barPos) , getHeight()-barWidth, width, barWidth-1);

	}
}
