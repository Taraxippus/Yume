package com.taraxippus.yume.game;

import com.taraxippus.yume.*;

public class FloatingBox extends Box 
{
	public FloatingBox(World world)
	{
		super(world);
	}

	float floatOffset;
	
	@Override
	public void update()
	{
		super.update();
		
		this. y -= floatOffset;
		this.rotY += 90F * Main.FIXED_DELTA;
		this.floatOffset = (float) Math.cos(world.time * 0.25F * Math.PI * 2) * 0.5F;
		this.y += floatOffset;
		this.updateMatrix();
	}
}
