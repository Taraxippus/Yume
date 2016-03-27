package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;
import java.util.*;

public class Jumper extends MovingObject
{
	public Jumper(World world)
	{
		super(world);
		setColor(0xFF0033);
	}

	@Override
	public void init()
	{
		super.init();
		
		setPath();
	}

	@Override
	public void onPathFinished()
	{
		super.onPathFinished();
		
		setPath();
	}

	@Override
	public void update()
	{
		if (path == null)
			setPath();
		
		super.update();
	}
	
	final Random random = new Random();
	
	public void setPath()
	{
		Path path = null;
		
		int side = random.nextInt(6);
		for (int i = 0; i < 10 && path == null; ++i)
		{
			if (side == 0)
				path = pathFinder.findPath(this, this.position.x, this.position.y, this.position.z, random.nextInt(world.main.level.getWidth()), 0, random.nextInt(world.main.level.getLength()), true);
			if (side == 1)
				path = pathFinder.findPath(this, this.position.x, this.position.y, this.position.z, random.nextInt(world.main.level.getWidth()), pathFinder.level.getHeight() - 1, random.nextInt(world.main.level.getLength()), true);
			if (side == 2)
				path = pathFinder.findPath(this, this.position.x, this.position.y, this.position.z, 0, random.nextInt(world.main.level.getHeight()), random.nextInt(world.main.level.getLength()), true);
			if (side == 3)
				path = pathFinder.findPath(this, this.position.x, this.position.y, this.position.z, pathFinder.level.getWidth() - 1, random.nextInt(world.main.level.getHeight()), random.nextInt(world.main.level.getLength()), true);
			if (side == 4)
				path = pathFinder.findPath(this, this.position.x, this.position.y, this.position.z, random.nextInt(world.main.level.getWidth()), random.nextInt(world.main.level.getHeight()), 0, true);
			if (side == 5)
				path = pathFinder.findPath(this, this.position.x, this.position.y, this.position.z, random.nextInt(world.main.level.getWidth()), random.nextInt(world.main.level.getHeight()), pathFinder.level.getLength(), true);
			
		}
		
		setPath(path);
	}
}
