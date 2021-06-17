package graphic_Z.HUDs;

public abstract class HUD implements Comparable<HUD>
{
	public short resolution[];
	protected short layer;				//layer越大，层越浅
	public	  boolean visible;
	
	public abstract void printNew();
	public abstract void printNew(short reslution_[], char	frapsBuffer[][]);
	
	public int compareTo(HUD another)
	{
		if(layer == another.layer)
			return 0;
		else if(layer > another.layer)
			return 1;
		else return -1;
	}
}