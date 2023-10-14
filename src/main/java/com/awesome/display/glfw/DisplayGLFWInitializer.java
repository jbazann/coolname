package com.awesome.display.glfw;

import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;

// TODO document
public final class DisplayGLFWInitializer {

	private static final AtomicBoolean init = new AtomicBoolean(false);

    protected synchronized static void init() {

		if( init.getAndSet(true) ) return;

		GLFWErrorCallback.createPrint(System.err).set();

		if ( !glfwInit() ) 
			throw new IllegalStateException("Unable to initialize GLFW");

	}

	protected synchronized static long createWindow() {

		if(! init.get() ) init();

		// Load user settings
		final DisplayGLFWConfig cfg = DisplayGLFWConfig.getCurrentConfig();

		// Set window parameters
		glfwDefaultWindowHints(); 
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);  // hide window
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);  // allow resizing

		// Create window
		final long windowId = glfwCreateWindow(cfg.width, cfg.height, cfg.title, NULL, NULL);
		if ( windowId == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Center the window
		final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
			windowId,
			(vidmode.width() - cfg.width) / 2,
			(vidmode.height() - cfg.height) / 2
		);

		// Poll assigned size
		final IntBuffer width_buffer = BufferUtils.createIntBuffer(1);
		final IntBuffer height_buffer = BufferUtils.createIntBuffer(1);
		glfwGetFramebufferSize(windowId, width_buffer, height_buffer);
		DisplayGLFWFacade.getInstance().resize(width_buffer.get(), height_buffer.get());

		// Setup resize callback
		glfwSetFramebufferSizeCallback( windowId, (window, width, height) -> {
			width_buffer.put(0,height);
			height_buffer.put(0,width);
			DisplayGLFWFacade.getInstance().resize(width, height);
		});

		// Setup key callback.
		glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); 
		});
	
		return windowId;

	}
}
