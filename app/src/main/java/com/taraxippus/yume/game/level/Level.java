package com.taraxippus.yume.game.level;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;

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
	
	public boolean setBlocked(int x, int z)
	{
		return blocked[x * getLength() + z] = true;
	}
	
    public float getCost(IMover mover, int sX, int sZ, int tX, int tY)
	{
		return 1;
	}
	
    public void pathFinderVisited(int x, int z)
	{
		
	}
}
