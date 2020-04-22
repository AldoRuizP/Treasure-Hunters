package worldObjects;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import entities.Entity;
import entities.Player;
import models.RawModel;
import models.TexturizedModel;
import rendering.ModelLoader;
import rendering.ModelOBJLoader;
import textures.ModelTexture;
import transparentEntities.TransparentEntity;
import org.lwjgl.util.vector.Vector3f;

public class WorldObjectsGenerator {	
	
	// Reads the world file and creates the proper entities
	// First, saves the strings read from file for each entity
	// Then, matches the height of a grid with the name of the object in the grid, and creates the entity in the grid's position
	// Then, it fills a table for each type of collidable entity. Some entities may enter in more than one category, like
	// spikes, which go in the harmfullCollision detection and the regular object collision detection
	
	
	private static int SIZE;
	private static int NUMBER_OF_VERTEX;
	private static float GRID_SIZE;
	private static float GRID_INCREMENT;

	private static Terrain terrain;	
	private static String nameOfFile;
	private static Entity prizeEntity;
	private static Vector3f playerPos;
	private static Vector3f prizePos;
	
	private static List<Entity> entities;
	private static List<TransparentEntity> transparentEntities;
	private static ModelLoader modelLoader;
	
	private static String[] entityName;
	private static Vector3f[] entityPosArray;
	
	private static String[][] nameOfEntity;
	private static boolean[][] hasInteractiveObject;
	private static boolean[][] hasObject;
	private static boolean[][] hasPrizeObject;
	private static boolean[][] hasHarmfullObject;
	
	private static int RENDER_GRASS;
	
	public WorldObjectsGenerator(String nameOfFile, Terrain terrain, ModelLoader modelLoader){
		WorldObjectsGenerator.nameOfFile = nameOfFile;
		WorldObjectsGenerator.terrain = terrain;		
		WorldObjectsGenerator.modelLoader = modelLoader;
		worldParser();		
	}	
	
	public static Vector3f[] getEntitiesPos(){
		return entityPosArray;
	}
	
	public static String[] getEntitiesNames(){
		return entityName;
	}
	
	public boolean hasObject(int gridX, int gridZ){	
		try{
			return hasObject[gridX][gridZ];
		}catch (Exception e) { return false;}	
	}
	
	public boolean hasHarmfullObject(int gridX, int gridZ){		
		try{
			return hasHarmfullObject[gridX][gridZ];			
		}catch(Exception e){return false;}
	}
	
	public boolean hasPrizeObject(int gridX, int gridZ){
		try{
			return hasPrizeObject[gridX][gridZ];			
		}catch(Exception e){return false;}
	}	
	
	public boolean hasInteractiveObject(int gridX, int gridZ){
		try{
			return hasInteractiveObject[gridX][gridZ];			
		}catch(Exception e){return false;}
	}
	
	public String getNameOfEntity(int gridX, int gridZ){
		try{
			return nameOfEntity[gridX][gridZ];			
		}catch(Exception e){return "";}
	}
	
	public void disableSolidObject(int gridX, int gridZ){
		hasObject[gridX][gridZ] = false;
	}
	
	public void enableSolidObject(int gridX, int gridZ){
		hasObject[gridX][gridZ] = true;
	}	
	
	private void worldParser(){		
		
		// Read file, sets the characteristics of the world, like size, number of vertexes and the size of each grid
		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/" + nameOfFile));			
		} catch (FileNotFoundException e) {e.printStackTrace();}		
		BufferedReader reader = new BufferedReader(fr);
		String line;	
		int pointer = 0;
		
		try{		
			while(true){				
				line = reader.readLine();				
				String[] currentLine = line.split(";");
				
				if(line.startsWith("#")){
					
					SIZE = Integer.parseInt(currentLine[1]);
					NUMBER_OF_VERTEX = Integer.parseInt(currentLine[2]);					
					RENDER_GRASS = Integer.parseInt(currentLine[3]);					
					GRID_SIZE = (float) SIZE/(NUMBER_OF_VERTEX -1);
					GRID_INCREMENT = GRID_SIZE/2;

					entityPosArray = new Vector3f[NUMBER_OF_VERTEX * NUMBER_OF_VERTEX];
					entityName     = new String  [NUMBER_OF_VERTEX * NUMBER_OF_VERTEX];
					
					float fx = SIZE-GRID_INCREMENT;
					float fz = 0-GRID_INCREMENT;
					float fy = (float) Math.ceil(terrain.getHeight(fx, fz));					
					entityPosArray[pointer++] = new Vector3f(fx,fy,fz);
					float newGridSize = SIZE - GRID_SIZE;
					float newGridIncrement = (float) newGridSize/(NUMBER_OF_VERTEX-1);
					float fz2 = entityPosArray[0].getZ();
					float fy2;
					float fx2;
					
					for(int i=0; i< NUMBER_OF_VERTEX; i++){
						fx2 = SIZE-GRID_INCREMENT + newGridIncrement;
						for(int j=0; j< NUMBER_OF_VERTEX; j++){	
							if(i==0 && j==0){
								fx2 -= newGridIncrement;
								j++;
							}							
							fx2 -= newGridIncrement;
							fy2 = (float) Math.ceil(terrain.getHeight(fx2, fz2));		
							entityPosArray[pointer] = new Vector3f(fx2,fy2,fz2);	
							pointer++;
						}							
						fz2 -= newGridIncrement;
					}					
					pointer = 0;					
				}
				else 
					for(int i=0; i<NUMBER_OF_VERTEX; i++)						
						entityName[pointer++] = currentLine[i];			
			}
		}catch(Exception e){}		
		try {
			fr.close();
		} catch (IOException e) {e.printStackTrace();}
		
		int p=0;
		hasObject = new boolean[NUMBER_OF_VERTEX][NUMBER_OF_VERTEX];
		hasPrizeObject = new boolean[NUMBER_OF_VERTEX][NUMBER_OF_VERTEX];	
		hasHarmfullObject = new boolean[NUMBER_OF_VERTEX][NUMBER_OF_VERTEX];
		hasInteractiveObject = new boolean[NUMBER_OF_VERTEX][NUMBER_OF_VERTEX];
		nameOfEntity = new String[NUMBER_OF_VERTEX][NUMBER_OF_VERTEX];
		
		
		// Fill collision lists
		int pointerOffset = NUMBER_OF_VERTEX-1;
		for(int i=0; i< NUMBER_OF_VERTEX; i++){
			for(int j=(NUMBER_OF_VERTEX-1); j>=0; j--){	
				hasObject[pointerOffset-j][pointerOffset-i] = (entityName[p].equals("ND") || (entityName[p].equals("PL")))? false:true;			
				hasHarmfullObject[pointerOffset-j][pointerOffset-i] = (entityName[p].equals("SP"))? true:false;
				hasPrizeObject[pointerOffset-j][pointerOffset-i] = (entityName[p].equals("PR"))? true:false;
				hasInteractiveObject[pointerOffset-j][pointerOffset-i] = (entityName[p].equals("WD"))? true:false;	
				nameOfEntity[pointerOffset-j][pointerOffset-i] = entityName[p];
				p++;
			}
		}		
	}	
	
	public void setEntities(){
		
		// Iterate through the entities read from the file
		
		Vector3f entitiesPos[] = getEntitiesPos();
		String entitiesNames[] = getEntitiesNames();	
		entities = new ArrayList<Entity>();	
		transparentEntities = new ArrayList<TransparentEntity>();
		int i=0;

		for(Vector3f pos : entitiesPos){	
			
			// for every positions in which there is an item: create the item
			
			float scale= 1.0f;
			String modelName="";
			Vector3f entityRotation = new Vector3f(0f, 0f, 0f);
			boolean valid = true;
			boolean shine = false;
			int random;
			Random rand;// = new Random();
			
			
			switch(entitiesNames[i]){
				
			case "ST": // Random Stone
				random = ThreadLocalRandom.current().nextInt(1, 3); // random int between 1 and 2	
				switch(random){
					case 1:
						modelName = "stone01";
						rand = new Random();						
						scale = 3.0f;
						entityRotation.setY(rand.nextFloat() * (360 - 0) + 360 );
						break;
					case 2:
						modelName = "stone02";
						rand = new Random();	
						scale = 1.5f;						
						entityRotation.setY(rand.nextFloat() * (360 - 0) + 360 );
						break;				
				}
				break;
			
			case "TR": // Random Tree
				random = ThreadLocalRandom.current().nextInt(1, 4); // random int between 1 and 3				
				switch(random){
					case 1:
						modelName = "tree";					
						rand = new Random();
						scale = rand.nextFloat() * (1.5f - 0.5f) + 0.5f;					
						entityRotation.setY(rand.nextFloat() * (360 - 0) + 360 );
						break;
					
					case 2:
						modelName = "tree2";						
						rand = new Random();
						scale = 3.0f;						
						entityRotation.setY(rand.nextFloat() * (360 - 0) + 360 );
						break;
					
					case 3:
						modelName = "dead_tree";						
						rand = new Random();
						scale = rand.nextFloat() * (5.0f - 2.0f) + 2.0f;							
						entityRotation.setY(rand.nextFloat() * (360 - 0) + 360 );
						break;					
				}				
				break;
			
			case "MU": // Mushroom
				random = ThreadLocalRandom.current().nextInt(1, 3); // random int between 1 and 2				
				modelName = random == 1? "mushroom01": "mushroom02";
				rand = new Random();
				scale = rand.nextFloat() * (2.0f - 0.5f) + 0.5f;				
				entityRotation.setY(rand.nextFloat() * (360 - 0) + 360 );
				break;
				
			
			case "PR": // Prize
				valid = false;
				prizePos =  new Vector3f(pos.getX(), pos.getY()+5, pos.getZ());
				break;
				
			case "WD": // Wooden Door
				modelName = "door";
				valid = false;
				Vector3f doorPosition = new Vector3f(pos.getX()+5, pos.getY(), pos.getZ()-5);
				entities.add(new WorldObject(modelLoader, doorPosition, entityRotation, scale, modelName, shine).getEntity());	
				break;
			
			case "PL": // Player				
				playerPos = new Vector3f(pos);
				valid = false;
				break;
			
			case "DR": // Opaque Dragon to the Right
				modelName = "dragon";
				scale =1.5f;
				break;					
			
			case "DL": // Opaque Dragon to the Left
				modelName = "dragon";
				scale =1.5f;	
				entityRotation.setY(180);
				break;				
			
			case "CH": // Chair
				modelName = "chair";
				scale = 10.0f;
				break;
			
			case "PI": // Pillar
				modelName = "pillar";
				scale = 5.0f;
				break;
				
			case "BW": // Brick Wall
				modelName = "wall";
				break;					
			
			case "SW": // Stone Wall
				modelName = "blockWall";
				break;
			
			case "TB": // Table
				modelName = "table";
				scale = 3.0f;
				break;
			
			case "KR": // Krustaceus
				modelName = "cangrejo";
				scale = 12.0f;				
				entityRotation.setY(90);
				break;
			
			case "SP": // Harmfull Spikes
				modelName = "spikes";
				scale = 0.63f;
				shine = false;
				break;
			
			case "GL": // Ramdom Color Glass
		        RawModel model = modelLoader.loadToVAO_Color(TransparentEntity.getVertexes(), TransparentEntity.getColors(), TransparentEntity.getIndexes());
		        TransparentEntity entity = new TransparentEntity(model, pos, entityRotation, scale);			        
		        transparentEntities.add(entity);
		        valid = false;
				break;
				
			default:
				valid = false;
				break;			
			}			
						
			if(valid) entities.add(new WorldObject(modelLoader, pos, entityRotation, scale, modelName, shine).getEntity());			
			i++;
		}		
		
		
		// Create grass entities. They don't need collision detection
		if(RENDER_GRASS != 0){
			float size = terrain.getSize();
			Random rand = new Random();
			for(i=0; i<RENDER_GRASS; i++){
				float x = rand.nextFloat() *size;
				float z = rand.nextFloat() * (size) - size;
				float y = terrain.getHeight(x, z);			
				float rotY = rand.nextFloat()*360;				
				Entity e = new WorldObject(modelLoader, new Vector3f(x, y, z), new Vector3f(0f,rotY,0f), 1.0f, "lawn", false).getEntity();
				e.getTexturizedModel().getModelTexture().setTransparent(true);
				entities.add(e);
			}
		}
	}
	
	public List<Entity> getEntitiesList(){
		return entities;
	}
	
	public Entity getPrizeEntity(){		
		String modelName = "dragon";
		prizeEntity = new WorldObject(modelLoader, new Vector3f(prizePos), new Vector3f(0f,0f, -25f), 1.0f, modelName, true).getEntity();			
		return prizeEntity;
	}
	
	public Player getPlayerEntity(){
		String modelName = "lego";		
		RawModel playerModel = ModelOBJLoader.loadOBJModel(modelName, modelLoader);
		TexturizedModel texturedPlayer = new TexturizedModel(playerModel, new ModelTexture(modelLoader.loadModelTexture(modelName)));
		return new Player(texturedPlayer, new Vector3f(playerPos), 0, 0, 0, 2.5f);
	}	
	
	public List<TransparentEntity> getTransparentEntities(){
		return transparentEntities;
	}
	
	
}
