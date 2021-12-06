package dogfight_Z.dogLog.controller;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JTextArea;

import dogfight_Z.CloudsManager;
import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.Cameras.TDCamera;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharHUD;
import graphic_Z.HUDs.CharImage;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharLoopingScrollBar;
import graphic_Z.HUDs.CharProgressBar;
import graphic_Z.HUDs.HUD;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Managers.EventManager;
import graphic_Z.Objects.CharMessObject;
import graphic_Z.Objects.TDObject;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.Common;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.HzController;
import dogfight_Z.dogLog.view.Menu;

public class ScreenResizer extends Menu
{
	public float fov = 2.2F;
	public float visibility = 12480;
	
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

    private VManager visualManager;
	private int scrSize = 8;
    private int currentFontIdx;
	private MCamera mainCamera;
	private int resolution_min;
	
	private static Font supportedFonts[]; static {
        supportedFonts = new Font[3];
        try {
            supportedFonts[0] = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("consola.ttf"));
            supportedFonts[1] = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("DejaVuSansMono_0.ttf"));
            supportedFonts[2] = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("simsun.ttc"));
        } catch (FontFormatException | IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    };
	
	/*
	public void initUI(int fontSize) {
        eventManager.setTitle("dogfightZ - Screen Resize");
        scrSize = fontSize;
        eventManager.setScrZoom(fontSize);
	}
	*/
	
	private static class VManager {
	    int    resolution[];
	    char   fraps_buffer[][];
	    List<Iterable<ThreeDs>> staticObjLists;
	    TreeSet<HUD> HUDs;
	    static char point[] = {'@','$','0','G','Q','O','o','*','+','=','^',';','"',':','~','-',',','`','\'','.'};
	    
	    public VManager(int resolution[], char frapsBuffer[][], int x, int y) {
	        this.resolution = resolution;
	        this.fraps_buffer = frapsBuffer;
	        staticObjLists = new ArrayList<Iterable<ThreeDs>>();
	        HUDs           = new TreeSet<HUD>();
            resolution[0]  = x;
            resolution[1]  = y;
	    }
	    
	    public void printNew(JTextArea mainScr) {
	        
	        for (
	            Iterator<HUD> iter = HUDs.iterator();
	            iter.hasNext();
	            iter.next().printNew()
	        );
	    }
	    /*
	    public void reSizeScreen(int x, int y) {
	        
	        fraps_buffer = new char[y][];
	        emptyLine    = new char[x];
	        
	        for(int i=0 ; i<y ; ++i)
	            fraps_buffer[i] = new char[x];

	        for(int i=0 ; i<x ; ++i)
	            emptyLine[i] = ' ';
	        
	        scr_show = new StringBuilder(x * y);
	        
	        resolution[0] = x;
	        resolution[1] = y;
	        
	        CharHUD ahud = null;
	        for(HUD hud : HUDs) {
	            ahud = (CharHUD) hud;
	            ahud.reSizeScreen(resolution, fraps_buffer);
	        }
	    }
	    */
	    public CharDynamicHUD newDynamicHUD(String HUDImgFile, int HUDLayer, int size_X, int size_Y)
	    {
	        CharDynamicHUD newHud = new CharDynamicHUD
	        (
	            HUDImgFile, fraps_buffer, 
	            HUDLayer, resolution, 
	            size_X, size_Y, 
	            resolution[0] >> 1, resolution[1] >> 1
	        );
	        
	        HUDs.add(newHud);
	        return newHud;
	    }
	    
	    public void newImage(CharImage newHud) {
	        HUDs.add(newHud);
	    }
	    
	    public CharImage newImage(String HUDImgFile, int HUDLayer, int size_X, int size_Y, int locatX, int locatY) {
	        CharImage newHud = new CharImage (
	            HUDImgFile, 
	            fraps_buffer, 
	            size_X, size_Y,
	            locatX, locatY,
	            HUDLayer, resolution, true
	        );
	        
	        HUDs.add(newHud);
	        return newHud;
	    }
	    
	    public CharLoopingScrollBar newLoopingScrollBar(
	        String HUDImgFile, int HUDLayer, 
	        int size_X, int size_Y,
	        int size_Show, CharLoopingScrollBar.Direction direction
	    ) {
	        CharLoopingScrollBar newHud = new CharLoopingScrollBar (
	            HUDImgFile, fraps_buffer, 
	            HUDLayer, resolution, 
	            size_X, size_Y, 
	            resolution[0] >> 1, resolution[1] >> 1,
	            direction, 0, size_Show
	        );
	        
	        HUDs.add(newHud);
	        return newHud;
	    }
	    
	    public CharLabel newLabel(String Text, int location_X,  int location_Y, int HUDLayer) {
	        CharLabel newLbl = 
	            new CharLabel(fraps_buffer, HUDLayer, resolution, Text, location_X, location_Y);
	        HUDs.add(newLbl);
	        return newLbl;
	    }
	    
	    public CharProgressBar newProgressBar
	    (
	        int    location_X,  
	        int    location_Y, 
	        int    HUDLayer,
	        int    size,
	        char   visual,
	        CharProgressBar.Direction direction,
	        float value
	    )
	    {
	        CharProgressBar newBar = 
	            new CharProgressBar(fraps_buffer, HUDLayer, resolution, location_X, location_Y, size, visual, direction, value);
	        HUDs.add(newBar);
	        return newBar;
	    }
	    
	}
	
	private static class MCamera extends TDCamera<CharWorld> {
	    
	    //float location[];
	    float roll_source[];
	    //float FOV;
	    
	    public MCamera(float cameraFOV, float visblt, CharWorld inWhichWorld) {
	        super(cameraFOV, visblt, inWhichWorld);
	        location = new float[3];
	        roll_source = new float[3];
	    }
	    /*
	    public void resizeScreen(int x, int y) {
	        if(x < 1 || y < 1) return;
	        visualManager.reSizeScreen(x, y);
	    }*/

        @Override
        public Object exposure() {
            return null;
        }
	}
	
	private void initMainCamera(int x, int y) {
	    visualManager = new VManager(resolution, screenBuffer, x, y);
	    mainCamera = new MCamera(2.6F, 12480, null);
	}
	
	private void initMe(String myJetModel_file, int x, int y) {

        myJet = new CharMessObject(myJetModel_file, 10000, true);
        myJet.setLocation(0, 0, 60);
        myJet.specialDisplay = '@';

        initMainCamera(x, y);

        myJet.visible = true;
	}
	
	private void playersCameraManage()
    {
	    
        float t1, t2, t3;

        mainCamera.location[0] = -240;
        mainCamera.location[1] = 0;
        mainCamera.location[2] = -960;
        
        if(mainCamera.roll_source[0] < 0)
        {
            if((-myJet.roll_angle[0]) - mainCamera.roll_source[0] < GraphicUtils.RAD180)
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0];
            else
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0] - GraphicUtils.RAD360;
        }
        else if(mainCamera.roll_source[0] > 0)
        {
            if(mainCamera.roll_source[0] - (-myJet.roll_angle[0]) < GraphicUtils.RAD180)
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0];
            else
                t1 = -myJet.roll_angle[0] - mainCamera.roll_source[0] + GraphicUtils.RAD360;
        }
        else t1 = -myJet.roll_angle[0];
        
        if(mainCamera.roll_source[1] < 0)
        {
            if((-myJet.roll_angle[1]) - mainCamera.roll_source[1] < GraphicUtils.RAD180)
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1];
            else
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1] - GraphicUtils.RAD360;
        }
        else if(mainCamera.roll_source[1] > 0)
        {
            if(mainCamera.roll_source[1] - (-myJet.roll_angle[1]) < GraphicUtils.RAD180)
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1];
            else
                t2 = -myJet.roll_angle[1] - mainCamera.roll_source[1] + GraphicUtils.RAD360;
        }
        else t2 = -myJet.roll_angle[1];
        
        if(mainCamera.roll_source[2] < 0)
        {
            if((-myJet.roll_angle[2]) - mainCamera.roll_source[2] < GraphicUtils.RAD180)
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2];
            else
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2] - GraphicUtils.RAD360;
        }
        else if(mainCamera.roll_source[2] > 0)
        {
            if(mainCamera.roll_source[2] - (-myJet.roll_angle[2]) < GraphicUtils.RAD180)
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2];
            else
                t3 = -myJet.roll_angle[2] - mainCamera.roll_source[2] + GraphicUtils.RAD360;
        }
        else t3 = -myJet.roll_angle[2];
        
        t1 /= 6;
        t2 /= 6;
        t3 /= 6;
        
        mainCamera.roll_source[0] = (mainCamera.roll_source[0] + t1) % GraphicUtils.RAD360;
        mainCamera.roll_source[1] = (mainCamera.roll_source[1] + t2) % GraphicUtils.RAD360;
        mainCamera.roll_source[2] = (mainCamera.roll_source[2] + t3) % GraphicUtils.RAD360;
    
        TDObject.getXYZ_afterRolling
        (
            mainCamera.location[0], mainCamera.location[1], mainCamera.location[2],
            myJet.roll_angle[0],     myJet.roll_angle[1],     myJet.roll_angle[2],
            
            mainCamera.location
        );

        mainCamera.location[0] += myJet.location[0];
        mainCamera.location[1] += myJet.location[1];
        mainCamera.location[2] += myJet.location[2];
        
        mainCamera.setFOV(2.6F);
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
	    resolution_min = GraphicUtils.min(visualManager.resolution[0], visualManager.resolution[1]) >> 1;
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
        
        hud_HP_progressBar          = visualManager.newProgressBar((progressBarLocationBaseX + 4), progressBarLocationBaseY, 205, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_pushTime_progressBar    = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 2), 209, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        
        hud_cannon_progressBar           = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 4), 210, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_cannonReloading_progressBar  = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 5), 212, 25, '-', CharProgressBar.Direction.horizon, 1.0F);
        hud_missile_progressBar          = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 6), 214, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_missileReloading_progressBar = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 7), 216, 25, '-', CharProgressBar.Direction.horizon, 1.0F);
        hud_decoy_progressBar            = visualManager.newProgressBar((progressBarLocationBaseX + 4), (progressBarLocationBaseY + 8), 218, 20, '|', CharProgressBar.Direction.horizon, 1.0F);
        hud_decoyReloading_progressBar   = visualManager.newProgressBar(progressBarLocationBaseX, (progressBarLocationBaseY + 9), 220, 25, '-', CharProgressBar.Direction.horizon, 1.0F);
        
        hud_roll_up_dn_angle     = visualManager.newDynamicHUD(hud_horizonIndicator, 21, 51, 33);
        hud_roll_up_dn_scrollBar = visualManager.newLoopingScrollBar(hud_loopingScrollBar_vertical, 50, 72, 4, 43, CharLoopingScrollBar.Direction.vertical);
        hud_turn_lr_scrollBar    = visualManager.newLoopingScrollBar(hud_loopingScrollBar_horizon, 32, 72, 2, 71, CharLoopingScrollBar.Direction.horizon);
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
	
	private void initClouds() {
        clouds = new ArrayList<ThreeDs>();
        visualManager.staticObjLists.add(clouds);

        cloudRefreshRateController = new HzController(32);
        cloudMan = new CloudsManager(clouds, cloudRefreshRateController, mainCamera, visibility);
        cloudManThread = new Thread(cloudMan);
	}
	
	private void initUI() {
	    screen.setFont(supportedFonts[currentFontIdx].deriveFont(Font.PLAIN, scrSize));
	}
	
	private void constructor
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
		int fontSize,
		int fontIdx,
        int resolutionSetting[]
	)
	{
        if(resolutionSetting != null) {
            resolutionX    = resolutionSetting[0];
            resolutionY    = resolutionSetting[1];
            scrSize        = resolutionSetting[2];
            currentFontIdx = resolutionSetting[3];;
        } else currentFontIdx = 1;
        
	    initUI();
		initMe(myJetModel_file, resolutionX, resolutionY);
        initHUDs(resolutionX, resolutionY, 
            hud_horizonIndicator,
            hud_crosshairImg,
            hud_loopingScrollBar_vertical,
            hud_loopingScrollBar_horizon,
            hud_radarBG,
            hud_radarPrinter
        );
        initClouds();
        
        playersCameraManage();
        myJet.visible = true;
        cloudManThread.start();
	}
	
	public ScreenResizer(String args[], JTextArea screen, int resolutionSetting[]) {
        super(
                args, 
                screen, 
                1, 
                resolutionSetting == null? Integer.parseInt(args[16]): resolutionSetting[0], 
                resolutionSetting == null? Integer.parseInt(args[17]): resolutionSetting[1]
        );
        constructor (
            args[0] ,args[1] , args[2], args[3], args[4], args[5] ,
            args[6] , args[7], args[8], args[9], args[10], args[11], 
            args[12], args[13], args[14], args[15], 
            Integer.parseInt(args[16]), 
            Integer.parseInt(args[17]), 
            Integer.parseInt(args[18]),
            Integer.parseInt(args[19]),
            Integer.parseInt(args[20]),
            resolutionSetting
	     );
	}
	
	private void reLocateHUD() {
	    resolution_min = GraphicUtils.min(visualManager.resolution[0], visualManager.resolution[1]) >> 1;
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
        hud_roll_up_dn_scrollBar.location[0] = (int) (resolution_X * 0.2F);
        
        hud_crosshair.setLocation(((resolution_X >> 1) - (65 >> 1)), ((resolution_Y >> 1) - (11 >> 1)));
        
        hud_roll_up_dn_angle.location[0] = (int) (GraphicUtils.sin(myJet.roll_angle[2]) * GraphicUtils.sin(GraphicUtils.abs(myJet.roll_angle[1]) > 90.0F? -myJet.roll_angle[1] : myJet.roll_angle[1]) * - resolution_min + (visualManager.resolution[0]>>1));
        hud_roll_up_dn_angle.location[1] = (int) (GraphicUtils.cos(myJet.roll_angle[2]) * GraphicUtils.sin(GraphicUtils.abs(myJet.roll_angle[1]) > 90.0F?  myJet.roll_angle[1] :-myJet.roll_angle[1]) * - resolution_min + (visualManager.resolution[1]>>1));
        
        hud_Radar.setLocation((visualManager.resolution[0] - 22), 1);
	}
	/*
	private void updateHUD() {
		lbl4.setText("hight: " + String.format("%.2f", -myJet.location[0]));
		
		hud_HP_progressBar.value = 1;
		hud_pushTime_progressBar.value = 1;
		hud_cannon_progressBar.value = 1;
		
		lblRespawnTimeLeft.visible = false;
		hud_roll_up_dn_angle.visible = hud_roll_up_dn_scrollBar.visible = hud_turn_lr_scrollBar.visible = 
				hud_Radar.visible = lbl1.visible = lbl2.visible = lbl3.visible = lbl4.visible = true;
		//------------[Dynamic HUDs]------------
		
		hud_roll_up_dn_angle.angle = GraphicUtils.abs(myJet.roll_angle[1]) > 90.0? myJet.roll_angle[2] + 180 : myJet.roll_angle[2];
		
		hud_roll_up_dn_angle.location[0] = (int) (GraphicUtils.sin(GraphicUtils.toRadians(myJet.roll_angle[2])) * GraphicUtils.sin(GraphicUtils.toRadians(GraphicUtils.abs(myJet.roll_angle[1]) > 90.0? -myJet.roll_angle[1] : myJet.roll_angle[1])) * - resolution_min + (visualManager.resolution[0]>>1));
		hud_roll_up_dn_angle.location[1] = (int) (GraphicUtils.cos(GraphicUtils.toRadians(myJet.roll_angle[2])) * GraphicUtils.sin(GraphicUtils.toRadians(GraphicUtils.abs(myJet.roll_angle[1]) > 90.0?  myJet.roll_angle[1] :-myJet.roll_angle[1])) * - resolution_min + (visualManager.resolution[1]>>1));
		
		hud_roll_up_dn_scrollBar.value = (int) (myJet.roll_angle[1] / 360 * 72);
		hud_turn_lr_scrollBar.value    = (int) (-myJet.roll_angle[0] / 360 * 72);
	
		hud_cannonReloading_progressBar.visible = false;
		
		hud_missile_progressBar.value = 1;
		hud_missileReloading_progressBar.visible = false;
		
		hud_decoy_progressBar.value = 1;
		hud_decoyReloading_progressBar.visible = false;
	}
	*/
	public int[] run()
	{
		//游戏主循环
	    /*
	    while(keyResponse()) {
			buffStatic();
			updateHUD();
			printNew();
			
		}
	    */
	    return new int[] {visualManager.resolution[0], visualManager.resolution[1], scrSize};
	}
	
    @Override
    public void getRefresh() {
        for(Iterable<ThreeDs> staticList : visualManager.staticObjLists) {
            for(ThreeDs aCloud : staticList) {
                CharFrapsCamera.exposureAnObject(
                    screenBuffer, 
                    resolution, 
                    mainCamera.location, 
                    visibility, 
                    mainCamera.FOV, 
                    resolution[0] >> 1, 
                    resolution[1] >> 1, 
                    aCloud, 
                    mainCamera.roll_source[0], 
                    mainCamera.roll_source[1], 
                    mainCamera.roll_source[2], 
                    true, 
                    VManager.point
                );
            }
        }
        
        CharFrapsCamera.exposureAnObject(
            screenBuffer, 
            resolution, 
            mainCamera.location, 
            visibility, 
            mainCamera.FOV, 
            resolution[0] >> 1, 
            resolution[1] >> 1, 
            myJet, 
            mainCamera.roll_source[0], 
            mainCamera.roll_source[1], 
            mainCamera.roll_source[2], 
            true, 
            VManager.point
        );
        
        visualManager.printNew(screen);
        
    }
    
    public void resizeScreen(int x, int y) {
        if(x < 1 || y < 1) return;
        super.resizeScreen(x, y);

        CharHUD ahud = null;
        for(HUD hud : visualManager.HUDs) {
            ahud = (CharHUD) hud;
            ahud.reSizeScreen(resolution, screenBuffer);
        }
    }

    @Override
    public Operation putKeyPressEvent(int keyCode) {
        
        switch(keyCode)
        {
            case 93://]
                scrSize += 1;
                screen.setFont(supportedFonts[currentFontIdx].deriveFont(Font.PLAIN, scrSize));
                break;
            case 91://[
                if(scrSize > 1)scrSize -= 1;
                screen.setFont(supportedFonts[currentFontIdx].deriveFont(Font.PLAIN, scrSize));
                break;

            case KeyEvent.VK_N:
                if(currentFontIdx-- == 0) currentFontIdx = EventManager.getSupportedFontsCount() - 1;
                screen.setFont(supportedFonts[currentFontIdx].deriveFont(Font.PLAIN, scrSize));
            break;
            case KeyEvent.VK_M:
                if(++currentFontIdx == EventManager.getSupportedFontsCount()) currentFontIdx = 0;
                screen.setFont(supportedFonts[currentFontIdx].deriveFont(Font.PLAIN, scrSize));
            break;
            
            case KeyEvent.VK_J:
                resizeScreen(resolution[0] - 1, resolution[1]);
                reLocateHUD();
                break;
            case KeyEvent.VK_L:
                resizeScreen(resolution[0] + 1, resolution[1]);
                reLocateHUD();
                break;
            case KeyEvent.VK_K:
                resizeScreen(resolution[0], resolution[1] + 1);
                reLocateHUD();
                break;
            case KeyEvent.VK_I:
                resizeScreen(resolution[0], resolution[1] - 1);
                reLocateHUD();
                break;
        }
        
        return null;
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
        return null;
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        
        switch(keyCode) {
        case KeyEvent.VK_ESCAPE:
            return new Operation(true, null, null, null, null, null);
        case KeyEvent.VK_ENTER:
            return new Operation(true, null, null, null, new int[] {resolution[0], resolution[1], scrSize, currentFontIdx}, null); 
        }
        return null;
    }

    @Override
    public Operation beforeRefreshNotification() {
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        return null;
    }

    @Override
    protected void beforeRefreshEvent() {
        
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void afterRefreshEvent() {

        if(cloudManThread != null) {
            cloudManThread.stop();
            cloudManThread = null;
        }
    }
}
