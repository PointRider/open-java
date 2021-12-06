package graphic_Z.Worlds;

import graphic_Z.HUDs.CharHUD;
import graphic_Z.Managers.CharObjectsManager;
import graphic_Z.Managers.CharVisualManager;
import graphic_Z.Managers.EventManager;
import graphic_Z.Objects.CharObject;

public class CharWorld extends TDWorld<CharWorld, CharObject, CharHUD>
{
	public CharObjectsManager objectsManager;
	public CharVisualManager  visualManager;
	
	public CharWorld(int resolution_X, int resolution_Y)
	{
		super(64);				//default
		
		eventManager = new EventManager();
		objectsManager = new CharObjectsManager(this);
		visualManager = new CharVisualManager(resolution_X, resolution_Y, this, eventManager.mainScr);

	}
	
	public CharWorld(int resolution_X, int resolution_Y, int refresh_rate)
	{
		super(refresh_rate);
		eventManager = new EventManager();
		objectsManager = new CharObjectsManager(this);
		visualManager = new CharVisualManager(resolution_X, resolution_Y, this, eventManager.mainScr);

	}
	
	public void setRefreshRate(int refresh_rate)
	{
		refreshHz = 1000 / refresh_rate;
		visualManager.refreshHz = refresh_rate;
	}
	
	public void printNew()
	{
		visualManager.printNew();
	}
	
}
