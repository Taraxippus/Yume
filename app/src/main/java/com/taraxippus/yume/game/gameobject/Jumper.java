package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.game.World;
import java.util.Random;

public class Jumper extends MovingObject
{
	public float direction;

	public Jumper(World world)
	{
		super(world);
		setColor(0xFF0033);
	}

	@Override
	public void init()
	{
		super.init();

		move(direction);
	}

	@Override
	public void onJumpFinished()
	{
		super.onJumpFinished();

		if (world.random.nextInt(10) == 0)
			randomDirection();

		move(direction);
	}

	public void randomDirection()
	{
		this.direction = this.direction + world.random.nextFloat() * 180 - 90;
	}
}
