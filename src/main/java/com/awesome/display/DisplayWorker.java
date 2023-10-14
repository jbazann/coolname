package com.awesome.display;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.awesome.audio.AudioListener;
import com.awesome.display.beans.DisplayBean;
import com.awesome.threading.ThreadingMonitor;
import com.awesome.threading.ThreadingTimers;
import com.awesome.threading.ThreadingWorker;

public class DisplayWorker extends ThreadingWorker {

    private volatile static DisplayWorker instance;

    public static DisplayWorker getInstance() {
        if( instance == null ) 
        {
            synchronized(DisplayWorker.class)
            {
                instance = instance != null ? instance : new DisplayWorker();
            }
        }
        return instance;
    }

    private final ConcurrentLinkedQueue<Double> buffer;

    private final ArrayDeque<Double> sourceQueue;

    private final CopyOnWriteArrayList<DisplayBean> subscribers;

    private final int bufferedSamples = 44100; // TODO cfg

    private final int channels = 2;

    private final ThreadingMonitor monitor;

    public DisplayWorker() {
        super("DisplayWorker");
        this.subscribers = new CopyOnWriteArrayList<DisplayBean>();
        this.monitor = new ThreadingMonitor(ThreadingTimers.GLFW,0,"Display worker");

        this.buffer = new ConcurrentLinkedQueue<Double>();
        this.sourceQueue = new ArrayDeque<Double>(bufferedSamples * channels);
    }

    @Override
    public void runIteration() {

        int i = 0;
        Double d = 0d;
        while(i++ < bufferedSamples * channels && (d = buffer.poll()) != null) sourceQueue.add(d);

        final double[] source = sourceQueue.stream().mapToDouble(Double::doubleValue).toArray();

        subscribers.forEach(bean -> bean.calculate(source));
        monitor.count();

    }

    @Override
    protected void _kill() {
        //stop monitor
    }

    @Override
    protected void _start() {
        setThrottleFrequency(60, true); //TODO CFG
        monitor.start();

        DisplayFacade.getInstance().addAudioListener((new AudioListener() {
            
            private ConcurrentLinkedQueue<Double> queue;
            
            @Override
            public void update(short[] data, int samplesRead) {
                int i = 0;
                while(i < samplesRead) queue.offer((double)data[i++]);
            } 

            public AudioListener setQueue(ConcurrentLinkedQueue<Double> q) {
                this.queue = q;
                return this;
            }

        }).setQueue(buffer));

    }

    public void subscribeBean(DisplayBean bean) {
        subscribers.add(bean);
    } 

}
