package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class ProgramShader {
	
	/*
	 * Basics of a shader
	 */
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4*4);
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
		
	public ProgramShader(String vertexShaderName, String fragmentShaderName){
		
		// shaders id's
		vertexShaderID   = loadShader(vertexShaderName, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentShaderName, GL_FRAGMENT_SHADER );
		programID = glCreateProgram();
		
		// Attach and link shaders to program
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		glLinkProgram(programID);
		glValidateProgram(programID);	
		getAllUniformLocations();
	}

	protected abstract void getAllUniformLocations();
	protected abstract void bindAttributes();	
	
	public void startProgram(){
		glUseProgram(programID);		
	}
	
	public void stopProgram(){
		glUseProgram(0);
	}
	
	protected int getUniformVariableLocation(String uniformVariable){
		return glGetUniformLocation(programID, uniformVariable);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4(location, false, matrixBuffer);
	}
	
	protected void loadBoolean(int location, boolean value){
		Float boolFloat = 0f;
		if(value) boolFloat = 1.0f;			
		glUniform1f(location, boolFloat);
	}
	
	protected void loadVector(int location, Vector3f vector){
		glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	protected void loadFloat(int location, float value){
		glUniform1f(location, value);
	}
	
	protected void bindAttributes(int attributeID, String nameOfAttribute){
		glBindAttribLocation(programID, attributeID, nameOfAttribute);
	}
	
	private static int loadShader(String name, int type){
		// Function obtained from the LWJGL documentation
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(name));
			String line;
			while((line = reader.readLine())!=null)
				shaderSource.append(line).append("//\n");
			reader.close();
		}catch(IOException e){ e.printStackTrace(); System.exit(-1); }
		
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS )== GL_FALSE){
			System.out.println(glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader!");
			System.exit(-1);
			}
		return shaderID;
	}
	
	public void cleanProgram(){
		stopProgram();
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		glDeleteProgram(programID);		
	}	

}
