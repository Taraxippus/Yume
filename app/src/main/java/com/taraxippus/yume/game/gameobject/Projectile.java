package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.util.VectorF;

public class Projectile extends OnTrackObject
{
	public static float MAX_LIFE_TIME = 0.75F;
	
	float lifeTime;
	final VectorF velocity = new VectorF();
	
	public Projectile(World world, Track track)
	{
		super(world, track);

		setColor(0xFF8800);
		setAlpha(1.0F);
		scale(0.025F, 0.025F, 0.025F);

		this.lifeTime = MAX_LIFE_TIME;
	}
	
	public Projectile setVelocity(VectorF velocity)
	{
		this.velocity.set(velocity);
		
		return this;
	}

	@Override
	public void update()
	{
		super.update();

		if (trackPosition.x < -Track.WIDTH / 2F)
			velocity.x = -velocity.x;
		
		else if (trackPosition.x > Track.WIDTH / 2F)
			velocity.x = -velocity.x;
		
		
		lifeTime -= Main.FIXED_DELTA;

		if (lifeTime > MAX_LIFE_TIME - 0.1F)
			scale.set(0.025F, 0.025F, 0.025F).multiplyBy(10 * (MAX_LIFE_TIME - lifeTime));
			
		else if (lifeTime < 0.1F)
		{
			scale.set(0.025F, 0.025F, 0.025F).multiplyBy(10 * lifeTime);
			
			if (lifeTime <= 0)
				world.removeLater(this);
		}
		
		translate(velocity);
		updateMatrix();
	}
}
