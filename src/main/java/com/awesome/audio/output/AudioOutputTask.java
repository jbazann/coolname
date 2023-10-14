package com.awesome.audio.output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class AudioOutputTask {

    public enum Parameters {
        CLIP_LOOPING(1),
        INPUT_FILE(),
        AUDIO_FORMAT(),
        ;

        public Optional<Object> defaultValue;

        private Parameters() {
            this.defaultValue = Optional.empty();
        }

        private Parameters(final Object defaultValue) {
            this.defaultValue = Optional.ofNullable(defaultValue);
        }

    }

    protected final HashMap<Parameters,Object> parameters;
    protected final AudioOutputListener listener;

    public AudioOutputTask(final AudioOutputListener listener) {
        this.listener = listener;
        this.parameters = new HashMap<Parameters,Object>();
    }

    public AudioOutputTask addParameter(final Parameters p, final Object value) {
        this.parameters.put(p, value != null ? value : p.defaultValue.get());
        return this;
    }

    protected abstract void perform() throws LineUnavailableException, IOException, UnsupportedAudioFileException;

    protected abstract void kill();

}
