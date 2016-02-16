package com.taraxippus.yume.game.level;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;
import com.taraxippus.yume.game.gameobject.*;

public class Level
{
	final Game game;
	final boolean[] blocked = new boolean[getWidth() * getLength()];
	
	public Level(Game game)
	{
		this.game = game;
	}
	
	public int getWidth()
	{
		return 24;
	}
	
    public int getLength()
	{
		return 24;
	}
	
    public boolean isBlocked(IMover mover, int x, int z)
	{
		return blocked[x * getLength() + z];
	}
	
	public void setBlocked(int x, int z, boolean block)
	{
		blocked[x * getLength() + z] = block;
		
		for (MovingObject gameObject : game.main.world.movingObjects)
			gameObject.checkPath();
	}
	
    public float getCost(IMover mover, int sX, int sZ, int tX, int tY)
	{
		return 1;
	}
	
    public void pathFinderVisited(int x, int z)
	{
		
	}
}
