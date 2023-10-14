package com.awesome.audio.input;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

import com.awesome.filesys.json.JsonWrapper;
import com.awesome.filesys.json.JsonFiles;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

// TODO bad stuff happens if config file changes between controller initializations
public final class AudioInputConfig {

    private AudioInputMode inputMode = AudioInputMode.FILE_WAV;

    // wav sample rate
    private int sampleRate = 44100;

    // buffer one second's worth of samples
    private int bufferSize = 44100;

    // 25 sample runs per second
    private long sampleRunLength = 44100 / 25;

    // sleep for half the required interval to reach the sample rate with the chosen sample run length
    private long sleepMillis = 1000 / (2 * 25);
    private int sleepNanos = 0;

    /**
     * Reads {@code input.cfg} and loads a new instance of this class with the found values.
     * @return an instance of {@code AudioInputConfig} exposing the values loaded from the config
     * file at the time this method was called.
     */
    public static AudioInputConfig getCurrentConfig() {

        Path filePath = JsonFiles.AudioInputConfig.createAndGet();

        JsonWrapper<AudioInputConfig> parser = new JsonWrapper<AudioInputConfig>(
            AudioInputConfig.class.toString(),
            new AudioInputConfig(),
            AudioInputConfig.class,
            filePath.toFile()
            );


        try{
            parser.read();
        }catch(JsonGenerationException e){// TODO exceptions
            e.printStackTrace();
        }catch(JsonMappingException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        return parser.getWrapped();

    }

    public enum AudioInputMode {

        RANDOM( () -> null ),
        RANDOM_SMOOTH( () -> null ),
        FILE_WAV( () -> null),
        ;

        private final Supplier<AudioInputWorker> controllerSupplier;
        private AudioInputMode(Supplier<AudioInputWorker> controllerBuilderFunction) {
            this.controllerSupplier = controllerBuilderFunction;
        }

        public AudioInputWorker getWorker() {
            return controllerSupplier.get();
        }
    }

    public AudioInputMode getInputMode() {
        return inputMode;
    }

    public void setInputMode(AudioInputMode inputMode) {
        this.inputMode = inputMode;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public long getSampleRunLength() {
        return sampleRunLength;
    }

    public void setSampleRunLength(long sampleRunLength) {
        this.sampleRunLength = sampleRunLength;
    }

    public long getSleepMillis() {
        return sleepMillis;
    }

    public void setSleepMillis(long sleepMillis) {
        this.sleepMillis = sleepMillis;
    }

    public int getSleepNanos() {
        return sleepNanos;
    }

    public void setSleepNanos(int sleepNanos) {
        this.sleepNanos = sleepNanos;
    }

}
