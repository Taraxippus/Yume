package com.taraxippus.yume.game;

import android.opengl.*;
import com.taraxippus.yume.*;
import java.util.*;

public class Camera
{
	public static final float Z_NEAR = 1;
	public static final float Z_FAR = 100;
	public static final float FOV = 60;
	
	public final Main main;
	
	final float[] viewMatrix = new float[16];
	final float[] projectionMatrix = new float[16];
	public final float[] viewProjectionMatrix = new float[16];
	
	public float zoom = 1;
	public float rotX = -5;
	public float rotY = 0;
	
	public final float[] eye = new float[] {0, 0, 5, 1};
	
	public Camera(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		updateView();
	}
	
	public void onResize(int width, int height)
	{
		Matrix.perspectiveM(projectionMatrix, 0, FOV, (float) width / height, Z_NEAR, Z_FAR);
		//Matrix.frustumM(projectionMatrix, 0, -(float) width / height, (float)width / height, -1, 1, Z_NEAR, Z_FAR);
		this.updateViewProjection();
	}

	public void updateView()
	{
		eye[0] = 0;
		eye[1] = 0;
		eye[2] = 5;
		eye[3] = 1;
		
		Matrix.setIdentityM(viewMatrix, 0);
		Matrix.rotateM(viewMatrix, 0, rotY, 0, 1, 0);
		Matrix.rotateM(viewMatrix, 0, rotX, 1, 0, 0);
		Matrix.scaleM(viewMatrix, 0, zoom, zoom, zoom);
		
		Matrix.multiplyMV(eye, 0, viewMatrix, 0, eye, 0);
		
		Matrix.setLookAtM(viewMatrix, 0, eye[0] / eye[3], eye[1] / eye[3], eye[2] / eye[3], 0, 0, 0, 0, 1, 0);
		this.updateViewProjection();
	}
	
	public void updateViewProjection()
	{
		Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
	}
}
