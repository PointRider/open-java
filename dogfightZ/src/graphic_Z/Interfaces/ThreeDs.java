package graphic_Z.Interfaces;

import java.util.ListIterator;

public interface ThreeDs extends Runnable
{
    public static enum DrawingMethod {drawPoint, drawLine, drawTriangleSurface};
    
    DrawingMethod getDrawingMethod();
	float[] getLocation();
	float[] getRollAngle();
	boolean getVisible();
    int	    getPointsCount();
	float[] getPoint(int index);
	void    go();
    char	getSpecialDisplayChar();
	void    setIterator(ListIterator<ThreeDs> itr);
	char    getSurfaceChar(int index);
}
