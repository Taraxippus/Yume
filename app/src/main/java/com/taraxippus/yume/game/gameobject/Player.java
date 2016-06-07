package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.util.VectorF;

public class Player extends OnTrackObject
{
	public static final float DURATION = 1F;
	
	public Player(World world, Track track)
	{
		super(world, track);
		
		scale(1.5F, 0.4F, 2.5F);
		setTouchable(true);
	}

	private float rotateTick;
	
	@Override
	public void update()
	{
		if (rotateTick > 0)
		{
			rotateTick -= Main.FIXED_DELTA;
			
			if (rotateTick <= 0)
				rotateTick = 0;
			
			rotation.z = rotateTick / DURATION * rotateTick / DURATION * 360;
		}
		
		updateMatrix();
	}
	
	@Override
	public void onTouch(VectorF intersection, VectorF normal)
	{
		rotateTick += DURATION;
	}
}
