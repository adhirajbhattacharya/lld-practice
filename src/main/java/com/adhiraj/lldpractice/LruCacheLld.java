package com.adhiraj.lldpractice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class LruCacheLld {
    public static void main(String[] args) {

    }
}

class LruCachedEntry {
    int key;
    int value;
    LruCachedEntry prev;
    LruCachedEntry next;

    public LruCachedEntry(int key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof LruCachedEntry)) return false;

        LruCachedEntry other = (LruCachedEntry) o;
        return this.key == other.key && this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}

/**
 * LRU Cache without using inbuilt classes. Else can be done with LinkedHashMap.
 */
class LruCache {
    Map<Integer, LruCachedEntry> cache = new HashMap<>();
    LruCachedEntry lruQueueHead = new LruCachedEntry(-1, -1);
    LruCachedEntry lruQueueTail = lruQueueHead;
    int capacity;

    public LruCache(int capacity) {
        this.capacity = capacity;
    }

    int get(int key) {
        LruCachedEntry lruCachedEntry = cache.get(key);
        if (lruCachedEntry == null) return -1;
        updateLruQueue(lruCachedEntry);
        return lruCachedEntry.value;
    }

    private void updateLruQueue(LruCachedEntry lruCachedEntry) {
        if (lruQueueTail.equals(lruCachedEntry)) return;
        lruCachedEntry.prev.next = lruCachedEntry.next;
        lruCachedEntry.next.prev = lruCachedEntry.prev;
        addToLruQueueTail(lruCachedEntry);
    }

    private void addToLruQueueTail(LruCachedEntry lruCachedEntry) {
        lruQueueTail.next = lruCachedEntry;
        lruCachedEntry.prev = lruQueueTail;
        lruCachedEntry.next = null;
        lruQueueTail = lruCachedEntry;
    }

    void put(int key, int value) {
        LruCachedEntry lruCachedEntry = cache.get(key);
        if (lruCachedEntry != null) {
            updateCachedValueAndLruQueue(lruCachedEntry, value);
            return;
        }

        if (capacity <= cache.size()) removeLru();

        addToCache(new LruCachedEntry(key, value));
    }

    private void addToCache(LruCachedEntry lruCachedEntry) {
        cache.put(lruCachedEntry.key, lruCachedEntry);
        addToLruQueueTail(lruCachedEntry);
    }

    private void updateCachedValueAndLruQueue(LruCachedEntry lruCachedEntry, int value) {
        if (lruCachedEntry.value != value) {
            lruCachedEntry.value = value;
        }
        updateLruQueue(lruCachedEntry);
    }

    private void removeLru() {
        LruCachedEntry lru;
        if (capacity == 1) {
            lru = lruQueueTail;
            lruQueueHead.next = null;
            lruQueueTail = lruQueueHead;
        } else {
            lru = lruQueueHead.next;
            lruQueueHead.next = lruQueueHead.next.next;
            lruQueueHead.next.prev = lruQueueHead;
        }

        cache.remove(lru.key);
        lru.next = null;
        lru.prev = null;
    }
}

class LruCacheWithLinkedHashMap {
    LinkedHashMap<Integer, Integer> cache;

    public LruCacheWithLinkedHashMap(int capacity) {
        cache = new LinkedHashMap<>(capacity, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer,Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    void put(int key, int value) {
        cache.put(key, value);
    }
}


