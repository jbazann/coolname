package com.awesome.filesys.json;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import com.awesome.filesys.Directories;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public enum JsonFiles {

    AudioInputConfig("audioinput.json",Directories.CONFIG),
    ;

    public final String name;
    public final Directories dir;
    public final Path path;

    private JsonFiles(String name, Directories dir) {
        this.name = name;
        this.dir = dir;
        this.path = Path.of(this.getFullPath());
    }

    public Path createAndGet() {
        this.create();
        return this.path;
    }

    private void create() {
        try{

            dir.create();
            Files.createFile(this.path, new FileAttribute[0]);

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

    private String getFullPath() {
        return dir.getFullPath() + this.name;
    }

}
