package dogfight_Z;

import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharObject;

public class RandomClouds extends CharObject implements Runnable, ThreeDs
{
	double size[];
	double density;
	double visibility;
	double hight;
	double location_player[];
	/*
	public RandomClouds (  
		double Location[], double Visibility, double Hight,
		double size_X, double size_Y, double size_Z, double max_density
	) {
		super(null, true);
		System.out.println("test");
		visibility = Visibility;
		hight = Hight;
		
		size = new double[3];
		size[0] = size_X;
		size[1] = size_Y;
		size[2] = size_Z;
		size_Y = size_Y / 2.0;
		location_player = Location;
		location = new double[3];
		
		location[0] = Math.random();
		if(((int)(location[0] * 1000000) & 1) == 0)
			location[0] = -location[0];
		location[1] = Math.random();
		if(((int)(location[1] * 1000000) & 1) == 0)
			location[1] = -location[1];
		location[2] = Math.random();
		if(((int)(location[2] * 1000000) & 1) == 0)
			location[2] = -location[2];
		
		location[0] = hight - location[0] * 2 * size[1];
		location[1] = location[1] * visibility + location_player[1];
		location[2] = location[2] * visibility + location_player[2];
		
		roll_angle[0] = 360 * Math.random();
		roll_angle[1] = 360 * Math.random();
		roll_angle[2] = 360 * Math.random();
		
		density = max_density * Math.random();
		
		//int count = (int)(size_X * size_Y * size_Z * max_density) >> 16;
		//
		//double newPonit[];
		//
		//for(points_count=0 ; points_count<count ; ++points_count)
		//{
		//	newPonit = new double[3];
		//	
		//	newPonit[0] = Math.random() * size_X;
		//	newPonit[1] = Math.random() * size_Y;
		//	newPonit[2] = Math.random() * size_Z;
		//	
		//	points.add(newPonit);
		//}
		

		int count = (int)(size_Z * density);
		
		double y0;

		double newLine[];
		max_density = 1.0 / density;
		for(double x = 1.0; count > 0  &&  x < size_X; x += density * size_X / 10) {
			for(double z = 1.0; count > 0  &&  z < size_Z; z += density * size_Z / 10) {
				
				y0 = size_Y * Math.random();
				newLine = new double[6];
				newLine[0] = newLine[3] = x;
				newLine[1] = y0;
				newLine[4] = y0 + size_Y * Math.random();
				newLine[5] = newLine[2] = z;
				
				points.add(newLine);
				--count;
				++points_count;
			}
		}

		visible = true;
	}*/
	
	public RandomClouds
	(	//		hights
		double Location[], double Visibility, double Hight,
		double size_X, double size_Y, double size_Z,
		double max_density
	)
	{
		super(null);

		System.out.println("test");
		visibility = Visibility;
		hight = Hight;
		
		size = new double[3];
		size[0] = size_X;
		size[1] = size_Y;
		size[2] = size_Z;

		density = max_density;
		
		location_player = Location;
		location = new double[3];
		
		location[0] = Math.random();
		if(((int)(location[0] * 1000000) & 1) == 0)
			location[0] = -location[0];
		location[1] = Math.random();
		if(((int)(location[1] * 1000000) & 1) == 0)
			location[1] = -location[1];
		location[2] = Math.random();
		if(((int)(location[2] * 1000000) & 1) == 0)
			location[2] = -location[2];
		
		location[0] = hight - location[0] * 2 * size[1];
		location[1] = location[1] * visibility + location_player[1];
		location[2] = location[2] * visibility + location_player[2];
		
		roll_angle[0] = roll_angle[1] = roll_angle[2] = 0.0;
		
		int count = (int)(size_X * size_Y * size_Z * max_density) >> 16;
		
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
			Math.random() * 300, 
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
		if(((int)(location[0] * 1000000) & 1) == 0)
			location[0] = -location[0];
		location[1] = Math.random();
		if(((int)(location[1] * 1000000) & 1) == 0)
			location[1] = -location[1];
		location[2] = Math.random();
		if(((int)(location[2] * 1000000) & 1) == 0)
			location[2] = -location[2];
		
		location[0] = /*location_player[0] < hight - visibility? hight + location_player[0] - location[0] * 2 * size[1] :*/ hight - location[0] * 2 * size[1];
		location[1] = location[1] * visibility + location_player[1];
		location[2] = location[2] * visibility + location_player[2];
		/*
		for(int i=0 ; i<points_count ; ++i)
		{
			points.get(i)[0] = Math.random() * size[0];
			points.get(i)[1] = Math.random() * size[1];
			points.get(i)[2] = Math.random() * size[2];
		}*/
	}

	@Override
	public void run()
	{
		reConstruct();
	}
}
