package com.taraxippus.yume.game.gameobject;

import android.opengl.Matrix;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.util.VectorF;

public class OnTrackObject extends SceneObject
{
	public final VectorF trackPosition = new VectorF();
	public Track track;
	
	public OnTrackObject(World world, Track track)
	{
		super(world);
		this.track = track;
	}

	@Override
	public SceneObject translate(float x, float y, float z)
	{
		trackPosition.add(x, y, z);
		
		updateMatrix();
		return this;
	}
	
	@Override
	public void updateMatrix()
	{
		if (track == null)
			return;
			
		track = track.getPiece(trackPosition);
		
		Matrix.setIdentityM(normalMatrix, 0);
		track.rotate(trackPosition, normalMatrix);
		Matrix.rotateM(normalMatrix, 0, this.rotation.y, 0, 1, 0);
		Matrix.rotateM(normalMatrix, 0, this.rotation.x, 1, 0, 0);
		Matrix.rotateM(normalMatrix, 0, this.rotation.z, 0, 0, 1);
		
		this.position.set(track.getPosition(trackPosition, position));
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);
		Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, normalMatrix, 0);
		Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);
		
		Matrix.invertM(invModelMatrix, 0, modelMatrix, 0);
		
		this.radius = getRadius();
	}
}
