package entities;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturizedModel;

public class Entity {
	
	/*
	 * Entity POJO
	 * 
	 * An Entity requires:
	 * 	 - A position in the world
	 *   - An orientation in the world
	 *   - A size scale
	 *   - A RawModel or a TexturizedModel, in which the buffers are contained	 
	 *   
	 *  With this class, one can alter the position, scale or rotation of an entity
	 *  
	 */
	
	
	
	private TexturizedModel texturizedModel;
	private RawModel rawModel;
	private Vector3f position;
	private float rotationX, rotationY, rotationZ;
	private float scale;
	
	public Entity(TexturizedModel texturizedModel, Vector3f position, float rotationX, float rotationY, float rotationZ,float scale){
		this.texturizedModel = texturizedModel;
		this.position = position;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.scale = scale;
	}
	
	public Entity(RawModel rawModel, Vector3f position, float rotationX, float rotationY, float rotationZ,float scale){
		this.rawModel = rawModel;
		this.position = position;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.scale = scale;
	}
	
	public void changeRotation(float rotationX, float rotationY, float rotationZ){
		this.rotationX += rotationX;	
		this.rotationY += rotationY;
		this.rotationZ += rotationZ;		
	}
	
	public void changePosition(float newX, float newY, float newZ){
		this.position.x += newX;
		this.position.y += newY;
		this.position.z += newZ;	
	}

	public TexturizedModel getTexturizedModel() {
		return texturizedModel;
	}
	
	public RawModel getRawModel(){
		return rawModel;
	}


	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotationX() {
		return rotationX;
	}

	public void setRotationX(float rotationX) {
		this.rotationX = rotationX;
	}

	public float getRotationY() {
		return rotationY;
	}

	public void setRotationY(float rotationY) {
		this.rotationY = rotationY;
	}

	public float getRotationZ() {
		return rotationZ;
	}

	public void setRotationZ(float rotationZ) {
		this.rotationZ = rotationZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}	
}
