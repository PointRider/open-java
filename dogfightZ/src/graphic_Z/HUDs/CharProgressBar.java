package graphic_Z.HUDs;

public class CharProgressBar extends CharLabel
{
	public enum Direction{horizon, vertical};
	
	public Direction direction;
	public char visual;
	public double value;
	public int size;
	
	public CharProgressBar
	(
		char[][]	frapsBuffer, 
		short		HUDLayer, 
		short[] 	scrResolution,
		short		location_X, 
		short		location_Y,
		//-----------------------
		short		Size, 
		char		Visual,
		Direction	barDirection,
		double		Value
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
		short		HUDLayer, 
		short[] 	scrResolution,
		short		location_X, 
		short		location_Y,
		//-----------------------
		short		Size, 
		char		Visual,
		Direction	barDirection
		//-----------------------
	)
	{
		this
		(
			frapsBuffer, HUDLayer,     scrResolution, location_X,  
			location_Y,  Size, Visual, barDirection,  0.0
		);
	}

	@Override
	public void printNew()
	{
		if(visible)
		{
			if(direction == Direction.horizon) for
			(
				short x=location[0], i=0; 
				x<resolution[0] && i<size*value; 
				++i, ++x
			)	fraps_buffer[location[1]][x] = visual;
			else for
			(
				short y=location[1], i=0; 
				y<resolution[0] && i<size*value; 
				++i, --y
			)	fraps_buffer[y][location[0]] = visual;
		}
	}
}
