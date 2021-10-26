package dogfight_Z;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import dogfight_Z.Ammo.Missile;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.utils.GraphicUtils;

public class NPC extends Aircraft
{
	//------------------------------------------
	private short		missileFireWaitingTime;
	private short		missileFireWaitingTimeLeft;
	//---------------[tracing]------------------
	public double searching_visibility;
	public Aircraft		tracingTarget;
	public double		maxMotionRate;
	private	double		halfAResolution_X;
	private double		halfAResolution_Y;
	private double		maxSearchingRange;
	public Aircraft		currentSelectObj;
	public boolean		locked;
	public boolean		lockingSelected;
	public short		lockTime;
	public short		lockTimeLeft;
	public short		currentMaxLockingPriority;
	public short		scrResolution[];
	public double		point_on_Scr[];
	//public double		point_on_Old[];
	//---------------[cruise]-------------------
	private short		goStreetTime;
	private short		turnLRTime;
	private short		roll_up_dn_Time;
	private boolean		turnRight;
	private boolean		turnUp;
	//------------------------------------------
	//----------------[decoy]-------------------
	//------------------------------------------
	public NPC
	(
		Game					theGame,
		String					modelFile, 
		String					id,
		double					Mess, 
		double					searching_visibility,
		double					max_motionRate,
		short					scrResolution_X,
		short 					scrResolution_Y,
		short					camp,
		PriorityQueue<Dynamic>	firedAmmo, 
		PriorityQueue<Dynamic>	Effects,  
		LinkedList<ThreeDs>		add_que,
		LinkedList<ThreeDs>		delete_que,
		HashSet<ThreeDs>		Aircrafts
	)
	{
		super(theGame, modelFile, Mess, camp, firedAmmo, Effects, delete_que, add_que, Aircrafts, null, id);
		scrResolution		= new short[2];
		point_on_Scr		= new double[2];
		//point_on_Old		= new double[2];
		maxMotionRate		= max_motionRate;
		maxSearchingRange	= searching_visibility;
		scrResolution[0]	= scrResolution_X;
		scrResolution[1]	= scrResolution_Y;
		halfAResolution_X	= scrResolution[0] / 2;
		halfAResolution_Y	= scrResolution[1] / 2;
		tracingTarget		= null;
		currentSelectObj	= null;
		isPlayer			= false;
		goStreetTime		= (short)(500 * Math.random());
		turnLRTime			= 0;
		roll_up_dn_Time		= 0;
		lockTime			= 100;
		missileFireWaitingTime	= missileFireWaitingTimeLeft = 1;
		turnRight			= (Math.random() > 0.5? true : false);
		turnUp  			= (Math.random() > 0.5? true : false);
		currentMaxLockingPriority = 0;
	}
	
	public void pursuit(double range_to_me) {
		if(speed < tracingTarget.speed || range_to_me > 5000)
			control_acc();
		else if(speed > tracingTarget.speed && speed > minStableSpeed)
			control_dec();
		
		if(maxVelRollUp > maxVelTurnLR)
		{
			if(point_on_Scr[0] > halfAResolution_X)
				control_roll_lr(-maxVelRollLR * maxMotionRate/* * (point_on_Scr[0] * 2 - point_on_Old[0] - halfAResolution_X)*/);
			else if(point_on_Scr[0] < halfAResolution_X)
				control_roll_lr(maxVelRollLR * maxMotionRate/* * (halfAResolution_X - point_on_Scr[0] * 2 + point_on_Old[0])*/);
			}
		else if(maxVelRollUp < maxVelTurnLR)
		{
			if(point_on_Scr[1] > halfAResolution_Y)
				control_roll_lr(-maxVelRollLR * maxMotionRate/* * (point_on_Scr[1] * 2 - point_on_Old[1] - halfAResolution_Y)*/);
			else if(point_on_Scr[1] < halfAResolution_Y)
				control_roll_lr(maxVelRollLR * maxMotionRate/* * (halfAResolution_Y) - point_on_Scr[1] * 2 + point_on_Old[1]*/);
		}
		
		if(point_on_Scr[0] > halfAResolution_X)
			control_turn_lr(maxVelTurnLR * maxMotionRate/* * (point_on_Scr[0] * 2 - point_on_Old[0] - halfAResolution_X)*/);
		else if(point_on_Scr[0] < halfAResolution_X)
			control_turn_lr(-maxVelTurnLR * maxMotionRate/* * (halfAResolution_X - point_on_Scr[0] * 2 + point_on_Old[0])*/);
		
		if(point_on_Scr[1] > halfAResolution_Y)
			control_roll_up_dn(-maxVelRollUp * maxMotionRate/* * (point_on_Scr[1] * 2 - point_on_Old[1] - halfAResolution_Y)*/);
		else if(point_on_Scr[1] < halfAResolution_Y)
			control_roll_up_dn(maxVelRollUp * maxMotionRate/* * (halfAResolution_Y - point_on_Scr[1] * 2 + point_on_Old[1])*/);
		
		//System.arraycopy(point_on_Scr, 0, point_on_Old, 0, 2);
	}
	
	public void trace()
	{
		if(tracingTarget != null  &&  tracingTarget.isAlive)
		{		
			double range_to_Scr = CharFrapsCamera.getXY_onCamera (
				tracingTarget.location[0], tracingTarget.location[1], tracingTarget.location[2], 
				scrResolution[0], scrResolution[1], location, cameraRollAngle, point_on_Scr, 2.6
			);
			
			if(range_to_Scr > 0) {
				pursuit(range_to_Scr);
				
				if(
					range_to_Scr < maxSearchingRange    &&    
					CharFrapsCamera.rangeXY(point_on_Scr[0], point_on_Scr[1], halfAResolution_X, halfAResolution_Y) < 24
				) {
					cannonOpenFire();
					Aircraft aJet;
					currentMaxLockingPriority = 0;
					lockingSelected = false;
					
					for(ThreeDs a : aircrafts)
					{
						aJet = (Aircraft) a;
						if(aJet.camp == camp  ||  !aJet.isAlive) continue;
						
						range_to_Scr = CharFrapsCamera.getXY_onCamera
						(
							aJet.location[0], aJet.location[1], aJet.location[2], 
							scrResolution[0], scrResolution[1], location, cameraRollAngle, point_on_Scr, 2.6
						);
						
						if
						(
							range_to_Scr > 0    &&    range_to_Scr < maxSearchingRange    &&    
							CharFrapsCamera.rangeXY(point_on_Scr[0], point_on_Scr[1], halfAResolution_X, halfAResolution_Y) < 24
						)
						{
							if(currentSelectObj == null || currentMaxLockingPriority < Math.abs(aJet.lockingPriority) && !currentSelectObj.ID.equals(aJet.ID))
							{	//当前选择目标切换到优先级更高的	(发生切换)
								if(aJet.lockingPriority > 0)
									tracingTarget			= aJet;
								currentMaxLockingPriority	= (short) Math.abs(aJet.lockingPriority);
								currentSelectObj			= aJet;
								lockTimeLeft				= lockTime;
								locked						= false;
								lockingSelected				= true;
							}
							else
							{
								if(currentSelectObj!=null && aJet!=null && currentSelectObj.ID.equals(aJet.ID))
								{
									lockingSelected = true;
									if(locked)
									{
										if(aJet.lockingPriority >= 0) aJet.warning(this);
										if(missileMagazineLeft > 0)   missileOpenFire(false, aJet);
									}
									else
									{
										if(--lockTimeLeft <= 0)
										{
											locked = true;
											if(tracingTarget.lockingPriority >= 0) tracingTarget.warning(this);
											if(missileMagazineLeft > 0)            missileOpenFire(false, aJet);
										}
										else
										{
											if(tracingTarget.lockingPriority >= 0)
												tracingTarget.warning(this);
										}
									}
								}
							}
						}
					}
					
					if(!lockingSelected)
					{
						currentMaxLockingPriority = 0;
						currentSelectObj		  = null;
						lockTimeLeft			  = lockTime;
						tracingTarget			  = null;
					}
					
					if(currentSelectObj!=null && currentSelectObj.lockingPriority >= 0)
						currentSelectObj.warning(this);
				}
				else cannonStopFiring();
			}
			else escape();//被咬尾，尝试逃离
		}
		else cannonStopFiring();
	}
	
	public void cruise
	(
		int motionRate,
		double maxRollAngle_lr,
		double minAcc,
		double time_street,
		double time_turn,
		double time_up_dn
	)
	{
			currentMaxLockingPriority = 0;
			//if(missileMagazineLeft < missileMagazine) missileOpenFire(false, null); //清空弹舱，重新装填
			
			if(control_stick_acc < minAcc)      control_acc();
			else if(control_stick_acc > minAcc) control_dec();
			
			if(--goStreetTime > 0)
			{
				
				if(roll_angle[2] < 0)
					control_roll_lr(maxVelRollLR / motionRate);
				else if(roll_angle[2] > 0)
					control_roll_lr(maxVelRollLR / -motionRate);
				
				if(location[0] < -3500 && roll_angle[1] > -15)
					control_roll_up_dn(maxVelRollLR / -motionRate);		//down
				else if(location[0] > -1250 && roll_angle[1] < 30)
					control_roll_up_dn(maxVelRollLR / motionRate);		//up
				else
				{
					if(--roll_up_dn_Time > 0)
					{
						if(turnUp && roll_angle[1] < maxRollAngle_lr)
							control_roll_up_dn(maxVelRollLR / motionRate);
						else if(!turnUp && roll_angle[1] > -maxRollAngle_lr)
							control_roll_up_dn(maxVelRollLR / -motionRate);
					}
					else
					{
						if(roll_angle[1] > 0)
							control_roll_up_dn(maxVelRollLR / -motionRate);		//down
						else if(roll_angle[1] < 0)
							control_roll_up_dn(maxVelRollLR / motionRate);		//up
					}
				}
			}
			else if(--turnLRTime > 0)
			{
				if(turnRight)
				{
					if(roll_angle[2] > -maxRollAngle_lr)
						control_roll_lr(maxVelRollLR / -motionRate);	//l
					else if(roll_angle[2] < -maxRollAngle_lr)
						control_roll_lr(maxVelRollLR / motionRate);	//r
					
					roll_up_dn(velocity_roll[1] * 2 * GraphicUtils.sin(Math.toRadians(maxRollAngle_lr)) / GraphicUtils.cos(Math.toRadians(maxRollAngle_lr)));
					
					control_turn_lr(maxVelRollLR / motionRate);
				}
				else
				{
					if(roll_angle[2] < maxRollAngle_lr)
						control_roll_lr(maxVelRollLR / motionRate);	//r
					else if(roll_angle[2] > maxRollAngle_lr)
						control_roll_lr(maxVelRollLR / -motionRate);	//l
					
					roll_up_dn(-velocity_roll[1] * 2 * GraphicUtils.sin(Math.toRadians(maxRollAngle_lr)) / GraphicUtils.cos(Math.toRadians(maxRollAngle_lr)));
	
					control_turn_lr(maxVelRollLR / -motionRate);
				}
				
				if(location[0] < -2500 && roll_angle[1] > -15)
					control_roll_up_dn(maxVelRollLR / -motionRate);		//down
				else if(location[0] > -1250 && roll_angle[1] < 30)
					control_roll_up_dn(maxVelRollLR / motionRate);		//up
			}
			else
			{
				boolean RandomFlg = (Math.random() > 0.5? true : false);
				if(RandomFlg)
					goStreetTime = (short) (Math.random() * time_street);
				else
				{
					turnLRTime = (short) (Math.random() * time_turn);
					turnRight  = (Math.random() > 0.5? true : false);
					
				}
				roll_up_dn_Time = (short) (Math.random() * time_up_dn);
				turnUp  = (Math.random() > 0.5? true : false);
			}
	}

	public void cruise()
	{
		cruise(8, 30, 11.2, 750, 500, 250);
	}
	
	public void escape() {
		cruise(4, 30, 14, 500, 500, 250);
	}
	
	@Override
	public Missile missileOpenFire(boolean cameraTrace, Aircraft target)
	{
		if(missileMagazineLeft > 0 && missileFireWaitingTimeLeft <= 0)
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
			
			missileFireWaitingTimeLeft = missileFireWaitingTime;
			return m;
		}
		else return null;
	}
	
	@Override
	public void go()
	{
		cameraRollAngle[0] = -roll_angle[0];
		cameraRollAngle[1] = -roll_angle[1];
		cameraRollAngle[2] = -roll_angle[2];
		
		if(locked_By != null)
			tracingTarget = locked_By;
		
		double range_to_Scr;
		Aircraft aJet;
		
		if(tracingTarget == null)
		{
			currentMaxLockingPriority = 0;
			lockingSelected = false;
			for(ThreeDs a : aircrafts)
			{
				aJet = (Aircraft) a;
				if(aJet.camp == camp  ||  !aJet.isAlive)
					continue;
				
				range_to_Scr = CharFrapsCamera.getXY_onCamera
				(
					aJet.location[0], aJet.location[1], aJet.location[2], 
					scrResolution[0], scrResolution[1], location, cameraRollAngle, point_on_Scr, 2.6
				);
				
				if
				(
					range_to_Scr    > 0 && range_to_Scr    < maxSearchingRange &&    
					point_on_Scr[0] > 0 && point_on_Scr[0] < scrResolution[0]  &&
					point_on_Scr[1] > 0 && point_on_Scr[1] < scrResolution[1]
					
				)	
				{
					//point_on_Old[0] = point_on_Scr[0];
					//point_on_Old[1] = point_on_Scr[1];
					
					if(currentSelectObj == null || currentMaxLockingPriority < Math.abs(aJet.lockingPriority) && !currentSelectObj.ID.equals(aJet.ID))
					{
						if(aJet.lockingPriority >= 0)
							tracingTarget			= aJet;
						currentMaxLockingPriority	= (short) Math.abs(aJet.lockingPriority);
						currentSelectObj			= aJet;
						lockTimeLeft				= lockTime;
						locked						= false;
						lockingSelected				= true;
					}
				}
			}
			
			if(!lockingSelected)
			{
				currentMaxLockingPriority = 0;
				currentSelectObj		  = null;
				lockTimeLeft			  = lockTime;
				tracingTarget			  = null;
			}
			
			if(currentSelectObj!=null && currentSelectObj.lockingPriority >= 0)
				currentSelectObj.warning(this);
		}
		
		if(tracingTarget != null && tracingTarget.isAlive || lockedByEnemy && locked_By.isAlive)
			trace();
		else
		{
			cannonStopFiring();
			cruise();
		}
		
		if(--lockingLife <= 0)
		{
			lockedByEnemy = false;
			locked_By     = null;
		}
		defaultGo();
	}
	
	@Override
	public void warning(Aircraft source)
	{
		if(lockedByEnemy) makeDecoy();
		
		if(missileFireWaitingTimeLeft > 0)
			--missileFireWaitingTimeLeft;
		
		locked_By			= source;
		lockedByEnemy		= true;
		lockingLife			= 20;
	}
	
	public void rename(String newName)
	{
		ID = newName;
	}
	
	public void rename()
	{
		ID = "NPC " + (int)(Math.random()*100);
	}
}
