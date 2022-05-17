package graphic_Z.GRecZ.orz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class BitArrayZ implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4250253089701461803L;
    private int     bitLen;
    private int     byteBase;
    private byte [] data;
    
    public static final int upDiv(int a, int b) {
        return (a-1) / b + 1;
    }
    
    public BitArrayZ(int bitLen) {
        data = new byte[upDiv(bitLen, 8)];
        byteBase = 0;
        this.bitLen = bitLen;
    }
    
    public BitArrayZ(int bitLen, byte [] bytes, int base) throws Exception {
        if(bitLen == 0  ||  bytes == null) throw new Exception("zero memory");
        data = bytes;
        byteBase = base;
        this.bitLen = bitLen;
    }

    public BitArrayZ(int bitLen, byte [] bytes) throws Exception {
        this(bitLen, bytes, 0);
    }
    
    public final void set(int bitIndex, int x) {
        int byteIndex = bitIndex >> 3 + byteBase;
        int bitOffset = bitIndex & 7;
        
        byte mask = (byte) (1 << (7 ^ bitOffset));
        data[byteIndex] = (byte) ((data[byteIndex] & (~mask)) | (x == 0? 0: mask));
    }

    public final int get(int bitIndex) {
        int byteIndex = bitIndex >> 3 + byteBase;
        int bitOffset = bitIndex & 7;
        return data[byteIndex] & (1 << (7 ^ bitOffset));
    }
    
    /*
    public final void add(int x) {
        if((bitLen & 7) == 0) 
            data.add((byte) (x == 0? 0: 1));
        else set(bitLen - 1, x);
        ++bitLen;
    }
    */
    
    public BitArrayZ(DataInputStream stream) throws IOException {
        load(stream);
    }
    
    public final void load(DataInputStream stream) throws IOException {
        bitLen = stream.readInt();
        byteBase = 0;
        int byteLen = upDiv(bitLen, 8);
        data = new byte[byteLen];
        stream.read(data, byteBase, byteLen);
    }
    
    public final void store(DataOutputStream stream) throws IOException {
        stream.writeInt(bitLen);
        stream.write(data);
    }
}