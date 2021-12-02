package graphic_Z.HUDs;

import graphic_Z.utils.GraphicUtils;

public class CharDynamicHUD extends CharImage
{
	public float  angle;
	public boolean transparentAtSpace;
	
	public CharDynamicHUD
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		int HUDLayer, 
		int[] scrResolution,
		int size_X,
		int size_Y,
		int Location_X,
		int Location_Y,
		float Angle_X,
		boolean transparent_at_space
	)
	{
		super(HUDImgFile, frapsBuffer, size_X, size_Y, Location_X, Location_Y, HUDLayer, scrResolution, true);
		transparentAtSpace = transparent_at_space;
		angle	= Angle_X;
	}
	
	public CharDynamicHUD
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		int HUDLayer, 
		int[] scrResolution,
		int size_X,
		int size_Y,
		int Location_X,
		int Location_Y
	) {this(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, Location_X, Location_Y, 0.0F, true);}
	
	public CharDynamicHUD
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		int HUDLayer, 
		int[] scrResolution,
		int size_X,
		int size_Y
	) {this(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, 0, 0, 0.0F, true);}
	
	protected float distance(float x0, float y0, int x2, int y2)
	{
		x2 -= x0;
		y2 -= y0;
		return GraphicUtils.sqrt(x2*x2 + y2*y2);
	}
	
	@Override
	public void printNew()
	{
		if(visible)
		{
			angle %= 360;
			float x0, y0;
			float r, X, Y, /*tmp,*/ cos$, sin$;
			
			for(int y=0 ; y<size[1] ; ++y)
			{
				for(int x=0 ; x<size[0] ; ++x)
				{
					if(HUDImg[y][x] != ' ' || !transparentAtSpace)
					{
						x0 = x-centerX;
						y0 = y-centerY;
						
						if(angle != 0)
						{
							/*
							r  = distance(x0, y0, 0, 0);
							
							tmp = GraphicUtils.atan(y0/x0)+GraphicUtils.toRadians(angle);
							X = GraphicUtils.cos(tmp) * r;
							Y = GraphicUtils.sin(tmp) * r;
							y0 = ((x0<0)?(-Y):Y);
							x0 = ((x0<0)?(-X):X);
							*/
							
							r = GraphicUtils.toRadians(angle);
							cos$ = GraphicUtils.cos(r);
							sin$ = GraphicUtils.sin(r);
							X = cos$ * x0 - sin$ * y0;
							Y = sin$ * x0 + cos$ * y0;
							x0 = X;
							y0 = Y;
						}
						x0 += location[0] + 0.5;
						y0 += location[1] + 0.5;
						
						if(x0 >= 0 && y0 >= 0 && x0 < resolution[0]	&&	y0 < resolution[1])
							fraps_buffer[(int) y0][(int) x0] = HUDImg[y][x];
					}
				}
			}
		}
	}
}
