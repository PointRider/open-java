package dogfight_Z.Effects;

import java.util.ArrayList;

import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Objects.CharObject;

public class EngineFlame extends CharObject implements Dynamic
{
	public long lifeLeft;
	public long life;
	public long lifeTo;
	public boolean end;
	//public boolean haveMess;
	//public float  resistanceRate;
	
	private static ArrayList<float[]> missileModelData;
	static {
		missileModelData = new ArrayList<float[]>();
		
		float newPonit[];
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = 5;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = -5;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 5;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = -5;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 5;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = -5;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
	}
	
	public EngineFlame(float Location[], long lifeTime) {
		this(Location, lifeTime, DrawingMethod.drawPoint);
	}
	
	public EngineFlame(float Location[], long lifeTime, DrawingMethod drawingMethod)
    {
        super(null, drawingMethod);
        specialDisplay  = '@';
        location[0] = Location[0];
        location[1] = Location[1];
        location[2] = Location[2];

        points = missileModelData;
        points_count = missileModelData.size();
        
        lifeLeft = life = lifeTime;
        lifeTo = life + System.currentTimeMillis();
        
        //resistanceRate    = resistance_rate;
        end             = false;
        visible         = true;
        //haveMess      = have_mess;
    }
	
	public EngineFlame(float Location[], long lifeTime, char specialDisplayChar)
	{
		this(Location, lifeTime);
		specialDisplay = specialDisplayChar;
	}
	
	@Override
	public void go()
	{
		if(lifeLeft <= 0) {
			visible = false;
			end = true;
		} else lifeLeft -= 1000;
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
	public int getHash()
	{
		// TODO 自动生成的方法存根
		return this.hashCode();
	}

}
