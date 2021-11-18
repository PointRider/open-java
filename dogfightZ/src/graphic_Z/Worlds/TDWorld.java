package graphic_Z.Worlds;

import graphic_Z.Managers.EventManager;
import graphic_Z.Managers.TDObjectsManager;
import graphic_Z.Managers.VisualManager;

public abstract class TDWorld<WorldType, ObjectType, HUDType>
{
	public TDObjectsManager         objectsManager;
	public VisualManager<WorldType> visualManager;
	public EventManager             eventManager;
	
	public int refreshHz;
	
	public TDWorld(int refresh_rate)
	{
		refreshHz = refresh_rate;
	}
	
	public void getIntoTheWorld()
	{
		eventManager.setVisible(true);
	}
	
	public void exitTheWorld()
	{
		eventManager.setVisible(false);
	}
	
	public abstract void setRefreshRate(int refresh_rate);
	public abstract void printNew();
}
