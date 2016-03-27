package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;

public class Follower extends MovingObject
{
	public Follower(World world)
	{
		super(world);
		setColor(0xFF0033);
	}
	
	public Follower follow(MovingObject target)
	{
		target.follower = this;
		return this;
	}
	
	public void onNextStep(MovingObject target)
	{
		if (target != null && target.nextStep != null)
		{
			lastStep = nextStep;
			if (lastStep == null)
				lastStep = new Path.Step((int)this.position.x, (int)this.position.y, (int)this.position.z, world.main.level.getGravity(this, (int)this.position.x, (int)this.position.y, (int)this.position.z));
			
			nextStep = target.nextStep;
			onNextStep();
		}
		else
		{
			jumpTick = 0;
			lastStep = null;
			nextStep = null;
		}
	}

	@Override
	public float getJumpDuration()
	{
		return 0.4F;
	}

	@Override
	public float getJumpPause()
	{
		return 0.25F;
	}
}
