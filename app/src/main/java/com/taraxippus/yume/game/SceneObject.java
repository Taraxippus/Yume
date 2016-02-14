package com.taraxippus.yume.game;
import android.graphics.*;
import android.opengl.*;
import com.taraxippus.yume.render.*;

import android.opengl.Matrix;

public class SceneObject extends GameObject
{
	public final float[] modelMatrix = new float[16];
	public float r = 0xCC / 255F, g = 0xCC / 255F, b = 0xCC / 255F;
	public float specularity = 20F;
	
	public float x, y, z;
	public float scaleX = 1, scaleY = 1, scaleZ = 1;
	public float rotX, rotY, rotZ;
	
	public SceneObject(World world)
	{
		super(world);
		
		this.updateMatrix();
	}

	public SceneObject setColor(int rgb)
	{
		this.r = Color.red(rgb) / 255F;
		this.g = Color.green(rgb) / 255F;
		this.b = Color.blue(rgb) / 255F;
		
		return this;
	}
	
	public SceneObject translate(float x, float y, float z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		
		this.updateMatrix();
		
		return this;
	}
	
	public SceneObject rotate(float x, float y, float z)
	{
		this.rotX += x;
		this.rotY += y;
		this.rotZ += z;
		
		this.updateMatrix();
		
		return this;
	}
	
	public SceneObject scale(float x, float y, float z)
	{
		this.scaleX *= x;
		this.scaleY *= y;
		this.scaleZ *= z;
		
		this.updateMatrix();
		
		return this;
	}
	
	public void updateMatrix()
	{
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, z);
		Matrix.rotateM(modelMatrix, 0, rotY, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, rotX, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, rotZ, 0, 0, 1);
		Matrix.scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);
	}
	
	@Override
	public void render(Renderer renderer)
	{
		renderer.uniform(modelMatrix, getPass());
		GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), r, g, b, specularity);
		
		super.render(renderer);
	}
}
