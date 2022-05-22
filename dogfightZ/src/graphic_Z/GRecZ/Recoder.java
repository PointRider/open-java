package graphic_Z.GRecZ;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import graphic_Z.utils.HzController;

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
    private long    refreshWaitNanoTime;
    
    private int resolutionX, resolutionY, flushBufferSize;
    
    private DataOutputStream recFile;
    
    public void setOutputFile(String recFile) {
        if(!terminated) {
            System.err.println("Recoder !> Can not change video file when recording.");
            return;
        }
        try {
            File file = new File(recFile);
            file.createNewFile();
            this.recFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        } catch(FileNotFoundException e) {
            System.err.println("Recoder !> Video recording file not exists.");
        } catch(IOException e) {
            System.err.println("Recoder !> New recording file create faild.");
        }
    }
    
    public Recoder(
        ConcurrentLinkedQueue<FrameLoad> framesQueue, 
        int resolutionX, int resolutionY, int fps, 
        int storeWaitSecond
    ) {
        this.framesQueue = framesQueue;
        running = false;
        terminated = true;
        this.resolutionX    = resolutionX;
        this.resolutionY    = resolutionY;
        recordFileBuffer    = new GameVideoRecording(resolutionX, resolutionY, fps);
        flushBufferSize     = fps * storeWaitSecond;
        recFile             = null;
        refreshWaitNanoTime = HzController.nanoOfHz(fps);
    }

    @Override
    public void run() {
        if(framesQueue == null) return;
        if(recFile == null) {
            System.err.println("Recoder !> Video recording output file not set.");
            return;
        }
        terminated = false;
        running = true;
        
        boolean ff = true;
        
        String    oldFrame  = null;
        FrameLoad frameLoad = null;
        Frame     frame     = null;
        long now, nextRefreshTime;
        
        try {
            while(!terminated) {
                nextRefreshTime = System.nanoTime() + refreshWaitNanoTime; 
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
                now = nextRefreshTime - System.nanoTime();
                if(now > 0) {
                    try {
                        Thread.sleep(now / 1000000, (int) (now % 1000000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            terminated = true;
            if(recFile != null) {
                //flush buffer
                if(recordFileBuffer.size() >= flushBufferSize) {
                    try {
                        recordFileBuffer.store(recFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //close
                try {
                    recFile.close();
                    recFile = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } 
        }
    }
    
    public void pause() {
        running = false;
    }
    
    public void resume() {
        running = true;
        synchronized(this) { notifyAll(); }
    }
    
    public void finish() {
        terminated = true;
    }
}
