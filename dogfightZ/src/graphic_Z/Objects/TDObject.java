package graphic_Z.Objects;

import graphic_Z.utils.GraphicUtils;

public abstract class TDObject
{
	public float location[];		//物体中心坐标
	public float roll_angle[];		//物体旋转角度
	public boolean visible;			//物体是否可见
	
	public TDObject(TDObject another)
	{
		location	= another.location.clone();
		roll_angle	= another.roll_angle.clone();
	}
	
	public static void getXYZ_afterRolling
	(
		float X0, float Y0, float Z0,
		float r0, float r1, float r2,
		float result[/*3*/]  //x, y, z
	)
	{
		float X, Y, Z, cos$, sin$;
		/*
		X = GraphicUtils.cos(GraphicUtils.atan(Y0/X0)+GraphicUtils.toRadians(rz))*GraphicUtils.sqrt(X0*X0+Y0*Y0);
		Y = GraphicUtils.sin(GraphicUtils.atan(Y0/X0)+GraphicUtils.toRadians(rz))*GraphicUtils.sqrt(X0*X0+Y0*Y0);
		Y0 = (X0<0)?(-Y):Y;
		X0 = (X0<0)?(-X):X;
		X = GraphicUtils.cos(GraphicUtils.atan(Z0/X0)+GraphicUtils.toRadians(ry))*GraphicUtils.sqrt(X0*X0+Z0*Z0);
		Z = GraphicUtils.sin(GraphicUtils.atan(Z0/X0)+GraphicUtils.toRadians(ry))*GraphicUtils.sqrt(X0*X0+Z0*Z0);
		Z0 = (X0<0)?(-Z):Z;
		X0 = (X0<0)?(-X):X;
		Z = GraphicUtils.cos(GraphicUtils.atan(Y0/Z0)+GraphicUtils.toRadians(rx))*GraphicUtils.sqrt(Z0*Z0+Y0*Y0);
		Y = GraphicUtils.sin(GraphicUtils.atan(Y0/Z0)+GraphicUtils.toRadians(rx))*GraphicUtils.sqrt(Z0*Z0+Y0*Y0);
		Y0 = (Z0<0)?(-Y):Y;
		Z0 = (Z0<0)?(-Z):Z;
		*/
		cos$ = GraphicUtils.cos(r2);
		sin$ = GraphicUtils.sin(r2);
		X = cos$ * X0 - sin$ * Y0;
		Y = sin$ * X0 + cos$ * Y0;
		X0 = X;
		Y0 = Y;

		cos$ = GraphicUtils.cos(r1);
		sin$ = GraphicUtils.sin(r1);
		X = cos$ * X0 - sin$ * Z0;
		Z = sin$ * X0 + cos$ * Z0;
		Z0 = Z;

		cos$ = GraphicUtils.cos(r0);
		sin$ = GraphicUtils.sin(r0);
		Z = cos$ * Z0 - sin$ * Y0;
		Y = sin$ * Z0 + cos$ * Y0;
		
		result[0] = X;
		result[1] = Y;
		result[2] = Z;
	}
	
	public static void getXYZ_beforeRolling
	(
		float X0, float Y0, float Z0,
        float r0, float r1, float r2,
		float result[]  //x, y, z
	)
	{
		/*
		float X, Y, Z;
		
		Z = GraphicUtils.cos(GraphicUtils.atan(Y0/Z0)+GraphicUtils.toRadians(-rx))*GraphicUtils.sqrt(Z0*Z0+Y0*Y0);
		Y = GraphicUtils.sin(GraphicUtils.atan(Y0/Z0)+GraphicUtils.toRadians(-rx))*GraphicUtils.sqrt(Z0*Z0+Y0*Y0);
		Y0 = (Z0<0)?(-Y):Y;
		Z0 = (Z0<0)?(-Z):Z;
		X = GraphicUtils.cos(GraphicUtils.atan(Z0/X0)+GraphicUtils.toRadians(-ry))*GraphicUtils.sqrt(X0*X0+Z0*Z0);
		Z = GraphicUtils.sin(GraphicUtils.atan(Z0/X0)+GraphicUtils.toRadians(-ry))*GraphicUtils.sqrt(X0*X0+Z0*Z0);
		Z0 = (X0<0)?(-Z):Z;
		X0 = (X0<0)?(-X):X;
		X = GraphicUtils.cos(GraphicUtils.atan(Y0/X0)+GraphicUtils.toRadians(-rz))*GraphicUtils.sqrt(X0*X0+Y0*Y0);
		Y = GraphicUtils.sin(GraphicUtils.atan(Y0/X0)+GraphicUtils.toRadians(-rz))*GraphicUtils.sqrt(X0*X0+Y0*Y0);
		Y0 = (X0<0)?(-Y):Y;
		X0 = (X0<0)?(-X):X;
		*/
		float X, Y, Z, cos$, sin$;
		
		cos$ = GraphicUtils.cos(r0);
		sin$ = GraphicUtils.sin(r0);
		Z = cos$ * Z0 - sin$ * Y0;
		Y = sin$ * Z0 + cos$ * Y0;
		Z0 = Z;
		Y0 = Y;
	
		cos$ = GraphicUtils.cos(r1);
		sin$ = GraphicUtils.sin(r1);
		X = cos$ * X0 - sin$ * Z0;
		Z = sin$ * X0 + cos$ * Z0;
		X0 = X;
		
		cos$ = GraphicUtils.cos(r2);
		sin$ = GraphicUtils.sin(r2);
		X = cos$ * X0 - sin$ * Y0;
		Y = sin$ * X0 + cos$ * Y0;
			
		result[0] = X;
		result[1] = Y;
		result[2] = Z;
	}
	
	public TDObject()
	{
		location	= new float[3];
		roll_angle	= new float[3];
		
		location[0]   = location[1]   = location[2]   = 0.0F;
		roll_angle[0] = roll_angle[1] = roll_angle[2] = 0.0F;
	}
	
	public abstract void go();
}
