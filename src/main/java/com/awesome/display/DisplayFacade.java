package com.awesome.display;

import java.util.LinkedList;

import com.awesome.audio.AudioFacade;
import com.awesome.audio.AudioListener;
import com.awesome.display.beans.DisplayBean;
import com.awesome.display.factories.DisplayBeanFactory;
import com.awesome.display.glfw.DisplayGLFWFacade;
import com.awesome.threading.ThreadingTimers;
import com.awesome.threading.ThreadingMonitor;

public class DisplayFacade {

	private volatile static DisplayFacade instance;

	private final LinkedList<DisplayBean> beans;
	private final ThreadingMonitor fpsMonitor;

	private DisplayFacade()
	{
		fpsMonitor = new ThreadingMonitor(ThreadingTimers.GLFW,0,"Display frame rate: ");
		beans = new LinkedList<DisplayBean>();
		loadStartingBeans();
	}

	public static DisplayFacade getInstance() {
		if( instance == null ) 
		{
			synchronized(DisplayFacade.class)
			{
				instance = instance != null ? instance : new DisplayFacade();
			}
		}
		return instance;
	}

	public void start()
	{
		DisplayGLFWFacade.getInstance().start();
		fpsMonitor.start();
		DisplayGLFWFacade.getInstance().runInContextAsync(new Runnable() {

			@Override
			public void run() {
				beans.forEach(bean -> bean.start()); // TODO update .start() documentation, requires GL capabilities
				DisplayWorker.getInstance().start();
			}
			
		});
	}
	public void end() {
		//fpsMonitor.kill();
		beans.forEach(bean -> bean.end());
	}

	private void loadStartingBeans() {

		// terminate existing beans just for robustness
		beans.forEach(bean -> bean.end());
		beans.clear();

		DisplayBeanFactory factory = new DisplayBeanFactory();

		beans.add(factory.newSpectrumBean());
		beans.add(factory.newFrequencySpectrumBean());

	}

    public void updateBeans() {
		beans.forEach(bean -> bean.draw());
    }

    public void countFrame() {
		fpsMonitor.count();
    }

	public void waitForUserInterface() {
		DisplayGLFWFacade.getInstance().waitForUserInterface();
	}

	public void enterEventLoop() {
		DisplayGLFWFacade.getInstance().enterEventLoop();
	}

    public void addAudioListener(AudioListener l) {
		AudioFacade.getInstance().play(l);
    }

	public void updateDisplayArea(int width, int height) {
		DisplayWindowAreaAllocator.update(width,height);
	}

}
