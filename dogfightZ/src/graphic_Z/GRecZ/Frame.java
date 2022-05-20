package graphic_Z.GRecZ;

import java.io.Serializable;
import graphic_Z.GRecZ.datastructureZ.BitArrayZ;
import graphic_Z.GRecZ.orz.OrzData;

public class Frame implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5924611790139759528L;
    
    private byte [] data;
    private boolean type;
    private int     length;
    
    private StringBuilder decodeString;
    private String        decodeResult;
    
    public Frame(OrzData orzedData, boolean type) {
        this.data = orzedData.unorz();
        this.type = type;
        this.length = orzedData.getSrcByteSize();
        decodeString = null;
        decodeResult = null;
    }
    
    public Frame(
        String firstFrame,
        byte br, byte bg, byte bb,
        byte fr, byte fg, byte fb,
        int resolutionX, int resolutionY
    ) {
        decodeString = null;
        decodeResult = null;
        type = true;
        char now;
        data = new byte[((resolutionX * resolutionY) << 1) + 6];
        
        length = 0;
        
        data[length++] = br;
        data[length++] = bg;
        data[length++] = bb;
        data[length++] = fr;
        data[length++] = fg;
        data[length++] = fb;
        
        for(int i = 0, end = firstFrame.length(), repeat; i < end;) {
            now = firstFrame.charAt(i);
            repeat = 0;
            while(i < end  &&  firstFrame.charAt(i) == now) {
                ++i; ++repeat;
            }
            data[length++] = (byte) BitArrayZ.ByteCodeMap.unsignedIntToByte(repeat);
            data[length++] = (byte) BitArrayZ.ByteCodeMap.unsignedIntToByte(now);
        }
    }
    
    public void addPixel(Pixel p) throws Exception {
        decodeResult = null;
    }
    
    public void addUpdate(Update u) throws Exception {
        decodeResult = null;
    }
    
    public final byte getBColorR() {
        if(data == null) return 0;
        return data[0];
    }
    public final byte getBColorG() {
        if(data == null) return 0;
        return data[1];
    }
    public final byte getBColorB() {
        if(data == null) return 0;
        return data[2];
    }
    public final byte getFColorR() {
        if(data == null) return 0;
        return data[3];
    }
    public final byte getFColorG() {
        if(data == null) return 0;
        return data[4];
    }
    public final byte getFColorB() {
        if(data == null) return 0;
        return data[5];
    }
    public String getFrame() {
        
        if(decodeResult != null) return decodeResult;
        if(data == null) return null;
        if(decodeString == null) decodeString = new StringBuilder(); 
            else decodeString.delete(0, decodeString.length());
        
        int decodingIndex = 6, repeat;
        char c;
        
        if(type) { //first frame
            while(decodingIndex < length) {
                repeat = BitArrayZ.ByteCodeMap.byteToUnsignedInt(data[decodingIndex++]);
                c = (char) BitArrayZ.ByteCodeMap.byteToUnsignedInt(data[decodingIndex++]);
                while(repeat --> 0) decodeString.append(c);
            }  
        } else {
            while(decodingIndex < length) {
                //TODO
            }  
        }
        return decodeString.toString();
    }
    
    public static void main(String [] args) {
        //11,s,33,d,j,m,o
        
        Frame f = new Frame("sssssssssssdddddddddddddddddddddddddddddddddjmmmoadawdawdui", 
            (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
            256, 256
        );
        
        System.out.println(f.getFrame());
        System.out.println(f.length);
        System.out.println(f.getOrz().getOrzedByteSize());
        
    }
    
    public byte[] getFrameData() {
        return data;
    }
    
    public OrzData getOrz() {
        return new OrzData(data, 0, length);
    }
    
    /**
     * true: first frame
     * */
    public final boolean getType() {
        return type;
    }

    public final int getLength() {
        return length;
    }
}
