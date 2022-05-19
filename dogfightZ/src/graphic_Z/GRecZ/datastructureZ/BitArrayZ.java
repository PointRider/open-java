package graphic_Z.GRecZ.datastructureZ;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BitArrayZ implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4250253089701461803L;
    private int     bitLen;
    private int     byteBase;
    private byte [] data;
    private StringBuilder toStringBuilder;
    
    public static class ByteCodeMap implements Map<Integer, BitArrayZ>, Serializable {
        
        /**
         * 
         */
        private static final long     serialVersionUID = -4064645930696370065L;
        private int                   nowSize;
        private BitArrayZ []          code;
        private ArrayListSet<Integer> keys;
        private StringBuilder         toStringBuilder;
        
        public ByteCodeMap() {
            code = new BitArrayZ[256];
            nowSize = 0;
            toStringBuilder = null;
            keys = null;
        }
        
        public ByteCodeMap(BitArrayZ [] codeTable) {
            code    = codeTable;
            nowSize = 0;
            for(int i = 0; i < 256; ++i) {
                if(code[i] != null) ++nowSize;
            }
            toStringBuilder = null;
            keys = null;
        }
        
        public ByteCodeMap(DataInputStream stream) throws IOException {
            this();
            for(int i = 0; i < 256; ++i) {
                code[i] = new BitArrayZ(stream);
            }
            toStringBuilder = null;
            keys = null;
        }
        
        public void store(DataOutputStream stream) throws IOException {
            for(BitArrayZ codeTbl : code) {
                codeTbl.store(stream);
            }
        }

        public static final int byteToUnsignedInt(byte x) {
            if(x >= 0) return x;
            else {
                return 256 + x;
            }
        }
        
        public static final byte unsignedIntToByte(int x) {
            if(x < 127) return (byte) x;
            else {
                return (byte) (x-256);
            }
        }
        
        @Override
        public int size() {
            return nowSize;
        }

        @Override
        public boolean isEmpty() {
            return nowSize == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            return code[(int) key] != null;
        }

        @Override
        public boolean containsValue(Object value) {
            throw new java.lang.UnsupportedOperationException("Unsupported Operation");
        }

        @Override
        public BitArrayZ get(Object key) {
            return code[(int) key];
        }

        @Override
        public BitArrayZ put(Integer key, BitArrayZ value) {
            BitArrayZ previous = this.code[key];
            this.code[key] = value;
            ++nowSize;
            if(keys != null) keys.add(key);
            return previous;
        }

        @Override
        public BitArrayZ remove(Object key) {
            BitArrayZ previous = this.code[(int) key];
            this.code[(int) key] = null;
            --nowSize;
            keys = null;
            return previous;
        }

        @Override
        public void clear() {
            for(int i = 0; i < 256; ++i) {
                code[i] = null;
            }
            keys = null;
            nowSize = 0;
        }
        
        @Override
        public String toString() {
            if(toStringBuilder == null) toStringBuilder = new StringBuilder();
                else toStringBuilder.delete(0, toStringBuilder.length());
            for(int i = 0; i < 256; ++i) {
                if(code[i] == null) {
                    toStringBuilder.append("\n\t\t" + i + ":\t<null>");
                } else {
                    toStringBuilder.append("\n\t\t" + i + ":\t" + code[i].toString());
                }
            }
            return "ByteCodeMap:\n\tnowSize: " + nowSize + "\n\tcode: " + toStringBuilder.toString() + '\n';
        }

        @Override
        public void putAll(Map<? extends Integer, ? extends BitArrayZ> m) {
            throw new java.lang.UnsupportedOperationException("Unsupported Operation");
        }
        
        public static class ArrayListSet<T> implements Set<T> {

            private ArrayList<T> list;
            public ArrayListSet() {
                list = new ArrayList<T>();
            }
            public ArrayListSet(int initCapacity) {
                list = new ArrayList<T>(initCapacity);
            }
            
            @Override
            public int size() {
                return list.size();
            }

            @Override
            public boolean isEmpty() {
                return list.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                throw new java.lang.UnsupportedOperationException("Unsupported Operation");
            }

            @Override
            public Iterator<T> iterator() {
                return list.iterator();
            }

            @Override
            public Object[] toArray() {
                return list.toArray();
            }

            @Override
            public <E> E[] toArray(E[] a) {
                return list.toArray(a);
            }

            @Override
            public boolean add(T e) {
                return list.add(e);
            }

            @Override
            public boolean remove(Object o) {
                return list.remove(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new java.lang.UnsupportedOperationException("Unsupported Operation");
            }

            @Override
            public boolean addAll(Collection<? extends T> c) {
                return list.addAll(c);
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return list.retainAll(c);
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return list.removeAll(c);
            }

            @Override
            public void clear() {
                list.clear();
            }
        }
        
        @Override
        public Set<Integer> keySet() {
            if(keys != null) return keys;
            keys = new ArrayListSet<Integer>();
            
            for(int i = 0; i < 256; ++i) {
                if(code[i] != null) keys.add(i);
            }
            
            return keys;
        }

        @Override
        public Collection<BitArrayZ> values() {
            throw new java.lang.UnsupportedOperationException("Unsupported Operation");
        }

        @Override
        public Set<Entry<Integer, BitArrayZ>> entrySet() {
            throw new java.lang.UnsupportedOperationException("Unsupported Operation");
        }
    }
    
    public static final int upDiv(int a, int b) {
        return (a-1) / b + 1;
    }
    
    public BitArrayZ(int bitLen) {
        data = new byte[upDiv(bitLen, 8)];
        byteBase = 0;
        this.bitLen = bitLen;
        toStringBuilder = null;
    }
    
    public BitArrayZ(int bitLen, byte [] bytes, int base) {
        if(bitLen == 0  ||  bytes == null) throw new NullPointerException("zero memory");
        data = bytes;
        byteBase = base;
        this.bitLen = bitLen;
        toStringBuilder = null;
    }

    public BitArrayZ(int bitLen, byte [] bytes) {
        this(bitLen, bytes, 0);
    }
    
    public BitArrayZ(final String codeStr) {
        this(codeStr.length());
        for(int i = 0; i < bitLen; ++i) {
            set(i, (codeStr.charAt(i) == '0')? 0: 1);
        }
    }
    
    public BitArrayZ(final String codeStr, int bitLen, byte [] bytes, int base) {
        this(bitLen, bytes, 0);
        for(int i = 0; i < bitLen; ++i) {
            set(i, (codeStr.charAt(i) == '0')? 0: 1);
        }
    }
    
    public BitArrayZ(final String codeStr, int bitLen, byte [] bytes) {
        this(codeStr, bitLen, bytes, 0);
    }
    
    public BitArrayZ(final String codeStr, byte [] bytes) {
        this(codeStr.length(), bytes, 0);
        for(int i = 0; i < bitLen; ++i) {
            set(i, (codeStr.charAt(i) == '0')? 0: 1);
        }
    }
    
    public static BitArrayZ encode(final String codeStr) {
        return new BitArrayZ(codeStr);
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
        return (data[byteIndex] & (1 << (7 ^ bitOffset))) == 0? 0: 1;
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
    
    public final int getBitLen() {
        return bitLen;
    }

    public final void setBitLen(int bitLen) {
        this.bitLen = bitLen;
    }

    @Override
    public String toString() {
        if(toStringBuilder == null) toStringBuilder = new StringBuilder();
            else toStringBuilder.delete(0, toStringBuilder.length());
        for(int i = 0; i < bitLen; ++i) {
            toStringBuilder.append(get(i));
        }
        return toStringBuilder.toString();
    }
    
    
}