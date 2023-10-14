package com.awesome.filesys;

import java.io.File;
import java.nio.file.Path;

public enum AudioFiles {
    
    SINEWAVE("sinewave.wav",Directories.DEMO),
    PIM("pim.wav",Directories.DEMO),
    VORTEX("vortex.wav",Directories.DEMO),
    SAMMY("sammy.wav",Directories.DEMO),
    ;

    public final String name;
    public final Directories dir;
    public final Path path;

    private AudioFiles(String name, Directories dir) {
        this.name = name;
        this.dir = dir;
        this.path = Path.of(this.getFullPath());

        dir.create();
    }

    private String getFullPath() {
        return dir.getFullPath() + this.name;
    }

    public String getExtension() {
        String p = this.path.toString();
        int index = p.lastIndexOf(".") + 1;
        return p.substring(index < 0 ? 0 : index);
    }

    public Path getPath() {
        return path;
    }

    public File getFile() {
        return path.toFile();
    }

}
