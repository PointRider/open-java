package graphic_Z.GRecZ.datastructureZ.binaryTree;

import graphic_Z.GRecZ.datastructureZ.BitArrayZ;
import graphic_Z.GRecZ.datastructureZ.Tuple;

import java.util.Map;
import java.util.PriorityQueue;

public final class HuffmanTree<ElementType> extends BinaryTree<BinarySearchingNode<Tuple<Integer, ElementType>>> {
    
    /**
     * 
     **/
    public static final class builder<ElementType> {

        private PriorityQueue<BinarySearchingNode<Tuple<Integer, ElementType>>> waitToMake;
        
        public builder() {
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
    
    HuffmanTree(PriorityQueue<BinarySearchingNode<Tuple<Integer, ElementType>>> weightList) {
        makeTree(weightList);
    }
    
    HuffmanTree() {
        root = null;
    }
    
    public static <ElementType> BinarySearchingNode<Tuple<Integer, ElementType>> newNode(
            Integer weight, ElementType e, 
            BinarySearchingNode<Tuple<Integer, ElementType>> leftChild,
            BinarySearchingNode<Tuple<Integer, ElementType>> rightChild
    ) {
        return new BinarySearchingNode<Tuple<Integer, ElementType>>(
            new Tuple<Integer, ElementType>(weight, e),
            leftChild, rightChild
        );
    }
    
    public static <ElementType> BinarySearchingNode<Tuple<Integer, ElementType>> newNode(Integer weight, ElementType e) {
        return new BinarySearchingNode<Tuple<Integer, ElementType>>(
            new Tuple<Integer, ElementType>(weight, e)
        );
    }
    
    private void makeTree(PriorityQueue<BinarySearchingNode<Tuple<Integer, ElementType>>> weightList) {
        
        for(BinarySearchingNode<Tuple<Integer, ElementType>> left = null, right = null; weightList.size() > 1;) {
            left = weightList.poll();
            right = weightList.poll();
            weightList.add(newNode(left.data.key.intValue() + right.data.key.intValue(), null, left, right));
        }
        
        root = weightList.poll();
    }
    
    public void encode(Map<ElementType, BitArrayZ> resultMap) {
        
        if(root == null || resultMap == null) return;
        
        //PreIterator<BinarySearchingNode<Tuple<Integer, ElementType>>> preView = iterator();
        
        
    }
}
