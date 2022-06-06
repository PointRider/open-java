package graphic_Z.Managers;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharMessObject;
//import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.LinkedListZ;

public class CharObjectsManager extends TDObjectsManager
{
    public LinkedListZ<ThreeDs> objects;
	public List<Iterable<ThreeDs>> staticObjLists;
	//public List<Iterable<Dynamic>> dynamicObjLists;
	public List<PriorityQueue<Dynamic>> selfDisposable;
	
	//private int cpuCount;
	//private CharWorld inWorld;
	
	public CharObjectsManager(/*CharWorld inWorld*/)
	{
	    //this.inWorld    = inWorld;
		count           = 0;
		//cpuCount        = Runtime.getRuntime().availableProcessors();
		objects         = new LinkedListZ<ThreeDs>();
		staticObjLists  = new ArrayList<Iterable<ThreeDs>>();
		//dynamicObjLists = new ArrayList<Iterable<Dynamic>>();
		selfDisposable  = new ArrayList<PriorityQueue<Dynamic>>();
	}
	
	public Iterable<ThreeDs> newStaticObjectList(Iterable<ThreeDs> objLst)
	{
		staticObjLists.add(objLst);
		return objLst;
	}
	/*
	public Iterable<Dynamic> newDynamicObjectList(Iterable<Dynamic> objLst)
	{
		dynamicObjLists.add(objLst);
		return objLst;
	}
	*/
	public PriorityQueue<Dynamic> newSelfDisposableObjList(PriorityQueue<Dynamic> objLst)
	{
		selfDisposable.add(objLst);
		return objLst;
	}
	
	public ListIterator<ThreeDs> newObject(ThreeDs newObj)
	{
		return objects.pushBack(newObj);
	}
	/*
	public ListIterator<ThreeDs> newObject(String modelFile)
	{
		return newObject(new CharObject(modelFile));
	}
	*/
	public CharMessObject newMessObject(CharMessObject newObj)
	{
		objects.add(newObj);
		return newObj;
	}
	/*
	public CharMessObject newMessObject(String modelFile, float Mess)
	{
		return newMessObject(new CharMessObject(modelFile, Mess));
	}
	*/
	public void remove(ThreeDs obj)
	{
		objects.remove(obj);
	}
	
	public void printNew() {
	    /*
		for(Iterable<Dynamic> eachList:dynamicObjLists)
			for(Dynamic aObj:eachList) //aObj.go();
			    inWorld.execute(aObj);
		*/
	    
		for(PriorityQueue<Dynamic> eachList:selfDisposable) {
			for(Dynamic aObj:eachList) {
				aObj.go();
			}
		}
		
		for(ThreeDs aObj:objects) {
		    //epool.execute(aObj);//
		    aObj.go();
		}
	}
	
}
