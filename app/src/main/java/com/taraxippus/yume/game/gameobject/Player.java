package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.util.VectorF;

public class Player extends OnTrackObject
{
	public final VectorF velocity = new VectorF();
	
	public static final float DURATION = 0.5F;
	
	public Player(World world, Track track)
	{
		super(world, track);
		
		scale(1.5F, 0.4F, 2.5F);
		setTouchable(true);
		translate(0, 0, 0.5F);
		rotation.y = 180;
	}

	private float rotateTick;
	
	@Override
	public void update()
	{
		this.trackPosition.add(velocity.x * Main.FIXED_DELTA, velocity.y * Main.FIXED_DELTA, velocity.z * Main.FIXED_DELTA);
		trackPosition.y = 0.02F + (float) Math.cos(world.time * 1.55790F) * 0.005F;
		
		this.velocity.multiplyBy(0.99F);
		
		if (trackPosition.x < -Track.WIDTH / 2F)
		{
			trackPosition.x = -Track.WIDTH / 2F;
			
			velocity.multiplyBy(0.95F);
		}
		else if (trackPosition.x > Track.WIDTH / 2F)
		{
			trackPosition.x = Track.WIDTH / 2F;

			velocity.multiplyBy(0.95F);
		}
		
		rotation.z = 0;
		
		if (rotateTick > 0)
		{
			rotateTick -= Main.FIXED_DELTA;
			
			if (rotateTick <= 0)
				rotateTick = 0;
			
			rotation.z = rotateTick / DURATION * rotateTick / DURATION * 360;
		}
		
		rotation.z += (float) (Math.sin(world.time * 2F) * 5 + Math.cos(world.time * 0.356F) * 0.25F);
		
		updateMatrix();
	}
	
	@Override
	public void onTouch(VectorF intersection, VectorF normal)
	{
		rotateTick += DURATION;
	}

	@Override
	public String toString()
	{
		return trackPosition.toString();
	}
}
