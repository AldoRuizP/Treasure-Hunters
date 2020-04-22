package rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.util.vector.*;

import math.Maths;
import models.RawModel;
import shaders.TerrainShader;
import textures.ModelTexture;
import worldObjects.Terrain;

public class TerrainRenderer {
	
	/*
	 * Renders the terrain
	 */
	
	private TerrainShader terrainShader;
	
	public TerrainRenderer(TerrainShader terrainShader, Matrix4f projectionMatrix){
		this.terrainShader = terrainShader;
		terrainShader.startProgram();
		terrainShader.loadProjectionMatrix(projectionMatrix);
		terrainShader.stopProgram();
	}
	
	public void render(Terrain terrain){
		
		RawModel rawModel = terrain.getRawModel();
		glBindVertexArray(rawModel.getVertexArrayObjectID());		
		glEnableVertexAttribArray(0); // positions
		glEnableVertexAttribArray(1); // texture Coordinates
		glEnableVertexAttribArray(2); // normals	
		
		ModelTexture modelTexture = terrain.getModelTexture();
		terrainShader.loadSpecularLight(modelTexture.getShiningPercentage(), modelTexture.getReflectivePercentage());
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, modelTexture.getTextureID());		
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		terrainShader.loadTransformationMatrix(transformationMatrix);	
		
		
		//glDrawElements(GL32.GL_LINE_STRIP_ADJACENCY, terrain.getRawModel().getVertexNumberOf(), GL_UNSIGNED_INT, 0);		
		glDrawElements(GL_TRIANGLES, terrain.getRawModel().getVertexNumberOf(), GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);		
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);	
	}	
	
	
	
	

}
