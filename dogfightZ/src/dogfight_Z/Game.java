package dogfight_Z;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import dogfight_Z.Ammo.Decoy;
import dogfight_Z.Ammo.Missile;

import graphic_Z.Common.SinglePoint;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharImage;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharLoopingScrollBar;
import graphic_Z.HUDs.CharProgressBar;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Interfaces.ThreeDs.DrawingMethod;
import graphic_Z.Managers.EventManager;
import graphic_Z.Managers.SoundTrack;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;

public class Game extends CharTimeSpace implements Runnable {
    
    private static String s_hudShiftValue = "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "32\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "24\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "16\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "08\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "- \n" + 
                                            "00\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--\n" + 
                                            "--";
    
    private float fov = 2.2F;
	private float visibility = 12480;
	private float gBlack;
	
	private PlayersJetCamera mainCamera;
	private Aircraft myJet;
	
	private CharLabel lbl1;
	//private CharLabel lbl2;
	//private CharLabel lbl3;
	private CharLabel lbl4; 
	private CharLabel lbl5;
    //private CharLabel lbl6;
    //private CharLabel lbl7;
    //private CharLabel lbl8;
    private CharLabel lbl9;
    private CharLabel lbl10;
	
    private CharLabel lblRespawnTimeLeft;
	private CharLabel lblGameTimeLeft;
    private CharLabel lblRecoding;
	private CharLabel lblKillTipList;

	private CharLabel 			EndScreen;
	//public CharLabel          lbltest;
	//private NPC npctest;
	
	private CharDynamicHUD		hud_roll_up_dn_angle;
	
	private CharLabel hud_turn_lr_scrollBar_pointer;
    private CharLabel hud_roll_up_dn_scrollBar_pointer;
    private CharLoopingScrollBar hud_turn_lr_scrollBar;
	private CharLoopingScrollBar hud_roll_up_dn_scrollBar;
    private CharLoopingScrollBar hud_shift_scrollBar;
	
	private CharProgressBar		hud_HP_progressBar;
	private CharProgressBar		hud_pushTime_progressBar;
	private CharLabel    		hud_cannon_progressBar;
	private CharProgressBar		hud_cannonReloading_progressBar;
	private CharProgressBar		hud_missile_progressBar;
	private CharProgressBar		hud_missileReloading_progressBar;
	private CharProgressBar		hud_decoy_progressBar;
	private CharProgressBar		hud_decoyReloading_progressBar;
	
	private ScoreList            scoreShow;
	private CharImage            hud_crosshair;
	private Radar                hud_Radar;
	
	private ArrayList<ThreeDs>	              clouds;
	private PriorityQueue<Dynamic>            firedAmmo;
	private PriorityQueue<Dynamic>            effects;
	private LinkedList<ListIterator<ThreeDs>> deleteQue;
	private LinkedList<ThreeDs>	              waitToAddQue;
	private LinkedList<String>	              killTipList;
	private int					              killTipListUpdateTimeLeft;
	private int					              killTipListUpdateTime;
	private int					              maxKillTipCount;

	private int respawnTime; //ms
	private int colorChangedTime;
	private int rgbTmp;
	public StringBuilder tmp;
	//public ArrayList<HashSet<Aircraft>> camps;
	private CloudsManager cloudMan;

	private boolean keyState_W;
	private boolean keyState_A;
	private boolean keyState_S;
	private boolean keyState_D;
	private boolean keyState_Up;
	private boolean keyState_Dn;
	private boolean keyState_Lf;
	private boolean keyState_Rt;
	private boolean keyState_X;
	private boolean keyState_V;
	private boolean keyState_TAB;
	private boolean keyState_SPACE;
	private boolean keyState_SHIFT;

	private boolean recording;
	
	private int   playersCamp;
	private long  gameTimeUsed;
	
	private int resolution_min;
	private int keyPressed;
	private int scrSize = 8;
    private int fontIdx = 1;
	private boolean flgWheelUp, flgWheelDn;
	private Aircraft lockedEnemy = null;
	//--------------------------------
	private LinkedList<Aircraft> scoreList;
	//--------------------------------
	
	//private Color foreRGB;
	private int backRGB;
	
	private String cfgFile;
	private String recFile;
	
	private SoundTrack  soundTrack;
	
	public long gameStopTime;
	
	private GameManager gameManager;
	
	private boolean paused;
	
	private volatile boolean running;
	
	private final void initRank(){
		scoreList = new LinkedList<Aircraft>();
	}
	
	public final Aircraft newNPC (
		String myJetModel_file,
		String id,
		float Mess,
		float searching_visibility,
		float max_motionRate,
		int   camp
	) {
		NPC npc = new NPC (
			gameManager, 
			myJetModel_file, 
			id, 
			Mess, 
			searching_visibility, 
			max_motionRate, 
			camp, 
			firedAmmo, 
			waitToAddQue, 
			deleteQue, 
			objectsManager.objects
		);
		npc.visible = true;
		
		scoreList.add(npc);
		objectsManager.newObject(npc);
		
		return npc;
	}
	
	private final void initClasses() {
        try{
            Class.forName("dogfight_Z.Ammo.CannonAmmo");
            Class.forName("dogfight_Z.Ammo.Decoy");
            Class.forName("dogfight_Z.Ammo.Missile");
            Class.forName("dogfight_Z.Effects.EngineFlame");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	private final void initUI(int fontSize, int fontIndx) {
        eventManager.setTitle("dogfightZ");
        scrSize = fontSize;
        fontIdx = fontIndx;
        eventManager.switchFont(fontIndx);
        eventManager.setScrZoom(fontSize);
        killTipListUpdateTimeLeft = killTipListUpdateTime = 450;
        maxKillTipCount       = 7;
        colorChangedTime      = 0;
	}
	
	private final void initDatastructure(String hud_scoreListBG, int resolutionX, int resolutionY) {
	    
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
        
        //foreRGB = new Color(255, 255, 255);
        backRGB = 0;
        gameManager = new GameManager(this);
        paused = false;
	}
	/*
	 "resources/MyJetHUD_Friend.hud" 
	 "resources/MyJetHUD_Enemy.hud" 
	 "resources/MyJetHUD_Locking.hud" 
	 "resources/MyJetHUD_Locked.hud" 
	 "resources/MissileWarning.hud" 
	 */
	
	private final void initMainCamera(
	        String hud_friend,
	        String hud_enemy,
	        String hud_locking,
	        String hud_locked,
	        String hud_missileWarning,
            String hud_lockingWarning
	    ) {
	    mainCamera = new PlayersJetCamera (
            fov, visibility, 
            visibility * 10, 0, 100, 
            visualManager.resolution, 
            visualManager.fraps_buffer,
            visualManager.motionalBlur,
            this, getMyJet(), 
            new CharImage (
                hud_friend,
                visualManager.fraps_buffer,
                6, 5, 0, 0, 0, 
                visualManager.resolution,
                true
            ), 
            new CharImage (
                hud_enemy,
                visualManager.fraps_buffer,
                5, 6, 0, 0, 0, 
                visualManager.resolution,
                true
            ),
            new CharImage (
                hud_locking,
                visualManager.fraps_buffer,
                5, 6, 0, 0, 0, 
                visualManager.resolution,
                true
            ),
            new CharImage (
                hud_locked,
                visualManager.fraps_buffer,
                5, 6, 0, 0, 0, 
                visualManager.resolution,
                true
            ),
            new CharImage (
                hud_missileWarning,
                visualManager.fraps_buffer,
                7, 6, 0, 0, 0, 
                visualManager.resolution,
                true
            ),
            new CharImage (
                hud_lockingWarning,
                visualManager.fraps_buffer,
                7, 6, 0, 0, 0, 
                visualManager.resolution,
                true
            ),
            visualManager.staticObjLists
        );
        visualManager.newCamera(mainCamera);
	}
	
	private final void initMe(
	        String myJetModel_file, 
            String hud_friend,
            String hud_enemy,
            String hud_locking,
            String hud_locked,
            String hud_missileWarning,
            String hud_lockingWarning
        ) {
	    gBlack = 1;
        setMyJet(new Aircraft(gameManager, myJetModel_file, 10000, playersCamp, objectsManager.objects, null, "Me", DrawingMethod.drawLine));
        
        scoreList.add(getMyJet());
        objectsManager.newMessObject(getMyJet());

        initMainCamera(hud_friend, hud_enemy, hud_locking, hud_locked, hud_missileWarning, hud_lockingWarning);

        getMyJet().mainCamera = mainCamera;
        mainCamera.connectLocationAndAngle(getMyJet().getCameraLocation(), getMyJet().getCameraRollAngle());
        
        getMyJet().randomRespawn();
        getMyJet().visible = true;
	}

    /**
     * myJet.setID(id)
     * gameTime -> gameTimeUsed, gameStopTime
     * ? x NPC(id, diff, camp)
     */
	private final void initPlayers(String myJetModel_file, String cfg_file, String rec_file) {
        long gameTime;
        setRespawnTime(10000);   //seconds
        playersCamp  = 0;
        //maxAmmoCount = 400;
        cfgFile = cfg_file;
        recFile = rec_file;
        try(FileReader reader = new FileReader(new File(cfgFile))) {
            String tmp;
            int c;
            tmp = "";
            while((c = reader.read()) != '\n')
                tmp = tmp + (char)c;

            getMyJet().setID(tmp);
            
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
                //npctest = (NPC) newNPC(myJetModel_file, id, 10000, visibility * 10, Float.parseFloat(diff), Integer.parseInt(cmp));
                //npctest.randomRespawn();
                //mainCamera.connectLocationAndAngle(npctest.getCameraLocation(), npctest.getCameraRollAngle());
                if(c == -1) break;
            }
        }   catch(IOException exc) {}
	}
	
	private final void initHUDs(
        int resolutionX, int resolutionY, 
        String hud_horizonIndicator,
        String hud_crosshairImg,
        String hud_loopingScrollBar_vertical,
        String hud_shiftScrollBar_vertical,
        String hud_loopingScrollBar_horizon,
        String hud_radarBG,
        String hud_radarPrinter
    ) {
	    resolution_min = GraphicUtils.min(visualManager.getResolution_X(), visualManager.getResolution_Y()) >> 1;
	    scoreShow.visible = false;
        visualManager.newImage(scoreShow);          //
        EndScreen = visualManager.newLabel("      GAME OVER\n\nPress ESC Key To Exit.", ((resolutionX>>1) - 9), (int)(resolutionY * 0.3), 999);
        EndScreen.visible = false;
        
        final int progressBarLocationBaseX0 = (resolutionX >> 1) - 24;
        final int progressBarLocationBaseY  = 5;
        final int progressBarLocationBaseXA = (int) (resolutionX>>1) + 26;
        final int progressBarLocationBaseXB = (int) (resolutionX>>1) - 31;
        final int progressBarLocationBaseYB = (int) (resolutionY>>1) + 7;

        mainCamera.getHudWarning_missile().location[0] = (resolutionX - 16);
        mainCamera.getHudWarning_missile().location[1] = (resolutionY >> 1);
        mainCamera.getHudWarningLocking().location[0] = (resolutionX - 16);
        mainCamera.getHudWarningLocking().location[1] = (resolutionY >> 1);
        
        //lbltest = visualManager.newLabel("", 18, (int)(resolutionY * 0.8), 99);
                
        lbl1 = visualManager.newLabel(" ", (resolutionX>>1) + 27, (resolutionY>>1) - 7, 32899);
        /*lbl2 = visualManager.newLabel(" ", (resolutionX - 18), (int)(resolutionY * 0.3 + 1), 101);
        lbl3 = visualManager.newLabel(" ", (resolutionX - 18), (int)(resolutionY * 0.3 + 2), 102);*/
        lbl4 = visualManager.newLabel(" ", (resolutionX>>1) - 30, (resolutionY>>1) - 7, 32898);
        lbl5 = visualManager.newLabel("HP:[                                            ]", progressBarLocationBaseX0, progressBarLocationBaseY, 204);
        //lbl6 = visualManager.newLabel("AB:[                    ]", progressBarLocationBaseX1, (progressBarLocationBaseY + 2), 207);
        //lbl7 = visualManager.newLabel("CN:[                    ]", progressBarLocationBaseX, (progressBarLocationBaseY + 4), 206);
        //lbl8 = visualManager.newLabel("MS:[                    ]", progressBarLocationBaseX, (progressBarLocationBaseY + 6), 208);
        lbl9 = visualManager.newLabel("DECOY:[    ]", (resolutionX - 18), (resolutionY >> 1) + 9, 256);
        lblRespawnTimeLeft = visualManager.newLabel(" ", ((resolutionX>>1) - 9), (int)(resolutionY * 0.3), 999);
        lblGameTimeLeft = visualManager.newLabel(" ", ((resolutionX>>1) - 11), 3, 998);
        lblRecoding = visualManager.newLabel("Recording Video...", ((resolutionX>>1) - 8), 6, 9927);
        lblRecoding.visible = false;
        lblKillTipList = visualManager.newLabel(" ", 3, 3, 123);
        
        hud_HP_progressBar          = visualManager.newProgressBar((progressBarLocationBaseX0 + 4), progressBarLocationBaseY, 205, 44, '=', CharProgressBar.Direction.horizon, 1.0F);
        hud_pushTime_progressBar    = visualManager.newProgressBar((resolutionX >> 1) + (resolutionX >> 2) + 1, (resolutionY >> 1) + 22, 209, 45, '=', CharProgressBar.Direction.vertical, 1.0F);
        
        //hud_cannon_progressBar = visualManager.newProgressBar(progressBarLocationBaseXA, progressBarLocationBaseYB, 40000, 8, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_cannon_progressBar = visualManager.newLabel("0", progressBarLocationBaseXA + 2, progressBarLocationBaseYB, 40000);
        hud_cannonReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseXA, progressBarLocationBaseYB + 1, 40001, 6, '=', CharProgressBar.Direction.horizon, 1.0F);
        hud_missile_progressBar = visualManager.newProgressBar(progressBarLocationBaseXB + 1, progressBarLocationBaseYB, 40002, 4, 'A', CharProgressBar.Direction.horizon, 1.0F);
        hud_missileReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseXB, progressBarLocationBaseYB + 1, 40003, 6, '=', CharProgressBar.Direction.horizon, 1.0F);
        hud_decoy_progressBar = visualManager.newProgressBar((resolutionX - 11), (resolutionY >> 1) + 9, 218, 4, '=', CharProgressBar.Direction.horizon, 1.0F);
        hud_decoyReloading_progressBar = visualManager.newProgressBar((resolutionX - 18), (resolutionY >> 1) + 10, 220, 12, '-', CharProgressBar.Direction.horizon, 1.0F);
        
        hud_roll_up_dn_angle        = visualManager.newDynamicHUD(hud_horizonIndicator, 21, 51, 33);
        hud_roll_up_dn_scrollBar    = visualManager.newLoopingScrollBar(hud_loopingScrollBar_vertical, 50, 72, 4, 43, CharLoopingScrollBar.Direction.vertical);
        hud_shift_scrollBar         = visualManager.newLoopingScrollBar(hud_shiftScrollBar_vertical, 52, 43, 4, 43, CharLoopingScrollBar.Direction.vertical);
        hud_turn_lr_scrollBar       = visualManager.newLoopingScrollBar(hud_loopingScrollBar_horizon, 2135, 72, 2, 71, CharLoopingScrollBar.Direction.horizon);
        hud_turn_lr_scrollBar.location[1] = resolutionY - 1;
        hud_roll_up_dn_scrollBar.location[0] = (resolutionX >> 1) - (resolutionX >> 2);
        hud_shift_scrollBar.location[0] = (resolutionX >> 1) + (resolutionX >> 2);
        lbl10 = visualManager.newLabel(s_hudShiftValue, (resolutionX >> 1) + (resolutionX >> 2) - 1, (resolutionY >> 1) - 22, 17);
        //hud_shift_scrollBar
        hud_turn_lr_scrollBar_pointer = visualManager.newLabel("|\nV\n|", resolutionX>>1, resolutionY - 4, 2134);
        hud_roll_up_dn_scrollBar_pointer = visualManager.newLabel("=>", (resolutionX >> 1) - (resolutionX >> 2) - 4, resolutionY>>1, 2136);
        hud_crosshair = visualManager.newImage(hud_crosshairImg, 32765, 65, 17, ((resolutionX >> 1) - (65 >> 1)), ((resolutionY >> 1) - (17 >> 1)));
        //"107"  "57" 160 84
        hud_Radar = new Radar
        (
            hud_radarBG, 
            hud_radarPrinter,
            visualManager.fraps_buffer, 124, visualManager.resolution, 
            21, (visualManager.resolution[0] - 12), 
            11, getMyJet(), visibility * 10
        );
        visualManager.newImage(hud_Radar);
        mainCamera.setRadar(hud_Radar);
	}
	
	private final void initClouds() {
        clouds = new ArrayList<ThreeDs>();
        objectsManager.newStaticObjectList(clouds);
        cloudMan = new CloudsManager(gameManager, refreshHz >> 1, visibility);
	}
	
	private final void initKeyboardAndMouse() {
        Object mouse = new MouseWheelControl(eventManager.EventFrapsQueue_keyboard);
        eventManager.mainScr.addMouseWheelListener((MouseWheelListener) mouse);
        eventManager.mainScr.addMouseListener((MouseListener) mouse);
        eventManager.addKeyListener(new ContinueListener(this));

        keyState_W =
        keyState_A =
        keyState_S =
        keyState_D =
        keyState_Up =
        keyState_Dn =
        keyState_Lf =
        keyState_Rt =
        keyState_X =
        keyState_V =
        keyState_TAB =
        keyState_SPACE =
        keyState_SHIFT = false;
	}
	
	private final void initSoundTrack(String bgm_file) {
        soundTrack = new SoundTrack(bgm_file);
	}
	
	public Game(
		String myJetModel_file,
		String hud_horizonIndicator,
		String hud_loopingScrollBar_vertical,
        String hud_shiftScrollBar_vertical,
		String hud_loopingScrollBar_horizon,
		String hud_crosshairImg,
        String hud_friend,
        String hud_enemy,
        String hud_locking,
        String hud_locked,
        String hud_missileWarning,
        String hud_lockingWarning,
		String hud_radarBG,
		String hud_radarPrinter,
		String hud_scoreListBG,
		String cfg_file,
		String rec_file,
		String bgm_file,
		int resolutionX, 
		int resolutionY, 
		int refresh_rate,
		int fontSize,
		int fontIndx
	) {
		super(resolutionX, resolutionY, refresh_rate, false);
		initClasses();
		initUI(fontSize, fontIndx);
		initRank();
		initDatastructure(hud_scoreListBG, resolutionX, resolutionY);
		initMe(myJetModel_file, hud_friend, hud_enemy, hud_locking, hud_locked, hud_missileWarning, hud_lockingWarning);
		initPlayers(myJetModel_file, cfg_file, rec_file);
        initHUDs(resolutionX, resolutionY, 
            hud_horizonIndicator,
            hud_crosshairImg,
            hud_loopingScrollBar_vertical,
            hud_shiftScrollBar_vertical,
            hud_loopingScrollBar_horizon,
            hud_radarBG,
            hud_radarPrinter
        );
        initClouds();
        initKeyboardAndMouse();
        initSoundTrack(bgm_file);
        /*
        CharObject flag = new CharObject(myJetModel_file, true);
        flag.location[2] = 10000;
        flag.specialDisplay = '#';
        flag.visible = true;
        clouds.add(flag);*/
        recording = false;
	}
	
	public final void addKillTip(Aircraft killer, Aircraft deader, String WeaponName) {
		killTipList.addLast(killer.getID() + " >>" + WeaponName + ">> " + deader.getID());
		if(killTipList.size() > maxKillTipCount)
			killTipList.removeFirst();
		
		tmp.delete(0, tmp.length());
		for(String aLine : killTipList) {
			tmp.append(aLine);
			tmp.append('\n');
		}
		lblKillTipList.setText(tmp.toString());
		killTipListUpdateTimeLeft = killTipListUpdateTime;
	}
	
	public final void colorFlash(
		int R_Fore, int G_Fore, int B_Fore, 
		int R_Back, int G_Back, int B_Back, 
		int time
	) {
		eventManager.mainScr.setForeground(new Color(R_Fore, G_Fore, B_Fore));
		eventManager.mainScr.setBackground(new Color(R_Back, G_Back, B_Back));
		colorChangedTime = time;
	}
	
	private final void updateKillList() {
		if(--killTipListUpdateTimeLeft <= 0) {
			if(!killTipList.isEmpty())
				killTipList.removeFirst();
			
			tmp.delete(0, tmp.length());
			for(String aLine : killTipList) {
				tmp.append(aLine);
				tmp.append('\n');
			}
			lblKillTipList.setText(tmp.toString());
			killTipListUpdateTimeLeft = killTipListUpdateTime;
		}
	}
	
	private final void reLocateHUD() {
	    resolution_min = GraphicUtils.min(visualManager.getResolution_X(), visualManager.getResolution_Y()) >> 1;
		
	    final int resolution_X = visualManager.resolution[0], resolution_Y = visualManager.resolution[1];
	    final int progressBarLocationBaseX0 = (resolution_X >> 1) - 24;
        final int progressBarLocationBaseY  = 5;

        final int progressBarLocationBaseX3 = (resolution_X>>1) + 27;
        final int progressBarLocationBaseY3 = (resolution_Y>>1) - 7;
        final int progressBarLocationBaseX4 = (resolution_X>>1) - 30;

        final int progressBarLocationBaseXA = (int) (resolution_X>>1) + 26;
        final int progressBarLocationBaseXB = (int) (resolution_X>>1) - 31;
        final int progressBarLocationBaseYB = (int) (resolution_Y>>1) + 7;
        
        mainCamera.getHudWarning_missile().location[0] = (resolution_X - 16);
        mainCamera.getHudWarning_missile().location[1] = (resolution_Y >> 1);
        mainCamera.getHudWarningLocking().location[0]  = (resolution_X - 16);
        mainCamera.getHudWarningLocking().location[1]  = (resolution_Y >> 1);
        
        //lbltest.setLocation(18, (int)(resolution_Y * 0.8));
        
        lbl1.setLocation(progressBarLocationBaseX3, progressBarLocationBaseY3);
        /*lbl2.setLocation(progressBarLocationBaseX2, (progressBarLocationBaseY2 + 1));
        lbl3.setLocation(progressBarLocationBaseX2, (progressBarLocationBaseY2 + 2));*/
        lbl4.setLocation(progressBarLocationBaseX4, progressBarLocationBaseY3);
        
        lbl5.setLocation(progressBarLocationBaseX0, progressBarLocationBaseY);
        //lbl6.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 2);
        //lbl7.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 4);
        //lbl8.setLocation(progressBarLocationBaseX, progressBarLocationBaseY + 6);
        lbl9.setLocation((resolution_X - 18), (resolution_Y >> 1) + 9);

        EndScreen.setLocation(((resolution_X>>1) - 9), (int) (resolution_Y * 0.3));
        lblRespawnTimeLeft.setLocation(((resolution_X>>1) - 9), (int) (resolution_Y * 0.3));
        lblGameTimeLeft.setLocation(((resolution_X>>1) - 11), 3);
        lblRecoding.setLocation(((resolution_X>>1) - 8), 6);
        hud_HP_progressBar.setLocation(progressBarLocationBaseX0 + 4, progressBarLocationBaseY);
        hud_pushTime_progressBar.setLocation((resolution_X >> 1) + (resolution_X >> 2) + 1, (resolution_Y >> 1) + 22);
        hud_cannon_progressBar.setLocation(progressBarLocationBaseX3 + 1, progressBarLocationBaseYB);
        hud_cannonReloading_progressBar.setLocation(progressBarLocationBaseXA, progressBarLocationBaseYB + 1);
        
        hud_missile_progressBar.setLocation(progressBarLocationBaseXB + 1, progressBarLocationBaseYB);
        hud_missileReloading_progressBar.setLocation(progressBarLocationBaseXB, progressBarLocationBaseYB + 1);
        hud_decoy_progressBar.setLocation((resolution_X - 11), (resolution_Y >> 1) + 9);
        hud_decoyReloading_progressBar.setLocation((resolution_X - 18), (resolution_Y >> 1) + 10);
        
        hud_turn_lr_scrollBar.location[1] = resolution_Y - 1;
        hud_turn_lr_scrollBar.location[0] = resolution_X >> 1;
        hud_roll_up_dn_scrollBar.location[1] = (resolution_Y >> 1);
        hud_roll_up_dn_scrollBar.location[0] =  (resolution_X >> 1) - (resolution_X >> 2);
        hud_shift_scrollBar.location[1] = (resolution_Y >> 1);
        hud_shift_scrollBar.location[0] =  (resolution_X >> 1) + (resolution_X >> 2);
        lbl10.setLocation((resolution_X >> 1) + (resolution_X >> 2) - 1, (resolution_Y >> 1) - 22);

        hud_turn_lr_scrollBar_pointer.setLocation(resolution_X>>1, resolution_Y - 4);
        hud_roll_up_dn_scrollBar_pointer.setLocation((resolution_X >> 1) - (resolution_X >> 2) - 4, resolution_Y>>1);
        
        hud_crosshair.setLocation(((resolution_X >> 1) - (65 >> 1)), ((resolution_Y >> 1) - (17 >> 1)));
        
        hud_Radar.setLocation((visualManager.resolution[0] - 12), 11);
        
        scoreShow.setLocation(resolution_X >> 1, resolution_Y >> 1);
	}
	
	private final void updateHUD() {

        updateKillList();

        if(isRecording()) {
            lblRecoding.visible = true;
            if(isRecordingPaused()) {
                lblRecoding.setText("Recording Paused..");
            } else lblRecoding.setText("Recording Video...");
        } else lblRecoding.visible = false;
        
        float x = getMyJet().getCurrentDirectionXYZ()[0];
        float y = getMyJet().getCurrentDirectionXYZ()[1];
	    
        /*
	    String testInfo = 
            "LocatVector - " +
                String.format("%.2f", myJet.location[0]) + ", " + 
                String.format("%.2f", myJet.location[1]) + ", " + 
                String.format("%.2f", myJet.location[2]) + '\n' +
	        
            "SpeedVector - " +
                String.format("%.2f", speedVec[0]) + ", " + 
                String.format("%.2f", speedVec[1]) + ", " + 
                String.format("%.2f", speedVec[2]) + '\n' +

            "DirecVector - " +
                String.format("%.2f", direcVec[0]) + ", " + 
                String.format("%.2f", direcVec[1]) + ", " + 
                String.format("%.2f", direcVec[2]) + '\n' +
            
            "DirecDegree - " +
                String.format("%.2f", direcXYZ[0]) + ", " + 
                String.format("%.2f", direcXYZ[1]) + ", " + 
                String.format("%.2f", direcXYZ[2]);
	    */
        /*
	    lbltest.setText("tracing " + ((npctest.tracingTarget == null)? "null" : npctest.tracingTarget.getID()) + ",\n" + 
                        "lockedBy " + ((npctest.locked_By == null)? "null" : npctest.locked_By.getID()) + ",\n" + 
	                    "currentMaxLockingPriority " + npctest.currentMaxLockingPriority + ",\n" +
                        "Selected " + npctest.lockingSelected + ",\n\n" + 
	                    "lockTimeLeft " + npctest.lockTimeLeft + ",\n" + 
	                    "locked " + npctest.locked + ",\n" + 
                        "currentSelectObj " + ((npctest.currentSelectObj == null)? "null" : npctest.currentSelectObj.getID()) + ",\n\n" +
	                    "missileLeft " + npctest.missileMagazineLeft
	    );
	    */
        
		lbl1.setText(String.format("%.0f", getMyJet().getSpeed() * 12));
		/*lbl2.setText("shift: " + String.format("%.2f", getMyJet().getControl_stick_acc()));
		lbl3.setText("rpm  : " + String.format("%.2f", Aircraft.getCurrentRPM(getMyJet().getMax_rpm(), getMyJet().getControl_stick_acc())));*/
		lbl4.setText(String.format("%.0f", (-getMyJet().location[0] + 200) / 10));
		//lbl5.setText("roll_angle: " + String.format("%.2f", myJet.roll_angle[2]));
		//lbl6.setText("cameraRoll: " + String.format("%.2f", myJet.cameraRollAngle[2]));
		
		hud_HP_progressBar.value = (float)getMyJet().getHP() / 100.0F;
		hud_pushTime_progressBar.value = getMyJet().getPushTimeLeft() / getMyJet().getMaxPushTime();
		//hud_cannon_progressBar.value = (float)getMyJet().cannonMagazineLeft / (float)getMyJet().cannonMagazine;
		hud_cannon_progressBar.setText(Integer.toString(getMyJet().cannonMagazineLeft));
        
		if(getMyJet().isAlive()) {
			lblRespawnTimeLeft.visible = false;
			hud_roll_up_dn_angle.visible = hud_roll_up_dn_scrollBar.visible = lbl10.visible = hud_pushTime_progressBar.visible = hud_shift_scrollBar.visible = hud_turn_lr_scrollBar.visible = 
					hud_Radar.visible = lbl1.visible = /*lbl2.visible = lbl3.visible = */lbl4.visible = true;
			//------------[Dynamic HUDs]------------
			
			boolean tmpCondi = GraphicUtils.abs(getMyJet().roll_angle[1]) > GraphicUtils.RAD90;
			hud_roll_up_dn_angle.angle = tmpCondi? getMyJet().roll_angle[2] + GraphicUtils.RAD180 : getMyJet().roll_angle[2];
			float tmp = GraphicUtils.sin(tmpCondi? getMyJet().roll_angle[1] :-getMyJet().roll_angle[1]);
			hud_roll_up_dn_angle.location[0] = (int) (GraphicUtils.sin(getMyJet().roll_angle[2]) * tmp * - resolution_min + (visualManager.getResolution_X()>>1));
			hud_roll_up_dn_angle.location[1] = (int) (GraphicUtils.cos(getMyJet().roll_angle[2]) * tmp * - resolution_min + (visualManager.getResolution_Y()>>1));
			
			hud_roll_up_dn_scrollBar.value = (int) (y / 0.08726646259971647F/*5=(360 / 72)*/);
			hud_turn_lr_scrollBar.value    = (int) ((x + GraphicUtils.RAD180) / 0.08726646259971647F);
			hud_shift_scrollBar.value      = GraphicUtils.max((int) ((32 - getMyJet().getControl_stick_acc()) / (38.0F / 43.0F) + 4), 1);
			if(hud_roll_up_dn_scrollBar.value > 0) {
	            backRGB = (int) (y / 0.02617993877991495F);
			} else backRGB = 0;
		} else {
			hud_roll_up_dn_angle.visible = hud_roll_up_dn_scrollBar.visible = lbl10.visible = hud_pushTime_progressBar.visible = hud_shift_scrollBar.visible = hud_turn_lr_scrollBar.visible = 
					hud_Radar.visible = lbl1.visible = /*lbl2.visible = lbl3.visible = */lbl4.visible = false;
			lbl1.visible = /*lbl2.visible = lbl3.visible = */lbl4.visible = false;
			lblRespawnTimeLeft.visible = true;
			lblRespawnTimeLeft.setText("You will respawn in\n     " + (getMyJet().getRespwanAtTime() - (float)System.currentTimeMillis()/1000) + " seconds.");
		}
		
		if(getMyJet().cannonMagazineLeft == 0)
		{
			hud_cannonReloading_progressBar.visible = true;
			hud_cannonReloading_progressBar.value = 1.0F - (float)getMyJet().cannonReloadingTimeLeft / (float)getMyJet().cannonReloadingTime;	
		}
		else hud_cannonReloading_progressBar.visible = false;
		
		hud_missile_progressBar.value = (float)getMyJet().missileMagazineLeft / (float)getMyJet().missileMagazine;
		if(getMyJet().missileMagazineLeft == 0)
		{
			hud_missileReloading_progressBar.visible = true;
			hud_missileReloading_progressBar.value = 1.0F - (float)getMyJet().missileReloadingTimeLeft / (float)getMyJet().missileReloadingTime;	
		}
		else hud_missileReloading_progressBar.visible = false;
		
		hud_decoy_progressBar.value = (float)getMyJet().decoyMagazineLeft / (float)getMyJet().decoyMagazine;
		if(getMyJet().decoyMagazineLeft == 0)
		{
			hud_decoyReloading_progressBar.visible = true;
			hud_decoyReloading_progressBar.value = 1.0F - (float)getMyJet().decoyReloadingTimeLeft / (float)getMyJet().decoyReloadingTime;	
		}
		else hud_decoyReloading_progressBar.visible = false;
	}
	
	public int[] getResolution() {
	    return visualManager.resolution;
	}

	private final void keyResponse() {
		
		flgWheelUp = flgWheelDn = false;
		while(!eventManager.EventFrapsQueue_keyboard.isEmpty())
		{
			keyPressed = eventManager.popAKeyOpreation();
			switch(keyPressed)
			{
				//-----------[Mouse]---------
			
				case 4096:if(!flgWheelUp) {
					//getMyJet().control_acc(0.2F);
				    if(getMyJet().isAlive() && !getMyJet().isPushing) {
				        if(getMyJet().fov_1stPerson > getMyJet().fov_gunFiring)
	                        getMyJet().fov_1stPerson -= 0.2F;
	                    if(getMyJet().fov_3thPerson > getMyJet().fov_gunFiring)
	                        getMyJet().fov_3thPerson -= 0.2F;
				    }
					flgWheelUp = true;
				} break;
				
				case 8192:if(!flgWheelDn) {
					//getMyJet().control_dec();
                    if(getMyJet().isAlive()) {
    				    if(getMyJet().fov_1stPerson < getMyJet().fov_1stPerson_base)
                            getMyJet().fov_1stPerson += 0.2F;
                        if(getMyJet().fov_3thPerson < getMyJet().fov_3thPerson_base)
                            getMyJet().fov_3thPerson += 0.2F;
                    }
					flgWheelDn = true;
				} break;
				
				case 16385:
					keyState_SPACE = true;
				break;
				
				case -16385:
					keyState_SPACE = false;
				break;
				
				case 16386:
				{
					Missile m = getMyJet().missileOpenFire(true, lockedEnemy);
					if(m != null)
						mainCamera.connectLocationAndAngle(m.location, m.getCameraRollAngle());
				}
				break;
				
				case 16387:
					getMyJet().missileOpenFire(false, lockedEnemy);
				break;
				
				//-----------------------------
                    
				case -KeyEvent.VK_C:
					++getMyJet().cameraLocationFlag;
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
				
				case KeyEvent.VK_UP://W
                    keyState_Up = true;
                break;
                case -KeyEvent.VK_UP://-W
                    keyState_Up = false;
                break;
                    
                case KeyEvent.VK_DOWN://S
                    keyState_Dn = true;
                break;
                case -KeyEvent.VK_DOWN://-S
                    keyState_Dn = false;
                break;
                
                case KeyEvent.VK_LEFT://A
                    keyState_Lf = true;
                break;
                case -KeyEvent.VK_LEFT://-A
                    keyState_Lf = false;
                break;
                
                case KeyEvent.VK_RIGHT://D
                    keyState_Rt = true;
                break;
                case -KeyEvent.VK_RIGHT://-D
                    keyState_Rt = false;
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
					mainCamera.connectLocationAndAngle(getMyJet().getCameraLocation(), getMyJet().getCameraRollAngle());
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
				    if(recording) break;
					mainCamera.resizeScreen(visualManager.resolution[0] - 1, visualManager.resolution[1]);
					reLocateHUD();
				break;
				case KeyEvent.VK_L:
                    if(recording) break;
					mainCamera.resizeScreen(visualManager.resolution[0] + 1, visualManager.resolution[1]);
					reLocateHUD();
				break;
                case KeyEvent.VK_K:
                    if(recording) break;
                    mainCamera.resizeScreen(visualManager.resolution[0], visualManager.resolution[1] + 1);
                    reLocateHUD();
                break;
                case KeyEvent.VK_I:
                    if(recording) break;
                    mainCamera.resizeScreen(visualManager.resolution[0], visualManager.resolution[1] - 1);
                    reLocateHUD();
                break;
                
				case -KeyEvent.VK_E:
				    soundTrack.switchPrevious();
				break;
				
				case -KeyEvent.VK_F11:
				    recordingPauseOrResume();
                break;
            
				case -KeyEvent.VK_F12:
				    recording = !recording;
				    if(recording) {
				        startRecording(
				            "recordedVidos/dogZ_" + 
				            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss")) +
				            "_" + GraphicUtils.absI(GraphicUtils.fastRanodmInt()) +
				            "_recording.dogGRecZ"
				        );  
				    } else finishRecording();
                break;
					
				case -KeyEvent.VK_R:
    				soundTrack.switchNext();
				break;
                
                case -KeyEvent.VK_ESCAPE:
                    exit();
                    break;
			}
		}
		
        SinglePoint xy;
        xy = eventManager.popAMouseOpreation();
        float currentRange = (getMyJet().fov_1stPerson_base - getMyJet().fov_1stPerson) / getMyJet().fov_range;
        float tmpF = (0.00018180513041607602F - currentRange * 0.00007791648446403259F);
        if(!(keyState_Up || keyState_Dn || keyState_Lf || keyState_Rt)) {
            
            /*if(getMyJet().isCannonFiring) {//7.791648446403259e-05F
                getMyJet().control_roll_lr((float)(-xy.x) * tmpF);
                getMyJet().control_roll_up_dn((float)(-xy.y) * tmpF);
            } else {*/
            getMyJet().control_roll_lr((float)(-xy.x) * tmpF);
            getMyJet().control_roll_up_dn((float)(-xy.y) * tmpF);
            //}
        }
        
		if(keyState_W) getMyJet().control_acc(0.05F);
		
		tmpF = (0.0087F - currentRange * 0.0043F);;

		if(keyState_A)  getMyJet().control_turn_lr(-tmpF);
		if(keyState_D)  getMyJet().control_turn_lr(tmpF);
		if(keyState_Up) getMyJet().control_roll_up_dn(-tmpF);
        if(keyState_Dn) getMyJet().control_roll_up_dn(tmpF);
        if(keyState_Lf) getMyJet().control_roll_lr(tmpF);
        if(keyState_Rt) getMyJet().control_roll_lr(-tmpF);
        
		if(keyState_S) getMyJet().control_brk();
		else getMyJet().control_stop_breaking();

        if(keyState_X) getMyJet().makeDecoy();
        
		if(keyState_SHIFT) {
		    getMyJet().control_push();
		} else {
		    getMyJet().control_stop_pushing();
		}
		
		if(keyState_SPACE) getMyJet().cannonOpenFire();
		else getMyJet().cannonStopFiring();
		
		scoreShow.visible = keyState_TAB;
		mainCamera.setReversed(keyState_V);
	}
	
	public final void exit() {
        setRunning(false);
        
	    File screenSizeFile      = new File("resources/screenSize.cfg");
        FileOutputStream fos     = null;
        DataOutputStream dos     = null;
        BufferedOutputStream bos = null;
        
        if(!screenSizeFile.exists()) {
            try {
                screenSizeFile.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }
        
        try {
            fos = new FileOutputStream(screenSizeFile);
            bos = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bos);
            dos.writeInt(getResolution()[0]);
            dos.writeInt(getResolution()[1]);
            dos.writeInt(scrSize);
            dos.writeInt(fontIdx);
        } catch (IOException e1) { e1.printStackTrace(); } finally {
            if(dos != null) try {
                dos.close();
            } catch (IOException e1) { e1.printStackTrace(); }
        }
        //System.exit(0);
	}

    public void pause() {
        paused = true;
    }
    
    public void resume() {
        paused = false;
    }
	
	@Override
	public final void run()
	{
		//float tmp_float_arr_2[] = new float[2];
	    
	    double leftTime = 0;
		int    min, hor, sec, tmp;
		if(gameStopTime == -1)
			lblGameTimeLeft.visible = false;
		setRunning(true);
		execute(cloudMan);
        execute(soundTrack);
        
		//游戏主循环
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		while(isRunning() && (gameStopTime == -1  ||  (leftTime = gameStopTime - ((double)System.currentTimeMillis()/1000.0)) > 0))
		{
		    if(paused) try {
		        synchronized(this) { wait(); }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		    
			buffStatic();
			if(gameStopTime != -1)
			{
				hor = (int) (leftTime / 3600);
				tmp = (int) (leftTime - hor * 3600);
				min = (int) (tmp / 60);
				sec = tmp % 60;
				lblGameTimeLeft.setText("Round Time Left: " + hor + ':' + min + ':' + sec);
			}
			
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

            if((gBlack -= 1.0F) <= 1) gBlack = 1;
                
            if(--colorChangedTime <= 0)
            {
                rgbTmp = (int)(256 - gBlack);
                eventManager.mainScr.setForeground(new Color(255 - (int)(gBlack/2), rgbTmp, rgbTmp));
                eventManager.mainScr.setBackground(new Color((int)(backRGB * 0.5 / gBlack), (int)(backRGB * 0.8 / gBlack), (int)((float)backRGB / gBlack)));
            }
			
			//XXXXXXXXXXXXX[print new]XXXXXXXXXXXXXX
			lockedEnemy = (Aircraft) visualManager.mainCameraFeedBack;
			if(getMyJet().isPushing) {
	            visualManager.setEnableMotionalBlur(true);
                getMyJet().fov_1stPerson = getMyJet().fov_1stPerson_base;
                getMyJet().fov_3thPerson = getMyJet().fov_3thPerson_base;
			} else {
			    visualManager.setEnableMotionalBlur(false);
			}
			printNew();
			//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		}
		getMyJet().visible = false;
		buffStatic();
		EndScreen.visible = true;
		scoreShow.visible = true;
		printNew();
		
		try(FileWriter fw = new FileWriter(new File(recFile), true))
		{
			fw.write(getMyJet().getID());
			fw.write('\n');
			fw.write(Long.toString(gameTimeUsed));
			fw.write('\n');
			fw.write(Integer.toString(getMyJet().killed));
			fw.write('\n');
			fw.write(Integer.toString(getMyJet().dead));
			fw.write('\n');
		}	catch(IOException exc){}
		
		eventManager.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                
                switch (e.getKeyCode()) {
                case KeyEvent.VK_F12:
                    if(recording) {
                        lblRecoding.visible = false;
                        buffStatic();
                        printNew();
                        buffStatic();
                        printNew();
                        recording = false;
                        finishRecording();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if(recording) {
                        lblRecoding.visible = false;
                        buffStatic();
                        printNew();
                        buffStatic();
                        printNew();
                        recording = false;
                        finishRecording();
                    }
                    soundTrack.terminate();
                    lblRecoding.visible = false;
                    setRunning(false);
                    soundTrack.interrupt();
                    shutdown();
                    eventManager.removeKeyListener(this);
                    eventManager.setVisible(false);
                    eventManager.dispose();
                    break;
                }
            }
		});
	}
    
	public final void addGBlack(float val) {
	    if((gBlack += val) > 255) gBlack = 255;
	}
	
	public static void main(String[] args) {
        Game game = new Game(
            args[0] ,args[1] , args[2], args[3], args[4], args[5] ,
            args[6] , args[7], args[8], args[9], args[10], args[11], 
            args[12], args[13], args[14], args[15], args[16], args[17], 
            Integer.parseInt(args[18]), 
            Integer.parseInt(args[19]), 
            Integer.parseInt(args[20]),
            Integer.parseInt(args[21]),
            Integer.parseInt(args[22])
        );
        game.getIntoGameWorld();
        game.run();
    }
	
	public final void fireAmmo(Dynamic ammo) {
        firedAmmo.add(ammo);
    }

    public final int getRespawnTime() {
        return respawnTime;
    }

    public final void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public final Aircraft getMyJet() {
        return myJet;
    }

    public final void setMyJet(Aircraft myJet) {
        this.myJet = myJet;
    }
    
    public final float getWeaponMaxSearchingRange() {
        return mainCamera.getMaxSearchingRange();
    }
    
    public final float[] getPlayerCameraLocation() {
        return mainCamera.location;
    }
    
    public final float[] getPlayerLocation() {
        return myJet.location;
    }
    
    public final void throwDecoy(Decoy decoy) {
        waitToAddQue.add(decoy);
    }
    
    public final void decoyDisable(ListIterator<ThreeDs> decoyID) {
        deleteQue.add(decoyID);
    }
    
    public final void newEffect(Dynamic effect) {
        effects.add(effect);
    }
    
    public final void newCloud(RandomCloud cloud) {
        clouds.add(cloud);
    }
    
    public final ArrayList<ThreeDs> getClouds() {
        return clouds;
    }

    public final boolean isRunning() {
        return running;
    }

    public final void setRunning(boolean running) {
        this.running = running;
    }
}
