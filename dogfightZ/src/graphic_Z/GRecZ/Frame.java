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
    
    private static final byte xCode = 127;
    
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
        this.length  = data.length;
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
        char repeat;
        for(int i = 0, end = firstFrame.length(); i < end;) {
            now = firstFrame.charAt(i);
            repeat = 0;
            while(i < end  &&  firstFrame.charAt(i) == now) {
                ++i; ++repeat;
            }
            if(repeat > 1) {
                data[length++] = xCode;
                data[length++] = Update.getHigherByteOfUShort(repeat);
                data[length++] = Update.getLowerByteOfUShort(repeat);
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
        this(newFrame,br, bg, bb,fr, fg, fb, resolutionX, resolutionY);
    }
    
    /*
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
        
        char repeat;
        
        for(int i = 0, end = newFrame.length(); i < end;) {
            
            if((first = newFrame.charAt(i)) == oldFrame.charAt(i)) {
                ++i; continue;
            }

            data[length++] = Update.getHigherByteOfUShort((char) i);//idx
            data[length++] = Update.getLowerByteOfUShort((char) i); //idx
            
            ++i;
            
            //"uhsaaaakdnbiasu iasxxxxxxxxxxxxxhd uias  iausdboiasudaiusbs", 
            //"uhszdcakdnbiasu iasxxxxyyyyyyxxxhd uias  iausdboiasudaiusbs",
            //
            ff = true;
            for(len = i; i < end  &&  (now = newFrame.charAt(i)) != oldFrame.charAt(i); ++i) {
                if(ff) {
                    data[length++] = xCode;              //x
                    lIndex = length++;                  //l - wait to update
                    length++;
                    data[length++] = storeByte(first); //c - 1
                    ff = false;
                }
                data[length++] = storeByte(now);    //c
            }
            if(ff) {
                data[length++] = storeByte(first);  //c
            } else {
                repeat = (char) (i - len + 1);
                data[lIndex] = Update.getHigherByteOfUShort(repeat);  //l
                data[lIndex + 1] = Update.getLowerByteOfUShort(repeat);  //l
            }
        }
    }*/
    /*
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
    }*/
    
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
        /*if(type) { //first frame*/
            byte higher, lower;
            while(decodingIndex < length) {
                c = (char) loadByte(data[decodingIndex++]);
                if(c == xCode) {//x
                    higher = data[decodingIndex++];
                    lower  = data[decodingIndex++];
                    repeat = Update.getUShortOfByte(higher, lower);//n
                    c = (char) loadByte(data[decodingIndex++]);//c
                    while(repeat --> 0) decodeString.append(c);
                } else decodeString.append(c);
            }  
        /*} else {
            if(oldFrame == null) return null;
            decodeString.append(oldFrame);
            char idx;
            byte higher, lower;
            while(decodingIndex < length) {
                higher = data[decodingIndex++];//1,2
                lower  = data[decodingIndex++];//0,1
                idx = Update.getUShortOfByte(higher, lower); //idx
                if(loadByte(data[decodingIndex]) == xCode) {//x //2,2
                    higher = data[++decodingIndex];//l 3,3
                    lower  = data[++decodingIndex];//l 4,4
                    for(int i = 0, j = Update.getUShortOfByte(higher, lower); i < j; ++i) {//l 4,4
                        decodeString.setCharAt(idx + i, (char) loadByte(data[++decodingIndex]));//5,5
                    }
                    ++decodingIndex;//5,5
                } else {
                    decodeString.setCharAt(idx, (char) loadByte(data[decodingIndex++]));//2,3
                }
            }  
        }*/
        result.frame = decodeString.toString();
        return decodeResult = result;
    }
    
    public FrameLoad getFrame() {
        return getFrame(null);
    }
    
    public static void main(String [] args) {
        //11,s,33,d,j,m,o
        /*Frame f1 = new Frame(
            "uhsaaaakdnbiasu iasxxxxxxxxxx xxhd uias  iausdboiasudaiusbs", 
            (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
            256, 256
        );*/
        
String tf1 = "    aaakdnbiasu iasxxxxxxxxxx xxhd uias  iausdboiasud uukukukggggggggss";
String tf2 = " sss        x xxhd uias  sd                  dboiasudefwef   wgggggggss";
String tf3 = "    xzc     x x    uias  sd                   cf         f   wggg gggss";
        
        Frame f1 = new Frame(
            tf1, 
            (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
            256, 256
        );
        System.out.println(f1.getFrame().frame);
        Frame f2 = new Frame(
            tf1, tf2,
            (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
            256, 256
        );
        System.out.println(tf1 = f2.getFrame(tf1).frame);
        Frame f3 = new Frame(
            tf1, tf3,
            (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
            256, 256
        );
        System.out.println(tf1 = f3.getFrame(tf1).frame);
    }
    /*
    public byte[] getFrameData() {
        return data;
    }*/
    
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
