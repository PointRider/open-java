package dogfight_Z.Ammo;

import java.util.ArrayList;

import dogfight_Z.Aircraft;
import dogfight_Z.GameManagement;
import dogfight_Z.Effects.EngineFlame;
import dogfight_Z.Effects.Particle;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.utils.GraphicUtils;

public class Missile extends Aircraft implements Dynamic, Dangerous
{
	protected	  boolean	actived;
	public static int		maxLife = 500;
	public		  int		life;
	public		  int		lifeLeft;
	public		  long		lifeTo;
	//public 		  float 	guideLocation[];
	public		  float 	startGuideTime;
	public		  Aircraft  launcher;
	public		  Aircraft  target;
	public		  CharFrapsCamera camera;
	public		  int    guideResolution;
	public		  float guideFOV;
	public		  float maxSpeed;
	private		  float halfAResolution;
	private		  float range_old;
	
	private static ArrayList<float[]> missileModelData;
	
	static {
		missileModelData = new ArrayList<float[]>();
		
		float newPonit[];
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		/*
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = -1;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 1;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = -1;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 1;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = -1;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);*/
	}
	
	//private		  float lrRangeToCenterOnScreen_old = 0.0;
	//private		  float up_dnRangeToCenterOnScreen_old = 0.0;
	
	public Missile
	(
		//String modelFile, 
	    GameManagement gameManager,
		int   lifeTime,
		float Speed,
		float maxSpeed,
		float Location[],
		float Roll_angle[],
		float guideStartTime,
		Aircraft Target
	) {
		this (
		    gameManager,lifeTime, Speed, maxSpeed, Location, 
			Roll_angle, guideStartTime, 100, 2.8F, null, Target, null
		);
	}
	
	public Missile
	(
		GameManagement gameManager,
		int   lifeTime,
		float Speed,
		float max_speed,
		float Location[],
		float Roll_angle[],
		float guideStartTime,
		int   guide_Resolution,
		float guide_FOV, 
		Aircraft From,
		Aircraft Target,
		CharFrapsCamera Camera
	)
	{
		super(gameManager, null, 0.0F, -1, null, null, null, false);
		specialDisplay	= '@';
		maxSpeed		= max_speed;
		setMaxVelRollUp(20.0F);				//导弹最大上下翻滚能力
		setMaxVelTurnLR(20.0F);				//导弹最大左右水平转向能力
		setMaxVelRollLR(20.0F);				//导弹最大左右翻滚能力
		launcher			= From;				//导弹发射源
		camera			= Camera;			//玩家摄像机，在跟随导弹视角后，将玩家视角归还给from所指示的源物体
		actived			= true;				//导弹是否被激活
		lifeLeft = life	= lifeTime;			//导弹有效时间(与speed一起决定最大射程)
		setSpeed(Speed);			//导弹速度
		startGuideTime	= guideStartTime;	//导弹发射后进入制导状态的时间
		guideResolution = guide_Resolution;	//导弹制导分辨率
		guideFOV		= guide_FOV;		//导弹最大搜寻视角
		lifeTo = life + System.currentTimeMillis() / 1000;
		setResistanceRate_normal(0.0F);
		range_old		= 0.0F;
		
		if(lifeTime > maxLife) lifeTime = maxLife;
		
		target = Target;
		
		motionRate = 1;
		
		location[0] = Location[0];
		location[1] = Location[1];
		location[2] = Location[2];
		
		roll_angle[0] = Roll_angle[0];
		roll_angle[1] = Roll_angle[1];
		roll_angle[2] = Roll_angle[2];
		
		halfAResolution = guideResolution >> 1;
		
		points = missileModelData;
		points_count = missileModelData.size();
		
		visible = true;
	}
	
	public void trace() {
		if(target!= null) {
			if(!target.isAlive()) {
				target = null;
				return;
			}
			float point_on_Scr[] = new float[2];
			float range = 
			CharFrapsCamera.getXY_onCamera (
				target.location[0], target.location[1], target.location[2], 
				guideResolution, guideResolution, location, getCameraRollAngle(), point_on_Scr, guideFOV
			);
			
			float lrRangeToCenterOnScreen    = point_on_Scr[0] - halfAResolution;
			float up_dnRangeToCenterOnScreen = point_on_Scr[1] - halfAResolution;
			float range_diff = range_old - range;
			float range_diff2 = range_diff * range_diff;
			float temp0 = range_diff2 * range_diff2 * halfAResolution;
			float lrControl = temp0 * lrRangeToCenterOnScreen;
			float udControl = temp0 * up_dnRangeToCenterOnScreen;
			float lrRate = 1.0F;
			float udRate = 1.0F;
			
			if(GraphicUtils.abs(lrControl) > GraphicUtils.abs(udControl))		
			    udRate = GraphicUtils.abs(udControl / lrControl);
			else if(GraphicUtils.abs(lrControl) < GraphicUtils.abs(udControl))	
			    lrRate = GraphicUtils.abs(lrControl / udControl);
			
			if(!(range<0 || point_on_Scr[0]<0)) {
				if(lrControl > 0)
					control_turn_lr(getMaxVelTurnLR(), /*lrRate*/ lrControl, lrRate);
				else if(lrControl < 0)
					control_turn_lr(-getMaxVelTurnLR(), /*lrRate*/-lrControl, lrRate);
				if(udControl > 0)
					control_roll_up_dn(-getMaxVelRollUp(), /*udRate*/ udControl, udRate);
				else if(udControl < 0)
					control_roll_up_dn(getMaxVelRollUp(), /*udRate*/ -udControl, udRate);
				
				if(((
							getSpeed() > maxSpeed / 3 ||
							getSpeed() > target.getSpeed() + 50
						)	&&	range < 10000 || 
						range_old - range > 64 && range < 20000 && range > 0 && 
						getSpeed() > target.getSpeed() + 50 && target.getSpeed() < 71
					)
				)	setSpeed(getSpeed() - 5);
				else if(getSpeed() < maxSpeed)	setSpeed(getSpeed() + 0.5F);
				target.warning(launcher);
			}
			range_old = range;
		}
	}
	/*
	protected static float range(float p1[], float p2[])
	{
		return GraphicUtils.abs
		(
			GraphicUtils.sqrt
			(
				(p2[0]-p1[0])*(p2[0]-p1[0]) +
				(p2[1]-p1[1])*(p2[1]-p1[1]) +
				(p2[2]-p1[2])*(p2[2]-p1[2]) 
			)
		);
	}
	*/
	@Override
	public void go()
	{
		if(!actived) return;
		if(lifeLeft <= 0) {
			disable();
			return;
		}
		
		getCameraRollAngle()[0] = -roll_angle[0];
		getCameraRollAngle()[1] = -roll_angle[1];
		getCameraRollAngle()[2] = -roll_angle[2];
		
		float x, y, z, t, r1, r2;
		getGameManager().newEffect(new EngineFlame(location, 100 + (int)(50 * GraphicUtils.random())));
		
		for(int repeat = 0; repeat < 2; ++repeat) {
			if(velocity_roll[0] != 0.0)
				velocity_roll[0] /= 2;
			if(velocity_roll[1] != 0.0)
				velocity_roll[1] /= 2;
			if(velocity_roll[2] != 0.0)
				velocity_roll[2] /= 2;
			
			if(life - lifeLeft > startGuideTime)
				trace();
			//------------[go street]------------
			r1 = GraphicUtils.toRadians(roll_angle[1]);
			r2 = GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0]));
			t  = GraphicUtils.cos(r1) * getSpeed();
			x  = GraphicUtils.tan(r1) * t;
			y  = GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[0])) * t;
			z  = r2 * t;
			
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;
			
			//--------------[motion]-------------
			roll_up_dn(velocity_roll[0]);
			turn_lr(velocity_roll[1]);
			roll_lr(velocity_roll[2]);
			//-----------------------------------
			//location[0] += CharTimeSpace.g;
			
			if(target != null  &&  target.isAlive()  &&  GraphicUtils.range(location, target.location) < 224)
			{
				target.getDamage((int)(50 - 10 * GraphicUtils.random()), launcher, "Missile");
				Particle.makeExplosion(getGameManager(), location, 15, 75, 0.025F, 0.1F);

				if(target.isPlayer()) target.getGameManager().colorFlash(255, 255, 255, 127, 16, 16, 20);
				if(launcher.isPlayer()) launcher.getGameManager().colorFlash(0, 192, 255, 0, 0, 0, 12);
				
				disable();
				return;
			}
		}
				
		--lifeLeft;
	}
	
	public final void disable() 
	{
	    setHP(0);
		if(camera != null && launcher != null)
			camera.connectLocationAndAngle(launcher.getCameraLocation(), launcher.getCameraRollAngle());
		actived = visible = false;
	}

	@Override
	public final boolean deleted() {
		return !actived;
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

    @Override
    public final int getDamage() {
        //disable();
        return (int)(50 - 10 * GraphicUtils.random());
    }

    @Override
    public final String getWeaponName() {
        return "Missile";
    }
    
    public final String getLauncherID() {
        return launcher.getID();
    }
}


/*侧桶滚， 现在的导弹设定中无需
if(maxVelRollUp > maxVelTurnLR)
{
    if(lrRangeToCenterOnScreen > 0)
        control_roll_lr(-maxVelRollLR);
    else if(lrRangeToCenterOnScreen < 0)
        control_roll_lr(maxVelRollLR);
}
else if(maxVelRollUp < maxVelTurnLR)
{
    if(up_dnRangeToCenterOnScreen > 0)
        control_roll_lr(-maxVelRollLR);
    else if(up_dnRangeToCenterOnScreen < 0)
        control_roll_lr(maxVelRollLR);
}
*/

/*  at function maxMoving
float upT		= GraphicUtils.atan(l / deltaZ);
float pz[]		= new float[3];

getXYZ_afterRolling
(
	0, 			0, 				1,
	roll_angle[0],	roll_angle[1],	roll_angle[2],
	
	pz
);

float myAngle2	= GraphicUtils.atan
	(GraphicUtils.sqrt(pz[0]*pz[0] + pz[1]*pz[1]) / pz[2]);

if(myAngle2	< upT)
	control_roll_up_dn(maxVelRollUp);
else
	control_roll_up_dn(-maxVelRollDn);
*/


/*
public void trace()
{
	//----------------------------------------
	float targetDirection[] = new float[3];
	targetDirection[0] = targetLocation[1] - location[1];
	targetDirection[1] = targetLocation[0] - location[0];
	targetDirection[2] = targetLocation[2] - location[2];
	
	float maxRange, maxIndex;
	
	if(targetDirection[0] > targetDirection[1])
	{
		if(targetDirection[0] > targetDirection[2])
		{
			maxRange = targetDirection[0];
			maxIndex = 0;
		}
		else
		{
			maxRange = targetDirection[2];
			maxIndex = 2;
		}
	}
	else
	{
		if(targetDirection[1] > targetDirection[2])
		{
			maxRange = targetDirection[1];
			maxIndex = 1;
		}
		else
		{
			maxRange = targetDirection[2];
			maxIndex = 2;
		}
	}
	
	targetDirection[0] = (maxIndex==0? 1 : targetDirection[0]/maxRange);
	targetDirection[1] = (maxIndex==1? 1 : targetDirection[1]/maxRange);
	targetDirection[2] = (maxIndex==2? 1 : targetDirection[2]/maxRange);
	//----------------------------------------
	float targetDirection[] = new float[2];
	if(targetDirection[0] > targetDirection[1])
	{
		targetDirection[1] /= targetDirection[0];
		targetDirection[0]  = 1;
	}
	else
	{
		targetDirection[0] /= targetDirection[1];
		targetDirection[1]  = 1;
	}
	maxMoving(targetDirection[0], targetDirection[1]);
}
*/