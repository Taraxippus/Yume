package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;
import com.taraxippus.yume.util.*;

public class Player extends MovingObject implements IMover
{
	public static final float JUMP_DURATION = 0.25F;
	public static final float JUMP_PAUSE = 0.05F;
	
	public boolean selected;
	
	public Player(World world)
	{
		super(world);
		
		this.setColor(0xFF8800);
		this.setTouchable(true);
	}

	float jumpTick;
	
	@Override
	public void onTouch(VectorF intersection, VectorF normal)
	{
		this.selected = !this.selected;
		this.world.main.game.grid.toggleVisibility();
	}
}
