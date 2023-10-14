package com.awesome.display.beans;

import java.util.Arrays;

public class DisplayBeanCalculatorChannelAdder extends DisplayBeanCalculator {
    
    private double[] localTarget = new double[1];

    protected DisplayBeanCalculatorChannelAdder() {
        super();
    }

    /**
     * Computes the sum of the values from {@code source} channels according to the
     * {@code Metadata.CHANNELS} value configured on this instance, returns
     * a private array regardless of the value of {@code target}. This array is meant to be
     * passed as source to other {@code DisplayBeanCalculator} instances, and
     * should be released before calling this method again on the same instance.
     */
    protected double[] calculate(final double[] target,final double[] source) {

        if( ! metadata.containsKey(Metadata.CHANNELS) ) return null;//TODO EXCEPTIONS
        final int channels = (int) metadata.get(Metadata.CHANNELS);

        if( localTarget.length < source.length / channels ) localTarget = new double[source.length / channels]; 
        Arrays.fill(localTarget, 0, localTarget.length, 0);

        int i = -1;
        while(++i < source.length) 
            localTarget[i / channels] += source[i];

        return localTarget;

    }

    public int getChannels() {//TODO EXCEPTIONS
        if( ! metadata.containsKey(Metadata.CHANNELS) ) return 0;
        return (int) metadata.get(Metadata.CHANNELS); 
    }


}
