package transparentEntities;

import java.util.Random;
import org.lwjgl.util.vector.Vector3f;
import models.RawModel;

public class TransparentEntity {
	
	/*
	 * Create a transparent entity
	 */

	private static final int NUMBER_OF_COLORS = 32;
	private static final float TRANSPARENCY = 0.5f;
	
	private static final float[] vertexes = {
			-5.0f,  0.0f,  5.0f, 
			 5.0f,  0.0f,  5.0f, 
			 5.0f,  0.0f, -5.0f, 
			-5.0f,  0.0f, -5.0f,
			-5.0f, 30.0f,  5.0f, 
			 5.0f, 30.0f,  5.0f, 
			 5.0f, 30.0f, -5.0f, 
			-5.0f, 30.0f, -5.0f
	};
	
	private static final int[] indexes = { 4, 0, 5, 1, 6, 2, 7, 3, 4, 0 };

	private float rotX, rotY, rotZ;
	private float scale;
	private Vector3f position;
	private RawModel rawModel;

	

	public TransparentEntity(RawModel model, Vector3f position, Vector3f rotation, float scale) {
		this.rawModel = model;
		this.position = position;
		this.rotX = rotation.getX();
		this.rotY = rotation.getY();
		this.rotZ = rotation.getZ();
		this.scale = scale;
	}

	public static float[] getVertexes() {
		return vertexes;
	}

	public static int[] getIndexes() {
		return indexes;
	}

	public static float[] getColors() {
		return fillColorArray();
	}

	public static float[] fillColorArray() {

		float[] color = new float[NUMBER_OF_COLORS];
		Random rand = new Random();
		float R = rand.nextFloat();
		float G = rand.nextFloat();
		float B = rand.nextFloat();
		for (int i = 0; i < NUMBER_OF_COLORS;) {
			color[i++] = R;
			color[i++] = G;
			color[i++] = B;
			color[i++] = TRANSPARENCY;
		}
		return color;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}
	public float getRotY() {
		return rotY;
	}
	public float getRotZ() {
		return rotZ;
	}
	
		
	public void setScale(float scale) {
		this.scale = scale;
	}	
	public float getScale() {
		return scale;
	}


}
