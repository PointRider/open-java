package graphic_Z.GRecZ.orz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import graphic_Z.GRecZ.datastructureZ.BitArrayZ;

public class OrzHead implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1086365314677382937L;
    int bitSize;
    int sourceByteSize;
    BitArrayZ.ByteCodeMap codeTable;
    
    public OrzHead(int bitSize, int sourceByteSize, BitArrayZ.ByteCodeMap codeTable) {
        this.bitSize        = bitSize;
        this.sourceByteSize = sourceByteSize;
        this.codeTable      = codeTable;
    }

    public OrzHead(DataInputStream stream) throws IOException {
        bitSize = stream.readInt();
        sourceByteSize = stream.readInt();
        codeTable = new BitArrayZ.ByteCodeMap(stream);
    }

    public void store(DataOutputStream stream) throws IOException {
        stream.writeInt(bitSize);
        stream.writeInt(sourceByteSize);
        codeTable.store(stream);
    }

}
