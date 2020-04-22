package transparentEntities;

import models.RawModel;
import rendering.GlobalModelRenderer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import java.util.List;
import java.util.Map;
import org.lwjgl.util.vector.Matrix4f;
import math.Maths;

public class TransparentEntitiesRenderer {
	
	/*
	 * This class renders the crystals
	 */

	private TransparentStaticShader tStaticShader;

	public TransparentEntitiesRenderer(TransparentStaticShader shader, Matrix4f projectionMatrix) {
		this.tStaticShader = shader;
		tStaticShader.startProgram();
		tStaticShader.loadProjectionMatrix(projectionMatrix);
		tStaticShader.stopProgram();
	}

	public void prepareRawModel(RawModel rawModel) {
		glBindVertexArray(rawModel.getVertexArrayObjectID());
		glEnableVertexAttribArray(0); // positions
		glEnableVertexAttribArray(1); // colors
	}

	private void prepareEntity(TransparentEntity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		tStaticShader.loadTransformationMatrix(transformationMatrix);
	}

	private void unbindRawModel() {
		GlobalModelRenderer.enableCulling();
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	public void renderSimple(Map<RawModel, List<TransparentEntity>> entities) {

		
		for (RawModel rawModel : entities.keySet()) {
			prepareRawModel(rawModel);
			List<TransparentEntity> setOfEntities = entities.get(rawModel);
			for (TransparentEntity entity : setOfEntities) {
				prepareEntity(entity);				
				glDrawElements(GL_TRIANGLE_STRIP, rawModel.getVertexNumberOf(), GL_UNSIGNED_INT, 0);
			}
			unbindRawModel();
		}

	}

}