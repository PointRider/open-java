package graphic_Z.Managers;

import java.util.TreeSet;

import graphic_Z.Cameras.TDCamera;
import graphic_Z.HUDs.HUD;
import graphic_Z.utils.HzController;

public abstract class VisualManager<WorldType>
{
	public int resolution[];			//分辨率(x,y)
	protected TreeSet<HUD> HUDs;
	protected WorldType inWorld;		//视觉所在世界
	public int refreshHz;
	protected HzController hzController;
	
	public abstract String printNew();
	
	public VisualManager(int resolution_X, int resolution_Y, WorldType inWhichWorld)
	{
		resolution	  = new int[2];
		resolution[0] = resolution_X;
		resolution[1] = resolution_Y;
		HUDs		  = new TreeSet<HUD>();
		inWorld		  = inWhichWorld;
	}
	
	public int getResolution_X()
	{
		return resolution[0];
	}
	
	public int getResolution_Y()
	{
		return resolution[1];
	}
	
	@SuppressWarnings("rawtypes")
	public abstract TDCamera newCamera(float FOV);
	public abstract HUD newHUD(String HUDImgFile, int HUDLayer);
}