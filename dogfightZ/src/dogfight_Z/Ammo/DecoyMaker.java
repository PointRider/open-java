package dogfight_Z.Ammo;

import java.util.LinkedList;
import java.util.PriorityQueue;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;

public class DecoyMaker
{
	public double roll_angle_aircraft[];
	public double speed_aircraft;
	public PriorityQueue<Dynamic> effects;
	
	public DecoyMaker
	(
		short  camp,
		double location[], 
		double rollAngle_aircraft[],
		double speedAircraft,
		double velocity,
		short lifeTime, 
		double density, 
		double resistanceRate, 
		LinkedList<ThreeDs>	   add_que,
		LinkedList<ThreeDs>	   del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		double roll[] = new double[3];
		roll[2] = 0;
		effects = effect;
		roll_angle_aircraft = new double[3];
		roll_angle_aircraft[0] = rollAngle_aircraft[0];
		roll_angle_aircraft[1] = rollAngle_aircraft[1];
		roll_angle_aircraft[2] = rollAngle_aircraft[2];
		
		speed_aircraft = speedAircraft;
		
		for(double x=120 ; x<240 ; x+=1/density) 
		{
			for(double y=-60 ; y<0 ; y+=1/density/2)
			{
				roll[0] = x;
				roll[1] = y*2;
				add_que.add
				(
					new Decoy
					(
						camp, lifeTime, velocity, speedAircraft, 
						resistanceRate, location, roll, 
						roll_angle_aircraft, del_que, effect
					)
				);
			}
		}
	}
	
	public DecoyMaker
	(
		double location[], 
		double rollAngle_aircraft[],
		double speedAircraft,
		double velocity,
		short lifeTime, 
		double density, 
		double resistanceRate, 
		LinkedList<ThreeDs>	   add_que,
		LinkedList<ThreeDs>	   del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		this
		(
			(short)(-1), location, rollAngle_aircraft, 
			speedAircraft, velocity, lifeTime, density,
			resistanceRate, add_que, del_que, effect
		);
	}
}
