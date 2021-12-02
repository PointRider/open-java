package dogfight_Z;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import dogfight_Z.Ammo.Missile;

import graphic_Z.Common.SinglePoint;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharImage;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharLoopingScrollBar;
import graphic_Z.HUDs.CharProgressBar;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Managers.EventManager;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.HzController;

public class Game extends CharTimeSpace implements Runnable
{
	public float fov = 2.2F;
	public float visibility = 12480;
	
	public PlayersJetCamera mainCamera;
	public Aircraft myJet;
	
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
	
	public ScoreList            scoreShow;
	public CharImage            hud_crosshair;
	public Radar                hud_Radar;
	
	public int					             maxAmmoCount;
	public ArrayList<ThreeDs>	             clouds;
	public PriorityQueue<Dynamic>            firedAmmo;
	public PriorityQueue<Dynamic>            effects;
	public LinkedList<ListIterator<ThreeDs>> deleteQue;
	public LinkedList<ThreeDs>	             waitToAddQue;
	public LinkedList<String>	             killTipList;
	public int					             killTipListUpdateTimeLeft;
	public int					             killTipListUpdateTime;
	public int					             maxKillTipCount;

	public int respawnTime;
	public int colorChangedTime;
	
	public StringBuilder tmp;
	//public ArrayList<HashSet<Aircraft>> camps;
	private CloudsManager cloudMan;
	private Thread        cloudManThread;
	public HzController   cloudRefreshRateController;

	public boolean keyState_W;
	public boolean keyState_A;
	public boolean keyState_S;
	public boolean keyState_D;
	public boolean keyState_X;
	public boolean keyState_V;
	public boolean keyState_TAB;
	public boolean keyState_SPACE;
	public boolean keyState_SHIFT;
	
	public int   playersCamp;
	public long  gameTimeUsed;
	
	private int resolution_min;
	private int keyPressed;
	private int scrSize = 8;
    private int fontIdx = 1;
	private boolean flgWheelUp, flgWheelDn;
	private Aircraft lockedEnemy = null;
	//--------------------------------
	public LinkedList<Aircraft> scoreList;
	//--------------------------------
	
	private Color foreRGB;
	private int backRGB;
	
	private String cfgFile;
	private String recFile;
	
	private SoundTrack  soundTrack;
	public  Thread		bgmThread;
	
	public long gameStopTime;
	
	public void initRank()
	{
		scoreList = new LinkedList<Aircraft>();
	}
	
	public Aircraft newNPC
	(
		String myJetModel_file,
		String id,
		float Mess,
		float searching_visibility,
		float max_motionRate,
		int    camp
	)
	{
		NPC npc = new NPC
		(
			this, 
			myJetModel_file, 
			id, 
			Mess, 
			searching_visibility, 
			max_motionRate, 
			visualManager.resolution[0], 
			visualManager.resolution[1], 
			camp, 
			firedAmmo, 
			effects, 
			waitToAddQue, 
			deleteQue, 
			objectsManager.objects
		);
		npc.visible = true;
		
		scoreList.add(npc);
		objectsManager.newObject(npc);
		
		return npc;
	}
	
	private void initClasses() {
        try{
            Class.forName("dogfight_Z.Ammo.CannonAmmo");
            Class.forName("dogfight_Z.Ammo.Decoy");
            Class.forName("dogfight_Z.Ammo.Missile");
            Class.forName("dogfight_Z.Effects.EngineFlame");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	private void initUI(int fontSize) {
        eventManager.setTitle("dogfightZ");
        scrSize = fontSize;
        fontIdx = 1;
        eventManager.setScrZoom(fontSize);
        killTipListUpdateTimeLeft = killTipListUpdateTime = 450;
        maxKillTipCount       = 7;
        colorChangedTime      = 0;
	}
	
	private void initDatastructure(String hud_scoreListBG, int resolutionX, int resolutionY) {
	    tmp = new StringBuilder();
        firedAmmo = new PriorityQueue<Dynamic>();
        objectsManager.newSelfDisposableObjList(firedAmmo);
        effects = new PriorityQueue<Dynamic>();
        objectsManager.newSelfDisposableObjList(effects);
        
        killTipList = new LinkedList<String>();
        deleteQue = new LinkedList<ListIterator<ThreeDs>>();
        waitToAddQue = new LinkedList<ThreeDs>();
        
        scoreShow = new ScoreList(
            hud_scoreListBG,
            visualManager.fraps_buffer,
            32767,
            visualManager.resolution,
            44, 27,
            resolutionX >> 1,
            resolutionY >> 1,
            false, scoreList, playersCamp
        );
        
        foreRGB = new Color(255, 255, 255);
        backRGB = 0;
	}
	/*
	 "resources/MyJetHUD_Friend.hud" 
	 "resources/MyJetHUD_Enemy.hud" 
	 "resources/MyJetHUD_Locking.hud" 
	 "resources/MyJetHUD_Locked.hud" 
	 "resources/MissileWarning.hud" 
	 */
	private void initMainCamera(
	        String hud_friend,
	        String hud_enemy,
	        String hud_locking,
	        String hud_locked,
	        String hud_missileWarning
	    ) {
	    mainCamera = new PlayersJetCamera (
            fov, visibility, 
            visibility * 10, 0, 100, 
            visualManager.resolution, 
            visualManager.fraps_buffer,
            this, myJet, 
            new CharDynamicHUD (
                hud_friend,
                visualManager.fraps_buffer,
                0, visualManager.resolution,
                6, 5
            ), 
            new CharDynamicHUD (
                hud_enemy,
                visualManager.fraps_buffer,
                0, visualManager.resolution,
                5, 6
            ),
            new CharDynamicHUD (
                hud_locking,
                visualManager.fraps_buffer,
                0, visualManager.resolution,
                5, 6
            ),
            new CharDynamicHUD (
                hud_locked,
                visualManager.fraps_buffer,
                0, visualManager.resolution,
                5, 6
            ),
            new CharDynamicHUD (
                hud_missileWarning,
                visualManager.fraps_buffer,
                0, visualManager.resolution,
                7, 6
            ),
            visualManager.staticObjLists
        );
        visualManager.newCamera(mainCamera);
	}
	
	private void initMe(
	        String myJetModel_file, 
            String hud_friend,
            String hud_enemy,
            String hud_locking,
            String hud_locked,
            String hud_missileWarning
        ) {

        myJet = new Aircraft(this, myJetModel_file, 10000, playersCamp, firedAmmo, effects, deleteQue, waitToAddQue, objectsManager.objects, null, "Me", true);
        //myJet.setLocation(0, 0, 60);
        scoreList.add(myJet);
        objectsManager.newMessObject(myJet);

        initMainCamera(hud_friend, hud_enemy, hud_locking, hud_locked, hud_missileWarning);

        myJet.mainCamera = mainCamera;
        mainCamera.connectLocationAndAngle(myJet.cameraLocation, myJet.cameraRollAngle);
        myJet.randomRespawn();
        myJet.visible = true;
	}

    /**
     * myJet.setID(id)
     * gameTime -> gameTimeUsed, gameStopTime
     * ? x NPC(id, diff, camp)
     */
	private void initPlayers(String myJetModel_file, String cfg_file, String rec_file) {
        long gameTime;
        respawnTime  = 10;   //seconds
        playersCamp  = 0;
        maxAmmoCount = 400;
        cfgFile = cfg_file;
        recFile = rec_file;
        try(FileReader reader = new FileReader(new File(cfgFile))) {
            String tmp;
            int c;
            tmp = "";
            while((c = reader.read()) != '\n')
                tmp = tmp + (char)c;

            myJet.setID(tmp);
            
            tmp = "";
            while((c = reader.read()) != '\n' && c != -1)
                tmp = tmp + (char)c;
            
            gameTime = Long.parseLong(tmp);
            if(gameTime > 0)
                gameStopTime = gameTime + System.currentTimeMillis()/1000;
            else gameStopTime = -1;
            
            gameTimeUsed = gameTime;
            
            String id, diff, cmp;
            if(c != -1) while(true)
            {
                tmp = "";
                while((c = reader.read()) != -1 && c != '\n')
                    tmp = tmp + (char)c;
                
                id = new String(tmp);
                
                tmp = "";
                while((c = reader.read()) != '\n')
                    tmp = tmp + (char)c;
                diff = new String(tmp);
                
                tmp = "";
                while((c = reader.read()) != '\n'  && c != -1)
                    tmp = tmp + (char)c;
                
                cmp = new String(tmp);
                newNPC(myJetModel_file, id, 10000, visibility * 10, Float.parseFloat(diff), Integer.parseInt(cmp)).randomRespawn();
                
                if(c == -1)
                    break;
            }
        }   catch(IOException exc) {}
	}
	
	private void initHUDs(
	        int resolutionX, int resolutionY, 
	        String hud_horizonIndicator,
	        String hud_crosshairImg,
	        String hud_loopingScrollBar_vertical,
	        String hud_loopingScrollBar_horizon,
	        String hud_radarBG,
	        String hud_radarPrinter
	    ) {
	    resolution_min = GraphicUtils.min(visualManager.getResolution_X(), visualManager.getResolution_Y()) >> 1;
	    scoreShow.visible = false;
        visualManager.newDynamicHUD(scoreShow);
        EndScreen = visualManager.newLabel("      TIME UP\nPress ESC Key To Exit.", ((resolutionX>>1) - 9), (int)(resolutionY * 0.3), 999);
        EndScreen.visible = false;
        
        final int progressBarLocationBaseX = (int) (resolutionX * 0.75);
        final int progressBarLocationBaseY = (int) (resolutionY * 0.65);

        mainCamera.hudWarning_missile.location[0] = (resolutionX - 16);
        mainCamera.hudWarning_missile.location[1] = (resolutionY >> 1);
        
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
        
        hud_HP_progressBar          = visualManager.newProgressBar((progressBarLocationBaseX + 4), progressBarLocationBaseY, 205, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_pushTime_progressBar    = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 2), 209, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        
        hud_cannon_progressBar = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 4), 210, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_cannonReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 5), 212, 25, '-', CharProgressBar.Direction.horizon, 1.0F);
        hud_missile_progressBar = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 6), 214, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_missileReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 7), 216, 25, '-', CharProgressBar.Direction.horizon, 1.0F);
        hud_decoy_progressBar = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 8), 218, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_decoyReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 9), 220, 25, '-', CharProgressBar.Direction.horizon, 1.0F);
        
        hud_roll_up_dn_angle        = visualManager.newDynamicHUD(hud_horizonIndicator, 21, 51, 33);
        hud_roll_up_dn_scrollBar    = visualManager.newLoopingScrollBar(hud_loopingScrollBar_vertical, 50, 72, 4, 43, CharLoopingScrollBar.Direction.vertical);
        hud_turn_lr_scrollBar       = visualManager.newLoopingScrollBar(hud_loopingScrollBar_horizon, 32, 72, 2, 71, CharLoopingScrollBar.Direction.horizon);
        hud_turn_lr_scrollBar.location[1] = resolutionY - 1;
        hud_roll_up_dn_scrollBar.location[0] = (int) (resolutionX * 0.2);
        hud_crosshair = visualManager.newImage(hud_crosshairImg, 32765, 65, 11, ((resolutionX >> 1) - (65 >> 1)), ((resolutionY >> 1) - (11 >> 1)));
        //"107"  "57" 160 84
        hud_Radar = new Radar
        (
            hud_radarBG, 
            hud_radarPrinter,
            visualManager.fraps_buffer, 124, visualManager.resolution, 
            21, (visualManager.resolution[0] - 12), 
            11, myJet, objectsManager.objects, visibility * 10
        );
        visualManager.newDynamicHUD(hud_Radar);
	}
	
	private void initClouds() {
        clouds = new ArrayList<ThreeDs>();
        objectsManager.newStaticObjectList(clouds);

        cloudRefreshRateController = new HzController(refreshHz >> 1); //32Hz
        cloudMan = new CloudsManager(clouds, cloudRefreshRateController, mainCamera, visibility);
        cloudManThread = new Thread(cloudMan);
	}
	
	private void initKeyboardAndMouse() {
        Object mouse = new MouseWheelControl(eventManager.EventFrapsQueue_keyboard);
        eventManager.mainScr.addMouseWheelListener((MouseWheelListener) mouse);
        eventManager.mainScr.addMouseListener((MouseListener) mouse);
        eventManager.addKeyListener(new ContinueListener(this, Thread.currentThread()));

        keyState_W =
        keyState_A =
        keyState_S =
        keyState_D =
        keyState_X =
        keyState_V =
        keyState_TAB =
        keyState_SPACE =
        keyState_SHIFT = false;
	}
	
	private void initSoundTrack(String bgm_file) {
        soundTrack    = new SoundTrack(bgm_file);
        bgmThread     = new Thread(soundTrack);
	}
	
	public Game
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
		
		initClasses();
		initUI(fontSize);
		initRank();
		initDatastructure(hud_scoreListBG, resolutionX, resolutionY);
		initMe(myJetModel_file, hud_friend, hud_enemy, hud_locking, hud_locked, hud_missileWarning);
		initPlayers(myJetModel_file, cfg_file, rec_file);
        initHUDs(resolutionX, resolutionY, 
            hud_horizonIndicator,
            hud_crosshairImg,
            hud_loopingScrollBar_vertical,
            hud_loopingScrollBar_horizon,
            hud_radarBG,
            hud_radarPrinter
        );
        initClouds();
        initKeyboardAndMouse();
        initSoundTrack(bgm_file);
	}
	
	public void addKillTip(Aircraft killer, Aircraft deader, String WeaponName)
	{
		killTipList.addLast(killer.getID() + " >>" + WeaponName + ">> " + deader.getID());
		if(killTipList.size() > maxKillTipCount)
			killTipList.removeFirst();
		
		tmp.delete(0, tmp.length());
		for(String aLine : killTipList)
		{
			tmp.append(aLine);
			tmp.append('\n');
		}
		lblKillTipList.setText(tmp.toString());
		killTipListUpdateTimeLeft = killTipListUpdateTime;
	}
	
	public void colorFlash
	(
		int R_Fore, int G_Fore, int B_Fore, 
		int R_Back, int G_Back, int B_Back, 
		int time, Aircraft source
	)
	{
		if(source.getID().equals(myJet.getID()))
		{
			eventManager.mainScr.setForeground(new Color(R_Fore, G_Fore, B_Fore));
			eventManager.mainScr.setBackground(new Color(R_Back, G_Back, B_Back));
			colorChangedTime = time;
		}
	}
	
	private void updateKillList() {
		if(--killTipListUpdateTimeLeft <= 0)
		{
			if(!killTipList.isEmpty())
				killTipList.removeFirst();
			
			tmp.delete(0, tmp.length());
			for(String aLine : killTipList)
			{
				tmp.append(aLine);
				tmp.append('\n');
			}
			lblKillTipList.setText(tmp.toString());
			killTipListUpdateTimeLeft = killTipListUpdateTime;
		}
	}
	
	private void reLocateHUD() {
	    resolution_min = GraphicUtils.min(visualManager.getResolution_X(), visualManager.getResolution_Y()) >> 1;
	    final int resolution_X = visualManager.resolution[0], resolution_Y = visualManager.resolution[1];
	    final int progressBarLocationBaseX  = (int) (resolution_X * 0.75);
        final int progressBarLocationBaseX2 = (int) (resolution_X - 18);
        final int progressBarLocationBaseY  = (int) (resolution_Y * 0.65);
        final int progressBarLocationBaseY2 = (int) (resolution_Y * 0.3);

        mainCamera.hudWarning_missile.location[0] = (resolution_X - 16);
        mainCamera.hudWarning_missile.location[1] = (resolution_Y >> 1);
        
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
        hud_roll_up_dn_scrollBar.location[0] = (int) (resolution_X * 0.2F);
        
        hud_crosshair.setLocation(((resolution_X >> 1) - (65 >> 1)), ((resolution_Y >> 1) - (11 >> 1)));
        
        hud_Radar.setLocation((visualManager.resolution[0] - 12), 11);
        
        scoreShow.setLocation(resolution_X >> 1, resolution_Y >> 1);
	}
	
	private void updateHUD() {
		lbl1.setText("speed: " + String.format("%.2f", myJet.speed * 14));
		lbl2.setText("shift: " + String.format("%.2f", myJet.control_stick_acc));
		lbl3.setText("rpm  : " + String.format("%.2f", Aircraft.getCurrentRPM(myJet.max_rpm, myJet.control_stick_acc)));
		lbl4.setText("hight: " + String.format("%.2f", -myJet.location[0]));
		//lbl5.setText("roll_angle: " + String.format("%.2f", myJet.roll_angle[2]));
		//lbl6.setText("cameraRoll: " + String.format("%.2f", myJet.cameraRollAngle[2]));
		
		hud_HP_progressBar.value = myJet.HP / 100.0;
		hud_pushTime_progressBar.value = myJet.pushTimeLeft / myJet.maxPushTime;
		hud_cannon_progressBar.value = (float)myJet.cannonMagazineLeft / (float)myJet.cannonMagazine;
		
		if(myJet.isAlive) {
			lblRespawnTimeLeft.visible = false;
			hud_roll_up_dn_angle.visible = hud_roll_up_dn_scrollBar.visible = hud_turn_lr_scrollBar.visible = 
					hud_Radar.visible = lbl1.visible = lbl2.visible = lbl3.visible = lbl4.visible = true;
			//------------[Dynamic HUDs]------------
			
			hud_roll_up_dn_angle.angle = GraphicUtils.abs(myJet.roll_angle[1]) > 90.0? myJet.roll_angle[2] + 180 : myJet.roll_angle[2];
			
			hud_roll_up_dn_angle.location[0] = (int) (GraphicUtils.sin(GraphicUtils.toRadians(myJet.roll_angle[2])) * GraphicUtils.sin((float) GraphicUtils.toRadians(GraphicUtils.abs(myJet.roll_angle[1]) > 90.0? -myJet.roll_angle[1] : myJet.roll_angle[1])) * - resolution_min + (visualManager.getResolution_X()>>1));
			hud_roll_up_dn_angle.location[1] = (int) (GraphicUtils.cos(GraphicUtils.toRadians(myJet.roll_angle[2])) * GraphicUtils.sin((float) GraphicUtils.toRadians(GraphicUtils.abs(myJet.roll_angle[1]) > 90.0?  myJet.roll_angle[1] :-myJet.roll_angle[1])) * - resolution_min + (visualManager.getResolution_Y()>>1));
			
			hud_roll_up_dn_scrollBar.value = (int) (myJet.roll_angle[1] / 5/*360 / 72*/);
			/*if(hud_roll_up_dn_scrollBar.value > 0) {
	            backRGB = hud_roll_up_dn_scrollBar.value * 255 / 90;
			} else backRGB = 0;*/
			hud_turn_lr_scrollBar.value    = (int) (-myJet.roll_angle[0] / 5);
		}
		else
		{
			hud_roll_up_dn_angle.visible = hud_roll_up_dn_scrollBar.visible = hud_turn_lr_scrollBar.visible = 
					hud_Radar.visible = lbl1.visible = lbl2.visible = lbl3.visible = lbl4.visible = false;
			lbl1.visible = lbl2.visible = lbl3.visible = lbl4.visible = false;
			lblRespawnTimeLeft.visible = true;
			lblRespawnTimeLeft.setText("You will respawn in\n     " + (myJet.respwanAtTime - System.currentTimeMillis()/1000) + " seconds.");
		}
		
		if(myJet.cannonMagazineLeft == 0)
		{
			hud_cannonReloading_progressBar.visible = true;
			hud_cannonReloading_progressBar.value = 1.0 - (float)myJet.cannonReloadingTimeLeft / (float)myJet.cannonReloadingTime;	
		}
		else hud_cannonReloading_progressBar.visible = false;
		
		hud_missile_progressBar.value = (float)myJet.missileMagazineLeft / (float)myJet.missileMagazine;
		if(myJet.missileMagazineLeft == 0)
		{
			hud_missileReloading_progressBar.visible = true;
			hud_missileReloading_progressBar.value = 1.0 - (float)myJet.missileReloadingTimeLeft / (float)myJet.missileReloadingTime;	
		}
		else hud_missileReloading_progressBar.visible = false;
		
		hud_decoy_progressBar.value = (float)myJet.decoyMagazineLeft / (float)myJet.decoyMagazine;
		if(myJet.decoyMagazineLeft == 0)
		{
			hud_decoyReloading_progressBar.visible = true;
			hud_decoyReloading_progressBar.value = 1.0 - (float)myJet.decoyReloadingTimeLeft / (float)myJet.decoyReloadingTime;	
		}
		else hud_decoyReloading_progressBar.visible = false;
	}

	@SuppressWarnings("deprecation")
	private void keyResponse() {
		SinglePoint xy;
		xy = eventManager.popAMouseOpreation();
		
		if(myJet.isCannonFiring) {
			myJet.control_roll_lr((float)(-xy.x)/224.0F);
			myJet.control_roll_up_dn((float)(-xy.y)/224.0F);
		} else {
			myJet.control_roll_lr((float)(-xy.x)/96.0F);
			myJet.control_roll_up_dn((float)(-xy.y)/96.0F);
		}
		
		flgWheelUp = flgWheelDn = false;
		while(!eventManager.EventFrapsQueue_keyboard.isEmpty())
		{
			keyPressed = eventManager.popAKeyOpreation();
			switch(keyPressed)
			{
				//-----------[Mouse]---------
			
				case 4096:
					if(!flgWheelUp)
					{
						myJet.control_acc(0.2F);
						flgWheelUp = true;
					}
				break;
				
				case 8192:
					if(!flgWheelDn)
					{
						myJet.control_dec();
						flgWheelDn = true;
					}
				break;
				
				case 16385:
					keyState_SPACE = true;
				break;
				
				case -16385:
					keyState_SPACE = false;
				break;
				
				case 16386:
				{
					Missile m = myJet.missileOpenFire(true, lockedEnemy);
					if(m != null)
						mainCamera.connectLocationAndAngle(m.location, m.cameraRollAngle);
				}
				break;
				
				case 16387:
					myJet.missileOpenFire(false, lockedEnemy);
				break;
				
				//-----------------------------
				
				case KeyEvent.VK_C:
					++myJet.cameraLocationFlag;
				break;
				
				case KeyEvent.VK_W://W
					keyState_W = true;
				break;
				case -KeyEvent.VK_W://-W
					keyState_W = false;
				break;
					
				case KeyEvent.VK_S://S
					keyState_S = true;
				break;
				case -KeyEvent.VK_S://-S
					keyState_S = false;
				break;
				
				case KeyEvent.VK_A://A
					keyState_A = true;
				break;
				case -KeyEvent.VK_A://-A
					keyState_A = false;
				break;
				
				case KeyEvent.VK_D://D
					keyState_D = true;
				break;
				case -KeyEvent.VK_D://-D
					keyState_D = false;
				break;
				
				case KeyEvent.VK_SPACE://Shift
					keyState_SHIFT = true;
				break;
				case -KeyEvent.VK_SPACE://-Shift
					keyState_SHIFT = false;
				break;
				
				case KeyEvent.VK_F:
					keyState_TAB = true;
				break;
				case -KeyEvent.VK_F:
					keyState_TAB = false;
				break;
				
				case KeyEvent.VK_X:
					keyState_X = true;
				break;
				case -KeyEvent.VK_X:
					keyState_X = false;
				break;
				
				//-----------------------------
				
				case KeyEvent.VK_Q:
					mainCamera.connectLocationAndAngle(myJet.cameraLocation, myJet.cameraRollAngle);
				break;
				
				case KeyEvent.VK_V:
					keyState_V = true;
				break;
				case -KeyEvent.VK_V:
					keyState_V = false;
				break;
				//fontIdx
				case 93://]
					scrSize += 1;
					eventManager.setScrZoom(scrSize);
				break;
				case 91://[
					if(scrSize > 1)scrSize -= 1;
					eventManager.setScrZoom(scrSize);
				break;
				
				case KeyEvent.VK_N:
				    if(fontIdx-- == 0) fontIdx = EventManager.getSupportedFontsCount() - 1;
                    eventManager.switchFont(fontIdx);
                break;
                case KeyEvent.VK_M:
                    if(++fontIdx == EventManager.getSupportedFontsCount()) fontIdx = 0;
                    eventManager.switchFont(fontIdx);
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
				case KeyEvent.VK_E:
					bgmThread.stop();
					soundTrack.switchPrevious();
					bgmThread = new Thread(soundTrack);
					bgmThread.start();
				break;
					
				case KeyEvent.VK_R:
					bgmThread.stop();
					soundTrack.switchNext();
					bgmThread = new Thread(soundTrack);
					bgmThread.start();
				break;
			}
		}
		
		if(keyState_W) myJet.control_acc(0.05F);
		
		
		if(keyState_A) {
			if(myJet.isCannonFiring) myJet.control_turn_lr(-0.1875F);
			else myJet.control_turn_lr(-0.5F);
		}
		
		if(keyState_D) {
			if(myJet.isCannonFiring) myJet.control_turn_lr(0.1875F);
			else myJet.control_turn_lr(0.5F);
		}
		
		if(keyState_X) myJet.makeDecoy();
		
		if(keyState_S) myJet.control_brk();
		else myJet.control_stop_breaking();
		
		if(keyState_SHIFT) myJet.control_push();
		else myJet.control_stop_pushing();
		
		if(keyState_SPACE) myJet.cannonOpenFire();
		else myJet.cannonStopFiring();
		
		scoreShow.visible = keyState_TAB;
		mainCamera.setReversed(keyState_V);
	}
	
	public void run()
	{
		//float tmp_float_arr_2[] = new float[2];
	    
		long leftTime = 0;
		long hor, min, sec;
		if(gameStopTime == -1)
			lblGameTimeLeft.visible = false;
		
        cloudManThread.start();
        bgmThread.start();
		
		//游戏主循环
		while(gameStopTime == -1  ||  (leftTime = gameStopTime - System.currentTimeMillis()/1000) > 0)
		{
			buffStatic();
			if(gameStopTime != -1)
			{
				hor = leftTime / 3600;
				min = (leftTime - hor*3600) / 60;
				sec = (leftTime - hor*3600) % 60;
				lblGameTimeLeft.setText("Round Time Left: " + Long.toString(hor) + ':' + Long.toString(min) + ':' + Long.toString(sec));
			}
			
			updateKillList();
			updateHUD();
			
			while(!deleteQue.isEmpty()) {
			    deleteQue.poll().remove();
			}
			while(!waitToAddQue.isEmpty()) {
				ThreeDs obj = waitToAddQue.poll();
				obj.setIterator(objectsManager.newObject(obj));
			}
			
			//----------------[ammos]---------------
			while(!firedAmmo.isEmpty() && firedAmmo.peek().deleted())
				firedAmmo.poll();
				
			//--------------------------------------
			//---------------[effects]--------------
			while(!effects.isEmpty() && effects.peek().deleted())
				effects.poll();
			//--------------------------------------
				
			keyResponse();
			
			if(--colorChangedTime <= 0)
			{
				eventManager.mainScr.setForeground(foreRGB);
				eventManager.mainScr.setBackground(new Color(backRGB, backRGB, backRGB));
			}
			//XXXXXXXXXXXXX[print new]XXXXXXXXXXXXXX
			lockedEnemy = (Aircraft) visualManager.mainCameraFeedBack;
			printNew();
			//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		}
		myJet.visible = false;
		buffStatic();
		EndScreen.visible = true;
		scoreShow.visible = true;
		printNew();
		
		try(FileWriter fw = new FileWriter(new File(recFile), true))
		{
			fw.write(myJet.getID());
			fw.write('\n');
			fw.write(Long.toString(gameTimeUsed));
			fw.write('\n');
			fw.write(Integer.toString(myJet.killed));
			fw.write('\n');
			fw.write(Integer.toString(myJet.dead));
			fw.write('\n');
		}	catch(IOException exc){}
	}
	
	protected void finalize() throws Throwable{
		cloudManThread.interrupt();
		bgmThread.interrupt();
	}
	
	public static void main(String[] args)
    {
        Game game = new Game
        (
            args[0] ,args[1] , args[2], args[3], args[4], args[5] ,
            args[6] , args[7], args[8], args[9], args[10], args[11], 
            args[12], args[13], args[14], args[15], 
            Integer.parseInt(args[16]), 
            Integer.parseInt(args[17]), 
            Integer.parseInt(args[18]),
            Integer.parseInt(args[19])
        );
        
        game.getIntoGameWorld();
        game.run();
    }
}
