package dogfight_Z;

import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharObject;
import graphic_Z.utils.GraphicUtils;

public class RandomCloud extends CharObject implements Runnable, ThreeDs
{
	private float size[];
	private float visibility;
	private float hight;
	private float playerCameraLocation[];
	//float location_player[];
	/*
	public RandomClouds (  
		float Location[], float Visibility, float Hight,
		float size_X, float size_Y, float size_Z, float max_density
	) {
		super(null, true);
		System.out.println("test");
		visibility = Visibility;
		hight = Hight;
		
		size = new float[3];
		size[0] = size_X;
		size[1] = size_Y;
		size[2] = size_Z;
		size_Y = size_Y / 2.0;
		location_player = Location;
		location = new float[3];
		
		location[0] = GraphicUtils.random();
		if(((int)(location[0] * 1000000) & 1) == 0)
			location[0] = -location[0];
		location[1] = GraphicUtils.random();
		if(((int)(location[1] * 1000000) & 1) == 0)
			location[1] = -location[1];
		location[2] = GraphicUtils.random();
		if(((int)(location[2] * 1000000) & 1) == 0)
			location[2] = -location[2];
		
		location[0] = hight - location[0] * 2 * size[1];
		location[1] = location[1] * visibility + location_player[1];
		location[2] = location[2] * visibility + location_player[2];
		
		roll_angle[0] = 360 * GraphicUtils.random();
		roll_angle[1] = 360 * GraphicUtils.random();
		roll_angle[2] = 360 * GraphicUtils.random();
		
		density = max_density * GraphicUtils.random();
		
		//int count = (int)(size_X * size_Y * size_Z * max_density) >> 16;
		//
		//float newPonit[];
		//
		//for(points_count=0 ; points_count<count ; ++points_count)
		//{
		//	newPonit = new float[3];
		//	
		//	newPonit[0] = GraphicUtils.random() * size_X;
		//	newPonit[1] = GraphicUtils.random() * size_Y;
		//	newPonit[2] = GraphicUtils.random() * size_Z;
		//	
		//	points.add(newPonit);
		//}
		

		int count = (int)(size_Z * density);
		
		float y0;

		float newLine[];
		max_density = 1.0 / density;
		for(float x = 1.0; count > 0  &&  x < size_X; x += density * size_X / 10) {
			for(float z = 1.0; count > 0  &&  z < size_Z; z += density * size_Z / 10) {
				
				y0 = size_Y * GraphicUtils.random();
				newLine = new float[6];
				newLine[0] = newLine[3] = x;
				newLine[1] = y0;
				newLine[4] = y0 + size_Y * GraphicUtils.random();
				newLine[5] = newLine[2] = z;
				
				points.add(newLine);
				--count;
				++points_count;
			}
		}

		visible = true;
	}*/
	
	public RandomCloud
	(	//		hights
	    float playerCameraLocation[], float Visibility, float Hight,
		float size_X, float size_Y, float size_Z,
		float max_density
	)
	{
		super(null);
		this.playerCameraLocation = playerCameraLocation;
		//System.out.println("test");
		visibility = Visibility;
		hight = Hight;
		
		size = new float[3];
		size[0] = size_X;
		size[1] = size_Y;
		size[2] = size_Z;
		
		location = new float[3];
		
		location[0] = GraphicUtils.random();
		if((GraphicUtils.fastRanodmInt() & 1) == 0)
			location[0] = -location[0];
		location[1] = GraphicUtils.random();
		if((GraphicUtils.fastRanodmInt() & 1) == 0)
			location[1] = -location[1];
		location[2] = GraphicUtils.random();
		if((GraphicUtils.fastRanodmInt() & 1) == 0)
			location[2] = -location[2];
		
		location[0] = hight - location[0] * 2 * size[1];
		location[1] = location[1] * visibility + playerCameraLocation[1];
		location[2] = location[2] * visibility + playerCameraLocation[2];
		
		roll_angle[0] = roll_angle[1] = roll_angle[2] = 0.0F;
		
		int count = (int)(size_X * size_Y * size_Z * max_density) >> 16;
		
		float newPonit[];
		
		for(points_count=0 ; points_count<count ; ++points_count)
		{
			newPonit = new float[3];
			
			newPonit[0] = GraphicUtils.random() * size_X;
			newPonit[1] = GraphicUtils.random() * size_Y;
			newPonit[2] = GraphicUtils.random() * size_Z;
			
			points.add(newPonit);
		}
		
		visible = true;
	}
	
	public RandomCloud (
	    float playerCameraLocation[], float Visibility,
		float Hight, float max_density
	)
	{
		this
		(
		    playerCameraLocation, Visibility, Hight,
			GraphicUtils.random() * 600, 
			GraphicUtils.random() * 2000, 
			GraphicUtils.random() * 2000,
			max_density
		);
	}
	
	public RandomCloud
	(
	    float playerCameraLocation[], float Visibility, float Hight
	)	{this(playerCameraLocation, Visibility, Hight, GraphicUtils.random() * 1000);}
	
	public void reConstruct()
	{
		location[0] = GraphicUtils.random();
		if((GraphicUtils.fastRanodmInt() & 1) == 0)
			location[0] = -location[0];
		location[1] = GraphicUtils.random();
		if((GraphicUtils.fastRanodmInt() & 1) == 0)
			location[1] = -location[1];
		location[2] = GraphicUtils.random();
		if((GraphicUtils.fastRanodmInt() & 1) == 0)
			location[2] = -location[2];
		
		location[0] = hight - location[0] * 2 * size[1]; /*location_player[0] < hight - visibility? hight + location_player[0] - location[0] * 2 * size[1] :*/ 
		location[1] = location[1] * visibility + playerCameraLocation[1];
		location[2] = location[2] * visibility + playerCameraLocation[2];
		/*
		for(int i=0 ; i<points_count ; ++i)
		{
			points.get(i)[0] = GraphicUtils.random() * size[0];
			points.get(i)[1] = GraphicUtils.random() * size[1];
			points.get(i)[2] = GraphicUtils.random() * size[2];
		}*/
	}

	@Override
	public void run()
	{
		reConstruct();
	}
}
