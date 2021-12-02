package graphic_Z.Objects;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import graphic_Z.Interfaces.ThreeDs;

public class CharObject extends TDObject implements ThreeDs
{
	public char specialDisplay;
	public int points_count;			//物体点个数
	public List<float[]> points;		//物体每个点坐标
	private boolean lineConstruct;
	protected ListIterator<ThreeDs> myPosition;
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
	
	public CharObject(String ModelFile, boolean lineConstruct)
	{
		super();
		myPosition = null;
		this.lineConstruct = lineConstruct;
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
				if(lineConstruct) newPonit = new float[6];
				else newPonit = new float[3];
				
				newPonit[0] = (float) data.readDouble();
				newPonit[1] = (float) data.readDouble();
				newPonit[2] = (float) data.readDouble();
				
				if(lineConstruct) {
					newPonit[3] = (float) data.readDouble();
					newPonit[4] = (float) data.readDouble();
					newPonit[5] = (float) data.readDouble();
				}
				
				points.add(newPonit);
			}
		} catch(Exception exc) {}
	}
	
	public CharObject(String ModelFile) {
		this(ModelFile, false);
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
	public float[] getLocation()
	{
		// TODO 自动生成的方法存根
		return location;
	}

	@Override
	public float[] getRollAngle()
	{
		// TODO 自动生成的方法存根
		return roll_angle;
	}

	@Override
	public boolean getVisible()
	{
		// TODO 自动生成的方法存根
		return visible;
	}

	@Override
	public int getPointsCount()
	{
		// TODO 自动生成的方法存根
		return points_count;
	}

	@Override
	public float[] getPoint(int index)
	{
		// TODO 自动生成的方法存根
		return points.get(index);
	}

	@Override
	public void run()
	{
		// TODO 自动生成的方法存根
	}

	@Override
	public char getSpecialDisplayChar() {
		return specialDisplay;
	}

	@Override
	public boolean constructWithLine() {
		return lineConstruct;
	}

	@Override
	public void setIterator(ListIterator<ThreeDs> itr) {
		myPosition = itr;
	}
}
