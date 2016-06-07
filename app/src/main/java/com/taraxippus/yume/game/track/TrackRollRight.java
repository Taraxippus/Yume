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
		
		trackModel = new TrackModel(matrix, 10, 10);
		trackSideModel = new TrackSideModel(matrix, 10, 10);
	}
	
	public TrackRollRight(World world, float z)
	{
		super(world, z);
	}

	@Override
	public void create()
	{
		super.create();
		
		Matrix.rotateM(nextMatrix, 0, 90, 0, 0, 1);
	}

	@Override
	public void addGameObjects()
	{
		world.add(new TrackObject(world, this).setModel(trackModel).scale(WIDTH, WIDTH, LENGTH).setColor(TRACK_COLOR).setAlpha(TRACK_ALPHA).setDepthOffset(1000));
		world.add(new TrackObject(world, this).setModel(trackSideModel).scale(WIDTH, WIDTH, LENGTH).setColor(TRACK_SIDE_COLOR).setAlpha(TRACK_SIDE_ALPHA).setDepthOffset(1000));
		world.add(new HexagonTube(world, this).setModel(hexagonTubeModel).scale(LENGTH, LENGTH, LENGTH));
	}
}
