package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    int size = 0;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;

    // You may add extra fields or helper methods though!

    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
        this.entries = this.createArrayOfEntries(DEFAULT_INITIAL_CAPACITY);
        this.size = 0;
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        V returnKey = null;
        if (size == 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                returnKey = entries[i].getValue();
            }
            if (i == size - 1 && returnKey != (V) key) {
                return returnKey;
            }
        }
        return (V) key;
    }

    @Override
    public V put(K key, V value) {
        //if it contains you need only edit the value but not change key
        if (this.containsKey(key)) {
            for (int i = 0; i < size; i++) {
                K temp2 = entries[i].getKey();
                if (Objects.equals(key, temp2)) {
                    V tempValue = entries[i].getValue();
                    entries[i] = new SimpleEntry<K, V>(key, value);
                    return tempValue;
                }
            }
        }
        //if it doesnt contain key you need add both key and value and adjust size
        else {
            entries[size] = new SimpleEntry<>(key, value);
            size += 1;
            if (size <= entries.length) {
                SimpleEntry<K, V>[] newEntries  = this.createArrayOfEntries(size + size);
                for (int i = 0; i < size; i++) {
                    newEntries[i] = entries[i];
                }
                entries = newEntries;
            }
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        V temp = null;
        if (!this.containsKey(key)) {
            return null;
        }
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(entries[i].getKey(), key)) {
                    temp = entries[i].getValue();
                    entries[i] = entries[size - 1];
                    entries[size - 1] = null;
                    i = size;
                    size--;
                }
            }
        }
        return temp;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            entries[i] = null;
        }
        size = 0;
        //hard coded since size will always be zero in this case
    }

    @Override
    public boolean containsKey(Object key) {
        if (size < 1) {
            return false;
        }
        else {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(key, entries[i].getKey())) {
                    return true;
                }
            }
            return false;
            }
        }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries);
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int size;
        private int current;
        // You may add more fields and constructor parameters

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.current = 0;
            this.size = size;
            this.entries = entries;
        }

        @Override
        public boolean hasNext() {
            if (entries == null) {
                return false;
            }
            return entries[current] != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            current++;
            if (entries[current - 1] == null) {
                throw new NoSuchElementException();
            }
            if (entries == null) {
                return null;
            }
            return entries[current - 1];
        }
    }
}
