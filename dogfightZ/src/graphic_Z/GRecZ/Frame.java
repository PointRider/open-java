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
    
    public final static int loadByte(byte x) {
        return BitArrayZ.ByteCodeMap.byteToUnsignedInt(x);
    }
    public final static byte storeByte(int x) {
        return BitArrayZ.ByteCodeMap.unsignedIntToByte(x);
    }
    
    public Frame(OrzData orzedData) {
        this.data = orzedData.unorz();
        this.type = (data[0] == 1);
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
        data = new byte[((resolutionX * resolutionY) << 1) + 7];
        
        length = 0;

        data[length++] = 1;
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
            if(repeat > 1) {
                data[length++] = 127;
                data[length++] = storeByte(repeat);
            }
            data[length++] = storeByte(now);
        }
    }
    
    public Frame(
        String oldFrame, String newFrame,
        byte br, byte bg, byte bb,
        byte fr, byte fg, byte fb,
        int resolutionX, int resolutionY
    ) {
        if(oldFrame.length() != newFrame.length()) throw new java.lang.ArithmeticException("Frame length is not equal.");
        
        decodeString = null;
        decodeResult = null;
        type = false;
        char first, now;
        boolean ff = false;
        int lIndex = 0;
        int len;
        data = new byte[((resolutionX * resolutionY) << 1) + 7];

        length = 0;
        
        data[length++] = 0;
        data[length++] = br;
        data[length++] = bg;
        data[length++] = bb;
        data[length++] = fr;
        data[length++] = fg;
        data[length++] = fb;
        
        for(int i = 0, end = newFrame.length(); i < end;) {
            
            if((first = newFrame.charAt(i)) == oldFrame.charAt(i)) {
                ++i; continue;
            }
            
            data[length++] = Update.getHigherByteOfUShort((char) i);//idx
            data[length++] = Update.getLowerByteOfUShort((char) i); //idx
            
            ++i;
            
            ff = true;
            for(len = i; len < end  &&  (now = newFrame.charAt(len)) != oldFrame.charAt(len); ++len) {
                if(ff) {
                    data[length++] = 127;              //x
                    lIndex = (length++);               //l - wait to update
                    data[length++] = storeByte(first); //c - 1
                    ff = false;
                }
                
                data[length++] = storeByte(now);    //c
            }
            if(ff) {
                data[length++] = storeByte(first);  //c
            } else {
                data[lIndex] = storeByte(len + 1 - i);  //l
            }
        }
    }
    
    public void addPixel(Pixel p) throws Exception {
        if(!type  ||  length >= data.length) return;
        for(int i = 0; i < p.repeat  &&  length < data.length; ++i) {
            data[length++] = p.pixel;
        }
        decodeResult = null;
    }
    
    public void addUpdate(Update u) throws Exception {
        if(type  ||  length >= data.length) return;
        
        decodeResult = null;
    }
    
    public final int getBColorR() {
        if(data == null) return 0;
        return loadByte(data[1]);
    }
    public final int getBColorG() {
        if(data == null) return 0;
        return loadByte(data[2]);
    }
    public final int getBColorB() {
        if(data == null) return 0;
        return loadByte(data[3]);
    }
    public final int getFColorR() {
        if(data == null) return 0;
        return loadByte(data[4]);
    }
    public final int getFColorG() {
        if(data == null) return 0;
        return loadByte(data[5]);
    }
    public final int getFColorB() {
        if(data == null) return 0;
        return loadByte(data[6]);
    }
    
    public String getFrame() {
        
        if(decodeResult != null) return decodeResult;
        if(data == null) return null;
        if(decodeString == null) decodeString = new StringBuilder(); 
            else decodeString.delete(0, decodeString.length());
        
        int decodingIndex = 7, repeat;
        char c;
        
        if(type) { //first frame
            while(decodingIndex < length) {
                c = (char) loadByte(data[decodingIndex++]);
                if(c == 127) {
                    repeat = loadByte(data[decodingIndex++]);
                    c = (char) loadByte(data[decodingIndex++]);
                    while(repeat --> 0) decodeString.append(c);
                } else decodeString.append(c);
            }  
        } else {
            while(decodingIndex < length) {
                //TODO
            }  
        }
        
        return decodeResult = decodeString.toString();
    }
    
    public static void main(String [] args) {
        //11,s,33,d,j,m,o
        Frame f = new Frame("uhsaaaakdnbiasu iasxxxxxxxxxxxxxhd uias  iausdboiasudaiusbs", 
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
