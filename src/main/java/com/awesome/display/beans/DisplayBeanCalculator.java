package com.awesome.display.beans;

import java.util.Arrays;
import java.util.HashMap;

public class DisplayBeanCalculator {

    public static final int DONT_QUESTION_THESE_I_JUST_WANTED_TO_USE_BIT_FLAGS = 0;
    public static final int TRIM_SOURCE_TAIL = 1;
    public static final int TRIM_SOURCE_HEAD = 2;
    public static final int FILL_SOURCE_TAIL = 4;
    public static final int FILL_SOURCE_HEAD = 8;
    public static final int FIND_EXCUSES_TO_WRITE_MORE_FLAGS_EVENTUALLY = 69;

    public final double MAX_Y;
    public final double MIN_Y;
    public final double MIN_X;
    public final double MAX_X;

    protected final HashMap<Metadata,Object> metadata;

    public enum Metadata {
        CHANNELS,
        MAX_UNNORMALIZED_Y,
        ;
    }

    protected DisplayBeanCalculator() {
        this.metadata = new HashMap<Metadata,Object>();
        this.MAX_Y = 1d;
        this.MIN_Y = -1d;
        this.MIN_X = 0d;
        this.MAX_X = 1d;
    }

    protected DisplayBeanCalculator(final double MAX_Y,
        final double MIN_Y,final double MIN_X,final double MAX_X) {
        this.metadata = new HashMap<Metadata,Object>();
        this.MAX_Y = MAX_Y;
        this.MIN_Y = MIN_Y;
        this.MIN_X = MIN_X;
        this.MAX_X = MAX_X;
    }
    
    protected double[] calculate(final double[] target,final double[] source) {

        int i = 0;
        while(i*2 < target.length)
        {
            target[i] = MIN_Y;
            target[i+1] = MAX_Y;
        }
        return target;

    }

    protected double[] calculate(final double[] target,
        double[] source, final int samples, final int flags) {
        if( (flags & TRIM_SOURCE_TAIL) == TRIM_SOURCE_TAIL )
        {
            if(source.length > samples) source = Arrays.copyOf(source, samples);
        }
        if( (flags & TRIM_SOURCE_HEAD) == TRIM_SOURCE_HEAD )
        {
            if(source.length > samples) source = Arrays.copyOfRange(source, source.length - samples, source.length);
        }
        if( (flags & FILL_SOURCE_TAIL) == FILL_SOURCE_TAIL )
        {
            if(source.length < samples) source = Arrays.copyOf(source, samples);
        }
        if( (flags & FILL_SOURCE_HEAD) == FILL_SOURCE_HEAD )
        {
            if(source.length < samples)
            {
                double[] newsource = new double[samples];
                int i = samples - source.length;
                Arrays.fill(newsource, 0, i, 0d);
                while(i < samples) newsource[i] = source[i++ - samples + source.length]; // sorry :3
                source = newsource;
            }
        }

        return this.calculate(target, source);
    }

    protected void metadata(Metadata property,Object value) {//TODO param checks
        this.metadata.put(property, value);
    }

}
