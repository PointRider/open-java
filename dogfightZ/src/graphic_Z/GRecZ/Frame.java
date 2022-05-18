package graphic_Z.GRecZ;

import java.io.Serializable;
import java.util.ArrayList;

import graphic_Z.GRecZ.orz.OrzData;

public class Frame implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5924611790139759528L;
    byte [] bgColor;
    byte [] fontColor;
    ArrayList<Pixel>  pixels;
    ArrayList<Update> updates;
    
    public Frame(OrzData orzedData) {
        //TODO
    }
    
    public Frame(
        String firstFrame, 
        byte br, byte bg, byte bb,
        byte fr, byte fg, byte fb
    ) {
        bgColor   = new byte [] {br, bg, bb};
        fontColor = new byte [] {fr, fg, fb};
        pixels    = new ArrayList<Pixel>();
        updates   = null;
        
        //TODO
    }
    
    public Frame(
        String oldFrame, String newFrame, 
        byte br, byte bg, byte bb,
        byte fr, byte fg, byte fb
    ) {
        bgColor   = new byte [] {br, bg, bb};
        fontColor = new byte [] {fr, fg, fb};
        pixels    = null;
        updates   = new ArrayList<Update>();
        
        //TODO
    }
    
    public void addPixel(Pixel p) throws Exception {
        if(pixels == null) throw new Exception("not first frame.");
        pixels.add(p);
    }
    
    public void addUpdate(Update u) throws Exception {
        if(updates == null) throw new Exception("first frame.");
        updates.add(u);
    }
    
    public String getFrame() {
        return null;
    }
    
    public OrzData getOrz() {
        return null;
    }
}
