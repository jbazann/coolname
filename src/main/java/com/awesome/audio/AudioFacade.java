package com.awesome.audio;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;

import com.awesome.audio.output.AudioOutputListener;
import com.awesome.audio.output.AudioOutputTask;
import com.awesome.audio.output.AudioOutputTaskClip;
import com.awesome.audio.output.AudioOutputWorker;
import com.awesome.filesys.AudioFiles;

public final class AudioFacade {
    
    private volatile static AudioFacade instance;

    public static AudioFacade getInstance() {
        
        if( instance == null ) 
        {
            synchronized(AudioFacade.class)
            {
                instance = instance != null ? instance : new AudioFacade();
            }
        }
        return instance;

    }

    private AudioFacade() {


    }

    
    public void start() {

        AudioOutputWorker.getInstance().start();

    }

    public void play(AudioListener l) {
        
        AudioOutputListener listener = new AudioOutputListener();

        listener.addListener(l);

        AudioOutputWorker.getInstance().addTask(
            new AudioOutputTaskClip(listener)
                .addParameter(
                    AudioOutputTask.Parameters.CLIP_LOOPING, Clip.LOOP_CONTINUOUSLY
                ).addParameter(
                    AudioOutputTask.Parameters.INPUT_FILE, AudioFiles.PIM.getFile()
                ).addParameter(
                    AudioOutputTask.Parameters.AUDIO_FORMAT, new AudioFormat(44100f, 16, 2, false, false)
                )
        );

    }

}
