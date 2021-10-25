package dogfight_Z;

import java.util.List;

import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharWorld;

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
	public Aircraft		  currentSelectObj;
	public Aircraft		  myJet;
	public boolean		  locked;
	public boolean		  lockingSelected;
	public short		  lockTime;
	public short		  lockTimeLeft;
	public short		  currentMaxLockingPriority;
	
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
			if((rge < visibility)) {/*(range = range(locationOfanObj, location)) < visibility) {
				if(aObject.getVisible() == true)
				{
					pcount = aObject.getPointsCount();
					for(int i=0 ; i<pcount ; ++i)				//for each point
					{
						aPointOfanObj = aObject.getPoint(i);
						//获取点随着物体分别绕X、Y、Z坐标轴滚动前的原坐标
						X0 = aPointOfanObj[0];
						Y0 = aPointOfanObj[1];
						Z0 = aPointOfanObj[2];
						
						tmp1 = Math.atan2(Y0, X0)+r2;
						tmp2 = Math.sqrt(X0*X0+Y0*Y0);
						//---自身旋转---
						X = GraphicUtils.cos(tmp1)*tmp2;
						Y = GraphicUtils.sin(tmp1)*tmp2;
						
						Y0 = Y;
						X0 = X;
						
						tmp1 = Math.atan2(Z0, X0)+r1;
						tmp2 = Math.sqrt(X0*X0+Z0*Z0);
						
						X = GraphicUtils.cos(tmp1)*tmp2;
						Z = GraphicUtils.sin(tmp1)*tmp2;
						Z0 = Z;
						X0 = X;
						
						tmp1 = Math.atan2(Y0, Z0)+r0;
						tmp2 = Math.sqrt(Z0*Z0+Y0*Y0);
						
						Z = GraphicUtils.cos(tmp1)*tmp2;
						Y = GraphicUtils.sin(tmp1)*tmp2;
						Y0 = Y;
						Z0 = Z;
						//---旋转结束---
						
						X0 += locationOfanObj[0] - location[0];
						Y0 += locationOfanObj[1] - location[1];
						Z0 += locationOfanObj[2] - location[2];
						
						//---围绕摄像机旋转(或相对的，摄像机原地左右上下转动)---
						tmp1 = Math.atan2(Y0, Z0)+cr0;
						tmp2 = Math.sqrt(Z0*Z0+Y0*Y0);
						Z = GraphicUtils.cos(tmp1)*tmp2;
						Y = GraphicUtils.sin(tmp1)*tmp2;
						Y0 = Y;
						Z0 = Z;
						
						tmp1 = Math.atan2(Z0, X0)+cr1;
						tmp2 = Math.sqrt(X0*X0+Z0*Z0);
						X = GraphicUtils.cos(tmp1)*tmp2;
						Z = GraphicUtils.sin(tmp1)*tmp2;
						Z0 = Z;
						X0 = X;
						//---旋转结束---
						
						if(Z0>=0)
						{
							tmp1 = Xcenter*FOV/(Xcenter+temp*Z0);
							X0 = X0 * tmp1;
							Y0 = Y0 * tmp1;
							
							//屏幕视角绕Z轴转动
							tmp1 = Math.atan2(Y0, X0)+cr2;
							tmp2 = Math.sqrt(X0*X0+Y0*Y0);
							X = GraphicUtils.cos(tmp1)*tmp2;
							Y = GraphicUtils.sin(tmp1)*tmp2;
							
							X1 = (short) (Y+Xcenter);
							Y1 = (short) (X+Ycenter);
							
							if(X1>=0 && Y1>=0 && X1<resolution[0] && Y1<resolution[1])
							{
								int index = (int)Z0 / 64;
								if(index < 0) index = 0;
								else if(index > 7) index = 7;
								
								fraps_buffer[Y1][X1] = (spc =='\0'? point[index] : spc);
							}
						}
					}
				}*/
			}
			else if(rge > maxSearchingRange) try
			{
				Aircraft tmp = (Aircraft)aObject;
				if(!tmp.ID.equals(myJet.ID))	//防止视角跟随导弹行进时将自己的进行战机拉回
					tmp.pollBack();
			} catch(ClassCastException e) {}
			
			if(a==null || a.ID.equals(myJet.ID) || !a.isAlive) continue;
			
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
				hudDistance.setText(String.format("%.2f", range_to_Scr));
				if(a.camp == thisCamp)
				{
					hudFriends.location[0] = (short) point_on_Scr[0];
					hudFriends.location[1] = (short) point_on_Scr[1];
					hudFriends.printNew();	//盖章
					hudDistance.setLocation((short)(point_on_Scr[0] - hudFriends.center_X), (short)(point_on_Scr[1] + hudFriends.center_Y + 1));
					hudDistance.printNew();
				}
				else
				{
					if(currentMaxLockingPriority < Math.abs(a.lockingPriority)    &&    !reversed)
					{	//当前选择目标切换到优先级更高的	(发生切换)
						currentMaxLockingPriority	= a.lockingPriority;
						currentSelectObj			= a;
						lockTimeLeft				= lockTime;
						locked						= false;
						lockingSelected				= true;
						
						hudLocking.location[0] = (short) point_on_Scr[0];
						hudLocking.location[1] = (short) point_on_Scr[1];
						hudLocking.printNew();	//盖章
						hudDistance.setLocation((short)(point_on_Scr[0] - hudLocking.center_X), (short)(point_on_Scr[1] + hudLocking.center_Y));
						hudDistance.printNew();
					}
					else
					{
						if(currentSelectObj!=null && a!=null && currentSelectObj.ID.equals(a.ID)    &&    !reversed)//
						{
							lockingSelected = true;
							if(locked)
							{
								hudLocked.location[0] = (short) point_on_Scr[0];
								hudLocked.location[1] = (short) point_on_Scr[1];
								hudLocked.printNew();	//盖章

								a.warning(myJet);
								
								hudDistance.setLocation((short)(point_on_Scr[0] - hudLocked.center_X), (short)(point_on_Scr[1] + hudLocked.center_Y));
								hudDistance.printNew();
							}
							else
							{
								if(--lockTimeLeft <= 0)
								{
									locked = true;
									hudLocked.location[0] = (short) point_on_Scr[0];
									hudLocked.location[1] = (short) point_on_Scr[1];
									hudLocked.printNew();	//盖章

									a.warning(myJet);
									
									hudDistance.setLocation((short)(point_on_Scr[0] - hudLocked.center_X), (short)(point_on_Scr[1] + hudLocked.center_Y));
									hudDistance.printNew();
									
									lockTimeLeft = lockTime;
								}
								else
								{
									hudLocking.location[0] = (short) point_on_Scr[0];
									hudLocking.location[1] = (short) point_on_Scr[1];
									hudLocking.printNew();	//盖章

									a.warning(myJet);
									
									hudDistance.setLocation((short)(point_on_Scr[0] - hudLocking.center_X), (short)(point_on_Scr[1] + hudLocking.center_Y));
									hudDistance.printNew();
								}
							}
						}
						else
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
		if(!lockingSelected)
		{
			currentMaxLockingPriority = 0;
			lockTimeLeft			  = lockTime;
		}
		else if(locked) return currentSelectObj;
		
		return null;
	}
	
}
