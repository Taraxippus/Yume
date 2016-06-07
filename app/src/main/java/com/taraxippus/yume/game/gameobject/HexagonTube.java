package com.taraxippus.yume.game.gameobject;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.VectorF;

public class HexagonTube extends TrackObject
{
	public final float[] modelMatrix2 = new float[16];
	public final float[] invModelMatrix2 = new float[16];
	public final float[] normalMatrix2 = new float[16];
	
	public HexagonTube(World world, Track track)
	{
		super(world, track);
		setPass(Pass.HEXAGON_OUTLINE);
	}

	@Override
	public void update()
	{
		super.update();
		
		rotate(0, 0, Main.FIXED_DELTA * 10);
	}
	
	@Override
	public void updateMatrix()
	{
		super.updateMatrix();
		
		System.arraycopy(track.nextMatrix, 0, modelMatrix2, 0, 16);
		Matrix.rotateM(modelMatrix2, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix2, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix2, 0, rotation.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix2, 0, scale.x, scale.y, scale.z);
		
		Matrix.invertM(invModelMatrix2, 0, modelMatrix2, 0);

		VectorF rotation = VectorF.obtain().setEuler(modelMatrix2);

		Matrix.setIdentityM(normalMatrix2, 0);
		Matrix.rotateM(normalMatrix2, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(normalMatrix2, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(normalMatrix2, 0, rotation.z, 0, 0, 1);

		rotation.release();
		
		this.radius = getRadius();
	}
	
	@Override
	public void uniformChild()
	{
		super.uniformChild();
		
		GLES20.glUniformMatrix4fv(getPass().getProgram().getUniform("u_N2"), 1, false, normalMatrix2, 0);
		GLES20.glUniformMatrix4fv(getPass().getProgram().getUniform("u_M2"), 1, false, modelMatrix2, 0);
	}

	@Override
	public void uniformParent()
	{
		super.uniformParent();
		
		GLES20.glUniformMatrix4fv(getPass().getParent().getProgram().getUniform("u_N2"), 1, true, invModelMatrix2, 0);
		GLES20.glUniformMatrix4fv(getPass().getParent().getProgram().getUniform("u_M2"), 1, false, modelMatrix2, 0);
	}
}
