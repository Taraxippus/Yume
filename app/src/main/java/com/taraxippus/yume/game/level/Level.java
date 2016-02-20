package com.taraxippus.yume.game.level;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.game.path.*;
import com.taraxippus.yume.util.*;

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
	
	public VectorF getGravity(IMover mover, int x, int y, int z)
	{
		return DIRECTION[
		((y <= 0 || blocked[x * getLength() * getHeight() + (y - 1) * getLength() + z]) ? 0 : (y >= getHeight() - 1 || blocked[x * getLength() * getHeight() + (y + 1) * getLength() + z]) ? 2 : 1) * 3
			+ ((x <= 0 || blocked[(x - 1) * getLength() * getHeight() + y * getLength() + z]) ? 0 : (x >= getWidth() - 1 || blocked[(x + 1) * getLength() * getHeight() + y * getLength() + z]) ? 2 : 1) * 9
			+ ((z <= 0 || blocked[x * getLength() * getHeight() + y * getLength() + z - 1]) ? 0 : (z >= getLength() - 1 || blocked[x * getLength() * getHeight() + y * getLength() + z + 1]) ? 2 : 1)];
	}
	
    public boolean isBlocked(IMover mover, int x, int y, int z)
	{
		VectorF gravity = getGravity(mover, x, y, z);
		
		return gravity.equals(VectorF.zero)
		|| blocked[x * getLength() * getHeight() + y * getLength() + z];
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
	
	public static final VectorF[] DIRECTION = new VectorF[27];
	
	static
	{
		int x, y, z;
		for (x = -1; x <= 1; ++x)
			for (y = -1; y <= 1; ++y)
				for (z = -1; z <= 1; ++z)
					DIRECTION[(x + 1) * 9 + (y + 1) * 3 + (z + 1)] = new VectorF(x, y, z);
	}
}
