package graphic_Z.GRecZ.player.controller;

import java.awt.Color;
import java.util.Iterator;

import javax.swing.JTextArea;

import graphic_Z.GRecZ.Frame;
import graphic_Z.GRecZ.GameVideoRecording;
import graphic_Z.GRecZ.orz.OrzData;
import graphic_Z.utils.HzController;

public class PlayerController implements Runnable {
    
    private                    JTextArea mainScr;
    //private int                resolutionX, resolutionY;
    private GameVideoRecording record;
    private boolean            paused;
    private boolean            running;
    private long               refreshWaitNanoTime;
    private Iterator<OrzData>  itr;
    private boolean            firstFrame;
    private String             frame;

    public PlayerController(JTextArea mainScr, GameVideoRecording record) {
        this.mainScr             = mainScr;
        this.record              = record;
        //this.resolutionX         = record.getResolutionX();
        //this.resolutionY         = record.getResolutionY();
        this.refreshWaitNanoTime = HzController.nanoOfHz(record.getFps());
        paused                   = false;
        running                  = false;
        itr                      = record.iterator();
        firstFrame               = true;
        frame                    = null;
    }

    public void reset() {
        itr        = record.iterator();
        firstFrame = true;
        frame      = null;
    }
    
    @Override
    public void run() {
        long now, nextRefreshTime;
        Frame newFrame;
        Color bg, fg;
        running = true;
        
        while(running && itr.hasNext()) {
            if(paused) try {
                synchronized(this) { wait(); }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            nextRefreshTime = System.nanoTime() + refreshWaitNanoTime; 
            
            newFrame = new Frame(itr.next(), firstFrame);
            firstFrame = false;
            bg = new Color(newFrame.getBColorR(), newFrame.getBColorG(), newFrame.getBColorB());
            fg = new Color(newFrame.getFColorR(), newFrame.getFColorG(), newFrame.getFColorB());
            
            frame = newFrame.getFrame(frame).frame;
            
            mainScr.setBackground(bg);
            mainScr.setForeground(fg);
            mainScr.setText(frame);
            
            //System.err.println(frame);
            
            now = nextRefreshTime - System.nanoTime();
            if(now > 0) {
                try {
                    Thread.sleep(now / 1000000, (int) (now % 1000000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        running = false;
        System.out.println("THE END");
    }

    public void pause() {
        paused = true;
    }
    
    public void resume() {
        paused = false;
        synchronized(this) { notifyAll(); }
    }
    
    public void finish() {
        running = false;
    }
}
