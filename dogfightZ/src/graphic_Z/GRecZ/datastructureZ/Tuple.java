package graphic_Z.GRecZ.datastructureZ;

public class Tuple<KeyType extends Comparable<KeyType>, ValueType> 
implements Comparable<Tuple<KeyType, ValueType>> {
    public KeyType   key;
    public ValueType value;
    
    public Tuple(KeyType key, ValueType value) {
        this.key   = key;
        this.value = value;
    }
    
    @Override
    public int compareTo(Tuple<KeyType, ValueType> o) {
        return key.compareTo(o.key);
    }
}