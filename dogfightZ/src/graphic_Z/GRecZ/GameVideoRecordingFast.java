package graphic_Z.GRecZ;

import graphic_Z.GRecZ.Recoder.FrameLoad;
import graphic_Z.GRecZ.orz.OrzData;
import graphic_Z.utils.LinkedListZ;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameVideoRecordingFast implements RecordingFile<FrameLoad> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8875764655301454786L;
    private int size;
    private boolean headNotStoredFF;
    
    private boolean readOnly;
    
    private int resolutionX, resolutionY, fps;
    
    ConcurrentLinkedQueue<OrzData> frames;
    LinkedListZ<FrameLoad> readOnlyFrames;
    
    public GameVideoRecordingFast(int resolutionX, int resolutionY, int fps) {
        size = 0;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.fps = fps;
        frames = new ConcurrentLinkedQueue<OrzData>();
        headNotStoredFF = true;
        readOnly = false;
    }
    
    public void reset() {
        headNotStoredFF = true;
    }
    
    public GameVideoRecordingFast(DataInputStream stream) throws IOException {
        headNotStoredFF = true;
        readOnly = true;
        boolean firstFrame = true;
        Frame f = null;
        String oldFrame = null;
        try {
            resolutionX = Frame.loadByte(stream.readByte());
            resolutionY = Frame.loadByte(stream.readByte());
            fps         = Frame.loadByte(stream.readByte());
        } catch (IOException e) {
            throw(e);
        }
        readOnlyFrames = new LinkedListZ<FrameLoad>();
        size = 0;
        try {
            while(true) {
                f = new Frame(new OrzData(stream), firstFrame);
                readOnlyFrames.add(f.getFrame(oldFrame));
                if(firstFrame) firstFrame = false;
                ++size;
            }
        } catch(EOFException e) {
            System.out.println("Recording loaded.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void store(DataOutputStream stream) throws IOException {
        if(readOnly) return;
        
        if(headNotStoredFF) {
            stream.writeByte(Frame.storeByte(resolutionX));
            stream.writeByte(Frame.storeByte(resolutionY));
            stream.writeByte(Frame.storeByte(fps));
            headNotStoredFF = false;
        }
        
        while(!frames.isEmpty()) {
            frames.poll().store(stream);
            --size;
        }
    }
    
    public final void add(Frame frame) {
        if(readOnly) return;
        frames.add(frame.getOrz());
        synchronized(this) {++size;}
    }
    
    public final synchronized int size() {
        return size;
    }

    public final int getResolutionX() {
        return resolutionX;
    }

    public final int getResolutionY() {
        return resolutionY;
    }
    
    public Iterator<OrzData> iterator() {
        if(!readOnly) return frames.iterator();
        return null;
    }
    
    public ListIterator<FrameLoad> listIterator() {
        if(readOnly) return readOnlyFrames.listIterator();
        return null;
    }
    
    public final void setResolution(int x, int y) {
        resolutionX = x;
        resolutionY = y;
    }

    public final int getFps() {
        return fps;
    }
}
