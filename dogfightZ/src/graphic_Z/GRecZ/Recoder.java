package graphic_Z.GRecZ;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Recoder implements Runnable {

    public static class FrameLoad {
        public String frame;
        public int br, bg, bb, fr, fg, fb;
        
        public FrameLoad(String frame, int br, int bg, int bb, int fr, int fg, int fb) {
            this.frame = frame;
            this.br = br;
            this.bg = bg;
            this.bb = bb;
            this.fr = fr;
            this.fg = fg;
            this.fb = fb;
        }
    }
    
    private ConcurrentLinkedQueue<FrameLoad> framesQueue;
    private GameVideoRecording recordFileBuffer;
    private boolean running;
    private boolean terminated;
    
    private int resolutionX, resolutionY, flushBufferSize;
    
    private DataOutputStream recFile;
    
    public Recoder(
        ConcurrentLinkedQueue<FrameLoad> framesQueue, 
        int resolutionX, int resolutionY, int fps, 
        int storeWaitSecond, DataOutputStream recFile
    ) {
        this.framesQueue = framesQueue;
        running = false;
        terminated = false;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        recordFileBuffer = new GameVideoRecording(resolutionX, resolutionY, fps);
        flushBufferSize  = fps * storeWaitSecond;
        this.recFile     = recFile;
    }

    @Override
    public void run() {
        if(framesQueue == null) return;
        terminated = false;
        running = true;
        
        boolean ff = true;
        
        String    oldFrame  = null;
        FrameLoad frameLoad = null;
        Frame     frame     = null;
        
        try {
            while(!terminated) {
                if(!running) try {
                    synchronized(this) { wait(); }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                if(!framesQueue.isEmpty()) {
                    frameLoad = framesQueue.poll();
                    
                    if(ff) {
                        frame = new Frame(
                            frameLoad.frame, 
                            frameLoad.br, frameLoad.bg, frameLoad.bb, 
                            frameLoad.fr, frameLoad.fg, frameLoad.fb,
                            resolutionX, resolutionY
                        );
                        ff = false;
                    } else {
                        frame = new Frame(
                            frameLoad.frame, oldFrame,
                            frameLoad.br, frameLoad.bg, frameLoad.bb, 
                            frameLoad.fr, frameLoad.fg, frameLoad.fb,
                            resolutionX, resolutionY
                        );
                    }
                    recordFileBuffer.add(frame);
                    if(recordFileBuffer.size() >= flushBufferSize) recordFileBuffer.store(recFile);
                    oldFrame = frameLoad.frame;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void pause() {
        running = false;
    }
    
    public void resume() {
        running = true;
        synchronized(this) { notifyAll(); }
    }
    
    public void terminate() {
        terminated = true;
    }
}
