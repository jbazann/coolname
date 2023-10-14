package com.awesome.display.factories;

import com.awesome.display.DisplayWorker;
import com.awesome.display.DisplayWindowAreaAllocator;
import com.awesome.display.beans.DisplayBean;
import com.awesome.display.beans.DisplayFrequencySpectrumBean;
import com.awesome.display.beans.DisplayRawAudioBean;

public class DisplayBeanFactory {

    public DisplayBean newSpectrumBean() {
        return this.defaultBuild(
            new DisplayRawAudioBean(
                44100 / 8, 
                2,
                Short.MAX_VALUE*2// TODO fix these
                ),
            DisplayWindowAreaAllocator.TOP
        );
    }

    public DisplayBean newFrequencySpectrumBean() {
        return this.defaultBuild(
            new DisplayFrequencySpectrumBean(
                (int) Math.pow(2d,12d),
                2,
                (double) 25000000
            ), // TODO make config
            DisplayWindowAreaAllocator.BOTTOM
        );
    }

    private DisplayBean defaultBuild(DisplayBean newBean, DisplayWindowAreaAllocator desiredArea) {
        newBean.assignDisplayArea(desiredArea);
        DisplayWorker.getInstance().subscribeBean(newBean);
        return newBean;
    }

}
