package dogfight_Z;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;

import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.LinkedListZ;

public class Radar extends CharDynamicHUD
{
	public Aircraft myself;
	public LinkedListZ<ThreeDs> aircrafts;
	public double maxSearchRange;
	public double tmp_double_xy[];
	public char Img[][];
	public char back[][];
	public int nowAngle;
	public CharDynamicHUD painter;
	
	@Override
	public void reSizeScreen(int resolution[], char fraps_buffer[][]) {
		super.reSizeScreen(resolution, fraps_buffer);
		painter.reSizeScreen(resolution, fraps_buffer);
	}
	
	public Radar
	(
		String HUDImgFile, 
		String HUDPainterImg,
		char[][] frapsBuffer, 
		int HUDLayer, 
		int[] scrResolution,
		int size,
		int Location_X,
		int Location_Y,
		Aircraft myJet,
		LinkedListZ<ThreeDs> aircraftsList,
		double maxSearch_range
	)
	{
		super(null, frapsBuffer, HUDLayer, scrResolution, size, size, Location_X, Location_Y, 0.0, true);
		myself			= myJet;
		aircrafts		= aircraftsList;
		tmp_double_xy	= new double[2];
		maxSearchRange	= maxSearch_range;
		Img				= new char[size][size];
		back			= new char[size][size];
		nowAngle		= 270;
		
		painter = new CharDynamicHUD(HUDPainterImg, frapsBuffer, HUDLayer, scrResolution, size, size, Location_X, Location_Y, 0.0, true);
		//\painter.location[0] += 1;
		//painter.location[1] += 1;
		if(HUDImgFile != null)try(FileReader data = new FileReader(HUDImgFile))
		{
			for(int i=0 ; i<size ; ++i)
			{
				for(int j=0 ; j<size ; ++j)
				{
					if(j != 0)
						data.read();
					Img[i][j] = (char) data.read();
				}
				data.read();data.read();
			}
		}
		catch(EOFException exc)
		{
		}
		catch(IOException exc)
		{
			System.out.println("HUD load fault.");
		}
		
		for(int i=0 ; i<size ; ++i)
		{
			for(int j=0 ; j<size ; ++j)
				HUDImg[i][j] = back[i][j] = Img[i][j];
		}
	}
	
	public double rangeXY(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	@Override
	public void printNew()
	{
		if(!visible) return;
		if(nowAngle == 270)
			makeNewReady();
		nowAngle+=6;
		
		painter.angle = nowAngle;
		painter.printNew();
		
		int x, y, r = (size[0]>>1);
		double theta = Math.toRadians(nowAngle);
		for(int i=0 ; i<r ; ++i)
		{
			x = (int)(GraphicUtils.cos(theta) * i);
			y = (int)(GraphicUtils.sin(theta) * i);
			
			x += centerX;
			y += centerY;

			HUDImg[y][x] = back[y][x];
		}
		nowAngle %= 360;
		super.printNew();
	}
	
	@Override
    public void setLocation(int X, int Y)
    {
        super.setLocation(X, Y);
        painter.setLocation(X, Y);
    }
	
	public void makeNewReady()
	{
		Aircraft aTarget = null;
		double range, r = (size[0]>>1);
		int x, y;
		
		for(int i=0 ; i<size[1] ; ++i)
		{
			for(int j=0 ; j<size[0] ; ++j)
				back[i][j] = Img[i][j];
		}
		
		for(ThreeDs a : aircrafts)
		{
			aTarget = (Aircraft) a;
			if(aTarget.getID().equals(myself.getID()) || aTarget.camp == -1 || !aTarget.isAlive)
				continue;
			myself.getRelativePosition_XY(aTarget.location[1], aTarget.location[2], tmp_double_xy);
			
			range = rangeXY(aTarget.location[1], aTarget.location[2], myself.location[1], myself.location[2]);
			
			if(Math.abs(range) <= maxSearchRange)
			{
				x = (int) (tmp_double_xy[0] * r / maxSearchRange);
				y = (int) (tmp_double_xy[1] * r / maxSearchRange);
				
				x += centerX;
				y += centerY + 1;
				
				if(Math.abs(myself.roll_angle[1]) < 90)
					 y = size[1] - y;
				else x = size[0] - x;
				
				x = x < size[0]? x : size[0]-1;
				y = y < size[1]? y : size[1]-1;
				
				if(x < 0) x = 0;
				if(y < 0) y = 0;
				
				back[y][x] = (aTarget.camp == myself.camp? 'o' : '@');
			}
		}
		
		back[centerX][centerY] = '+';
	}
}
