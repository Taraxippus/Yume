package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;
import android.view.*;

public class Player extends Box implements IMover
{
	public static final float JUMP_DURATION = 0.25F;
	public static final float JUMP_PAUSE = 0.05F;
	
	public boolean selected;
	public Path path;
	public Path.Step lastStep, nextStep;
	
	public Player(World world)
	{
		super(world);
		
		this.setColor(0xFF8800);
		this.setTouchable(true);
		this.translate(0, 0.5F * 0.75F, 0);
		this.scale(0.75F, 0.75F, 0.75F);
		
		this.specularity = 50F;
	}

	float jumpTick;
	
	@Override
	public void update()
	{
		super.update();
		
		if (jumpTick > 0)
		{
			jumpTick -= Main.FIXED_DELTA;
			
			float delta = (JUMP_DURATION - jumpTick) / JUMP_DURATION;
			
			this.position.set(
				nextStep.x * delta + lastStep.x * (1 - delta), 
				0.5F * scale.y + (0.5F * World.GRAVITY * delta * delta + -World.GRAVITY * 0.5F * delta),
				nextStep.z * delta + lastStep.z * (1 - delta));
			
			this.updateMatrix();
			
			if (jumpTick <= 0)
			{
				jumpTick = -JUMP_PAUSE;
				
				this.position.set(nextStep.x, 0.5F * scale.y, nextStep.z);
				this.updateMatrix();
			}
		}
		else if (jumpTick < 0)
		{
			jumpTick += Main.FIXED_DELTA;
			
			if (jumpTick >= 0)
				if (path != null && path.hasNext())
				{
					lastStep = nextStep;
					nextStep = path.nextStep();
					if (lastStep == null)
					{
						lastStep = nextStep;
						nextStep = path.nextStep();
					}
					jumpTick = JUMP_DURATION;
				}
				else
					jumpTick = 0;
		}
	}
	
	public void setPath(Path path)
	{
		this.path = path;
		
		if (jumpTick == 0)
		{
			jumpTick = -0.00001F;
		}
	}
	
	@Override
	public void onTouch()
	{
		this.selected = !this.selected;
		this.world.main.game.grid.toggleVisibility();
	}
}
