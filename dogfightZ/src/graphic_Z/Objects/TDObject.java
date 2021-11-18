package graphic_Z.Objects;

import graphic_Z.utils.GraphicUtils;

public abstract class TDObject
{
	public double location[];		//物体中心坐标
	public double roll_angle[];		//物体旋转角度
	public boolean visible;			//物体是否可见
	
	public TDObject(TDObject another)
	{
		location	= another.location.clone();
		roll_angle	= another.roll_angle.clone();
	}
	
	public static void getXYZ_afterRolling
	(
		double X0, double Y0, double Z0,
		double rx, double ry, double rz,
		
		double result[/*3*/]  //x, y, z
	)
	{
		double X, Y, Z, cos$, sin$, 
			r2 = Math.toRadians(rz), r1 = Math.toRadians(ry), r0 = Math.toRadians(rx);
		/*
		X = GraphicUtils.cos(Math.atan(Y0/X0)+Math.toRadians(rz))*Math.sqrt(X0*X0+Y0*Y0);
		Y = GraphicUtils.sin(Math.atan(Y0/X0)+Math.toRadians(rz))*Math.sqrt(X0*X0+Y0*Y0);
		Y0 = (X0<0)?(-Y):Y;
		X0 = (X0<0)?(-X):X;
		X = GraphicUtils.cos(Math.atan(Z0/X0)+Math.toRadians(ry))*Math.sqrt(X0*X0+Z0*Z0);
		Z = GraphicUtils.sin(Math.atan(Z0/X0)+Math.toRadians(ry))*Math.sqrt(X0*X0+Z0*Z0);
		Z0 = (X0<0)?(-Z):Z;
		X0 = (X0<0)?(-X):X;
		Z = GraphicUtils.cos(Math.atan(Y0/Z0)+Math.toRadians(rx))*Math.sqrt(Z0*Z0+Y0*Y0);
		Y = GraphicUtils.sin(Math.atan(Y0/Z0)+Math.toRadians(rx))*Math.sqrt(Z0*Z0+Y0*Y0);
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
		double X0, double Y0, double Z0,
		double rx, double ry, double rz,
		
		double result[]  //x, y, z
	)
	{
		/*
		double X, Y, Z;
		
		Z = GraphicUtils.cos(Math.atan(Y0/Z0)+Math.toRadians(-rx))*Math.sqrt(Z0*Z0+Y0*Y0);
		Y = GraphicUtils.sin(Math.atan(Y0/Z0)+Math.toRadians(-rx))*Math.sqrt(Z0*Z0+Y0*Y0);
		Y0 = (Z0<0)?(-Y):Y;
		Z0 = (Z0<0)?(-Z):Z;
		X = GraphicUtils.cos(Math.atan(Z0/X0)+Math.toRadians(-ry))*Math.sqrt(X0*X0+Z0*Z0);
		Z = GraphicUtils.sin(Math.atan(Z0/X0)+Math.toRadians(-ry))*Math.sqrt(X0*X0+Z0*Z0);
		Z0 = (X0<0)?(-Z):Z;
		X0 = (X0<0)?(-X):X;
		X = GraphicUtils.cos(Math.atan(Y0/X0)+Math.toRadians(-rz))*Math.sqrt(X0*X0+Y0*Y0);
		Y = GraphicUtils.sin(Math.atan(Y0/X0)+Math.toRadians(-rz))*Math.sqrt(X0*X0+Y0*Y0);
		Y0 = (X0<0)?(-Y):Y;
		X0 = (X0<0)?(-X):X;
		*/
		double X, Y, Z, cos$, sin$, 
			r2 = -Math.toRadians(rz), r1 = -Math.toRadians(ry), r0 = -Math.toRadians(rx);

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
		location	= new double[3];
		roll_angle	= new double[3];
		
		location[0]   = location[1]   = location[2]   = 0.0;
		roll_angle[0] = roll_angle[1] = roll_angle[2] = 0.0;
	}
	
	public abstract void go();
}
