package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.SourceOfLight;
import entities.ViewCamera;
import math.Maths;

public class TerrainShader extends ProgramShader{
	
	/*
	 * Shader for the terrain
	 */

	private static final String VERTEX_SHADER_FILE_NAME = "src/shaders/terrainVertexShader";
	private static final String FRAGMENT_SHADER_FILE_NAME = "src/shaders/terrainFragmentShader";
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColor;
	private int location_reflectivePercentage;
	private int location_shinePercentage;
	
	public TerrainShader() {
		super(VERTEX_SHADER_FILE_NAME, FRAGMENT_SHADER_FILE_NAME);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttributes(0,  "position");
		super.bindAttributes(1, "textureCoordinates");
		super.bindAttributes(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformVariableLocation("transformationMatrix");
		location_projectionMatrix     = super.getUniformVariableLocation("projectionMatrix");
		location_viewMatrix = super.getUniformVariableLocation("viewMatrix");
		location_lightPosition = super.getUniformVariableLocation("lightPosition");
		location_lightColor = super.getUniformVariableLocation("lightColor");
		location_shinePercentage = super.getUniformVariableLocation("shinePercentage");
		location_reflectivePercentage = super.getUniformVariableLocation("reflectivePercentage");
	}
	
	public void loadSpecularLight(float shinePercentage, float reflectivePercentage){
		super.loadFloat(location_reflectivePercentage, reflectivePercentage);
		super.loadFloat(location_shinePercentage, shinePercentage);
	}
	
	public void loadSourceOfLight(SourceOfLight sourceOfLight){
		super.loadVector(location_lightPosition, sourceOfLight.getPosition());
		super.loadVector(location_lightColor, sourceOfLight.getColor());	
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);	
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);	
	}
	
	public void loadViewMatrix(ViewCamera viewCamera){
		Matrix4f matrix = Maths.createViewMatrix(viewCamera);
		super.loadMatrix(location_viewMatrix, matrix);
	}
}
