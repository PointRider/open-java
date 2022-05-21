package graphic_Z.GRecZ;

import graphic_Z.GRecZ.orz.OrzData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameVideoRecording implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8875764655301454786L;
    private int size;
    
    private int resolutionX, resolutionY, fps;
    
    ConcurrentLinkedQueue<OrzData> frames;
    
    public GameVideoRecording(int resolutionX, int resolutionY, int fps) {
        size = 0;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.fps = fps;
        frames = new ConcurrentLinkedQueue<OrzData>();
    }
    
    public GameVideoRecording(DataInputStream stream) throws IOException {
        try {
            resolutionX = Frame.loadByte(stream.readByte());
            resolutionY = Frame.loadByte(stream.readByte());
            fps = Frame.loadByte(stream.readByte());
        } catch (IOException e) {
            throw(e);
        }
        frames = new ConcurrentLinkedQueue<OrzData>();
        size = 0;
        try {
            while(true) {
                frames.add(new OrzData(stream));
                ++size;
            }
        } catch(EOFException e) {
            System.out.println("Recording loaded.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void store(DataOutputStream stream) throws IOException {
        stream.writeByte(Frame.storeByte(resolutionX));
        stream.writeByte(Frame.storeByte(resolutionY));
        stream.writeByte(Frame.storeByte(fps));
        
        while(!frames.isEmpty()) {
            frames.poll().store(stream);
            --size;
        }
    }
    
    public final void add(Frame frame) {
        frames.add(frame.getOrz());
        synchronized(this) {++size;}
    }
    
    public final synchronized int size() {
        return size;
    }
}
