package com.awesome.display.beans;

import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.opengl.GL32;

import com.awesome.display.DisplayWindowArea;
import com.awesome.display.DisplayWindowAreaAllocator;
import com.awesome.display.DisplayWindowBean;

/**
 * Superclass for basic display components. Provides
 * standard attributes and method interfaces to build and render
 * the OpenGL UI.
 * <p>
 * General use of this class hierarchy consists of a single call to
 * {@code start()}, followed by a series of {@code update()} calls.
 * If a given {@code DisplayBean} is to be disposed before the application
 * exits, {@code end()} must be called to ensure resources are released.
 * 
 * @TODO Review implementation of template methods start(), update() and end().
 */
public abstract class DisplayBean implements DisplayWindowBean {

    /*
     * // TODO review use of these attributes
     */
    protected double DRAW_RANGE_TOP;
    protected double DRAW_RANGE_BOT;
    protected double DRAW_RANGE_LEFT;
    protected double DRAW_RANGE_RIGHT;
    private AtomicReference<ViewportSettings> currentViewport;
    private volatile boolean started;

    private class ViewportSettings {

        private static final float marginPercentX = 0.04f;
        private static final float marginPercentY = 0.1f;// TODO ew

        private int originX = 0;
        private int width = 0;
        private int originY = 0;
        private int height = 0;
        private int marginX = 0;
        private int marginY = 0;

    }


    /**
     * Vertex Array Object
     */
    protected Integer vao;
    /**
     * Vertex Buffer Object
     */
    protected Integer vbo;

    
    private AtomicReference<DisplayWindowAreaAllocator> areaAllocator;

    protected DisplayBean() {
        this.DRAW_RANGE_TOP = 1d;
        this.DRAW_RANGE_BOT = -1d;
        this.DRAW_RANGE_LEFT = 0d;
        this.DRAW_RANGE_RIGHT = 1d;
        this.started = false;
        this.areaAllocator = new AtomicReference<DisplayWindowAreaAllocator>(DisplayWindowAreaAllocator.NULL);
        this.currentViewport = new AtomicReference<DisplayBean.ViewportSettings>(new ViewportSettings());
    }

    protected DisplayBean(final double DRAW_RANGE_TOP,
    final double DRAW_RANGE_BOT,final double DRAW_RANGE_LEFT,final double DRAW_RANGE_RIGHT) {
        this();
        this.DRAW_RANGE_TOP = DRAW_RANGE_TOP;
        this.DRAW_RANGE_BOT = DRAW_RANGE_BOT;
        this.DRAW_RANGE_LEFT = DRAW_RANGE_LEFT;
        this.DRAW_RANGE_RIGHT = DRAW_RANGE_RIGHT;
    }

    /**
     * Draw this {@code DisplayBean} in its latest state.
     */
    public final void draw() {

        if( ! started ) return;

        // setup OpenGL to draw with this Bean's normalized coordinates.
        ViewportSettings vp = currentViewport.get();
        GL32.glViewport(
            vp.originX + vp.marginX, 
            vp.originY + vp.marginY, 
            vp.width - 2*vp.marginX, 
            vp.height - 2*vp.marginY);

        GL32.glMatrixMode(GL32.GL_PROJECTION);
        GL32.glLoadIdentity();
        GL32.glOrtho(DRAW_RANGE_LEFT,DRAW_RANGE_RIGHT,DRAW_RANGE_BOT,DRAW_RANGE_TOP,-1,1);

        _draw(); // draw

    }

    /**
     * Proceed with the graphic update. 
     */
    protected abstract void _draw();

    /**
     * Initialize resource consuming tasks and dynamic properties (such as screen coordinates), 
     * preparing this {@code DisplayBean} to enter the main loop.
     * Ideally, this method should be called right before this object is intended to be
     * shown on screen. Taking too long between calls to {@code start()} and {@code update()} 
     * might cause temporary visual artifacts depending on this bean's implementation of
     * {@code update_override()}. 
     */
    public final void start() {
        vao = GL32.glGenVertexArrays();
        GL32.glBindVertexArray(vao);
        vbo = GL32.glGenBuffers();
        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, vbo);

        _start();

        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, 0);
        GL32.glBindVertexArray(0);

        this.started = true;

    }
    /**
     * Subclass-specific code for DisplayBean.start(). VAO and VBO are
     * already bound when calling this method, and unbound upon return.
     */
    protected abstract void _start();
    /**
     * Release OpenGL resources and perform any other necessary tasks before deleting
     * the DisplayBean. There is no guarantee that this method will be called, nor is
     * it necessary if performance isn't a concern. 
     */
    public final void end() {

        _end();

        GL32.glDeleteVertexArrays(vao);
        GL32.glDeleteBuffers(vbo);
    }
    /**
     * Subclass-specific code for DisplayBean.end(). The state of the object when calling this 
     * method is the same as required for update_override() calls. Implementations of 
     * this method should only address the release of resources allocated 
     * by start_override() (if any). 
     */
    protected abstract void _end();


    public void assignDisplayArea(DisplayWindowAreaAllocator allocator) {
        areaAllocator.set(allocator);
        resize(areaAllocator.get().getDisplayArea(this));
    }

    /**
     * Update the OpenGL transform matrix and this object's attributes that handle drawing
     * within the assigned space in DisplayWindow for this DisplayBean.
     */
    @Override
    public void resize(DisplayWindowArea dwa) {
        ViewportSettings vp = new ViewportSettings();
        
        vp.originX = dwa.getOriginX();
        vp.width = dwa.getWidth();
        vp.originY = dwa.getOriginY();
        vp.height = dwa.getHeight();
        vp.marginX = Math.round(ViewportSettings.marginPercentX * vp.width);
        vp.marginY = Math.round(ViewportSettings.marginPercentY * vp.height);

        currentViewport.set(vp);
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'release'");
    }

    /**
     * Update {@code this.vertexArray} with newer data. 
     * @param data Ordered amplitude samples to be drawn.
     * @implNote Implementations should only directly interact with {@code vertexArray}
     * by replacing its value with a reference to an updated, equivalent array.
     */
    public abstract void calculate(double[] data);

}
