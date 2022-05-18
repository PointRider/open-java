package graphic_Z.GRecZ.datastructureZ.binaryTree;

import java.util.Iterator;

public class BinaryTree<NodeType extends BinaryNodeRequire<NodeType>> implements Iterable<NodeType> {

    protected NodeType              root;
    protected PreIterator<NodeType> preIterator;
    
    public BinaryTree() {
        root = null;
        initPreIterator();
    }
    
    protected void resetPreIterator() {
        preIterator.setNow(root);
        preIterator.setLrDirection(PreIterator.LEFT);
        preIterator.setUdDirection(PreIterator.TODOWN);
    }
    
    protected void initPreIterator() {
        preIterator = new PreIterator<NodeType>();
        preIterator.setNow(root);
        preIterator.setNil(null);
        resetPreIterator();
    }
    
    public static abstract class IteratorBase<NodeType> implements Iterator<NodeType> {
        protected NodeType now;
        protected NodeType nil;
        
        public IteratorBase(NodeType now, NodeType nil) {
            this.now = now;
            this.nil = nil;
        }
        public IteratorBase(NodeType now) {
            this(now, null);
        }
        
        public IteratorBase() {
            this(null);
        }
        
        public NodeType peek() {
            return now;
        }
        
        final NodeType getNow() {
            return now;
        }
        final void setNow(NodeType now) {
            this.now = now;
        }
        final NodeType getNil() {
            return nil;
        }
        final void setNil(NodeType nil) {
            this.nil = nil;
        }
    }
    
    public static class PreIterator<NodeType extends BinaryNodeRequire<NodeType>> extends IteratorBase<NodeType> {
        static final boolean LEFT     = false;
        static final boolean RIGHT    = true;
        static final boolean TODOWN   = false;
        static final boolean FROMDOWN = true;
        
        private boolean lrDirection;
        private boolean udDirection;
        
        final boolean getLrDirection() {
            return lrDirection;
        }

        final void setLrDirection(boolean lrDirection) {
            this.lrDirection = lrDirection;
        }

        final boolean getUdDirection() {
            return udDirection;
        }

        final void setUdDirection(boolean udDirection) {
            this.udDirection = udDirection;
        }

        public PreIterator(NodeType now, NodeType nil, boolean lr, boolean ud) {
            super(now, nil);
            lrDirection = lr;
            udDirection = ud;
        }
        
        public PreIterator(NodeType now, NodeType nil) {
            super(now, nil);
            lrDirection = LEFT;
            udDirection = TODOWN;
        }
        
        public PreIterator(NodeType now) {
            super(now, null);
            lrDirection = LEFT;
            udDirection = TODOWN;
        }
        
        public PreIterator() {
            this(null);
        }
        
        @Override
        public boolean hasNext() {
            return now == nil;
        }

        @Override
        public NodeType next() {
            NodeType preNode = now;
            
            if(udDirection == TODOWN) {
                if(lrDirection == LEFT) {
                    if(now.getLeftChild() != nil) {
                        now = now.getLeftChild();
                    } else {
                        if(now.getRightChild() != nil) {
                            now = now.getRightChild();
                        } else {
                            if(now == now.getParent().getRightChild()) lrDirection = RIGHT;
                            udDirection = FROMDOWN;
                            now = now.getParent();
                            next();
                        }
                    }
                } /**/
            } else {
                if(now == null) throw new NullPointerException("Iterator is end.");
                if(lrDirection == LEFT) {
                    if(now.getRightChild() != nil) {
                        now = now.getRightChild();
                        udDirection = TODOWN;
                    } else {
                        if(now == now.getParent().getRightChild()) lrDirection = RIGHT;
                        now = now.getParent();
                        next();
                    }
                } else {
                    if(now == now.getParent().getLeftChild()) lrDirection = LEFT;
                    now = now.getParent();
                    next();
                }
            }
            return preNode;
        }
    }

    @Override
    public PreIterator<NodeType> iterator() {
        return preIterator;
    }
}
