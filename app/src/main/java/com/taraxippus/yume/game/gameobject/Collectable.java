package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;

public class Collectable extends SceneObject
{
	public Collectable(World world)
	{
		super(world);
		
		setColor(0xFF8800);
		setAlpha(0.75F);
	}

	@Override
	public void update()
	{
		super.update();
		
		position.y = 1 + (float) Math.cos(world.time) * 0.25F;
		rotation.y += Main.FIXED_DELTA * 90;
		rotation.x += Main.FIXED_DELTA * 45;
		updateMatrix();
	}
}
