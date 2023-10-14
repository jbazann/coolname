package com.awesome.display.beans;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32;

import com.awesome.display.beans.DisplayBeanCalculator.Metadata;
import com.awesome.display.utils.ConcurrentArraySupplier;

public class DisplayRawAudioBean extends DisplayBean {

    private final int visibleSampleSize;
    private final DoubleBuffer verticesBuffer;
    private final ConcurrentArraySupplier arraySupplier;

    private final DisplayBeanCalculatorDrawRawAudioSteps draw;
    private final DisplayBeanCalculatorChannelAdder addChannels;

    public DisplayRawAudioBean(int visibleSampleSize, int channels, double maxSampleAmplitude) {
        super(1,-1,0,1);
        this.visibleSampleSize = visibleSampleSize;

        this.addChannels = new DisplayBeanCalculatorChannelAdder();
        this.draw = new DisplayBeanCalculatorDrawRawAudioSteps(DRAW_RANGE_TOP,DRAW_RANGE_BOT,DRAW_RANGE_LEFT,DRAW_RANGE_RIGHT);
        
        this.addChannels.metadata(Metadata.CHANNELS, channels);
        this.draw.metadata(Metadata.MAX_UNNORMALIZED_Y,maxSampleAmplitude);

        this.verticesBuffer = BufferUtils.createDoubleBuffer(draw.targetSizeFor(visibleSampleSize));
        this.arraySupplier = new ConcurrentArraySupplier(draw.targetSizeFor(visibleSampleSize));
    }

    @Override
    protected void _start() {
        GL32.glVertexAttribPointer(0, 2, GL32.GL_DOUBLE, false, 0, 0);
        GL32.glEnableVertexAttribArray(0);
    }
    @Override
    public void _draw() {
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
    protected void _end() {
        return; // no further actions needed in current version.
    }

    /**
     * Builds a vertex array to draw {@code data}. This implementation does so
     * complying with the following structural constraint: 
     * <p>
     * {@code < x0, y0, x1, y1, x2, y2, ... >}
     * <p>
     * Where a point {@code p0 = (x0, y0)} represents the start of a line, {@code p1} represents
     * the end of said line, {@code p2} represents the start of the next line, and so on. 
     * Values are also be normalized within the range {@code [0, 1]} for the <em>x</em> axis, 
     * and {@code [-1, 1]} for the <em>y</em> axis.
     */
    @Override
    public void calculate(double[] data) {

        double[] nextVertexArray = arraySupplier.getNewArray();

        double[] addedData = addChannels.calculate(null, data, visibleSampleSize * addChannels.getChannels(), 
            DisplayBeanCalculator.FILL_SOURCE_HEAD | DisplayBeanCalculator.TRIM_SOURCE_HEAD);

        arraySupplier.postLatestArray(draw.calculate(nextVertexArray, addedData));

    }
}
