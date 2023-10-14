package com.awesome.display.glfw;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import com.awesome.threading.ThreadingThreadController;

//TODO document
public final class DisplayGLFWRenderLoop extends ThreadingThreadController {

    private final Runnable renderIteration;

    protected DisplayGLFWRenderLoop() {
        super("RenderLoop");
        this.renderIteration = new Runnable() {
            @Override
            public void run() {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                DisplayGLFWFacade.getInstance().updateBeans().swapBuffers();
            }
        };
    }

    @Override
    public void runIteration() {
        DisplayGLFWFacade.getInstance().waitForUserInterface();

		while ( this.shouldRun.get() ) 
            DisplayGLFWFacade.getInstance().runInContextSync(renderIteration);
    }

    @Override
    protected void _start() {
        return;
    }

    @Override
    protected void _kill() {
        return;
    }
}
