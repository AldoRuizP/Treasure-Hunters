package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class ViewCamera {
	
	private static final float MIN_ZOOM = 100f;
	private static final float MAX_ZOOM = 5f;
	private static final float CENTER_OF_PLAYER = 8f;
	private static final float DISTANCE_OF_LIGHT_FROM_CAMERA = 10;
	private static final float HORIZONTAL_DISTANCE_OF_LIGHT_FROM_CAMERA = 150;
	private static final float VERTICAL_DISTANCE_OF_LIGHT_FROM_CAMERA = 100;
			
	private float yaw; // horizontal direction of camera
	private float pitch = 20;// vertical direction of camera
	private float distanceFromPlayer  = 25;
	private float angleArroundPlayer  = 0;	
	
	private Player player;
	private SourceOfLight light;
	private Vector3f position = new Vector3f(0,0,0);
	private Vector3f lightPosition = new Vector3f(0,0,0);
	
	
	public ViewCamera(Player player, SourceOfLight light){	
		this.player = player;
		this.light = light;
	}
	
	public void moveCamera(){		
		calculateZoom();
		calculatePitch();		
		calculateAngleArroundPlayer();	
		
		float dx = calculateHorizontalDistance();
		float dy = calculateVerticalDistance();
		calculateCameraPosition(dx, dy);
	
		this.yaw = 180 - (player.getRotationY() + angleArroundPlayer);
		
		dx = calculateHorizontalDistanceOfLight()+HORIZONTAL_DISTANCE_OF_LIGHT_FROM_CAMERA;
		dy = calculateVerticalDistanceOfLight()+VERTICAL_DISTANCE_OF_LIGHT_FROM_CAMERA;
		calculateLightPosition(dx, dy);
		light.setPosition(getLightPosition());
		
	}
	
	
	private void calculateCameraPosition(float dx, float dy){
		float angle = player.getRotationY() + angleArroundPlayer;
		float xOffset = (float) (dx * Math.sin(Math.toRadians(angle)));
		float zOffset = (float) (dx * Math.cos(Math.toRadians(angle)));
		position.x = player.getPosition().x - xOffset;
		position.y = player.getPosition().y + dy + CENTER_OF_PLAYER ;		
		position.z = player.getPosition().z - zOffset;
	}
	
	private void calculateLightPosition(float dx, float dy){
		float angle = player.getRotationY() + angleArroundPlayer;
		float xOffset = (float) (dx * Math.sin(Math.toRadians(angle)));
		float zOffset = (float) (dx * Math.cos(Math.toRadians(angle)));		
		lightPosition.x = player.getPosition().x - xOffset;
		lightPosition.y = player.getPosition().y + dy + CENTER_OF_PLAYER ;		
		lightPosition.z = player.getPosition().z - zOffset;
	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));		
	}	
	
	private float calculateHorizontalDistanceOfLight(){
		return (float) (distanceFromPlayer-DISTANCE_OF_LIGHT_FROM_CAMERA * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistanceOfLight(){
		return (float) (distanceFromPlayer-DISTANCE_OF_LIGHT_FROM_CAMERA * Math.sin(Math.toRadians(pitch)));		
	}	
	
	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel()*0.01f;		
		float tempDistanceFromPlayer = 	distanceFromPlayer -zoomLevel;		
		if(tempDistanceFromPlayer > MIN_ZOOM)
			distanceFromPlayer = MIN_ZOOM;
		else if(tempDistanceFromPlayer < MAX_ZOOM)
			distanceFromPlayer = MAX_ZOOM;
		else
			distanceFromPlayer = tempDistanceFromPlayer;		
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * 0.15f;
			float tempPitch = pitch -pitchChange;
			if(tempPitch < 0.11f) pitch = 0.11f;
			else if(tempPitch > 89f) pitch = 89f;
			else pitch = tempPitch;	
		}	
	}
	
	private void calculateAngleArroundPlayer(){
		if(Mouse.isButtonDown(0)){
			float angleChange = Mouse.getDX() * 0.3f;
			angleArroundPlayer -= angleChange;
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}	

	private Vector3f getLightPosition(){
		return lightPosition;
	}

}
