package com.awesome.threading;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleSupplier;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public enum ThreadingTimers {
        
        /**
         * Use GLFW time with up to milisecond precision. More stable.
         */ // TODO find more than that one source about GLFW precision
        GLFW(() ->
                glfwGetTime()
        ), 
        /**
         * Use JVM time with up to nanosecond precision. Less stable.
         */ // TODO this is also probably wrong in many ways so read more
        SYSTEM(() -> 
            System.nanoTime() / 1_000_000_000
        ),
        ;

        private DoubleSupplier func;
        private AtomicReference<Double> lastCheck;

        private ThreadingTimers(DoubleSupplier secondCounter) {
            func = secondCounter;
            lastCheck = new AtomicReference<Double>(func.getAsDouble());
        }

        protected double getSeconds() {
            return func.getAsDouble() - lastCheck.getAndSet(func.getAsDouble());
        }
    }
