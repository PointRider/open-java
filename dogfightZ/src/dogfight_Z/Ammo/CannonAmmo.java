package dogfight_Z.Ammo;

import java.util.HashSet;
import java.util.PriorityQueue;

import dogfight_Z.Aircraft;
import dogfight_Z.Effects.ExplosionMaker;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharMessObject;
//import graphic_Z.Worlds.CharTimeSpace;

public class CannonAmmo extends CharMessObject implements Dynamic
{
	protected	 boolean	actived;
	
	public static short 	maxLife = 500;
	public		  short		life;
	public		  short		myCamp;
	public		  short		lifeLeft;
	public		  long		lifeTo;
	public		  double	speed;
	public		  double	resistanceRate;
	public		  double[]	temp;
	public PriorityQueue<Dynamic> effects;
	public		  Aircraft	from;
	public		  HashSet<ThreeDs> aircrafts;
	
	public CannonAmmo
	(
		short  lifeTime,
		short  my_camp,
		double Speed,
		double resistance_rate,
		double Location[],
		double Roll_angle[],
		HashSet<ThreeDs> Aircrafts,
		PriorityQueue<Dynamic> Effects,
		Aircraft souce
	)
	{
		super(null, 1);
		specialDisplay	= '@';
		temp 			= new double[3];
		actived			= true;
		lifeLeft = life = lifeTime;
		myCamp			= my_camp;
		speed			= Speed;
		resistanceRate	= resistance_rate;
		aircrafts		= Aircrafts;
		effects			= Effects;
		from			= souce;
		if(lifeTime > maxLife)
			lifeTime = maxLife;
		
		lifeTo			= lifeTime + System.currentTimeMillis() / 1000;
		
		location[0] = Location[0];
		location[1] = Location[1];
		location[2] = Location[2];
		
		roll_angle[0] = Roll_angle[0];
		roll_angle[1] = Roll_angle[1];
		roll_angle[2] = Roll_angle[2];
		
		double newPonit[];
		
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
		
		visible = true;
	}
	
	protected static double range(double p1[], double p2[])
	{
		return Math.abs
		(
			Math.sqrt
			(
				(p2[0]-p1[0])*(p2[0]-p1[0]) +
				(p2[1]-p1[1])*(p2[1]-p1[1]) +
				(p2[2]-p1[2])*(p2[2]-p1[2]) 
			)
		);
	}
	
	@Override
	public void go()
	{
		if(!actived)
			return;
		if(lifeLeft <= 0)
		{
			disable();
			return;
		}
		double x, y, z, t, r1, r2;
		Aircraft aJet = null;
		
		for(int repeat = 0; repeat < 5; ++repeat)
		{
			//------------[go street]------------
			r1 = Math.toRadians(roll_angle[1]);
			r2 = Math.cos(Math.toRadians(roll_angle[0]));
			t = Math.cos(r1) * speed;
			x = Math.tan(r1) * t;
			y = Math.sin(Math.toRadians(roll_angle[0])) * t;
			z = r2 * t;
			
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;
			
			for(ThreeDs T:aircrafts)
			{
				aJet = (Aircraft) T;
				if(aJet.ID.charAt(0) != '\n')
				if(range(location, aJet.location) < 128)
				{
					if(aJet.camp != myCamp)
					{
						aJet.getDamage(15, from, "Cannon");
						new ExplosionMaker(location, 10, (short)75, 0.01, 0.1, effects);
						aJet.colorFlash(255, 255, 128, 127, 15, 15, (short)2);
						from.colorFlash(255, 255, 128, 128, 96, 0, (short)2);
						disable();
						return;
					}
				}
			}
		}
		
		--lifeLeft;
	}
	
	public void disable() 
	{
		actived = visible = false;
	}
	
	public boolean deleted()
	{
		return !actived;
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
}
