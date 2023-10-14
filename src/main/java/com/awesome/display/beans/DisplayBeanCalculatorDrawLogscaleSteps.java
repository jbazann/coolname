package com.awesome.display.beans;

public class DisplayBeanCalculatorDrawLogscaleSteps extends DisplayBeanCalculator {

    /**
     * Amount of values stored in target per value found in source.
     */
    private final int SOURCE_TO_TARGET_PROPORTION = 8;

    /**
     * The {@code while} loop in {@code calculate()} covers indexes 0
     * to source.length - 2, so it actually generates {@code sourceToTargetProportion
     * * (source.length - 1)} values in target. 
     * In the current version these extra spaces could be occupied by adding the
     * two lines the algorithm doesn't cover, a starting oblique line from zero,
     * and the last horizontal line for the magnitude of the highest frequnecy.
     * Implementing this would make this class even uglier, so instead we
     * have this funny attention calling constant that explains.
     */
    private final int UGLY_IMPORTANT_NUMBER = -8;

    public DisplayBeanCalculatorDrawLogscaleSteps(final double MAX_Y,
        final double MIN_Y,final double MIN_X,final double MAX_X) {
        super(MAX_Y,MIN_Y,MIN_X,MAX_X);
    }

    /**
     * Loads {@code target} with drawable values for the data in {@code source}.
     */
    @Override
    protected double[] calculate(final double[] target,final double[] source) {
        if(target.length != this.targetSizeFor(source.length)) return null;//TODO EXCEPTIONS
        if( ! metadata.containsKey(Metadata.MAX_UNNORMALIZED_Y) ) return null;//TODO EXCEPTIONS

        final double RANGE_X = MAX_X - MIN_X;
        final double RANGE_Y = MAX_Y - MIN_Y;
        final double MAX_LOG = Math.log(source.length);
        final double MAX_UNNORMALIZED_Y = (double) metadata.get(Metadata.MAX_UNNORMALIZED_Y);

        int i = -1;
        while(++i < source.length - 1)
        {
            // X interval between points i and i+1, normalized to [0,1]
            double xCurrentSampleStart = Math.log(i+1) / MAX_LOG;
            double xNextSampleStart = Math.log(i+2) / MAX_LOG;

            // end of X interval assigned to point i, normalized to [0,1]
            double xCurrentSampleEnd = xCurrentSampleStart + (xNextSampleStart - xCurrentSampleStart)/2;

            // Y values normalized to [0,1] 
            // (assuming source has only positive values and MAX_UNNORMALIZED_Y was set properly)
            double yCurrentSample = source[i] / MAX_UNNORMALIZED_Y;
            double yNextSample = source[i+1] / MAX_UNNORMALIZED_Y;

            int j = SOURCE_TO_TARGET_PROPORTION * i;
            // horizontal line for current sample
            target[j] = MIN_X + xCurrentSampleStart * RANGE_X;
            target[j+1] = MIN_Y + yCurrentSample * RANGE_Y;
            target[j+2] = MIN_X + xCurrentSampleEnd * RANGE_X;
            target[j+3] = MIN_Y + yCurrentSample * RANGE_Y;
            // line between current and next sample
            target[j+4] = MIN_X + xCurrentSampleEnd * RANGE_X;
            target[j+5] = MIN_Y + yCurrentSample * RANGE_Y;
            target[j+6] = MIN_X + xNextSampleStart * RANGE_X;
            target[j+7] = MIN_Y + yNextSample * RANGE_Y;
        }

        return target;

    }

    public int targetSizeFor(int sourceLength) {
        return SOURCE_TO_TARGET_PROPORTION * sourceLength + UGLY_IMPORTANT_NUMBER;
    }

}
