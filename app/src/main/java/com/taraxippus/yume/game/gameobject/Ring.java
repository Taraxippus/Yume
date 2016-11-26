package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;

public class Ring extends OnTrackObject
{
	final float speed;
	
	public Ring(World world, Track track)
	{
		super(world, track);

		this.scale(Track.SIZE * 0.25F, Track.SIZE * 0.25F, 0.2F);
		this.setColor(Track.TRACK_SIDE_COLOR);
		this.setAlpha(Track.TRACK_SIDE_ALPHA);
		this.setDepthOffset(-500);
		
		speed = 20 + world.random.nextFloat() * 20;
	}

	@Override
	public void update()
	{
		super.update();
		
		rotation.z += Main.FIXED_DELTA * speed;
		updateMatrix();
	}
}
