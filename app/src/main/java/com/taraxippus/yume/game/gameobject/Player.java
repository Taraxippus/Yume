package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.game.World;
import com.taraxippus.yume.util.VectorF;

public class Player extends MovingObject
{
	public boolean selected;
	
	public Player(World world)
	{
		super(world);
		
		this.setColor(0xFF8800);
		this.setTouchable(true);
	}

	@Override
	public void onTouch(VectorF intersection, VectorF normal)
	{
		this.selected = !this.selected;
	}
}
