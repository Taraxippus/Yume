package com.taraxippus.yume.game.gameobject;

import android.opengl.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;

public class ReflectionObject extends GameObject
{
	public final SceneObject parent;
	public final float[] modelMatrix = new float[16];
	public final VectorF position = new VectorF();
	
	public ReflectionObject(SceneObject parent)
	{
		super(parent.world);
		this.parent = parent;
	}

	@Override
	public Pass getPass()
	{
		return Pass.REFLECTION;
	}

	@Override
	public void render(Renderer renderer)
	{
		if (!parent.enabled || !world.main.camera.insideFrustum(position.set(parent.position.x, -parent.position.y, parent.position.z), parent.radius))
			return;
		
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, parent.position.x, -parent.position.y, parent.position.z);
		Matrix.rotateM(modelMatrix, 0, parent.rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, parent.rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, parent.rotation.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix, 0, parent.scale.x, -parent.scale.y, parent.scale.z);
		
		renderer.uniform(modelMatrix, getPass());
		GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), parent.color.x, parent.color.y, parent.color.z, parent.alpha);
		GLES20.glUniform1f(getPass().getProgram().getUniform("u_Specularity"), parent.specularity);
		
		if (parent.shape != null)
			parent.shape.render();
	}
	
	@Override
	public float getDepth()
	{
		return parent.getDepth();
	}
}
