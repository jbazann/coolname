package com.awesome.display.glfw;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.util.concurrent.atomic.AtomicBoolean;

import com.awesome.display.DisplayFacade;

// TODO document
// TODO review reasons to return this so many times
public class DisplayGLFWFacade {

    private volatile static DisplayGLFWFacade instance;

    public static DisplayGLFWFacade getInstance() {
        if( instance == null ) 
        {
            synchronized(DisplayGLFWFacade.class)
            {
                instance = instance != null ? instance : new DisplayGLFWFacade();
            }
        }
        return instance;
    }

    private final DisplayGLFWEventLoop eventLoop;
    private final DisplayGLFWRenderLoop renderLoop;
    private final AtomicBoolean renderReady;
    private DisplayGLFWMainWindow window;
    private DisplayGLFWContextManager contextManager;

    private DisplayGLFWFacade() {
        this.eventLoop = new DisplayGLFWEventLoop();
        this.renderLoop = new DisplayGLFWRenderLoop();

        this.renderReady = new AtomicBoolean(false);
    }    
    
    public DisplayGLFWFacade start() {
        this.renderLoop.start();
        DisplayGLFWInitializer.init();
        this.window = new DisplayGLFWMainWindow(DisplayGLFWInitializer.createWindow());
        this.contextManager = new DisplayGLFWContextManager(this.window.getWindowId());
        this.contextManager.start();
        this.window.showWindow();
        this.renderReady.set(true);
        return this;
    }

    public void kill() {
        this.window.kill();
        //this.renderLoop.kill();
		glfwTerminate();
		glfwSetErrorCallback(null).free();

    }

    public DisplayGLFWFacade waitForUserInterface() {
        // TODO probably deal with this some day
        while( ! this.renderReady.get() ) {
            try{
                Thread.sleep(100); // TODO numba
            }catch(Exception e){}// TODO exceptions
        }
        return this;
    }

    public DisplayGLFWFacade runInContextAsync(Runnable work) {
        contextManager.runInContextAsync(work);
        return this;
    }

    //TODO maybe check only the main thread can enter
    public void enterEventLoop() {
        eventLoop.enterEventLoop();
    }

    protected DisplayGLFWFacade updateBeans() {
        DisplayFacade.getInstance().updateBeans();
        return this;
    }

    protected DisplayGLFWFacade swapBuffers() {
        DisplayFacade.getInstance().countFrame();//TODO ??????
        this.window.swapBuffers();
        return this;
    }

    protected DisplayGLFWFacade runInContextSync(Runnable work) {
        contextManager.runInContextSync(work);
        return this;
    }

    protected boolean shouldClose() {
        return window.shouldClose();
    }

    protected DisplayGLFWFacade resize(int width, int height) {
        DisplayFacade.getInstance().updateDisplayArea(width, height);
        return this;
    }

}
