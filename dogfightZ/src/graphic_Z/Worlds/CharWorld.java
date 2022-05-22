package graphic_Z.Worlds;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentLinkedQueue;

import graphic_Z.GRecZ.Recoder;
import graphic_Z.GRecZ.Recoder.FrameLoad;
import graphic_Z.HUDs.CharHUD;
import graphic_Z.Managers.CharObjectsManager;
import graphic_Z.Managers.CharVisualManager;
import graphic_Z.Managers.EventManager;
import graphic_Z.Objects.CharObject;

public class CharWorld extends TDWorld<CharWorld, CharObject, CharHUD>
{
	public CharObjectsManager objectsManager;
	public CharVisualManager  visualManager;

    private     Recoder                          recoder;
    private     ConcurrentLinkedQueue<FrameLoad> recodingQueue;
    private     boolean                          recoding;
	
	public static final int DEFAULTMOTIONBLURLEVEL = 8;
	
	public CharWorld(int resolution_X, int resolution_Y) {
		super(64);				//default
		eventManager   = new EventManager();
		objectsManager = new CharObjectsManager(/*this*/);
		visualManager  = new CharVisualManager(resolution_X, resolution_Y, this, eventManager.mainScr);
	}
	
    public CharWorld(int resolution_X, int resolution_Y, int refresh_rate) {
        this(resolution_X, resolution_Y, refresh_rate, false);
    }
    
    private void initRecoder(int resolutionX, int resolutionY, int fps) throws FileNotFoundException {
        recodingQueue = new ConcurrentLinkedQueue<>();
        recoder       = new Recoder(recodingQueue, resolutionX, resolutionY, fps, 4);
        recoding      = false;
    }
	
	public CharWorld(int resolution_X, int resolution_Y, int refresh_rate, boolean useZBuffer) {
		super(refresh_rate);
		eventManager   = new EventManager();
		objectsManager = new CharObjectsManager(/*this*/);
		visualManager  = new CharVisualManager(resolution_X, resolution_Y, this, eventManager.mainScr, useZBuffer, DEFAULTMOTIONBLURLEVEL);

        try {
            initRecoder(resolution_X, resolution_Y, refresh_rate);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	public void setRefreshRate(int refresh_rate) {
		refreshHz = 1000 / refresh_rate;
		visualManager.refreshHz = refresh_rate;
	}
	
	public void printNew() {
		if(recoder == null  ||  !recoding) visualManager.printNew();
		else {
		    Color bg = eventManager.mainScr.getBackground(), fg = eventManager.mainScr.getForeground();
		    recodingQueue.add(new FrameLoad(
                visualManager.printNew(), 
                bg.getRed(), bg.getGreen(), bg.getBlue(), 
                fg.getRed(), fg.getGreen(), fg.getBlue()
            ));
		}
	}
	
	public void recordingPause() {
        recoder.pause();
        recoding = false;
	}
	
	public void recordingResume() {
        recoding = true;
	    recoder.resume();
	}
	
	public void recordingPauseOrResume() {
        recoding = !recoding;
        if(recoding) {
            recoder.resume();
        } else recoder.pause();
    }
	
	public void startRecording(String recFile) {
	    recoding = true;
	    recoder.setOutputFile(recFile);
	    execute(recoder);
	}
	
	public void finishRecording() {
	    recoder.finish();
	}
	
}
