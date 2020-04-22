package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.SourceOfLight;
import entities.ViewCamera;
import math.Maths;

public class NormalModelShader extends ProgramShader {
	
	/*
	 * Shader needed for most of the objtecs
	 * Implements the transformation, projection and view matrixes
	 */
	
	private static final String VERTEX_SHADER_FILE_NAME = "src/shaders/vertexShader";
	private static final String FRAGMENT_SHADER_FILE_NAME = "src/shaders/fragmentShader";
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColor;
	private int location_reflectivePercentage;
	private int location_shinePercentage;
	
	public NormalModelShader() {
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
		// Uniform locs
		location_transformationMatrix = super.getUniformVariableLocation("transformationMatrix");
		location_projectionMatrix     = super.getUniformVariableLocation("projectionMatrix");
		location_viewMatrix = super.getUniformVariableLocation("viewMatrix");
		location_lightPosition = super.getUniformVariableLocation("lightPosition");
		location_lightColor = super.getUniformVariableLocation("lightColor");
		location_shinePercentage = super.getUniformVariableLocation("shinePercentage");
		location_reflectivePercentage = super.getUniformVariableLocation("reflectivePercentage");
	}
	
	
	// Functions to load uniform values
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
