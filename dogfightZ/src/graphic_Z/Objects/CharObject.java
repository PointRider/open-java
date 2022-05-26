package graphic_Z.Objects;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import graphic_Z.Interfaces.ThreeDs;

public abstract class CharObject extends TDObject implements ThreeDs
{
	public char specialDisplay;
	public int points_count;			//物体点个数
	public List<float[]> points;		//物体每个点坐标
    public List<float[]> points_abs;        //物体每个点坐标
	private DrawingMethod drawingMethod;
	protected ListIterator<ThreeDs> myPosition;
	protected char surfaceChar[];
	/*
	public CharObject(CharObject another)
	{
		super(another);

		points_count	= another.points_count;
		location 		= another.location.clone();
		
		points = new ArrayList<float[]>();
		for(int x=0 ; x<another.points.size() ; ++x)
			points.add(x, another.points.get(x).clone());
	}
	*/
	/*
	public CharObject clone()
	{
		return new CharObject(this);
	}
	*/
	
	
	public CharObject(String ModelFile, DrawingMethod drawingMethod)
	{
		super();
		myPosition = null;
		this.drawingMethod = drawingMethod;
		specialDisplay = '\0';
		points = new ArrayList<float[]>();
		location[0]   = location[1]   = location[2]   = 0.0F;
		roll_angle[0] = roll_angle[1] = roll_angle[2] = 0.0F;
		
		if(ModelFile != null) try (
			DataInputStream data = new DataInputStream
			(new FileInputStream(ModelFile))
		) {
			float newPonit[] = null;
			for(points_count=0 ; true ; ++points_count)
			{
				if(drawingMethod == DrawingMethod.drawLine) newPonit = new float[6];
				else newPonit = new float[3];
				
				switch(drawingMethod) {
        			case drawPoint:           newPonit = new float[3]; break;
        			case drawLine:            newPonit = new float[6]; break;
        			case drawTriangleSurface: newPonit = new float[9]; break;
				}
				
				newPonit[0] = (float) data.readDouble();
				newPonit[1] = (float) data.readDouble();
				newPonit[2] = (float) data.readDouble();
				
				if(drawingMethod == DrawingMethod.drawLine || drawingMethod == DrawingMethod.drawTriangleSurface) {
					newPonit[3] = (float) data.readDouble();
					newPonit[4] = (float) data.readDouble();
					newPonit[5] = (float) data.readDouble();
				}
				
				if(drawingMethod == DrawingMethod.drawTriangleSurface) {
                    newPonit[6] = (float) data.readDouble();
                    newPonit[7] = (float) data.readDouble();
                    newPonit[8] = (float) data.readDouble();
                }
				
				points.add(newPonit);
			}
		} catch(Exception exc) {}
	}
	
	public CharObject(String ModelFile) {
		this(ModelFile, DrawingMethod.drawPoint);
	}

	public void setLocation(float x, float y, float z)
	{
		location[0] = x;
		location[1] = y;
		location[2] = z;
	}
	
	public void setRollAngle(float x, float y, float z)
	{
		roll_angle[0] = x;
		roll_angle[1] = y;
		roll_angle[2] = z;
	}
	
	@Override
	public void go()
	{
	}

	@Override
	public final float[] getLocation() {
		return location;
	}

	@Override
	public final float[] getRollAngle() {
		return roll_angle;
	}

	@Override
	public final boolean getVisible() {
		return visible;
	}

	@Override
	public final int getPointsCount() {
		return points_count;
	}

	@Override
	public final float[] getPoint(int index) {
	    if(points == null) return null;
		return points.get(index);
	}

	@Override
	public void run()
	{}

	@Override
	public char getSpecialDisplayChar() {
		return specialDisplay;
	}

	@Override
	public DrawingMethod getDrawingMethod() {
		return drawingMethod;
	}

	@Override
	public void setIterator(ListIterator<ThreeDs> itr) {
		myPosition = itr;
	}

    @Override
    public final char getSurfaceChar(int index) {
        if(surfaceChar != null) return surfaceChar[index];
        return '\0';
    }

    @Override
    public float[] getAbsPoint(int index) {
        return points_abs.get(index);
    }
}
