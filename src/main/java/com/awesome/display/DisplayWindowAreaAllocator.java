package com.awesome.display;

import java.util.Optional;
import java.util.function.Function;

public enum DisplayWindowAreaAllocator {

    NULL(new Function<DisplayWindowArea,DisplayWindowArea>() {

        @Override
        public DisplayWindowArea apply(DisplayWindowArea area) {
            area.setOriginX(0);
			area.setOriginY(0);
            area.setHeight(0);
            area.setWidth(0);
            return area;
        }

    }),
    TOP(new Function<DisplayWindowArea,DisplayWindowArea>() {

        @Override
        public DisplayWindowArea apply(DisplayWindowArea area) {
            area.setOriginX(0);
            area.setOriginY(framebuffer_height / 2);
            area.setHeight(framebuffer_height / 2);
            area.setWidth(framebuffer_width);
            return area;
        }

    }),
    BOTTOM(new Function<DisplayWindowArea,DisplayWindowArea>() {

        @Override
        public DisplayWindowArea apply(DisplayWindowArea area) {
            area.setOriginX(0);
            area.setOriginY(0);
            area.setHeight(framebuffer_height / 2);
            area.setWidth(framebuffer_width);
            return area;
        }

    }),
    BOTTOM_RIGHT_HALF(new Function<DisplayWindowArea,DisplayWindowArea>() {

        @Override
        public DisplayWindowArea apply(DisplayWindowArea area) {
            area.setOriginX(framebuffer_width / 2);
            area.setOriginY(0);
            area.setHeight(framebuffer_height / 2);
            area.setWidth(framebuffer_width / 2);
            return area;
        }

    });

	private static int framebuffer_height;
	private static int framebuffer_width;

    private Function<DisplayWindowArea,DisplayWindowArea> resizeFunction;
	private Optional<DisplayWindowBean> owner;

    private DisplayWindowAreaAllocator(Function<DisplayWindowArea,DisplayWindowArea> resizeFunction) {
        this.owner = Optional.empty();
        this.resizeFunction = resizeFunction;
    }

	/**
     * Calculate sector centers and dimensions for all assigned {@code DisplayWindowArea} elements.
     * This method is meant to be called as framebuffer resize callback.
     * @param width framebuffer width for which to calculate sector properties.
     * @param height framebuffer height for which to calculate sector properties.
     */
    public static void update(int width, int height) {
        
        DisplayWindowAreaAllocator.framebuffer_width = width;
        DisplayWindowAreaAllocator.framebuffer_height = height;

        for (DisplayWindowAreaAllocator value : DisplayWindowAreaAllocator.values()) {
            if( value.owner.isPresent() ) 
                value.owner.get().resize(value.resizeFunction.apply(new DisplayWindowArea()));
        };

    }
	//TODO review side effects
    public DisplayWindowArea getDisplayArea(DisplayWindowBean bean) {
		if( this.owner.isPresent() ) this.owner.get().release();// TODO EXCPETIONS
		owner = Optional.of(bean);
        System.out.println("Acquired "+this.toString());//TODO remove prints
		return this.resizeFunction.apply(new DisplayWindowArea());
	}

	public void releaseDisplayArea() {
		if(this.owner.isPresent()) {
			this.owner.get().release();
			this.owner = Optional.empty();
		}
	}

}
