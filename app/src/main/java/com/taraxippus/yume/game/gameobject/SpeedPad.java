package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.util.VectorF;

public class SpeedPad extends OnTrackObject
{
	boolean enabled = true;
	float enabledTick;
	
	public SpeedPad(World world, Track track)
	{
		super(world, track);
		
		updateAlways = true;
		setColor(0xFF8803);
		scale(0.1F, 0.0125F, 0.1F);
	}

	@Override
	public void update()
	{
		super.update();
		
		if (enabled)
		{
			if (Track.getDistance(trackPosition, world.main.game.player.trackPosition) < 0.03F)
			{
				world.main.game.player.velocity.add(0, 0, 0.5F);
				
				enabled = false;
				enabledTick = 5F;
				setColor(0x994400);
			}
		}
		else
		{
			enabledTick -= Main.FIXED_DELTA;
			
			if (enabledTick <= 0)
			{
				setColor(0xFF8803);
				enabled = true;
			}
		}
	}
}
