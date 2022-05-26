package graphic_Z.GRecZ;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ListIterator;

import graphic_Z.GRecZ.orz.OrzData;

public interface RecordingFile<FrameType> extends Iterable<OrzData>, Serializable {
    void reset();
    void store(DataOutputStream stream) throws IOException ;
    void add(Frame frame);
    int size();
    int getResolutionX();
    int getResolutionY();
    ListIterator<FrameType> listIterator();
    void setResolution(int x, int y);
    int getFps();
}
