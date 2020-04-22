package worldObjects;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturizedModel;
import rendering.ModelLoader;
import rendering.ModelOBJLoader;
import textures.ModelTexture;

public class WorldObject {
	
	private Entity entity;
	
	public WorldObject(ModelLoader modelLoader, Vector3f position, Vector3f rotation, float scale, String modelName, boolean shine){
		RawModel rawModel = ModelOBJLoader.loadOBJModel(modelName, modelLoader); 
		TexturizedModel texturizedModel = new TexturizedModel(rawModel, new ModelTexture(modelLoader.loadModelTexture(modelName)));
		
		
		if(shine){
			ModelTexture texturedModifier = texturizedModel.getModelTexture();
			texturedModifier.setShiningPercentage(10);
			texturedModifier.setReflectivePercentage(1);
		}
		this.entity = new Entity(texturizedModel, position, rotation.x, rotation.y, rotation.z, scale);
	}
	
	public Entity getEntity(){
		return this.entity;
	}
	
}
