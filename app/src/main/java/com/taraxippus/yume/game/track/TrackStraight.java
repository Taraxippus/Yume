package com.taraxippus.yume.game.track;

import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.gameobject.HexagonTube;
import com.taraxippus.yume.game.gameobject.TrackObject;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.game.model.TrackModel;
import com.taraxippus.yume.game.model.TrackSideModel;
import com.taraxippus.yume.game.gameobject.Collectable;

public class TrackStraight extends Track
{
	public static final Model trackModel = new TrackModel(null, 1, 1);
	public static final Model trackSideModel = new TrackSideModel(null, 1, 1);
	
	public TrackStraight(World world, float z, boolean tube)
	{
		super(world, z, tube);
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
