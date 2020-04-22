package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import models.TexturizedModel;
import rendering.DisplayController;
import worldObjects.Terrain;
import worldObjects.WorldObjectsGenerator;

public class Player extends Entity{
	
	
	// A player is an extension of an Entity, so it needs a position, rotation, scalation and a TexturizedModel
	// In this class, the player position and status is updated. This class also checks from different types of collisions
	
	private static final float TURN = 120;  // Turning angle
	private static final float SPEED = 100; // Moving speed
	private static final float GRAVITY = 500; // Gravity Speed
	private static final float JUMP_LENGHT = 80; // Jumping force
		
	private Vector3f lastValidPos; // For collision detection
	private float currentSpeed = 0; 
	private float currentTurn  = 0;
	private float jumpSpeed	   = 0;	
	private boolean godMode = false; // Dont check for collisions
	private boolean isJumping  = false; // Cancel multi-jumping
	private boolean isColliding = false;
	private boolean prizeCollision = false;
	private boolean harmfullCollision = false;	
	private boolean isNearInteractiveObject = false;
	
	public Player(TexturizedModel texturizedModel, Vector3f position, float rotationX, float rotationY, float rotationZ,float scale){
		super(texturizedModel, position, rotationX, rotationY, rotationZ, scale);
	}	
	
	public void keyboardInput(){	// Detect keyboard input for moving the player	
		// Walking
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) currentSpeed = SPEED;			
		else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) currentSpeed = -SPEED;			
		else currentSpeed = 0;

		// Turning
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) currentTurn = -TURN;
		else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) currentTurn = TURN;
		else currentTurn = 0;
	
		// Jumping
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0) && !isJumping){
			jumpSpeed = JUMP_LENGHT;
			isJumping = true;
		}
	}
	
	public void movePlayer(Terrain terrain, WorldObjectsGenerator wog){		
		keyboardInput();				
		super.changeRotation(0, currentTurn * DisplayController.getTimePerFrame(), 0); 		
		float terrainHeight = terrain.getHeight(super.getPosition().x, super.getPosition().z);	
		
		// Calculate where should the players position be located after the key input
		float distance = currentSpeed * DisplayController.getTimePerFrame();	
		float distanceX = (float) (distance * Math.sin(Math.toRadians(super.getRotationY())));
		float distanceZ = (float) (distance * Math.cos(Math.toRadians(super.getRotationY())));
		
		if(!godMode){ // If the player hasn't won, then the collision checking is necessary
			
			// In which grid of the map is the player located?
			int xGrid = terrain.getXGrid();
			int zGrid = terrain.getZGrid();
			harmfullCollision = wog.hasHarmfullObject(xGrid, zGrid); // Is there a harmfull object in this grid?
			prizeCollision    = wog.hasPrizeObject(xGrid, zGrid); // Is there a prize object in this grid ?
			isColliding = wog.hasObject(xGrid, zGrid); // Is the player colliding with something ?
			isNearInteractiveObject = wog.hasInteractiveObject(xGrid, zGrid); // Is the player colliding with an interactive object ?
			if(isColliding){
				// If its colliding, then do not update the player pos 
				distanceX = lastValidPos.getX() - super.getPosition().getX();
				distanceZ = lastValidPos.getZ() - super.getPosition().getZ();
			}else  lastValidPos = new Vector3f(super.getPosition());
		}		
		updatePosition(distanceX, distanceZ);
		returnToGroundLevel(terrainHeight);
	}
	
	public void updatePosition(float distanceX, float distanceZ){
		super.changePosition(distanceX, 0, distanceZ);
		jumpSpeed -= GRAVITY * DisplayController.getTimePerFrame();		
		super.changePosition(0, jumpSpeed * DisplayController.getTimePerFrame(), 0);		
	}
	
	private void returnToGroundLevel(float terrainHeight){
		//If the player has jumped, then he must return to ground level
		if(super.getPosition().y < terrainHeight){
			jumpSpeed = 0;
			isJumping = false;
			super.getPosition().y = terrainHeight;
		}
	}
	
	public boolean isColliding(){
		return isColliding;
	}
	
	public boolean isNearInteractiveObject(){
		return isNearInteractiveObject;
	}
	
	public boolean isDamaged(){
		return harmfullCollision;
	}
	
	public boolean foundPrize(){
		return prizeCollision;
	}
	
	public void setGodMode(){
		godMode = true;
	}
}
