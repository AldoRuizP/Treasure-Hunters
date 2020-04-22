package transparentEntities;

import org.lwjgl.util.vector.Matrix4f;

import entities.ViewCamera;
import math.Maths;
import shaders.ProgramShader;

public class TransparentStaticShader extends ProgramShader {

	private static final String VERTEX_FILE = "src/transparentEntities/transparentVertexShader";
	private static final String FRAGMENT_FILE = "src/transparentEntities/transparentFragmentShader";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	public TransparentStaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttributes(0, "position");
		super.bindAttributes(1, "color");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformVariableLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformVariableLocation("projectionMatrix");
		location_viewMatrix = super.getUniformVariableLocation("viewMatrix");
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadViewMatrix(ViewCamera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

}
