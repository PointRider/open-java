package graphic_Z.HUDs;

public class CharLoopingScrollBar extends CharDynamicHUD
{
	public enum Direction{horizon, vertical};
	
	public Direction direction;
	public int size_show;
	public int value;
	
	public CharLoopingScrollBar
	(
		String    HUDImgFile, 
		char[][]  frapsBuffer, 
		int       HUDLayer, 
		int[]     scrResolution,
		int       size_X,
		int       size_Y,
		int       Location_X,
		int       Location_Y,
		Direction barDirection,
		//----------------------
		int       Value,
		int       size_Show
		//----------------------
	)
	{
		//super("", frapsBuffer, HUDLayer, size_X, size_Y);
		super(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, Location_X, Location_Y, 0.0, true);
		
		direction = barDirection;
		
		value		= Value;
		
		if(size_X < size_Show)
			size_show = size_X;
		else size_show = size_Show;
	}
	
	public CharLoopingScrollBar
	(
		String		HUDImgFile, 
		char[][]	frapsBuffer, 
		int		HUDLayer, 
		int[]		scrResolution,
		int		size_X,
		int		size_Y,
		int		Location_X,
		int		Location_Y,
		//----------------------
		int		Value,
		int		size_Show
		//----------------------
	)
	{
		this
		(
			HUDImgFile, frapsBuffer, HUDLayer, 
			scrResolution, size_X, size_Y, Location_X, 
			Location_Y, Direction.horizon, Value, size_Show
		);
	}
	
	@Override
	public void printNew()
	{
		int x1, y1;
		if(visible)
		{
			if(value < 0)
				value = (short) (size[0] - (-value)%size[0]);
			for(short y=0 ; y<size[1] ; ++y)
			{
				for(short xi=0, x=0 ; xi < size[0]; ++xi)
				{
					x = (short) ((xi + value) % size[0]);
					
					if(x < size_show && HUDImg[y][xi] != ' ')
					{
						if(direction == Direction.horizon)
						{
							x1 = x+location[0] - size_show / 2;
							y1 = y+location[1] - size[1] / 2;
							if(y1 >= 0  &&  x1 >= 0  &&  y1 < resolution[1]  &&  x1 < resolution[0])
								fraps_buffer[y1][x1] = HUDImg[y][xi];
						}
						else
						{
							x1 = y+location[0] - size[1] / 2;
							y1 = x+location[1] - size_show / 2;
							if(y1 >= 0  &&  x1 >= 0  &&  y1 < resolution[1]  &&  x1 < resolution[0])
								fraps_buffer[y1][x1] = HUDImg[y][xi];
						}
					}
				}
			}
		}
	}
}
