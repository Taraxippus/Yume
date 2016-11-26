package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.util.VectorF;

public class Collectable extends OnTrackObject
{
	public Collectable(World world, Track track)
	{
		super(world, track);
		
		setColor(0xFF8803);
		setAlpha(0.75F);
		scale(0.05F, 0.05F, 0.05F);
	}
	
	boolean collected = false;
	float distance;
	
	@Override
	public void update()
	{
		super.update();
		
		distance = Track.getDistance(trackPosition, world.main.game.player.trackPosition);
	
		collected = distance < 0.075;
			
		if (collected)
		{
			VectorF tmp = VectorF.obtain().set(world.main.game.player.trackPosition).set(2, (world.main.game.player.trackPosition.z + Track.trackLength) % Track.trackLength).subtract(trackPosition).normalize().multiplyBy(0.02F);
			if (tmp.length() > distance)
			{
				world.removeLater(this);
				tmp.release();
				return;
			}
			trackPosition.add(tmp);
			tmp.release();
			distance = Math.min(0.5F, distance / 0.075F * 0.5F);
			scale.set(distance * 0.1F, distance * 0.1F, distance * 0.1F);
		}
		else
			trackPosition.y = 0.02F + (float) Math.cos(world.time) * 0.005F;
		
		rotation.y += Main.FIXED_DELTA * 90;
		rotation.x += Main.FIXED_DELTA * 45;
		updateMatrix();
	}
}
