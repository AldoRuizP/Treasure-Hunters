/*
 *  Aldo Conrado Ruiz Pimentel
 *  is692909
 *  Proyecto final de Programación Gráfica
 *  Otoño 2016
 *  Séptimo Semestre
 */

package gameTester;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import entities.*;
import playerStats.*;
import rendering.*;
import shaders.*;
import textures.*;
import transparentEntities.*;
import worldObjects.*;


public class Main {		

	private static final String DIED_MODEL_NAME   = "died";
	private static final String GRASS_MODEL_NAME  = "grass";	
	private static final String WINNER_MODEL_NAME = "winner";	
	private static final String WORLD_FILE_NAME   = "world.txt";
	private static final String TERRAIN_FILE_NAME = "terrain.txt";
	
	private static final int PRIZE_REAPPEARING_OFFSET = 200;  
	private static final int PRIZE_ROTATION_SPEED     = 1;
		
	static List<StatsTexture> playerStats; 
	static List<StatsTexture> timer;
	static List<TransparentEntity> transparentEntities;
	static List<Entity> entities;	
	
	static Player player;	
	static Terrain terrain;
	static StatsTexture died;
	static StatsTexture winer;
	static Entity prizeEntity;	
	static ViewCamera viewCamera;
	static ModelLoader modelLoader;
	static GameLogicController glc;
	static NormalModelShader shader;
	static WorldObjectsGenerator wog;
	static SourceOfLight sourceOfLight;
	static StatsRenderer statsRenderer;
	static GlobalModelRenderer globalRenderer;	
	
	public static void startUp(){	
		
		DisplayController.startDisplay();	
		modelLoader    = new ModelLoader();
		sourceOfLight  = new SourceOfLight();		
		shader		   = new NormalModelShader();
		globalRenderer = new GlobalModelRenderer();	
		timer 		   = new ArrayList<StatsTexture>();
		entities 	   = new ArrayList<Entity>();
		playerStats    = new ArrayList<StatsTexture>();
		statsRenderer  = new StatsRenderer(modelLoader);
		glc            = new GameLogicController(modelLoader);
		transparentEntities = new ArrayList<TransparentEntity>();	
		loadTerrains();	
		loadEntities();
		loadPlayer();
		loadPlayerStats();
	}	

	public static void loadTerrains(){	
		// Loads the terrain, by reading the file and creating the mesh
		ModelTexture modelTexture = new ModelTexture(modelLoader.loadModelTexture(GRASS_MODEL_NAME));
		terrain = new Terrain(modelLoader, modelTexture,  TERRAIN_FILE_NAME);
	}
	
	public static void loadEntities(){	
		// Loads all the entities, by reading the file and creating the objects
		wog = new WorldObjectsGenerator(WORLD_FILE_NAME, terrain, modelLoader);		
		wog.setEntities();
		entities    = wog.getEntitiesList(); // List of all entities
		prizeEntity = wog.getPrizeEntity();	 // Instance of entity which represents the prize
		transparentEntities = wog.getTransparentEntities();	// List of crystals
	}
	
	public static void loadPlayer(){
		// Load the player entity
		player = wog.getPlayerEntity();
		viewCamera = new ViewCamera(player, sourceOfLight);
	}
	
	public static void loadPlayerStats(){
		died  = new StatsTexture(new Vector2f(0f,0f), new Vector2f(0.8f,0.8f), modelLoader.loadModelTexture(DIED_MODEL_NAME));
		winer = new StatsTexture(new Vector2f(0.9f,0.70f), new Vector2f(0.5f,0.25f), modelLoader.loadModelTexture(WINNER_MODEL_NAME));			
		timer = glc.getFirstTimeTextures();	// List of time-texures
		playerStats = glc.getPlayerStats(); // List of lives-textures
		glc.resetStats();
	}	

	public static void renderAll(){		
		
		// Render all the models
		globalRenderer.renderEntity(player);
		globalRenderer.renderTerrain(terrain);		
		globalRenderer.renderEntity(prizeEntity);	
		for(Entity entity : entities)	
			globalRenderer.renderEntity(entity);	
		for(TransparentEntity entity : transparentEntities)
			globalRenderer.renderTransparent(entity);
		globalRenderer.globalRender(sourceOfLight, viewCamera);
		
		// Render the lives
		statsRenderer.renderStats(playerStats);		
		
		// If the player is playing, render the timer
		if(glc.isPlaying()){
			timer = glc.getTimeTextures(timer);
			statsRenderer.renderStats(timer);	
		}
	}
	
	public static void exitAll(){			
		System.out.println("Total number of rendered objects: " + (entities.size() + transparentEntities.size() + playerStats.size()));		
		
		// Clear all lists
		entities.clear();
		transparentEntities.clear();
		playerStats.clear();
		
		// Clean shaders
		statsRenderer.deleteAll();
		globalRenderer.deleteAll();
		modelLoader.deleteAll();
		
		
		DisplayController.closeDisplay();				
	}	
	
	public static void upDateObjects() {
		
		DisplayController.updateDisplay(); // Update screen
		viewCamera.moveCamera(); // Update camera
		
		if (player.isNearInteractiveObject() && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			// If the player is trying to interact with an interactive object
			Entity interactive;
			for (Entity e : entities) {
				// Find which object he's trying to interact
				int eGridX = terrain.getXGridOfElement(e.getPosition().getX());
				int eGridZ = terrain.getZGridOfElement(e.getPosition().getZ());
				if ((terrain.getXGrid() == eGridX) && (terrain.getZGrid() == eGridZ)) {
					interactive = e;
					if (e.getRotationY() == 90) // If the door is already open, disable collision with object
						wog.disableSolidObject(eGridX, eGridZ);
					if (player.isColliding() && interactive.getRotationY() != 90)
						// If the door is closed, change rotation by 1 degree
						interactive.changeRotation(0, 1, 0);
					break;
				}
			}
		}
		prizeEntity.changeRotation(0f, PRIZE_ROTATION_SPEED, 0f); // Make prize rotate
		if (!glc.hasLost())
			movePlayer(); // While the player hasn't lost the game, update it's position
	}
	
	public static void movePlayer(){
		player.movePlayer(terrain, wog);		
		if(glc.isPlaying() && player.isDamaged() && glc.hasPassedASecond()){
			// If player got damaged, remove one live
			glc.isDamaged();
			playerStats.remove(glc.getLives());
		}
	}
	
	public static void checkGameStatus(){	
		
		if((!glc.hasLost() && glc.getLives() == 0)){
			playerStats.add(died);	// Set the game-over screen
			glc.setLost(); // Update game status to lost
			prizeEntity.changePosition(0, -PRIZE_REAPPEARING_OFFSET, 0); // Make prize disapear from map
		}else if(!glc.hasWon() && player.foundPrize()){
			playerStats.add(winer); // Set the game-won screen
			glc.setwon(); // Update game status to won
			player.setGodMode(); // Disable player collisions
			prizeEntity.changePosition(0, -PRIZE_REAPPEARING_OFFSET, 0); // Make prize disapear from map
		}
	}
	
	public static void checkIfResetGame(){		
		
		// If the player has lost or won the game and is pressing the reset key		
		if(!glc.isPlaying() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				prizeEntity.changePosition(0, PRIZE_REAPPEARING_OFFSET, 0); // Place prize in it's original position
				glc.resetStats(); // Reset game status
				playerStats.clear(); // Reset player status
				for(Entity e : entities){					
					// Reset all interactive objects to it's original status
					int eGridX = terrain.getXGridOfElement(e.getPosition().getX());
					int eGridZ = terrain.getZGridOfElement(e.getPosition().getZ());
					if(wog.hasInteractiveObject(eGridX, eGridZ)){						
						e.setRotationY(0);
						wog.enableSolidObject(eGridX, eGridZ);			
					}
				}
				loadPlayerStats(); // Reload player status
				loadPlayer(); // Reload player in it's original position
		}	
	}		
	
	public static void main(String[] args) {		
		startUp();
		while(!Display.isCloseRequested()){						
			checkGameStatus(); 
			upDateObjects();			
			renderAll();			
			checkIfResetGame();            
		}
		exitAll();		 
	}
	
}