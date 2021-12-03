package graphic_Z.HUDs;

public class CharProgressBar extends CharLabel
{
	public enum Direction{horizon, vertical};
	
	public Direction direction;
	public char visual;
	public float value;
	public int size;
	
	public CharProgressBar
	(
		char[][]	frapsBuffer, 
		int		HUDLayer, 
		int[] 	scrResolution,
		int		location_X, 
		int		location_Y,
		//-----------------------
		int		Size, 
		char		Visual,
		Direction	barDirection,
		float		Value
		//-----------------------
	)
	{
		super(frapsBuffer, HUDLayer, scrResolution, null, location_X, location_Y);
		direction	= barDirection;
		visual		= Visual;
		size		= Size;
		value		= Value;
		visible		= true;
	}
	
	public CharProgressBar
	(
		char[][]	frapsBuffer, 
		int		HUDLayer, 
		int[] 	scrResolution,
		int		location_X, 
		int		location_Y,
		//-----------------------
		int		Size, 
		char		Visual,
		Direction	barDirection
		//-----------------------
	)
	{
		this
		(
			frapsBuffer, HUDLayer,     scrResolution, location_X,  
			location_Y,  Size, Visual, barDirection,  0.0F
		);
	}

	@Override
	public void printNew()
	{
		if(visible)
		{
			if(direction == Direction.horizon) for
			(
				int x=location[0], i=0; 
			    location[1] >= 0 && 
			    location[1] < resolution[1] &&
				x<resolution[0] && i<size*value; 
				++i, ++x
			)	fraps_buffer[location[1]][x] = visual;
			else for
			(
				int y=location[1], i=0; 
			    location[0] >= 0 && 
                location[0] < resolution[0] &&
				y<resolution[0] && i<size*value; 
				++i, --y
			)	fraps_buffer[y][location[0]] = visual;
		}
	}
}
