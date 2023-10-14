package com.awesome.audio.output;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioOutputTaskClip extends AudioOutputTask {

    private volatile int loops = 0;
    private volatile boolean shouldRun = true;

    public AudioOutputTaskClip(final AudioOutputListener listener) {
        super(listener);
    }

    @Override
    protected void perform() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        if( ! this.parameters.containsKey(AudioOutputTask.Parameters.CLIP_LOOPING) ) return;//TODO EXCEPTIONS
        if( ! this.parameters.containsKey(AudioOutputTask.Parameters.INPUT_FILE) ) return;
        if( ! this.parameters.containsKey(AudioOutputTask.Parameters.AUDIO_FORMAT) ) return;
        
        this.loops = (int) this.parameters.get(AudioOutputTask.Parameters.CLIP_LOOPING);
        final File file = (File) this.parameters.get(AudioOutputTask.Parameters.INPUT_FILE);
        final AudioFormat format = (AudioFormat) this.parameters.get(AudioOutputTask.Parameters.AUDIO_FORMAT);
        final SourceDataLine sdl = (SourceDataLine) AudioSystem.getLine(new Line.Info(SourceDataLine.class));

        sdl.open(format,(44100/60)*Short.BYTES*2);
        sdl.start();
        final byte[] buffer = new byte[sdl.getBufferSize()];
        int loopCount = 0;
        int bytesRead = 69;

        do{
            if( loops == Clip.LOOP_CONTINUOUSLY ) loopCount = -69;
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);

            while( (bytesRead = ais.read(buffer, 0, buffer.length)) > 0 && this.shouldRun ) {
                listener.update(buffer, bytesRead);
                sdl.write(buffer, 0, bytesRead);
            }
        }while( ++loopCount < loops );
    }

    @Override
    protected void kill() {
        this.loops = 0;
        this.shouldRun = false;
    }

}
