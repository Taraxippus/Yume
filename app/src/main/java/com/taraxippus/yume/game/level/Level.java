package com.taraxippus.yume.game.level;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.game.path.*;
import com.taraxippus.yume.util.*;
import java.nio.*;

public class Level
{
	final Main main;
	
	private int width = 24, height = 24, length = 24;
	private byte[] ids = new byte[getWidth() * getHeight() * getLength()];
	
	
	public Level(Main main)
	{
		this.main = main;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
    public int getLength()
	{
		return length;
	}
	
	public VectorF getGravity(IMover mover, int x, int y, int z)
	{
		return DIRECTION[
		((y <= 0 || y >= 0 && y < getHeight() && isBlocked(ids[x * getLength() * getHeight() + (y - 1) * getLength() + z])) ? 0 : (y >= getHeight() - 1 || y >= 0 && y < getHeight() && isBlocked(ids[x * getLength() * getHeight() + (y + 1) * getLength() + z])) ? 2 : 1) * 3
			+ ((x <= 0 || x >= 0 && x < getWidth() && isBlocked(ids[(x - 1) * getLength() * getHeight() + y * getLength() + z])) ? 0 : (x >= getWidth() - 1 || x >= 0 && x < getWidth() && isBlocked(ids[(x + 1) * getLength() * getHeight() + y * getLength() + z])) ? 2 : 1) * 9
			+ ((z <= 0 || z >= 0 && z < getLength() && isBlocked(ids[x * getLength() * getHeight() + y * getLength() + z - 1])) ? 0 : (z >= getLength() - 1 || z >= 0 && z < getLength() && isBlocked(ids[x * getLength() * getHeight() + y * getLength() + z + 1])) ? 2 : 1)];
	}
	
	public boolean isBlocked(byte id)
	{
		return id != 0;
	}
	
    public boolean isBlocked(IMover mover, int x, int y, int z)
	{
		if (x >= getWidth() || y >= getHeight() || z >= getLength() || x < 0 || y < 0 || z < 0)
			return true;
		
		VectorF gravity = getGravity(mover, x, y, z);
		
		return gravity.equals(VectorF.zero)
		|| isBlocked(ids[x * getLength() * getHeight() + y * getLength() + z]);
	}
	
	public void setBlocked(int x, int y, int z, byte id)
	{
		ids[x * getLength() * getHeight() + y * getLength() + z] = id;
		
		for (MovingObject gameObject : main.world.movingObjects)
			gameObject.checkPath();
	}
	
    public float getCost(IMover mover, int sX, int sY, int sZ, int tX, int tY, int tZ)
	{
		return 1;
	}
	
    public void pathFinderVisited(int x, int y, int z)
	{
		
	}
	
	public ByteBuffer save(ByteBuffer buffer)
	{
		buffer.putInt(0);
		
		buffer.putInt(getWidth());
		buffer.putInt(getHeight());
		buffer.putInt(getLength());
		
		for (byte id : ids)
			buffer.put(id);
		
		return buffer;
	}
	
	public ByteBuffer load(ByteBuffer buffer)
	{
		int version = buffer.getInt();
		
		if (version >= 0)
		{
			width = buffer.getInt();
			height = buffer.getInt();
			length = buffer.getInt();
			
			ids = new byte[getWidth() * getHeight() * getLength()];
			
			for (int i = 0; i < ids.length; ++i)
				ids[i] = buffer.get();
				
		}
		
		for (MovingObject gameObject : main.world.movingObjects)
			gameObject.checkPath();
		
		return buffer;
	}
	
	public int getBytes()
	{
		return (Integer.SIZE / Byte.SIZE) * 4 + ids.length;
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
