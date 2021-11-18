package graphic_Z.Interfaces;

import java.util.ListIterator;

public interface ThreeDs extends Runnable
{
	public boolean  constructWithLine();
	public double[] getLocation();
	public double[] getRollAngle();
	public boolean  getVisible();
	public int		getPointsCount();
	public double[] getPoint(int index);
	public void 	go();
	public char		getSpecialDisplayChar();
	public void     setIterator(ListIterator<ThreeDs> itr);
}
