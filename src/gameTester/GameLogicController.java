package gameTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import playerStats.StatsTexture;
import rendering.ModelLoader;

public class GameLogicController {
	
	/*
	 * This class monitors the game status
	 * Manages lives, damage and the timer	 
	 */

	public static final long MAX_NUMBER_OF_SECONDS = 80; 
	
	static int lives = 3;
	static long lastSecond;     
	static long damageTime;    	
	static long initialTime;    
	static long secondsCounter = MAX_NUMBER_OF_SECONDS;
	static boolean gameOver = false;
	static boolean gameWon  = false;
	ModelLoader modelLoader;
	
	
	public GameLogicController(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
	}
	public void setInitialTime(){
		// The moment when the match starts
		secondsCounter = MAX_NUMBER_OF_SECONDS;
		lastSecond  = System.currentTimeMillis();
		initialTime = System.currentTimeMillis();
	}
	
	public List<StatsTexture> getPlayerStats(){		
		// Get the initial health status; three hearts
		List <StatsTexture> playerStats = new ArrayList<StatsTexture>();
		playerStats.add(new StatsTexture(new Vector2f(-0.90f, -0.90f), new Vector2f(0.05f, 0.05f), modelLoader.loadModelTexture("heart"))); 
		playerStats.add(new StatsTexture(new Vector2f(-0.75f, -0.90f), new Vector2f(0.05f, 0.05f), modelLoader.loadModelTexture("heart"))); 
		playerStats.add(new StatsTexture(new Vector2f(-0.60f, -0.90f), new Vector2f(0.05f, 0.05f), modelLoader.loadModelTexture("heart"))); 
		return playerStats;
	}
	
	public List<StatsTexture> getFirstTimeTextures(){	
		// The timer is made of rectangles in which a texture is set. The textures are plain images of numbers
		// This function returns the initial textures, which correspond to the time set by the MAX_NUMBER_OF_SECONDS
		List<StatsTexture> timer = new ArrayList<StatsTexture>();
		String secondsCounterS = "" + secondsCounter;			
		int numberOfDigits = secondsCounterS.length();
		float startX = -0.9f;			
		for(int i=0; i< numberOfDigits; i++){				
			timer.add(new StatsTexture(new Vector2f(startX,0.9f), new Vector2f(0.05f,0.05f), modelLoader.loadModelTexture("timer", "number_" + secondsCounterS.charAt(i))));
			startX += 0.1;
		}			
		lastSecond = System.currentTimeMillis();	
		return timer;
		
	}	
	
	public List<StatsTexture> getTimeTextures(List<StatsTexture> originalTimeTextures){		
		// The timer is made of rectangles in which a texture is set. The textures are plain images of numbers
		// This function returns a new time-texture every time a second has passed
		if(((System.currentTimeMillis() - lastSecond) / 1000) == 1.0){		
			List<StatsTexture> timer = new ArrayList<StatsTexture>();
			
			if(secondsCounter == 0){
				lives = 0;				
				timer.add(new StatsTexture(new Vector2f(-0.9f,0.9f), new Vector2f(0.05f,0.05f), modelLoader.loadModelTexture("timer", "number_0")));
				return timer;
			}			
			String secondsCounterS = "" + secondsCounter;			
			int numberOfDigits = secondsCounterS.length();
			float startX = -0.9f;			
			for(int i=0; i< numberOfDigits; i++){				
				timer.add(new StatsTexture(new Vector2f(startX,0.9f), new Vector2f(0.05f,0.05f), modelLoader.loadModelTexture("timer", "number_" + secondsCounterS.charAt(i))));
				startX += 0.1;
			}			
			lastSecond = System.currentTimeMillis();			
			secondsCounter--;
			return timer;
		}else
			return originalTimeTextures;	
	}
	
	public void setLost(){
		gameOver = true;
	}
	
	public void setwon(){
		gameWon = true;		
	}
	
	public int getLives(){
		return lives;
	}

	public boolean hasWon(){
		return gameWon;
	}	
	
	public boolean hasLost(){
		return gameOver;
	}
	
	
	public boolean isPlaying(){			
		return (!gameOver && !gameWon)? true: false;
	}
		
	public boolean hasPassedASecond(){
		return (((System.currentTimeMillis() - damageTime) / 1000.0) >= 1.0)? true: false;	
	}
	
	public void isDamaged(){
		damageTime = System.currentTimeMillis();
		lives--;
	}
		
	public void resetStats(){
		lives = 3;
		secondsCounter = MAX_NUMBER_OF_SECONDS;
		gameOver = false;
		gameWon  = false;
		setInitialTime();
	}	
}
