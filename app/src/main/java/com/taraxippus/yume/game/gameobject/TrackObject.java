package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import android.opengl.Matrix;
import com.taraxippus.yume.util.VectorF;

public class TrackObject extends SceneObject
{
	public Track track;
	
	public TrackObject(World world, Track track)
	{
		super(world);
		
		this.track = track;
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

		VectorF rotation = VectorF.obtain().setEuler(modelMatrix);
		
		Matrix.setIdentityM(normalMatrix, 0);
		Matrix.rotateM(normalMatrix, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(normalMatrix, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(normalMatrix, 0, rotation.z, 0, 0, 1);
		
		rotation.release();
		
		this.radius = getRadius();
	}
}
