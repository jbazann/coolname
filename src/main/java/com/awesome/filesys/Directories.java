package com.awesome.filesys;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public enum Directories {
    ROOT("\\"),
    CONFIG("config\\",ROOT),
    DEMO("demo\\",ROOT),
    ;


    private final String path;
    private final Optional<Directories> parent;

    private Directories(String path) {
        this.path = path;
        this.parent = Optional.empty();
    }

    private Directories(String path,Directories parent) {
        this.path = path;
        this.parent = parent != null ? Optional.of(parent) : Optional.empty();
    }

    public void create() {
        try{

            Files.createDirectories(Path.of(this.getFullPath()), new FileAttribute[0]);

        }catch(FileAlreadyExistsException e) {
            return;//TODO exceptions
        }catch(JsonGenerationException e){// TODO exceptions
            e.printStackTrace();
        }catch(JsonMappingException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getLocalPath() {
        if( this.parent.isEmpty() ) return this.path;
        return this.parent.get().getLocalPath() + this.path;
    }

    public String getFullPath() {
        String cwd = System.getProperty("user.dir");
        return cwd + getLocalPath();
    }

}
