package graphic_Z.GRecZ.player.controller;

import java.awt.Color;
import java.util.ListIterator;

import javax.swing.JTextArea;

import graphic_Z.GRecZ.Frame;
import graphic_Z.GRecZ.GameVideoRecording;
import graphic_Z.GRecZ.orz.OrzData;
import graphic_Z.utils.HzController;

public class PlayerController implements Runnable {
    
    private                    JTextArea mainScr;
    //private int                resolutionX, resolutionY;
    private GameVideoRecording    record;
    private boolean               paused;
    private boolean               running;
    private long                  refreshWaitNanoTime;
    private ListIterator<OrzData> itr;
    private boolean               firstFrame;
    private String                frame;

    public PlayerController(JTextArea mainScr, GameVideoRecording record) {
        this.mainScr             = mainScr;
        this.record              = record;
        //this.resolutionX         = record.getResolutionX();
        //this.resolutionY         = record.getResolutionY();
        this.refreshWaitNanoTime = HzController.nanoOfHz(record.getFps());
        paused                   = false;
        running                  = false;
        itr                      = record.listIterator();
        firstFrame               = true;
        frame                    = null;
    }

    public void reset() {
        boolean toPause = paused;
        if(paused) resume();
        itr        = record.listIterator();
        firstFrame = true;
        frame      = null;
        if(toPause) pause();
    }
    
    private final void pausePoint() {
        if(paused) try {
            synchronized(this) { wait(); }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        long now, nextRefreshTime;
        Frame newFrame = null;
        Color bg, fg;
        running = true;
        while(running) {
            firstFrame = true;
            while(running) {
                pausePoint();
                nextRefreshTime = System.nanoTime() + refreshWaitNanoTime; 
                synchronized(itr) {
                    if(itr.hasNext()) {
                        newFrame = new Frame(itr.next(), firstFrame);
                    } else break;
                }
                firstFrame = false;
                bg = new Color(newFrame.getBColorR(), newFrame.getBColorG(), newFrame.getBColorB());
                fg = new Color(newFrame.getFColorR(), newFrame.getFColorG(), newFrame.getFColorB());
                
                frame = newFrame.getFrame(frame).frame;
                
                mainScr.setBackground(bg);
                mainScr.setForeground(fg);
                mainScr.setText(frame);
                
                now = nextRefreshTime - System.nanoTime();
                if(now > 0) {
                    try {
                        Thread.sleep(now / 1000000, (int) (now % 1000000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(running) {
                pause();
                pausePoint();
                reset();
            } else System.out.println("THE END");
        }
    }

    public void pause() {
        paused = true;
    }
    
    public final boolean isPaused() {
        return paused;
    }
    
    public final void goAhead(int frames) {
        --frames;
        boolean toPause = paused;
        if(paused) resume();
        synchronized(itr) {
            for(int i = 0; i < frames && itr.hasNext(); ++i) itr.next(); 
        }
        if(toPause) pause();
    }

    public final void goAhead() {
        goAhead(8);
    }
    
    public final void goAround() {
        goAround(8);
    }
    
    public final void goAround(int frames) {
        ++frames;
        boolean toPause = paused;
        if(paused) resume();
        synchronized(itr) {
            for(int i = 0; i < frames && itr.hasPrevious(); ++i) itr.previous();
            if(!itr.hasPrevious()) reset();
        }
        if(toPause) pause();
    }
    
    public void resume() {
        paused = false;
        synchronized(this) { notifyAll(); }
    }
    
    public void finish() {
        running = false;
    }
}
