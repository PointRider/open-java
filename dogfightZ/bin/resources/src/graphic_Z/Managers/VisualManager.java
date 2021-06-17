package graphic_Z.Managers;

import java.util.TreeSet;

import graphic_Z.Cameras.TDCamera;
import graphic_Z.HUDs.HUD;

public abstract class VisualManager<WorldType>
{
	public short resolution[];			//分辨率(x,y)
	protected TreeSet<HUD> HUDs;
	protected WorldType inWorld;		//视觉所在世界
	public int refreshDelay;
	
	public abstract void printNew();
	
	public VisualManager(short resolution_X, short resolution_Y, WorldType inWhichWorld)
	{
		resolution	  = new short[2];
		resolution[0] = resolution_X;
		resolution[1] = resolution_Y;
		HUDs		  = new TreeSet<HUD>();
		inWorld		  = inWhichWorld;
	}
	
	public short getResolution_X()
	{
		return resolution[0];
	}
	
	public short getResolution_Y()
	{
		return resolution[1];
	}
	
	@SuppressWarnings("rawtypes")
	public abstract TDCamera newCamera(double FOV);
	public abstract HUD newHUD(String HUDImgFile, short HUDLayer);
}