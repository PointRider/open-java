package dogfight_Z;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.HUDs.CharImage;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.GraphicUtils;

public class PlayersJetCamera extends CharFrapsCamera
{
    private int thisCamp;
    private CharImage hudFriends;
	private CharImage hudLocking;
	private CharImage hudEnemy;
	private CharImage hudLocked;
	private CharLabel hudDistance;
	private CharImage hudWarning_missile;
	private CharImage hudWarningLocking;
	private Radar     radar;
	
	private float 		  maxSearchingRange;
	public Aircraft		  myJet;
	//-------------------------------------------------
	public Aircraft		  currentSelectObj;
	public boolean		  locked;
	public boolean		  lockingSelected;
	public int		  lockTime;
	public int		  lockTimeLeft;
	public int		  currentMaxLockingPriority;
	//-------------------------------------------------
	private int       hud_friends_hlfSize[];
    private int       hud_enemy_hlfSize[];
    private int       hud_locking_hlfSize[];
    private int       hud_locked_hlfSize[];

    private final int lockedTime = 100;
    private int lockedLeft = 100;
    //private int       hudWarningMissile_hlfSize[];
    //private int       hud_friends_hlfSize[];
	
	private static final char linePixel = '#';
	
	public PlayersJetCamera
	(
		float         FOV, 
		float         visblt, 
		float         max_searchingRange,
		int            this_camp,
		int            lock_time,
		int[]          resolution_XY, 
		char[][]       frapsBuffer,
		ConcurrentLinkedQueue<char[][]> motional_blur,
		CharWorld      inWhichWorld,
		Aircraft       my_jet,
		CharImage      hud_friends,
		CharImage      hud_enemy,
		CharImage      hud_locking,
		CharImage      hud_locked,
		CharImage      hudWarningMissile,
        CharImage      hudWarningLocking,
		List<Iterable<ThreeDs>> static_objLists
	) {
		super(FOV, visblt, resolution_XY, frapsBuffer, motional_blur, inWhichWorld, static_objLists);
		thisCamp	= this_camp;
		hudFriends	= hud_friends;
		hudEnemy	= hud_enemy;
		hudLocking	= hud_locking;
		setHudWarning_missile(hudWarningMissile);
		this.hudWarningLocking = hudWarningLocking;
		myJet		= my_jet;
		hudLocked	= hud_locked;
		lockTime	= lockTimeLeft = lock_time;
		locked		= false;
		radar       = null;
		lockingSelected = false;
		currentSelectObj = null;
		setMaxSearchingRange(max_searchingRange);
		currentMaxLockingPriority = 0;

		hudDistance = new CharLabel(frapsBuffer, 0, resolution_XY, true);
		
		hud_friends_hlfSize       = new int [] {hud_friends.getSize()      [0] >> 1, hud_friends.getSize()      [1] >> 1};
		hud_enemy_hlfSize         = new int [] {hud_enemy.getSize()        [0] >> 1, hud_enemy.getSize()        [1] >> 1};
		hud_locking_hlfSize       = new int [] {hud_locking.getSize()      [0] >> 1, hud_locking.getSize()      [1] >> 1};
		hud_locked_hlfSize        = new int [] {hud_locked.getSize()       [0] >> 1, hud_locked.getSize()       [1] >> 1};
	  //hudWarningMissile_hlfSize = new int [] {hudWarningMissile.getSize()[0] >> 1, hudWarningMissile.getSize()[1] >> 1};
      //hud_friends_hlfSize = hud_friends.getSize();
		lockedLeft = lockedTime;
		inWhichWorld.execute(new Runnable() {
            @Override
            public void run() {
                //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                try {
                    while(true) {
                        hudWarningLocking.visible = !hudWarningLocking.visible;
                        Thread.sleep(lockedLeft);
                    }
                } catch (InterruptedException e) {}
            }
		});
	}
	
	
	
	/*
	 public CharDynamicHUD hudFriends;
	public CharDynamicHUD hudLocking;
	public CharDynamicHUD hudEnemy;
	public CharDynamicHUD hudLocked;
	public CharLabel	  hudDistance;
	public CharDynamicHUD hudWarning_missile; 
	 */
	
	public final Radar getRadar() {
        return radar;
    }

    public final void setRadar(Radar radar) {
        this.radar = radar;
    }

    public void resizeScreen(int x, int y) {
		super.resizeScreen(x, y);
		hudFriends.reSizeScreen(inWorld.visualManager.resolution, inWorld.visualManager.fraps_buffer);
		hudLocking.reSizeScreen(inWorld.visualManager.resolution, inWorld.visualManager.fraps_buffer);
		hudEnemy.reSizeScreen(inWorld.visualManager.resolution, inWorld.visualManager.fraps_buffer);
		hudLocked.reSizeScreen(inWorld.visualManager.resolution, inWorld.visualManager.fraps_buffer);
		hudDistance.reSizeScreen(inWorld.visualManager.resolution, inWorld.visualManager.fraps_buffer);
		hudWarning_missile.reSizeScreen(inWorld.visualManager.resolution, inWorld.visualManager.fraps_buffer);
		hudWarningLocking.reSizeScreen(inWorld.visualManager.resolution, inWorld.visualManager.fraps_buffer);
	}
	
    public final void setHudWarningLocking(CharImage hudWarningLocking) {
        this.hudWarningLocking = hudWarningLocking;
    }

    public void warning_lockedByMissile() {
		hudWarning_missile.printNew();
	}

    public void warning_locking() {
        hudWarningLocking.printNew();
        if(lockedLeft > 0) --lockedLeft;
    }
    
    private float locationOfanObj[] = new float[3];
    private float point_on_Scr[] = new float[2];
    private float xyzr[] = new float[4];
	@Override
	public Object exposure()
	{
		reversedAngle[0] =  roll_source[0] + GraphicUtils.RAD180;
		reversedAngle[1] = -roll_source[1];
		reversedAngle[2] = -roll_source[2];

        radar.clear();
		/*
		float rollAngleOfanObj[] = new float[3];
		float aPointOfanObj[]    = new float[3];
		short  X1, Y1;
		char spc;*/
		lockingSelected = false;

		if(myJet.lockedByMissile) warning_lockedByMissile();
		else if(myJet.lockedByEnemy) warning_locking();
		else lockedLeft = lockedTime;
		
		Aircraft a = null;
		
		float rge;
		
		int p0, p1;
		float sin$, cos$, y, z;
		
		for(ThreeDs aObject:inWorld.objectsManager.objects)	//for each object
		{
			a = (Aircraft)aObject;
			locationOfanObj = aObject.getLocation();
			/*
			spc = aObject.getSpecialDisplayChar();
			rollAngleOfanObj= aObject.getRollAngle();

			r0 = rad(rollAngleOfanObj[0]);
			r1 = rad(rollAngleOfanObj[1]);
			r2 = rad(rollAngleOfanObj[2]);
			*/
			//rge = exposureObject(aObject, roll_angle[0], roll_angle[1], roll_angle[2], false);
			exposureObject(aObject, false, xyzr);
            cos$ = GraphicUtils.cos(roll_angle[0]);
            sin$ = GraphicUtils.sin(roll_angle[0]);
            y = sin$ * xyzr[2] + cos$ * xyzr[1];
            z = cos$ * xyzr[2] - sin$ * xyzr[1];
            if(a == myJet || !a.isAlive() || !myJet.isAlive()) continue;
            if(a.getLockingPriority() >= 0) {
                if(a.getCamp() == myJet.getCamp()) radar.report(y, z, true);
                else radar.report(y, z, false);
            }
            
			rge = xyzr[3];
			
			if(rge > getMaxSearchingRange()) /*try*/
			{
				if(a != myJet) a.pollBack();	//防止视角跟随导弹行进时将自己的进行战机拉回
			} /*catch(ClassCastException e) {System.err.println(aObject.toString());}*/

			if(/*a==null ||*/ a.getID().equals(myJet.getID()) || !a.isAlive() || !myJet.isAlive()) continue;
			
			float range_to_Scr = CharFrapsCamera.getXY_onCamera
			(
				locationOfanObj[0], locationOfanObj[1], locationOfanObj[2], 
				resolution[0], resolution[1], location, roll_angle, point_on_Scr, FOV
			);
			
            p0 = (int) point_on_Scr[0];
            p1 = (int) point_on_Scr[1];
            
			if
			(
				range_to_Scr > 0    &&    range_to_Scr < getMaxSearchingRange()    &&    
				GraphicUtils.rangeXY(point_on_Scr[0], point_on_Scr[1], XcenterI, YcenterI) < 24
			)
			{
				hudDistance.setText(String.format("%.0f", range_to_Scr));
				if(a.getCamp() == thisCamp) //友军
				{
					hudFriends.location[0] = p0 - hud_friends_hlfSize[0];
					hudFriends.location[1] = p1 - hud_friends_hlfSize[1];
					hudFriends.printNew();	//盖章
					hudDistance.setLocation(p0 - hudFriends.centerX, p1 + hudFriends.centerY + 1);
					hudDistance.printNew();
				}
				else //敌军
				{
					if(/*myJet.missileMagazineLeft > 0  &&  */GraphicUtils.absI(currentMaxLockingPriority) < GraphicUtils.absI(a.getLockingPriority())  &&  !reversed)
					{	//当前选择目标切换到优先级更高的	(发生切换)
						currentMaxLockingPriority	= a.getLockingPriority();
						currentSelectObj			= a;
						lockTimeLeft				= lockTime;
						locked						= false;
						lockingSelected				= true;
					}
					else //未发生优先级切换
					{
						if(/*myJet.missileMagazineLeft > 0  &&  */location == myJet.getCameraLocation() && currentSelectObj!=null && a!=null && currentSelectObj.getID().equals(a.getID())  &&  !reversed)//
						{//如果正在锁定或者已锁定了a
							lockingSelected = true;//设置已选择锁定a的状态
							
							if(locked)//已锁定
							{
								hudLocked.location[0] = p0 - hud_locked_hlfSize[0];
								hudLocked.location[1] = p1 - hud_locked_hlfSize[1];

                                hudLocked.printNew(); //盖章
								GraphicUtils.drawLine(fraps_buffer, XcenterI, YcenterI, p0, p1, linePixel);
								GraphicUtils.drawCircle(fraps_buffer, hudLocked.location[0] + hud_locked_hlfSize[0], hudLocked.location[1] + hud_locked_hlfSize[1], 5, '+');
								hudDistance.setLocation(p0 - hudLocked.centerX, p1 + hudLocked.centerY);
								hudDistance.printNew();
							}
							else//正在锁定
							{
								if(--lockTimeLeft <= 0)//刚好锁定
								{
									locked = true;
									lockTimeLeft = lockTime;
								}
								else //还未锁定
								{
									hudLocking.location[0] = p0 - hud_locking_hlfSize[0];
									hudLocking.location[1] = p1 - hud_locking_hlfSize[1];
									hudLocking.printNew();	//盖章

									GraphicUtils.drawLine(fraps_buffer, XcenterI, YcenterI, p0, p1, linePixel);
									
									hudDistance.setLocation(p0 - hudLocking.centerX, p1 + hudLocking.centerY);
									hudDistance.printNew();
								}
							}
                            a.warning(myJet);
						}
						else//没有选择锁定的敌机
						{
							hudEnemy.location[0] = p0 - hud_enemy_hlfSize[0];
							hudEnemy.location[1] = p1 - hud_enemy_hlfSize[1];
							hudEnemy.printNew();//盖章
							hudDistance.setLocation(p0 - hudEnemy.centerX, p1 + hudEnemy.centerY);
							hudDistance.printNew();
						}
					}
				}
			}
		}
		
		if(!lockingSelected) {
			currentMaxLockingPriority = 0;
			lockTimeLeft			  = lockTime;
		} else if(locked) return currentSelectObj;
		return null;
	}

    public final float getMaxSearchingRange() {
        return this.maxSearchingRange;
    }

    public final void setMaxSearchingRange(float maxSearchingRange) {
        this.maxSearchingRange = maxSearchingRange;
    }

    public final CharImage getHudWarning_missile() {
        return hudWarning_missile;
    }

    public final void setHudWarning_missile(CharImage hudWarning_missile) {
        this.hudWarning_missile = hudWarning_missile;
    }

    public final CharImage getHudWarningLocking() {
        return hudWarningLocking;
    }

}
