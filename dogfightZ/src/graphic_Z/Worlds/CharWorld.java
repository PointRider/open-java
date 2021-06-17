package graphic_Z.Worlds;

import graphic_Z.HUDs.CharHUD;
import graphic_Z.Managers.CharObjectsManager;
import graphic_Z.Managers.CharVisualManager;
import graphic_Z.Managers.EventManager;
import graphic_Z.Objects.CharObject;

public class CharWorld extends TDWorld<CharWorld, CharObject, CharHUD>
{
	public CharObjectsManager	objectsManager;
	public CharVisualManager	visualManager;
	
	public CharWorld(short resolution_X, short resolution_Y)
	{
		super(60);				//default
		
		eventManager = new EventManager();
		objectsManager = new CharObjectsManager();
		visualManager = new CharVisualManager(resolution_X, resolution_Y, this, eventManager.mainScr);

	}
	
	public CharWorld(short resolution_X, short resolution_Y, int refresh_rate)
	{
		super(refresh_rate);
		eventManager = new EventManager();
		objectsManager = new CharObjectsManager();
		visualManager = new CharVisualManager(resolution_X, resolution_Y, this, eventManager.mainScr);

	}
	
	public void setRefreshRate(int refresh_rate)
	{
		refreshDelay = 1000 / refresh_rate;
		visualManager.refreshDelay = refreshDelay;
	}
	
	public void printNew()
	{
		visualManager.printNew();
	}
	
}
