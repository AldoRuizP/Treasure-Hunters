package models;

import textures.ModelTexture;

public class TexturizedModel {
	
	/*
	 * The TexturizedModel is an update of the RawModel
	 * It contains the RawModel itself and a ModelTexture
	 */
	
	private RawModel rawModel;
	private ModelTexture modelTexture;
	
	public TexturizedModel(RawModel rawModel, ModelTexture modelTexture){
		this.rawModel = rawModel;
		this.modelTexture = modelTexture;
	}

	public ModelTexture getModelTexture() {
		return modelTexture;
	}
	
	public RawModel getRawModel() {
		return rawModel;
	}
}
