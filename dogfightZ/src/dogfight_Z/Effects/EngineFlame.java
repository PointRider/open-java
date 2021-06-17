package dogfight_Z.Effects;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Objects.CharObject;

public class EngineFlame extends CharObject implements Dynamic
{
	public long lifeLeft;
	public long life;
	public long lifeTo;
	public boolean end;
	//public boolean haveMess;
	//public double  resistanceRate;
	
	public EngineFlame(double Location[], long lifeTime)
	{
		super(null);
		specialDisplay	= '@';
		location[0] = Location[0];
		location[1] = Location[1];
		location[2] = Location[2];
		
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
		
		lifeLeft = life = lifeTime;
		lifeTo = life + System.currentTimeMillis() / 1000;
		
		//resistanceRate	= resistance_rate;
		end				= false;
		visible			= true;
		//haveMess		= have_mess;
	}
	
	public EngineFlame(double Location[], long lifeTime, char specialDisplayChar)
	{
		this(Location, lifeTime);
		specialDisplay = specialDisplayChar;
	}
	/*
	public EngineFlame(double Location[], short lifeTime)
	{
		this(Location, lifeTime, false, 0.0);
	}
	*/
	
	@Override
	public void go()
	{
		if(lifeLeft <= 0)
		{
			visible = false;
			end = true;
		}
		else
			--lifeLeft;
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

}
