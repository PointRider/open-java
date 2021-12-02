package dogfight_Z.Effects;

import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;

public class Particle extends EngineFlame
{
	public float  velocity;
	public float  resistanceRate;
	
	public Particle(float[] Location, float [] roll_Angle, long lifeTime, float Velocity, float resistance_rate)
	{
		super(Location, lifeTime);

		roll_angle[0] = roll_Angle[0];
		roll_angle[1] = roll_Angle[1];
		roll_angle[2] = roll_Angle[2];
		
		velocity = Velocity * GraphicUtils.random() * 2;
		resistanceRate = resistance_rate;
		resistanceRate *= GraphicUtils.random();
	}
	
	@Override
	public void go()
	{
		if(lifeLeft <= 0)
		{
			visible = false;
			end = true;
		}
		else
		{
			float x, y, z, t = GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[1])) * velocity;
			
			x = GraphicUtils.tan(GraphicUtils.toRadians(roll_angle[1])) * t;
			y = GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[0])) * t;
			z = GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * t;
			
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;
			
			velocity -= velocity * resistanceRate * 1.5;
			location[0] += CharTimeSpace.g * (life - lifeLeft) * 0.0275 - (life-lifeLeft) * resistanceRate * 2.4;
			
			--lifeLeft;
		}
	}
}
