package playerStats;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ProgramShader;

public class StatsShader extends ProgramShader {
	
	/*
	 * Shader just for the game/player stats
	 */
	
	private static final String VERTEX_SHADER_FILENAME   = "src/playerStats/statsVertexShader";
	private static final String FRAGMENT_SHADER_FILENAME = "src/playerStats/statsFragmentShader";
	private int location_transformationMatrix;
	
	public StatsShader() {
		super(VERTEX_SHADER_FILENAME, FRAGMENT_SHADER_FILENAME);
	}
	
	public void loadTransformation(Matrix4f matrix){
		// Transformation matrix to rotate/scale/translate
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		// Get matrix loc
		location_transformationMatrix = super.getUniformVariableLocation("transformationMatrix");		
	}

	@Override
	protected void bindAttributes() {
		// Bind position buffer
		super.bindAttributes(0, "position");		
	}
	
}
