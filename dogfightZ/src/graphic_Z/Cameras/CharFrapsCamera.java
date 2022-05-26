package graphic_Z.Cameras;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Interfaces.ThreeDs.PointType;
import graphic_Z.Managers.CharVisualManager;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.GraphicUtils;

public class CharFrapsCamera extends TDCamera<CharWorld> implements Runnable
{
	//private static	final float PI = GraphicUtils.PI;
	public int	                   resolution[];			//分辨率(x,y)(引用，实体在CharVisualManager中)
	public char	                   fraps_buffer[][];		//帧缓冲区(引用，实体在CharVisualManager中)
    public ConcurrentLinkedQueue<char[][]>    motionalBlur;
	private float                  zBuffer[][];
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
		ConcurrentLinkedQueue<char[][]> motional_blur,
		CharWorld inWhichWorld,
		List<Iterable<ThreeDs>> static_objLists
	)
	{
		super(FOV, visblt, inWhichWorld);
		staticObjLists	= static_objLists;
		zBuffer         = inWhichWorld.visualManager.zBuffer;
		resolution		= resolution_XY;
		fraps_buffer	= frapsBuffer;
        motionalBlur    = motional_blur;
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
		for(ThreeDs aObject:objList) exposureObject(aObject, roll_angle[0], roll_angle[1], roll_angle[2]);
	}

    private static final float[] notOnScreen = {-5.0F, -5.0F, -5.0F};
    
	protected float exposureObject(ThreeDs aObject, float cr0, float cr1, float cr2, boolean staticOver) {
	    
		float locationOfanObj[]  = aObject.getLocation();
		float rge = GraphicUtils.range(locationOfanObj, location);
		if(aObject.getVisible() == false || rge > visibility * 10) return rge;
		
		char spc     = aObject.getSpecialDisplayChar(), suc = '\0';
		int  pcount  = aObject.getPointsCount();
		
		float rollAngleOfanObj[] = aObject.getRollAngle();
		float aPointOfanObj[]    = null;
		
		//float X0, Y0, Z0, X, Y, Z;
		float X1, Y1, Z1, X2, Y2, Z2, Z3, rel_X, rel_Y, rel_Z;
		int index, x, y;
		
		float r0 = rollAngleOfanObj[0];
		float r1 = rollAngleOfanObj[1];
		float r2 = rollAngleOfanObj[2];

		rel_X = locationOfanObj[0] - location[0];
		rel_Y = locationOfanObj[1] - location[1];
		rel_Z = locationOfanObj[2] - location[2];
        
		final float temp = GraphicUtils.tan(FOV/2.0F);
		float p1[], p2[], p3[];
		
		boolean absPoint = (aObject.getPointType() == PointType.abs? true: false);
		//   x r/v
		for(int i=0 ; i<pcount ;) //for each point
		{
			aPointOfanObj = (absPoint? aPointOfanObj = aObject.getAbsPoint(i): aObject.getPoint(i));
            
			XYLambdaI2 getPoint = (float X0, float Y0, float Z0, float relX, float relY, float relZ, boolean absP) -> {
				float X, Y, Z/*, tmp1, tmp2*/, cos$, sin$;
				if(!absP) {
    				//物体上的每个点 绕物体自身中心点旋转
    				//r0、r1、r2是绕x、y、z轴的旋转角度（弧度制）
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
    				X0 += relX;
    				Y0 += relY;
    				Z0 += relZ;
				} else {
				    X0 -= location[0];
	                Y0 -= location[1];
	                Z0 -= location[2];
				}
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
					
					float result[] = {Y+XcenterI, X+YcenterI, Z0};
					return result;
				}
				return notOnScreen;
			};
			
			//获取一个点在屏幕上的x,y坐标以及距离屏幕的深度z
            p1 = getPoint.run(aPointOfanObj[0], aPointOfanObj[1], aPointOfanObj[2], rel_X, rel_Y, rel_Z, absPoint);
            X1 = p1[0];
            Y1 = p1[1];
            Z1 = p1[2];
            
            switch(aObject.getDrawingMethod()) {
            case drawTriangleSurface:

                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
                Z2 = p2[2];
                p3 = getPoint.run(aPointOfanObj[6], aPointOfanObj[7], aPointOfanObj[8], rel_X, rel_Y, rel_Z, absPoint);
                Z3 = p3[2];
                
                suc = aObject.getSurfaceChar(i);

                if(Z1 < 0  ||  Z2 < 0  || Z3 < 0) {
                    ++i;
                    continue;
                }
                
                GraphicUtils.drawTriangleSurface_ZBuffer(fraps_buffer, zBuffer, p1, p2, p3, suc, staticOver);
                
                ++i;
                break;
            case drawLine:
                //获取二个点在屏幕上的x,y坐标以及距离屏幕的深度z
                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
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
                break;
            case drawPoint:
                x = (int)(X1 + 0.5F);
                y = (int)(Y1 + 0.5F);
                if(x>=0 && y>=0 && x<resolution[0] && y<resolution[1]) {
                    index = (int)((Z1 * 38) / visibility);

                    if(index < 0) index = 0;
                    else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
                    
                    if(!staticOver  ||  fraps_buffer[y][x] == ' ')
                        fraps_buffer[y][x] = (spc =='\0'? inWorld.visualManager.point[index] : spc);
                }
                i += GraphicUtils.max(5 * (rge / visibility), 1);
                break;
            }
		}
		return rge;
	}
	
	protected void exposureObject(ThreeDs aObject, boolean staticOver, float result_xyzr[]) {
	    float locationOfanObj[]  = aObject.getLocation();
        float rge = GraphicUtils.range(locationOfanObj, location);
        if(aObject.getVisible() == false || rge > visibility * 10) {
            result_xyzr[0] = -1;
            result_xyzr[1] = -1;
            result_xyzr[2] = -1;
            result_xyzr[3] = rge;
            return;
        }
        
        char spc     = aObject.getSpecialDisplayChar(), suc = '\0';
        int  pcount  = aObject.getPointsCount();
        
        float rollAngleOfanObj[] = aObject.getRollAngle();
        float aPointOfanObj[]    = null;
        
        //float X0, Y0, Z0, X, Y, Z;
        float X1, Y1, Z1, X2, Y2, Z2, Z3, rel_X, rel_Y, rel_Z;
        int index, x, y;
        
        float r0 = rollAngleOfanObj[0];
        float r1 = rollAngleOfanObj[1];
        float r2 = rollAngleOfanObj[2];

        rel_X = locationOfanObj[0] - location[0];
        rel_Y = locationOfanObj[1] - location[1];
        rel_Z = locationOfanObj[2] - location[2];

        result_xyzr[0] = rel_X;
        result_xyzr[1] = rel_Y;
        result_xyzr[2] = rel_Z;
        result_xyzr[3] = rge;
        
        final float temp = GraphicUtils.tan(FOV/2.0F);
        float p1[], p2[], p3[];
        
        boolean absPoint = (aObject.getPointType() == PointType.abs? true: false);
        //   x r/v
        for(int i=0 ; i<pcount ;) //for each point
        {
            aPointOfanObj = (absPoint? aPointOfanObj = aObject.getAbsPoint(i): aObject.getPoint(i));
            
            XYLambdaI2 getPoint = (float X0, float Y0, float Z0, float relX, float relY, float relZ, boolean absP) -> {
                float X, Y, Z/*, tmp1, tmp2*/, cos$, sin$;
                if(!absP) {
                    //物体上的每个点 绕物体自身中心点旋转
                    //r0、r1、r2是绕x、y、z轴的旋转角度（弧度制）
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
                    X0 += relX;
                    Y0 += relY;
                    Z0 += relZ;
                } else {
                    X0 -= location[0];
                    Y0 -= location[1];
                    Z0 -= location[2];
                }
                //---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
                cos$ = GraphicUtils.cos(roll_angle[0]);
                sin$ = GraphicUtils.sin(roll_angle[0]);
                Z = cos$ * Z0 - sin$ * Y0;
                Y = sin$ * Z0 + cos$ * Y0;
                Z0 = Z;
                Y0 = Y;

                cos$ = GraphicUtils.cos(roll_angle[1]);
                sin$ = GraphicUtils.sin(roll_angle[1]);
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
                    cos$ = GraphicUtils.cos(roll_angle[2]);
                    sin$ = GraphicUtils.sin(roll_angle[2]);
                    X = cos$ * X0 - sin$ * Y0;
                    Y = sin$ * X0 + cos$ * Y0;
                    
                    float result[] = {Y+XcenterI, X+YcenterI, Z0};
                    return result;
                }
                return notOnScreen;
            };
            
            //获取一个点在屏幕上的x,y坐标以及距离屏幕的深度z
            p1 = getPoint.run(aPointOfanObj[0], aPointOfanObj[1], aPointOfanObj[2], rel_X, rel_Y, rel_Z, absPoint);
            X1 = p1[0];
            Y1 = p1[1];
            Z1 = p1[2];
            
            switch(aObject.getDrawingMethod()) {
            case drawTriangleSurface:

                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
                Z2 = p2[2];
                p3 = getPoint.run(aPointOfanObj[6], aPointOfanObj[7], aPointOfanObj[8], rel_X, rel_Y, rel_Z, absPoint);
                Z3 = p3[2];
                
                suc = aObject.getSurfaceChar(i);

                if(Z1 < 0  ||  Z2 < 0  || Z3 < 0) {
                    ++i;
                    continue;
                }
                
                GraphicUtils.drawTriangleSurface_ZBuffer(fraps_buffer, zBuffer, p1, p2, p3, suc, staticOver);
                
                ++i;
                break;
            case drawLine:
                //获取二个点在屏幕上的x,y坐标以及距离屏幕的深度z
                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
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
                break;
            case drawPoint:
                x = (int)(X1 + 0.5F);
                y = (int)(Y1 + 0.5F);
                if(x>=0 && y>=0 && x<resolution[0] && y<resolution[1]) {
                    index = (int)((Z1 * 38) / visibility);

                    if(index < 0) index = 0;
                    else if(index > CharVisualManager.POINTLEVEL) index = CharVisualManager.POINTLEVEL;
                    
                    if(!staticOver  ||  fraps_buffer[y][x] == ' ')
                        fraps_buffer[y][x] = (spc =='\0'? inWorld.visualManager.point[index] : spc);
                }
                i += GraphicUtils.max(5 * (rge / visibility), 1);
                break;
            }
        }
	}
	
	//char                      fraps_buffer[][]
	//ThreeDs aObject, float cr0, float cr1, float cr2, boolean staticOver
	public static final float exposureAnObject (
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
     ) {return exposureAnObject (
            fraps_buffer, 
            resolution, 
            cameraLocation, 
            visibility, 
            FOV, 
            XcenterI, YcenterI, 
            aObject, 
            cr0, cr1, cr2, 
            staticOver, 
            pointChar, 
            null
	);}
	
	public static final float exposureAnObject (
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
	    char    pointChar[],
	    float   zBuffer[][]
	) {
        
        float locationOfanObj[]  = aObject.getLocation();
        float rge = GraphicUtils.range(locationOfanObj, cameraLocation);
        if(aObject.getVisible() == false || rge > visibility * 10) return rge;
        
        char spc     = aObject.getSpecialDisplayChar(), suc = '\0';
        int  pcount  = aObject.getPointsCount();
        
        float rollAngleOfanObj[] = aObject.getRollAngle();
        float aPointOfanObj[]    = null;
        
        //float X0, Y0, Z0, X, Y, Z;
        float X1, Y1, Z1, X2, Y2, Z2, Z3, rel_X, rel_Y, rel_Z;
        int index, x, y;
        
        rel_X = locationOfanObj[0] - cameraLocation[0];
        rel_Y = locationOfanObj[1] - cameraLocation[1];
        rel_Z = locationOfanObj[2] - cameraLocation[2];
        float r0 = rollAngleOfanObj[0];
        float r1 = rollAngleOfanObj[1];
        float r2 = rollAngleOfanObj[2];
        
        final float temp = GraphicUtils.tan(FOV/2.0F);
        float p1[], p2[], p3[];

        boolean absPoint = (aObject.getPointType() == PointType.abs? true: false);
        //   x r/v
        for(int i=0 ; i<pcount ;) //for each point
        {
            aPointOfanObj = (absPoint? aPointOfanObj = aObject.getAbsPoint(i): aObject.getPoint(i));
            
            XYLambdaI2 getPoint = (float X0, float Y0, float Z0, float relX, float relY, float relZ, boolean absP) -> {
                float X, Y, Z/*, tmp1, tmp2*/, cos$, sin$;
                if(!absP) {
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
                    X0 += relX;
                    Y0 += relY;
                    Z0 += relZ;
                } else {
                    X0 -= cameraLocation[0];
                    Y0 -= cameraLocation[1];
                    Z0 -= cameraLocation[2];
                }
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
                
                if(Z0 >= 0)
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
                    
                    float result[] = {Y+XcenterI, X+YcenterI, Z0};
                    return result;
                }
                return notOnScreen;
            };
            
            //获取一个点在屏幕上的x,y坐标以及距离屏幕的深度z
            p1 = getPoint.run(aPointOfanObj[0], aPointOfanObj[1], aPointOfanObj[2], rel_X, rel_Y, rel_Z, absPoint);
            X1 = p1[0];
            Y1 = p1[1];
            Z1 = p1[2];
            
            switch(aObject.getDrawingMethod()) {
            case drawTriangleSurface:

                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
                Z2 = p2[2];
                p3 = getPoint.run(aPointOfanObj[6], aPointOfanObj[7], aPointOfanObj[8], rel_X, rel_Y, rel_Z, absPoint);
                Z3 = p3[2];
                
                suc = aObject.getSurfaceChar(i);

                if(Z1 < 0  ||  Z2 < 0  || Z3 < 0) {
                    ++i;
                    continue;
                }
                
                GraphicUtils.drawTriangleSurface_ZBuffer(fraps_buffer, zBuffer, p1, p2, p3, suc, staticOver);
                
                ++i;
                break;
            case drawLine:
                //获取二个点在屏幕上的x,y坐标以及距离屏幕的深度z
                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
                X2 = p2[0];
                Y2 = p2[1];
                Z2 = p2[2];
                
                if(Z1 < 0  ||  Z2 < 0) {
                    ++i;
                    continue; 
                }
                
                index = (int)((Z1 * 38) / visibility);
                
                if(index < 0) index = 0;
                else if(index > pointChar.length - 1) index = pointChar.length - 1;
                
                GraphicUtils.drawLine(fraps_buffer, X1, Y1, X2, Y2, (spc =='\0'? pointChar[index] : spc), staticOver);
                ++i;
                break;
            case drawPoint:
                x = (int)(X1 += 0.5F);
                y = (int)(Y1 += 0.5F);
                if(x>=0 && y>=0 && x<resolution[0] && y<resolution[1]) {
                    index = (int)((Z1 * 38) / visibility);

                    if(index < 0) index = 0;
                    else if(index > pointChar.length - 1) index = pointChar.length - 1;
                    
                    if(!staticOver  ||  fraps_buffer[y][x] == ' ')
                        fraps_buffer[y][x] = (spc =='\0'? pointChar[index] : spc);
                }
                i += GraphicUtils.max(5 * (rge / visibility), 1);
                break;
            } 
        }
        return rge;
    }
	
	public static final float exposureAnObject (
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
        char    pointChar
     ) {return exposureAnObject (
            fraps_buffer, 
            resolution, 
            cameraLocation, 
            visibility, 
            FOV, 
            XcenterI, YcenterI, 
            aObject, 
            cr0, cr1, cr2, 
            staticOver, 
            pointChar, 
            null
    );}
    
    public static final float exposureAnObject (
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
        char    pointChar,
        float   zBuffer[][]
    ) {
        
        float locationOfanObj[]  = aObject.getLocation();
        float rge = GraphicUtils.range(locationOfanObj, cameraLocation);
        if(aObject.getVisible() == false || rge > visibility * 10) return rge;
        
        char spc     = aObject.getSpecialDisplayChar(), suc = '\0';
        int  pcount  = aObject.getPointsCount();
        
        float rollAngleOfanObj[] = aObject.getRollAngle();
        float aPointOfanObj[]    = null;
        
        //float X0, Y0, Z0, X, Y, Z;
        float X1, Y1, Z1, X2, Y2, Z2, Z3, rel_X, rel_Y, rel_Z;
        int x, y;
        
        rel_X = locationOfanObj[0] - cameraLocation[0];
        rel_Y = locationOfanObj[1] - cameraLocation[1];
        rel_Z = locationOfanObj[2] - cameraLocation[2];
        
        float r0 = rollAngleOfanObj[0];
        float r1 = rollAngleOfanObj[1];
        float r2 = rollAngleOfanObj[2];
        
        final float temp = GraphicUtils.tan(FOV/2.0F);
        float p1[], p2[], p3[];

        boolean absPoint = (aObject.getPointType() == PointType.abs? true: false);
        //   x r/v
        for(int i=0 ; i<pcount ;) //for each point
        {
            aPointOfanObj = (absPoint? aPointOfanObj = aObject.getAbsPoint(i): aObject.getPoint(i));
            
            XYLambdaI2 getPoint = (float X0, float Y0, float Z0, float relX, float relY, float relZ, boolean absP) -> {
                float X, Y, Z/*, tmp1, tmp2*/, cos$, sin$;
                if(!absP) {
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
                } else {
                    X0 -= cameraLocation[0];
                    Y0 -= cameraLocation[1];
                    Z0 -= cameraLocation[2];
                }
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
                
                if(Z0 >= 0)
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
                    
                    float result[] = {Y+XcenterI, X+YcenterI, Z0};
                    return result;
                }
                return notOnScreen;
            };
            
            //获取一个点在屏幕上的x,y坐标以及距离屏幕的深度z
            p1 = getPoint.run(aPointOfanObj[0], aPointOfanObj[1], aPointOfanObj[2], rel_X, rel_Y, rel_Z, absPoint);
            X1 = p1[0];
            Y1 = p1[1];
            Z1 = p1[2];
            
            switch(aObject.getDrawingMethod()) {
            case drawTriangleSurface:

                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
                Z2 = p2[2];
                p3 = getPoint.run(aPointOfanObj[6], aPointOfanObj[7], aPointOfanObj[8], rel_X, rel_Y, rel_Z, absPoint);
                Z3 = p3[2];
                
                suc = aObject.getSurfaceChar(i);

                if(Z1 < 0  ||  Z2 < 0  || Z3 < 0) {
                    ++i;
                    continue;
                }
                
                GraphicUtils.drawTriangleSurface_ZBuffer(fraps_buffer, zBuffer, p1, p2, p3, suc, staticOver);
                
                ++i;
                break;
            case drawLine:
                //获取二个点在屏幕上的x,y坐标以及距离屏幕的深度z
                p2 = getPoint.run(aPointOfanObj[3], aPointOfanObj[4], aPointOfanObj[5], rel_X, rel_Y, rel_Z, absPoint);
                X2 = p2[0];
                Y2 = p2[1];
                Z2 = p2[2];
                
                if(Z1 < 0  ||  Z2 < 0) {
                    ++i;
                    continue; 
                }
                GraphicUtils.drawLine(fraps_buffer, X1, Y1, X2, Y2, (spc =='\0'? pointChar : spc), staticOver);
                ++i;
                break;
            case drawPoint:
                x = (int)(X1 += 0.5F);
                y = (int)(Y1 += 0.5F);
                if(x>=0 && y>=0 && x<resolution[0] && y<resolution[1]) {
                    if(!staticOver  ||  fraps_buffer[y][x] == ' ')
                        fraps_buffer[y][x] = (spc =='\0'? pointChar : spc);
                }
                i += GraphicUtils.max(5 * (rge / visibility), 1);
                break;
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
		float[] run(float X0, float Y0, float Z0);
	}

    interface XYLambdaI2 {
        float[] run(float X0, float Y0, float Z0, float absX, float absY, float absZ, boolean absP);
    }
    
	protected float exposureObject(ThreeDs aObject, float cr0, float cr1, float cr2) {
		return exposureObject(aObject, cr0, cr1, cr2, false);
	}
	
	public Object exposure(Iterable<ThreeDs> objList)
	{
		for(ThreeDs aObject:objList) {
		    exposureObject(aObject, roll_angle[0], roll_angle[1], roll_angle[2]);
		}
		return null;
	}
	/*
	 * char    fraps_buffer[][],
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
	 */
	private char [][] blurFrame;
	/*
	private static char [] blurChars = {
        '.', '\'', '`', ',', '-', '~', ':', '"', ';', '^', 
        '=', '+', '*', 'o', 'O', 'Q', 'G', '0', '$', '@'
	};*/
	public Object exposureStatic()
	{
        if(inWorld.visualManager.enableMotionalBlur) {
            if(motionalBlur.size() < inWorld.visualManager.getMotionalBlurLevel()) {
                blurFrame = new char[resolution[1]][resolution[0]];
                /*for(int i=0 ; i<resolution[1] ; ++i) {
                    System.arraycopy(inWorld.visualManager.emptyLine, 0, blurFrame[i], 0, resolution[0]);
                }*/
                
            	for(Iterable<ThreeDs> aList:staticObjLists) { 
            		for(ThreeDs aObject:aList) {
            			//exposureObject(aObject, roll_angle[0], roll_angle[1], roll_angle[2], true);
            		    exposureAnObject(
            		        blurFrame, resolution, location, visibility, FOV, 
            	            XcenterI, YcenterI, 
            	            aObject, 
            	            roll_angle[0], roll_angle[1], roll_angle[2], 
            	            false, /*blurChars*//*inWorld.visualManager.point*/'.'
            		    );
            		}
            	}
    
                
                motionalBlur.add(blurFrame);
            }
        } else {
            for(Iterable<ThreeDs> aList:staticObjLists) { 
                for(ThreeDs aObject:aList) {
                    exposureObject(aObject, roll_angle[0], roll_angle[1], roll_angle[2], true);
                }
            }
            if(motionalBlur.size() < inWorld.visualManager.getMotionalBlurLevel())
                motionalBlur.add(fraps_buffer);
        }
        
        return null;
	}
	
	@Override
	public void run() {
		exposureStatic();
	}
	
	public Object exposure()
	{
		exposure(inWorld.objectsManager.objects);
		return null;
	}
	
	public void goStreet(float distance) {
		float z0, x0;
		
		z0 = GraphicUtils.cos(roll_angle[0]) * (GraphicUtils.cos(roll_angle[1]) * distance);
		x0 = GraphicUtils.sin(roll_angle[1]) * (GraphicUtils.cos(roll_angle[0]) * distance);
		location[2]+= z0;
		location[1]-= GraphicUtils.tan(roll_angle[0]) * z0;
		location[0]+= GraphicUtils.abs(roll_angle[0]) > GraphicUtils.halfAPI? -x0 : x0;
	}
	
	public void goLeft(float distance)
	{
		location[1]-=GraphicUtils.cos(roll_angle[0]) * distance;
		location[2]-=GraphicUtils.sin(roll_angle[0]) * distance;
		location[0]-=GraphicUtils.sin(roll_angle[2]) * distance;
	}
	
	public void goBack(float distance)
	{
		float z0, x0;
		
		z0 = GraphicUtils.cos(roll_angle[0]) * (GraphicUtils.cos(roll_angle[1]) * distance);
		x0 = GraphicUtils.sin(roll_angle[1]) * (GraphicUtils.cos(roll_angle[0]) * distance);
		location[2]-= z0;
		location[1]+= GraphicUtils.tan(roll_angle[0]) * z0;
		location[0]-= GraphicUtils.abs(roll_angle[0]) > GraphicUtils.halfAPI? -x0 : x0;
	}
	
	public void goRight(float distance)
	{
		location[1]+=GraphicUtils.cos(roll_angle[0]) * distance;
		location[2]+=GraphicUtils.sin(roll_angle[0]) * distance;
		location[0]+=GraphicUtils.sin(roll_angle[2]) * distance;
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



