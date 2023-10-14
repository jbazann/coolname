package com.awesome.audio.input;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;

import com.awesome.filesys.AudioFiles;

public enum AudioInputFileFormat {
        WAVE(AudioFileFormat.Type.WAVE),
        ;

        private final AudioFileFormat.Type format;

        private AudioInputFileFormat(AudioFileFormat.Type format) {
            this.format = format;
        }

        public AudioInputStreamWrapper open(AudioFiles file) throws UnsupportedAudioFileException, IOException {
            String ext = file.getExtension();
            System.out.println(format.getExtension());
            if( ! format.getExtension().equalsIgnoreCase(ext) ) throw new UnsupportedAudioFileException(); // TODO review exception

            File input = file.getPath().toFile();
            AudioInputStreamWrapper wrapper = new AudioInputStreamWrapper();
            wrapper.file = file;
            wrapper.stream = AudioSystem.getAudioInputStream(input);

            return wrapper;
        }

        public class AudioInputStreamWrapper {

            private AudioFiles file;
            private AudioInputStream stream;

                
            public float getFrameRate() {
                return stream.getFormat().getFrameRate();
            }

            public int getFrameSize() {
                return stream.getFormat().getFrameSize();
            }

                
            public Buffer getBuffer() {
                Buffer type;
                long len = stream.getFrameLength();
                
                type = BufferUtils.createShortBuffer(1);
                if( len == Integer.BYTES ) type = BufferUtils.createIntBuffer(1);
                if( len == Long.BYTES ) type = BufferUtils.createLongBuffer(1);

                return type;
            }

            public int read(byte[] inbuffer, int amount) throws IOException {
                return stream.readNBytes(inbuffer, 0, amount);
            }

            public void close() throws IOException {
                stream.close();
            }

            public boolean reset() {
                File input = file.getPath().toFile();
                try{
                    this.stream = AudioSystem.getAudioInputStream(input);
                    return true;
                }catch(UnsupportedAudioFileException e){
                    //TODO exceptions
                    return false;
                }catch(IOException e){
                    return false;
                }
            }


        }


    }
