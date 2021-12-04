package dogfight_Z;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import dogfight_Z.Ammo.Missile;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.LinkedListZ;

public class NPC extends Aircraft
{
	//------------------------------------------
	private int		missileFireWaitingTime;
	private int		missileFireWaitingTimeLeft;
	//---------------[tracing]------------------
	public float searching_visibility;
	public Aircraft		tracingTarget;
	public float		maxMotionRate;
	private	float		halfAResolution_X;
	private float		halfAResolution_Y;
	private float		maxSearchingRange;
	public Aircraft		currentSelectObj;
	public boolean		locked;
	public boolean		lockingSelected;
	public int		lockTime;
	public int		lockTimeLeft;
	public int		currentMaxLockingPriority;
	public int		scrResolution[];
	public float		point_on_Scr[];
	//public float		point_on_Old[];
	//---------------[cruise]-------------------
	private int		goStreetTime;
	private int		turnLRTime;
	private int		roll_up_dn_Time;
	private boolean		turnRight;
	private boolean		turnUp;
	//------------------------------------------
	//----------------[decoy]-------------------
	//------------------------------------------
	public NPC
	(
		GameManager			    theGameManager,
		String					modelFile, 
		String					id,
		float					Mess, 
		float					searching_visibility,
		float					max_motionRate,
		int					    scrResolution_X,
		int 					scrResolution_Y,
		int					    camp,
		PriorityQueue<Dynamic>	firedAmmo, 
		LinkedList<ThreeDs>		add_que,
		LinkedList<ListIterator<ThreeDs>>		delete_que,
		LinkedListZ<ThreeDs>	Aircrafts
	)
	{
		super(theGameManager, modelFile, Mess, camp, Aircrafts, null, id, true);
		scrResolution		= new int[2];
		point_on_Scr		= new float[2];
		//point_on_Old		= new float[2];
		maxMotionRate		= max_motionRate;
		maxSearchingRange	= searching_visibility;
		scrResolution[0]	= scrResolution_X;
		scrResolution[1]	= scrResolution_Y;
		halfAResolution_X	= scrResolution[0] >> 1;
		halfAResolution_Y	= scrResolution[1] >> 1;
		tracingTarget		= null;
		currentSelectObj	= null;
		setPlayer(false);
		goStreetTime		= (GraphicUtils.randomInt(500));
		turnLRTime			= 0;
		roll_up_dn_Time		= 0;
		lockTime			= 100;
		missileFireWaitingTime	= missileFireWaitingTimeLeft = 1;
		turnRight			= (GraphicUtils.random() > 0.5? true : false);
		turnUp  			= (GraphicUtils.random() > 0.5? true : false);
		currentMaxLockingPriority = 0;
	}
	
	public void pursuit(float range_to_me) {
		if(getSpeed() < tracingTarget.getSpeed() || range_to_me > 5000)
			control_acc();
		else if(getSpeed() > tracingTarget.getSpeed() && getSpeed() > getMinStableSpeed())
			control_dec();
		
		if(getMaxVelRollUp() > getMaxVelTurnLR())
		{
			if(point_on_Scr[0] > halfAResolution_X)
				control_roll_lr(-getMaxVelRollLR() * maxMotionRate/* * (point_on_Scr[0] * 2 - point_on_Old[0] - halfAResolution_X)*/);
			else if(point_on_Scr[0] < halfAResolution_X)
				control_roll_lr(getMaxVelRollLR() * maxMotionRate/* * (halfAResolution_X - point_on_Scr[0] * 2 + point_on_Old[0])*/);
			}
		else if(getMaxVelRollUp() < getMaxVelTurnLR())
		{
			if(point_on_Scr[1] > halfAResolution_Y)
				control_roll_lr(-getMaxVelRollLR() * maxMotionRate/* * (point_on_Scr[1] * 2 - point_on_Old[1] - halfAResolution_Y)*/);
			else if(point_on_Scr[1] < halfAResolution_Y)
				control_roll_lr(getMaxVelRollLR() * maxMotionRate/* * (halfAResolution_Y) - point_on_Scr[1] * 2 + point_on_Old[1]*/);
		}
		
		if(point_on_Scr[0] > halfAResolution_X)
			control_turn_lr(getMaxVelTurnLR() * maxMotionRate/* * (point_on_Scr[0] * 2 - point_on_Old[0] - halfAResolution_X)*/);
		else if(point_on_Scr[0] < halfAResolution_X)
			control_turn_lr(-getMaxVelTurnLR() * maxMotionRate/* * (halfAResolution_X - point_on_Scr[0] * 2 + point_on_Old[0])*/);
		
		if(point_on_Scr[1] > halfAResolution_Y)
			control_roll_up_dn(-getMaxVelRollUp() * maxMotionRate/* * (point_on_Scr[1] * 2 - point_on_Old[1] - halfAResolution_Y)*/);
		else if(point_on_Scr[1] < halfAResolution_Y)
			control_roll_up_dn(getMaxVelRollUp() * maxMotionRate/* * (halfAResolution_Y - point_on_Scr[1] * 2 + point_on_Old[1])*/);
		
		//System.arraycopy(point_on_Scr, 0, point_on_Old, 0, 2);
	}
	
	public void trace()
	{
		if(tracingTarget != null  &&  tracingTarget.isAlive())
		{		
			float range_to_Scr = CharFrapsCamera.getXY_onCamera (
				tracingTarget.location[0], tracingTarget.location[1], tracingTarget.location[2], 
				scrResolution[0], scrResolution[1], location, getCameraRollAngle(), point_on_Scr, 2.6F
			);
			
			if(range_to_Scr > 0) {
				pursuit(range_to_Scr);
				
				if(
					range_to_Scr < maxSearchingRange    &&    
					GraphicUtils.rangeXY(point_on_Scr[0], point_on_Scr[1], halfAResolution_X, halfAResolution_Y) < 24
				) {
					cannonOpenFire();
					Aircraft aJet;
					currentMaxLockingPriority = 0;
					lockingSelected = false;
					
					for(ThreeDs a : aircrafts)
					{
						aJet = (Aircraft) a;
						if(aJet.getCamp() == getCamp()  ||  !aJet.isAlive()) continue;
						
						range_to_Scr = CharFrapsCamera.getXY_onCamera
						(
							aJet.location[0], aJet.location[1], aJet.location[2], 
							scrResolution[0], scrResolution[1], location, getCameraRollAngle(), point_on_Scr, 2.6F
						);
						
						if
						(
							range_to_Scr > 0    &&    range_to_Scr < maxSearchingRange    &&    
							GraphicUtils.rangeXY(point_on_Scr[0], point_on_Scr[1], halfAResolution_X, halfAResolution_Y) < 24
						)
						{
							if(currentSelectObj == null || currentMaxLockingPriority < GraphicUtils.abs(aJet.getLockingPriority()) && !currentSelectObj.getID().equals(aJet.getID()))
							{	//当前选择目标切换到优先级更高的	(发生切换)
								if(aJet.getLockingPriority() > 0)
									tracingTarget			= aJet;
								currentMaxLockingPriority	= (short) GraphicUtils.abs(aJet.getLockingPriority());
								currentSelectObj			= aJet;
								lockTimeLeft				= lockTime;
								locked						= false;
								lockingSelected				= true;
							}
							else
							{
								if(currentSelectObj!=null && aJet!=null && currentSelectObj.getID().equals(aJet.getID()))
								{
									lockingSelected = true;
									if(locked)
									{
										if(aJet.getLockingPriority() >= 0) aJet.warning(this);
										if(missileMagazineLeft > 0)   missileOpenFire(false, aJet);
									}
									else
									{
										if(--lockTimeLeft <= 0)
										{
											locked = true;
											if(tracingTarget.getLockingPriority() >= 0) tracingTarget.warning(this);
											if(missileMagazineLeft > 0)            missileOpenFire(false, aJet);
										}
										else
										{
											if(tracingTarget.getLockingPriority() >= 0)
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
					
					if(currentSelectObj!=null && currentSelectObj.getLockingPriority() >= 0)
						currentSelectObj.warning(this);
				}
				else cannonStopFiring();
			}
			else escape();//被咬尾，尝试逃离
		}
		else cannonStopFiring();
	}
	
	public void cruise(
		int motionRate,
		float maxRollAngle_lr,
		float minAcc,
		float time_street,
		float time_turn,
		float time_up_dn
	) {
			currentMaxLockingPriority = 0;
			//if(missileMagazineLeft < missileMagazine) missileOpenFire(false, null); //清空弹舱，重新装填
			
			if(getControl_stick_acc() < minAcc)      control_acc();
			else if(getControl_stick_acc() > minAcc) control_dec();
			
			if(--goStreetTime > 0) {
				
				if(roll_angle[2] < 0)
					control_roll_lr(getMaxVelRollLR() / motionRate);
				else if(roll_angle[2] > 0)
					control_roll_lr(getMaxVelRollLR() / -motionRate);
				
				if(location[0] < -3500 && roll_angle[1] > -15)
					control_roll_up_dn(getMaxVelRollLR() / -motionRate);		//down
				else if(location[0] > -1250 && roll_angle[1] < 30)
					control_roll_up_dn(getMaxVelRollLR() / motionRate);		//up
				else {
					if(--roll_up_dn_Time > 0) {
						if(turnUp && roll_angle[1] < maxRollAngle_lr)
							control_roll_up_dn(getMaxVelRollLR() / motionRate);
						else if(!turnUp && roll_angle[1] > -maxRollAngle_lr)
							control_roll_up_dn(getMaxVelRollLR() / -motionRate);
					} else {
						if(roll_angle[1] > 0)
							control_roll_up_dn(getMaxVelRollLR() / -motionRate);		//down
						else if(roll_angle[1] < 0)
							control_roll_up_dn(getMaxVelRollLR() / motionRate);		//up
					}
				}
			}
			else if(--turnLRTime > 0) {
				if(turnRight) {
					if(roll_angle[2] > -maxRollAngle_lr)
						control_roll_lr(getMaxVelRollLR() / -motionRate);	//l
					else if(roll_angle[2] < -maxRollAngle_lr)
						control_roll_lr(getMaxVelRollLR() / motionRate);	//r
					
					roll_up_dn(velocity_roll[1] * 2 * GraphicUtils.sin(GraphicUtils.toRadians(maxRollAngle_lr)) 
					        / GraphicUtils.cos(GraphicUtils.toRadians(maxRollAngle_lr)));
					
					control_turn_lr(getMaxVelRollLR() / motionRate);
				} else {
					if(roll_angle[2] < maxRollAngle_lr)
						control_roll_lr(getMaxVelRollLR() / motionRate);	//r
					else if(roll_angle[2] > maxRollAngle_lr)
						control_roll_lr(getMaxVelRollLR() / -motionRate);	//l
					
					roll_up_dn(-velocity_roll[1] * 2 * GraphicUtils.sin(GraphicUtils.toRadians(maxRollAngle_lr)) 
					        / GraphicUtils.cos(GraphicUtils.toRadians(maxRollAngle_lr)));
	
					control_turn_lr(getMaxVelRollLR() / -motionRate);
				}
				
				if(location[0] < -2500 && roll_angle[1] > -15)
					control_roll_up_dn(getMaxVelRollLR() / -motionRate);		//down
				else if(location[0] > -1250 && roll_angle[1] < 30)
					control_roll_up_dn(getMaxVelRollLR() / motionRate);		//up
			} else {
				boolean RandomFlg = ((GraphicUtils.fastRanodmInt() & 1) == 0? true : false);
				if(RandomFlg)
					goStreetTime = (int) (GraphicUtils.random() * time_street);
				else {
					turnLRTime = (int) (GraphicUtils.random() * time_turn);
					turnRight  = ((GraphicUtils.fastRanodmInt() & 1) == 0? true : false);
				}
				roll_up_dn_Time = (int) (GraphicUtils.random() * time_up_dn);
				turnUp  = ((GraphicUtils.fastRanodmInt() & 1) == 0? true : false);
			}
	}

	public void cruise() { cruise(8, 30, 18, 750, 500, 250); }
	public void escape() { cruise(4, 30, 22, 500, 500, 250); }
	
	@Override
	public Missile missileOpenFire(boolean cameraTrace, Aircraft target)
	{
		if(missileMagazineLeft <= 0 || missileFireWaitingTimeLeft > 0 || !isAlive()) return null;
		Missile m = newMissile(cameraTrace, target);
		missileFireWaitingTimeLeft = missileFireWaitingTime;
		return m;
	}
	
	@Override
	public void go() {
		getCameraRollAngle()[0] = -roll_angle[0];
		getCameraRollAngle()[1] = -roll_angle[1];
		getCameraRollAngle()[2] = -roll_angle[2];
		
		if(locked_By != null)
			tracingTarget = locked_By;
		
		float range_to_Scr;
		Aircraft aJet;
		
		if(tracingTarget == null) {
			currentMaxLockingPriority = 0;
			lockingSelected = false;
			for(ThreeDs a : aircrafts) {
				aJet = (Aircraft) a;
				if(aJet.getCamp() == getCamp()  ||  !aJet.isAlive())
					continue;
				
				range_to_Scr = CharFrapsCamera.getXY_onCamera(
					aJet.location[0], aJet.location[1], aJet.location[2], 
					scrResolution[0], scrResolution[1], location, getCameraRollAngle(), point_on_Scr, 2.6F
				);
				
				if(
					range_to_Scr    > 0 && range_to_Scr    < maxSearchingRange &&    
					point_on_Scr[0] > 0 && point_on_Scr[0] < scrResolution[0]  &&
					point_on_Scr[1] > 0 && point_on_Scr[1] < scrResolution[1]
					
				) {
					//point_on_Old[0] = point_on_Scr[0];
					//point_on_Old[1] = point_on_Scr[1];
					
					if(currentSelectObj == null || currentMaxLockingPriority < GraphicUtils.abs(aJet.getLockingPriority()) && !currentSelectObj.getID().equals(aJet.getID()))
					{
						if(aJet.getLockingPriority() > 0)
							tracingTarget			= aJet;
						currentMaxLockingPriority	= (short) GraphicUtils.abs(aJet.getLockingPriority());
						currentSelectObj			= aJet;
						lockTimeLeft				= lockTime;
						locked						= false;
						lockingSelected				= true;
					}
				}
			}
			
			if(!lockingSelected) {
				currentMaxLockingPriority = 0;
				currentSelectObj		  = null;
				lockTimeLeft			  = lockTime;
				tracingTarget			  = null;
			}
			
			if(currentSelectObj!=null && currentSelectObj.getLockingPriority() > 0)
				currentSelectObj.warning(this);
		}
		
		if(tracingTarget != null && tracingTarget.isAlive() && tracingTarget.getLockingPriority() > 0 || lockedByEnemy && locked_By.isAlive())
			trace();
		else {
			cannonStopFiring();
			cruise();
		}
		
		if(--lockingLife <= 0) {
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
		setID(newName);
	}
	
	public void rename()
	{
		setID("NPC " + (int)(GraphicUtils.random()*100));
	}
}
