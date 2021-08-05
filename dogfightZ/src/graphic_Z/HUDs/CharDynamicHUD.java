package graphic_Z.HUDs;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;

import graphic_Z.utils.GraphicUtils;

public class CharDynamicHUD extends CharHUD
{
	protected short size[];
	public short location[];
	public double angle;
	public final short center_X;
	public final short center_Y;
	public boolean transparentAtSpace;
	
	public CharDynamicHUD
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
		
		if(HUDImgFile != null)try(FileReader data = new FileReader(HUDImgFile))
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
		
		size = new short[2];
		size[0] = size_X;
		size[1] = size_Y;
		
		location= new short[2];
		location[0] = Location_X;
		location[1] = Location_Y;
		
		angle	= Angle_X;
		center_X = (short) (size[0]/2);
		center_Y = (short) (size[1]/2);
	}
	
	public CharDynamicHUD
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
	
	public CharDynamicHUD
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y
	) {this(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, (short)0, (short)0, 0.0, true);}
	
	protected double distance(double x0, double y0, short x2, short y2)
	{
		return Math.sqrt((x2-x0)*(x2-x0)+(y2-y0)*(y2-y0));
	}
	
	@Override
	public void printNew()
	{
		if(visible)
		{
			angle %= 360;
			double x0, y0;
			double r, X, Y, tmp;
			
			for(short y=0 ; y<size[1] ; ++y)
			{
				for(short x=0 ; x<size[0] ; ++x)
				{
					if(HUDImg[y][x] != ' ' || !transparentAtSpace)
					{
						x0 = (short) (x-center_X);
						y0 = (short) (y-center_Y);
						
						if(angle != 0)
						{
							r  = distance(x0, y0, (short)0, (short)0);
							
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
