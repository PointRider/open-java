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
	private Aircraft myself;
	private LinkedListZ<ThreeDs> aircrafts;
	private float maxSearchRange;
	private float tmp_float_xy[];
	private char  Img[][];
	private char  back[][];
	private float nowAngle;
	private CharDynamicHUD painter;
	
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
		float maxSearch_range
	)
	{
		super(null, frapsBuffer, HUDLayer, scrResolution, size, size, Location_X, Location_Y, 0.0F, true);
		myself			= myJet;
		aircrafts		= aircraftsList;
		tmp_float_xy	= new float[2];
		maxSearchRange	= maxSearch_range;
		Img				= new char[size][size];
		back			= new char[size][size];
		nowAngle		= GraphicUtils.RAD270;
		r               = size >> 1;
		
		painter = new CharDynamicHUD(HUDPainterImg, frapsBuffer, HUDLayer, scrResolution, size, size, Location_X, Location_Y , 0.0F, true);
		//painter.location[0] += 1;
		//painter.location[1] += 1;
		if(HUDImgFile != null) try(FileReader data = new FileReader(HUDImgFile)) {
			for(int i=0 ; i<size ; ++i) {
				for(int j=0 ; j<size ; ++j) {
					if(j != 0) data.read();
					Img[i][j] = (char) data.read();
				}
				data.read();data.read();
			}
		} catch(EOFException exc) {} catch(IOException exc) {
			System.out.println("HUD load fault.");
		}
		
		for(int i=0 ; i<size ; ++i) {
			for(int j=0 ; j<size ; ++j) HUDImg[i][j] = back[i][j] = Img[i][j];
		}
	}
	
	public float rangeXY(float x1, float y1, float x2, float y2)
	{
		return GraphicUtils.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	private int x, y, r;
	private float old;
	
	@Override
	public void printNew()
	{
		if(!visible) return;
		old = nowAngle += 0.1047197551196598F;
		
		painter.angle = nowAngle;
		painter.printNew();
		
		for(int i=0 ; i<r ; ++i)
		{
			x = (int)(GraphicUtils.cos(nowAngle) * i);
			y = (int)(GraphicUtils.sin(nowAngle) * i);
			
			x += centerX;
			y += centerY;

			HUDImg[y][x] = back[y][x];
		}
		
		nowAngle %= GraphicUtils.RAD360;

        if(old > nowAngle)
            makeNewReady();
        
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
		float range;
		int x, y;
		
		for(int i=0 ; i<size[1] ; ++i)
		{
			for(int j=0 ; j<size[0] ; ++j)
				back[i][j] = Img[i][j];
		}
		
		for(ThreeDs a : aircrafts)
		{
			aTarget = (Aircraft) a;
			if(aTarget.getID().equals(myself.getID()) || aTarget.getCamp() == -1 || !aTarget.isAlive()) continue;
			myself.getRelativePosition_XY(aTarget.location[1], aTarget.location[2], tmp_float_xy);
			
			range = rangeXY(aTarget.location[1], aTarget.location[2], myself.location[1], myself.location[2]);
			
			if(GraphicUtils.abs(range) <= maxSearchRange)
			{
				x = (int) (tmp_float_xy[0] * r / maxSearchRange);
				y = (int) (tmp_float_xy[1] * r / maxSearchRange);
				
				x += centerX;
				y += centerY + 1;
				
				if(GraphicUtils.abs(myself.roll_angle[1]) < GraphicUtils.RAD90)
					 y = size[1] - y;
				else x = size[0] - x;
				
				x = x < size[0]? x : size[0]-1;
				y = y < size[1]? y : size[1]-1;
				
				if(x < 0) x = 0;
				if(y < 0) y = 0;
				
				back[y][x] = (aTarget.getCamp() == myself.getCamp()? 'o' : 'X');
			}
		}
		
		back[centerX][centerY] = '+';
	}
}
