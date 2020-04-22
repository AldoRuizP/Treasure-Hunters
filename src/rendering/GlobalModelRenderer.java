package rendering;

import shaders.NormalModelShader;
import shaders.TerrainShader;
import transparentEntities.TransparentEntitiesRenderer;
import transparentEntities.TransparentEntity;
import transparentEntities.TransparentStaticShader;
import worldObjects.Terrain;
import models.RawModel;
import models.TexturizedModel;

import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import entities.SourceOfLight;
import entities.ViewCamera;

public class GlobalModelRenderer {
	
	/*
	 * This class manages others rendering process for optimization
	 */
	
	
	// Frustum specifications
	private static final float FIELD_OF_VIEW = 80;
	private static final float NEAR_PLANE = 0.001f;	
	private static final float FAR_PLANE = 2000;
	private static final float FRUSTUM_LENGHT = FAR_PLANE - NEAR_PLANE;
	
	// Color of the sky
	private static final float SKY_COLOR_R = 0.5294f;
	private static final float SKY_COLOR_G = 0.8078f;
	private static final float SKY_COLOR_B = 0.9803f;
	
	private Terrain terrain;
	private Matrix4f projectionMatrix;
	private ModelRenderer modelRenderer;
	private TerrainRenderer terrainRenderer;
	private TransparentEntitiesRenderer transparentRenderer;
	
	private TerrainShader terrainShader = new TerrainShader();
	private NormalModelShader normalModelShader = new NormalModelShader();
	private TransparentStaticShader transparentShader = new TransparentStaticShader();	
	
	private Map<TexturizedModel, List<Entity>> mapOfEntities = new HashMap<TexturizedModel,List<Entity>>();
	private Map<RawModel, List<TransparentEntity>> mapOfTransparentEntities = new HashMap<RawModel, List<TransparentEntity>>();
	
	public GlobalModelRenderer(){
		enableCulling();
		createProjectionMatrix();
		
		// Create renderers
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);		
		transparentRenderer = new TransparentEntitiesRenderer(transparentShader, projectionMatrix);
		modelRenderer   = new ModelRenderer(normalModelShader, projectionMatrix);
	}
	
	public static void enableCulling(){
		// Dont render the back triangles
		glEnable(GL_CULL_FACE); 
		glCullFace(GL_BACK);
	}
	
	public static void disabelCulling(){
		// Do render the back triangles. Needed for flat objects like the grass
		glDisable(GL_CULL_FACE);
	}

	private void createProjectionMatrix(){
		// Create the basic projection matrix
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FIELD_OF_VIEW / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / FRUSTUM_LENGHT);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / FRUSTUM_LENGHT);
        projectionMatrix.m33 = 0;
    }
	
	public void globalRender(SourceOfLight sourceOfLight, ViewCamera viewCamera){
		
		setUp(); // Initial setup for all the rendering
		
		// Render first the regular entities
		normalModelShader.startProgram(); // Start shaders
		normalModelShader.loadSourceOfLight(sourceOfLight); // Load light
		normalModelShader.loadViewMatrix(viewCamera); // Load view matrix
		modelRenderer.renderGlobalEntity(mapOfEntities); // render entities
		normalModelShader.stopProgram(); // stop shader

		// Render the terrain
		terrainShader.startProgram();
		terrainShader.loadSourceOfLight(sourceOfLight);
		terrainShader.loadViewMatrix(viewCamera);
		terrainRenderer.render(terrain);
		terrainShader.stopProgram();	
				
		// Render the transparent entities (crystals)
		transparentShader.startProgram();
        transparentShader.loadViewMatrix(viewCamera);  
        transparentRenderer.renderSimple(mapOfTransparentEntities);
        transparentShader.stopProgram();          
    
        // Must be cleared to avoid rendering the same objects over themselves for ever
		mapOfEntities.clear(); 
		mapOfTransparentEntities.clear();
		terrain = null;
	}
	
	public void setUp(){ 
		glEnable(GL_BLEND); // Enable blending
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST); // Enable depth test
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear color buffer
		glClearColor(SKY_COLOR_R, SKY_COLOR_G, SKY_COLOR_B, 1f);  // Clear color
	}
	
	
	public void renderTerrain(Terrain terrain){
		this.terrain = terrain;
	}	
	
	public void renderTransparent(TransparentEntity entity){	
		
		// Process load all crystals in a single list		
		RawModel rawModel = entity.getRawModel();
		List<TransparentEntity> setOfModels = mapOfTransparentEntities.get(rawModel);
		if(setOfModels != null)
			setOfModels.add(entity);
		else{
			List<TransparentEntity> newSetOfModels = new ArrayList<TransparentEntity>();
			newSetOfModels.add(entity);
			mapOfTransparentEntities.put(rawModel, newSetOfModels);			
		}		
	}
	
	
	public void renderEntity(Entity entity){
		
		// Process to lead all entities in a single list
		TexturizedModel texturizedModel = entity.getTexturizedModel();
		List<Entity> setOfModels = mapOfEntities.get(texturizedModel);		
		if(setOfModels != null)
			setOfModels.add(entity);
		else{
			List<Entity> newSetOfModels = new ArrayList<Entity>();
			newSetOfModels.add(entity);
			mapOfEntities.put(texturizedModel, newSetOfModels);			
		}		
	}
	
	public void deleteAll(){
		normalModelShader.cleanProgram();	
		terrainShader.cleanProgram();
		transparentShader.cleanProgram();
	}
}
