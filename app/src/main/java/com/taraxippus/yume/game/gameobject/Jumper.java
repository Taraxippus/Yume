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
		
		for (int i = 0; i < 10 && path == null; ++i)
		{
			path = world.main.game.pathFinder.findPath(this, this.position.x, this.position.z, random.nextInt(world.main.game.level.getWidth()), random.nextInt(world.main.game.level.getLength()), true);
		}
		
		setPath(path);
	}
}
