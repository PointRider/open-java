package graphic_Z.GRecZ.player.controller;

import javax.swing.JTextArea;

import graphic_Z.GRecZ.GameVideoRecording;
import graphic_Z.utils.HzController;

public class PlayerController implements Runnable {
    
    public          JTextArea mainScr;
    public          int resolutionX, resolutionY, fps;
    public          GameVideoRecording record;
    private boolean paused;
    private boolean running;
    private long    refreshWaitNanoTime;

    public PlayerController(JTextArea mainScr, GameVideoRecording record) {
        this.mainScr = mainScr;
        this.resolutionX = record.getResolutionX();
        this.resolutionY = record.getResolutionY();
        this.refreshWaitNanoTime = HzController.nanoOfHz(record.getFps());
        paused  = false;
        running = false;
    }

    @Override
    public void run() {
        long now, nextRefreshTime;
        
        while(running) {
            if(paused) try {
                synchronized(this) { wait(); }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            nextRefreshTime = System.nanoTime() + refreshWaitNanoTime; 
            
            
            now = nextRefreshTime - System.nanoTime();
            if(now > 0) {
                try {
                    Thread.sleep(now / 1000000, (int) (now % 1000000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
