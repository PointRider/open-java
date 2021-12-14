package dogfight_Z;

import dogfight_Z.Ammo.CannonAmmo;
import dogfight_Z.Ammo.Decoy;
import dogfight_Z.Ammo.Missile;
import dogfight_Z.Effects.EngineFlame;
import dogfight_Z.Effects.EngineFlame2;
import dogfight_Z.Effects.Particle;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharMessObject;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.LinkedListZ;

public class Aircraft extends CharMessObject
{
	private String ID;
	private int HP;
	private int camp;
	private int lockingPriority;
	private int lockingPriority_backup;
	
	private GameManagement	     gameManager;
	public  LinkedListZ<ThreeDs>	aircrafts;
	private boolean iAmPlayer;
	
	private float speed;
	private float maxSpeed;				      //限制最高速度
	private float maxAccForce;			      //最大推力(与mess一起决定最大加速度)
	
	private float control_stick_acc;	      //当前引擎档位(通过getCurrentForce函数获得当前引擎推力)HashSet<ThreeDs>
	private static final float maxShift = 32; //最大操纵杆档位
	
	private float engine_rpm;			      //引擎转速
	private float max_rpm;				      //最大转速
	private float pushPower;			      //当前加力燃烧推力
	private float maxPushTime;			      //最大加力燃烧时间
	private float pushTimeLeft;			      //加力燃烧剩余时间
	
	private float acc_shift;			      //上拨操纵杆速度(固定)
	
	private float resistanceRate_current;	  //当前空气阻力系数
	private float resistanceRate_normal;	  //无动作时空气阻力系数
	private float resistanceRate_breaking;	  //打开减速板时的空气阻力系数
	
	private float minStableSpeed;		      //最小稳定速度(达到最大升力)
	private float maxVelRollUp;			      //最大向上翻滚速度
	private float maxVelRollDn;			      //最大向下翻滚速度
	private float maxVelRollLR;			      //最大左右翻滚速度
	private float maxVelTurnLR;			      //最大左右水平转向速度

	private float cameraRollAngle[];
	protected float cameraLocation[];
	protected float cannonLocation[];
	//private boolean isAlive;
	
	private long  respwanAtTime;
	public  int   cameraLocationFlag;
	protected float directionXYZ[];
	
	//---------[state]----------
	public boolean isPushing;
	public boolean isCannonFiring;
	public int     cannonFireLoadTime;
	public float   motionRate;
	protected int  cannonGunFlg;
    protected int  missileFlg;
	public         CharFrapsCamera mainCamera;
	//--------------------------
	public Aircraft locked_By;
	public int		lockingLife;
	public boolean	lockedByEnemy;
    public int      missileLockingLife;
    public boolean  lockedByMissile;
	//--------------------------
	//---------[ammo]-----------
	public int   missileMagazine;			//单次挂载最大弹容量
	public int   missileMagazineLeft;		//当前挂载导弹余量
	public int   missileReloadingTime;	    //重新挂载时间
	public int   missileReloadingTimeLeft;	//重新挂载时间剩余
	public int   cannonMagazine;		    //单次装填最大弹容量
	public int   cannonMagazineLeft;		//当前装填炮弹余量
	public int   cannonReloadingTime;		//重新装填时间
	public int   cannonReloadingTimeLeft;	//重新装填时间剩余
	public int   decoyMagazine;				//单次诱饵弹装填最大弹容量
	public int   decoyMagazineLeft;			//当前诱饵弹装填炮弹余量
	public int   decoyReloadingTime;		//诱饵弹装填时间
	public int   decoyReloadingTimeLeft;	//诱饵弹装填时间剩余
	public float effectMakingLocation[][];
	//--------------------------
	public int killed;
	public int dead;
	
	public float fov_1stPerson;
	public float fov_3thPerson;
	public float fov_pushing;
	public float fov_current;
	public float fov_gunFiring;
	
	public static float getCurrentForce(float maxAccForce, float max_rpm, float rpm) {
		float result = ((-1 / ( rpm / max_rpm * 4.75F-5 )) - 0.2F) / 3.8F * maxAccForce;
		if(result < 0)
			return 0;
		else return result;
	}
	
	public static float getCurrentRPM(float max_rpm, float shift) /*/ln(shift) / ln(max_rpm)*/ {
		if(shift>0.0F && maxShift>0.0F) {
			float currentForceRate = (GraphicUtils.log(shift) / GraphicUtils.log(maxShift));
			if(currentForceRate > 0.0F)
				return currentForceRate * max_rpm;
			else return 0.0F;
		} else return 0.0F;
	}

	public Aircraft (
	    GameManagement         theGameManager,
		String                 modelFile, 
		float                  Mess, 
		int                    Camp,
		LinkedListZ<ThreeDs>   Aircrafts,
		CharFrapsCamera		   MainCamera, 
		String                 id,
		boolean                line
	) {
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
        
		setRespwanAtTime(0);
		specialDisplay		= '@';
		ID					= id;
		mainCamera			= MainCamera;
		//cannonRollAngle	= new float[3];
		cameraRollAngle     = new float[3];
		cannonLocation		= new float[3];
		cameraLocation		= new float[3];
        directionXYZ        = new float[3];
		effectMakingLocation= new float[3][3];
		cameraLocationFlag	= 0;
		setLockingPriority(lockingPriority_backup = 1);
		setCamp(Camp);
		setHP(100);
		max_rpm				= 14000;
		maxSpeed			= 600;
		maxAccForce			= 18000;
		maxPushTime			= 1000;
		pushPower 			= 1500;
		pushTimeLeft		= 1000;
		control_stick_acc	= 0.0F;
		acc_shift			= 0.05F;
		//maxdeceleration		= 1.0F;
		minStableSpeed		    = 30.0F;
		resistanceRate_current	= 0.00500F;
		resistanceRate_normal	= 0.00500F;
		resistanceRate_breaking = 0.00575F;
		/*
		setMaxVelTurnLR(5.0F);
		setMaxVelRollLR(9.0F);//
		setMaxVelRollUp(9.0F);
		maxVelRollDn		= 9.0F;
		*/
		setMaxVelTurnLR(0.08726646259971649F);
        setMaxVelRollLR(0.15707963267948968F);//
        setMaxVelRollUp(0.15707963267948968F);
        setMaxVelRollDn(0.15707963267948968F);
        
		motionRate			= 0.0F;
		cannonFireLoadTime	= 0;
		isPushing			= false;
		setPlayer(true);
		//setAlive(true);
		cannonGunFlg		= 1;
		missileFlg          = 1;
		gameManager			= theGameManager;
		locked_By			= null;
		lockingLife			= 0;
		missileLockingLife  = 0;
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
		
		getCameraRollAngle()[0] = -roll_angle[0];
		getCameraRollAngle()[1] = -roll_angle[1];
		getCameraRollAngle()[2] = -roll_angle[2];
	}

    public void getRelativePosition_XY(float y, float z, float result[])
	{
		float Y, Z, cos$, sin$, r0 = getCameraRollAngle()[0];
		
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
	    GameManagement theGameManager,
		String modelFile,
		float Mess, 
		int Camp,
		LinkedListZ<ThreeDs> Aircrafts,
		CharFrapsCamera	     MainCamera,
		boolean              line
	)
	{
		this(theGameManager, modelFile, Mess, Camp, Aircrafts, MainCamera, "Me", line);
	}
	
	public void warning(Aircraft source) {
		locked_By			= source;
		lockedByEnemy		= true;
		lockingLife			= 20;
	}
	
	public void warningMissile(Aircraft source) {
        locked_By           = source;
        lockedByMissile     = true;
        missileLockingLife  = 20;
    }
	
	public void roll_up_dn(float angleVel)
	{
        angleVel /= 6.4;
	    float sinR2MulAngleVel = GraphicUtils.sin(roll_angle[2]) * angleVel;
	    
		if(GraphicUtils.abs(roll_angle[1]) > GraphicUtils.RAD90)	
		    roll_angle[0] += sinR2MulAngleVel;
		else roll_angle[0] -= sinR2MulAngleVel;
		
		roll_angle[1] += GraphicUtils.cos(roll_angle[2]) * angleVel;
		roll_angle[0] %= GraphicUtils.RAD360;//
		
		//if(roll_angle[1] > 180) roll_angle[1] = -180.0F + (roll_angle[1]-180.0F);
        if(roll_angle[1] > GraphicUtils.RAD180) roll_angle[1] -= GraphicUtils.RAD360;
		else if(roll_angle[1] < GraphicUtils.ngaHALFAPI) roll_angle[1] += GraphicUtils.RAD360;
	}
	
	public void turn_lr(float angleVel)
	{
        angleVel /= 6.4;
	    float cosR2MulAngleVel = GraphicUtils.cos(roll_angle[2]) * angleVel;
        
		if(GraphicUtils.abs(roll_angle[1]) > GraphicUtils.RAD90)	
		    roll_angle[0] -= cosR2MulAngleVel;
		else roll_angle[0] += cosR2MulAngleVel;

		roll_angle[1] += GraphicUtils.sin(roll_angle[2]) * angleVel;
		roll_angle[0] %= GraphicUtils.RAD360;//

        if(roll_angle[1] > GraphicUtils.RAD180) roll_angle[1] -= GraphicUtils.PIMUL2;
        else if(roll_angle[1] < GraphicUtils.ngaHALFAPI) roll_angle[1] += GraphicUtils.PIMUL2;
	}
	
	public void roll_lr(float angleVel) { roll_angle[2] = (roll_angle[2] + (angleVel/1.8F)) % GraphicUtils.PIMUL2; }
	
	public void control_roll_up_dn(float acceleration, float limit, float maxLimit)
	{
	    maxLimit *= motionRate;
		velocity_roll[0] += acceleration * motionRate * limit;
		
		if(velocity_roll[0] < 0 && velocity_roll[0] < -maxVelRollDn * maxLimit)
		    velocity_roll[0] = -maxVelRollDn * maxLimit;
		else if(velocity_roll[0] >= 0 && velocity_roll[0] > getMaxVelRollUp() * maxLimit)
		    velocity_roll[0] = getMaxVelRollUp() * maxLimit;
	}
	
	private float tmp;
	
	public void control_roll_up_dn(float acceleration)
	{
	    if(acceleration < 0) {
            if(acceleration < -maxVelRollDn) acceleration = -maxVelRollDn * motionRate;
        } else {
            if(acceleration > maxVelRollUp) acceleration = maxVelRollUp * motionRate;
        }
		velocity_roll[0] += acceleration * motionRate;
		
		if(velocity_roll[0] < 0) {
		    if(velocity_roll[0] < (tmp = -maxVelRollDn * motionRate))
	            velocity_roll[0] = tmp;
		} else {
		    if(velocity_roll[0] > (tmp = maxVelRollUp * motionRate))
	            velocity_roll[0] = tmp;
		}
		
		if(acceleration != 0)velocity_roll[0] *= 1.025F;
	}
	
	public void getDamage(int damage, Aircraft giver, String weaponName)
	{
		setHP(getHP() - damage);
		if(getHP() <= 0)
		{
			isPushing = false;
			//if(isAlive()) {
            //setAlive(false);
            visible = false;
			setLockingPriority(0);
			getGameManager().addKillTip(giver, this, weaponName);
			++dead;
			++giver.killed;
			Particle.makeExplosion(getGameManager(), location, 20, 40000, 0.075F, 0.2F);
			setRespwanAtTime(System.currentTimeMillis() + getGameManager().getRespawnTime());
			//}
		}
	}
	
	public void control_acc(float acc) //in a game frap
	{
		resistanceRate_current = resistanceRate_normal;
		control_stick_acc = getControl_stick_acc() + acc;
		if(getControl_stick_acc() > maxShift)
			control_stick_acc = maxShift;
	}
	
	public void control_push() //in a game frap
	{
		if(getPushTimeLeft() > getMaxPushTime() / 3)
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
		control_stick_acc = getControl_stick_acc() - acc_shift * 2;
		if(getControl_stick_acc() < 0.0F)
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
		if(GraphicUtils.abs(velocity_roll[2]) > (tmp = maxVelRollLR * motionRate))
		{
			if(velocity_roll[2] < 0)
				velocity_roll[2] = -tmp;
			else velocity_roll[2] = tmp;
		}
	}
	
	public void control_roll_lr(float acceleration)
	{
		acceleration /= GraphicUtils.min(GraphicUtils.pow(3.0F, GraphicUtils.abs(acceleration)), 3.0F);
		if(acceleration < 0) {
            if(acceleration < -maxVelRollLR) acceleration = -maxVelRollLR * motionRate;
        } else {
            if(acceleration > maxVelRollLR) acceleration = maxVelRollLR * motionRate;
        }
		velocity_roll[2] += acceleration * motionRate;
		if(GraphicUtils.abs(velocity_roll[2]) > (tmp = maxVelRollLR * motionRate))
		{
			if(velocity_roll[2] < 0)
				velocity_roll[2] = -tmp;
			else velocity_roll[2] = tmp;
		}
		
		if(acceleration != 0) velocity_roll[2] *= 1.0375F;
	}
	
	public void control_turn_lr(float acceleration, float limit, float maxLimit)
	{
	    maxLimit *= motionRate;
		velocity_roll[1] += acceleration * motionRate * limit;
		if(GraphicUtils.abs(velocity_roll[1]) > getMaxVelTurnLR() * maxLimit)
		{
			if(velocity_roll[1] < 0)
				velocity_roll[1] = -getMaxVelTurnLR() * maxLimit;
			else velocity_roll[1] = getMaxVelTurnLR() * maxLimit;
		}
	}
	
	public void control_turn_lr(float acceleration)
	{
	    tmp = maxVelTurnLR * motionRate;
		if(acceleration < 0) {
            if(acceleration < -maxVelTurnLR) acceleration = -tmp;
		} else {
		    if(acceleration > maxVelTurnLR) acceleration = tmp;
		}
		
		velocity_roll[1] += acceleration * motionRate;
		if(GraphicUtils.abs(velocity_roll[1]) > getMaxVelTurnLR() * motionRate)
		{
			if(velocity_roll[1] < 0)
				velocity_roll[1] = -tmp;
			else velocity_roll[1] = tmp;
		}
		
		if(acceleration != 0) velocity_roll[1] *= 1.025F;
	}
	
	public void makeDecoy()
	{
		if(decoyMagazineLeft > 0 && isAlive())
		{
			Decoy.make(getGameManager(), getCamp(), location, roll_angle, getSpeed(), 64.0F, 150000, 0.04F, 0.125F);

			if(--decoyMagazineLeft == 0)
				decoyReloadingTimeLeft = decoyReloadingTime;
		}
	}
	
	public void cannonOpenFire()
	{
		if(cannonMagazineLeft > 0 && isAlive())
			isCannonFiring = true;
	}
	
	public void cannonStopFiring()
	{
		isCannonFiring = false;
	}
	
	protected Missile newMissile(boolean cameraTrace, Aircraft target) {
	    
	    cannonLocation[0] = 70;
        cannonLocation[1] = 0;
        cannonLocation[2] = 0;
        
        if((missileFlg & 1) == 0)
            cannonLocation[1] += 200;
        else cannonLocation[1] -= 200;
        if(missileFlg < 2)
            cannonLocation[0] += 180;
        else cannonLocation[0] -= 60;
        
        getXYZ_afterRolling
        (
            cannonLocation[0], cannonLocation[1], cannonLocation[2],
            roll_angle[0],     roll_angle[1],     roll_angle[2],
            
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
                getGameManager(), 1280000, getSpeed()/2+5, 512, cannonLocation, 
                roll_angle, 20, 512, 3.0F, this, target, mainCamera
            );
        }
        else
        {
            m = new Missile
            (
                getGameManager(), 1280000, getSpeed()/2+5, 512, cannonLocation, 
                roll_angle, 20, 512, 3.0F, this, target, null
            );
        }
        
        getGameManager().fireAmmo(m);
        
        if(++missileFlg == 4)
            missileFlg = 0;

        if(--missileMagazineLeft == 0)
            missileReloadingTimeLeft = missileReloadingTime;
        return m;
	}
	
	public Missile missileOpenFire(boolean cameraTrace, Aircraft target) {
		if(missileMagazineLeft <= 0 || !isAlive()) return null;
		Missile m = newMissile(cameraTrace, target);
        getGameManager().colorFlash(255, 255, 255, 0, 64, 96, 6);
        return m;
	}
	
	public void wingsEffectRun() {
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
		
		getGameManager().newEffect(new EngineFlame(effectMakingLocation[0], 75000 + (int)(75000 * GraphicUtils.random()), '*'));
		getGameManager().newEffect(new EngineFlame(effectMakingLocation[1], 75000 + (int)(75000 * GraphicUtils.random()), '*'));
	}

    public void pushEffectRun() {
        effectMakingLocation[2][0] = 120;
        effectMakingLocation[2][1] = 0;
        effectMakingLocation[2][2] = -240;
        
        getXYZ_afterRolling
        (
            effectMakingLocation[2][0], 
            effectMakingLocation[2][1], 
            effectMakingLocation[2][2],
            roll_angle[0],
            roll_angle[1],
            roll_angle[2],
            effectMakingLocation[2]
        );
        
        effectMakingLocation[2][0] += location[0];
        effectMakingLocation[2][1] += location[1];
        effectMakingLocation[2][2] += location[2];
        
        getGameManager().newEffect(new EngineFlame2(effectMakingLocation[2], 10000, '*'));
    }
    
	public float[] getCurrentDirectionXYZ() {
        return directionXYZ;
    }
	
    public void doMotion() {
		//------------[go street]------------
        float r1 = roll_angle[1];
        float r2 = roll_angle[0];
        
		float t  = GraphicUtils.cos(r1) * getSpeed();
		
		location[0]	-= directionXYZ[0] = GraphicUtils.sin(r1) * getSpeed();
		location[1]	+= directionXYZ[1] = GraphicUtils.sin(r2) * t;
		location[2]	+= directionXYZ[2] = GraphicUtils.cos(r2) * t;
		GraphicUtils.toDirectionVector(directionXYZ);
		toDirectionXY(directionXYZ);
		directionXYZ[2] = roll_angle[2];
		//--------------[motion]-------------
		roll_up_dn(velocity_roll[0]);
		turn_lr(velocity_roll[1]);
		roll_lr(velocity_roll[2]);
		//-----------------------------------
		engine_rpm = getCurrentRPM(getMax_rpm(), getControl_stick_acc());
		float F   = getCurrentForce(maxAccForce, getMax_rpm(), engine_rpm);
	
		if(isPushing) {
			F += pushPower;
			if(isPlayer()) getGameManager().addGBlack(1.3F);
			if((pushTimeLeft = getPushTimeLeft() - 2) <= 0) isPushing = false;
			pushEffectRun();
		} else if(getPushTimeLeft() < getMaxPushTime())pushTimeLeft = getPushTimeLeft() + 1;
		
		if(velocity_roll[0] != 0.0F)
			velocity_roll[0] /= 1.050F;
		if(velocity_roll[1] != 0.0F)
			velocity_roll[1] /= 1.050F;
		if(velocity_roll[2] != 0.0F)
			velocity_roll[2] /= 1.075F;
		
		setSpeed(getSpeed() + F/mess);
		setSpeed(getSpeed() - speed * resistanceRate_current);
		
		if(getSpeed() > maxSpeed)
			setSpeed(maxSpeed);
		
		if(getSpeed() < getMinStableSpeed()) {
			motionRate = getSpeed() * 0.8F / getMinStableSpeed();
			if(location[0] < 200)
				location[0] += CharTimeSpace.g * (1.0F - getSpeed() / getMinStableSpeed());
			else location[0] = 200;
		} else {
			motionRate = 0.8F;
			if(location[0] > 200) location[0] = 200;
			float a = GraphicUtils.abs(velocity_roll[0]) / getMaxVelRollUp();
            float b = GraphicUtils.abs(velocity_roll[2]) / getMaxVelRollLR();
            float c = GraphicUtils.max(a, b);
			if (c > 0.6F) {
		        if(isPlayer()) getGameManager().addGBlack(c * 2.6F);
			    wingsEffectRun();
			}
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
					cannonLocation[0] = 70;
					cannonLocation[1] = 0;
					cannonLocation[2] = 0;
					
					if((cannonGunFlg & 1) == 0)
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
					getGameManager().fireAmmo(
						new CannonAmmo (
							getGameManager(), 400000, getCamp(), 400 + getSpeed(), 0.00175F, 
							cannonLocation, roll_angle, aircrafts, this
						)
					);
					if(isPlayer()) getGameManager().colorFlash(255, 224, 128, 0, 0, 0, 3);
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
				setLockingPriority(lockingPriority_backup);
			}
		}
		
		if(decoyReloadingTimeLeft > 0)
		{
			if(--decoyReloadingTimeLeft == 0)
				decoyMagazineLeft = decoyMagazine;
		}
	}
	
	protected final void playersCameraManage() {
		if(cameraLocationFlag == 0) {
			float t1, t2, t3;
			/*
			cameraLocation[0] = -50;
			cameraLocation[1] = 0;
			cameraLocation[2] =  -150;
			*/

			getCameraLocation()[0] = -240;
			getCameraLocation()[1] = 0;
			getCameraLocation()[2] = -960;
			
			if(getCameraRollAngle()[0] < 0) {
				if((-roll_angle[0]) - getCameraRollAngle()[0] < GraphicUtils.PI) t1 = -roll_angle[0] - getCameraRollAngle()[0];
				else t1 = -roll_angle[0] - getCameraRollAngle()[0] - GraphicUtils.PIMUL2;
			} else if(getCameraRollAngle()[0] > 0) {
				if(getCameraRollAngle()[0] - (-roll_angle[0]) < GraphicUtils.PI) t1 = -roll_angle[0] - getCameraRollAngle()[0];
				else t1 = -roll_angle[0] - getCameraRollAngle()[0] + GraphicUtils.PIMUL2;
			} else t1 = -roll_angle[0];
			
			if(getCameraRollAngle()[1] < 0) {
				if((-roll_angle[1]) - getCameraRollAngle()[1] < GraphicUtils.PI) t2 = -roll_angle[1] - getCameraRollAngle()[1];
				else t2 = -roll_angle[1] - getCameraRollAngle()[1] - GraphicUtils.PIMUL2;
			} else if(getCameraRollAngle()[1] > 0) {
				if(getCameraRollAngle()[1] - (-roll_angle[1]) < GraphicUtils.PI) t2 = -roll_angle[1] - getCameraRollAngle()[1];
				else t2 = -roll_angle[1] - getCameraRollAngle()[1] + GraphicUtils.PIMUL2;
			} else t2 = -roll_angle[1];
			
			if(getCameraRollAngle()[2] < 0) {
				if((-roll_angle[2]) - getCameraRollAngle()[2] < GraphicUtils.PI) t3 = -roll_angle[2] - getCameraRollAngle()[2];
				else t3 = -roll_angle[2] - getCameraRollAngle()[2] - GraphicUtils.PIMUL2;
			} else if(getCameraRollAngle()[2] > 0) {
				if(getCameraRollAngle()[2] - (-roll_angle[2]) < GraphicUtils.PI) t3 = -roll_angle[2] - getCameraRollAngle()[2];
				else t3 = -roll_angle[2] - getCameraRollAngle()[2] + GraphicUtils.PIMUL2;
			} else t3 = -roll_angle[2];
			
			t1 /= 6;
			t2 /= 6;
			t3 /= 6;
			
			getCameraRollAngle()[0] = (getCameraRollAngle()[0] + t1) % GraphicUtils.PIMUL2;
			getCameraRollAngle()[1] = (getCameraRollAngle()[1] + t2) % GraphicUtils.PIMUL2;
			getCameraRollAngle()[2] = (getCameraRollAngle()[2] + t3) % GraphicUtils.PIMUL2;
		} else {
			if(cameraLocationFlag > 1) cameraLocationFlag = 0;
			
			getCameraLocation()[0] = -96;
			getCameraLocation()[1] = 0;
			getCameraLocation()[2] = -320;
			
			getCameraRollAngle()[0] = -roll_angle[0];
			getCameraRollAngle()[1] = -roll_angle[1];
			getCameraRollAngle()[2] = -roll_angle[2];
		}
		
		getXYZ_afterRolling (
			getCameraLocation()[0], getCameraLocation()[1], getCameraLocation()[2],
			roll_angle[0],	   roll_angle[1],	  roll_angle[2],
			
			getCameraLocation()
		);

		getCameraLocation()[0] += location[0];
		getCameraLocation()[1] += location[1];
		getCameraLocation()[2] += location[2];
		
		if(mainCamera.location != getCameraLocation()) fov_current = 9.37F; //视角跟随导弹时
		else if(isPushing) fov_current += (fov_pushing / fov_current - 1) * 2;
		else {
			if(isCannonFiring) fov_current += (fov_gunFiring / fov_current - 1);
			else switch(cameraLocationFlag) {
				case 0: fov_current += (fov_3thPerson / fov_current - 1) / 2; break;
				case 1: fov_current += (fov_1stPerson / fov_current - 1) / 2; break;
			}
		}
		mainCamera.setFOV(fov_current);
	}
	
	protected final void defaultGo() {
		if(!isAlive()) {
			if(System.currentTimeMillis() >= getRespwanAtTime())	
				randomRespawn();
			return;
		}

		doMotion();
		weaponSystemRun();
	}
	
	@Override
	public void go() {
		defaultGo();
		if(--lockingLife <= 0) {
			lockedByEnemy = false;
			if(lockedByMissile == false) locked_By = null;
		}
		if(--missileLockingLife <= 0) {
            lockedByMissile = false;
            if(lockedByEnemy == false) locked_By = null;
        }
		playersCameraManage();
	}
	
	@Override
	public void run() {
		go();
	}
	
	protected void randomRespawn() {
		setLockingPriority(0);
		
		setLocation (
			200,
			getGameManager().getPlayerCameraLocation()[1] + GraphicUtils.random() * getGameManager().getWeaponMaxSearchingRange() * ((GraphicUtils.fastRanodmInt() & 1) == 0? -1 : 1),
			getGameManager().getPlayerCameraLocation()[2] + GraphicUtils.random() * getGameManager().getWeaponMaxSearchingRange() * ((GraphicUtils.fastRanodmInt() & 1) == 0? -1 : 1)
		);
		//setLocation(0, 0, 0);
		
		setRollAngle(GraphicUtils.random() * GraphicUtils.PIMUL2, 0, 0);
		//setRollAngle(0, 0, 0);
		
		getCameraRollAngle()[0] = -roll_angle[0];
		getCameraRollAngle()[1] = -roll_angle[1];
		getCameraRollAngle()[2] = -roll_angle[2];
		
		setSpeed(0);
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
		
		pushTimeLeft = getMaxPushTime();
		
		visible = true;
        setHP(100);
		//setAlive(true);
	}
	
	public final void setID(String id) {
	    this.ID = new String(id);
	}

    public final String getID() {
        return this.ID;
    }
    
    /**only for NPC*/
	public final void pollBack() {
		setHP(0);
	}
	
	@Override
	public final String toString()
	{
		return ID;
	}

    public final int getHP() {
        return HP;
    }

    public final void setHP(int hP) {
        HP = hP;
    }

    public final int getLockingPriority() {
        return lockingPriority;
    }

    public final void setLockingPriority(int lockingPriority) {
        this.lockingPriority = lockingPriority;
    }

    public final int getCamp() {
        return camp;
    }

    public final void setCamp(int camp) {
        this.camp = camp;
    }

    public final boolean isPlayer() {
        return iAmPlayer;
    }

    public final void setPlayer(boolean trueOrFalse) {
        this.iAmPlayer = trueOrFalse;
    }

    public final long getRespwanAtTime() {
        return respwanAtTime;
    }

    public final void setRespwanAtTime(long respwanAtTime) {
        this.respwanAtTime = respwanAtTime;
    }
    
    public final float getResistanceRate_normal() {
        return resistanceRate_normal;
    }

    public final void setResistanceRate_normal(float resistanceRate_normal) {
        this.resistanceRate_normal = resistanceRate_normal;
    }

    public final GameManagement getGameManager() {
        return gameManager;
    }

    public final float getPushTimeLeft() {
        return pushTimeLeft;
    }

    public final float getMaxPushTime() {
        return maxPushTime;
    }

    public final float getMax_rpm() {
        return max_rpm;
    }

    public final float getControl_stick_acc() {
        return control_stick_acc;
    }

    public final float getSpeed() {
        return speed;
    }

    public final void setSpeed(float speed) {
        this.speed = speed;
    }

    public final float getMaxVelTurnLR() {
        return maxVelTurnLR;
    }

    public final void setMaxVelTurnLR(float maxVelTurnLR) {
        this.maxVelTurnLR = maxVelTurnLR;
    }

    public final float getMaxVelRollUp() {
        return maxVelRollUp;
    }

    public final void setMaxVelRollUp(float maxVelRollUp) {
        this.maxVelRollUp = maxVelRollUp;
    }

    public final void setMaxVelRollDn(float maxVelRollDn) {
        this.maxVelRollDn = maxVelRollDn;
    }

    public final float getMaxVelRollLR() {
        return maxVelRollLR;
    }

    public final void setMaxVelRollLR(float maxVelRollLR) {
        this.maxVelRollLR = maxVelRollLR;
    }

    public final float[] getCameraRollAngle() {
        return cameraRollAngle;
    }

    public final float[] getCameraLocation() {
        return cameraLocation;
    }

    public final boolean isAlive() {
        return HP > 0;
    }
/*
    public final void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }
*/
    /**
     * 返回方向向量所对应的xyz夹角的弧度
     * @param directionVector 方向向量
     * @return 弧度制夹角
     */
    public static final void toDirectionXY(float directionXYZVector[]) {
        float x = directionXYZVector[0];
        directionXYZVector[0] = GraphicUtils.atan2(directionXYZVector[2], -directionXYZVector[1]);
        directionXYZVector[1] = GraphicUtils.asin(x);
        directionXYZVector[2] = 0;
    }
    
    public final float getMinStableSpeed() {
        return minStableSpeed;
    }
}
