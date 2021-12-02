package graphic_Z.Cameras;

public abstract class TDCamera<WorldType>
{
	public		float	FOV;			//视场角大小(弧度制)
	public		float	visibility;	//能见度
	public		float	location[];	//摄像机位置坐标(x,y,z)
	public		float	roll_angle[];	//摄像机旋转角度(x,y,z)
	public		float	roll_source[];
	public 		WorldType inWorld;	//摄像机所在世界

	protected	boolean	reversed;
	protected	float	reversedAngle[];
	
	public TDCamera(float cameraFOV, float visblt, WorldType inWhichWorld)
	{
		FOV			  = cameraFOV;
		visibility	  = visblt;
		reversed	  = false;

		reversedAngle = new float[3];
		
		disconnect();
		
		inWorld = inWhichWorld;
	}
	
	public void connectLocationAndAngle(float locationVector[], float angleVector[])
	{
		location	= locationVector;
		roll_source	= angleVector;
		if(reversed)
			roll_angle = reversedAngle;
		else roll_angle = roll_source;
	}
	
	public void disconnect()
	{
		location	  = new float[3];
		roll_source	  = new float[3];
		
		if(reversed)
			roll_angle = reversedAngle;
		else roll_angle = roll_source;
		
		location[0]	  = 0.0F;
		location[1]	  = 0.0F;
		location[2]	  = 0.0F;
		
		roll_angle[0] = 0.0F;
		roll_angle[1] = 0.0F;
		roll_angle[2] = 0.0F;
	}
	
	public abstract Object exposure();
	public void setFOV(float fov)
	{
		FOV = fov;
	}
	
	public void setVisibility(float visblt)
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