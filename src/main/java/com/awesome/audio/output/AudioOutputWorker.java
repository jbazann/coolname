package com.awesome.audio.output;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.awesome.threading.ThreadingWorker;

public final class AudioOutputWorker extends ThreadingWorker {

    private volatile static AudioOutputWorker instance;

    public static AudioOutputWorker getInstance() {
        if( instance == null ) 
        {
            synchronized(AudioOutputWorker.class)
            {
                instance = instance != null ? instance : new AudioOutputWorker();
            }
        }
        return instance;
    }

    private final ConcurrentLinkedQueue<AudioOutputTask> pendingTasks;

    public AudioOutputWorker() {
        super("Audio Output");
        this.pendingTasks = new ConcurrentLinkedQueue<AudioOutputTask>();
    }

    public boolean addTask(AudioOutputTask task) {
        return pendingTasks.offer(task);
    }

    @Override
    public void runIteration() {
        
        if( pendingTasks.isEmpty() ) return;

        try{
            pendingTasks.poll().perform();
        }catch(LineUnavailableException e){
            e.printStackTrace(); //TODO Exceptions
        }catch(IOException e){
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void _start() {
        this.setThrottleFrequency(10, true);
        return;
    }

    @Override
    protected void _kill() { 
        // do something with pending tasks
        return;
    }
    
}
