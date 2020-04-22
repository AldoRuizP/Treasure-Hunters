package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.List;
import java.util.Map;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import entities.Player;
import math.Maths;
import models.RawModel;
import models.TexturizedModel;
import shaders.NormalModelShader;
import textures.ModelTexture;

public class ModelRenderer {
	
	/*
	 * Basic renderer
	 */
	
	private NormalModelShader normalModelShader;
	
	public ModelRenderer(NormalModelShader staticModelShader, Matrix4f projectionMatrix){
		this.normalModelShader = staticModelShader;
		staticModelShader.startProgram();
		staticModelShader.loadProjectionMatrix(projectionMatrix);
		staticModelShader.stopProgram();
	}
	
	
	public void renderGlobalEntity(Map<TexturizedModel, List<Entity>> entities){
		 
		//Renders all the elements of the same type, to avoid loading the same VAO over and over 
		for(TexturizedModel texturizedModel : entities.keySet()){
			// For every type of entity
			prepareTexturizedModel(texturizedModel);
			List<Entity> setOfEntities = entities.get(texturizedModel);			
			for(Entity entity : setOfEntities){
				// For every instance of the same entity
				prepareEntity(entity);
				glDrawElements(GL_TRIANGLES, texturizedModel.getRawModel().getVertexNumberOf(), GL_UNSIGNED_INT, 0);
			}			
			unbindTexturizedModel();		
		}
	}
	
	public void renderPlayer(Player player){
		TexturizedModel texturizedModel = player.getTexturizedModel();
		prepareTexturizedModel(texturizedModel);
		prepareEntity(player);
		glDrawElements(GL_TRIANGLES, texturizedModel.getRawModel().getVertexNumberOf(), GL_UNSIGNED_INT, 0);
		unbindTexturizedModel();
		
	}
	
	private void prepareTexturizedModel(TexturizedModel texturizedModel){
		// Load the proper buffers needed for this entity 
		RawModel rawModel = texturizedModel.getRawModel();
		glBindVertexArray(rawModel.getVertexArrayObjectID());		
		glEnableVertexAttribArray(0); // positions
		glEnableVertexAttribArray(1); // texture Coordinates
		glEnableVertexAttribArray(2); // normals	
		ModelTexture modelTexture = texturizedModel.getModelTexture();		
		if(modelTexture.isTransparent()) GlobalModelRenderer.disabelCulling();
		normalModelShader.loadSpecularLight(modelTexture.getShiningPercentage(), modelTexture.getReflectivePercentage());
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texturizedModel.getModelTexture().getTextureID());
	}
	
	private void unbindTexturizedModel(){
		GlobalModelRenderer.enableCulling();
		glDisableVertexAttribArray(0);		
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);		
	}
	
	private void prepareEntity(Entity entity){
		// Load the proper transformation matrix needed for this entity
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotationX(), entity.getRotationY(), entity.getRotationZ(), entity.getScale());
		normalModelShader.loadTransformationMatrix(transformationMatrix);
	}
}
