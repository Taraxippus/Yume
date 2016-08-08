package com.taraxippus.yume.game.gameobject;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.VectorF;

public class HexagonTube extends SceneObject
{
	public final float[] modelMatrix2 = new float[16];
	public final float[] invModelMatrix2 = new float[16];
	public final float[] normalMatrix2 = new float[16];
	
	Track track;
	
	public HexagonTube(World world, Track track)
	{
		super(world);
		setPass(Pass.HEXAGON_OUTLINE);
		
		this.track = track;
	}

	@Override
	public float getRadius()
	{
		return  (float) Math.sqrt(scale.x * scale.x * Track.SIZE * Track.SIZE * 0.5 * 0.5 + scale.y * scale.y * Track.SIZE * Track.SIZE * 0.5 * 0.5 + scale.z * scale.z * Track.SIZE * Track.SIZE * 0.5 * 0.5);
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
		
		System.arraycopy(track.nextMatrix, 0, modelMatrix2, 0, 16);
		Matrix.rotateM(modelMatrix2, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix2, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix2, 0, rotation.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix2, 0, scale.x, scale.y, scale.z);
		
		Matrix.invertM(invModelMatrix2, 0, modelMatrix2, 0);
		Matrix.transposeM(normalMatrix2, 0, invModelMatrix2, 0);
		
		this.radius = getRadius();
	}

	@Override
	public void uniformParent()
	{
		super.uniformParent();
		
		GLES20.glUniformMatrix4fv(getPass().getParent().getProgram().getUniform("u_M2"), 1, false, modelMatrix2, 0);
	}
	
	@Override
	public void uniformChild()
	{
		super.uniformChild();
		
		GLES20.glUniformMatrix4fv(getPass().getProgram().getUniform("u_N2"), 1, false, normalMatrix2, 0);
		GLES20.glUniformMatrix4fv(getPass().getProgram().getUniform("u_M2"), 1, false, modelMatrix2, 0);
		
		GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), 1, 1, 1, 0.8F);
	}
}
