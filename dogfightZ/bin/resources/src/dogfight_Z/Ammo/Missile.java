package dogfight_Z.Ammo;

import dogfight_Z.Aircraft;
import dogfight_Z.Effects.EngineFlame;
import dogfight_Z.Effects.ExplosionMaker;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Interfaces.Dynamic;

public class Missile extends Aircraft implements Dynamic
{
	protected	  boolean	actived;
	protected	  boolean	turnOffCamrea;
	public static short		maxLife = 500;
	public		  short		life;
	public		  short		lifeLeft;
	public		  long		lifeTo;
	//public 		  double 	guideLocation[];
	public		  double 	startGuideTime;
	public		  Aircraft  from;
	public		  Aircraft  target;
	public		  CharFrapsCamera camera;
	
	public		  double guideResolution;
	public		  double guideFOV;
	public		  double maxSpeed;
	private		  double halfAResolution;
	private		  double range_old;
	
	//private		  double lrRangeToCenterOnScreen_old = 0.0;
	//private		  double up_dnRangeToCenterOnScreen_old = 0.0;
	
	public Missile
	(
		//String modelFile, 
		short  lifeTime,
		double Speed,
		double maxSpeed,
		double resistance_rate,
		double Location[],
		double Roll_angle[],
		double guideStartTime,
		Aircraft Target
	)
	{
		this
		(
			lifeTime, Speed, maxSpeed, resistance_rate, Location, 
			Roll_angle, guideStartTime, 100, 2.8, null, Target, null, false
		);
	}
	
	public Missile
	(
		//String modelFile, 
		//double Mess,
		short lifeTime,
		double Speed,
		double max_speed,
		double resistance_rate,
		double Location[],
		double Roll_angle[],
		double guideStartTime,
		double guide_Resolution,
		double guide_FOV, 
		Aircraft From,
		Aircraft Target,
		CharFrapsCamera Camera,
		boolean turnOffCma
	)
	{
		super(null, null, 0.0, (short)-1, null, null, null, null, null, null);
		turnOffCamrea	= turnOffCma;
		specialDisplay	= '@';
		maxSpeed		= max_speed;
		maxVelRollUp	= 20.0;				//导弹最大上下翻滚能力
		maxVelTurnLR	= 20.0;				//导弹最大左右水平转向能力
		maxVelRollLR	= 20.0;				//导弹最大左右翻滚能力
		from			= From;				//导弹发射源
		camera			= Camera;			//玩家摄像机，在跟随导弹视角后，将玩家视角归还给from所指示的源物体
		actived			= true;				//导弹是否被激活
		lifeLeft = life	= lifeTime;			//导弹有效时间(与speed一起决定最大射程)
		speed			= Speed;			//导弹速度
		startGuideTime	= guideStartTime;	//导弹发射后进入制导状态的时间
		guideResolution = guide_Resolution;	//导弹制导分辨率
		guideFOV		= guide_FOV;		//导弹最大搜寻视角
		lifeTo = life + System.currentTimeMillis() / 1000;
		resistanceRate_normal	= 0.0;
		range_old		= 0.0;
		
		if(lifeTime > maxLife)
			lifeTime = maxLife;
		
		target = Target;
		
		motionRate = 1;
		
		location[0] = Location[0];
		location[1] = Location[1];
		location[2] = Location[2];
		
		roll_angle[0] = Roll_angle[0];
		roll_angle[1] = Roll_angle[1];
		roll_angle[2] = Roll_angle[2];
		
		halfAResolution = guideResolution / 2;
		
		double newPonit[];
		/*
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
		*/
		points_count = 0;
		
		visible = true;
		
	}
	
	public void trace()
	{
		if(target!= null)
		{
			if(!target.isAlive)
			{
				target = null;
				return;
			}
			
			double point_on_Scr[] = new double[2];
			double range = 
			CharFrapsCamera.getXY_onCamera
			(
				target.location[0], target.location[1], target.location[2], 
				guideResolution, guideResolution, location, cameraRollAngle, point_on_Scr, guideFOV
			);
			
			double lrRangeToCenterOnScreen		= point_on_Scr[0] - halfAResolution;
			double up_dnRangeToCenterOnScreen	= point_on_Scr[1] - halfAResolution;
			
			double range_diff = range_old - range;
			double range_diff2 = range_diff * range_diff;
			
			//double lrRangeToCenterOnScreen_diff = Math.abs(lrRangeToCenterOnScreen_old - lrRangeToCenterOnScreen);
			//double lrRangeToCenterOnScreen_diff2 = lrRangeToCenterOnScreen_diff * lrRangeToCenterOnScreen_diff;
			
			//double up_dnRangeToCenterOnScreen_diff = Math.abs(up_dnRangeToCenterOnScreen_old - up_dnRangeToCenterOnScreen);
			//double up_dnRangeToCenterOnScreen_diff2 = up_dnRangeToCenterOnScreen_diff * up_dnRangeToCenterOnScreen_diff;
			
			double temp0 = range_diff2 * range_diff2 * halfAResolution;
			
			double lrControl = temp0 * lrRangeToCenterOnScreen;
			double udControl = temp0 * up_dnRangeToCenterOnScreen;
			
			double lrRate = 1.0;
			double udRate = 1.0;
			
			if(Math.abs(lrControl) > Math.abs(udControl))		udRate = Math.abs(udControl / lrControl);
			else if(Math.abs(lrControl) < Math.abs(udControl))	lrRate = Math.abs(lrControl / udControl);
			
			if(!(range<0 || point_on_Scr[0]<0))
			{
				
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
				
				if(lrControl > 0)
					control_turn_lr(maxVelTurnLR, /*lrRate*/ lrControl, lrRate);
				else if(lrControl < 0)
					control_turn_lr(-maxVelTurnLR, /*lrRate*/-lrControl, lrRate);
				
				if(udControl > 0)
					control_roll_up_dn(-maxVelRollUp, /*udRate*/ udControl, udRate);
				else if(udControl < 0)
					control_roll_up_dn(maxVelRollUp, /*udRate*/ -udControl, udRate);
				
				//lrRangeToCenterOnScreen_old = lrRangeToCenterOnScreen;
				//up_dnRangeToCenterOnScreen_old = up_dnRangeToCenterOnScreen;
				
				if
				(
					(
						(
							speed > maxSpeed / 3 ||
							speed > target.speed + 50
						)	&&	range < 10000 || 
						range_old - range > 64 && range < 20000 && range > 0 && speed > target.speed + 50 && target.speed < 71
					)
				)	speed -= 10;
				else if(speed < maxSpeed)	++speed;
				
				target.warning(from);
			}
			
			range_old = range;
		}
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
		
		cameraRollAngle[0] = -roll_angle[0];
		cameraRollAngle[1] = -roll_angle[1];
		cameraRollAngle[2] = -roll_angle[2];
		
		if(velocity_roll[0] != 0.0)
			velocity_roll[0] /= 2;
		if(velocity_roll[1] != 0.0)
			velocity_roll[1] /= 2;
		if(velocity_roll[2] != 0.0)
			velocity_roll[2] /= 2;
		
		if(life - lifeLeft > startGuideTime)
			trace();
		
		//------------[go street]------------
		double x, y, z, t, r1 = Math.toRadians(roll_angle[1]), r2 = Math.cos(Math.toRadians(roll_angle[0]));
		t = Math.cos(r1) * speed;
		x = Math.tan(r1) * t;
		y = Math.sin(Math.toRadians(roll_angle[0])) * t;
		z = r2 * t;
		
		location[0]	-= x;
		location[1]	+= y;
		location[2]	+= z;
		
		from.effects.add(new EngineFlame(location, (short)50));
		//--------------[motion]-------------
		roll_up_dn(velocity_roll[0]);
		turn_lr(velocity_roll[1]);
		roll_lr(velocity_roll[2]);
		//-----------------------------------
		//location[0] += CharTimeSpace.g;
		
		if(target != null    &&    target.isAlive    &&    range(location, target.location) < 100)
		{
			target.getDamage((int)(50 - 10 * Math.random()), from, "Missile");
			new ExplosionMaker(location, 15, (short)75, 0.025, 0.1, from.effects);

			target.colorFlash(255, 255, 255, 127, 16, 16, (short)20);
			//target.colorFlash(255, 255, 255, 64, 127, 0, (short)20);
			//from.colorFlash(255, 255, 255, 127, 16, 16, (short)20);
			from.colorFlash(0, 192, 255, 0, 0, 0, (short)12);
			
			disable();
			return;
		}
		
		--lifeLeft;
	}
	
	public void disable() 
	{
		if(camera != null && from != null)
		{
			if(turnOffCamrea) camera.enabled = false;
			else camera.connectLocationAndAngle(from.cameraLocation, from.cameraRollAngle);
		}
		actived = visible = false;
	}

	@Override
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

/*  at function maxMoving
double upT		= Math.atan(l / deltaZ);
double pz[]		= new double[3];

getXYZ_afterRolling
(
	0, 			0, 				1,
	roll_angle[0],	roll_angle[1],	roll_angle[2],
	
	pz
);

double myAngle2	= Math.atan
	(Math.sqrt(pz[0]*pz[0] + pz[1]*pz[1]) / pz[2]);

if(myAngle2	< upT)
	control_roll_up_dn(maxVelRollUp);
else
	control_roll_up_dn(-maxVelRollDn);
*/


/*
public void trace()
{
	//----------------------------------------
	double targetDirection[] = new double[3];
	targetDirection[0] = targetLocation[1] - location[1];
	targetDirection[1] = targetLocation[0] - location[0];
	targetDirection[2] = targetLocation[2] - location[2];
	
	double maxRange, maxIndex;
	
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
	double targetDirection[] = new double[2];
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