package com.awesome.display.beans;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32;

import com.awesome.display.beans.DisplayBeanCalculator.Metadata;
import com.awesome.display.utils.ConcurrentArraySupplier;

public class DisplayFrequencySpectrumBean extends DisplayBean {

    private final int targetSampleSize; 
    private final DoubleBuffer verticesBuffer;
    private final ConcurrentArraySupplier arraySupplier;
    private final DisplayBeanCalculatorChannelAdder addChannels;
    private final DisplayBeanCalculatorMonoFourier fourier;
    private final DisplayBeanCalculatorDrawLogscaleSteps draw;

    public DisplayFrequencySpectrumBean(int sampleSize,int channels,double maxAmplitude) {
        super(1,0,0,1);
        this.targetSampleSize = sampleSize; 

        this.addChannels = new DisplayBeanCalculatorChannelAdder();
        this.fourier = new DisplayBeanCalculatorMonoFourier();
        this.draw = new DisplayBeanCalculatorDrawLogscaleSteps(DRAW_RANGE_TOP,DRAW_RANGE_BOT,DRAW_RANGE_LEFT,DRAW_RANGE_RIGHT);

        this.addChannels.metadata(Metadata.CHANNELS, channels);
        this.draw.metadata(Metadata.MAX_UNNORMALIZED_Y, maxAmplitude);

        this.verticesBuffer = BufferUtils.createDoubleBuffer(draw.targetSizeFor(sampleSize));
        this.arraySupplier = new ConcurrentArraySupplier(draw.targetSizeFor(sampleSize));
    }

    @Override
    protected void _draw() {
        GL32.glBindVertexArray(vao);
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);

        verticesBuffer.clear();
        verticesBuffer.put(arraySupplier.getLatestArray()).flip();
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, verticesBuffer, GL32.GL_DYNAMIC_DRAW);

        GL32.glDrawArrays(GL32.GL_LINES, 0, arraySupplier.length);

        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, 0);
        GL32.glBindVertexArray(0);
    }

    @Override
    protected void _start() {
        GL32.glVertexAttribPointer(0, 2, GL32.GL_DOUBLE, false, 0, 0);
        GL32.glEnableVertexAttribArray(0);
    }

    @Override
    protected void _end() {
        return; // no further actions needed in current version.
    }

    @Override
    public void calculate(double[] data) {

        double[] addedChannels = addChannels.calculate(null, data, targetSampleSize * addChannels.getChannels(), 
            DisplayBeanCalculator.FILL_SOURCE_TAIL | DisplayBeanCalculator.TRIM_SOURCE_HEAD);

        double[] fourierAmplitudes = fourier.calculate(null, addedChannels);    

        arraySupplier.postLatestArray(
                draw.calculate(arraySupplier.getNewArray(), fourierAmplitudes)
            );

    }
    
}
