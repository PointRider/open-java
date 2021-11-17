package graphic_Z.HUDs;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;

import graphic_Z.utils.GraphicUtils;

public class CharDynamicHUD2 extends CharHUD
{
	protected       int     size[];
	public          int     location[];
	public          double  angle;
	public    final int     center_X;
	public    final int     center_Y;
	public          boolean transparentAtSpace;
	
	public CharDynamicHUD2
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y,
		short Location_X,
		short Location_Y,
		double Angle_X,
		boolean transparent_at_space
	)
	{
		super(null, frapsBuffer, HUDLayer, scrResolution, true);
		transparentAtSpace = transparent_at_space;
		HUDImg = new char[size_Y][size_X];
		
		if(HUDImgFile != null) try(FileReader data = new FileReader(HUDImgFile))
		{
			for(int i=0 ; i<size_Y ; ++i)
			{
				for(int j=0 ; j<size_X ; ++j)
				{
					if(j != 0)
						data.read();
					HUDImg[i][j] = (char) data.read();
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
		else for(int i=0 ; i<size_Y ; ++i)
		{
			for(int j=0 ; j<size_X ; ++j)
				HUDImg[i][j] = ' ';
		}
		
		size = new int[2];
		size[0] = size_X;
		size[1] = size_Y;
		
		location= new int[2];
		location[0] = Location_X;
		location[1] = Location_Y;
		
		angle	= Angle_X;
		center_X = size[0] >> 1;
		center_Y = size[1] >> 1;
	}
	
	public CharDynamicHUD2
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y,
		short Location_X,
		short Location_Y
	) {this(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, Location_X, Location_Y, 0.0, true);}
	
	public CharDynamicHUD2
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y
	) {this(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, (short)0, (short)0, 0.0, true);}
	
	protected double distance(double x0, double y0, int x2, int y2)
	{
		x2 -= x0;
		y2 -= y0;
		return Math.sqrt(x2*x2 + y2*y2);
	}
	
	@Override
	public void printNew()
	{
		if(visible)
		{
			angle %= 360;
			double x0, y0;
			double r, X, Y, tmp;
			
			for(int y=0 ; y<size[1] ; ++y)
			{
				for(int x=0 ; x<size[0] ; ++x)
				{
					if(HUDImg[y][x] != ' ' || !transparentAtSpace)
					{
						x0 = x-center_X;
						y0 = y-center_Y;
						
						if(angle != 0)
						{
							r  = distance(x0, y0, 0, 0);
							
							tmp = Math.atan(y0/x0)+Math.toRadians(angle);
							X = GraphicUtils.cos(tmp) * r;
							Y = GraphicUtils.sin(tmp) * r;
							y0 = (short) ((x0<0)?(-Y):Y);
							x0 = (short) ((x0<0)?(-X):X);
						}
						x0 += location[0];
						y0 += location[1];
						
						if(x0 >= 0 && y0 >= 0 && x0 < resolution[0]	&&	y0 < resolution[1])
							fraps_buffer[(int) y0][(int) x0] = HUDImg[y][x];
					}
				}
			}
		}
	}
}
