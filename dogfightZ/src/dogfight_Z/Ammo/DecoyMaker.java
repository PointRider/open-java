package dogfight_Z.Ammo;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;

public class DecoyMaker
{
	public float roll_angle_aircraft[];
	public float speed_aircraft;
	public PriorityQueue<Dynamic> effects;
	
	public DecoyMaker
	(
		int  camp,
		float location[], 
		float rollAngle_aircraft[],
		float speedAircraft,
		float velocity,
		int lifeTime, 
		float density, 
		float resistanceRate, 
		LinkedList<ThreeDs>	              add_que,
		LinkedList<ListIterator<ThreeDs>> del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		float roll[] = new float[3];
		roll[2] = 0;
		effects = effect;
		roll_angle_aircraft = new float[3];
		roll_angle_aircraft[0] = rollAngle_aircraft[0];
		roll_angle_aircraft[1] = rollAngle_aircraft[1];
		roll_angle_aircraft[2] = rollAngle_aircraft[2];
		
		speed_aircraft = speedAircraft;
		
		for(float x=105 ; x<=255 ; x+=1/density) 
		{
			for(float y=-30 ; y<0 ; y+=1/density)
			{
			    roll[0] = x;
	            roll[1] = y;
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
	/*
	public DecoyMaker
	(
		float location[], 
		float rollAngle_aircraft[],
		float speedAircraft,
		float velocity,
		int    lifeTime, 
		float density, 
		float resistanceRate, 
		LinkedList<ThreeDs>	   add_que,
		LinkedList<ListIterator<ThreeDs>> del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		this
		(
			-1, location, rollAngle_aircraft, 
			speedAircraft, velocity, lifeTime, density,
			resistanceRate, add_que, del_que, effect
		);
	}*/
}
