package com.taraxippus.yume.game.gameobject;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.render.Pass;

public class HexagonTube extends SceneObject
{
	public final float[] modelMatrix2 = new float[16];
	public final float[] invModelMatrix2 = new float[16];
	public final float[] normalMatrix2 = new float[16];
	
	public boolean curve;
	
	public HexagonTube(World world, boolean curve)
	{
		super(world);
		setPass(Pass.HEXAGON_OUTLINE);
		
		this.curve = curve;
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
		
		Matrix.setIdentityM(modelMatrix2, 0);
		Matrix.translateM(modelMatrix2, 0, position.x, position.y, position.z);
		if (curve)
			Matrix.rotateM(modelMatrix2, 0, 90, 1, 0, 0);
		Matrix.rotateM(modelMatrix2, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix2, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix2, 0, rotation.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix2, 0, scale.x, scale.y, scale.z);
		
		Matrix.invertM(invModelMatrix2, 0, modelMatrix2, 0);
		
		Matrix.setIdentityM(normalMatrix2, 0);
		if (curve)
		Matrix.rotateM(normalMatrix2, 0, 90, 1, 0, 0);
		Matrix.rotateM(normalMatrix2, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(normalMatrix2, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(normalMatrix2, 0, rotation.z, 0, 0, 1);
		
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
