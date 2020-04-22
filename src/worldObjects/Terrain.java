package worldObjects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.lwjgl.util.vector.Vector3f;
import models.RawModel;
import rendering.ModelLoader;
import textures.ModelTexture;

public class Terrain {
	
	
	private int SIZE;
	private int NUMBER_OF_VERTEX;	
	private float x;
	private float z;	
	private RawModel rawModel;
	private ModelTexture modelTexture;
	private TerrainGenerator terrainGenerator;
	private String nameOfFile;
	
	public Terrain(ModelLoader modelLoader, ModelTexture modelTexture, String nameOfFile){
		
		// Terrain constructor. Needs the model loader, the model texture (grass) and a filename
		this.modelTexture = modelTexture;
		this.nameOfFile = nameOfFile;
		getDimens();		
		this.x =  0;
		this.z = SIZE * -1;		
		terrainGenerator = new TerrainGenerator( SIZE, NUMBER_OF_VERTEX, modelLoader, nameOfFile);
		this.rawModel = terrainGenerator.generateTerrain();		
	}
	
	public int getSize(){
		return SIZE;
	}
	
	private void getDimens(){
		// Get the dimension of the terrain. They are in the first line of the txt file
		FileReader fr = null;		
		try {
			fr = new FileReader(new File("res/" + nameOfFile));
		} catch (FileNotFoundException e) {e.printStackTrace();}		
		BufferedReader reader = new BufferedReader(fr);
		String line;	
		try{			
			line = reader.readLine();				
			String[] currentLine = line.split(";");	
			SIZE = Integer.parseInt(currentLine[1]);
			NUMBER_OF_VERTEX  = Integer.parseInt(currentLine[2]);				
			fr.close();
		}catch(Exception e){}
	}	
	
	public float getHeight(float wx, float wz){		
		// The height of the grid in which the player is currently standing
		return terrainGenerator.getHeightOfGridSquaree(wx, wz);
	}
	
	public int getXGrid(){
		return terrainGenerator.getXGrid();
	}
	public int getZGrid(){
		return terrainGenerator.getZGrid();
	}
	
	public int getXGridOfElement(float wx){
		// X grid in which an entity is
		return terrainGenerator.getXGridOfElement(wx);
	}
	
	public int getZGridOfElement(float wz){
		// Z grid in which an entity is
		return terrainGenerator.getZGridOfElement(wz);
	}
	
	public Vector3f getCenter(){
		// Returns the coordinates of the center of the terrain
		float c = (float)(SIZE/2);		
		return new Vector3f(c,1,-c);
	}

	public float getX() {
		return x;
	}
	public float getZ() {
		return z;
	}	
	
	public RawModel getRawModel() {
		return rawModel;
	}
	public ModelTexture getModelTexture() {
		return modelTexture;
	}
}
