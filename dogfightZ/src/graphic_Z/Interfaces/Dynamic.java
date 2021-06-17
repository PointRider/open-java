package graphic_Z.Interfaces;

public interface Dynamic extends ThreeDs, Comparable<Dynamic>
{
	public void go();
	public boolean deleted();
	public long   getLife();
}
