package dogfight_Z.Effects;

import graphic_Z.Worlds.CharTimeSpace;

public class Particle extends EngineFlame
{
	public double  velocity;
	public double  resistanceRate;
	
	public Particle(double[] Location, double [] roll_Angle, long lifeTime, double Velocity, double resistance_rate)
	{
		super(Location, lifeTime);

		roll_angle[0] = roll_Angle[0];
		roll_angle[1] = roll_Angle[1];
		roll_angle[2] = roll_Angle[2];
		
		velocity = Velocity * Math.random() * 2;
		resistanceRate = resistance_rate;
		resistanceRate *= Math.random();
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
			double x, y, z, t = Math.cos(Math.toRadians(roll_angle[1])) * velocity;
			
			x = Math.tan(Math.toRadians(roll_angle[1])) * t;
			y = Math.sin(Math.toRadians(roll_angle[0])) * t;
			z = Math.cos(Math.toRadians(roll_angle[0])) * t;
			
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;
			
			velocity -= velocity * resistanceRate * 1.5;
			location[0] += CharTimeSpace.g * (life - lifeLeft) * 0.025 - (life-lifeLeft) * resistanceRate * 2.4;
			
			--lifeLeft;
		}
	}
}
