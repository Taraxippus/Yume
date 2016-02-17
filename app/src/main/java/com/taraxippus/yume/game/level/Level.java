package com.taraxippus.yume.game.level;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;
import com.taraxippus.yume.game.gameobject.*;

public class Level
{
	final Game game;
	final boolean[] blocked = new boolean[getWidth() * getHeight() * getLength()];
	
	public Level(Game game)
	{
		this.game = game;
	}
	
	public int getWidth()
	{
		return 24;
	}
	
	public int getHeight()
	{
		return 24;
	}
	
    public int getLength()
	{
		return 24;
	}
	
	public boolean canStand(IMover mover, int x, int y, int z)
	{
		return y <= 0 || blocked[x * getLength() * getHeight() + (y - 1) * getLength() + z];
	}
	
    public boolean isBlocked(IMover mover, int x, int y, int z)
	{
		return !canStand(mover, x, y, z) || blocked[x * getLength() * getHeight() + y * getLength() + z];
	}
	
	public void setBlocked(int x, int y, int z, boolean block)
	{
		blocked[x * getLength() * getHeight() + y * getLength() + z] = block;
		
		for (MovingObject gameObject : game.main.world.movingObjects)
			gameObject.checkPath();
	}
	
    public float getCost(IMover mover, int sX, int sY, int sZ, int tX, int tY, int tZ)
	{
		return 1;
	}
	
    public void pathFinderVisited(int x, int y, int z)
	{
		
	}
}
