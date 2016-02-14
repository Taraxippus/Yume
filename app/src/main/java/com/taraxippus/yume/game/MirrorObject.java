package com.taraxippus.yume.game;
import android.opengl.*;
import com.taraxippus.yume.render.*;

public class MirrorObject extends GameObject
{
	public final SceneObject parent;
	public final float[] modelMatrix = new float[16];
	
	public MirrorObject(SceneObject parent)
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
		Matrix.scaleM(modelMatrix, 0, parent.modelMatrix, 0, 1, 1, 1);
		Matrix.translateM(modelMatrix, 0, 0, -parent.y * 2, 0);
		
		renderer.uniform(modelMatrix, getPass());
		GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), parent.r, parent.g, parent.b, parent.specularity);
		
		if (parent.shape != null)
			parent.shape.render();
	}
}
