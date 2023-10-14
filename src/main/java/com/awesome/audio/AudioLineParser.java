package com.awesome.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public final class AudioLineParser {
    

    public LineInfoPair findLine(String name) {
        for(Mixer.Info minfo : AudioSystem.getMixerInfo())
        {
            Mixer m = AudioSystem.getMixer(minfo);
            for(Line.Info linfo : m.getSourceLineInfo()) {
                if( name.equals(minfo.getName()+linfo) ) return new LineInfoPair(minfo, linfo);
            }
            for(Line.Info linfo : m.getTargetLineInfo()) {
                if( name.equals(minfo.getName()+linfo) ) return new LineInfoPair(minfo, linfo);;
            }
        }
        return null;// TODO rethink this
    }

    public class LineInfoPair {

        public Mixer.Info mixer;
        public Line.Info line;

        public LineInfoPair(Mixer.Info mixer, Line.Info line) {
            this.mixer = mixer;
            this.line = line;
        }

    }


}
