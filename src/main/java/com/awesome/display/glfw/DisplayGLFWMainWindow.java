package com.awesome.display.glfw;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;

//TODO document
public final class DisplayGLFWMainWindow {

    private long window;

    protected DisplayGLFWMainWindow(long window) {
        this.window = window;
    }

    protected synchronized void showWindow() {
        
        final DisplayGLFWConfig cfg = DisplayGLFWConfig.getCurrentConfig();

        DisplayGLFWFacade.getInstance().runInContextSync(
            new Runnable() {
                @Override
                public void run() {
                    glfwSwapInterval(cfg.vsync ? 1 : 0);
                    glfwShowWindow(window);
                }
            }
        );

    }

    protected void kill() {
        glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
    }

    protected boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

    protected long getWindowId() {
		return window;
	}

    protected void swapBuffers() {
        glfwSwapBuffers(window);
    }

}
