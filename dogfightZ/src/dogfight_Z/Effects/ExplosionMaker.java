package dogfight_Z.Effects;

import java.util.PriorityQueue;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.utils.GraphicUtils;

public class ExplosionMaker
{
	public ExplosionMaker(float location[], float velocity, long lifeTime, float density, float resistanceRate, PriorityQueue<Dynamic> destoryQue)
	{
		float roll[] = new float[3];
		roll[2] = 0;
		
		for(float x=0 ; x<360 ; x+=1/density) 
		{
			for(float y=-30 ; y<90 ; y+=1/density/2)
			{
				roll[0] = x;
				roll[1] = y*2;
				destoryQue.add(new Particle(location, roll, lifeTime + (int)(y * GraphicUtils.random()), velocity, resistanceRate));
			}
		}
	}
}
