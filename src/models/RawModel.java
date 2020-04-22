package models;

public class RawModel {
	
	/*
	 * The RawModel is the most basic type of model
	 * It contains the buffers arrays
	 */
	
	private int vertexArrayObjectID;
	private int vertexNumberOf;
	
	public RawModel(int vertexArrayObjectID, int vertexNumberOf){
		this.vertexArrayObjectID = vertexArrayObjectID;
		this.vertexNumberOf = vertexNumberOf;
	}
	
	public void setVertexArrayObjectID(int vertexArrayObjectID) {
		this.vertexArrayObjectID = vertexArrayObjectID;
	}

	public void setVertexNumberOf(int vertexNumberOf) {
		this.vertexNumberOf = vertexNumberOf;
	}

	public int getVertexNumberOf() {
		return vertexNumberOf;
	}

	public int getVertexArrayObjectID() {
		return vertexArrayObjectID;
	}
	
}
