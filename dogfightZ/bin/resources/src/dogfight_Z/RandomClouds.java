package dogfight_Z;

import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharObject;

public class RandomClouds extends CharObject implements Runnable, ThreeDs
{
	double size[];
	double maxDensity;
	double visibility;
	double hight;
	double location_player[];
	
	public RandomClouds
	(	//		hights
		double Location[], double Visibility, double Hight,
		double size_X, double size_Y, double size_Z,
		double max_density
	)
	{
		super((String)null);
		
		visibility = Visibility;
		hight = Hight;
		
		size = new double[3];
		size[0] = size_X;
		size[1] = size_Y;
		size[2] = size_Z;

		maxDensity = max_density;
		
		location_player = Location;
		location = new double[3];
		location[0] = Math.random();
		if((int)(location[0] * 1000000) % 2 == 0)
			location[0] = -location[0];
		location[1] = Math.random();
		if((int)(location[1] * 1000000) % 2 == 0)
			location[1] = -location[1];
		location[2] = Math.random();
		if((int)(location[2] * 1000000) % 2 == 0)
			location[2] = -location[2];
		
		location[0] = /*location_player[0] < hight - visibility? hight + location_player[0] - location[0] * 2 * size[1] :*/ hight - location[0] * 2 * size[1];
		location[1] = location[1] * visibility + location_player[1];
		location[2] = location[2] * visibility + location_player[2];
		
		roll_angle[0] = roll_angle[1] = roll_angle[2] = 0.0;
		
		int count = (int)(size_X * size_Y * size_Z * max_density)/100000;
		
		double newPonit[];
		
		for(points_count=0 ; points_count<count ; ++points_count)
		{
			newPonit = new double[3];
			
			newPonit[0] = Math.random() * size_X;
			newPonit[1] = Math.random() * size_Y;
			newPonit[2] = Math.random() * size_Z;
			
			points.add(newPonit);
		}
		
		visible = true;
	}
	
	public RandomClouds
	(
		double Location[], double Visibility,
		double Hight, double max_density
	)
	{
		this
		(
			Location, Visibility, Hight,
			Math.random() * 200, 
			Math.random() * 1000, 
			Math.random() * 1000,
			max_density
		);
	}
	
	public RandomClouds
	(
		double Location[], double Visibility, double Hight
	)	{this(Location, Visibility, Hight, Math.random() * 1000);}
	
	public void reConstruct()
	{
		location[0] = Math.random();
		if((int)(location[0] * 1000000) % 2 == 0)
			location[0] = -location[0];
		location[1] = Math.random();
		if((int)(location[1] * 1000000) % 2 == 0)
			location[1] = -location[1];
		location[2] = Math.random();
		if((int)(location[2] * 1000000) % 2 == 0)
			location[2] = -location[2];
		
		location[0] = /*location_player[0] < hight - visibility? hight + location_player[0] - location[0] * 2 * size[1] :*/ hight - location[0] * 2 * size[1];
		location[1] = location[1] * visibility + location_player[1];
		location[2] = location[2] * visibility + location_player[2];
		
		for(int i=0 ; i<points_count ; ++i)
		{
			points.get(i)[0] = Math.random() * size[0];
			points.get(i)[1] = Math.random() * size[1];
			points.get(i)[2] = Math.random() * size[2];
		}
	}

	@Override
	public void run()
	{
		reConstruct();
	}
}

