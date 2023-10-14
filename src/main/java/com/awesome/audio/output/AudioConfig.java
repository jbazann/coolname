package com.awesome.audio.output;

import java.io.IOException;
import java.nio.file.Path;

import com.awesome.filesys.json.JsonFiles;
import com.awesome.filesys.json.JsonWrapper;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class AudioConfig {
    
    private String sourceLineName;

    private String targetLineName;

    private String mixerName;

    /**
     * Reads {@code input.cfg} and loads a new instance of this class with the found values.
     * @return an instance of {@code AudioInputConfig} exposing the values loaded from the config
     * file at the time this method was called.
     */
    public static AudioConfig getCurrentConfig() {

        Path filePath = JsonFiles.AudioInputConfig.createAndGet();

        JsonWrapper<AudioConfig> parser = new JsonWrapper<AudioConfig>(
            AudioConfig.class.toString(),
            new AudioConfig(),
            AudioConfig.class,
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

    public String getSourceLineName() {
        return sourceLineName;
    }

    public void setSourceLineName(String sourceLineName) {
        this.sourceLineName = sourceLineName;
    }

    public String getTargetLineName() {
        return targetLineName;
    }

    public void setTargetLineName(String targetLineName) {
        this.targetLineName = targetLineName;
    }

    public String getMixerName() {
        return mixerName;
    }

    public void setMixerName(String mixerName) {
        this.mixerName = mixerName;
    }

}
