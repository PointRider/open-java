package graphic_Z.GRecZ.orz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import graphic_Z.GRecZ.datastructureZ.BitArrayZ;
import graphic_Z.GRecZ.datastructureZ.BitArrayZ.ByteCodeMap;
import graphic_Z.GRecZ.datastructureZ.binaryTree.HuffmanTree;

public class OrzData implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8844720456758379734L;
    OrzHead head;
    byte [] data;
    
    public OrzData(DataInputStream stream) throws IOException {
        head = new OrzHead(stream);
        int byteLen = BitArrayZ.upDiv(head.bitSize, 8);
        data = new byte[byteLen];
        stream.read(data);
    }
    
    public void store(DataOutputStream stream) throws IOException {
        head.store(stream);
        stream.write(data);
    }
    
    public HuffmanTree<Integer> countByte(byte [] bytes, int start, int len) {
        //HuffmanTree.newNode(weight, e)
        int i;
        int [] counts = new int[256];
        
        for(i = 0; i < 256; ++i) counts[i] = 0;
        
        for(i = 0; i < len; ++i) {
            ++counts[ByteCodeMap.byteToUnsignedInt(bytes[start + i])];
        }

        HuffmanTree.Builder<Integer> builder = new HuffmanTree.Builder<Integer>();
        for(i = 0; i < 256; ++i) {
            builder.insert(counts[i],  i);
        }
        return builder.build();
    }
    /*
    //only for test
    private OrzData() {
        head = null;
        data = null;
    }*/
    //同下
    public OrzData(byte [] bytes) {
        this(bytes, 0, bytes.length);
    }
    //压缩一段byte数组到data中
    public OrzData(byte [] bytes, int start, int len) {
        HuffmanTree<Integer> encoder = countByte(bytes, start, len);
        if(encoder == null) throw new NullPointerException("Huffman encoding failed.");
        
        BitArrayZ.ByteCodeMap codeTable = new BitArrayZ.ByteCodeMap();
        encoder.encode(codeTable);
        
        byte [] orzed = new byte[len];
        BitArrayZ orz = new BitArrayZ(len << 3, orzed, 0); // x 8
        
        int orzBitIndex = 0, bitIndexOfCode, bitLenOfCode;
        BitArrayZ codeOfByte = null;
        for(byte B : bytes) {
            codeOfByte = codeTable.get(ByteCodeMap.byteToUnsignedInt(B));
            
            for(
                bitIndexOfCode = 0, bitLenOfCode = codeOfByte.getBitLen(); 
                bitIndexOfCode < bitLenOfCode; 
                ++bitIndexOfCode
            ) {
                orz.set(orzBitIndex++, codeOfByte.get(bitIndexOfCode));
            }
        }
        
        head = new OrzHead(orzBitIndex, len, codeTable);
        data = orzed;
    }
    
    public byte[] getOrzed() {
        return data;
    }
    
    public byte[] unorz() {
        if(head == null || data == null) return null;
        int bitLen = head.bitSize;
        byte []   unorzed = new byte[head.sourceByteSize];
        BitArrayZ orz     = new BitArrayZ(bitLen, data, 0);
        
        HuffmanTree<Integer> decoder = new HuffmanTree<Integer>(head.codeTable);
        HuffmanTree.MatchTable<Integer> matchTable = null;
        for(int bitIndex = 0, byteIndex = 0; bitIndex < bitLen;) {
           matchTable = decoder.match(orz, bitIndex);
           if(matchTable == null) return null;
           unorzed[byteIndex++] = ByteCodeMap.unsignedIntToByte(matchTable.result);
           bitIndex += matchTable.length;
        }
        
        return unorzed;
    }
    
    public static void main(String args[]) {
        byte [] testArr1 = new byte[256];
        
        for(int i = 0; i < 256; ++i) {
            testArr1[i] = (byte) (i & 3);
        }
        
        OrzData orz = new OrzData(testArr1);
        System.out.println("orzed:  " + orz.head.bitSize + "bit(" + BitArrayZ.upDiv(orz.head.bitSize, 8) + "bytes)");
        System.out.println("source: " + orz.head.sourceByteSize + "bytes, rate: " + (float) BitArrayZ.upDiv(orz.head.bitSize, 8) / (float) orz.head.sourceByteSize);
        
        for(byte b : orz.unorz()) {
            System.out.print(b + ",");
        }
    }
}
