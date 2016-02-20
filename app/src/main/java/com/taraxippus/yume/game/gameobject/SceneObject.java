package com.taraxippus.yume.game.gameobject;

import android.graphics.*;
import android.opengl.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;

import android.opengl.Matrix;

public class SceneObject extends GameObject
{
	public final float[] modelMatrix = new float[16];
	public final float[] invModelMatrix = new float[16];
	
	public final VectorF color = new VectorF(0xCC / 255F, 0xCC / 255F, 0xCC / 255F);
	public float alpha = 1F;
	public float specularity = 20F;
	
	public final VectorF position = new VectorF();
	public final VectorF scale = new VectorF(1, 1, 1);
	public final VectorF rotationPre = new VectorF();
	public final VectorF rotation = new VectorF();
	
	public final VectorF tmp = new VectorF();
	
	public float radius;
	
	public boolean touchable = false;
	public boolean enabled = true;
	public boolean hasReflection = true;
	private ReflectionObject[] reflection;
	
	public SceneObject(World world)
	{
		super(world);
		
		this.updateMatrix();
	}

	@Override
	public void init()
	{
		super.init();
		
		if (hasReflection)
		{
			this.reflection = new ReflectionObject[getPass() == Pass.REFLECTION ? 27 : 26];
			
			int x, y, z, offset = 0;
			for (x = -1; x <= 1; ++x)
				for (y = -1; y <= 1; ++y)
					for (z = -1; z <= 1; ++z)
						if (getPass() == Pass.REFLECTION || x != 0 || y != 0 || z != 0)
							world.add(this.reflection[offset++] = new ReflectionObject(this, new VectorF(x, y, z)).setPostPass(getPass() == Pass.REFLECTION));

		}
	}

	@Override
	public void delete()
	{
		super.delete();
		
		if (hasReflection)
			for (ReflectionObject reflectionObject : reflection)
				world.remove(reflectionObject);
		
	}

	
	public SceneObject setColor(int rgb)
	{
		this.color.set(Color.red(rgb) / 255F, Color.green(rgb) / 255F, Color.blue(rgb) / 255F);
		
		return this;
	}
	
	public SceneObject setAlpha(float alpha)
	{
		this.alpha = alpha;
		return this;
	}
	
	public SceneObject setTouchable(boolean touchable)
	{
		this.touchable = touchable;
		
		return this;
	}
	
	public SceneObject setHasReflection(boolean hasReflection)
	{
		this.hasReflection = hasReflection;

		return this;
	}
	
	public void onTouch(VectorF intersection, VectorF normal)
	{
		
	}
	
	public void onLongTouch(VectorF intersection, VectorF normal)
	{

	}
	
	public void onSingleTouch(VectorF intersection, VectorF normal)
	{

	}
	
	public void onDoubleTouch(VectorF intersection, VectorF normal)
	{

	}
	
	public SceneObject setEnabled(boolean enabled)
	{
		this.enabled = enabled;

		return this;
	}
	
	public SceneObject translate(float x, float y, float z)
	{
		this.position.add(x, y, z);
		
		this.updateMatrix();
		
		return this;
	}
	
	public SceneObject rotate(float x, float y, float z)
	{
		this.rotation.add(x, y, z);
		
		this.updateMatrix();
		
		return this;
	}
	
	public SceneObject scale(float x, float y, float z)
	{
		this.scale.multiplyBy(x, y, z);
		
		this.updateMatrix();
		
		return this;
	}
	
	public void updateMatrix()
	{
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
		Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);
		
		Matrix.rotateM(modelMatrix, 0, rotationPre.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, rotationPre.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, rotationPre.z, 0, 0, 1);
		
		Matrix.rotateM(modelMatrix, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.z, 0, 0, 1);
		
		Matrix.invertM(invModelMatrix, 0, modelMatrix, 0);
		
		this.radius = getRadius();
	}
	
	public float getRadius()
	{
		return (float) Math.sqrt(scale.x * scale.x + scale.y * scale.y + scale.z * scale.z);
	}
	
	@Override
	public void render(Renderer renderer)
	{
		if (!enabled || hasReflection && getPass() == Pass.REFLECTION || !world.main.camera.insideFrustum(position, radius))
			return;
		
		renderer.uniform(modelMatrix, getPass());
		GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), color.x, color.y, color.z, alpha);
		GLES20.glUniform1f(getPass().getProgram().getUniform("u_Specularity"), specularity);
		
		if (getPass() == Pass.REFLECTION)
		{
			GLES20.glUniform3f(getPass().getProgram().getUniform("u_Eye"), world.main.camera.eye.x, world.main.camera.eye.y, world.main.camera.eye.z);
			
			GLES20.glUniform3f(getPass().getProgram().getUniform("u_Light"), world.main.game.light.x,world.main.game.light.y, world.main.game.light.z);
			GLES20.glUniform3f(getPass().getProgram().getUniform("u_ReflectionOffset"), 0, 0, 0);
			GLES20.glUniform3f(getPass().getProgram().getUniform("u_ReflectionDir"), 0, 0, 0);

			GLES20.glDepthMask(false);
			GLES20.glCullFace(GLES20.GL_BACK);
		}
		
		super.render(renderer);
	}

	@Override
	public float getDepth()
	{
		return super.getDepth() + (getPass() == Pass.REFLECTION ? 1000 : 0) + tmp.set(position).subtract(world.main.camera.eye).length();
	}
}
