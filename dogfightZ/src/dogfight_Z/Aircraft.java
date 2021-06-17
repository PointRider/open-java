package dogfight_Z;

import java.util.HashSet;
import java.util.LinkedList;
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

public class Aircraft extends CharMessObject
{
	public String ID;
	public short HP;
	public short camp;
	public short lockingPriority;
	public short lockingPriority_backup;
	public Game	 game;
	public LinkedList<ThreeDs> deleteQue;
	public HashSet<ThreeDs>	   aircrafts;
	public boolean isPlayer;
	
	public double speed;
	public double maxSpeed;				//限制最高速度
	public double maxAccForce;			//最大推力(与mess一起决定最大加速度)
	
	public double control_stick_acc;	//当前引擎档位(通过getCurrentForce函数获得当前引擎推力)HashSet<ThreeDs>
	public static final double maxShift = 16;	//最大操纵杆档位
	
	public double engine_rpm;			//引擎转速
	public double max_rpm;				//最大转速
	public double pushPower;			//当前加力燃烧推力
	public double maxPushTime;			//最大加力燃烧时间
	public double pushTimeLeft;			//加力燃烧剩余时间
	
	public double acc_shift;			//上拨操纵杆速度(固定)
	public double maxdeceleration;		//最大减速度(打开减速板)
	
	public double resistanceRate_current;	//当前空气阻力系数
	public double resistanceRate_normal;	//无动作时空气阻力系数
	public double resistanceRate_breaking;	//打开减速板时的空气阻力系数
	
	public double minStableSpeed;		//最小稳定速度(达到最大升力)
	public double maxVelRollUp;			//最大向上翻滚速度
	public double maxVelRollDn;			//最大向下翻滚速度
	public double maxVelRollLR;			//最大左右翻滚速度
	public double maxVelTurnLR;			//最大左右水平转向速度

	public double tmp_double[];
	//public double cannonRollAngle[];
	public double cameraRollAngle[];
	public double cameraLocation[];
	//public double cameraRollAngle_rev[];
	public double cannonLocation[];
	public boolean isAlive;
	
	public long respwanAtTime;
			
	public PriorityQueue<Dynamic> fired;
	public PriorityQueue<Dynamic> effects;
	public short 	cameraLocationFlag;
	
	//---------[state]----------
	public boolean  isPushing;
	public boolean  isCannonFiring;
	public int		cannonFireLoadTime;
	public double   motionRate;
	protected int	cannonGunFlg;
	public CharFrapsCamera mainCamera;
	//--------------------------
	public Aircraft locked_By;
	public int		lockingLife;
	public boolean	lockedByEnemy;
	//--------------------------
	public LinkedList<ThreeDs> addWatingQueue;
	//---------[ammo]-----------
	public int		missileMagazine;				//单次挂载最大弹容量
	public int		missileMagazineLeft;			//当前挂载导弹余量
	public short	missileReloadingTime;			//重新挂载时间
	public short	missileReloadingTimeLeft;		//重新挂载时间剩余
	public int		cannonMagazine;					//单次装填最大弹容量
	public int		cannonMagazineLeft;				//当前装填炮弹余量
	public short	cannonReloadingTime;			//重新装填时间
	public short	cannonReloadingTimeLeft;		//重新装填时间剩余
	public int		decoyMagazine;					//单次诱饵弹装填最大弹容量
	public int		decoyMagazineLeft;				//当前诱饵弹装填炮弹余量
	public short	decoyReloadingTime;				//诱饵弹装填时间
	public short	decoyReloadingTimeLeft;			//诱饵弹装填时间剩余
	public double	effectMakingLocation[][];
	//--------------------------
	public int killed;
	public int dead;
	
	public double fov_1stPerson;
	public double fov_3thPerson;
	public double fov_pushing;
	public double fov_current;
	
	public static double getCurrentForce(double maxAccForce, double max_rpm, double rpm)	//
	{
		double result = ((-1 / ( rpm/max_rpm*4.75-5 )) - 0.2) / 3.8 * maxAccForce;
		if(result < 0)
			return 0;
		else return result;
	}
	
	public static double getCurrentRPM(double max_rpm, double shift)	//ln(shift) / ln(max_rpm)
	{
		if(shift>0.0 && maxShift>0.0)
		{
			double currentForceRate = Math.log(shift) / Math.log(maxShift);
			if(currentForceRate > 0.0)
				return currentForceRate * max_rpm;
			else return 0.0;
		}
		else return 0.0;
	}

	public Aircraft
	(
		Game theGame,
		String modelFile, 
		double Mess, 
		short Camp,
		PriorityQueue<Dynamic> firedAmmo, 
		PriorityQueue<Dynamic> Effects, 
		LinkedList<ThreeDs>	   delete_que,
		LinkedList<ThreeDs>	   add_que,
		HashSet<ThreeDs>	   Aircrafts,
		CharFrapsCamera		   MainCamera, 
		String id
	)
	{
		super(modelFile, Mess);
		killed				= dead = 0;
		fov_1stPerson		= 2.6;
		fov_3thPerson		= 2.6;
		fov_pushing			= 2.8;
		fov_current			= 2.6;
		respwanAtTime		= 0;
		specialDisplay		= '@';
		ID					= id;
		mainCamera			= MainCamera;
		//cannonRollAngle		= new double[3];
		cameraRollAngle		= new double[3];
		tmp_double			= new double[3];
		cannonLocation		= new double[3];
		cameraLocation		= new double[3];
		effectMakingLocation= new double[2][3];
		cameraLocationFlag	= 0;
		addWatingQueue		= add_que;
		lockingPriority		= lockingPriority_backup = 1;
		camp				= Camp;
		HP					= 100;
		max_rpm				= 14000;
		maxSpeed			= 300;
		maxAccForce			= 6000;
		maxPushTime			= 1000;
		pushPower 			= 1500;
		pushTimeLeft		= 1000;
		control_stick_acc	= 0.0;
		acc_shift			= 0.05;
		maxdeceleration		= 1.0;
		minStableSpeed		= 30.0;
		resistanceRate_current	= 0.00500;
		resistanceRate_normal	= 0.00500;
		resistanceRate_breaking = 0.00575;
		maxVelTurnLR		= 5.0;
		maxVelRollLR		= 9.0;//
		maxVelRollUp		= 9.0;
		maxVelRollDn		= 9.0;
		motionRate			= 0.0;
		cannonFireLoadTime	= 0;
		isPushing			= false;
		isPlayer			= true;
		isAlive				= true;
		cannonGunFlg		= 1;
		game				= theGame;
		locked_By			= null;
		lockingLife			= 0;
		fired				= firedAmmo;
		effects				= Effects;
		deleteQue			= delete_que;
		aircrafts			= Aircrafts;
		
		missileMagazine		= 4;
		missileMagazineLeft	= 0;
		missileReloadingTime	= 500;
		missileReloadingTimeLeft= 500;
		
		cannonMagazine		= 500;
		cannonMagazineLeft	= 0;
		cannonReloadingTime		= 250;
		cannonReloadingTimeLeft	= 250;
		
		decoyMagazine		= 1;
		decoyMagazineLeft	= 1;
		decoyReloadingTime		= 500;
		decoyReloadingTimeLeft	= 500;
		
		cameraRollAngle[0] = -roll_angle[0];
		cameraRollAngle[1] = -roll_angle[1];
		cameraRollAngle[2] = -roll_angle[2];
	}
	
	
	public void getRelativePosition_XY(double y, double z, double result[])
	{
		double Y, Z;
		
		y -= location[1];
		z -= location[2];
		
		Z = Math.cos(Math.atan(y/z)+Math.toRadians(cameraRollAngle[0]))*Math.sqrt(z*z+y*y);
		Y = Math.sin(Math.atan(y/z)+Math.toRadians(cameraRollAngle[0]))*Math.sqrt(z*z+y*y);
		
		y = (z<0)?(-Y):Y;
		z = (z<0)?(-Z):Z;
		
		result[0] = y;
		result[1] = z;
	}
	
	public Aircraft
	(
		Game game,
		String modelFile,
		double Mess, 
		short Camp,
		PriorityQueue<Dynamic> firedAmmo, 
		PriorityQueue<Dynamic> Effects,  
		LinkedList<ThreeDs>	   delete_que,
		LinkedList<ThreeDs>	   add_que,
		HashSet<ThreeDs>	   Aircrafts,
		CharFrapsCamera		   MainCamera
	)
	{
		this(game, modelFile, Mess, Camp, firedAmmo, Effects, delete_que, add_que, Aircrafts, MainCamera, "Me");
	}
	
	public void warning(Aircraft source)
	{
		locked_By			= source;
		lockedByEnemy		= true;
		lockingLife			= 20;
	}
	
	public void roll_up_dn(double angleVel)
	{
		if(Math.abs(roll_angle[1]) > 90.0)	roll_angle[0] += Math.sin(Math.toRadians(roll_angle[2])) * angleVel / 6.4;
			else							roll_angle[0] -= Math.sin(Math.toRadians(roll_angle[2])) * angleVel / 6.4;
		
		roll_angle[1] += Math.cos(Math.toRadians(roll_angle[2])) * angleVel / 6.4;
			
		roll_angle[0] %= 360;//
		//roll_angle[1] %= 360;//
		
		if(roll_angle[1] > 180) roll_angle[1] = -180.0 + (roll_angle[1]-180.0);
		else if(roll_angle[1] < -180) roll_angle[1] = 180.0 + (roll_angle[1]+180.0);
	}
	
	public void turn_lr(double angleVel)
	{
		if(Math.abs(roll_angle[1]) > 90.0)	roll_angle[0] -= Math.cos(Math.toRadians(roll_angle[2])) * angleVel / 6.4;
			else							roll_angle[0] += Math.cos(Math.toRadians(roll_angle[2])) * angleVel / 6.4;

		roll_angle[1] += Math.sin(Math.toRadians(roll_angle[2])) * angleVel / 6.4;
		
		roll_angle[0] %= 360;//
		//roll_angle[1] %= 360;//
		
		if(roll_angle[1] > 180) roll_angle[1] = -180.0 + (roll_angle[1]-180.0);
		else if(roll_angle[1] < -180) roll_angle[1] = 180.0 + (roll_angle[1]+180.0);
	}
	
	public void roll_lr(double angleVel)
	{
		roll_angle[2] = (roll_angle[2] + (angleVel/1.8)) % 360;
	}
	
	public void control_roll_up_dn(double acceleration, double limit, double maxLimit)
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
	
	public void control_roll_up_dn(double acceleration)
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
		if(acceleration != 0)velocity_roll[0] *= 1.025;
	}
	
	public void getDamage(int damage, Aircraft giver, String weaponName)
	{
		HP -= damage;
		if(HP <= 0)
		{
			if(isAlive)
			{
				lockingPriority = 0;
				visible = false;
				game.addKillTip(giver, this, weaponName);
				++dead;
				++giver.killed;
				new ExplosionMaker(location, 20, (short)75, 0.075, 0.2, effects);
				respwanAtTime = System.currentTimeMillis()/1000 + game.respawnTime;
			}
			isAlive = false;
		}
	}
	
	public void control_acc(double acc) //in a game frap
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
		if(control_stick_acc < 0.0)
			control_stick_acc = 0.0;
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
	
	public void control_roll_lr(double acceleration, double limit)
	{
		acceleration /= Math.min(Math.pow(3.0, Math.abs(acceleration)), 3.0);
		velocity_roll[2] += acceleration * motionRate * limit;
		if(Math.abs(velocity_roll[2]) > maxVelRollLR * motionRate)
		{
			if(velocity_roll[2] < 0)
				velocity_roll[2] = -maxVelRollLR * motionRate;
			else velocity_roll[2] = maxVelRollLR * motionRate;
		}
	}
	
	public void control_roll_lr(double acceleration)
	{
		acceleration /= Math.min(Math.pow(3.0, Math.abs(acceleration)), 3.0);
		if(acceleration < 0 && acceleration < -maxVelRollDn * motionRate)
		{
			acceleration = -maxVelRollDn * motionRate;
		}
		else if(acceleration >= 0 && acceleration > maxVelRollUp * motionRate)
		{
			acceleration = maxVelRollUp * motionRate;
		}
		velocity_roll[2] += acceleration * motionRate;
		if(Math.abs(velocity_roll[2]) > maxVelRollLR * motionRate)
		{
			if(velocity_roll[2] < 0)
				velocity_roll[2] = -maxVelRollLR * motionRate;
			else velocity_roll[2] = maxVelRollLR * motionRate;
		}
		
		if(acceleration != 0)velocity_roll[2] *= 1.0375;
	}
	
	public void control_turn_lr(double acceleration, double limit, double maxLimit)
	{
		velocity_roll[1] += acceleration * motionRate * limit;
		if(Math.abs(velocity_roll[1]) > maxVelTurnLR * motionRate * maxLimit)
		{
			if(velocity_roll[1] < 0)
				velocity_roll[1] = -maxVelTurnLR * motionRate * maxLimit;
			else velocity_roll[1] = maxVelTurnLR * motionRate * maxLimit;
		}
	}
	
	public void control_turn_lr(double acceleration)
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
		if(Math.abs(velocity_roll[1]) > maxVelTurnLR * motionRate)
		{
			if(velocity_roll[1] < 0)
				velocity_roll[1] = -maxVelTurnLR * motionRate;
			else velocity_roll[1] = maxVelTurnLR * motionRate;
		}
		
		if(acceleration != 0)velocity_roll[1] *= 1.025;
	}
	
	public void makeDecoy()
	{
		if(decoyMagazineLeft > 0 && isAlive)
		{
			new DecoyMaker
			(
				camp, location, roll_angle, speed, 10, 
				(short)300, 0.02, 0.125, 
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
	
	public Missile missileOpenFire(boolean cameraTrace, Aircraft target)
	{
		if(missileMagazineLeft > 0 && isAlive)
		{
			cannonLocation[0] = 35;
			cannonLocation[1] = 0;
			cannonLocation[2] = 0;
			
			if(cannonGunFlg % 2 == 0)
				cannonLocation[1] += 75;
			else cannonLocation[1] -= 75;
			if(cannonGunFlg < 2)
				cannonLocation[0] += 10;
			else cannonLocation[0] -= 10;
			
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
					(short)1280, speed/2+5, 256, resistanceRate_normal, 
					cannonLocation, roll_angle, 20, 512, 3.0, this, target, mainCamera
				);
			}
			else
			{
				m = new Missile
				(
					(short)1280, speed/2+5, 256, resistanceRate_normal, 
					cannonLocation, roll_angle, 20, 512, 3.0, this, target, null
				);
			}
			
			fired.add(m);
	
			
			colorFlash(255, 255, 255, 0, 64, 96, (short)6);
			if(++cannonGunFlg == 4)
				cannonGunFlg = 0;

			if(--missileMagazineLeft == 0)
				missileReloadingTimeLeft = missileReloadingTime;
			
			return m;
		}
		else return null;
	}
	
	public void cannonStopFiring()
	{
		isCannonFiring = false;
	}
	
	public void colorFlash
	(
		int R_Fore, int G_Fore, int B_Fore, 
		int R_Back, int G_Back, int B_Back, 
		short time
	)
	{
		game.colorFlash(R_Fore, G_Fore, B_Fore, R_Back, G_Back, B_Back, time, this);
	}
	
	public void wingsEffectRun()
	{
		effectMakingLocation[0][0] = 20;
		effectMakingLocation[0][1] = 50;
		effectMakingLocation[0][2] = 0;
		
		effectMakingLocation[1][0] = 20;
		effectMakingLocation[1][1] = -50;
		effectMakingLocation[1][2] = 0;
		
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
		
		effects.add(new EngineFlame(effectMakingLocation[0], (short)50, '*'));
		effects.add(new EngineFlame(effectMakingLocation[1], (short)50, '*'));
	}
	
	public void doMotion()
	{
		//------------[go street]------------
		double x, y, z, t, tmp = Math.toRadians(roll_angle[0]);
		double r1 = Math.toRadians(roll_angle[1]), r2 = Math.cos(tmp);
		t = Math.cos(r1) * speed;
		x = Math.tan(r1) * t;
		y = Math.sin(tmp) * t;
		z = r2 * t;
		
		location[0]	-= x;
		location[1]	+= y;
		location[2]	+= z;
		//--------------[motion]-------------
		roll_up_dn(velocity_roll[0]);
		turn_lr(velocity_roll[1]);
		roll_lr(velocity_roll[2]);
		//-----------------------------------
		engine_rpm	=  getCurrentRPM(max_rpm, control_stick_acc);
		double F	=  getCurrentForce(maxAccForce, max_rpm, engine_rpm);
		
		if(isPushing)
		{
			F += pushPower;
			if((pushTimeLeft -= 2) <= 0)
				isPushing = false;
		}
		else if(pushTimeLeft < maxPushTime)++pushTimeLeft;
		
		if(velocity_roll[0] != 0.0)
			velocity_roll[0] /= 1.050;
		if(velocity_roll[1] != 0.0)
			velocity_roll[1] /= 1.050;
		if(velocity_roll[2] != 0.0)
			velocity_roll[2] /= 1.075;
		
		speed += F/mess;
		speed -= speed * resistanceRate_current;
		
		if(speed > maxSpeed)
			speed = maxSpeed;
		
		if(speed < minStableSpeed)
		{
			//motionRate = 1;
			motionRate = speed / minStableSpeed * 0.8;
			if(location[0] < 0)
				location[0] += CharTimeSpace.g * (minStableSpeed - speed) / minStableSpeed;
			else location[0] = 0;
		}
		else 
		{
			motionRate = 0.8;
			
			if
			(
				Math.abs(velocity_roll[0]) > 0.7 * maxVelRollUp ||
				Math.abs(velocity_roll[2]) > 0.7 * maxVelRollLR
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
					cannonLocation[0] = 35;
					cannonLocation[1] = 0;
					cannonLocation[2] = 0;
					
					if(cannonGunFlg % 2 == 0)
						cannonLocation[1] += 50;
					else cannonLocation[1] -= 50;
					if(cannonGunFlg < 2)
						cannonLocation[0] += 25;
					else cannonLocation[0] -= 25;
					
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
					
					fired.add
					(
						new CannonAmmo
						(
							(short)600, camp, 50 + speed, resistanceRate_normal, 
							cannonLocation, roll_angle, aircrafts, effects, this
						)
					);
					colorFlash(255, 224, 128, 0, 0, 0, (short)3);
					cannonFireLoadTime = 1;
					
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
			if(--missileReloadingTimeLeft == 0)
				missileMagazineLeft = missileMagazine;
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
			double t1, t2, t3;
			
			cameraLocation[0] = -50;
			cameraLocation[1] = 0;
			cameraLocation[2] =  -150;
			
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
			
			t1 /= 8;
			t2 /= 8;
			t3 /= 8;
			
			cameraRollAngle[0] = (cameraRollAngle[0] + t1) % 360;
			cameraRollAngle[1] = (cameraRollAngle[1] + t2) % 360;
			cameraRollAngle[2] = (cameraRollAngle[2] + t3) % 360;
		}
		else
		{
			if(cameraLocationFlag > 1)
				cameraLocationFlag = 0;
			
			cameraLocation[0] = -9;
			cameraLocation[1] = 0;
			cameraLocation[2] = 1;
			
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
		
		if(isPushing) {
			fov_current += 0.025;
			if(fov_current > fov_pushing)
				fov_current = fov_pushing;
		}
		else
		{
			switch(cameraLocationFlag)
			{
				case 0:
					if(fov_current > fov_3thPerson)
						fov_current -= 0.005;
					else if(fov_current < fov_3thPerson)
						fov_current = fov_3thPerson;
				break;
				case 1:
					if(fov_current > fov_1stPerson)
						fov_current -= 0.005;
					else if(fov_current < fov_1stPerson)
						fov_current = fov_1stPerson;
				break;
			}
			
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
		setLocation
		(
			0,
			game.mainCamera.location[1] + Math.random() * game.mainCamera.maxSearchingRange * (Math.random()>0.5? -1 : 1),
			game.mainCamera.location[2] + Math.random() * game.mainCamera.maxSearchingRange * (Math.random()>0.5? -1 : 1)
		);
		
		setRollAngle(Math.random() * 360, 0, 0);
		
		cameraRollAngle[0] = -roll_angle[0];
		cameraRollAngle[1] = -roll_angle[1];
		cameraRollAngle[2] = -roll_angle[2];
		
		speed = 0;
		missileMagazineLeft	= 0;
		missileReloadingTimeLeft= 500;
		
		cannonMagazineLeft	= 0;
		cannonReloadingTimeLeft	= 250;
		
		decoyMagazineLeft	= 1;
		decoyReloadingTimeLeft	= 500;
		
		velocity_roll[0] = 0.0;
		velocity_roll[1] = 0.0;
		velocity_roll[2] = 0.0;
		
		control_stick_acc	= 0.0;
		
		HP = 100;
		pushTimeLeft = maxPushTime;
		lockingPriority = lockingPriority_backup;
		
		visible = true;
		isAlive = true;
	}
	
	public void pollBack()
	{
		setLocation
		(
			Math.abs(location[0]) > game.visibility? -1500 : -Math.abs(location[0]),
			game.mainCamera.location[1] + Math.random() * game.mainCamera.maxSearchingRange * (Math.random()>0.5? -1 : 1),
			game.mainCamera.location[2] + Math.random() * game.mainCamera.maxSearchingRange * (Math.random()>0.5? -1 : 1)
		);
	}
	
	@Override
	public String toString()
	{
		return ID;
	}
}
