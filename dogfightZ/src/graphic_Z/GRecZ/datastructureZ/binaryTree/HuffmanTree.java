package graphic_Z.GRecZ.datastructureZ.binaryTree;

import graphic_Z.GRecZ.datastructureZ.BitArrayZ;
import graphic_Z.GRecZ.datastructureZ.Tuple;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

public final class HuffmanTree<ValueType> extends BinaryTree<BinarySearchingNode<Tuple<Integer, ValueType>>> implements Iterable<ValueType> {
    
    /**
     * 
     **/
    public static final class Builder<ElementType> {

        private PriorityQueue<BinarySearchingNode<Tuple<Integer, ElementType>>> waitToMake;
        
        public Builder() {
            waitToMake = new PriorityQueue<BinarySearchingNode<Tuple<Integer, ElementType>>>();
        }
        
        public void insert(int weight, ElementType e) {
            waitToMake.add(HuffmanTree.newNode(weight, e));
        }
        
        public HuffmanTree<ElementType> build() {
            if(waitToMake == null) throw new java.lang.NullPointerException("No element to build a Huffman-Tree.");
            return new HuffmanTree<ElementType>(waitToMake);
        }
    }
    
    public static final class PreIterator<ElementType> implements Iterator<ElementType> {
        
        private BinaryTree.PreIterator<BinarySearchingNode<Tuple<Integer, ElementType>>> superPreIterator;
        
        public PreIterator(BinaryTree.PreIterator<BinarySearchingNode<Tuple<Integer, ElementType>>> superPreIterator) {
            this.superPreIterator = superPreIterator;
        }
        
        @Override
        public boolean hasNext() {
            return superPreIterator.hasNext();
        }

        @Override
        public ElementType next() {
            return superPreIterator.next().data.value;
        }
        
    }
    
    HuffmanTree(PriorityQueue<BinarySearchingNode<Tuple<Integer, ValueType>>> weightList) {
        super();
        makeTree(weightList);
    }
    
    HuffmanTree() {
        super();
        root = null;
    }

    public HuffmanTree(Map<ValueType, BitArrayZ> codeMap) {
        super();
        for(ValueType key : codeMap.keySet()) {
            insert(codeMap.get(key), key);
        }
        resetPreIterator();
    }
    
    public static <ElementType> BinarySearchingNode<Tuple<Integer, ElementType>> newNode(
            Integer weight, ElementType e, 
            BinarySearchingNode<Tuple<Integer, ElementType>> leftChild,
            BinarySearchingNode<Tuple<Integer, ElementType>> parent,
            BinarySearchingNode<Tuple<Integer, ElementType>> rightChild
    ) {
        return new BinarySearchingNode<Tuple<Integer, ElementType>>(
            new Tuple<Integer, ElementType>(weight, e),
            leftChild, rightChild, parent
        );
    }
    
    public static <ElementType> BinarySearchingNode<Tuple<Integer, ElementType>> newNode(Integer weight, ElementType e) {
        return new BinarySearchingNode<Tuple<Integer, ElementType>>(
            new Tuple<Integer, ElementType>(weight, e)
        );
    }
    
    private void makeTree(PriorityQueue<BinarySearchingNode<Tuple<Integer, ValueType>>> weightList) {
        
        for(BinarySearchingNode<Tuple<Integer, ValueType>> left = null, right = null, mid = null; weightList.size() > 1;) {
            left = weightList.poll();
            right = weightList.poll();
            mid = newNode(left.data.key.intValue() + right.data.key.intValue(), null, left, null, right);
            left.parent  = mid;
            right.parent = mid;
            weightList.add(mid);
        }
        
        root = weightList.poll();
        resetPreIterator();
    }
    
    private static <ElementType> void encode(
        Map<ElementType, BitArrayZ> resultMap, 
        LinkedList<Byte> encodingStack,
        BinarySearchingNode<Tuple<Integer, ElementType>> cur
    ) {
        if(cur == null) return;
        
        encodingStack.addLast((byte) 0);
        encode(resultMap, encodingStack, cur.left);
        encodingStack.removeLast();
        
        if(cur.left == null && cur.right == null) {
            BitArrayZ newCode = new BitArrayZ(encodingStack.size());
            int i = 0;
            for(Byte b : encodingStack) {
                newCode.set(i++, b);
            } 
            resultMap.put(cur.data.value, newCode);
        }
        
        encodingStack.addLast((byte) 1);
        encode(resultMap, encodingStack, cur.right);
        encodingStack.removeLast();
    }
    
    public void encode(Map<ValueType, BitArrayZ> resultMap) {
        LinkedList<Byte> encodingStack = new LinkedList<Byte>();
        encode(resultMap, encodingStack, root);
    }

    /**
     * remember to resetPreIterator();*/
    private void insert(BitArrayZ code, ValueType key) {
        
        if(root == null) {
            root = newNode(0, null);
        }

        BinarySearchingNode<Tuple<Integer, ValueType>> cur = root;
        
        for(int i = 0, j = code.getBitLen(); i < j; ++i) {
            switch (code.get(i)) {
            case 0:
                if(cur.left == null) {
                    if(i + 1 == j) {
                        cur.left = newNode(0, key, null, cur, null);
                    } else cur.left = newNode(0, null, null, cur, null);
                }
                cur = cur.left;
                break;
            case 1:
                if(cur.right == null) {
                    if(i + 1 == j) {
                        cur.right = newNode(0, key, null, cur, null);
                    } else cur.right = newNode(0, null, null, cur, null);
                }
                cur = cur.right;
                break;
            }
        }
    }
    
    public static class MatchTable<ValueType> {
        public ValueType result;
        public int length;
        
        public MatchTable(ValueType result, int length) {
            this.result = result;
            this.length = length;
        }
    }
    
    public MatchTable<ValueType> match(BitArrayZ code, int start) {
        if(root == null) return null;
        BinarySearchingNode<Tuple<Integer, ValueType>> cur = root;
        int i, j;
        for(i = start, j = code.getBitLen(); i < j; ++i) {
            switch (code.get(i)) {
            case 0: cur = cur.left;  break;
            case 1: cur = cur.right; break;
            }
            if(cur == null) return null;
            if(cur.left == null  &&  cur.right == null) {
                return new MatchTable<ValueType>(cur.data.value, i - start + 1);
            }
        }
        return null;
    }
    
    public MatchTable<ValueType> match(BitArrayZ code) {
        return match(code, 0);
    }
    
    public ValueType decode(BitArrayZ code) {
        return match(code).result;
    }
    
    @Override
    public  PreIterator<ValueType> iterator() {
        return new PreIterator<ValueType>(preIterator);
    }
    
    public static void main(String [] args) {
        HuffmanTree.Builder<Integer> builder = new HuffmanTree.Builder<Integer>();
        /*
        builder.insert(5, 3);
        builder.insert(4, 2);
        builder.insert(6, 7);
        builder.insert(1, 9);
        builder.insert(7, 5);
        */
        
        for(int b = 0; b < 256; ++b) {
            builder.insert(b,  b);
        }
        
        HuffmanTree<Integer> t1 = builder.build();
        Map<Integer, BitArrayZ> codeMap1 = new BitArrayZ.ByteCodeMap();
        t1.encode(codeMap1);
        
        HuffmanTree<Integer> t2 = new HuffmanTree<Integer>(codeMap1);
        Map<Integer, BitArrayZ> codeMap2 = new BitArrayZ.ByteCodeMap();
        t2.encode(codeMap2);
        
        System.err.println(codeMap1.toString());
        System.err.println(codeMap2.toString());
        
        HuffmanTree.MatchTable<Integer> match = t2.match(BitArrayZ.encode("00000001111110011001010"), 15);
        System.err.println(match.length);
        System.err.println(match.result);
    }
}
