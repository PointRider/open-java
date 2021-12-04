package graphic_Z.Interfaces;

import java.util.ListIterator;

public interface ThreeDs extends Runnable
{
	boolean constructWithLine();
	float[] getLocation();
	float[] getRollAngle();
	boolean getVisible();
    int	    getPointsCount();
	float[] getPoint(int index);
	void    go();
    char	getSpecialDisplayChar();
	void    setIterator(ListIterator<ThreeDs> itr);
}
