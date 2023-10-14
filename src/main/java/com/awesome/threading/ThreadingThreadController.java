package com.awesome.threading;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

//TODO document
public abstract class ThreadingThreadController implements Runnable {

    private static final int threadKillTimeout = 1000; // TODO cfg

    protected AtomicReference<Thread> thread;

    protected AtomicBoolean shouldRun;

    private String name;
    
    protected ThreadingThreadController(String name) {
        this.name = (name == null) ? "Unnamed" : name;
        this.shouldRun = new AtomicBoolean(true);
        this.thread = new AtomicReference<Thread>(); // :s
    }

    @Override
    public void run() {
        while( this.shouldRun.get() )
        {
            this.runIteration();
        }
    }
    
    public abstract void runIteration();

    public synchronized void start() {
        if( ! this.shouldRun.get() ) throw new IllegalStateException("ThreadController wasn't restored");
        this._start();
        this.getThread().start();
    }

    protected abstract void _start();

    public synchronized void kill() throws TimeoutException, InterruptedException {

        Thread thread = this.getThread();
        if( ! thread.isAlive() ) return;

        // perform kill operations
        this.shouldRun.set(false);
        this._kill();
        thread.join( threadKillTimeout );
        if( thread.isAlive() ) 
            throw new TimeoutException("Failed to kill "+thread.getName());

        // prepare this to run again just in case
        this.thread.set(null); // :s
        this.shouldRun.set(true);

    }

    protected abstract void _kill();

    /**
     * Get or create this object's Thread.
     */
    private synchronized Thread getThread() {
     
        if( this.thread.get() == null ) 
        {
            this.thread.set( new Thread(this,name) ); 
        }
        
        return this.thread.get();
    }

}
