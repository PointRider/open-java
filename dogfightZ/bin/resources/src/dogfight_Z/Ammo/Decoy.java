package dogfight_Z.Ammo;

import java.util.LinkedList;
import java.util.PriorityQueue;

import dogfight_Z.Aircraft;
import dogfight_Z.Effects.EngineFlame;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharTimeSpace;

public class Decoy extends Aircraft implements Dynamic
{
	public double  velocity;
	public double  resistanceRate;
	public short lifeLeft;
	public short life;
	public long lifeTo;
	public boolean end;
	public double Roll_angle_Aircraft[];
	
	public Decoy
	(
		short  campTo,
		short  lifeTime,
		double Speed,
		double Speed_aircraft,
		double resistance_rate,
		double Location[],
		double Roll_angle[],
		double Roll_angle_aircraft[],
		LinkedList<ThreeDs>	   del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		super(null, null, 0.0, (short)-1, null, effect, del_que, null, null, null, "\nDecory" + Math.random());
		camp = campTo;
		specialDisplay = '\0';
		location[0] = Location[0];
		location[1] = Location[1];
		location[2] = Location[2];
		
		double newPonit[];
		speed = Speed_aircraft;
		Roll_angle_Aircraft = Roll_angle_aircraft;
		newPonit = new double[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = 0;
		points.add(newPonit);
		
		newPonit = new double[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = 1;
		points.add(newPonit);
		
		newPonit = new double[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = -1;
		points.add(newPonit);
		
		newPonit = new double[3];
		newPonit[0] = 0;
		newPonit[1] = 1;
		newPonit[2] = 0;
		points.add(newPonit);
		
		newPonit = new double[3];
		newPonit[0] = 0;
		newPonit[1] = -1;
		newPonit[2] = 0;
		points.add(newPonit);
		
		newPonit = new double[3];
		newPonit[0] = 1;
		newPonit[1] = 0;
		newPonit[2] = 0;
		points.add(newPonit);
		
		newPonit = new double[3];
		newPonit[0] = -1;
		newPonit[1] = 0;
		newPonit[2] = 0;
		points.add(newPonit);
		
		points_count = 7;
		
		lifeLeft = life = lifeTime;
		lifeTo = life + System.currentTimeMillis() / 1000;
		
		end				= false;
		visible			= true;
		
		roll_angle[0] = Roll_angle[0];
		roll_angle[1] = Roll_angle[1];
		roll_angle[2] = Roll_angle[2];
		
		velocity = Speed * Math.random() * 2;
		resistanceRate = resistance_rate;
		resistanceRate *= Math.random();
		isAlive = true;
		lockingPriority = (short) (-(2.0 + 10.0 * Math.random()));
	}
	
	public Decoy
	(
		short  lifeTime,
		double Speed,
		double Speed_aircraft,
		double resistance_rate,
		double Location[],
		double Roll_angle[],
		double Roll_angle_aircraft[],
		LinkedList<ThreeDs>	   del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		this
		(
			(short)(-1), lifeTime, Speed, Speed_aircraft, 
			resistance_rate, Location, Roll_angle, 
			Roll_angle_aircraft, del_que, effect
		);
	}
	
	@Override
	public void go()
	{
		if(lifeLeft <= 0)
			disable();
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
			location[0] += CharTimeSpace.g * (life - lifeLeft) * 0.0125 - (life-lifeLeft) * resistanceRate;
			
			t = Math.cos(Math.toRadians(Roll_angle_Aircraft[1])) * speed;
			
			x = Math.tan(Math.toRadians(Roll_angle_Aircraft[1])) * t;
			y = Math.sin(Math.toRadians(Roll_angle_Aircraft[0])) * t;
			z = Math.cos(Math.toRadians(Roll_angle_Aircraft[0])) * t;
			
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;
			
			effects.add(new EngineFlame(location, (short)25, '\0'));
			--lifeLeft;
		}
	}
	
	public void disable() 
	{
		lockingPriority = 0;
		visible = false;
		isAlive = false;
		end = true;
		deleteQue.add(this);
	}

	@Override
	public boolean deleted()
	{
		return end;
	}

	@Override
	public int compareTo(Dynamic o)
	{
		return (int) (getLife() - o.getLife());
	}

	@Override
	public long getLife()
	{
		return lifeTo;
	}
	
	@Override
	public void getDamage(int damage, Aircraft giver, String weaponName)
	{
	}
	
	@Override
	public void randomRespawn()
	{
	}
	
	@Override
	public void pollBack()
	{
	}
}
