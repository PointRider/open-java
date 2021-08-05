package graphic_Z.Managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JTextArea;

import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharHUD;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharLoopingScrollBar;
import graphic_Z.HUDs.CharProgressBar;
import graphic_Z.HUDs.HUD;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharWorld;

public class CharVisualManager extends VisualManager<CharWorld> implements Runnable
{
	public		char	point[];			//点样式
	protected	char	blank;				//空白样式
	public		char	fraps_buffer[][];			//帧缓冲，实体
	protected	List<CharFrapsCamera> cameras;
	protected	JTextArea	mainScr;		//在主屏幕引用
	public		List<Iterable<ThreeDs>> staticObjLists;
	public		List<Iterable<Dynamic>> dynamicObjLists;
	public		List<PriorityQueue<Dynamic>> selfDisposable;
	public		Object	mainCameraFeedBack;
	private StringBuilder scr_show;
	private Thread tmpThread;
	//private Thread staticObjExposureThread;
	
	public CharVisualManager(short resolution_X, short resolution_Y, CharWorld inWhichWorld, JTextArea	main_scr)
	{
		super(resolution_X, resolution_Y, inWhichWorld);
		
		mainCameraFeedBack = null;
		scr_show = new StringBuilder(resolution_X * resolution_Y);
		refreshDelay = inWorld.refreshDelay;
		staticObjLists	= inWorld.objectsManager.staticObjLists;
		dynamicObjLists	= inWorld.objectsManager.dynamicObjLists;
		selfDisposable  = inWorld.objectsManager.selfDisposable;
		//point = '*';					//default
		point = new char[8];
		point[0] = '@';
		point[1] = '0';
		point[2] = 'O';
		point[3] = 'o';
		point[4] = '*';
		point[5] = '+';
		point[6] = '\'';
		point[7] = '.';
		
		blank = ' ';					//default
		
		fraps_buffer = new char[resolution_Y][];
		
		for(short i=0 ; i<resolution_Y ; ++i)
			fraps_buffer[i] = new char[resolution_X];
		
		cameras = new ArrayList<CharFrapsCamera>();

		mainScr = main_scr;
	}
	
	public void newCamera()
	{
		CharFrapsCamera newCamera = new CharFrapsCamera(1.0, 1000.0, resolution, fraps_buffer, inWorld, staticObjLists);
		cameras.add(newCamera);
		//staticObjExposureThread = new Thread(newCamera);
	}
	
	public CharFrapsCamera newCamera(double FOV)
	{
		CharFrapsCamera newCma = new CharFrapsCamera(FOV, 1000.0, resolution, fraps_buffer, inWorld, staticObjLists);
		cameras.add(newCma);
		//staticObjExposureThread = new Thread(newCma);
		return newCma;
	}
	
	public CharFrapsCamera newCamera(double FOV, double visibility)
	{
		CharFrapsCamera newCma = new CharFrapsCamera(FOV, visibility, resolution, fraps_buffer, inWorld, staticObjLists);
		cameras.add(newCma);
		//staticObjExposureThread = new Thread(newCma);
		return newCma;
	}
	
	public CharFrapsCamera newCamera(CharFrapsCamera newCma)
	{
		cameras.add(newCma);
		//staticObjExposureThread = new Thread(newCma);
		return newCma;
	}
	
	public CharHUD newHUD(String HUDImgFile, short HUDLayer, boolean transparentAtSpace)
	{
		CharHUD newHud = new CharHUD(HUDImgFile, fraps_buffer, HUDLayer, resolution, transparentAtSpace);
		HUDs.add(newHud);
		return newHud;
	}
	
	public CharHUD newHUD(String HUDImgFile, short HUDLayer)
	{
		return newHUD(HUDImgFile, HUDLayer, true);
	}
	
	public CharDynamicHUD newDynamicHUD(String HUDImgFile, short HUDLayer, short size_X, short size_Y)
	{
		CharDynamicHUD newHud = new CharDynamicHUD
		(
			HUDImgFile, fraps_buffer, 
			HUDLayer, resolution, 
			size_X, size_Y, 
			(short)(resolution[0]/2), (short)(resolution[1]/2)
		);
		
		HUDs.add(newHud);
		return newHud;
	}
	
	public void newDynamicHUD(CharDynamicHUD newHud)
	{
		HUDs.add(newHud);
	}
	
	public CharLoopingScrollBar newLoopingScrollBar
	(
		String HUDImgFile, short HUDLayer, 
		short size_X, short size_Y,
		short size_Show, CharLoopingScrollBar.Direction direction
	)
	{
		CharLoopingScrollBar newHud = new CharLoopingScrollBar
		(
			HUDImgFile, fraps_buffer, 
			HUDLayer, resolution, 
			size_X, size_Y, 
			(short)(resolution[0]/2), (short)(resolution[1]/2),
			direction, (short)0, size_Show
		);
		
		HUDs.add(newHud);
		return newHud;
	}
	
	public CharLabel newLabel(String Text, short location_X,  short location_Y, short HUDLayer)
	{
		CharLabel newLbl = 
			new CharLabel(fraps_buffer, HUDLayer, resolution, Text, location_X, location_Y);
		HUDs.add(newLbl);
		return newLbl;
	}
	
	public void newLabel(CharLabel newLbl)
	{
		HUDs.add(newLbl);
	}
	
	public CharProgressBar newProgressBar
	(
		short	location_X,  
		short	location_Y, 
		short	HUDLayer,
		short	size,
		char	visual,
		CharProgressBar.Direction direction,
		double	value
	)
	{
		CharProgressBar newBar = 
			new CharProgressBar(fraps_buffer, HUDLayer, resolution, location_X, location_Y, size, visual, direction, value);
		HUDs.add(newBar);
		return newBar;
	}
	
	public CharProgressBar newProgressBar
	(
		short	location_X,  
		short	location_Y, 
		short	HUDLayer,
		short	size,
		char	visual,
		CharProgressBar.Direction direction
	)
	{
		CharProgressBar newBar = 
			new CharProgressBar(fraps_buffer, HUDLayer, resolution, location_X, location_Y, size, visual, direction);
		HUDs.add(newBar);
		return newBar;
	}
	
	public void run() { 
		try{Thread.sleep(refreshDelay);} catch(InterruptedException e) {}
	}
	
	public void srun()
	{
		for(int i=0 ; i<resolution[1] ; ++i) for(int j=0 ; j<resolution[0] ; ++j) {
			fraps_buffer[i][j] = blank;
		}
		
		
		for(CharFrapsCamera aCamera : cameras)
		{
			Thread staticObjExposureThread = new Thread(aCamera);
			staticObjExposureThread.start();

			for(Iterable<Dynamic> eachList:dynamicObjLists)
				aCamera.exposure(eachList, 0);
			for(Iterable<Dynamic> eachList:selfDisposable)
				aCamera.exposure(eachList, 0);
			mainCameraFeedBack = aCamera.exposure();
		}
		for
		(
			Iterator<HUD> iter = HUDs.iterator();
			iter.hasNext();
			iter.next().printNew()
		);
	}
	
	public void printNew()	//关于颜色、多摄像机的改进待做(包括裸眼3D)
	{
		tmpThread = new Thread(this);
		tmpThread.start();
		
		srun();

		try{tmpThread.join();} catch (InterruptedException e) {}
		boolean firstInLine, firstLine;
		
		scr_show.delete(0, scr_show.length());
		firstLine = true;
		
		for(char y[]:fraps_buffer)
		{
			firstInLine = true;
			if(!firstLine) scr_show.append('\n');
			
			for(char x:y)
			{
				if(!firstInLine) scr_show.append(' ');
				scr_show.append(x);
				firstInLine = false;
			}
			
			firstLine = false;
		}

		mainScr.setText(scr_show.toString());
	}

}
