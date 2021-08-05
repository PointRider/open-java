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
		double X, Y, Z;
		
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
		
		result[0] = X0;
		result[1] = Y0;
		result[2] = Z0;
	}
	
	public static void getXYZ_beforeRolling
	(
		double X0, double Y0, double Z0,
		double rx, double ry, double rz,
		
		double result[/*3*/]  //x, y, z
	)
	{
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
		
		result[0] = X0;
		result[1] = Y0;
		result[2] = Z0;
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
