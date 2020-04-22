package rendering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

public class ModelOBJLoader {
	
	/*
	 * OBJ Parser
	 * Reads an OBJ file and loads the proper vertexes, indexes, texture coordinates and normals into VAO's
	 * This parser was mostly based on the one created by Fabien Sanglard in 2008 and can be obtained from
	 * http://fabiensanglard.net/Mykaruga/index.php
	 */

	public static RawModel loadOBJModel(String nameOfFile, ModelLoader modelLoader) {

		// Lists are to iterate through file
		List<Vector3f> vertexes = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals  = new ArrayList<Vector3f>();
		List<Integer> indexes   = new ArrayList<Integer>();

		// Arrays are to create models
		float[] vertexArray = null;
		float[] normalsArray = null;
		float[] texturesArray = null;
		int[] indexArray = null;

		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/" + nameOfFile + "/" + nameOfFile + ".obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;

		try {
			while (true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					// If so, then its a vertex position
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					vertexes.add(vertex);
				}
				else if (line.startsWith("vt ")) {
					// If so, then its a texture coordinate
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					// If so, then its a normal vector
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					// If so, then its a face
					texturesArray = new float[vertexes.size() * 2];
					normalsArray = new float[vertexes.size() * 3];
					break;
				}
			}

			while (line != null) {
				if (!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, indexes, textures, normals, texturesArray, normalsArray);
				processVertex(vertex2, indexes, textures, normals, texturesArray, normalsArray);
				processVertex(vertex3, indexes, textures, normals, texturesArray, normalsArray);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) { e.printStackTrace(); }

		vertexArray = new float[vertexes.size() * 3];
		indexArray = new int[indexes.size()];
		int vertexPointer = 0;

		for (Vector3f vertex : vertexes) {
			vertexArray[vertexPointer++] = vertex.x;
			vertexArray[vertexPointer++] = vertex.y;
			vertexArray[vertexPointer++] = vertex.z;
		}

		for (int i = 0; i < indexes.size(); i++) 
			indexArray[i] = indexes.get(i);
		
		return modelLoader.loadIntoVertexArrayObject(vertexArray, indexArray, texturesArray, normalsArray);
	}

	private static void processVertex(String[] vertexData, List<Integer> indexes, List<Vector2f> textures,
			List<Vector3f> normals, float[] texturesArray, float[] normalsArray) {

		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indexes.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
		texturesArray[currentVertexPointer * 2] = currentTex.x;
		texturesArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[currentVertexPointer * 3] = currentNorm.x;
		normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
		normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
	}
}
