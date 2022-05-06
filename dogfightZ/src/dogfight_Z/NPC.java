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
	public Aircraft    tracingTarget;
	public float		maxMotionRate;
	private	float		halfAResolution_X;
	private float		halfAResolution_Y;
	private float		maxSearchingRange;
	public Aircraft		currentSelectObj;
	public boolean     locked;
	public boolean     lockingSelected;
    public int         lockTimeLeft;
	public int		   lockTime;
	public int		   currentMaxLockingPriority;
	public int		   scrResolution[];
	public float       point_on_Scr[];
	//public float     point_on_Old[];
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
		int					    camp,
		PriorityQueue<Dynamic>	firedAmmo, 
		LinkedList<ThreeDs>		add_que,
		LinkedList<ListIterator<ThreeDs>>		delete_que,
		LinkedListZ<ThreeDs>	Aircrafts
	) {
		super(theGameManager, modelFile, Mess, camp, Aircrafts, null, id, DrawingMethod.drawLine);
		scrResolution		= new int[2];
		point_on_Scr		= new float[2];
		poScreen            = new float[2];
		//point_on_Old		= new float[2];
		maxMotionRate		= max_motionRate;
		maxSearchingRange	= searching_visibility;
		scrResolution[0]	= getGameManager().getResolution()[0];
		scrResolution[1]	= getGameManager().getResolution()[1];
		halfAResolution_X	= scrResolution[0] >> 1;
		halfAResolution_Y	= scrResolution[1] >> 1;
		tracingTarget		= null;
		currentSelectObj	= null;
		setPlayer(false);
		goStreetTime		= GraphicUtils.randomInt(500);
		turnLRTime			= 0;
		roll_up_dn_Time		= 0;
		lockTime			= 100;
		missileFireWaitingTime	= missileFireWaitingTimeLeft = 4;
		turnRight			= (GraphicUtils.random() > 0.5? true : false);
		turnUp  			= (GraphicUtils.random() > 0.5? true : false);
		currentMaxLockingPriority = 0;
		buf_a               = maxMotionRate / halfAResolution_Y;
	}
	
	private final float buf_a;
	
	public void pursuit(float range_to_me) {
		if(getSpeed() < tracingTarget.getSpeed() || range_to_me > 10000)
			control_acc();
		else if(getSpeed() > tracingTarget.getSpeed() && getSpeed() > getMinStableSpeed())
			control_dec();

        if(getMaxVelRollUp() > getMaxVelTurnLR()  &&  GraphicUtils.abs(directionXYZ[2]) < RAD30) {
            control_roll_lr((halfAResolution_X - poScreen[0]) * maxMotionRate);
        } else if(getMaxVelRollUp() < getMaxVelTurnLR()) {
            control_roll_lr((halfAResolution_Y - poScreen[1]) * maxMotionRate);
        }
		
        control_turn_lr((poScreen[0] - halfAResolution_X) * buf_a);
        control_roll_up_dn((halfAResolution_Y - poScreen[1]) * buf_a);
		//System.arraycopy(point_on_Scr, 0, point_on_Old, 0, 2);
	}
	
    private static final float 
        RAD15 = 0.26179938779914946F, 
        RADnga15 = -RAD15, 
        RAD30 = 0.5235987755982989F, 
        RADnga30 = -RAD30;
   
	public void cruise(
		float motionRate,
		float maxRollAngle_lr,
		float minAcc,
		float time_street,
		float time_turn,
		float time_up_dn
	) {
		//if(missileMagazineLeft < missileMagazine) missileOpenFire(false, null); //清空弹舱，重新装填
		
		if(getControl_stick_acc() < minAcc)      control_acc();
		else if(getControl_stick_acc() > minAcc) control_dec();
		
		float ry = getCurrentDirectionXYZ()[1], rz = roll_angle[2], 
		        tmp = motionRate, tmp2 = RAD15 / tmp, tmp3 = RADnga15 / tmp;
		
		if(--goStreetTime > 0) {
			//侧滚改正
            control_roll_lr(-rz / tmp);
            
			//超出限制高度或者低度
            if(location[0] < -3500  &&  ry > RADnga30) {
                if((GraphicUtils.fastRanodmInt() & 1) == 0)
                    turnUp  = ((GraphicUtils.fastRanodmInt() & 1) == 0? true : false);//太高
                else roll_up_dn_Time = 0;
                control_roll_up_dn(tmp3); //down
            } else if(location[0] > -1250  &&  ry < RAD30) {
                if((GraphicUtils.fastRanodmInt() & 1) == 0)
                    turnUp  = ((GraphicUtils.fastRanodmInt() & 1) == 0? true : false); //太低
                else roll_up_dn_Time = 0;
                control_roll_up_dn(tmp2);    //up
            }  else {
				if(--roll_up_dn_Time > 0) {
					if(turnUp && ry < maxRollAngle_lr)
						control_roll_up_dn(tmp2);
					else if(!turnUp && ry > -maxRollAngle_lr)
						control_roll_up_dn(tmp3);
				}
			}
            if(roll_up_dn_Time <= 0) control_roll_up_dn((-ry) / tmp);
            else control_roll_up_dn((RAD15 - ry) / tmp);
		} else if(--turnLRTime > 0) {
		    
		    tmp2 = getMaxVelTurnLR() / tmp;
		    
			if(turnRight) {
				control_roll_lr((-maxRollAngle_lr - rz) / tmp);	
                control_roll_up_dn(GraphicUtils.sin(maxRollAngle_lr) * tmp2);
                control_turn_lr(GraphicUtils.cos(maxRollAngle_lr) * tmp2);
			} else {
                control_roll_lr((maxRollAngle_lr - rz) / tmp); 
                control_roll_up_dn(GraphicUtils.sin(maxRollAngle_lr) * tmp2);
                control_turn_lr(GraphicUtils.cos(maxRollAngle_lr) * -tmp2);
			}
			
			if(location[0] < -3500 ||  location[0] > -1250) turnLRTime = 0;
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

	public void cruise() { cruise(48, GraphicUtils.RAD45, 20, 750, 500, 125); }
	public void escape() { cruise(32, GraphicUtils.RAD90, 21, 500, 500, 125); }
	
	@Override
	public Missile missileOpenFire(boolean cameraTrace, Aircraft target)
	{
		if(missileMagazineLeft <= 0 || (missileFireWaitingTimeLeft--) > 0 || !isAlive()) return null;
		Missile m = newMissile(cameraTrace, target);
		missileFireWaitingTimeLeft = missileFireWaitingTime;
		return m;
	}

    @Override
	protected final void randomRespawn() {
	    super.randomRespawn();
	    tracingTarget    = null;
        currentSelectObj = null;
        lockTimeLeft     = lockTime;
        locked           = false;
        lockingSelected  = false;
        currentMaxLockingPriority = 0;
	}
	
    private float poScreen[];
    private float rge;
    
	@Override
	public void go() {

        if(currentSelectObj != null && currentSelectObj.getLockingPriority() > 0 && locked && missileMagazineLeft > 0) missileOpenFire(false, currentSelectObj);
        
        defaultGo();
        
		getCameraRollAngle()[0] = -roll_angle[0];
		getCameraRollAngle()[1] = -roll_angle[1];
		getCameraRollAngle()[2] = -roll_angle[2];
		
		tracingTarget = null;
        float range_to_Scr;
        Aircraft aJet;
        lockingSelected = false;
        //找到当前能看到的最高优先级的敌机
        for(ThreeDs a : aircrafts) {
            aJet = (Aircraft) a;
            if(aJet.getCamp() == getCamp()  ||  !aJet.isAlive()) continue;
            
            range_to_Scr = CharFrapsCamera.getXY_onCamera(
                aJet.location[0], aJet.location[1], aJet.location[2], 
               scrResolution[0], scrResolution[1], location, getCameraRollAngle(), point_on_Scr, fov_1stPerson
            );
            
            if(range_to_Scr > maxSearchingRange) continue;
            
            if(
                range_to_Scr > 0 &&    
                point_on_Scr[0] >= 0 && point_on_Scr[0] < scrResolution[0]  &&
                point_on_Scr[1] >= 0 && point_on_Scr[1] < scrResolution[1]
               
            ) { //寻找新的目标 或 切换目标
                if(tracingTarget == null || currentSelectObj == null || tracingTarget.getLockingPriority() < aJet.getLockingPriority()) {
                    if(aJet.getLockingPriority() > 0) {
                       tracingTarget = aJet;
                       //tracingTargetOnScreen = true;
                       poScreen[0] = point_on_Scr[0];
                       poScreen[1] = point_on_Scr[1];
                       rge = range_to_Scr;
                    }
                }
                
                rangeXY = GraphicUtils.rangeXY(point_on_Scr[0], point_on_Scr[1], halfAResolution_X, halfAResolution_Y);
                if(rangeXY < 24) {
                    if(rangeXY < 12) cannonOpenFire();
                    
                    if(currentSelectObj == null || 
                            GraphicUtils.abs(currentSelectObj.getLockingPriority()) < 
                                GraphicUtils.abs(aJet.getLockingPriority()) 
                    ) {
                        if(aJet.getLockingPriority() > 0) {
                            tracingTarget = aJet;
                            //tracingTargetOnScreen = true;
                            poScreen[0] = point_on_Scr[0];
                            poScreen[1] = point_on_Scr[1];
                            rge = range_to_Scr;
                        }
                        currentSelectObj = aJet;
                        locked = false;
                        lockingSelected = true;
                        lockTimeLeft = lockTime;
                    } else {//已有选择的目标
                        if(currentSelectObj == aJet) {
                            lockingSelected = true;
                            if(aJet.getLockingPriority() > 0)  aJet.warning(this);
                            if(!locked && --lockTimeLeft <= 0) locked = true; 
                        }
                    }
                }
           } else if(aJet == tracingTarget) rge = -1.0F;
        }
        
        if(!lockingSelected) {
            currentSelectObj = null;
            locked = false;
            lockTimeLeft = lockTime;
        }
        if(locked_By != null && currentSelectObj != locked_By && !locked) tracingTarget = locked_By;
        
		if(rge > 0 && tracingTarget != null && tracingTarget.isAlive() && tracingTarget.getLockingPriority() > 0)
            pursuit(rge);
		else if(lockedByEnemy && locked_By.isAlive()) {
            cannonStopFiring();
		    escape();
		} else {
            cannonStopFiring();
			cruise();
		}
		
		if(--lockingLife <= 0) {
			lockedByEnemy = false;
			locked_By     = null;
		}
	}
	
    private float rangeXY;
	
	@Override
	public void warning(Aircraft source) {
		if(lockedByEnemy) makeDecoy();
		
		if(missileFireWaitingTimeLeft > 0)
			--missileFireWaitingTimeLeft;
		
		locked_By			= source;
		lockedByEnemy		= true;
		lockingLife			= 20;
	}
	
	public void rename(String newName) {
		setID(newName);
	}
	
	public void rename() {
		setID("NPC " + GraphicUtils.fastRanodmInt());
	}
}
