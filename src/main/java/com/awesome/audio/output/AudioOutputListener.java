package com.awesome.audio.output;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import com.awesome.audio.AudioListener;

public final class AudioOutputListener {

    private final LinkedList<AudioListener> listeners;

    public AudioOutputListener() {
        this.listeners = new LinkedList<AudioListener>();
    }

    public void update(final byte[] bufferArray, final int bytesRead) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bufferArray).order(ByteOrder.BIG_ENDIAN);
        final short[] shortArray = new short[bytesRead/Short.BYTES];

        for(int i = 0; i < bytesRead/Short.BYTES;i++) {
            shortArray[i] = byteBuffer.getShort();
        }

        listeners.forEach(l -> l.update(shortArray, bytesRead/Short.BYTES));
    }

    public void addListener(final AudioListener listener) {
        if( ! listeners.contains(listener) ) listeners.add(listener);
    }

    public void removeListener(final AudioListener listener) {
        listeners.remove(listener);
    }
    
}
