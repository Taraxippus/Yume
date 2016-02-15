package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.util.*;

public class FloatingBox extends Box 
{
	public static final VectorF COLOR_DEFAULT = new VectorF(0, 0xCC / 255F, 1.0F);
	public static final VectorF COLOR_ACTIVATED = new VectorF(0, 1, 0.5F);
	
	public static final float ANIMATION_DURATION = 1.5F;
	
	public static final float TIME = 0.25F;
	
	public FloatingBox(World world)
	{
		super(world);
		
		this.color.set(COLOR_DEFAULT);
		this.setTouchable(true);
	}

	float floatOffset;
	float animationTick;
	
	@Override
	public void update()
	{
		if (animationTick > 0)
		{
			animationTick -= Main.FIXED_DELTA;

			color.approach(COLOR_DEFAULT, COLOR_ACTIVATED, 1 - animationTick / ANIMATION_DURATION);
			world.main.timeFactor = 1 * animationTick / ANIMATION_DURATION + TIME * (1 - animationTick / ANIMATION_DURATION);
			
			if (animationTick <= 0)
			{
				animationTick = 0;
				color.set(COLOR_ACTIVATED);
				world.main.timeFactor = TIME;
			}
		}
		else if (animationTick < 0)
		{
			animationTick += Main.FIXED_DELTA;

			color.approach(COLOR_DEFAULT, COLOR_ACTIVATED, - animationTick / ANIMATION_DURATION);
			world.main.timeFactor = 1 * (1 + animationTick / ANIMATION_DURATION) + TIME * -animationTick / ANIMATION_DURATION;
			
			if (animationTick >= 0)
			{
				animationTick = 0;
				color.set(COLOR_DEFAULT);
				world.main.timeFactor = 1;
			}
		}
		
		super.update();
		
		this.position.y -= floatOffset;
		this.rotation.y += 90F * Main.FIXED_DELTA;
		this.floatOffset = (float) Math.cos(world.time * 0.25F * Math.PI * 2) * 0.5F;
		this.position.y += floatOffset;
		this.updateMatrix();
	}

	boolean activated;
	
	@Override
	public void onTouch()
	{
		activated = !activated;
		
		if (animationTick == 0)
		{
			animationTick = activated ? ANIMATION_DURATION : -ANIMATION_DURATION;
		}
		else
			animationTick = -animationTick;
	}
}
