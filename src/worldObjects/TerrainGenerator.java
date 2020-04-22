package worldObjects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import math.Maths;
import models.RawModel;
import rendering.ModelLoader;

public class TerrainGenerator {

	// Generates the terrain based on the input file
	// To generate the terrain the X and Z components are created from an algorithm
	// The Y component is obtained from the file
	// The normals are obtained from an algorithm which depends on the neighbor vertexes
	// The indexes are obtained from an algorithm
	// The texture coordinates are obtained from an algorithm. Each square gets the full texture image

	private static final float MINIMUM_HEIGHT = -100;

	private static float textureCoords[];
	private static float yPositions[];
	private static float vertexes[];
	private static float normals[];
	private static int indexes[];
	private static int size;
	private static int numberOfVertex;
	private static int totalNumberOfVertex;
	private static ModelLoader loader;
	private static float[][] heights;
	private static String nameOfFile;

	private static int xGrid, zGrid;

	public TerrainGenerator(int size, int numberOfVertex, ModelLoader loader, String nameOfFile) {
		TerrainGenerator.size = size;
		TerrainGenerator.numberOfVertex = numberOfVertex;
		TerrainGenerator.totalNumberOfVertex = numberOfVertex * numberOfVertex;
		TerrainGenerator.loader = loader;
		TerrainGenerator.nameOfFile = nameOfFile;
	}

	public RawModel generateTerrain() {
		// Init the needed variables
		textureCoords = new float[totalNumberOfVertex * 2];
		vertexes = new float[totalNumberOfVertex * 3];
		normals = new float[totalNumberOfVertex * 3];
		yPositions = new float[totalNumberOfVertex];
		indexes = new int[(numberOfVertex - 1) * (numberOfVertex - 1) * 6];
		heights = new float[numberOfVertex][numberOfVertex];
		loadTerrainHeight();
		fillVertexArray();
		fillNormalsArray();
		fillIndexArray();
		fillTextureCoordsArray();
		return loader.loadIntoVertexArrayObject(vertexes, indexes, textureCoords, normals);
	}

	private static void loadTerrainHeight() {

		// Load heights from the txt file. The Y component is the only thing
		// that shows in the input
		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/" + nameOfFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		int p = -1;

		try {
			line = reader.readLine(); // skip first line
			while (true) {
				line = reader.readLine();
				String[] currentLine = line.split(";");
				for (int i = 0; i < numberOfVertex; i++) {
					float yPos = Float.parseFloat(currentLine[i]);
					yPositions[++p] = yPos * 10;
					// yPositions[++p] = 0;
				}
			}
		} catch (Exception e) {
		}
		try {
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void fillVertexArray() {
		// Fill the array of positions
		// X and Z can be calculated from this function. The Y is from the input
		float lenght = size / (float) (numberOfVertex - 1);
		int p = 0;
		float currentXPos = 0;
		float currentZPos = 0;

		// First, we fill the X and Z components. We leave the Y in 0 for this
		// part
		for (int i = 0; i < numberOfVertex; i++) {
			currentXPos = 0;
			for (int j = 0; j < numberOfVertex; j++) {
				vertexes[p] = currentXPos; // x coord
				vertexes[p + 1] = 0; // y coord
				vertexes[p + 2] = currentZPos; // z coord
				p += 3;
				currentXPos += lenght;
			}
			currentZPos += lenght;
		}

		// In this part, we set the Y of each vector
		int vertexPosCounter = 1;
		int yPosPointer = totalNumberOfVertex - 1;
		int yPosPointerControler = yPosPointer;
		for (int i = 0; i < numberOfVertex; i++) {
			for (int j = 0; j < numberOfVertex; j++) {
				vertexes[vertexPosCounter] = yPositions[yPosPointer];
				vertexPosCounter += 3;
				yPosPointer -= 1;
			}
			yPosPointerControler -= numberOfVertex;
			yPosPointer = yPosPointerControler;
		}

		p = 1;
		for (int i = 0; i < numberOfVertex; i++) {
			for (int j = (numberOfVertex - 1); j >= 0; j--) {
				heights[j][i] = vertexes[p];
				p += 3;
			}
		}
	}

	private void fillNormalsArray() {
		// Find the normal of each vertex
		int p = -1;
		for (int i = 0; i < totalNumberOfVertex; i++) {
			Vector3f normal = calculateNormal(i);
			normals[++p] = (float) normal.x;
			normals[++p] = (float) normal.y;
			normals[++p] = (float) normal.z;
		}
	}

	private static Vector3f calculateNormal(int n) {

		// To calculate the normal of a specific vertex, we need to find frist
		// the adjacent vertexes from the current point
		int pointer = (totalNumberOfVertex - 1) - n;
		float yLeft = (pointer % numberOfVertex == 0) ? 0 : yPositions[pointer - 1];
		float yRight = ((pointer + 1) % numberOfVertex == 0) ? 0 : yPositions[pointer + 1];
		float yUp = ((pointer - numberOfVertex) <= 0) ? 0 : yPositions[pointer - numberOfVertex];
		float yDown = ((pointer + numberOfVertex) >= totalNumberOfVertex) ? 0 : yPositions[pointer + numberOfVertex];

		// With the adjacents vertexes Y component, we can calculate the current
		// vertex normal
		float normalX = yRight - yLeft;
		float normalY = 30f;
		float normalZ = yDown - yUp;
		Vector3f normal = new Vector3f(normalX, normalY, normalZ);
		normal.normalise();
		return normal;
	}

	// Fill the indexes
	private static void fillIndexArray() {
		int a = 0;
		int b = numberOfVertex;
		int p = -1;
		int indexPerLine = (numberOfVertex - 1) * 2;

		boolean change = true; // if change = true, increase a. Else increase b

		for (int i = 0; i < numberOfVertex - 1; i++) {
			a = numberOfVertex * i;
			b = numberOfVertex + a;
			for (int j = 0; j < indexPerLine; j++) {
				indexes[++p] = a;
				indexes[++p] = b;
				if (change) {
					a++;
					indexes[++p] = a;
				} else {
					b++;
					indexes[++p] = b;
				}
				change = !change;
			}
		}
	}

	private void fillTextureCoordsArray() {

		// Fill the texture coordinates
		// Each individual square of the terrain, gets the full texture
		boolean iPow2 = false;
		boolean jPow2 = false;
		int p = -1;

		for (int i = 0; i < numberOfVertex; i++) {
			iPow2 = (i == 0 || i % 2 == 0) ? true : false;
			for (int j = 0; j < numberOfVertex; j++) {
				jPow2 = (j == 0 || j % 2 == 0) ? true : false;
				if (iPow2 && jPow2) {
					textureCoords[++p] = 0;
					textureCoords[++p] = 1;
				} else if (iPow2 && !jPow2) {
					textureCoords[++p] = 1;
					textureCoords[++p] = 1;
				} else if (!iPow2 && jPow2) {
					textureCoords[++p] = 0;
					textureCoords[++p] = 0;
				} else if (!iPow2 && !jPow2) {
					textureCoords[++p] = 1;
					textureCoords[++p] = 0;
				}
			}
		}
	}

	// Get the X grid of a position
	public int getXGridOfElement(float wx) {
		float tx = -wx + (size);
		float gridSize = (float) size / (numberOfVertex - 1);
		int gridX = (int) Math.floor(tx / gridSize);
		return gridX;
	}

	// Get the Z grid of a position
	public int getZGridOfElement(float wz) {
		float tz = wz - (size * -1);
		float gridSize = (float) size / (numberOfVertex - 1);
		int gridZ = (int) Math.floor(tz / gridSize);
		return gridZ;
	}

	public float getHeightOfGridSquaree(float realX, float realZ) {
		// Get the height of a grid The interpolated barycentric coordinate system function is needed to accomplish this.
		float terrainX = -realX + (size);
		float terrainZ = realZ - (size * -1);
		float gridSize = (float) size / (numberOfVertex - 1);
		int gridX = (int) Math.floor(terrainX / gridSize);
		int gridZ = (int) Math.floor(terrainZ / gridSize);

		setXGrid(gridX);
		setZGrid(gridZ);

		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0)
			return MINIMUM_HEIGHT;

		float xCoord = (terrainX % gridSize) / gridSize;
		float zCoord = (terrainZ % gridSize) / gridSize;

		int var1 = xCoord <= (1 - zCoord) ? 0 : 1;
		float height = Maths.getBaryCentricPoint(new Vector3f(var1, heights[gridX + var1][gridZ], 0),
				new Vector3f(1, heights[gridX + 1][gridZ + var1], var1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
				new Vector2f(xCoord, zCoord));

		return height;
	}

	public int getXGrid() {
		// X grid in where the player is standing on
		return xGrid;
	}

	public static void setXGrid(int xGrid) {
		// X grid in where the player is staing on
		TerrainGenerator.xGrid = xGrid;
	}

	public int getZGrid() {
		return zGrid;
	}

	public static void setZGrid(int zGrid) {
		TerrainGenerator.zGrid = zGrid;
	}
}
