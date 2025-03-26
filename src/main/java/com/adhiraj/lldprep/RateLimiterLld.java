package com.adhiraj.lldprep;

import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

public class RateLimiterLld {
    public static void main(String[] args) {

    }
}

interface RateLimitStrategy {

    boolean isAllowed(String clientId);
}
@AllArgsConstructor
class TokenBucket {

    int availableTokens;
    long lastRefillTime;
    void refill(int tokensToFill, long durationInMillis) {
        long now = System.currentTimeMillis();
        if (now - lastRefillTime < durationInMillis) return;
        lastRefillTime = now;
        availableTokens = tokensToFill;
    }

}
@AllArgsConstructor
class TokenBucketStrategy implements RateLimitStrategy{

    int tokensToFill;
    long durationInMillis;
    Map<String, TokenBucket> clientMap;

    @Override
    public boolean isAllowed(String clientId) {
        TokenBucket bucket = clientMap.computeIfAbsent(clientId, ignore -> new TokenBucket(tokensToFill, System.currentTimeMillis()));
        bucket.refill(tokensToFill, durationInMillis);
        if (bucket.availableTokens <= 0) return false;
        bucket.availableTokens--;
        return true;
    }
}

@AllArgsConstructor
class LeakyBucketStrategy implements RateLimitStrategy {
    int capacity;
    Queue<Object> sharedBuffer;

    @Override
    public boolean isAllowed(String clientId) {
        if (sharedBuffer.size() >= capacity) return false;
        sharedBuffer.offer(new Object());
        return true;
    }
}

@AllArgsConstructor
class FixedWindowCounter {
    // TODO Encapsulate both attributes into separate model as CurrentWindow.
    //  Can be used for Sliding window as well.
    long currWindowStartInMillis;
    int currentWindowCounter;

    void updateWindow(long windowSizeInMillis) {
        long now = System.currentTimeMillis();
        if (now - currWindowStartInMillis <= windowSizeInMillis) return;
        currWindowStartInMillis = now;
        currentWindowCounter = 0;
    }
}

@AllArgsConstructor
class FixedWindowCounterStrategy implements RateLimitStrategy {
    // TODO Encapsulate as WindowConfig to be used by all the WindowStrategies
    long windowSizeInMillis;
    int maxRequestInWindow;
    Map<String, FixedWindowCounter> windowMap;

    @Override
    public boolean isAllowed(String clientId) {
        FixedWindowCounter window = windowMap.computeIfAbsent(clientId, ignore -> new FixedWindowCounter(System.currentTimeMillis(), 0));
        window.updateWindow(windowSizeInMillis);
        if (window.currentWindowCounter >= maxRequestInWindow) return false;
        window.currentWindowCounter++;
        return true;
    }
}

@AllArgsConstructor
class SlidingWindowLogStrategy implements RateLimitStrategy {
    long windowSizeInMillis;
    int maxRequestInWindow;
    Map<String, Set<Long>> windowMap;

    @Override
    public boolean isAllowed(String clientId) {
        long now = System.currentTimeMillis();
        Set<Long> log = updateLog(clientId, now, windowSizeInMillis);
        if (log.size() >= maxRequestInWindow) return false;
        log.add(now);
        return true;
    }

    private Set<Long> updateLog(String clientId, long now, long windowSizeInMillis) {
        Set<Long> log = windowMap.computeIfAbsent(clientId, k -> new HashSet<>());
        Set<Long> filteredLog = log.stream().filter(ts -> now - ts <= windowSizeInMillis).collect(Collectors.toSet());
        if (log.size() == filteredLog.size()) {
            log = filteredLog;
            windowMap.put(clientId, log);
        }
        return log;
    }
}

@AllArgsConstructor
class SlidingWindowCounter {
    int prevWindowCounter;
    int currentWindowCounter;
    long currWindowStartInMillis;

    void updateWindow(long nowInMillis, long windowSizeInMillis) {
        long timeDiffInMillis = nowInMillis - currWindowStartInMillis;
        if (timeDiffInMillis <= windowSizeInMillis) return;
        if (timeDiffInMillis > 3 * windowSizeInMillis) {
            prevWindowCounter = 0;
        } else if (timeDiffInMillis > 2 * windowSizeInMillis) {
            prevWindowCounter = currentWindowCounter;
        }
        currentWindowCounter = 0;
        currWindowStartInMillis = nowInMillis;
    }

    int getCount(long now, long windowSizeInMillis) {
        int prevWindowWeight = (int) (1 - (now - currWindowStartInMillis) / windowSizeInMillis);
        return prevWindowWeight * prevWindowCounter + currentWindowCounter;
    }
}

@AllArgsConstructor
class SlidingWindowCounterStrategy implements RateLimitStrategy{
    long windowSizeInMillis;
    int maxRequestInWindow;
    Map<String, SlidingWindowCounter> windowMap;

    @Override
    public boolean isAllowed(String clientId) {
        SlidingWindowCounter counter = windowMap.get(clientId);
        long nowInMillis = System.currentTimeMillis();
        counter.updateWindow(nowInMillis, windowSizeInMillis);
        if (counter.getCount(nowInMillis, windowSizeInMillis) >= maxRequestInWindow) return false;
        counter.currentWindowCounter++;
        return true;
    }
}

@AllArgsConstructor
class RateLimiter {
    RateLimitStrategy strategy; // strategy pattern

    boolean isAllowed(String clientId) {
        return strategy.isAllowed(clientId);
    }
}
