package playerStats;

import java.util.List;
import math.Maths;
import models.RawModel;
import rendering.ModelLoader;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.util.vector.Matrix4f;


public class StatsRenderer {

	
	/*
	 * The game stats are just a big square which has the size of the whole screen
	 * This square is later resized and repositioned to get the desired result 
	 * The square gets a texture to represent the game or player status 
	 */
	
	private StatsShader statsShader;	// Special shader for the game stats
	private final static float[] positions = {-1, 1,-1,-1, 1,  1, 1, -1}; // Base square
	private final RawModel statsFrame; // Base square entity 
		
	public StatsRenderer(ModelLoader modelLoader){		
		statsFrame = modelLoader.loadIntoVertexArrayObject(positions);
		statsShader = new StatsShader();
	}	
	
	public void renderStats(List<StatsTexture> statsTextures){
		
		/*
		 * Special render just for the game stats.
		 * Has it's own program and shader
		 */
		
		statsShader.startProgram();
		glBindVertexArray(statsFrame.getVertexArrayObjectID()); // bind arrays
		glEnableVertexAttribArray(0); // vertex attribs
		glEnable(GL_BLEND); // color blending
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);	// color mix func
		for(StatsTexture statsTexture : statsTextures){
			// for every stat that needs to be rendered
			
			glActiveTexture(GL_TEXTURE0); // activate texture
			glBindTexture(GL_TEXTURE_2D, statsTexture.getTexture()); // get texture
			Matrix4f matrix = Maths.createTransformationMatrix(statsTexture.getPosition(), statsTexture.getScale());
			statsShader.loadTransformation(matrix); // Transform object 
			glDrawArrays(GL_TRIANGLE_STRIP, 0, statsFrame.getVertexNumberOf()); // Draw object
		}
		glDisable(GL_BLEND); // After rendering all, disable color blending
		glDisableVertexAttribArray(0); // Disable vertex array
		glBindVertexArray(0); // unbind arrays
		statsShader.stopProgram(); // stop shader
	}
	
	public void deleteAll(){
		statsShader.cleanProgram();
	}
}
