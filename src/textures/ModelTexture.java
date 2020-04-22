package textures;

public class ModelTexture {
	
	/*
	 * Texture lightening
	 */
	private int textureID;
	private float reflectivePercentage=0; // Objects reflectivity
	private float shiningPercentage=1; // Shine according to the cameras position
	private boolean transparent = false;

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}	
	
	public float getReflectivePercentage() {
		return reflectivePercentage;
	}

	public void setReflectivePercentage(float reflectivePercentage) {
		this.reflectivePercentage = reflectivePercentage;
	}

	public float getShiningPercentage() {
		return shiningPercentage;
	}

	public void setShiningPercentage(float shiningPercentage) {
		this.shiningPercentage = shiningPercentage;
	}

	public ModelTexture(int textureID){
		this.textureID = textureID;
	}
	
	public int getTextureID(){
		return this.textureID;
	}
}
