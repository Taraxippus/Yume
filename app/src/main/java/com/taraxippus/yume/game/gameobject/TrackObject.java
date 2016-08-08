package com.taraxippus.yume.game.gameobject;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Renderer;

public class TrackObject extends SceneObject
{
	public final Track track;
	
	public TrackObject(World world, Track track)
	{
		super(world);
		
		this.track = track;
	}
	
	@Override
	public void render(Renderer renderer)
	{
		if (renderer.currentPass == getPass().getParent())
		{
			if (!enabled || !world.main.camera.insideFrustum(position, radius))
			{
				noUpdate = !updateAlways;
				return;
			}

			noUpdate = false;
	
			renderer.uniform(modelMatrix, invModelMatrix, getPass().getParent());
			uniformParent();
			if (shape != null)
				shape.render();
		}
		else
		{
			if (outlineShape == null)
				outlineShape = createOutlineShape();

			GLES20.glCullFace(GLES20.GL_FRONT);
			renderer.uniform(modelMatrix, normalMatrix, getPass());
			uniformChild();
			if (outlineShape != null)
				outlineShape.render();
			GLES20.glCullFace(GLES20.GL_BACK);
		}
	}
	
	public boolean renderPass(Pass pass)
	{
		return getPass().getParent() == pass || getPass() == pass;
	}
	
	@Override
	public void updateMatrix()
	{
		System.arraycopy(track.lastMatrix, 0, modelMatrix, 0, 16);
		Matrix.rotateM(modelMatrix, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);
		this.position.set(0, 0, 0).multiplyBy(modelMatrix);
		
		Matrix.invertM(invModelMatrix, 0, modelMatrix, 0);
		Matrix.transposeM(normalMatrix, 0, invModelMatrix, 0);
		
		this.radius = getRadius();
	}

	@Override
	public float getRadius()
	{
		return  (float) Math.sqrt(scale.x * scale.x * Track.SIZE * Track.SIZE * 0.5 * 0.5 + scale.y * scale.y * Track.SIZE * Track.SIZE * 0.5 * 0.5 + scale.z * scale.z * Track.SIZE * Track.SIZE * 0.5 * 0.5);
	}
}
