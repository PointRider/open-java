package dogfight_Z;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import dogfight_Z.Ammo.CannonAmmo;
import dogfight_Z.Ammo.DecoyMaker;
import dogfight_Z.Ammo.Missile;
import dogfight_Z.Effects.EngineFlame;
import dogfight_Z.Effects.ExplosionMaker;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharMessObject;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.LinkedListZ;

public class Aircraft extends CharMessObject
{
	private String ID;
	public int HP;
	public int camp;
	public int lockingPriority;
	public int lockingPriority_backup;
	public Game	 game;
	public LinkedList<ListIterator<ThreeDs>> deleteQue;
	public LinkedListZ<ThreeDs>	   aircrafts;
	public boolean isPlayer;
	
	public float speed;
	public float maxSpeed;				//限制最高速度
	public float maxAccForce;			//最大推力(与mess一起决定最大加速度)
	
	public float control_stick_acc;	//当前引擎档位(通过getCurrentForce函数获得当前引擎推力)HashSet<ThreeDs>
	public static final float maxShift = 32;	//最大操纵杆档位
	
	public float engine_rpm;			//引擎转速
	public float max_rpm;				//最大转速
	public float pushPower;			//当前加力燃烧推力
	public float maxPushTime;			//最大加力燃烧时间
	public float pushTimeLeft;			//加力燃烧剩余时间
	
	public float acc_shift;			//上拨操纵杆速度(固定)
	public float maxdeceleration;		//最大减速度(打开减速板)
	
	public float resistanceRate_current;	//当前空气阻力系数
	public float resistanceRate_normal;	//无动作时空气阻力系数
	public float resistanceRate_breaking;	//打开减速板时的空气阻力系数
	
	public float minStableSpeed;		//最小稳定速度(达到最大升力)
	public float maxVelRollUp;			//最大向上翻滚速度
	public float maxVelRollDn;			//最大向下翻滚速度
	public float maxVelRollLR;			//最大左右翻滚速度
	public float maxVelTurnLR;			//最大左右水平转向速度

	public float tmp_float[];
	//public float cannonRollAngle[];
	public float cameraRollAngle[];
	public float cameraLocation[];
	//public float cameraRollAngle_rev[];
	public float cannonLocation[];
	public boolean isAlive;
	
	public long respwanAtTime;
			
	public PriorityQueue<Dynamic> fired;
	public PriorityQueue<Dynamic> effects;
	public int 	cameraLocationFlag;
	
	//---------[state]----------
	public boolean  isPushing;
	public boolean  isCannonFiring;
	public int		cannonFireLoadTime;
	public float   motionRate;
	protected int	cannonGunFlg;
    protected int   missileFlg;
	public CharFrapsCamera mainCamera;
    private float speedVector[];
	//--------------------------
	public Aircraft locked_By;
	public int		lockingLife;
	public boolean	lockedByEnemy;
	//--------------------------
	public LinkedList<ThreeDs> addWatingQueue;
	//---------[ammo]-----------
	public int		missileMagazine;				//单次挂载最大弹容量
	public int		missileMagazineLeft;			//当前挂载导弹余量
	public int	missileReloadingTime;			//重新挂载时间
	public int	missileReloadingTimeLeft;		//重新挂载时间剩余
	public int		cannonMagazine;					//单次装填最大弹容量
	public int		cannonMagazineLeft;				//当前装填炮弹余量
	public int	cannonReloadingTime;			//重新装填时间
	public int	cannonReloadingTimeLeft;		//重新装填时间剩余
	public int		decoyMagazine;					//单次诱饵弹装填最大弹容量
	public int		decoyMagazineLeft;				//当前诱饵弹装填炮弹余量
	public int	decoyReloadingTime;				//诱饵弹装填时间
	public int	decoyReloadingTimeLeft;			//诱饵弹装填时间剩余
	public float	effectMakingLocation[][];
	//--------------------------
	public int killed;
	public int dead;
	
	public float fov_1stPerson;
	public float fov_3thPerson;
	public float fov_pushing;
	public float fov_current;
	public float fov_gunFiring;
	
	public static float getCurrentForce(float maxAccForce, float max_rpm, float rpm)	//
	{
		float result = ((-1 / ( rpm/max_rpm*4.75F-5 )) - 0.2F) / 3.8F * maxAccForce;
		if(result < 0)
			return 0;
		else return result;
	}
	
	public static float getCurrentRPM(float max_rpm, float shift)	//ln(shift) / ln(max_rpm)
	{
		if(shift>0.0F && maxShift>0.0F)
		{
			float currentForceRate = (GraphicUtils.log(shift) / GraphicUtils.log(maxShift));
			if(currentForceRate > 0.0F)
				return currentForceRate * max_rpm;
			else return 0.0F;
		}
		else return 0.0F;
	}

	public Aircraft
	(
		Game                   theGame,
		String                 modelFile, 
		float                 Mess, 
		int                    Camp,
		PriorityQueue<Dynamic> firedAmmo, 
		PriorityQueue<Dynamic> Effects, 
		LinkedList<ListIterator<ThreeDs>>	   delete_que,
		LinkedList<ThreeDs>	   add_que,
		LinkedListZ<ThreeDs>   Aircrafts,
		CharFrapsCamera		   MainCamera, 
		String                 id,
		boolean                line
	)
	{
		super(modelFile, Mess, line);
		killed				= dead = 0;
		/*
		fov_1stPerson		= 2.6;
		fov_3thPerson		= 2.6;
		fov_pushing			= 2.9;
		fov_gunFiring       = 1.0;
		fov_current			= 2.6;
		*/

        fov_1stPerson       = 9.28F;
        fov_3thPerson       = 9.28F;
        fov_pushing         = 9.36F;
        fov_gunFiring       = 8.9F;
        fov_current         = 9.28F;
        
		respwanAtTime		= 0;
		specialDisplay		= '@';
		ID					= id;
		mainCamera			= MainCamera;
		//cannonRollAngle	= new float[3];
		cameraRollAngle		= new float[3];
		tmp_float			= new float[3];
		cannonLocation		= new float[3];
		cameraLocation		= new float[3];
        speedVector         = new float[3];
		effectMakingLocation= new float[2][3];
		cameraLocationFlag	= 0;
		addWatingQueue		= add_que;
		lockingPriority		= lockingPriority_backup = 1;
		camp				= Camp;
		HP					= 100;
		max_rpm				= 14000;
		maxSpeed			= 600;
		maxAccForce			= 18000;
		maxPushTime			= 1000;
		pushPower 			= 1500;
		pushTimeLeft		= 1000;
		control_stick_acc	= 0.0F;
		acc_shift			= 0.05F;
		maxdeceleration		= 1.0F;
		minStableSpeed		= 30.0F;
		resistanceRate_current	= 0.00500F;
		resistanceRate_normal	= 0.00500F;
		resistanceRate_breaking = 0.00575F;
		maxVelTurnLR		= 5.0F;
		maxVelRollLR		= 9.0F;//
		maxVelRollUp		= 9.0F;
		maxVelRollDn		= 9.0F;
		motionRate			= 0.0F;
		cannonFireLoadTime	= 0;
		isPushing			= false;
		isPlayer			= true;
		isAlive				= true;
		cannonGunFlg		= 1;
		missileFlg          = 1;
		game				= theGame;
		locked_By			= null;
		lockingLife			= 0;
		fired				= firedAmmo;
		effects				= Effects;
		deleteQue			= delete_que;
		aircrafts			= Aircrafts;
		missileMagazine		= 4;
		missileMagazineLeft	= 4;
		missileReloadingTime	= 500;
		missileReloadingTimeLeft= 500;
		
		cannonMagazine		= 360;
		cannonMagazineLeft	= 360;
		cannonReloadingTime		= 500;
		cannonReloadingTimeLeft	= 500;
		
		decoyMagazine		= 1;
		decoyMagazineLeft	= 1;
		decoyReloadingTime		= 500;
		decoyReloadingTimeLeft	= 500;
		
		cameraRollAngle[0] = -roll_angle[0];
		cameraRollAngle[1] = -roll_angle[1];
		cameraRollAngle[2] = -roll_angle[2];
	}
	
	
	public void getRelativePosition_XY(float y, float z, float result[])
	{
		float Y, Z, cos$, sin$, r0 = GraphicUtils.toRadians(cameraRollAngle[0]);
		
		y -= location[1];
		z -= location[2];
		
		cos$ = GraphicUtils.cos(r0);
		sin$ = GraphicUtils.sin(r0);
		Z = cos$ * z - sin$ * y;
		Y = sin$ * z + cos$ * y;
		
		result[0] = Y;
		result[1] = Z;
	}
	
	public Aircraft
	(
		Game game,
		String modelFile,
		float Mess, 
		short Camp,
		PriorityQueue<Dynamic> firedAmmo, 
		PriorityQueue<Dynamic> Effects,  
		LinkedList<ListIterator<ThreeDs>>	   delete_que,
		LinkedList<ThreeDs>	   add_que,
		LinkedListZ<ThreeDs>   Aircrafts,
		CharFrapsCamera		   MainCamera,
		boolean                line
	)
	{
		this(game, modelFile, Mess, Camp, firedAmmo, Effects, delete_que, add_que, Aircrafts, MainCamera, "Me", line);
	}
	
	public void warning(Aircraft source)
	{
		locked_By			= source;
		lockedByEnemy		= true;
		lockingLife			= 20;
	}
	
	public void roll_up_dn(float angleVel)
	{
		if(GraphicUtils.abs(roll_angle[1]) > 90.0)	roll_angle[0] += GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[2])) * angleVel / 6.4;
			else							roll_angle[0] -= GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[2])) * angleVel / 6.4;
		
		roll_angle[1] += GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[2])) * angleVel / 6.4;
			
		roll_angle[0] %= 360;//
		//roll_angle[1] %= 360;//
		
		if(roll_angle[1] > 180) roll_angle[1] = -180.0F + (roll_angle[1]-180.0F);
		else if(roll_angle[1] < -180) roll_angle[1] = 180.0F + (roll_angle[1]+180.0F);
	}
	
	public void turn_lr(float angleVel)
	{
		if(GraphicUtils.abs(roll_angle[1]) > 90.0)	roll_angle[0] -= GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[2])) * angleVel / 6.4;
			else							roll_angle[0] += GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[2])) * angleVel / 6.4;

		roll_angle[1] += GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[2])) * angleVel / 6.4;
		
		roll_angle[0] %= 360;//
		//roll_angle[1] %= 360;//
		
		if(roll_angle[1] > 180) roll_angle[1] = -180.0F + (roll_angle[1]-180.0F);
		else if(roll_angle[1] < -180) roll_angle[1] = 180.0F + (roll_angle[1]+180.0F);
	}
	
	public void roll_lr(float angleVel)
	{
		roll_angle[2] = (roll_angle[2] + (angleVel/1.8F)) % 360;
	}
	
	public void control_roll_up_dn(float acceleration, float limit, float maxLimit)
	{
		velocity_roll[0] += acceleration * motionRate * limit;
		
		if(velocity_roll[0] < 0 && velocity_roll[0] < -maxVelRollDn * motionRate * maxLimit)
		{
			velocity_roll[0] = -maxVelRollDn * motionRate * maxLimit;
		}
		else if(velocity_roll[0] >= 0 && velocity_roll[0] > maxVelRollUp * motionRate * maxLimit)
		{
			velocity_roll[0] = maxVelRollUp * motionRate * maxLimit;
		}
	}
	
	public void control_roll_up_dn(float acceleration)
	{
		if(acceleration < 0 && acceleration < -maxVelRollDn * motionRate)
		{
			acceleration = -maxVelRollDn * motionRate;
		}
		else if(acceleration >= 0 && acceleration > maxVelRollUp * motionRate)
		{
			acceleration = maxVelRollUp * motionRate;
		}

		velocity_roll[0] += acceleration * motionRate;
		
		if(velocity_roll[0] < 0 && velocity_roll[0] < -maxVelRollDn * motionRate)
		{
			velocity_roll[0] = -maxVelRollDn * motionRate;
		}
		else if(velocity_roll[0] >= 0 && velocity_roll[0] > maxVelRollUp * motionRate)
		{
			velocity_roll[0] = maxVelRollUp * motionRate;
		}
		if(acceleration != 0)velocity_roll[0] *= 1.025F;
	}
	
	public void getDamage(int damage, Aircraft giver, String weaponName)
	{
		HP -= damage;
		if(HP <= 0)
		{
			isPushing = false;
			if(isAlive)
			{
				lockingPriority = 0;
				visible = false;
				game.addKillTip(giver, this, weaponName);
				++dead;
				++giver.killed;
				new ExplosionMaker(location, 20, (short)75, 0.075F, 0.2F, effects);
				respwanAtTime = System.currentTimeMillis()/1000 + game.respawnTime;
			}
			isAlive = false;
		}
	}
	
	public void control_acc(float acc) //in a game frap
	{
		resistanceRate_current = resistanceRate_normal;
		control_stick_acc += acc;
		if(control_stick_acc > maxShift)
			control_stick_acc = maxShift;
	}
	
	public void control_push() //in a game frap
	{
		if(pushTimeLeft > maxPushTime / 3)
		{
			isPushing = true;
			resistanceRate_current = resistanceRate_normal;
		}
	}
	
	public void control_stop_pushing()
	{
		isPushing = false;
	}
	
	public void control_dec() //in a game frap
	{
		control_stick_acc -= acc_shift * 2;
		if(control_stick_acc < 0.0F)
			control_stick_acc = 0.0F;
	}
	
	public void control_acc() //in a game frap
	{
		//isAccelerating = true;
		control_acc(acc_shift);
	}
	
	public void control_brk() //in a game frap
	{
		resistanceRate_current = resistanceRate_breaking;
		control_dec();
	}
	
	public void control_stop_breaking()
	{
		resistanceRate_current = resistanceRate_normal;
	}
	
	public void control_roll_lr(float acceleration, float limit)
	{
		acceleration /= GraphicUtils.min(GraphicUtils.pow(3.0F, GraphicUtils.abs(acceleration)), 3.0F);
		velocity_roll[2] += acceleration * motionRate * limit;
		if(GraphicUtils.abs(velocity_roll[2]) > maxVelRollLR * motionRate)
		{
			if(velocity_roll[2] < 0)
				velocity_roll[2] = -maxVelRollLR * motionRate;
			else velocity_roll[2] = maxVelRollLR * motionRate;
		}
	}
	
	public void control_roll_lr(float acceleration)
	{
		acceleration /= GraphicUtils.min(GraphicUtils.pow(3.0F, GraphicUtils.abs(acceleration)), 3.0F);
		if(acceleration < 0 && acceleration < -maxVelRollDn * motionRate)
		{
			acceleration = -maxVelRollDn * motionRate;
		}
		else if(acceleration >= 0 && acceleration > maxVelRollUp * motionRate)
		{
			acceleration = maxVelRollUp * motionRate;
		}
		velocity_roll[2] += acceleration * motionRate;
		if(GraphicUtils.abs(velocity_roll[2]) > maxVelRollLR * motionRate)
		{
			if(velocity_roll[2] < 0)
				velocity_roll[2] = -maxVelRollLR * motionRate;
			else velocity_roll[2] = maxVelRollLR * motionRate;
		}
		
		if(acceleration != 0)velocity_roll[2] *= 1.0375F;
	}
	
	public void control_turn_lr(float acceleration, float limit, float maxLimit)
	{
		velocity_roll[1] += acceleration * motionRate * limit;
		if(GraphicUtils.abs(velocity_roll[1]) > maxVelTurnLR * motionRate * maxLimit)
		{
			if(velocity_roll[1] < 0)
				velocity_roll[1] = -maxVelTurnLR * motionRate * maxLimit;
			else velocity_roll[1] = maxVelTurnLR * motionRate * maxLimit;
		}
	}
	
	public void control_turn_lr(float acceleration)
	{
		if(acceleration < 0 && acceleration < -maxVelRollDn * motionRate)
		{
			acceleration = -maxVelRollDn * motionRate;
		}
		else if(acceleration >= 0 && acceleration > maxVelRollUp * motionRate)
		{
			acceleration = maxVelRollUp * motionRate;
		}
		velocity_roll[1] += acceleration * motionRate;
		if(GraphicUtils.abs(velocity_roll[1]) > maxVelTurnLR * motionRate)
		{
			if(velocity_roll[1] < 0)
				velocity_roll[1] = -maxVelTurnLR * motionRate;
			else velocity_roll[1] = maxVelTurnLR * motionRate;
		}
		
		if(acceleration != 0)velocity_roll[1] *= 1.025F;
	}
	
	public void makeDecoy()
	{
		if(decoyMagazineLeft > 0 && isAlive)
		{
			new DecoyMaker
			(
				camp, location, roll_angle, speed, 64, 
				300, 0.04F, 0.125F, 
				addWatingQueue, deleteQue, effects
			);

			if(--decoyMagazineLeft == 0)
				decoyReloadingTimeLeft = decoyReloadingTime;
		}
	}
	
	public void cannonOpenFire()
	{
		if(cannonMagazineLeft > 0 && isAlive)
			isCannonFiring = true;
	}
	
	public void cannonStopFiring()
	{
		isCannonFiring = false;
	}
	
	public Missile missileOpenFire(boolean cameraTrace, Aircraft target)
	{
		if(missileMagazineLeft > 0 && isAlive)
		{
			cannonLocation[0] = 70;
			cannonLocation[1] = 0;
			cannonLocation[2] = 0;
			
			if(missileFlg % 2 == 0)
				cannonLocation[1] += 200;
			else cannonLocation[1] -= 200;
			if(missileFlg < 2)
				cannonLocation[0] += 180;
			else cannonLocation[0] -= 60;
			
			getXYZ_afterRolling
			(
				cannonLocation[0], cannonLocation[1], cannonLocation[2],
				roll_angle[0],	   roll_angle[1],	  roll_angle[2],
				
				cannonLocation
			);
			
			cannonLocation[0] += location[0];
			cannonLocation[1] += location[1];
			cannonLocation[2] += location[2];
			
			Missile m;
			
			if(cameraTrace)
			{
				m = new Missile
				(
					1280, speed/2+5, 512, resistanceRate_normal, 
					cannonLocation, roll_angle, 20, 512, 3.0F, this, target, mainCamera
				);
			}
			else
			{
				m = new Missile
				(
					1280, speed/2+5, 512, resistanceRate_normal, 
					cannonLocation, roll_angle, 20, 512, 3.0F, this, target, null
				);
			}
			
			fired.add(m);
			
			colorFlash(255, 255, 255, 0, 64, 96, 6);
			if(++missileFlg == 4)
			    missileFlg = 0;

			if(--missileMagazineLeft == 0)
				missileReloadingTimeLeft = missileReloadingTime;
			
			return m;
		}
		else return null;
	}
	
	public void colorFlash
	(
		int R_Fore, int G_Fore, int B_Fore, 
		int R_Back, int G_Back, int B_Back, 
		int time
	)
	{
		game.colorFlash(R_Fore, G_Fore, B_Fore, R_Back, G_Back, B_Back, time, this);
	}
	
	public void wingsEffectRun()
	{
		effectMakingLocation[0][0] = 120;
		effectMakingLocation[0][1] = 320;
		effectMakingLocation[0][2] = -240;
		
		effectMakingLocation[1][0] = 120;
		effectMakingLocation[1][1] = -320;
		effectMakingLocation[1][2] = -240;
		
		getXYZ_afterRolling
		(
			effectMakingLocation[0][0], 
			effectMakingLocation[0][1], 
			effectMakingLocation[0][2],
			roll_angle[0],
			roll_angle[1],
			roll_angle[2],
			effectMakingLocation[0]
		);
		
		getXYZ_afterRolling
		(
			effectMakingLocation[1][0], 
			effectMakingLocation[1][1], 
			effectMakingLocation[1][2],
			roll_angle[0],
			roll_angle[1],
			roll_angle[2],
			effectMakingLocation[1]
		);
		
		effectMakingLocation[0][0] += location[0];
		effectMakingLocation[0][1] += location[1];
		effectMakingLocation[0][2] += location[2];
		
		effectMakingLocation[1][0] += location[0];
		effectMakingLocation[1][1] += location[1];
		effectMakingLocation[1][2] += location[2];
		
		effects.add(new EngineFlame(effectMakingLocation[0], 100 + (int)(50 * GraphicUtils.random()), '*'));
		effects.add(new EngineFlame(effectMakingLocation[1], 100 + (int)(50 * GraphicUtils.random()), '*'));
	}
	
	public float[] getCurrentSpeedVector() {
        return speedVector;
    }

    public static float vectorLength(float xyz[]) {
        float x2 = xyz[0] * xyz[0];
        float y2 = xyz[1] * xyz[1];
        float z2 = xyz[2] * xyz[2];
        
        return GraphicUtils.sqrt(x2 + y2 + z2);
    }
    /*
	public static float[] toDirectionVector(float speedVector[]) {
	    float x = speedVector[0], y = speedVector[1], z = speedVector[2];
	    float length = 
	    
	    speedVector[0] = x / max;
	    speedVector[1] = y / max;
	    speedVector[2] = z / max;
	    
	    return speedVector;
	}
	*/
	
	
    public void doMotion()
	{
		//------------[go street]------------
        float r1 = GraphicUtils.toRadians(roll_angle[1]);
        float r2 = GraphicUtils.toRadians(roll_angle[0]);
		float t  = GraphicUtils.cos(r1) * speed;
		speedVector[0] = GraphicUtils.sin(r1) * speed;
		speedVector[1] = GraphicUtils.sin(r2) * t;
		speedVector[2] = GraphicUtils.cos(r2) * t;
		
		location[0]	-= speedVector[0];
		location[1]	+= speedVector[1];
		location[2]	+= speedVector[2];
		//--------------[motion]-------------
		roll_up_dn(velocity_roll[0]);
		turn_lr(velocity_roll[1]);
		roll_lr(velocity_roll[2]);
		//-----------------------------------
		engine_rpm = getCurrentRPM(max_rpm, control_stick_acc);
		float F   = getCurrentForce(maxAccForce, max_rpm, engine_rpm);
		
		if(isPushing)
		{
			F += pushPower;
			if((pushTimeLeft -= 2) <= 0)
				isPushing = false;
		}
		else if(pushTimeLeft < maxPushTime)++pushTimeLeft;
		
		if(velocity_roll[0] != 0.0F)
			velocity_roll[0] /= 1.050F;
		if(velocity_roll[1] != 0.0F)
			velocity_roll[1] /= 1.050F;
		if(velocity_roll[2] != 0.0F)
			velocity_roll[2] /= 1.075F;
		
		speed += F/mess;
		speed -= speed * resistanceRate_current;
		
		if(speed > maxSpeed)
			speed = maxSpeed;
		
		if(speed < minStableSpeed)
		{
			motionRate = speed * 0.8F / minStableSpeed;
			if(location[0] < 0)
				location[0] += CharTimeSpace.g * (1.0F - speed / minStableSpeed);
			else location[0] = 0;
		}
		else 
		{
			motionRate = 0.8F;
			if(location[0] > 0) location[0] = 0;
			if (
				GraphicUtils.abs(velocity_roll[0]) > 0.6F * maxVelRollUp ||
				GraphicUtils.abs(velocity_roll[2]) > 0.6F * maxVelRollLR
			)	wingsEffectRun();
		}
	}
	
	public void weaponSystemRun()
	{
		if(isCannonFiring)
		{
			if(cannonFireLoadTime <= 0)
			{
				if(cannonMagazineLeft > 0)
				{
					/*
					cannonLocation[0] = 35;
					cannonLocation[1] = 0;
					cannonLocation[2] = 0;
					
					if(cannonGunFlg % 2 == 0)
						cannonLocation[1] += 50;
					else cannonLocation[1] -= 50;
					if(cannonGunFlg < 2)
						cannonLocation[0] += 25;
					else cannonLocation[0] -= 25;
					*/
					cannonLocation[0] = 70;
					cannonLocation[1] = 0;
					cannonLocation[2] = 0;
					
					if(cannonGunFlg % 2 == 0)
						cannonLocation[1] += 180;
					else cannonLocation[1] -= 180;
					if(cannonGunFlg < 2)
						cannonLocation[0] += 120;
					else cannonLocation[0] -= 120;
					
					getXYZ_afterRolling
					(
						cannonLocation[0], cannonLocation[1], cannonLocation[2],
						roll_angle[0],	   roll_angle[1],	  roll_angle[2],
						
						cannonLocation
					);
					
					cannonLocation[0] += location[0];
					cannonLocation[1] += location[1];
					cannonLocation[2] += location[2];
					//---旋转结束---
					/*
					 * v0 = 400 + speed = v
					 * v1 = 50
					 *  r = 0.00175
					 *  Q : t
					 *  t = (v1 - v0) / a
					 *  
					 */
					fired.add
					(
						new CannonAmmo
						(
							400, camp, 400 + speed, 0.00175F, 
							cannonLocation, roll_angle, aircrafts, effects, this
						)
					);
					colorFlash(255, 224, 128, 0, 0, 0, 3);
					cannonFireLoadTime = 2;
					
					if(++cannonGunFlg == 4)
						cannonGunFlg = 0;
					
					if(--cannonMagazineLeft == 0)
						cannonReloadingTimeLeft = cannonReloadingTime;
				}
				else isCannonFiring = false;
			}
			else --cannonFireLoadTime;
		}
		
		if(cannonReloadingTimeLeft > 0)
		{
			if(--cannonReloadingTimeLeft == 0)
				cannonMagazineLeft = cannonMagazine;
		}
		
		if(missileReloadingTimeLeft > 0)
		{
			if(--missileReloadingTimeLeft == 0) {
				missileMagazineLeft = missileMagazine;
				lockingPriority = lockingPriority_backup;
			}
		}
		
		if(decoyReloadingTimeLeft > 0)
		{
			if(--decoyReloadingTimeLeft == 0)
				decoyMagazineLeft = decoyMagazine;
		}
	}
	
	public void playersCameraManage()
	{
		if(cameraLocationFlag == 0)
		{
			float t1, t2, t3;
			/*
			cameraLocation[0] = -50;
			cameraLocation[1] = 0;
			cameraLocation[2] =  -150;
			*/

			cameraLocation[0] = -240;
			cameraLocation[1] = 0;
			cameraLocation[2] = -960;
			
			if(cameraRollAngle[0] < 0)
			{
				if((-roll_angle[0]) - cameraRollAngle[0] < 180)
					t1 = -roll_angle[0] - cameraRollAngle[0];
				else
					t1 = -roll_angle[0] - cameraRollAngle[0] - 360;
			}
			else if(cameraRollAngle[0] > 0)
			{
				if(cameraRollAngle[0] - (-roll_angle[0]) < 180)
					t1 = -roll_angle[0] - cameraRollAngle[0];
				else
					t1 = -roll_angle[0] - cameraRollAngle[0] + 360;
			}
			else t1 = -roll_angle[0];
			
			if(cameraRollAngle[1] < 0)
			{
				if((-roll_angle[1]) - cameraRollAngle[1] < 180)
					t2 = -roll_angle[1] - cameraRollAngle[1];
				else
					t2 = -roll_angle[1] - cameraRollAngle[1] - 360;
			}
			else if(cameraRollAngle[1] > 0)
			{
				if(cameraRollAngle[1] - (-roll_angle[1]) < 180)
					t2 = -roll_angle[1] - cameraRollAngle[1];
				else
					t2 = -roll_angle[1] - cameraRollAngle[1] + 360;
			}
			else t2 = -roll_angle[1];
			
			if(cameraRollAngle[2] < 0)
			{
				if((-roll_angle[2]) - cameraRollAngle[2] < 180)
					t3 = -roll_angle[2] - cameraRollAngle[2];
				else
					t3 = -roll_angle[2] - cameraRollAngle[2] - 360;
			}
			else if(cameraRollAngle[2] > 0)
			{
				if(cameraRollAngle[2] - (-roll_angle[2]) < 180)
					t3 = -roll_angle[2] - cameraRollAngle[2];
				else
					t3 = -roll_angle[2] - cameraRollAngle[2] + 360;
			}
			else t3 = -roll_angle[2];
			
			t1 /= 6;
			t2 /= 6;
			t3 /= 6;
			
			cameraRollAngle[0] = (cameraRollAngle[0] + t1) % 360;
			cameraRollAngle[1] = (cameraRollAngle[1] + t2) % 360;
			cameraRollAngle[2] = (cameraRollAngle[2] + t3) % 360;
		}
		else
		{
			if(cameraLocationFlag > 1)
				cameraLocationFlag = 0;
			
			cameraLocation[0] = -96;
			cameraLocation[1] = 0;
			cameraLocation[2] = -320;
			
			cameraRollAngle[0] = -roll_angle[0];
			cameraRollAngle[1] = -roll_angle[1];
			cameraRollAngle[2] = -roll_angle[2];
		}
		
		getXYZ_afterRolling
		(
			cameraLocation[0], cameraLocation[1], cameraLocation[2],
			roll_angle[0],	   roll_angle[1],	  roll_angle[2],
			
			cameraLocation
		);

		cameraLocation[0] += location[0];
		cameraLocation[1] += location[1];
		cameraLocation[2] += location[2];
		
		if(mainCamera.location != cameraLocation) {
		    fov_current = 9.37F;
		} else if(isPushing) {
		    /*
			fov_current += 0.005;
			if(fov_current > fov_pushing)
				fov_current = fov_pushing;*/
			fov_current += (fov_pushing / fov_current - 1) * 2;
		} else {
			if(isCannonFiring) {
			    fov_current += (fov_gunFiring / fov_current - 1);
			    /*
				if(fov_current > fov_gunFiring) fov_current -= 0.08;
				else fov_current = fov_gunFiring;*/
			} else switch(cameraLocationFlag) {
				case 0:
					fov_current += (fov_3thPerson / fov_current - 1) / 2;
					/*
					if(fov_current > fov_3thPerson)      fov_current -= 0.005;
					else if(fov_current < fov_3thPerson) fov_current += 0.005;
					*/
					break;
				case 1:
					fov_current += (fov_1stPerson / fov_current - 1) / 2;
					/*
					if(fov_current > fov_1stPerson)      fov_current -= 0.005;
					else if(fov_current < fov_1stPerson) fov_current += 0.005;
					*/
					break;
			}
			/* x
			 * = y + ((x - y) / 2)
			 * = y + x/2 - y/2
			 * = x/2 + y/2
			 * = (x + y) / 2
			 */
		}
		mainCamera.setFOV(fov_current);
	}
	
	public void defaultGo()
	{
		if(HP <= 0)
		{
			if(System.currentTimeMillis() / 1000 >= respwanAtTime)	
				randomRespawn();
			
			return;
		}

		doMotion();
		
		weaponSystemRun();
		
	}
	
	@Override
	public void go()
	{
		defaultGo();
		
		if(--lockingLife <= 0)
		{
			lockedByEnemy = false;
			locked_By = null;
		}

		playersCameraManage();
	}
	
	@Override
	public void run()
	{
		go();
	}
	
	public void randomRespawn()
	{
		lockingPriority = 0;
		setLocation
		(
			0,
			game.mainCamera.location[1] + GraphicUtils.random() * game.mainCamera.maxSearchingRange * (GraphicUtils.random()>0.5F? -1 : 1),
			game.mainCamera.location[2] + GraphicUtils.random() * game.mainCamera.maxSearchingRange * (GraphicUtils.random()>0.5F? -1 : 1)
		);
		
		setRollAngle(GraphicUtils.random() * 360, 0, 0);
		
		cameraRollAngle[0] = -roll_angle[0];
		cameraRollAngle[1] = -roll_angle[1];
		cameraRollAngle[2] = -roll_angle[2];
		
		speed = 0;
		missileMagazineLeft	= 0;
		missileReloadingTimeLeft = missileReloadingTime;
		
		cannonMagazineLeft	= cannonMagazine;
		cannonReloadingTimeLeft	= 0;
		
		decoyMagazineLeft	= decoyMagazine;
		decoyReloadingTimeLeft	= 0;
		
		velocity_roll[0] = 0.0F;
		velocity_roll[1] = 0.0F;
		velocity_roll[2] = 0.0F;
		
		control_stick_acc	= 0.0F;
		
		HP = 100;
		pushTimeLeft = maxPushTime;
		
		visible = true;
		isAlive = true;
	}
	
	public void setID(String id) {
	    this.ID = new String(id);
	}

    public String getID() {
        return this.ID;
    }
    
	public void pollBack()
	{
		/*
		setLocation
		(
			GraphicUtils.abs(location[0]) > game.visibility? -1500 : -GraphicUtils.abs(location[0]),
			game.mainCamera.location[1] + GraphicUtils.random() * game.mainCamera.maxSearchingRange * (GraphicUtils.random()>0.5? -1 : 1),
			game.mainCamera.location[2] + GraphicUtils.random() * game.mainCamera.maxSearchingRange * (GraphicUtils.random()>0.5? -1 : 1)
		);*/
		HP = 0;
	}
	
	@Override
	public String toString()
	{
		return ID;
	}
}
