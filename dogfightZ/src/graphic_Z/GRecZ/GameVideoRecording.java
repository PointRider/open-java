package graphic_Z.GRecZ;

import graphic_Z.GRecZ.orz.OrzData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

public class GameVideoRecording implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8875764655301454786L;
    byte [] resolution;
    byte fps;
    LinkedList<OrzData> frames;
    
    public GameVideoRecording(byte resolutionX, byte resolutionY, byte fps) {
        resolution = new byte[] {resolutionX, resolutionY};
        this.fps = fps;
        frames = new LinkedList<>();
    }
    
    public GameVideoRecording(DataInputStream stream) throws IOException {
        stream.read(resolution);
        fps = stream.readByte();
        frames = new LinkedList<OrzData>();

        while(true) frames.addLast(new OrzData(stream));
    }
    
    public void store(DataOutputStream stream) throws IOException {
        stream.write(resolution);
        stream.writeByte(fps);
        for(OrzData frame : frames) {
            frame.store(stream);
        }
    }
}
