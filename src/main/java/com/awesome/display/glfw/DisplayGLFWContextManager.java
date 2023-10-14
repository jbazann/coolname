package com.awesome.display.glfw;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import com.awesome.threading.ThreadingWorker;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

//TODO document
public final class DisplayGLFWContextManager extends ThreadingWorker {

    private final GLCapabilities glCapabilities;
    private final AtomicBoolean busyContext;
    private final ConcurrentLinkedQueue<Runnable> pendingWork;
    private final long window;

    protected DisplayGLFWContextManager(final long window) {
        super("Context Manager");
        this.busyContext = new AtomicBoolean(false);
        this.pendingWork = new ConcurrentLinkedQueue<Runnable>();
        this.window = window;

        glfwMakeContextCurrent(window);
        this.glCapabilities = GL.createCapabilities();
        glfwMakeContextCurrent(NULL);
    }

    @Override
    public void runIteration() {
        DisplayGLFWFacade.getInstance().waitForUserInterface();
        Runnable work;
        while((work = pendingWork.poll()) != null) runInContextSync(work);
    }

    @Override
    protected void _start() {
        final DisplayGLFWConfig cfg = DisplayGLFWConfig.getCurrentConfig();
        setDefaultSleepThrottle(cfg.contextManagerWait, 0);
        return;
    }

    @Override
    protected void _kill() {
        return;
    }

    /*
     * DO NOT synchronize this method without thinking very
     * thoughtfully about blocking the interface thread
     */
    protected void runInContextSync(Runnable work) {
        while( busyContext.getAndSet(true) ); // TODO cry about this then fix it
        this.getContext();
        work.run();
        this.releaseContext();
        busyContext.set(false);
    }

    protected void runInContextAsync(Runnable work) {
        pendingWork.offer(work);
    }

    private void getContext() {
        glfwMakeContextCurrent(this.window);
        GL.setCapabilities(glCapabilities);
    }

    private void releaseContext() {
        glfwMakeContextCurrent(NULL);
    }
    
}
