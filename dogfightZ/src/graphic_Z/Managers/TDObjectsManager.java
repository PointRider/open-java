package graphic_Z.Managers;

import graphic_Z.Objects.TDObject;

public abstract class TDObjectsManager
{
	protected int count;
	
	public TDObjectsManager()
	{
		count = 0;
	}
	
	public abstract TDObject newObject(String modelFile);
	
	//public abstract TDObject Object(int index);
	//public abstract TDObject get(int index);
	//public abstract void set(int index, TDObject obj);
	//public abstract void remove(int index);
}
