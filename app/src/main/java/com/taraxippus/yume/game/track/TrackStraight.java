package com.taraxippus.yume.game.track;

import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.gameobject.HexagonTube;
import com.taraxippus.yume.game.gameobject.TrackObject;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.game.model.TrackModel;
import com.taraxippus.yume.game.model.TrackSideModel;

public class TrackStraight extends Track
{
	public static final Model trackModel = new TrackModel(null, 1, 1);
	public static final Model trackSideModel = new TrackSideModel(null, 1, 1);
	
	public TrackStraight(World world, float z)
	{
		super(world, z);
	}

	@Override
	public void addGameObjects()
	{
		world.add(new TrackObject(world, this).setModel(trackModel).scale(WIDTH, WIDTH, LENGTH).setColor(TRACK_COLOR).setAlpha(TRACK_ALPHA).setDepthOffset(1000));
		world.add(new TrackObject(world, this).setModel(trackSideModel).scale(WIDTH, WIDTH, LENGTH).setColor(TRACK_SIDE_COLOR).setAlpha(TRACK_SIDE_ALPHA).setDepthOffset(1000));
		world.add(new HexagonTube(world, this).setModel(hexagonTubeModel).scale(LENGTH, LENGTH, LENGTH));
	}
}
