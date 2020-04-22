package entities;

import org.lwjgl.util.vector.Vector3f;

public class SourceOfLight {
	
	/*
	 * Light POJO
	 */
	
	private static final Vector3f color = new Vector3f(1f, 1f, 1f);
	private Vector3f position = new Vector3f();
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColor() {
		return color;
	}

}
