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
	
	public static final VectorF xN = new VectorF(-1, 0, 0);
	public static final VectorF xP = new VectorF(1, 0, 0);
	public static final VectorF yN = new VectorF(0, -1, 0);
	public static final VectorF yP = new VectorF(0, 1, 0);
	public static final VectorF zN = new VectorF(0, 0, -1);
	public static final VectorF zP = new VectorF(0, 0, 1);
	
	public VectorF getOpposite(VectorF direction)
	{
		if (direction == xN)
			return xP;
		else if (direction == xP)
			return xN;
			
		if (direction == yN)
			return yP;
		else if (direction == yP)
			return yN;
			
		if (direction == zN)
			return zP;
		else if (direction == zP)
			return zN;
			
		return VectorF.zero;
	}
	
	public VectorF getGravity(IMover mover, int x, int y, int z)
	{
		if (y <= 0 || blocked[x * getLength() * getHeight() + (y - 1) * getLength() + z])
			return yN;
		else if (y >= getHeight() - 1 || blocked[x * getLength() * getHeight() + (y + 1) * getLength() + z])
			return yP;
			
		if (x <= 0 || blocked[(x - 1) * getLength() * getHeight() + y * getLength() + z])
			return xN;
		else if (x >= getWidth() - 1 || blocked[(x + 1) * getLength() * getHeight() + y * getLength() + z])
			return xP;
			
		if (z <= 0 || blocked[x * getLength() * getHeight() + y * getLength() + z - 1])
			return zN;
		else if (z >= getLength() - 1 || blocked[x * getLength() * getHeight() + y * getLength() + z + 1])
			return zP;
			
		return VectorF.zero;
	}
	
    public boolean isBlocked(IMover mover, int x, int y, int z)
	{
		VectorF gravity = getGravity(mover, x, y, z);
		
		return gravity == VectorF.zero
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
}
