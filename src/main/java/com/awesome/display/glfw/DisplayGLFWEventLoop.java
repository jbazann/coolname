package com.awesome.display.glfw;

import static org.lwjgl.glfw.GLFW.glfwWaitEvents;

//TODO document
public final class DisplayGLFWEventLoop {
    protected void enterEventLoop() {
		while ( ! DisplayGLFWFacade.getInstance().shouldClose() ) {
			glfwWaitEvents(); // TODO glfwPostEmptyEvent() when .shouldClose() ?
		}
    }
}
