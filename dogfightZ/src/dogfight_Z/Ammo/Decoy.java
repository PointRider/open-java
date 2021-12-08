package dogfight_Z.Ammo;

import java.util.ArrayList;
import dogfight_Z.Aircraft;
import dogfight_Z.GameManagement;
import dogfight_Z.Effects.EngineFlame;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;

public class Decoy extends Aircraft implements Dynamic
{
	private float   velocity;
	private int     lifeLeft;
	private int     life;
	private long    lifeTo;
	private boolean end;
	private float   Roll_angle_Aircraft[];
	
private static ArrayList<float[]> missileModelData;
	
	static {
		missileModelData = new ArrayList<float[]>();
		
		float newPonit[];
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = 40;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = -40;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 40;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = -40;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 40;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = -40;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
	}
	
	public static final void make (
        GameManagement gameManager,
        int   camp,
        float location[], 
        float rollAngle_aircraft[],
        float speedAircraft,
        float velocity,
        int   lifeTime, 
        float density, 
        float resistanceRate
    ) {
        float roll[] = new float[3];
        /*
        for(float x=105 ; x<=255 ; x+=1/density) {
            for(float y=-30 ; y<0 ; y+=1/density) {*/
        for(float x=1.8325957145940464F, delta=GraphicUtils.RAD1/density ; x<=4.450589592585541F ; x+=delta) {
            for(float y=-0.5235987755982989F ; y<0 ; y+=delta) {
                roll[0] = x;
                roll[1] = y;
                gameManager.throwDecoy (
                    new Decoy (
                        gameManager, camp, lifeTime + (int) (GraphicUtils.random() * lifeTime), velocity, speedAircraft, 
                        resistanceRate, location, roll, 
                        new float[] {rollAngle_aircraft[0], rollAngle_aircraft[1], rollAngle_aircraft[2]}
                    )
                );
            }
        }
    }
	
	private Decoy
	(
	    GameManagement gameManager,
		int   campTo,
		int   lifeTime,
		float Speed,
		float Speed_aircraft,
		float resistance_rate,
		float Location[],
		float Roll_angle[],
		float Roll_angle_aircraft[]
	) {
		super(gameManager, null, 0.0F, -1, null, null, "\nDecory" + GraphicUtils.random(), false);
		setCamp(campTo);
		specialDisplay = '@';
		location[0] = Location[0];
		location[1] = Location[1];
		location[2] = Location[2];
		
		setSpeed(Speed_aircraft);
		Roll_angle_Aircraft = Roll_angle_aircraft;
		
		points = missileModelData;
		points_count = missileModelData.size();
		
		lifeLeft = life = lifeTime;
		lifeTo = life + System.currentTimeMillis();
		
		end				= false;
		visible			= true;
		
		roll_angle[0] = Roll_angle[0];
		roll_angle[1] = Roll_angle[1];
		roll_angle[2] = Roll_angle[2];
		
		velocity = Speed * GraphicUtils.random() * 2;
		setResistanceRate_normal(resistance_rate * GraphicUtils.random());
		setHP(100);
		setLockingPriority(-(int)(10.0F * GraphicUtils.random()));
	}
	
	private Decoy (
	    GameManagement gameManager,
		short lifeTime,
		float Speed,
		float Speed_aircraft,
		float resistance_rate,
		float Location[],
		float Roll_angle[],
		float Roll_angle_aircraft[]
	) {
		this (
		    gameManager, -1, lifeTime, Speed, Speed_aircraft, 
			resistance_rate, Location, Roll_angle, 
			Roll_angle_aircraft
		);
	}
	
	@Override
	public void go() {
		if(lifeLeft <= 0) disable();
		else {
            velocity -= velocity * getResistanceRate_normal() * 1.5;
            
		    float x, y, z;
			float r1 = roll_angle[1] + Roll_angle_Aircraft[1];
	        float r2 = roll_angle[0] + Roll_angle_Aircraft[0];
	        float t  = GraphicUtils.cos(r1) * velocity;
	        x  = GraphicUtils.sin(r1) * velocity;
	        y  = GraphicUtils.sin(r2) * t;
	        z  = GraphicUtils.cos(r2) * t;

            location[0] -= x;
            location[1] += y;
            location[2] += z;
            
	        r1 = Roll_angle_Aircraft[1];
	        r2 = Roll_angle_Aircraft[0];
	        t  = GraphicUtils.cos(r1) * getSpeed();
            x  = GraphicUtils.sin(r1) * getSpeed();
            y  = GraphicUtils.sin(r2) * t;
            z  = GraphicUtils.cos(r2) * t;
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;

            location[0] += (CharTimeSpace.g - getResistanceRate_normal()) * (life - lifeLeft) * 0.0000125F;
            
			getGameManager().newEffect(new EngineFlame(location, 25000 + (int)(50000 * GraphicUtils.random()), '*'));
			lifeLeft -= 1000;
		}
	}
	
	public final void disable() 
	{
		setLockingPriority(0);
		visible = false;
		setHP(0);
		end = true;
		if(myPosition != null) getGameManager().decoyDisable(myPosition);// deleteQue.add(myPosition);
	}

	@Override
	public final boolean deleted() {
		return end;
	}

	@Override
	public final int compareTo(Dynamic o) {
		return (int) (getLife() - o.getLife());
	}

	@Override
	public final long getLife() {
		return lifeTo;
	}

    @Override
	public final int getHash() {
		return this.hashCode();
	}
}
