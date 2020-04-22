package rendering;

import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import org.lwjgl.LWJGLException;
import static org.lwjgl.opengl.GL11.*;


public class DisplayController {	
	
	// Screen size and update frame rate
	public static final int WIDTH  = 950;
	public static final int HEIGHT = 950;
	public static final int FPS    = 120;
	
	private static long lastUpdate;
	private static float timeSinceLastUpdate;
	
	public static void startDisplay() {	
		// Initial configuration for the display
		ContextAttribs attributes = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true); // OpenGL V3.2		
		try {			
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT)); // Specific Window Size
			Display.create(new PixelFormat(), attributes);
			Display.setTitle("Treasure Hunter - Aldo Ruiz - Programación Gráfica - Otoño 2016");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}		
		glViewport(0, 0, WIDTH, HEIGHT);
		lastUpdate = getTime();
	}	
	
	
	public static float getTimePerFrame(){
		return timeSinceLastUpdate;
	}
	
	public static long getTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
	
	public static void updateTimeMeasures(){
		long currentTime = getTime();
		timeSinceLastUpdate = (currentTime - lastUpdate)/1000f;
		lastUpdate = currentTime;
	}
	
	public static void updateDisplay(){
		Display.sync(FPS);
		Display.update();	
		updateTimeMeasures();		
	}
	public static void closeDisplay(){
		Display.destroy();		
	}

}
