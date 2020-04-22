package rendering;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

public class ModelLoader {
	
	/*
	 * This class creates the RawModels
	 * It loads all the buffers that the object will need
	 */

	private List<Integer> vertexArrayObjects = new ArrayList<Integer>();
	private List<Integer> vertexBufferObjects = new ArrayList<Integer>();
	private List<Integer> texturesList = new ArrayList<Integer>();

	
	// For Models that need textures, normals, positions and indexes normals
	public RawModel loadIntoVertexArrayObject(float[] positions, int[] indexes, float[] textureCoordinates, float[] normals) {
		int vertexArrayObjectID = createVertexArrayObject();
		bindIndexesBuffer(indexes);
		storeDataIntoAttributeList(0, positions, 3);
		storeDataIntoAttributeList(1, textureCoordinates, 2);
		storeDataIntoAttributeList(2, normals, 3);
		unbindVertexArrayObject();
		return new RawModel(vertexArrayObjectID, indexes.length);
	}
	
	// For models that only need positions buffer
	public RawModel loadIntoVertexArrayObject(float[] positions){
		int vertexArrayObjectID = createVertexArrayObject();
		this.storeDataIntoAttributeList(0, positions, 2);
		unbindVertexArrayObject();
		RawModel rawModel = new RawModel(vertexArrayObjectID, positions.length/2);
		return rawModel;
	}
	
	// For models that need positions, colors and index buffer
	public RawModel loadToVAO_Color(float[] positions,float[] color,int[] indexes){
        int vaoID = createVertexArrayObject();
        bindIndexesBuffer(indexes);
        storeDataIntoAttributeList(0,positions, 3);
        storeDataIntoAttributeList(1, color, 4);
        unbindVertexArrayObject();
        return new RawModel(vaoID,indexes.length);
    }

	private int createVertexArrayObject() {
		// Create a VAO
		int vertexArrayObjectID = glGenVertexArrays();
		vertexArrayObjects.add(vertexArrayObjectID);
		glBindVertexArray(vertexArrayObjectID);
		return vertexArrayObjectID;
	}

	private void bindIndexesBuffer(int[] indexes) {
		// Bind the buffers
		int vertexBufferObjectID = glGenBuffers();
		vertexBufferObjects.add(vertexBufferObjectID);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBufferObjectID);
		IntBuffer buffer = storeDataIntoIntBuffer(indexes);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	private void storeDataIntoAttributeList(int numberOfAttributeList, float[] data, int numberOfElements) {
		// Fill attributes list
		int vertexBufferObjectID = glGenBuffers();
		vertexBufferObjects.add(vertexBufferObjectID);
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObjectID);
		FloatBuffer buffer = storeDataIntoFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(numberOfAttributeList, numberOfElements, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO
	}

	private IntBuffer storeDataIntoIntBuffer(int[] data) {
		// Buffer of integers
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public int loadModelTexture(String textureName) {
		// Load up a texture from file. Folder and file share the same name
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + textureName + "/" + textureName + ".png"));
		} catch (FileNotFoundException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		int textureID = texture.getTextureID();
		texturesList.add(textureID);
		return textureID;
	}
	
	public int loadModelTexture(String folderName, String textureName){
		// Load up a texture from file. Folder and file have different names
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + folderName + "/" + textureName + ".png"));
		} catch (FileNotFoundException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		int textureID = texture.getTextureID();
		texturesList.add(textureID);
		return textureID;
	}

	private FloatBuffer storeDataIntoFloatBuffer(float[] data) {
		// Buffer of floats
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private void unbindVertexArrayObject() {
		glBindVertexArray(0);
	}

	public void deleteAll() {
		for (int vertexArrayObject : vertexArrayObjects)
			glDeleteVertexArrays(vertexArrayObject);
		for (int vertexBufferObject : vertexBufferObjects)
			glDeleteVertexArrays(vertexBufferObject);
		for (int texture : texturesList)
			glDeleteTextures(texture);
	}
}
