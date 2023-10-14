package com.awesome.display.glfw;

// TODO document
public class DisplayGLFWConfig {
    private final static String defaultTitle = "Awesome Window";
	private final static int defaultWidth = 880;
	private final static int defaultHeight = 495;
    private final static boolean defaultVSync = true;
    public final String title;
	public final int width;
	public final int height;
    public final boolean vsync;
    public final int contextManagerWait =  250;

    public static DisplayGLFWConfig getCurrentConfig() {
        return new DisplayGLFWConfig();
    }

    /*
     * TODO Create the config file when its missing
     * TODO Use an actual config file instead of default values.
     * TODO Consider DTO pattern to avoid reading file in constructor
     */
    private DisplayGLFWConfig() {
        title = defaultTitle;
        width = defaultWidth;
        height = defaultHeight;
        vsync = defaultVSync;
    }

}
