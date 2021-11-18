package graphic_Z.Managers;

import java.util.ListIterator;

import graphic_Z.Interfaces.ThreeDs;

public abstract class TDObjectsManager
{
	protected int count;
	
	public TDObjectsManager()
	{
		count = 0;
	}
	
	public abstract ListIterator<ThreeDs> newObject(String modelFile);
	
	//public abstract TDObject Object(int index);
	//public abstract TDObject get(int index);
	//public abstract void set(int index, TDObject obj);
	//public abstract void remove(int index);
}
