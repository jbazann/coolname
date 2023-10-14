package com.awesome;

import com.awesome.audio.AudioFacade;
import com.awesome.display.DisplayFacade;

public class Main 
{
    public static void main( String[] args )
    {
        startupRoutine();
        runLoop();
        shutdownRoutine();
    }

    private static void startupRoutine() {
        DisplayFacade.getInstance().start();
        AudioFacade.getInstance().start();
    }

    private static void runLoop() {
        DisplayFacade.getInstance().enterEventLoop();
    }

    private static void shutdownRoutine() {
    }

}
