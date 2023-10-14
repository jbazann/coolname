package com.awesome.threading;

import java.util.concurrent.atomic.AtomicLong;

// TODO document this class
public final class ThreadingMonitor extends ThreadingThreadController {

    private AtomicLong counter;
    private final ThreadingTimers timer;
    private volatile long currentRate;
    private volatile int interval;
    private volatile String message;

    //TODO null checks
    public ThreadingMonitor(ThreadingTimers timer, int waitMillis, String name) {
        super(name);
        this.counter = new AtomicLong(0);
        this.timer = timer;
        this.interval = ( waitMillis <= 0 ) ? 
            ThreadingConstants.MONITOR_SLEEP_MILLIS : waitMillis;
        this.message = ( name == "" ) ? "Unnamed monitor" : name;
    }

    public void count() {
        counter.incrementAndGet();
    }

    public long gatCurrentRate() {
        return currentRate;
    }

    public ThreadingTimers getTimer() {
        return timer;
    }

    public long getCurrentRate() {
        return currentRate;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void runIteration() {

        try{

            Thread.sleep(interval);

        }catch(Exception e){
            e.printStackTrace();
        } // TODO unswallow this

        currentRate = Math.round(counter.getAndSet(0) / timer.getSeconds());
        System.out.println(message + " rate: " + String.valueOf(currentRate));

    }

    @Override
    protected void _start() {
        return;
    }

    @Override
    protected void _kill() {
        return;
    }
}
