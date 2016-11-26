package com.taraxippus.yume.game.track;

import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.gameobject.HexagonTube;
import com.taraxippus.yume.game.gameobject.TrackObject;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.game.model.TrackModel;
import com.taraxippus.yume.game.model.TrackSideModel;
import android.opengl.Matrix;

public class TrackRollRight extends Track
{
	public static final Model trackModel; 
	public static final Model trackSideModel;

	static
	{
		float[] matrix = new float[16];
		
		Matrix.setIdentityM(matrix, 0);
		Matrix.rotateM(matrix, 0, 90, 0, 0, 1);
		
		trackModel = new TrackModel(matrix, 20, 20);
		trackSideModel = new TrackSideModel(matrix, 20, 20);
	}
	
	public TrackRollRight(World world, float z, boolean tube)
	{
		super(world, z, tube);
	}

	@Override
	public void create()
	{
		super.create();
		
		Matrix.rotateM(nextMatrix, 0, 90, 0, 0, 1);
		nextRotation.multiplyRightByAngleAxis((float) Math.PI / 2F, 0, 0, 1);
	}

	@Override
	public void addGameObjects()
	{
		world.add(new TrackObject(world, this).setModel(trackModel).setColor(TRACK_COLOR).setAlpha(TRACK_ALPHA).setDepthOffset(TRACK_OFFSET));
		world.add(new TrackObject(world, this).setModel(trackSideModel).setColor(TRACK_SIDE_COLOR).setAlpha(TRACK_SIDE_ALPHA).setDepthOffset(TRACK_SIDE_OFFSET));
		
		if (tube)
			world.add(new HexagonTube(world, this).setModel(hexagonTubeModel));
	}
}
