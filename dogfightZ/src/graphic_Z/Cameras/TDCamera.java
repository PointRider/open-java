package graphic_Z.Cameras;

import graphic_Z.utils.GraphicUtils;

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

    public static float getXY_onCamera
    (
        float X0, float Y0, float Z0,
        int    resolution_X,
        int    resolution_Y,
        float cameraLocation[/*3*/], //x, y, z
        float cameraRollAngl[/*3*/], //x, y, z
        float result[/*2*/], //x, y
        float FOV
    )
    {
        float X, Y, Z, X1, Y1;
        int    Xcenter = resolution_X >> 1, 
               Ycenter = resolution_Y >> 1;
        
        float cos$, sin$, temp=GraphicUtils.tan(FOV/2.0F);
        float cr0 = cameraRollAngl[0];
        float cr1 = cameraRollAngl[1];
        float cr2 = cameraRollAngl[2];
        
        X0 -= cameraLocation[0];
        Y0 -= cameraLocation[1];
        Z0 -= cameraLocation[2];
        
        cos$ = GraphicUtils.cos(cr0);
        sin$ = GraphicUtils.sin(cr0);
        Z = cos$ * Z0 - sin$ * Y0;
        Y = sin$ * Z0 + cos$ * Y0;
        Z0 = Z;
        Y0 = Y;

        cos$ = GraphicUtils.cos(cr1);
        sin$ = GraphicUtils.sin(cr1);
        X = cos$ * X0 - sin$ * Z0;
        Z = sin$ * X0 + cos$ * Z0;
        X0 = X;
        Z0 = Z;
        //---旋转结束---
        
        if(Z0>=0 && result!=null)
        {
            //借用cos$作为临时变量计算小孔成像
            cos$ = Xcenter*FOV/(Xcenter+temp*Z0);
            X0 = X0 * cos$;
            Y0 = Y0 * cos$;
            
            //屏幕视角绕Z轴转动
            cos$ = GraphicUtils.cos(cr2);
            sin$ = GraphicUtils.sin(cr2);
            X = cos$ * X0 - sin$ * Y0;
            Y = sin$ * X0 + cos$ * Y0;
            X0 = X;
            Y0 = Y;
            
            X1 = Y+Xcenter;
            Y1 = X+Ycenter;
            
            if(X1>=0 && Y1>=0 && X1<resolution_X && Y1<resolution_Y)
            {
                result[0] = X1;
                result[1] = Y1;
            }
            else result[0] = result[1] = -1;
        }
        
        return Z0;
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
	
	public final float getVisibility() {
        return visibility;
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