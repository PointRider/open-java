package dogfight_Z.dogLog.view.menus;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import dogfight_Z.CloudsManager;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharImage;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharLoopingScrollBar;
import graphic_Z.HUDs.CharProgressBar;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Objects.CharMessObject;
import graphic_Z.Objects.TDObject;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.Common;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.HzController;

public class ScreenResizer extends CharTimeSpace implements Runnable
{
	public double fov = 2.2;
	public double visibility = 12480;
	
	public CharFrapsCamera mainCamera;
	public CharMessObject myJet;
	
	public CharLabel lbl1;
	public CharLabel lbl2;
	public CharLabel lbl3;
	public CharLabel lbl4; 
    public CharLabel lbl5;
    public CharLabel lbl6;
    public CharLabel lbl7;
    public CharLabel lbl8;
    public CharLabel lbl9;
	public CharLabel lblRespawnTimeLeft;
	public CharLabel lblGameTimeLeft;
	public CharLabel lblKillTipList;
	
	public CharLabel lblTop, lblLeft, lblRight, lblBottom;

	public CharLabel 			EndScreen;
	
	public CharDynamicHUD		hud_roll_up_dn_angle;
	
	public CharLoopingScrollBar hud_turn_lr_scrollBar;
	public CharLoopingScrollBar hud_roll_up_dn_scrollBar;
	
	public CharProgressBar		hud_HP_progressBar;
	public CharProgressBar		hud_pushTime_progressBar;
	public CharProgressBar		hud_cannon_progressBar;
	public CharProgressBar		hud_cannonReloading_progressBar;
	public CharProgressBar		hud_missile_progressBar;
	public CharProgressBar		hud_missileReloading_progressBar;
	public CharProgressBar		hud_decoy_progressBar;
	public CharProgressBar		hud_decoyReloading_progressBar;
	
	public CharImage            hud_crosshair;
	public CharImage			hud_Radar;
	
	public int					maxAmmoCount;
	public ArrayList<ThreeDs>	clouds;

	
	public StringBuilder tmp;
	//public ArrayList<HashSet<Aircraft>> camps;
	private CloudsManager cloudMan;
	private Thread        cloudManThread;
	public  HzController  cloudRefreshRateController;
	
	private final int resolution_min = Math.min(visualManager.getResolution_X(), visualManager.getResolution_Y()) >> 1;
	private int keyPressed;
	private int scrSize = 8;
	
	public void initUI(int fontSize) {
        eventManager.setTitle("dogfightZ");
        scrSize = fontSize;
        eventManager.setScrZoom(fontSize);
	}
	
	public void initMainCamera(
	        String hud_friend,
	        String hud_enemy,
	        String hud_locking,
	        String hud_locked,
	        String hud_missileWarning
	    ) {
	    mainCamera = new CharFrapsCamera (
            2.6, 12480, 
            visualManager.resolution, 
            visualManager.fraps_buffer,
            this,
            visualManager.staticObjLists
            
        );
        visualManager.newCamera(mainCamera);
	}
	
	public void initMe(
	        String myJetModel_file, 
            String hud_friend,
            String hud_enemy,
            String hud_locking,
            String hud_locked,
            String hud_missileWarning
        ) {

        myJet = new CharMessObject(myJetModel_file, 10000, true);
        myJet.setLocation(0, 0, 60);
        myJet.specialDisplay = '@';
        objectsManager.newMessObject(myJet);

        initMainCamera(hud_friend, hud_enemy, hud_locking, hud_locked, hud_missileWarning);

        //myJet.mainCamera = mainCamera;
        //mainCamera.connectLocationAndAngle(myJet.cameraLocation, myJet.cameraRollAngle);
        //myJet.randomRespawn();
        myJet.visible = true;
	}
	
	public void playersCameraManage()
    {
	    
        double t1, t2, t3;
        /*
        cameraLocation[0] = -50;
        cameraLocation[1] = 0;
        cameraLocation[2] =  -150;
        */

        mainCamera.location[0] = -240;
        mainCamera.location[1] = 0;
        mainCamera.location[2] = -960;
        
        if(mainCamera.roll_source[0] < 0)
        {
            if((-myJet.roll_angle[0]) - mainCamera.roll_source[0] < 180)
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0];
            else
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0] - 360;
        }
        else if(mainCamera.roll_source[0] > 0)
        {
            if(mainCamera.roll_source[0] - (-myJet.roll_angle[0]) < 180)
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0];
            else
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0] + 360;
        }
        else t1 = -myJet.roll_angle[0];
        
        if(mainCamera.roll_source[1] < 0)
        {
            if((-myJet.roll_angle[1]) - mainCamera.roll_source[1] < 180)
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1];
            else
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1] - 360;
        }
        else if(mainCamera.roll_source[1] > 0)
        {
            if(mainCamera.roll_source[1] - (-myJet.roll_angle[1]) < 180)
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1];
            else
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1] + 360;
        }
        else t2 = -myJet.roll_angle[1];
        
        if(mainCamera.roll_source[2] < 0)
        {
            if((-myJet.roll_angle[2]) - mainCamera.roll_source[2] < 180)
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2];
            else
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2] - 360;
        }
        else if(mainCamera.roll_source[2] > 0)
        {
            if(mainCamera.roll_source[2] - (-myJet.roll_angle[2]) < 180)
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2];
            else
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2] + 360;
        }
        else t3 = -myJet.roll_angle[2];
        
        t1 /= 6;
        t2 /= 6;
        t3 /= 6;
        
        mainCamera.roll_source[0] = (mainCamera.roll_source[0] + t1) % 360;
        mainCamera.roll_source[1] = (mainCamera.roll_source[1] + t2) % 360;
        mainCamera.roll_source[2] = (mainCamera.roll_source[2] + t3) % 360;
    
        TDObject.getXYZ_afterRolling
        (
            mainCamera.location[0], mainCamera.location[1], mainCamera.location[2],
            myJet.roll_angle[0],     myJet.roll_angle[1],     myJet.roll_angle[2],
            
            mainCamera.location
        );

        mainCamera.location[0] += myJet.location[0];
        mainCamera.location[1] += myJet.location[1];
        mainCamera.location[2] += myJet.location[2];
        
        mainCamera.setFOV(2.6);
    }

	public void initHUDs(
	        int resolutionX, int resolutionY, 
	        String hud_horizonIndicator,
	        String hud_crosshairImg,
	        String hud_loopingScrollBar_vertical,
	        String hud_loopingScrollBar_horizon,
	        String hud_radarBG,
	        String hud_radarPrinter
	    ) {
        EndScreen = visualManager.newLabel("      TIME UP\nPress ESC Key To Exit.", ((resolutionX>>1) - 9), (int)(resolutionY * 0.3), 999);
        EndScreen.visible = false;
        
        final int progressBarLocationBaseX = (int) (resolutionX * 0.75);
        final int progressBarLocationBaseY = (int) (resolutionY * 0.65);
        
        lblTop    = visualManager.newLabel(Common.loopChar('X', resolutionX), 0, 0, 65537);
        lblLeft   = visualManager.newLabel(Common.loopStr("X\n", resolutionY), 0, 0, 65538);
        lblRight  = visualManager.newLabel(Common.loopStr("X\n", resolutionY), resolutionX - 1, 0, 65539);
        lblBottom = visualManager.newLabel(Common.loopChar('X', resolutionX), 0, resolutionY - 1, 65540);
        
        lbl1 = visualManager.newLabel(" ", (resolutionX - 18), (int)(resolutionY * 0.3), 100);
        lbl2 = visualManager.newLabel(" ", (resolutionX - 18), (int)(resolutionY * 0.3 + 1), 101);
        lbl3 = visualManager.newLabel(" ", (resolutionX - 18), (int)(resolutionY * 0.3 + 2), 102);
        lbl4 = visualManager.newLabel(" ", (resolutionX - 18), (int)(resolutionY * 0.3 + 3), 103);
        lbl5 = visualManager.newLabel("HP:[                    ]", progressBarLocationBaseX, progressBarLocationBaseY, 204);
        lbl6 = visualManager.newLabel("AB:[                    ]", progressBarLocationBaseX, (progressBarLocationBaseY + 2), 207);
        lbl7 = visualManager.newLabel("CN:[                    ]", progressBarLocationBaseX, (progressBarLocationBaseY + 4), 206);
        lbl8 = visualManager.newLabel("MS:[                    ]", progressBarLocationBaseX, (progressBarLocationBaseY + 6), 208);
        lbl9 = visualManager.newLabel("DC:[                    ]", progressBarLocationBaseX, (progressBarLocationBaseY + 8), 256);
        
        lblRespawnTimeLeft = visualManager.newLabel(" ", ((resolutionX>>1) - 9), (int)(resolutionY * 0.3), 999);
        lblGameTimeLeft = visualManager.newLabel(" ", ((resolutionX>>1) - 11), 3, 998);
        lblKillTipList = visualManager.newLabel(" ", 3, 3, 123);
        
        hud_HP_progressBar          = visualManager.newProgressBar((progressBarLocationBaseX + 4), progressBarLocationBaseY, 205, 20, '|', CharProgressBar.Direction.horizon, 1.0);
        hud_pushTime_progressBar    = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 2), 209, 20, '|', CharProgressBar.Direction.horizon, 1.0);
        
        hud_cannon_progressBar = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 4), 210, 20, '|', CharProgressBar.Direction.horizon, 1.0);
        hud_cannonReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 5), 212, 25, '-', CharProgressBar.Direction.horizon, 1.0);
        hud_missile_progressBar = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 6), 214, 20, '|', CharProgressBar.Direction.horizon, 1.0);
        hud_missileReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 7), 216, 25, '-', CharProgressBar.Direction.horizon, 1.0);
        hud_decoy_progressBar = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 8), 218, 20, '|', CharProgressBar.Direction.horizon, 1.0);
        hud_decoyReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 9), 220, 25, '-', CharProgressBar.Direction.horizon, 1.0);
        
        hud_roll_up_dn_angle        = visualManager.newDynamicHUD(hud_horizonIndicator, 21, 51, 33);
        hud_roll_up_dn_scrollBar    = visualManager.newLoopingScrollBar(hud_loopingScrollBar_vertical, 50, 72, 4, 43, CharLoopingScrollBar.Direction.vertical);
        hud_turn_lr_scrollBar       = visualManager.newLoopingScrollBar(hud_loopingScrollBar_horizon, 32, 72, 2, 71, CharLoopingScrollBar.Direction.horizon);
        hud_turn_lr_scrollBar.location[1] = resolutionY - 1;
        hud_roll_up_dn_scrollBar.location[0] = (int) (resolutionX * 0.2);
        hud_crosshair = visualManager.newImage(hud_crosshairImg, 32765, 65, 11, ((resolutionX >> 1) - (65 >> 1)), ((resolutionY >> 1) - (11 >> 1)));
        //"107"  "57" 160 84
        hud_Radar = new CharImage(
            hud_radarBG, 
            visualManager.fraps_buffer,
            21,
            21,
            (visualManager.resolution[0] - 22), 
            1,
            124, 
            visualManager.resolution,
            true
        );
        visualManager.newImage(hud_Radar);
	}
	
	public void initClouds() {
        clouds = new ArrayList<ThreeDs>();
        objectsManager.newStaticObjectList(clouds);

        cloudRefreshRateController = new HzController(refreshHz >> 1); //32Hz
        cloudMan = new CloudsManager(clouds, cloudRefreshRateController, myJet.location, visibility);
        cloudManThread = new Thread(cloudMan);
	}
	
	public ScreenResizer
	(
		String myJetModel_file,
		String hud_horizonIndicator,
		String hud_loopingScrollBar_vertical,
		String hud_loopingScrollBar_horizon,
		String hud_crosshairImg,
        String hud_friend,
        String hud_enemy,
        String hud_locking,
        String hud_locked,
        String hud_missileWarning,
		String hud_radarBG,
		String hud_radarPrinter,
		String hud_scoreListBG,
		String cfg_file,
		String rec_file,
		String bgm_file,
		int resolutionX, 
		int resolutionY, 
		int refresh_rate,
		int fontSize
	)
	{
		super(resolutionX, resolutionY, refresh_rate);
		
		initUI(fontSize);
		initMe(myJetModel_file, hud_friend, hud_enemy, hud_locking, hud_locked, hud_missileWarning);
        initHUDs(resolutionX, resolutionY, 
            hud_horizonIndicator,
            hud_crosshairImg,
            hud_loopingScrollBar_vertical,
            hud_loopingScrollBar_horizon,
            hud_radarBG,
            hud_radarPrinter
        );
        initClouds();
	}
	
	private void reLocateHUD() {
	    final int resolution_X = visualManager.resolution[0], resolution_Y = visualManager.resolution[1];
	    final int progressBarLocationBaseX  = (int) (resolution_X * 0.75);
        final int progressBarLocationBaseX2 = (int) (resolution_X - 18);
        final int progressBarLocationBaseY  = (int) (resolution_Y * 0.65);
        final int progressBarLocationBaseY2 = (int) (resolution_Y * 0.3);
        
        lblTop.setText(Common.loopChar('X', resolution_X));
        lblLeft.setText(Common.loopStr("X\n", resolution_Y));
        lblRight.setText(Common.loopStr("X\n", resolution_Y));
        lblBottom.setText(Common.loopChar('X', resolution_X));
        lblRight.setLocation(resolution_X - 1, 0);
        lblBottom.setLocation(0, resolution_Y - 1);
        
        lbl1.setLocation(progressBarLocationBaseX2, (int)(progressBarLocationBaseY2));
        lbl2.setLocation(progressBarLocationBaseX2, (int)(progressBarLocationBaseY2 + 1));
        lbl3.setLocation(progressBarLocationBaseX2, (int)(progressBarLocationBaseY2 + 2));
        lbl4.setLocation(progressBarLocationBaseX2, (int)(progressBarLocationBaseY2 + 3));
        
        lbl5.setLocation(progressBarLocationBaseX, progressBarLocationBaseY);
        lbl6.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 2);
        lbl7.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 4);
        lbl8.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 6);
        lbl9.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 8);

        EndScreen.setLocation(((resolution_X>>1) - 9), (int)(resolution_Y * 0.3));
        lblRespawnTimeLeft.setLocation(((resolution_X>>1) - 9), (int)(resolution_Y * 0.3));
        lblGameTimeLeft.setLocation(((resolution_X>>1) - 11), 3);
        
        hud_HP_progressBar.setLocation(progressBarLocationBaseX + 4, progressBarLocationBaseY);
        hud_pushTime_progressBar.setLocation(progressBarLocationBaseX + 4, progressBarLocationBaseY + 2);
        hud_cannon_progressBar.setLocation(progressBarLocationBaseX + 4, progressBarLocationBaseY + 4);
        hud_cannonReloading_progressBar.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 5);
        
        hud_missile_progressBar.setLocation(progressBarLocationBaseX + 4, progressBarLocationBaseY + 6);
        hud_missileReloading_progressBar.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 7);
        hud_decoy_progressBar.setLocation(progressBarLocationBaseX + 4, progressBarLocationBaseY + 8);
        hud_decoyReloading_progressBar.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 9);
        
        hud_turn_lr_scrollBar.location[1] = resolution_Y - 1;
        hud_turn_lr_scrollBar.location[0] = resolution_X >> 1;
        hud_roll_up_dn_scrollBar.location[1] = (int) (resolution_Y >> 1);
        hud_roll_up_dn_scrollBar.location[0] = (int) (resolution_X * 0.2);
        
        hud_crosshair.setLocation(((resolution_X >> 1) - (65 >> 1)), ((resolution_Y >> 1) - (11 >> 1)));
        
        hud_Radar.setLocation((visualManager.resolution[0] - 22), 1);
	}
	
	private void updateHUD() {
		lbl4.setText("hight: " + String.format("%.2f", -myJet.location[0]));
		
		hud_HP_progressBar.value = 1;
		hud_pushTime_progressBar.value = 1;
		hud_cannon_progressBar.value = 1;
		
		lblRespawnTimeLeft.visible = false;
		hud_roll_up_dn_angle.visible = hud_roll_up_dn_scrollBar.visible = hud_turn_lr_scrollBar.visible = 
				hud_Radar.visible = lbl1.visible = lbl2.visible = lbl3.visible = lbl4.visible = true;
		//------------[Dynamic HUDs]------------
		
		hud_roll_up_dn_angle.angle = Math.abs(myJet.roll_angle[1]) > 90.0? myJet.roll_angle[2] + 180 : myJet.roll_angle[2];
		
		hud_roll_up_dn_angle.location[0] = (int) (GraphicUtils.sin(Math.toRadians(myJet.roll_angle[2])) * GraphicUtils.sin(Math.toRadians(Math.abs(myJet.roll_angle[1]) > 90.0? -myJet.roll_angle[1] : myJet.roll_angle[1])) * - resolution_min + (visualManager.getResolution_X()>>1));
		hud_roll_up_dn_angle.location[1] = (int) (GraphicUtils.cos(Math.toRadians(myJet.roll_angle[2])) * GraphicUtils.sin(Math.toRadians(Math.abs(myJet.roll_angle[1]) > 90.0?  myJet.roll_angle[1] :-myJet.roll_angle[1])) * - resolution_min + (visualManager.getResolution_Y()>>1));
		
		hud_roll_up_dn_scrollBar.value = (int) (myJet.roll_angle[1] / 360 * 72);
		hud_turn_lr_scrollBar.value    = (int) (-myJet.roll_angle[0] / 360 * 72);
	
		hud_cannonReloading_progressBar.visible = false;
		
		hud_missile_progressBar.value = 1;
		hud_missileReloading_progressBar.visible = false;
		
		hud_decoy_progressBar.value = 1;
		hud_decoyReloading_progressBar.visible = false;
	}

	private void keyResponse() {
		while(!eventManager.EventFrapsQueue_keyboard.isEmpty())
		{
			keyPressed = eventManager.popAKeyOpreation();
			switch(keyPressed)
			{
				case 93://]
					scrSize += 1;
					eventManager.setScrZoom(scrSize);
				break;
				case 91://[
					if(scrSize > 1)scrSize -= 1;
					eventManager.setScrZoom(scrSize);
				break;

				case KeyEvent.VK_J:
					mainCamera.resizeScreen(visualManager.resolution[0] - 1, visualManager.resolution[1]);
					reLocateHUD();
				break;
				case KeyEvent.VK_L:
					mainCamera.resizeScreen(visualManager.resolution[0] + 1, visualManager.resolution[1]);
					reLocateHUD();
				break;
                case KeyEvent.VK_K:
                    mainCamera.resizeScreen(visualManager.resolution[0], visualManager.resolution[1] + 1);
                    reLocateHUD();
                break;
                case KeyEvent.VK_I:
                    mainCamera.resizeScreen(visualManager.resolution[0], visualManager.resolution[1] - 1);
                    reLocateHUD();
                break;
			}
		}
	}
	
	public void run()
	{
		//游戏主循环
		
	    playersCameraManage();
	    myJet.visible = true;
	    cloudManThread.start();
	    while(true) {

			buffStatic();
			
			updateHUD();
			
			keyResponse();
			//XXXXXXXXXXXXX[print new]XXXXXXXXXXXXXX
			printNew();
			//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		}
	}
	
	public static void main(String [] args) {
	    ScreenResizer resizer = new ScreenResizer(
            args[0] ,args[1] , args[2], args[3], args[4], args[5] ,
            args[6] , args[7], args[8], args[9], args[10], args[11], 
            args[12], args[13], args[14], args[15], 
            Integer.parseInt(args[16]), 
            Integer.parseInt(args[17]), 
            Integer.parseInt(args[18]),
            Integer.parseInt(args[19])
	    );
	    
	    resizer.getIntoGameWorld();
	    
	    resizer.run();
	}
}
