package graphic_Z.GRecZ;

import java.io.Serializable;

import graphic_Z.GRecZ.Recoder.FrameLoad;
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
    private FrameLoad     decodeResult;
    
    public final static int loadByte(byte x) {
        return BitArrayZ.ByteCodeMap.byteToUnsignedInt(x);
    }
    public final static byte storeByte(int x) {
        return BitArrayZ.ByteCodeMap.unsignedIntToByte(x);
    }
    
    public Frame(OrzData orzedData, boolean type) {
        this.data = orzedData.unorz();
        this.type = type;
        this.length = orzedData.getSrcByteSize();
        decodeString = null;
        decodeResult = null;
    }
    
    public Frame(
        String firstFrame,
        int br, int bg, int bb,
        int fr, int fg, int fb,
        int resolutionX, int resolutionY
    ) {
        decodeString = null;
        decodeResult = null;
        type = true;
        char now;
        data = new byte[((resolutionX * resolutionY) << 1) + 6];
        
        length = 0;

        //data[length++] = 1;
        data[length++] = storeByte(br);
        data[length++] = storeByte(bg);
        data[length++] = storeByte(bb);
        data[length++] = storeByte(fr);
        data[length++] = storeByte(fg);
        data[length++] = storeByte(fb);
        
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
        int br, int bg, int bb,
        int fr, int fg, int fb,
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
        data = new byte[((resolutionX * resolutionY) << 1) + 6];

        length = 0;
        
        //data[length++] = 0;
        data[length++] = storeByte(br);
        data[length++] = storeByte(bg);
        data[length++] = storeByte(bb);
        data[length++] = storeByte(fr);
        data[length++] = storeByte(fg);
        data[length++] = storeByte(fb);
        
        for(int i = 0, end = newFrame.length(); i < end;) {
            
            if((first = newFrame.charAt(i)) == oldFrame.charAt(i)) {
                ++i; continue;
            }
            
            data[length++] = Update.getHigherByteOfUShort((char) i);//idx
            data[length++] = Update.getLowerByteOfUShort((char) i); //idx
            
            ++i;
            /*
            "uhsaaaakdnbiasu iasxxxxxxxxxxxxxhd uias  iausdboiasudaiusbs", 
            "uhszdcakdnbiasu iasxxxxyyyyyyxxxhd uias  iausdboiasudaiusbs",
             * */
            ff = true;
            for(len = i; i < end  &&  (now = newFrame.charAt(i)) != oldFrame.charAt(i); ++i) {
                if(ff) {
                    data[length++] = 127;              //x
                    lIndex = length++;               //l - wait to update
                    data[length++] = storeByte(first); //c - 1
                    ff = false;
                }
                data[length++] = storeByte(now);    //c
            }
            if(ff) {
                data[length++] = storeByte(first);  //c
            } else {
                data[lIndex] = storeByte(i - len + 1);  //l
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
        return loadByte(data[0]);
    }
    public final int getBColorG() {
        if(data == null) return 0;
        return loadByte(data[1]);
    }
    public final int getBColorB() {
        if(data == null) return 0;
        return loadByte(data[2]);
    }
    public final int getFColorR() {
        if(data == null) return 0;
        return loadByte(data[3]);
    }
    public final int getFColorG() {
        if(data == null) return 0;
        return loadByte(data[4]);
    }
    public final int getFColorB() {
        if(data == null) return 0;
        return loadByte(data[5]);
    }
    
    public FrameLoad getFrame(String oldFrame) {
        
        if(decodeResult != null) return decodeResult;
        if(data == null) return null;
        if(decodeString == null) decodeString = new StringBuilder(); 
            else decodeString.delete(0, decodeString.length());
        
        FrameLoad result = new FrameLoad(
            null, 
            loadByte(data[0]), loadByte(data[1]), loadByte(data[2]), 
            loadByte(data[3]), loadByte(data[4]), loadByte(data[5])
        );
        
        int decodingIndex = 6, repeat;
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
            if(oldFrame == null) return null;
            decodeString.append(oldFrame);
            char idx;
            byte higher, lower;
            while(decodingIndex < length) {
                higher = data[decodingIndex++];
                lower  = data[decodingIndex++];
                idx = Update.getUShortOfByte(higher, lower); //idx
                if(data[decodingIndex] == 127) {//x
                    for(int i = 0, j = data[++decodingIndex]; i < j; ++i) {//l
                        decodeString.setCharAt(idx + i, (char) loadByte(data[++decodingIndex]));
                    }
                    ++decodingIndex;
                } else decodeString.setCharAt(idx, (char) loadByte(data[decodingIndex++]));
            }  
        }
        result.frame = decodeString.toString();
        return decodeResult = result;
    }
    
    public FrameLoad getFrame() {
        return getFrame(null);
    }
    
    public static void main(String [] args) {
        //11,s,33,d,j,m,o
        Frame f1 = new Frame(
            "uhsaaaakdnbiasu iasxxxxxxxxxxxxxhd uias  iausdboiasudaiusbs", 
            (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
            256, 256
        );
        
        Frame f2 = new Frame(
            "uhsaaaakdnbiasu iasxxxxxxxxxxxxxhd uias  iausdboiasudaiusbs", 
            "uhszdcakdnbiasu iasxxxxyyyyyyxxxhd uias  iausdboiasudaiusbs", 
            (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
            256, 256
        );
        
        System.out.println(f1.getFrame());
        System.out.println(f1.length);
        System.out.println(f1.getOrz().getOrzedByteSize());

        System.out.println(f2.getFrame("                                                           "));
        System.out.println(f2.length);
        System.out.println(f2.getOrz().getOrzedByteSize());
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
