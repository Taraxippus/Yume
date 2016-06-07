package com.taraxippus.yume.game.gameobject;

import android.opengl.Matrix;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.util.VectorF;

public class OnTrackObject extends TrackObject
{
	public final VectorF trackPosition = new VectorF();
	
	public OnTrackObject(World world, Track track)
	{
		super(world, track);
	}

	@Override
	public SceneObject translate(float x, float y, float z)
	{
		trackPosition.add(x, y, z);
		
		if (track != null)
			track = track.getPiece(trackPosition);
		
		updateMatrix();
		return this;
	}
	
	@Override
	public void updateMatrix()
	{
		if (track == null)
			return;
			
		VectorF rotation = VectorF.obtain();
		rotation.set(track.getRotation(trackPosition, rotation));
		this.position.set(track.getPosition(trackPosition, position));
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
		Matrix.rotateM(modelMatrix, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, rotation.z, 0, 0, 1);
		Matrix.rotateM(modelMatrix, 0, this.rotation.y, 0, 1, 0);
		Matrix.rotateM(modelMatrix, 0, this.rotation.x, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, this.rotation.z, 0, 0, 1);
		Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);

		Matrix.invertM(invModelMatrix, 0, modelMatrix, 0);

		rotation.setEuler(modelMatrix);

		Matrix.setIdentityM(normalMatrix, 0);
		Matrix.rotateM(normalMatrix, 0, rotation.x, 1, 0, 0);
		Matrix.rotateM(normalMatrix, 0, rotation.y, 0, 1, 0);
		Matrix.rotateM(normalMatrix, 0, rotation.z, 0, 0, 1);

		rotation.release();

		this.radius = getRadius();
	}
}
