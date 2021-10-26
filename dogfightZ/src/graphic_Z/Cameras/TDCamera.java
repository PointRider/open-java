package graphic_Z.Cameras;

public abstract class TDCamera<WorldType>
{
	public		double	FOV;			//视场角大小(弧度制)
	public		double	visibility;	//能见度
	public		double	location[];	//摄像机位置坐标(x,y,z)
	public		double	roll_angle[];	//摄像机旋转角度(x,y,z)
	public		double	roll_source[];
	public 		WorldType inWorld;	//摄像机所在世界

	protected	boolean	reversed;
	protected	double	reversedAngle[];
	
	public TDCamera(double cameraFOV, double visblt, WorldType inWhichWorld)
	{
		FOV			  = cameraFOV;
		visibility	  = visblt;
		reversed	  = false;

		reversedAngle = new double[3];
		
		disconnect();
		
		inWorld = inWhichWorld;
	}
	
	public void connectLocationAndAngle(double locationVector[], double angleVector[])
	{
		location	= locationVector;
		roll_source	= angleVector;
		if(reversed)
			roll_angle = reversedAngle;
		else roll_angle = roll_source;
	}
	
	public void disconnect()
	{
		location	  = new double[3];
		roll_source	  = new double[3];
		
		if(reversed)
			roll_angle = reversedAngle;
		else roll_angle = roll_source;
		
		location[0]	  = 0.0;
		location[1]	  = 0.0;
		location[2]	  = 0.0;
		
		roll_angle[0] = 0.0;
		roll_angle[1] = 0.0;
		roll_angle[2] = 0.0;
	}
	
	public abstract Object exposure();
	public void setFOV(double fov)
	{
		FOV = fov;
	}
	
	public void setVisibility(double visblt)
	{
		visibility = visblt;
	}
	
	public void reverse()
	{
		reversed = !reversed;
		if(reversed)
			roll_angle = reversedAngle;
		else roll_angle = roll_source;
	}
	
	public void setReversed(boolean rev) {
		if(!(rev ^ reversed)) return;
		reverse();
	}
}