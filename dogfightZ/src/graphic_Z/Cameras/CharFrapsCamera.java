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
	//private static	final float PI = GraphicUtils.PI;
	public int	                   resolution[];			//分辨率(x,y)(引用，实体在CharVisualManager中)
	public char	                   fraps_buffer[][];		//帧缓冲区(引用，实体在CharVisualManager中)
	public List<Iterable<ThreeDs>> staticObjLists;
	public LinkedList<Thread>      ThreadSyncQueue;

	//----buffered data----
	protected int XcenterI; 
	protected int YcenterI;
	//----buffered data----
	
	public CharFrapsCamera
	(
		float	FOV, 
		float	visblt,
		int	    resolution_XY[],
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
	
	public void getXY_onCamera (
		float X0, float Y0, float Z0,
		float cameraLocation[/*3*/], //x, y, z
		float cameraRollAngl[/*3*/], //x, y, z
		float result[/*2*/] //x, y
	)	{getXY_onCamera(X0, Y0, Z0, resolution[0], resolution[1], cameraLocation, cameraRollAngl, result, FOV);}
	
	public void exposure(Iterable<Dynamic> objList, int unUsed) {
		for(ThreeDs aObject:objList) exposureObject(aObject, GraphicUtils.toRadians(roll_angle[0]), GraphicUtils.toRadians(roll_angle[1]), GraphicUtils.toRadians(roll_angle[2]));
	}

    private static final int notOnScreen[] = {-1, -1, -1};
    
	protected float exposureObject(ThreeDs aObject, float cr0, float cr1, float cr2, boolean staticOver) {
	    
		float locationOfanObj[]  = aObject.getLocation();
		float rge = GraphicUtils.range(locationOfanObj, location);
		if(aObject.getVisible() == false || rge > visibility * 10) return rge;
		
		char spc     = aObject.getSpecialDisplayChar();
		int  pcount  = aObject.getPointsCount();
		
		float rollAngleOfanObj[] = aObject.getRollAngle();
		float aPointOfanObj[]    = null;
		
		//float X0, Y0, Z0, X, Y, Z;
		int X1, Y1, Z1, X2, Y2, Z2;
		int index;
		
		float r0 = GraphicUtils.toRadians(rollAngleOfanObj[0]);
		float r1 = GraphicUtils.toRadians(rollAngleOfanObj[1]);
		float r2 = GraphicUtils.toRadians(rollAngleOfanObj[2]);
		
		final float temp = GraphicUtils.tan(FOV/2.0F);
		int p1[], p2[];
		
		//   x r/v
		for(int i=0 ; i<pcount ;) //for each point
		{
			aPointOfanObj = aObject.getPoint(i);
			
			XYLambdaI getPoint = (float X0, float Y0, float Z0) -> {
				float X, Y, Z/*, tmp1, tmp2*/, cos$, sin$;
				//获取点随着物体分别绕X、Y、Z坐标轴滚动前的原坐标
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
				X0 = X;
				Z0 = Z;

				cos$ = GraphicUtils.cos(r0);
				sin$ = GraphicUtils.sin(r0);
				Z = cos$ * Z0 - sin$ * Y0;
				Y = sin$ * Z0 + cos$ * Y0;
				Z0 = Z;
				Y0 = Y;
				//---旋转结束---
				
				//---获得物体每个点相对于摄像机的相对坐标
				X0 += locationOfanObj[0] - location[0];
				Y0 += locationOfanObj[1] - location[1];
				Z0 += locationOfanObj[2] - location[2];
				
				//---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
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
				
				if(Z0>=0)
				{
					//借用cos$作临时变量，计算小孔成像
					cos$ = XcenterI*FOV/(XcenterI+temp*Z0);
					X0 = X0 * cos$;
					Y0 = Y0 * cos$;
					//屏幕视角绕Z轴转动
					cos$ = GraphicUtils.cos(cr2);
					sin$ = GraphicUtils.sin(cr2);
					X = cos$ * X0 - sin$ * Y0;
					Y = sin$ * X0 + cos$ * Y0;
					
					int result[] = {(int)Y+XcenterI, (int)X+YcenterI, (int)Z0};
					return result;
				}
				return notOnScreen;
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
				
				if(Z1 < 0  ||  Z2 < 0) {
				    ++i;
				    continue; 
				}
				
				index = (int)((Z1 * 38) / visibility);
				
				if(index < 0) index = 0;
				else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
				
				GraphicUtils.drawLine(fraps_buffer, X1, Y1, X2, Y2, (spc =='\0'? inWorld.visualManager.point[index] : spc), staticOver);
				++i;
			} else {
				if(Z1 >=0 && X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
				{
					index = (int)((Z1 * 38) / visibility);

					if(index < 0) index = 0;
					else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
					
					if(!staticOver  ||  fraps_buffer[Y1][X1] == ' ')
						fraps_buffer[Y1][X1] = (spc =='\0'? inWorld.visualManager.point[index] : spc);
				}
				i += GraphicUtils.max(5 * (rge / visibility), 1);
			}
		}
		return rge;
	}
	
	public static float exposureAnObject (
	    char    fraps_buffer[][],
	    int     resolution[],
	    float  cameraLocation[],
	    float  visibility,
	    float  FOV,
	    int     XcenterI, 
	    int     YcenterI,
	    ThreeDs aObject, 
	    float  cr0, 
	    float  cr1, 
	    float  cr2, 
	    boolean staticOver, 
	    char    pointChar[]
	) {
        
        float locationOfanObj[]  = aObject.getLocation();
        float rge = GraphicUtils.range(locationOfanObj, cameraLocation);
        if(aObject.getVisible() == false || rge > visibility * 10) return rge;
        
        char spc     = aObject.getSpecialDisplayChar();
        int  pcount  = aObject.getPointsCount();
        
        float rollAngleOfanObj[] = aObject.getRollAngle();
        float aPointOfanObj[]    = null;
        
        //float X0, Y0, Z0, X, Y, Z;
        int X1, Y1, Z1, X2, Y2, Z2;
        int index;
        
        float r0 = GraphicUtils.toRadians(rollAngleOfanObj[0]);
        float r1 = GraphicUtils.toRadians(rollAngleOfanObj[1]);
        float r2 = GraphicUtils.toRadians(rollAngleOfanObj[2]);
        
        final float temp = GraphicUtils.tan(FOV/2.0F);
        int p1[], p2[];
        
        //   x r/v
        for(int i=0 ; i<pcount ;) //for each point
        {
            aPointOfanObj = aObject.getPoint(i);
            
            XYLambdaI getPoint = (float X0, float Y0, float Z0) -> {
                float X, Y, Z/*, tmp1, tmp2*/, cos$, sin$;
                //获取点随着物体分别绕X、Y、Z坐标轴滚动前的原坐标
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
                X0 = X;
                Z0 = Z;

                cos$ = GraphicUtils.cos(r0);
                sin$ = GraphicUtils.sin(r0);
                Z = cos$ * Z0 - sin$ * Y0;
                Y = sin$ * Z0 + cos$ * Y0;
                Z0 = Z;
                Y0 = Y;
                //---旋转结束---
                
                //---获得物体每个点相对于摄像机的相对坐标
                X0 += locationOfanObj[0] - cameraLocation[0];
                Y0 += locationOfanObj[1] - cameraLocation[1];
                Z0 += locationOfanObj[2] - cameraLocation[2];
                
                //---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
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
                
                if(Z0>=0)
                {
                    //借用cos$作临时变量，计算小孔成像
                    cos$ = XcenterI*FOV/(XcenterI+temp*Z0);
                    X0 = X0 * cos$;
                    Y0 = Y0 * cos$;
                    //屏幕视角绕Z轴转动
                    cos$ = GraphicUtils.cos(cr2);
                    sin$ = GraphicUtils.sin(cr2);
                    X = cos$ * X0 - sin$ * Y0;
                    Y = sin$ * X0 + cos$ * Y0;
                    
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
                
                if(Z1 < 0  ||  Z2 < 0) {
                    ++i;
                    continue; 
                }
                
                index = (int)((Z1 * 38) / visibility);
                
                if(index < 0) index = 0;
                else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
                
                GraphicUtils.drawLine(fraps_buffer, X1, Y1, X2, Y2, (spc =='\0'? pointChar[index] : spc), staticOver);
                ++i;
            } else {
                if(X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
                {
                    index = (int)((Z1 * 38) / visibility);

                    if(index < 0) index = 0;
                    else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
                    
                    if(!staticOver  ||  fraps_buffer[Y1][X1] == ' ')
                        fraps_buffer[Y1][X1] = (spc =='\0'? pointChar[index] : spc);
                }
                i += GraphicUtils.max(5 * (rge / visibility), 1);
            }
        }
        return rge;
    }

	public void resizeScreen(int x, int y) {
	    if(x < 1 || y < 1) return;
		inWorld.visualManager.reSizeScreen(x, y);
		resolution   = inWorld.visualManager.resolution;
		fraps_buffer = inWorld.visualManager.fraps_buffer;
        XcenterI = resolution[0] >> 1;
        YcenterI = resolution[1] >> 1;
	}
	
	interface XYLambdaI {
		int[] run(float X0, float Y0, float Z0);
	}
	
	protected float exposureObject(ThreeDs aObject, float cr0, float cr1, float cr2) {
		return exposureObject(aObject, cr0, cr1, cr2, false);
	}
	
	public Object exposure(Iterable<ThreeDs> objList)
	{
		for(ThreeDs aObject:objList) {
		    exposureObject(aObject, GraphicUtils.toRadians(roll_angle[0]), GraphicUtils.toRadians(roll_angle[1]), GraphicUtils.toRadians(roll_angle[2]));
		}
		return null;
	}
	
	public Object exposureStatic()
	{
		for(Iterable<ThreeDs> aList:staticObjLists) { 
			for(ThreeDs aObject:aList) {
				exposureObject(aObject, GraphicUtils.toRadians(roll_angle[0]), GraphicUtils.toRadians(roll_angle[1]), GraphicUtils.toRadians(roll_angle[2]), true);
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
	
	public void goStreet(float distance)
	{
		float z0, x0;
		
		z0 = GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * (GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[1])) * distance);
		x0 = GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[1])) * (GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * distance);
		location[2]+= z0;
		location[1]-= GraphicUtils.tan(GraphicUtils.toRadians(roll_angle[0])) * z0;
		location[0]+= GraphicUtils.abs(roll_angle[0]) > 90.0? -x0 : x0;
	}
	
	public void goLeft(float distance)
	{
		location[1]-=GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * distance;
		location[2]-=GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[0])) * distance;
		location[0]-=GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[2])) * distance;
	}
	
	public void goBack(float distance)
	{
		float z0, x0;
		
		z0 = GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * (GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[1])) * distance);
		x0 = GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[1])) * (GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * distance);
		location[2]-= z0;
		location[1]+= GraphicUtils.tan(GraphicUtils.toRadians(roll_angle[0])) * z0;
		location[0]-= GraphicUtils.abs(roll_angle[0]) > 90.0? -x0 : x0;
	}
	
	public void goRight(float distance)
	{
		location[1]+=GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * distance;
		location[2]+=GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[0])) * distance;
		location[0]+=GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[2])) * distance;
	}
	
	/*
	public static float tracingXY_onCamera
	(
		float X0, float Y0, float Z0,
		float resolution_X,
		float resolution_Y,
		float cameraLocation[], //x, y, z
		float cameraRollAngl[], //x, y, z
		float result[], //x, y
		float FOV
	)
	{
		float X, Y, Z, X1, Y1;
		float Xcenter = resolution_X / 2, 
			   Ycenter = resolution_Y / 2;
		
		float cr0, cr1,cr2;
		float tmp1, tmp2, temp=GraphicUtils.tan(FOV/2.0);
		
		cr0 = GraphicUtils.toRadians(cameraRollAngl[0]);
		cr1 = GraphicUtils.toRadians(cameraRollAngl[1]);
		cr2 = GraphicUtils.toRadians(cameraRollAngl[2]);
		
		
		X0 -= cameraLocation[0];
		Y0 -= cameraLocation[1];
		Z0 -= cameraLocation[2];
		
		//---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
		tmp1 = GraphicUtils.atan2(Y0, Z0)+cr0;
		tmp2 = GraphicUtils.sqrt(Z0*Z0+Y0*Y0);
		Z = GraphicUtils.cos(tmp1)*tmp2;
		Y = GraphicUtils.sin(tmp1)*tmp2;
		Y0 = Y;
		Z0 = Z;
		
		tmp1 = GraphicUtils.atan2(Z0, X0)+cr1;
		tmp2 = GraphicUtils.sqrt(X0*X0+Z0*Z0);
		X = GraphicUtils.cos(tmp1)*tmp2;
		Z = GraphicUtils.sin(tmp1)*tmp2;
		Z0 = Z;
		X0 = X;
		//---旋转结束---
		tmp1 = Xcenter*FOV/(Xcenter+temp*Z0);
		X0 = X0 * tmp1;
		Y0 = Y0 * tmp1;
		
		//屏幕视角绕Z轴转动
		tmp1 = GraphicUtils.atan2(Y0, X0)+cr2;
		tmp2 = GraphicUtils.sqrt(X0*X0+Y0*Y0);
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



