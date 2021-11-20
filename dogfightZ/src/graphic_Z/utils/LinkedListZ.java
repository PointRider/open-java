package graphic_Z.utils;

import java.util.Collection;
import java.util.Deque;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class LinkedListZ<ElementType> implements Iterable<ElementType>, Deque<ElementType> {


    public static class Node<ElementType> {
        ElementType data;
        Node<ElementType> pre, next;
        
        Node(Node<ElementType> pre, ElementType data, Node<ElementType> next) {
            this.pre = pre;
            this.data = data;
            this.next = next;
        }
        
        Node(ElementType data) {
            this.pre = this;
            this.data = data;
            this.next = this;
        }
        
        Node() {
            this.pre = this;
            this.data = null;
            this.next = this;
        }
    }
    
    public class Iterator implements ListIterator<ElementType> {
        
        //-----------------[data]-----------------
        private LinkedListZ<ElementType> fromList; //所属链表
        private Node<ElementType>        current;  //当前节点
        //-----------------[data]-----------------
        
        //构造函数 
        Iterator(LinkedListZ<ElementType> llist, Node<ElementType> nowAt) {
            fromList = llist;
            current  = nowAt;
        }
        
        Iterator(LinkedListZ<ElementType> llist) {
            this(llist, llist.entry);
        }
        
        //拷贝构造函数
        Iterator(final Iterator another) {
            fromList = another.fromList;
            current  = another.current;
        }
        
        //默认构造函数：创建一个无效的迭代器
        Iterator() {
            this(null, null);
        }
        
        //返回迭代器是否有效
        public boolean loaded() { return fromList != null  &&  current != null; }
        
        //返回所在链表
        public LinkedListZ<ElementType> lList() throws NullPointerException {
            if(fromList != null) return fromList;
            else throw new NullPointerException("Iterator not belong to any list.");
        }
        
        public boolean inEmpty() { return fromList.isEmpty(); }
        public boolean isBegin() { return current == fromList.entry.next; }
        public boolean isEnd()   { return current == fromList.entry; }
        
        @Override
        public boolean hasNext() {
            return current != fromList.entry;
        }
        
        public boolean at(Iterator another) {
            return current == another.current;
        }

        @Override
        public ElementType next() {
            ElementType temp = current.data;
            current = current.next;
            return temp;
        }

        @Override
        public boolean hasPrevious() {
            return current != fromList.entry;
        }

        @Override
        public ElementType previous() {
            ElementType temp = current.data;
            current = current.pre;
            return temp;
        }

        @Override
        public int nextIndex() {
            // TODO 自动生成的方法存根
            return -1;
        }

        @Override
        public int previousIndex() {
            // TODO 自动生成的方法存根
            return -1;
        }

        @Override
        public void remove() {
            fromList.remove(this);
        }

        @Override
        public void set(ElementType e) throws NullPointerException {
            if(!isEnd()) current.data = e;
            else throw new NullPointerException("Can not store data to the head node.");
        }

        @Override
        public void add(ElementType e) {
            fromList.insertToFront(this, e);
        }
        
        public ElementType get() {
            return current.data;
        }
    }
    //-----------------[data]-----------------
    private Node<ElementType> entry;
    private Iterator          start;
    private final Iterator    finish;
    private int               sizeNow;
    //-----------------[data]-----------------
    
	public LinkedListZ() {
		entry   = new Node<ElementType>();
		sizeNow = 0;
		start   = new Iterator(this);
		finish  = new Iterator(start);
	}
	
	public Iterator begin() {
	    return new Iterator(start);
	}
	
	public Iterator end() {
	    return new Iterator(finish);
	}
	
    @Override
    public Iterator iterator() {
        return begin();
    }

    @Override
    public ElementType removeFirst() throws NoSuchElementException {
        if(!isEmpty()) {
            Node<ElementType> firstNode = entry.next;//第一个节点 
            
            //更改链式结构 
            entry.next = firstNode.next;
            firstNode.next.pre = entry;

            --sizeNow;
            //维护迭代器 
            start.next();
            return firstNode.data;
        } else throw new NoSuchElementException("List is empty.");
    }
    
    @Override
    public ElementType removeLast() {
        if(!isEmpty()) {
            Node<ElementType> lastNode = entry.pre;//最后一个节点
            
            //更改链式结构
            entry.pre = lastNode.pre;
            lastNode.pre.next = entry;

            --sizeNow;
            //维护迭代器
            start.current = entry.next;
            return lastNode.data;
        } else throw new NoSuchElementException("List is empty.");
    }
    
    public Iterator first() { return begin(); }
    public Iterator last()  {
        Iterator lastItr = end();
        lastItr.previous();
        return lastItr;
    }

    @Override
    public ElementType peekFirst() {
        return entry.next.data;
    }

    @Override
    public ElementType peekLast() {
        return entry.pre.data;
    }

    @Override
    public ElementType getFirst() throws NoSuchElementException {
        ElementType e = peekFirst();
        if(e == null) throw new NoSuchElementException("List is empty.");
        return e;
    }

    @Override
    public ElementType getLast() throws NoSuchElementException {
        ElementType e = peekLast();
        if(e == null) throw new NoSuchElementException("List is empty.");
        return e;
    }

    public Iterator pushFront(ElementType e) {
        
        //更改链式结构
        Node<ElementType> oldNext = entry.next;
        Node<ElementType> newNode = new Node<ElementType>(entry, e, oldNext);
        entry.next                = newNode;
        oldNext.pre               = newNode;

        ++sizeNow;
        //维护迭代器
        start.current             = newNode;
        return new Iterator(this, newNode);
    }
    
    public Iterator pushBack(ElementType e) {

        //更改链式结构
        Node<ElementType> oldPre  = entry.pre;
        Node<ElementType> newNode = new Node<ElementType>(oldPre, e, entry);
        entry.pre                 = newNode;
        oldPre.next               = newNode;

        ++sizeNow;
        //维护迭代器
        start.current             = entry.next;
        return new Iterator(this, newNode);
    }
    
    @Override
    public void addFirst(ElementType e) {
        pushFront(e);
    }

    @Override
    public void addLast(ElementType e) {
        pushBack(e);
    }
    
    public Iterator insertToFront(Iterator itr, ElementType e) {
        
        //更改链式结构
        Node<ElementType> oldPre  = itr.current.pre;
        Node<ElementType> newNode = new Node<ElementType>(oldPre, e, itr.current);
        itr.current.pre = newNode;
        oldPre.next  = newNode;
        
        ++sizeNow;
        
        //维护迭代器
        itr.fromList.start.current = itr.fromList.entry.next;
        
        return new Iterator(itr.fromList, newNode);
    }
    
    public Iterator insertToBack(Iterator itr, ElementType e) {

        //更改链式结构
        Node<ElementType> oldNext = itr.current.next;
        Node<ElementType> newNode = new Node<ElementType>(itr.current, e, oldNext);
        itr.current.next          = newNode;
        oldNext.pre               = newNode;
        
        ++sizeNow;
        
        //维护迭代器
        itr.fromList.start.current = itr.fromList.entry.next;
        return new Iterator(itr.fromList, newNode);
    }
    
    public void remove(Iterator itr) {
        //检查迭代器是否属于本链表，且不允许擦除入口哨兵节点(nil) 
        if(itr.fromList != this  ||  itr.current == entry) return;
        
        //更改链式结构
        Node<ElementType> oldNxt = itr.current.next;
        itr.current.pre.next = oldNxt;
        itr.current.next.pre = itr.current.pre;
        
        //删除目标迭代器itr指向的节点 
        itr.current.data = null;
        
        //使得目标迭代器itr指向下一个节点
        itr.current = oldNxt;
        
        --sizeNow;
        
        //维护迭代器
        start.current = entry.next;
    }

    public boolean isEmpty() {
	    return entry.next == entry;
	}

    @Override
    public void clear() {
        Node<ElementType> eachNode = entry.next;
        Node<ElementType> eachNext = eachNode.next;
        
        while(eachNode != entry) {
            eachNode.data = null;
            eachNode = eachNext;
            eachNext = eachNode.next;
        }
        
        sizeNow = 0;
        
        entry.next = entry.pre = entry;
        start.current = entry;
    }

    public ListIterator<ElementType> listIterator(int index) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public boolean offerFirst(ElementType e) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean offerLast(ElementType e) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public ElementType pollFirst() {
        if(isEmpty()) return null;
        return removeFirst();
    }

    @Override
    public ElementType pollLast() {
        if(isEmpty()) return null;
        return removeLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean offer(ElementType e) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public ElementType remove() {
        return removeFirst();
    }

    @Override
    public ElementType poll() {
        return pollFirst();
    }

    @Override
    public ElementType element() {
        return getFirst();
    }

    @Override
    public ElementType peek() {
        return peekFirst();
    }

    @Override
    public void push(ElementType e) {
        addFirst(e);
    }

    @Override
    public ElementType pop() {
        return removeFirst();
    }

    @Override
    public java.util.Iterator<ElementType> descendingIterator() {
        return null;
    }

    @Override
    public int size() {
        return sizeNow;
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
    public boolean add(ElementType e) {
        addLast(e);
        return true;
    }


    public ListIterator<ElementType> listIterator() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends ElementType> c) {
        // TODO 自动生成的方法存根
        return false;
    }

    @Override
    public boolean remove(Object o) {
        // TODO 自动生成的方法存根
        return false;
    }

    public static void main(String [] args) {
        LinkedListZ<Integer> list = new LinkedListZ<>();
        LinkedListZ<Integer>.Iterator itr = null;
        list.pushFront(1);
        list.pushFront(2);
        itr = list.pushFront(3);
        list.pushFront(4);
        list.pushFront(5);
        
        for(Integer e : list) {
            System.out.print(e + " ");
        }
        System.out.println();
        
        System.out.println(itr.get());
        itr.remove();
        itr.remove();
        itr.remove();
        System.out.println(itr.get());
        
        for(Integer e : list) {
            System.out.print(e + " ");
        }
        System.out.println();

        System.out.println(list.size());
    }

}
