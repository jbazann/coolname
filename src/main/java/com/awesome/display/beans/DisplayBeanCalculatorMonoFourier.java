package com.awesome.display.beans;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.hipparchus.complex.Complex;
import org.hipparchus.transform.DftNormalization;
import org.hipparchus.transform.FastFourierTransformer;
import org.hipparchus.transform.TransformType;

public class DisplayBeanCalculatorMonoFourier extends DisplayBeanCalculator {

    private final LinkedList<Double> bufferList = new LinkedList<Double>(); 
    private final FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
    private double[] buffer = new double[1];
    private double[] localTarget = new double[1];

    protected DisplayBeanCalculatorMonoFourier() {
        super();
    }

    /**
     * Computes fft for values in {@code source} and returns a private array
     * regardless of the value of {@code target}. This array is meant to be
     * passed as source to other {@code DisplayBeanCalculator} instances, and
     * should be released before calling this method again on the same instance.
     */
    @Override
    protected double[] calculate(final double[] target,final double[] source) {

        // fft wants size to be a power of two
        int powerOfTwo = 0; 
        while(Math.pow(2, powerOfTwo) < source.length) powerOfTwo++;

        // update local buffers
        if(buffer.length != source.length) buffer = new double[source.length];
        if(localTarget.length != source.length) localTarget = new double[source.length];
        bufferList.clear();

        // load samples into the fft dedicated buffer
        for(int i = 0;i < source.length;i++) buffer[i] = source[i];

        // do the thing
        Complex[] complexData = fft.transform(buffer, TransformType.FORWARD);

        // process the complex data into frequency magnitude values
        Stream.of(complexData)
            .mapToDouble(c -> c.norm())
            .forEach(xd -> bufferList.addLast(xd));// TODO review this vs collectors

        // load target with results
        Iterator<Double> it = bufferList.iterator();
        for(int i = 0;i < source.length;i++) localTarget[i] = it.next();

        return localTarget;

    }

}
