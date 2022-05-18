package graphic_Z.GRecZ.datastructureZ.binaryTree;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public class BinarySearchingTree<ElementType extends Comparable<ElementType>> 
    extends BinaryTree<BinaryNode<ElementType>> implements NavigableSet<BinaryNode<ElementType>> {
    
    int size;
    
    public BinarySearchingTree() {
        super();
        size = 0;
    }

    @Override
    public Comparator<? super BinaryNode<ElementType>> comparator() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public BinaryNode<ElementType> first() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public BinaryNode<ElementType> last() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public int size() {
        // TODO 自动生成的方法存根
        return 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean contains(Object o) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public Object[] toArray() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public boolean add(BinaryNode<ElementType> e) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean remove(Object o) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends BinaryNode<ElementType>> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public void clear() {
        // TODO 自动生成的方法存根
        
    }

    @Override
    public BinaryNode<ElementType> lower(BinaryNode<ElementType> e) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public BinaryNode<ElementType> floor(BinaryNode<ElementType> e) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public BinaryNode<ElementType> ceiling(BinaryNode<ElementType> e) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public BinaryNode<ElementType> higher(BinaryNode<ElementType> e) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public BinaryNode<ElementType> pollFirst() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public BinaryNode<ElementType> pollLast() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public NavigableSet<BinaryNode<ElementType>> descendingSet() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public Iterator<BinaryNode<ElementType>> descendingIterator() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public NavigableSet<BinaryNode<ElementType>> subSet(BinaryNode<ElementType> fromElement, boolean fromInclusive,
            BinaryNode<ElementType> toElement, boolean toInclusive) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public NavigableSet<BinaryNode<ElementType>> headSet(BinaryNode<ElementType> toElement, boolean inclusive) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public NavigableSet<BinaryNode<ElementType>> tailSet(BinaryNode<ElementType> fromElement, boolean inclusive) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public SortedSet<BinaryNode<ElementType>> subSet(BinaryNode<ElementType> fromElement,
            BinaryNode<ElementType> toElement) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public SortedSet<BinaryNode<ElementType>> headSet(BinaryNode<ElementType> toElement) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public SortedSet<BinaryNode<ElementType>> tailSet(BinaryNode<ElementType> fromElement) {
        // TODO 自动生成的方法存根
        return null;
    }

    //TreeSet<Integer> t;
    
}
