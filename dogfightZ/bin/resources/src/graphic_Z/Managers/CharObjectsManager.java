package graphic_Z.Managers;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharMessObject;
import graphic_Z.Objects.CharObject;

public class CharObjectsManager extends TDObjectsManager
{
	public HashSet<ThreeDs> objects;
	public List<Iterable<ThreeDs>> staticObjLists;
	public List<Iterable<Dynamic>> dynamicObjLists;
	public List<PriorityQueue<Dynamic>> selfDisposable;
	
	public CharObjectsManager()
	{
		count = 0;
		objects = new HashSet<ThreeDs>();
		staticObjLists = new ArrayList<Iterable<ThreeDs>>();
		dynamicObjLists= new ArrayList<Iterable<Dynamic>>();
		selfDisposable= new ArrayList<PriorityQueue<Dynamic>>();
	}
	
	public Iterable<ThreeDs> newStaticObjectList(Iterable<ThreeDs> objLst)
	{
		staticObjLists.add(objLst);
		return objLst;
	}
	
	public Iterable<Dynamic> newDynamicObjectList(Iterable<Dynamic> objLst)
	{
		dynamicObjLists.add(objLst);
		return objLst;
	}
	
	public PriorityQueue<Dynamic> newSelfDisposableObjList(PriorityQueue<Dynamic> objLst)
	{
		selfDisposable.add(objLst);
		return objLst;
	}
	
	public CharObject newObject(ThreeDs newObj)
	{
		objects.add(newObj);
		return (CharObject) newObj;
	}
	
	public CharObject newObject(String modelFile)
	{
		return newObject(new CharObject(modelFile));
	}
	
	public CharMessObject newMessObject(CharMessObject newObj)
	{
		objects.add(newObj);
		return newObj;
	}
	
	public CharMessObject newMessObject(String modelFile, double Mess)
	{
		return newMessObject(new CharMessObject(modelFile, Mess));
	}
	
	public void remove(ThreeDs obj)
	{
		objects.remove(obj);
	}

	public void printNew()
	{
		// TODO 自动生成的方法存根
		for(Iterable<Dynamic> eachList:dynamicObjLists)
			for(Dynamic aObj:eachList)
				aObj.go();
		
		for(PriorityQueue<Dynamic> eachList:selfDisposable)
			for(Dynamic aObj:eachList)
				aObj.go();
		
		for(ThreeDs aObj:objects)
			aObj.go();
		
		
	}
	
}
