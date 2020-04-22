package playerStats;

import org.lwjgl.util.vector.Vector2f;

public class StatsTexture {
	
	/*
	 * StatsTexture POJO
	 * Needs 2D coordinates 
	 */

	private int texture;	
	private Vector2f scale;
	private Vector2f position;
	
	public StatsTexture(Vector2f position, Vector2f scale, int texture) {
		this.position = position;
		this.scale = scale;
		this.texture = texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}

	public int getTexture() {
		return texture;
	}
}
