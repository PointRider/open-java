package graphic_Z.GRecZ.orz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class OrzData implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8844720456758379734L;
    OrzHead head;
    ArrayList<Byte> data;
    
    public OrzData(DataInputStream stream) throws IOException {
        head = new OrzHead(stream);
        int byteLen = BitArrayZ.upDiv(head.bitSize, 8);
        data = new ArrayList<Byte>(byteLen);
        for(int i = 0; i < byteLen; ++i) {
            data.add(stream.readByte());
        }
    }
    
    public void store(DataOutputStream stream) throws IOException {
        head.store(stream);
        for(Byte b : data) {
            stream.writeByte(b);
        }
    }
    
    public OrzData(byte [] bytes) {
        this(bytes, 0, bytes.length);
    }
    
    public OrzData(byte [] bytes, int start, int len) {
        //...
        head = new OrzHead(0/**/, len, null/**/);
        //...
    }
    
    public byte[] unorz() {
        //...
        return null;
    }
}
