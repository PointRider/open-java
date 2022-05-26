package graphic_Z.Common;

public class SinglePoint
{
	public int x;
	public int y;
	
	public SinglePoint()
	{
		x = 0;
		y = 0;
	}
	
	public SinglePoint(SinglePoint another)
	{
		x = another.x;
		y = another.y;
	}
	
	public SinglePoint(int X, int Y)
	{
		x = X;
		y = Y;
	}
	
	public SinglePoint(int XY[])
	{
		x = XY[0];
		y = XY[1];
	}
	
	public void set(int XY[])
	{
		x = XY[0];
		y = XY[1];
	}
}
