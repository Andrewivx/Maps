package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 1;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 10;
    double resizing;
    int chainCount;
    AbstractIterableMap<K, V>[] chains;
    int chainCapacity;
    int size;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */

    // You're encouraged to add extra fields (and helper methods) though!

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        chains = createArrayOfChains(initialChainCount);
        size = 0;
        chainCapacity = chainInitialCapacity;
        chainCount = initialChainCount;
        resizing = resizingLoadFactorThreshold;
        //one chain per slot basicaly
        for (int i = 0; i < initialChainCount; i++) {
            chains[i] = createChain(chainInitialCapacity);
        }
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        V newChain = null;
        int hash = 0;
        if (key != null) {
            hash = key.hashCode();
        }
        if (hash < 1) {
            hash *= (-1);
        }
        newChain = chains[hash % chains.length].get(key);
        if (key == null) {
            newChain = chains[0].get(key);
        }
        return newChain;
    }

    @Override
    public V put(K key, V value) {
        int hash = 0;
        int value1 = 0;
        int increment = 1;
        boolean isKey = containsKey(key);
        if (key != null) {
            value1 = key.hashCode();
            if (value1 < 0) {
                value1 *= -1;
            }
        }
            hash = value1;
            hash = hash % chains.length;
            V result = chains[hash].put(key, value);
        if (!isKey) {
            size = size + increment;
        }
        int value3 = size / chains.length;
        //2x use mayb
        if (value3 > resizing) {
            resize();
        }
        return result;
    }

    private void resize() {
        AbstractIterableMap<K, V>[] newChains = createArrayOfChains(chains.length * 2);
        for (int i = 0; i < newChains.length; i++) {
            newChains[i] = createChain(DEFAULT_INITIAL_CHAIN_CAPACITY);
        }
        int incremented = 0;
        for (int i = 0; i < size; i++) {
            incremented++;
            chainCapacity = incremented;
        }
        // each chain make a fkin little one stickingoutta
        for (int i = 0; i < chains.length; i++) {
            Iterator<Map.Entry<K, V>> iter = chains[i].iterator();
            while (iter.hasNext()) {
                int hash = 0;
                Map.Entry<K, V> newPart = iter.next();
                int value = newPart.getKey().hashCode();
                if (value < 0) {
                    value *= (-1);
                }
                hash = value % chains.length * 2;
                K key = newPart.getKey();
                V value2 = newPart.getValue();
                newChains[hash].put(key, value2);
            }
        }
        chains = newChains;
    }

    @Override
    public V remove(Object key) {
        int index = 0;
        int value = 0;
        int sizeR = -1;
        if (key != null) {
            value = key.hashCode();
            if (value < 0) {
                value *= -1;
            }
            index = value % chains.length;
        }
        if (chains[index] == null) {
            return null;
        } else {
            size += sizeR;
            return chains[index].remove(key);
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < chains.length; i++) {
                chains[i] = null;
        }
        size = 0;
        // again hardcoded since it'll always be zero
    }

    @Override
    public boolean containsKey(Object key) {
        int hash = 0;
        if (key == null) {
            return true;
        }
        int index = key.hashCode();
        if (index < 0) {
            index *= -1;
        }
        index = index % chains.length;
        if (chains[index] != null) {
            return chains[index].containsKey(key);
        }
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private int current;
        private Iterator<Map.Entry<K, V>> iter;
        // You may add more fields and constructor parameters

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.current = 0;
            this.chains = chains;
            if (chains[0] != null) {
                iter = chains[0].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            while (current < chains.length) {
                int value = 1;
                if (iter.hasNext()) {
                    return true;
                } else {
                    current += value;
                    if (current == chains.length) {
                        return false;
                    } else if (chains[current] != null) {
                        iter = chains[current].iterator();
                    }
                }
            }
            return false;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (this.hasNext()) {
                return iter.next();
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
