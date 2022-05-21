package graphic_Z.GRecZ;

import java.io.Serializable;

public class Update implements Serializable {

    /**
     * 
     *//*
    private static final long serialVersionUID = 9042029515299889702L;
    short index;
    byte pixel;
    
    public Update(short index, byte pixel) {
        this.index = index;
        this.pixel = pixel;
    }
    
    public static byte getHigherByteOfShort(short x) {
        return (byte) (x >> 8);
    }
    
    public static byte getLowerByteOfShort(short x) {
        return Frame.storeByte(x & 0xFF);
    }
    
    public static short getShortOfByte(byte higher, byte lower) {
        return (short) ((higher << 8) | Frame.loadByte(lower));
    }
    
    public static void main(String [] args) {
        short x = 32767;
        System.out.println(x);
        System.out.println(getHigherByteOfShort(x) + "," + getLowerByteOfShort(x));
        System.out.println(getShortOfByte(getHigherByteOfShort(x), getLowerByteOfShort(x)));
    }
    */
    // 10011010 11010010
    // 00000000 11111111
    
    private static final long serialVersionUID = 9042029515299889702L;
    char index;
    byte pixel;
    
    public Update(char index, byte pixel) {
        this.index = index;
        this.pixel = pixel;
    }
    
    public static byte getHigherByteOfUShort(char x) {
        return (byte) (x >> 8);
    }
    
    public static byte getLowerByteOfUShort(char x) {
        return (byte) Frame.storeByte(x & 0xFF);
    }
    
    public static char getUShortOfByte(byte higher, byte lower) {
        return (char) ((higher << 8) | Frame.loadByte(lower));
    }
    
    public static void main(String [] args) {
        char x = 65535;
        System.out.println((int) x);
        System.out.println(getHigherByteOfUShort(x) + "," + getLowerByteOfUShort(x));
        System.out.println((int) getUShortOfByte((byte)1, (byte)0));
    }
}
