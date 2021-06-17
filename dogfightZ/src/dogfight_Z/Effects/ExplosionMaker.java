package dogfight_Z.Effects;

import java.util.PriorityQueue;

import graphic_Z.Interfaces.Dynamic;

public class ExplosionMaker
{
	public ExplosionMaker(double location[], double velocity, long lifeTime, double density, double resistanceRate, PriorityQueue<Dynamic> destoryQue)
	{
		double roll[] = new double[3];
		roll[2] = 0;
		
		for(double x=0 ; x<360 ; x+=1/density) 
		{
			for(double y=-30 ; y<90 ; y+=1/density/2)
			{
				roll[0] = x;
				roll[1] = y*2;
				destoryQue.add(new Particle(location, roll, lifeTime, velocity, resistanceRate));
			}
		}
	}
}
