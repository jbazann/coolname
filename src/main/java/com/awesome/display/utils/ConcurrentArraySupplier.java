package com.awesome.display.utils;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

// TODO probably add silent monitors for the array pools to make sure this class actually works
/**
 * This class intends to allow/simulate concurrent use
 * of a primitive array between interface and worker threads,
 * complying with the following constaints:
 * <p> - An interface thread must not be blocked by a worker thread.
 * <p> - A worker thread may update the array at a high frequency, without
 * consuming unnecessary amounts of memory.
 * <p>
 * The intended use case involves only one consumer (UI thread) and one producer (Worker thread).
 * No guarantees are provided outside of these conditions.
 */
public final class ConcurrentArraySupplier {
    /**
     * The array currently visible to the interface. This class
     * must guarantee no other thread has access to arrays 
     * referenced by this attribute.
     */
    private final AtomicReference<double[]> exposedArray;
    /**
     * The most recent array provided through {@code postLatestArray()}, or
     * a zero-initialized one. Only references held by this variable may
     * be acquired by UI threads. When doing so, consumers must atomically
     * replace the reference with {@code zeroArray}, i.e. workers
     * may atomically retrieve this reference with the guarantee that if it's
     * not zeroArray, then it isn't visible to the UI.
     */
    private volatile AtomicReference<double[]> latestArray;
    /**
     * Stack of allocated, but currently unused arrays. Workers may acquire
     * them through {@code getNewArray()}.
     */
    private final ArrayDeque<double[]> arrayPool;
    /**
     * Pool of formerly exposed arrays. Workers may retrieve them.
     */
    private final ConcurrentLinkedQueue<double[]> recyclableArrays;
    /**
     * Placeholder to avoid null references. Methods reading {@code AtomicReference}
     * must atomically replace their contents with this reference, allowing comparison
     * against zeroArray to guarantee a valid read. // TODO wtf does that mean
     */
    private final double[] zeroArray;
    /**
     * The allocated capacity for all arrays used by this instance.
     */
    public final int length;

    /**
     * Create a {@code ConcurrentArraySupplier} providing {@code float} arrays of size {@code s} to
     * exactly one consumer and one producer thread.
     * @param size Array size.
     */
    public ConcurrentArraySupplier(int size) {
        length = size;
        zeroArray = new double[length];
        latestArray = new AtomicReference<double[]>(zeroArray);
        exposedArray = new AtomicReference<double[]>(zeroArray);
        arrayPool = new ArrayDeque<double[]>();
        recyclableArrays = new ConcurrentLinkedQueue<double[]>();

        IntStream.range(0, size)
            .forEach(i -> zeroArray[i] = 0);
    }

    /**
     * Return an array provided through {@code postLatestArray()}, or
     * a zero-initialized one.
     * @return An array only visible to the calling thread.
     */ // TODO this could expose zeroArray, allowing clients to turn it into a non-zeroArray :(
    public double[] getLatestArray() {
        double[] cache = latestArray.getAndSet(zeroArray);
        if( cache == zeroArray ) return exposedArray.get(); // worker outpaced by UI, repeat frame
        
        recyclableArrays.add(exposedArray.getAndSet(cache));

        return exposedArray.get();
    }

    //TODO document or remove
    public double[] peekLatestArray() {
        return latestArray.get();
    }

    /**
     * Provides an array reference from the pool, or allocates a new one
     * if none are available.
     * @return an uninitialized array, or one containing old values.
     */ // TODO checking for .isEmpty() isn't thread safe. It only works with a single worker.
    public double[] getNewArray() {
        return arrayPool.isEmpty() ? new double[length] : arrayPool.pop();
    }

    /**
     * Update the visible array. Callers are expected to guarantee no further
     * changes will be done to the passed array. Ideally, they should lose
     * the reference.
     * @param postArray an updated array ready to be passed to UI threads.
     */
    public void postLatestArray(double[] postArray) {
        if( postArray.length != length ) return; // discard unfit arrays that shouldn't be passed anyway

        // otherwise, commit to updating latestArray
        double[] retrievedArray = latestArray.getAndSet(postArray); 

        // Add unexposed arrays back to the pool. 
        if( retrievedArray != zeroArray ) {
            arrayPool.push(retrievedArray); 
        }
        // Recycle previously exposed arrays.
        while( (retrievedArray = recyclableArrays.poll()) != null) {
            if(retrievedArray != zeroArray) arrayPool.push(retrievedArray);
        }
    }

}
