package dogfight_Z;

import java.util.List;

import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.GraphicUtils;

public class PlayersJetCamera extends CharFrapsCamera
{
	public short thisCamp;
	public CharDynamicHUD hudFriends;
	public CharDynamicHUD hudLocking;
	public CharDynamicHUD hudEnemy;
	public CharDynamicHUD hudLocked;
	public CharLabel	  hudDistance;
	public CharDynamicHUD hudWarning_missile;
	
	public double 		  maxSearchingRange;
	public Aircraft		  myJet;
	//-------------------------------------------------
	public Aircraft		  currentSelectObj;
	public boolean		  locked;
	public boolean		  lockingSelected;
	public short		  lockTime;
	public short		  lockTimeLeft;
	public short		  currentMaxLockingPriority;
	//-------------------------------------------------
	
	private static final char linePixel = '#';
	
	public PlayersJetCamera
	(
		double FOV, double visblt, 
		double max_searchingRange,
		short		this_camp,
		short		lock_time,
		short[] 	resolution_XY, 
		char[][]	frapsBuffer,
		CharWorld	inWhichWorld,
		Aircraft	   my_jet,
		CharDynamicHUD hud_friends,
		CharDynamicHUD hud_enemy,
		CharDynamicHUD hud_locking,
		CharDynamicHUD hud_locked,
		CharDynamicHUD hudWarningMissile,
		List<Iterable<ThreeDs>> static_objLists
	)
	{
		super(FOV, visblt, resolution_XY, frapsBuffer, inWhichWorld, static_objLists);
		thisCamp	= this_camp;
		hudFriends	= hud_friends;
		hudEnemy	= hud_enemy;
		hudLocking	= hud_locking;
		hudWarning_missile = hudWarningMissile;
		myJet		= my_jet;
		hudLocked	= hud_locked;
		lockTime	= lockTimeLeft = lock_time;
		locked		= false;
		lockingSelected = false;
		currentSelectObj = null;
		maxSearchingRange = max_searchingRange;
		currentMaxLockingPriority = 0;

		hudDistance = new CharLabel(frapsBuffer, (short)0, resolution_XY, true);
	}
	
	public void warning_lockedByMissile()
	{
		hudWarning_missile.printNew();
	}
	
	@Override
	public Object exposure()
	{
		reversedAngle[0] = roll_source[0] + 180;
		reversedAngle[1] = -roll_source[1];
		reversedAngle[2] = -roll_source[2];
		
		double locationOfanObj[]  = new double[3];
		/*
		double rollAngleOfanObj[] = new double[3];
		double aPointOfanObj[]    = new double[3];
		short  X1, Y1;
		char spc;*/
		lockingSelected = false;

		if(myJet.lockedByEnemy) warning_lockedByMissile();

		Aircraft a = null;
		double point_on_Scr[] = new double[2];

		double rge;
		
		for(ThreeDs aObject:inWorld.objectsManager.objects)	//for each object
		{
			if(aObject instanceof Aircraft) a = (Aircraft)aObject; else a = null;
			locationOfanObj = aObject.getLocation();
			/*
			spc = aObject.getSpecialDisplayChar();
			rollAngleOfanObj= aObject.getRollAngle();

			r0 = rad(rollAngleOfanObj[0]);
			r1 = rad(rollAngleOfanObj[1]);
			r2 = rad(rollAngleOfanObj[2]);
			*/
			rge = exposureObject(aObject, rad(roll_angle[0]), rad(roll_angle[1]), rad(roll_angle[2]), false);
			
			if(rge > maxSearchingRange) try
			{
				Aircraft tmp = (Aircraft)aObject;
				if(!tmp.ID.equals(myJet.ID))	//防止视角跟随导弹行进时将自己的进行战机拉回
					tmp.pollBack();
			} catch(ClassCastException e) {}
			
			if(a==null || a.ID.equals(myJet.ID) || !a.isAlive || !myJet.isAlive) continue;
			
			double range_to_Scr = CharFrapsCamera.getXY_onCamera
			(
				locationOfanObj[0], locationOfanObj[1], locationOfanObj[2], 
				resolution[0], resolution[1], location, roll_angle, point_on_Scr, FOV
			);
			
			if
			(
				range_to_Scr > 0    &&    range_to_Scr < maxSearchingRange    &&    
				rangeXY(point_on_Scr[0], point_on_Scr[1], XcenterI, YcenterI) < 24
			)
			{
				hudDistance.setText(String.format("%.0f", range_to_Scr));
				if(a.camp == thisCamp) //友军
				{
					hudFriends.location[0] = (short) point_on_Scr[0];
					hudFriends.location[1] = (short) point_on_Scr[1];
					hudFriends.printNew();	//盖章
					hudDistance.setLocation((short)(hudFriends.location[0] - hudFriends.center_X), (short)(hudFriends.location[1] + hudFriends.center_Y + 1));
					hudDistance.printNew();
				}
				else //敌军
				{
					if(GraphicUtils.absI(currentMaxLockingPriority) < GraphicUtils.absI(a.lockingPriority)  &&  !reversed)
					{	//当前选择目标切换到优先级更高的	(发生切换)
						currentMaxLockingPriority	= a.lockingPriority;
						currentSelectObj			= a;
						lockTimeLeft				= lockTime;
						locked						= false;
						lockingSelected				= true;
						
						hudLocking.location[0] = (short) point_on_Scr[0];
						hudLocking.location[1] = (short) point_on_Scr[1];
						hudLocking.printNew();	//盖章
						GraphicUtils.drawLine(fraps_buffer, XcenterI, YcenterI, (int)point_on_Scr[0], (int)point_on_Scr[1], linePixel);
						hudDistance.setLocation((short)(point_on_Scr[0] - hudLocking.center_X), (short)(point_on_Scr[1] + hudLocking.center_Y));
						hudDistance.printNew();
					}
					else //未发生优先级切换
					{
						if(currentSelectObj!=null && a!=null && currentSelectObj.ID.equals(a.ID)  &&  !reversed)//
						{//如果正在锁定或者已锁定了a
							lockingSelected = true;//设置已选择锁定a的状态
							if(locked)//已锁定
							{
								hudLocked.location[0] = (short) point_on_Scr[0];
								hudLocked.location[1] = (short) point_on_Scr[1];
								
								GraphicUtils.drawLine(fraps_buffer, XcenterI, YcenterI, (int)point_on_Scr[0], (int)point_on_Scr[1], linePixel);
								a.warning(myJet);
								
								hudDistance.setLocation((short)(point_on_Scr[0] - hudLocked.center_X), (short)(point_on_Scr[1] + hudLocked.center_Y));
								hudDistance.printNew();
							}
							else//正在锁定
							{
								if(--lockTimeLeft <= 0)//刚好锁定
								{
									locked = true;
									hudLocked.location[0] = (short) point_on_Scr[0];
									hudLocked.location[1] = (short) point_on_Scr[1];
									hudLocked.printNew();	//盖章

									GraphicUtils.drawLine(fraps_buffer, XcenterI, YcenterI, (int)point_on_Scr[0], (int)point_on_Scr[1], linePixel);
									a.warning(myJet);
									
									hudDistance.setLocation((short)(point_on_Scr[0] - hudLocked.center_X), (short)(point_on_Scr[1] + hudLocked.center_Y));
									hudDistance.printNew();
									
									lockTimeLeft = lockTime;
								}
								else //还未锁定
								{
									hudLocking.location[0] = (short) point_on_Scr[0];
									hudLocking.location[1] = (short) point_on_Scr[1];
									hudLocking.printNew();	//盖章

									GraphicUtils.drawLine(fraps_buffer, XcenterI, YcenterI, (int)point_on_Scr[0], (int)point_on_Scr[1], linePixel);
									a.warning(myJet);
									
									hudDistance.setLocation((short)(point_on_Scr[0] - hudLocking.center_X), (short)(point_on_Scr[1] + hudLocking.center_Y));
									hudDistance.printNew();
								}
							}
						}
						else//没有选择锁定的敌机
						{
							hudEnemy.location[0] = (short) point_on_Scr[0];
							hudEnemy.location[1] = (short) point_on_Scr[1];
							hudEnemy.printNew();//盖章
							hudDistance.setLocation((short)(point_on_Scr[0] - hudEnemy.center_X), (short)(point_on_Scr[1] + hudEnemy.center_Y));
							hudDistance.printNew();
						}
					}
				}
			}
		}
		
		
		if(!lockingSelected) {
			currentMaxLockingPriority = 0;
			lockTimeLeft			  = lockTime;
		} else if(locked) {
			hudLocked.printNew();	//盖章
			
			GraphicUtils.drawCircle(fraps_buffer, hudLocked.location[0], hudLocked.location[1], 5, '+');
			return currentSelectObj;
		}
		
		return null;
	}
	
}
