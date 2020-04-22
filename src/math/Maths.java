package math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.ViewCamera;

public class Maths {
	
	
	/* The barycentric interpolate coordinate system funcion was obtained from gamedev.net
	 * Published by the user Eric Rufelt on March 11, 2011
	 * http://www.gamedev.net/topic/597393-getting-the-height-of-a-point-on-a-triangle 
	 */	
	public static float getBaryCentricPoint(Vector3f vec1, Vector3f vec2, Vector3f vec3, Vector2f cpos) {
		float det = ((vec2.z - vec3.z) * (vec1.x - vec3.x) + (vec3.x - vec2.x) * (vec1.z - vec3.z));
		float l1  = ((vec2.z - vec3.z) * (cpos.x - vec3.x) + (vec3.x - vec2.x) * (cpos.y - vec3.z)) / det;
		float l2  = ((vec3.z - vec1.z) * (cpos.x - vec3.x) + (vec1.x - vec3.x) * (cpos.y - vec3.z)) / det;
		float l3  = 1.0f - l1 - l2;
		return l1 * vec1.y + l2 * vec2.y + l3 * vec3.y;
	}
	
	
	
	/*
	 * The functions to creat the matrixes where bases on the material seen in the course and in the examples
	 * shown in the following link
	 *  http://www.programcreek.com/java-api-examples/index.php?class=org.lwjgl.util.vector.Matrix4f&method=rotate
	 */
	public static Matrix4f createViewMatrix(ViewCamera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rotationX, float rotationY, float rotationZ,float scale){
		Matrix4f result = new Matrix4f();
		result.setIdentity();
		Matrix4f.translate(translation, result, result);
		Matrix4f.rotate((float)Math.toRadians(rotationX), new Vector3f(1,0,0) ,result, result);
		Matrix4f.rotate((float)Math.toRadians(rotationY), new Vector3f(0,1,0) ,result, result);
		Matrix4f.rotate((float)Math.toRadians(rotationZ), new Vector3f(0,0,1) ,result, result);
		Matrix4f.scale(new Vector3f(scale, scale, scale), result, result);
		return result;		
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
}
