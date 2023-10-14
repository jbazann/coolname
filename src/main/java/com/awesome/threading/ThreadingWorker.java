package com.awesome.threading;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class ThreadingWorker extends ThreadingThreadController {

    public final AtomicBoolean shouldRun;
    private Optional<Thread> runningThread;
    protected int throttleFrequency;
    protected long lastThrottleCheck;
    private long sleepMillis;
    private int sleepNanos;

    public ThreadingWorker(String name) {
        super(name);
        shouldRun = new AtomicBoolean(true);
        runningThread = Optional.empty();
        throttleFrequency = 0;
        lastThrottleCheck = 0;
        sleepMillis = 0;
        sleepNanos = 0;
    }

    @Override
    public void run() {
        while( this.shouldRun.get() )
        {
            if( this.throttle() ) this.runIteration();
            this.throttle(0);
        }
    }

    public String getThreadName() {
        if( ! runningThread.isPresent() ) return "None";
        return runningThread.get().getName();
    }

    /**
     * Check whether enough time has passed since the last call to this method
     * to maintain a frequency equal or lower than set through {@code setThrottleFrequency()}.
     * @return true if this worker should proceed with its operations.
     */
    protected boolean throttle() {
        if( throttleFrequency == 0) return true; // unset throttle freq
        long now = System.nanoTime();
        if( 1_000_000_000 / throttleFrequency < now - lastThrottleCheck) {
            lastThrottleCheck = now;
            return true;
        }
        return false;
    }

    /**
     * Set the amount of times per second this worker should operate. Setting this
     * to zero or less means unlimited frequency. Invoking this method with a 
     * value greater than zero and {@code updateSleep = true} will set
     * the default sleep interval of {@code throttle(0)} to half the period
     * of the given frequency.
     * @param freq
     * @param updateSleep
     */
    public void setThrottleFrequency(int freq, boolean updateSleep) {
        throttleFrequency = 0;
        if( freq > 0 ) throttleFrequency = freq;

        if(updateSleep && throttleFrequency > 0)
        {
            final int totalNanos = 1_000_000_000 / throttleFrequency;
            final int millis = totalNanos / 2_000_000;
            setDefaultSleepThrottle(millis, (totalNanos - millis*2_000_000) / 2 );
        }
    }

    /**
     * Set the default amount of time this worker will sleep between calls to
     * {@code runIteration()} or when manually invoking {@code throttle(0)}. 
     * @param millis
     * @param nanos
     */
    public void setDefaultSleepThrottle(long millis, int nanos) {
        this.sleepMillis = millis;
        this.sleepNanos = nanos;
    }

    protected void throttle(long sleep) {
        try{
            if(sleep == 0) {
                Thread.sleep(this.sleepMillis,this.sleepNanos);
            }else{
                Thread.sleep(sleep);
            }
        }catch(Exception e){
            e.printStackTrace();
        } // TODO exceptions
        
    }

}
