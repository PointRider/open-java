package graphic_Z.Cameras;

import java.util.LinkedList;
import java.util.List;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.GraphicUtils;

public class CharFrapsCamera extends TDCamera<CharWorld> implements Runnable
{
	//private static	final double PI = Math.PI;
	public	short	resolution[];			//分辨率(x,y)(引用，实体在CharVisualManager中)
	public	char	fraps_buffer[][];		//帧缓冲区(引用，实体在CharVisualManager中)
	public			List<Iterable<ThreeDs>> staticObjLists;
	public			LinkedList<Thread> ThreadSyncQueue;

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
	}
	
	public static double rad(double x)
	{
		return x * (Math.PI / 180.0);
	}
	
	public static double range(double p1[], double p2[])
	{
		return Math.abs
		(
			Math.sqrt
			(
				(p2[0]-p1[0])*(p2[0]-p1[0]) +
				(p2[1]-p1[1])*(p2[1]-p1[1]) +
				(p2[2]-p1[2])*(p2[2]-p1[2]) 
			)
		);
	}
	
	public static double getXY_onCamera
	(
		double X0, double Y0, double Z0,
		double resolution_X,
		double resolution_Y,
		double cameraLocation[/*3*/], //x, y, z
		double cameraRollAngl[/*3*/], //x, y, z
		double result[/*2*/], //x, y
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
		return Math.sqrt((x2-x0)*(x2-x0)+(y2-y0)*(y2-y0));
	}
	
	public static double tracingXY_onCamera
	(
		double X0, double Y0, double Z0,
		double resolution_X,
		double resolution_Y,
		double cameraLocation[/*3*/], //x, y, z
		double cameraRollAngl[/*3*/], //x, y, z
		double result[/*2*/], //x, y
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
	
	public void getXY_onCamera
	(
		double X0, double Y0, double Z0,
		double cameraLocation[/*3*/], //x, y, z
		double cameraRollAngl[/*3*/], //x, y, z
		double result[/*2*/] //x, y
	)	{getXY_onCamera(X0, Y0, Z0, resolution[0], resolution[1], cameraLocation, cameraRollAngl, result, FOV);}
	
	public void exposure(Iterable<Dynamic> objList, int unUsed)
	{
		reversedAngle[0] = roll_source[0] + 180;
		reversedAngle[1] = -roll_source[1];
		reversedAngle[2] = -roll_source[2];
		
		char point[] = inWorld.visualManager.point;
		int  pcount,
			 Xcenter = resolution[0] / 2, 
			 Ycenter = resolution[1] / 2;
		
		double X0, Y0, Z0, X, Y, Z;
		
		double locationOfanObj[] = new double[3];
		double rollAngleOfanObj[] = new double[3];
		double aPointOfanObj[] = new double[3];
		
		short  X1, Y1;
		char spc;
		
		double r0, r1, r2;
		double cr0, cr1,cr2;
		double tmp1, tmp2, temp=GraphicUtils.tan(FOV/2.0);
		
		cr0 = rad(roll_angle[0]);
		cr1 = rad(roll_angle[1]);
		cr2 = rad(roll_angle[2]);
		
		for(ThreeDs aObject:objList)	//for each object
		{
			spc = aObject.getSpecialDisplayChar();
			locationOfanObj = aObject.getLocation();
			rollAngleOfanObj= aObject.getRollAngle();
			
			r0 = rad(rollAngleOfanObj[0]);
			r1 = rad(rollAngleOfanObj[1]);
			r2 = rad(rollAngleOfanObj[2]);
			
			if
			(
				aObject.getVisible() == false || 
				range(locationOfanObj, location) > visibility
			)	continue;
			
			pcount = aObject.getPointsCount();
			for(int i=0 ; i<pcount ; ++i)				//for each point
			{
				aPointOfanObj = aObject.getPoint(i);
				//获取点随着物体分别绕X、Y、Z坐标轴滚动前的原坐标
				X0 = aPointOfanObj[0];
				Y0 = aPointOfanObj[1];
				Z0 = aPointOfanObj[2];
				
				X0 = aPointOfanObj[0];
				Y0 = aPointOfanObj[1];
				Z0 = aPointOfanObj[2];
				
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
				
				if(Z0>=0)
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
					
					if(X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
					{
						int index = (int)Z0 / 64;
						if(index < 0)
							index = 0;
						else if(index > 7)
							index = 7;
						fraps_buffer[Y1][X1] = (spc =='\0'? point[index] : spc);
					}
				}
			}	
		}
	}
	
	public Object exposure(Iterable<ThreeDs> objList)
	{
		reversedAngle[0] = roll_source[0] + 180;
		reversedAngle[1] = -roll_source[1];
		reversedAngle[2] = -roll_source[2];
		
		char point[] = inWorld.visualManager.point;
		int  pcount,
			 Xcenter = resolution[0] / 2, 
			 Ycenter = resolution[1] / 2;
		
		double X0, Y0, Z0, X, Y, Z;
		
		double locationOfanObj[] = new double[3];
		double rollAngleOfanObj[] = new double[3];
		double aPointOfanObj[] = new double[3];
		
		short  X1, Y1;
		char spc;
		double r0, r1, r2;
		double cr0, cr1,cr2;
		double tmp1, tmp2, temp=GraphicUtils.tan(FOV/2.0);
		
		cr0 = rad(roll_angle[0]);
		cr1 = rad(roll_angle[1]);
		cr2 = rad(roll_angle[2]);
		
		for(ThreeDs aObject:objList)	//for each object
		{
			spc = aObject.getSpecialDisplayChar();
			locationOfanObj = aObject.getLocation();
			rollAngleOfanObj= aObject.getRollAngle();

			r0 = rad(rollAngleOfanObj[0]);
			r1 = rad(rollAngleOfanObj[1]);
			r2 = rad(rollAngleOfanObj[2]);
			
			if
			(
				aObject.getVisible() == false || 
				range(locationOfanObj, location) > visibility
			)	continue;
			
			pcount = aObject.getPointsCount();
			for(int i=0 ; i<pcount ; ++i)				//for each point
			{
				aPointOfanObj = aObject.getPoint(i);
				//获取点随着物体分别绕X、Y、Z坐标轴滚动前的原坐标
				X0 = aPointOfanObj[0];
				Y0 = aPointOfanObj[1];
				Z0 = aPointOfanObj[2];
				
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
				
				if(Z0>=0)
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
					
					if(X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
					{
						int index = (int)Z0 / 64;
						if(index < 0)
							index = 0;
						else if(index > 7)
							index = 7;
						
						if(fraps_buffer[Y1][X1] != ' ')
							fraps_buffer[Y1][X1] = (spc =='\0'? point[index] : spc);
					}
				}
			}	
		}
		
		return null;
	}
	
	@Override
	public void run()
	{
		reversedAngle[0] = roll_source[0] + 180;
		reversedAngle[1] = -roll_source[1];
		reversedAngle[2] = -roll_source[2];
		
		char point[] = inWorld.visualManager.point;
		int  pcount,
			 Xcenter = resolution[0] / 2, 
			 Ycenter = resolution[1] / 2;
		
		double X0, Y0, Z0, X, Y, Z;
		
		double locationOfanObj[] = new double[3];
		double rollAngleOfanObj[] = new double[3];
		double aPointOfanObj[] = new double[3];
		
		short  X1, Y1;
		char spc;
		double r0, r1, r2;
		double cr0, cr1,cr2;
		double tmp1, tmp2, temp=GraphicUtils.tan(FOV/2.0);
		
		cr0 = rad(roll_angle[0]);
		cr1 = rad(roll_angle[1]);
		cr2 = rad(roll_angle[2]);
		
		for(Iterable<ThreeDs> aList:staticObjLists) for(ThreeDs aObject:aList)	//for each object
		{
			spc = aObject.getSpecialDisplayChar();
			locationOfanObj = aObject.getLocation();
			rollAngleOfanObj= aObject.getRollAngle();

			r0 = rad(rollAngleOfanObj[0]);
			r1 = rad(rollAngleOfanObj[1]);
			r2 = rad(rollAngleOfanObj[2]);
			
			if
			(
				aObject.getVisible() == false || 
				range(locationOfanObj, location) > visibility
			)	continue;
			
			pcount = aObject.getPointsCount();
			for(int i=0 ; i<pcount ; ++i)				//for each point
			{
				aPointOfanObj = aObject.getPoint(i);
				//获取点随着物体分别绕X、Y、Z坐标轴滚动前的原坐标
				X0 = aPointOfanObj[0];
				Y0 = aPointOfanObj[1];
				Z0 = aPointOfanObj[2];
				
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
				
				if(Z0>=0)
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
					
					if(X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
					{
						int index = (int)Z0 / 64;
						
						if(index < 0) index = 0;
						else if(index > 7) index = 7;
						
						if(fraps_buffer[Y1][X1] == ' ')
							fraps_buffer[Y1][X1] = (spc =='\0'? point[index] : spc);
					}
				}
			}	
		}
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
}
