package com.awesome.audio.input;

import java.util.List;

import com.awesome.threading.ThreadingWorker;

public abstract class AudioInputWorker extends ThreadingWorker {
    
    public AudioInputWorker(String name) {
        super(name);
    }

    /**
     * Read up to {@code n} elements from this worker's buffer.
     * @param n number of elements to attempt to read.
     * @return a {@code List} containing up to, but not necessarily, {@code n} elements.
     */
    protected abstract List<Integer> readBuffer(int n);
}
