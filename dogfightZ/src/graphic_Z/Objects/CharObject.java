package graphic_Z.Objects;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import graphic_Z.Interfaces.ThreeDs;

public class CharObject extends TDObject implements ThreeDs
{
	public char specialDisplay;
	public int points_count;			//物体点个数
	public List<double[]> points;		//物体每个点坐标
	private boolean lineConstruct;
	
	/*
	public CharObject(CharObject another)
	{
		super(another);

		points_count	= another.points_count;
		location 		= another.location.clone();
		
		points = new ArrayList<double[]>();
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
		this.lineConstruct = lineConstruct;
		specialDisplay = '\0';
		points = new ArrayList<double[]>();
		location[0]   = location[1]   = location[2]   = 0.0;
		roll_angle[0] = roll_angle[1] = roll_angle[2] = 0.0;
		
		if(ModelFile != null) try (
			DataInputStream data = new DataInputStream
			(new FileInputStream(ModelFile))
		) {
			double newPonit[] = null;
			for(points_count=0 ; true ; ++points_count)
			{
				if(lineConstruct) newPonit = new double[6];
				else newPonit = new double[3];
				
				newPonit[0] = data.readDouble();
				newPonit[1] = data.readDouble();
				newPonit[2] = data.readDouble();
				
				if(lineConstruct) {
					newPonit[3] = data.readDouble();
					newPonit[4] = data.readDouble();
					newPonit[5] = data.readDouble();
				}
				
				points.add(newPonit);
			}
		} catch(Exception exc) {}
	}
	
	public CharObject(String ModelFile) {
		this(ModelFile, false);
	}

	public void setLocation(double x, double y, double z)
	{
		location[0] = x;
		location[1] = y;
		location[2] = z;
	}
	
	public void setRollAngle(double x, double y, double z)
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
	public double[] getLocation()
	{
		// TODO 自动生成的方法存根
		return location;
	}

	@Override
	public double[] getRollAngle()
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
	public double[] getPoint(int index)
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
}
