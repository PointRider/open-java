package graphic_Z.Cameras;

import java.util.LinkedList;
import java.util.List;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Managers.CharVisualManager;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.GraphicUtils;

public class CharFrapsCamera extends TDCamera<CharWorld> implements Runnable
{
	//private static	final double PI = Math.PI;
	public	short	resolution[];			//分辨率(x,y)(引用，实体在CharVisualManager中)
	public	char	fraps_buffer[][];		//帧缓冲区(引用，实体在CharVisualManager中)
	public			List<Iterable<ThreeDs>> staticObjLists;
	public			LinkedList<Thread> ThreadSyncQueue;

	//----buffered data----
	protected int XcenterI; 
	protected int YcenterI;
	//----buffered data----
	
	public CharFrapsCamera
	(
		double	FOV, 
		double	visblt,
		short	resolution_XY[],
		char	frapsBuffer[][],
		CharWorld inWhichWorld,
		List<Iterable<ThreeDs>> static_objLists
	)
	{
		super(FOV, visblt, inWhichWorld);
		staticObjLists	= static_objLists;
		resolution		= resolution_XY;
		fraps_buffer	= frapsBuffer;
		ThreadSyncQueue = new LinkedList<Thread>();
		
		//buffing
		XcenterI = resolution[0] >> 1;
		YcenterI = resolution[1] >> 1;
	}
	
	public static double rad(double x) {
		return x * (Math.PI / 180.0);
	}
	
	public static double range(double p1[], double p2[])
	{
		double d1 = (p2[0]-p1[0]);
		double d2 = (p2[1]-p1[1]);
		double d3 = (p2[2]-p1[2]);
		
		return Math.sqrt(d1*d1 + d2*d2 + d3*d3);
	}
	
	public static double getXY_onCamera
	(
		double X0, double Y0, double Z0,
		int    resolution_X,
		int    resolution_Y,
		double cameraLocation[/*3*/], //x, y, z
		double cameraRollAngl[/*3*/], //x, y, z
		double result[/*2*/], //x, y
		double FOV
	)
	{
		double X, Y, Z, X1, Y1;
		int    Xcenter = resolution_X >> 1, 
			   Ycenter = resolution_Y >> 1;
		
		double tmp1, tmp2, temp=GraphicUtils.tan(FOV/2.0);
		double cr0 = rad(cameraRollAngl[0]);
		double cr1 = rad(cameraRollAngl[1]);
		double cr2 = rad(cameraRollAngl[2]);
		
		X0 -= cameraLocation[0];
		Y0 -= cameraLocation[1];
		Z0 -= cameraLocation[2];
		
		//---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
		tmp1 = Math.atan2(Y0, Z0)+cr0;
		tmp2 = Math.sqrt(Z0*Z0+Y0*Y0);
		Z = GraphicUtils.cos(tmp1)*tmp2;
		Y = GraphicUtils.sin(tmp1)*tmp2;
		Y0 = Y;
		Z0 = Z;
		
		tmp1 = Math.atan2(Z0, X0)+cr1;
		tmp2 = Math.sqrt(X0*X0+Z0*Z0);
		X = GraphicUtils.cos(tmp1)*tmp2;
		Z = GraphicUtils.sin(tmp1)*tmp2;
		Z0 = Z;
		X0 = X;
		//---旋转结束---
		
		if(Z0>=0 && result!=null)
		{
			tmp1 = Xcenter*FOV/(Xcenter+temp*Z0);
			
			X0 = X0 * tmp1;
			Y0 = Y0 * tmp1;
			
			//屏幕视角绕Z轴转动
			tmp1 = Math.atan2(Y0, X0)+cr2;
			tmp2 = Math.sqrt(X0*X0+Y0*Y0);
			X = GraphicUtils.cos(tmp1)*tmp2;
			Y = GraphicUtils.sin(tmp1)*tmp2;

			X1 = (short) (Y+Xcenter);
			Y1 = (short) (X+Ycenter);
			
			if(X1>=0 && Y1>=0 && X1<resolution_X && Y1<resolution_Y)
			{
				result[0] = X1;
				result[1] = Y1;
			}
			else result[0] = result[1] = -1;
		}
		
		return Z0;
	}
	
	public static double rangeXY(double x0, double y0, double x2, double y2)
	{
		x2 -= x0;
		y2 -= y0;
		return Math.sqrt(x2*x2 + y2*y2);
	}
	
	public void getXY_onCamera
	(
		double X0, double Y0, double Z0,
		double cameraLocation[/*3*/], //x, y, z
		double cameraRollAngl[/*3*/], //x, y, z
		double result[/*2*/] //x, y
	)	{getXY_onCamera(X0, Y0, Z0, resolution[0], resolution[1], cameraLocation, cameraRollAngl, result, FOV);}
	
	public void exposure(Iterable<Dynamic> objList, int unUsed)
	{
		for(ThreeDs aObject:objList) exposureObject(aObject, rad(roll_angle[0]), rad(roll_angle[1]), rad(roll_angle[2]));
	}
	
	protected double exposureObject(ThreeDs aObject, double cr0, double cr1, double cr2, boolean staticOver) {

		double locationOfanObj[]  = aObject.getLocation();
		double rge = range(locationOfanObj, location);
		if(aObject.getVisible() == false || rge > visibility * 10) return rge;
		
		char spc     = aObject.getSpecialDisplayChar();
		int  pcount  = aObject.getPointsCount();
		
		double rollAngleOfanObj[] = aObject.getRollAngle();
		double aPointOfanObj[]    = null;
		
		//double X0, Y0, Z0, X, Y, Z;
		int X1, Y1, Z1, X2, Y2, Z2;
		int index;
		
		double r0 = rad(rollAngleOfanObj[0]);
		double r1 = rad(rollAngleOfanObj[1]);
		double r2 = rad(rollAngleOfanObj[2]);
		
		final double temp = GraphicUtils.tan(FOV/2.0);
		int p1[], p2[];
		
		//   x r/v
		for(int i=0 ; i<pcount ;) //for each point
		{
			aPointOfanObj = aObject.getPoint(i);
			
			XYLambdaI getPoint = (double X0, double Y0, double Z0) -> {
				double X, Y, Z, tmp1, tmp2;
				//获取点随着物体分别绕X、Y、Z坐标轴滚动前的原坐标
				/*
				X0 = aPointOfanObj[0];
				Y0 = aPointOfanObj[1];
				Z0 = aPointOfanObj[2];
				*/
				tmp1 = Math.atan2(Y0, X0)+r2;
				tmp2 = Math.sqrt(X0*X0+Y0*Y0);
				//---自身旋转---
				X = GraphicUtils.cos(tmp1)*tmp2;
				Y = GraphicUtils.sin(tmp1)*tmp2;
				Y0 = Y;
				X0 = X;
				
				tmp1 = Math.atan2(Z0, X0)+r1;
				tmp2 = Math.sqrt(X0*X0+Z0*Z0);
				
				X = GraphicUtils.cos(tmp1)*tmp2;
				Z = GraphicUtils.sin(tmp1)*tmp2;
				Z0 = Z;
				X0 = X;
				
				tmp1 = Math.atan2(Y0, Z0)+r0;
				tmp2 = Math.sqrt(Z0*Z0+Y0*Y0);
				
				Z = GraphicUtils.cos(tmp1)*tmp2;
				Y = GraphicUtils.sin(tmp1)*tmp2;
				Y0 = Y;
				Z0 = Z;
				//---旋转结束---
				
				X0 += locationOfanObj[0] - location[0];
				Y0 += locationOfanObj[1] - location[1];
				Z0 += locationOfanObj[2] - location[2];
				
				//---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
				tmp1 = Math.atan2(Y0, Z0) + cr0;
				tmp2 = Math.sqrt(Z0*Z0+Y0*Y0);
				Z = GraphicUtils.cos(tmp1)*tmp2;
				Y = GraphicUtils.sin(tmp1)*tmp2;
				Y0 = Y;
				Z0 = Z;
				
				tmp1 = Math.atan2(Z0, X0) + cr1;
				tmp2 = Math.sqrt(X0*X0+Z0*Z0);
				X = GraphicUtils.cos(tmp1)*tmp2;
				Z = GraphicUtils.sin(tmp1)*tmp2;
				Z0 = Z;
				X0 = X;
				//---旋转结束---
				
				if(Z0>=0)
				{
					tmp1 = XcenterI*FOV/(XcenterI+temp*Z0);
					X0 = X0 * tmp1;
					Y0 = Y0 * tmp1;
					
					//屏幕视角绕Z轴转动
					tmp1 = Math.atan2(Y0, X0) + cr2;
					tmp2 = Math.sqrt(X0*X0+Y0*Y0);
					X = GraphicUtils.cos(tmp1)*tmp2;
					Y = GraphicUtils.sin(tmp1)*tmp2;
					/*
					X1 = ((int)Y+XcenterI);
					Y1 = ((int)X+YcenterI);
					
					if(X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
					{
						int index = (int)Z0 / 64;
						
						if(index < 0) index = 0;
						else if(index > 7) index = 7;
						
						if(!staticOver  ||  fraps_buffer[Y1][X1] == ' ')
							fraps_buffer[Y1][X1] = (spc =='\0'? inWorld.visualManager.point[index] : spc);
					}*/
					int result[] = {(int)Y+XcenterI, (int)X+YcenterI, (int)Z0};
					return result;
				}
				int result[] = {-1, -1, -1};
				return result;
			};
			
			p1 = getPoint.run(aPointOfanObj[0], aPointOfanObj[1], aPointOfanObj[2]);

			X1 = p1[0];
			Y1 = p1[1];
			Z1 = p1[2];
			
			if(aObject.constructWithLine()) {
				p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5]);
				X2 = p2[0];
				Y2 = p2[1];
				Z2 = p2[2];
				
				if(Z1 < 0  ||  Z2 < 0) return rge;
				
				index = (int)((Z1 * 38) / visibility);
				
				if(index < 0) index = 0;
				else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
				
				GraphicUtils.drawLine(fraps_buffer, X1, Y1, X2, Y2, (spc =='\0'? inWorld.visualManager.point[index] : spc), staticOver);
				++i;
			} else {
				if(X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
				{
					index = (int)((Z1 * 38) / visibility);

					if(index < 0) index = 0;
					else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
					
					if(!staticOver  ||  fraps_buffer[Y1][X1] == ' ')
						fraps_buffer[Y1][X1] = (spc =='\0'? inWorld.visualManager.point[index] : spc);
				}
				i += Math.max(5 * (rge / visibility), 1);
			}
		}
		return rge;
	}

	public void resizeScreen(short x, short y) {
		inWorld.visualManager.reSizeScreen(x, y);
		resolution   = inWorld.visualManager.resolution;
		fraps_buffer = inWorld.visualManager.fraps_buffer;
	}
	
	interface XYLambdaI {
		int[] run(double X0, double Y0, double Z0);
	}
	
	protected double exposureObject(ThreeDs aObject, double cr0, double cr1, double cr2) {
		return exposureObject(aObject, cr0, cr1, cr2, false);
	}
	
	public Object exposure(Iterable<ThreeDs> objList)
	{
		for(ThreeDs aObject:objList) exposureObject(aObject, rad(roll_angle[0]), rad(roll_angle[1]), rad(roll_angle[2]));
		return null;
	}
	
	public Object exposureStatic()
	{
		for(Iterable<ThreeDs> aList:staticObjLists) { 
			for(ThreeDs aObject:aList) {
				exposureObject(aObject, rad(roll_angle[0]), rad(roll_angle[1]), rad(roll_angle[2]), true);
			}
		}
		return null;
	}
	
	@Override
	public void run()
	{
		exposureStatic();
	}
	
	public Object exposure()
	{
		exposure(inWorld.objectsManager.objects);
		return null;
	}
	
	public void goStreet(double distance)
	{
		double z0, x0;
		
		z0 = GraphicUtils.cos(rad(roll_angle[0])) * (GraphicUtils.cos(rad(roll_angle[1])) * distance);
		x0 = GraphicUtils.sin(rad(roll_angle[1])) * (GraphicUtils.cos(rad(roll_angle[0])) * distance);
		location[2]+= z0;
		location[1]-= GraphicUtils.tan(rad(roll_angle[0])) * z0;
		location[0]+= Math.abs(roll_angle[0]) > 90.0? -x0 : x0;
	}
	
	public void goLeft(double distance)
	{
		location[1]-=GraphicUtils.cos(rad(roll_angle[0])) * distance;
		location[2]-=GraphicUtils.sin(rad(roll_angle[0])) * distance;
		location[0]-=GraphicUtils.sin(rad(roll_angle[2])) * distance;
	}
	
	public void goBack(double distance)
	{
		double z0, x0;
		
		z0 = GraphicUtils.cos(rad(roll_angle[0])) * (GraphicUtils.cos(rad(roll_angle[1])) * distance);
		x0 = GraphicUtils.sin(rad(roll_angle[1])) * (GraphicUtils.cos(rad(roll_angle[0])) * distance);
		location[2]-= z0;
		location[1]+= GraphicUtils.tan(rad(roll_angle[0])) * z0;
		location[0]-= Math.abs(roll_angle[0]) > 90.0? -x0 : x0;
	}
	
	public void goRight(double distance)
	{
		location[1]+=GraphicUtils.cos(rad(roll_angle[0])) * distance;
		location[2]+=GraphicUtils.sin(rad(roll_angle[0])) * distance;
		location[0]+=GraphicUtils.sin(rad(roll_angle[2])) * distance;
	}
	
	/*
	public static double tracingXY_onCamera
	(
		double X0, double Y0, double Z0,
		double resolution_X,
		double resolution_Y,
		double cameraLocation[], //x, y, z
		double cameraRollAngl[], //x, y, z
		double result[], //x, y
		double FOV
	)
	{
		double X, Y, Z, X1, Y1;
		double Xcenter = resolution_X / 2, 
			   Ycenter = resolution_Y / 2;
		
		double cr0, cr1,cr2;
		double tmp1, tmp2, temp=GraphicUtils.tan(FOV/2.0);
		
		cr0 = rad(cameraRollAngl[0]);
		cr1 = rad(cameraRollAngl[1]);
		cr2 = rad(cameraRollAngl[2]);
		
		
		X0 -= cameraLocation[0];
		Y0 -= cameraLocation[1];
		Z0 -= cameraLocation[2];
		
		//---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
		tmp1 = Math.atan2(Y0, Z0)+cr0;
		tmp2 = Math.sqrt(Z0*Z0+Y0*Y0);
		Z = GraphicUtils.cos(tmp1)*tmp2;
		Y = GraphicUtils.sin(tmp1)*tmp2;
		Y0 = Y;
		Z0 = Z;
		
		tmp1 = Math.atan2(Z0, X0)+cr1;
		tmp2 = Math.sqrt(X0*X0+Z0*Z0);
		X = GraphicUtils.cos(tmp1)*tmp2;
		Z = GraphicUtils.sin(tmp1)*tmp2;
		Z0 = Z;
		X0 = X;
		//---旋转结束---
		tmp1 = Xcenter*FOV/(Xcenter+temp*Z0);
		X0 = X0 * tmp1;
		Y0 = Y0 * tmp1;
		
		//屏幕视角绕Z轴转动
		tmp1 = Math.atan2(Y0, X0)+cr2;
		tmp2 = Math.sqrt(X0*X0+Y0*Y0);
		X = GraphicUtils.cos(tmp1)*tmp2;
		Y = GraphicUtils.sin(tmp1)*tmp2;
		
		X1 = (short) (Y+Xcenter);
		Y1 = (short) (X+Ycenter);
		
		result[0] = X1;
		result[1] = Y1;
		
		return Z0;
	}
	*/
}



