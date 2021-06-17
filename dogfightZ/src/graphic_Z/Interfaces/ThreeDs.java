package graphic_Z.Interfaces;

public interface ThreeDs extends Runnable
{
	public double[] getLocation();
	public double[] getRollAngle();
	public boolean  getVisible();
	public int		getPointsCount();
	public double[] getPoint(int index);
	public void 	go();
	public char		getSpecialDisplayChar();
}
