package com.adhiraj.lldprep;

import java.util.*;

public class LfuCacheLld {
    public static void main(String[] args) {
        LfuCache cache = new LfuCache(10);
        cache.put(10,13);
        cache.put(3,17);
        cache.put(6,11);
        cache.put(10,5);
        cache.put(9,10);
        System.out.println(cache.get(13));
        cache.put(2,19);
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
        cache.put(5,25);
        System.out.println(cache.get(8));
        cache.put(9,22);
        cache.put(5,5);
        cache.put(1,30);
        System.out.println(cache.get(11));
        cache.put(9,12);
        System.out.println(cache.get(7));
        System.out.println(cache.get(5));
        System.out.println(cache.get(8));
        System.out.println(cache.get(9));
        cache.put(4,30);
        cache.put(9,3);
        System.out.println(cache.get(9));
        System.out.println(cache.get(10));
        System.out.println(cache.get(10));
        cache.put(6,14);
        cache.put(3,1);
        System.out.println(cache.get(3));
        cache.put(10,11);
        System.out.println(cache.get(8));
        cache.put(2,14);
        System.out.println(cache.get(1));
        System.out.println(cache.get(5));
        System.out.println(cache.get(4));
        cache.put(11,4);
        cache.put(12,24);
        cache.put(5,18);
        System.out.println(cache.get(13));
        cache.put(7,23);
        System.out.println(cache.get(8));
        System.out.println(cache.get(12));
        cache.put(3,27);
        cache.put(2,12);
        System.out.println(cache.get(5));
        cache.put(2,9);
        cache.put(13,4);
        cache.put(8,18);
        cache.put(1,7);
        System.out.println(cache.get(6));
        cache.put(9,29);
        cache.put(8,21);
        System.out.println(cache.get(5));
        cache.put(6,30);
        cache.put(1,12);
        System.out.println(cache.get(10));
        cache.put(4,15);
        cache.put(7,22);
        cache.put(11,26);
        cache.put(8,17);
        cache.put(9,29);
        System.out.println(cache.get(5));
        cache.put(3,4);
        cache.put(11,30);
        System.out.println(cache.get(12));
        cache.put(4,29);
        System.out.println(cache.get(3));
        System.out.println(cache.get(9));
        System.out.println(cache.get(6));
        cache.put(3,4);
        System.out.println(cache.get(1));
        System.out.println(cache.get(10));
        cache.put(3,29);
        cache.put(10,28);
        cache.put(1,20);
        cache.put(11,13);
        System.out.println(cache.get(3));
        cache.put(3,12);
        cache.put(3,8);
        cache.put(10,9);
        cache.put(3,26);
        System.out.println(cache.get(8));
        System.out.println(cache.get(7));
        System.out.println(cache.get(5));
        cache.put(13,17);
        cache.put(2,27);
        cache.put(11,15);
        System.out.println(cache.get(12));
        cache.put(9,19);
        cache.put(2,15);
        cache.put(3,16);
        System.out.println(cache.get(1));
        cache.put(12,17);
        cache.put(9,1);
        cache.put(6,19);
        System.out.println(cache.get(4));
        System.out.println(cache.get(5));
        System.out.println(cache.get(5));
        cache.put(8,1);
        cache.put(11,7);
        cache.put(5,2);
        cache.put(9,28);
        System.out.println(cache.get(1));
        cache.put(2,2);
        cache.put(7,4);
        cache.put(4,22);
        cache.put(7,24);
        cache.put(9,26);
        cache.put(13,28);
        cache.put(11,26);
    }
}

class LfuCachedEntry {
    int key;
    int value;
    int freq;
    LfuCachedEntry prev, next;

    public LfuCachedEntry(int key, int value) {
        this.key = key;
        this.value = value;
        freq = 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof LfuCachedEntry)) return false;

        LfuCachedEntry other = (LfuCachedEntry) o;
        return this.key == other.key && this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}

class LfuCachedEntryDll {
    LfuCachedEntry head, tail;

    LfuCachedEntryDll() {
        head = new LfuCachedEntry(-1, -1);
        tail = head;
    }

    void add(LfuCachedEntry node) {
        tail.next = node;
        node.prev = tail;
        tail = node;
    }

    void remove(LfuCachedEntry node) {
        node.prev.next = node.next;
        if (node == tail) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
    }

    LfuCachedEntry removeFirst() {
        LfuCachedEntry first = head.next;
        if (first == tail) {
            head.next = null;
            head.prev = null;
            tail = head;
            return first;
        }
        head.next = first.next;
        first.next.prev = head;
        first.next = null;
        first.prev = null;
        return first;
    }

    boolean isEmpty() {
        return head == tail;
    }

}

class LfuCacheWithOwnLl {
    Map<Integer, LfuCachedEntry> cache = new HashMap<>();
    Map<Integer, LfuCachedEntryDll> frequencyMap = new HashMap<>();
    int minFrequency = Integer.MAX_VALUE;
    int capacity;

    LfuCacheWithOwnLl(int capacity) {
        this.capacity = capacity;
    }

    int get(int key) {
        LfuCachedEntry cachedEntry = cache.get(key);
        if (cachedEntry == null) return -1;

        updateUsage(cachedEntry);
        updateMinFrequency(cachedEntry.freq);

        return cachedEntry.value;
    }

    private void updateUsage(LfuCachedEntry cachedEntry) {
        removeFromFreqMap(cachedEntry);
        cachedEntry.freq++;
        addToFreqMap(cachedEntry);
    }

    private void removeFromFreqMap(LfuCachedEntry cachedEntry) {
        LfuCachedEntryDll freqval = frequencyMap.get(cachedEntry.freq);
        freqval.remove(cachedEntry);
        if (freqval.isEmpty())
            frequencyMap.remove(cachedEntry.freq);
    }

    private void addToFreqMap(LfuCachedEntry cachedEntry) {
        LfuCachedEntryDll freqval = frequencyMap.computeIfAbsent(cachedEntry.freq, ignore -> new LfuCachedEntryDll());
        freqval.add(cachedEntry);
    }

    private void updateMinFrequency(int candidate) {
        if (minFrequency >= candidate || !frequencyMap.containsKey(minFrequency))
            minFrequency = candidate;
    }

    void put(int key, int value) {
        LfuCachedEntry cachedEntry = cache.get(key);
        if (cachedEntry != null) {
            updateCacheValueAndUsage(cachedEntry, value);
            updateMinFrequency(cachedEntry.freq);
            return;
        }

        if (cache.size() >= capacity) {
            removeLfu();
        }

        cachedEntry = new LfuCachedEntry(key, value);
        addToCache(cachedEntry);
        minFrequency = 1;
    }

    private void updateCacheValueAndUsage(LfuCachedEntry cachedEntry, int value) {
        removeFromFreqMap(cachedEntry);
        cachedEntry.value = value;
        cachedEntry.freq++;
        addToFreqMap(cachedEntry);
    }

    private void removeLfu() {
        LfuCachedEntryDll freqval = frequencyMap.get(minFrequency);
        LfuCachedEntry lfu = freqval.removeFirst();
        cache.remove(lfu.key);
        if (freqval.isEmpty()) {
            frequencyMap.remove(minFrequency);
        }
    }

    private void addToCache(LfuCachedEntry cachedEntry) {
        cache.put(cachedEntry.key, cachedEntry);
        addToFreqMap(cachedEntry);
    }
}

class LfuCache {
    Map<Integer, LfuCachedEntry> cache = new HashMap<>();
    Map<Integer, LinkedHashSet<LfuCachedEntry>> frequencyMap = new HashMap<>();
    int minFrequency = Integer.MAX_VALUE;
    int capacity;

    LfuCache(int capacity) {
        this.capacity = capacity;
    }

    int get(int key) {
        LfuCachedEntry cachedEntry = cache.get(key);
        if (cachedEntry == null) return -1;

        updateUsageFrequency(cachedEntry);
        updateMinFrequency(cachedEntry.freq);

        return cachedEntry.value;
    }

    private void updateUsageFrequency(LfuCachedEntry cachedEntry) {
        removeFromFrequencyMap(cachedEntry);
        cachedEntry.freq++;
        addToFrequencyMap(cachedEntry);
    }

    private void removeFromFrequencyMap(LfuCachedEntry cachedEntry) {
        Set<LfuCachedEntry> lfuCachedEntries = frequencyMap.get(cachedEntry.freq);
        lfuCachedEntries.remove(cachedEntry);
        if (lfuCachedEntries.isEmpty()) {
            frequencyMap.remove(cachedEntry.freq);
        }
    }

    private void addToFrequencyMap(LfuCachedEntry cachedEntry) {
        frequencyMap.computeIfAbsent(cachedEntry.freq, ignore -> new LinkedHashSet<>()).addLast(cachedEntry);
    }

    private void updateMinFrequency(int candidate) {
        if (minFrequency >= candidate || !frequencyMap.containsKey(minFrequency))
            minFrequency = candidate;
    }

    void put(int key, int value) {
        LfuCachedEntry cachedEntry = cache.get(key);
        if (cachedEntry != null) {
            updateCacheValueAndLfu(cachedEntry, value);
            updateMinFrequency(cachedEntry.freq);
            return;
        }

        if (cache.size() == capacity) removeLfuEntry();

        cachedEntry = new LfuCachedEntry(key, value);
        addToCache(cachedEntry);
        updateMinFrequency(1);
    }

    private void updateCacheValueAndLfu(LfuCachedEntry cachedEntry, int value) {
        removeFromFrequencyMap(cachedEntry);
        cachedEntry.value = value;
        cachedEntry.freq++;
        addToFrequencyMap(cachedEntry);
    }

    private void removeLfuEntry() {
        LinkedHashSet<LfuCachedEntry> lfuCachedEntries = frequencyMap.get(minFrequency);
        LfuCachedEntry cachedEntry = lfuCachedEntries.removeFirst();
        cache.remove(cachedEntry.key);
        if (lfuCachedEntries.isEmpty()) {
            frequencyMap.remove(minFrequency);
        }
    }

    private void addToCache(LfuCachedEntry cachedEntry) {
        cache.put(cachedEntry.key, cachedEntry);
        addToFrequencyMap(cachedEntry);
    }
}
