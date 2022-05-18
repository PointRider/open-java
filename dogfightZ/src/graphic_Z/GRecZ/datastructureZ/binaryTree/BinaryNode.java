package graphic_Z.GRecZ.datastructureZ.binaryTree;

public class BinaryNode<DataType> implements BinaryNodeRequire<BinaryNode<DataType>> {
    protected DataType data;
    protected BinaryNode<DataType> left;
    protected BinaryNode<DataType> right;
    protected BinaryNode<DataType> parent;
    
    public BinaryNode(DataType data, BinaryNode<DataType> left, BinaryNode<DataType> right, BinaryNode<DataType> parent) {
        this.data   = data;
        this.left   = left;
        this.right  = right;
        this.parent = parent;
    }
    
    public BinaryNode(DataType data, BinaryNode<DataType> left, BinaryNode<DataType> right) {
        this(data, left, right, null);
    }
    
    public BinaryNode(DataType data) {
        this(data, null, null);
    }
    
    public BinaryNode() {
        this(null);
    }

    @Override
    public BinaryNode<DataType> getParent() {
        return parent;
    }

    @Override
    public BinaryNode<DataType> getLeftChild() {
        return left;
    }

    @Override
    public BinaryNode<DataType> getRightChild() {
        return right;
    }
}