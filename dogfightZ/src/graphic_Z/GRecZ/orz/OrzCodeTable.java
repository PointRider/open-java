package graphic_Z.GRecZ.orz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class OrzCodeTable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1657639527337937333L;
    
    BitArrayZ [] code;
    
    public OrzCodeTable() {
        code = new BitArrayZ[256];
    }
    
    public OrzCodeTable(DataInputStream stream) throws IOException {
        this();
        for(int i = 0; i < 256; ++i) {
            code[i] = new BitArrayZ(stream);
        }
    }

    public final BitArrayZ getCode(byte value) {
        return code[value];
    }
    
    public final void setCode(byte value, BitArrayZ code) {
        this.code[value] = code;
    }

    public void store(DataOutputStream stream) throws IOException {
        for(BitArrayZ codeTbl : code) {
            codeTbl.store(stream);
        }
    }
}
