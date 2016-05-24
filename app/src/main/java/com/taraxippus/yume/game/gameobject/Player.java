package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.util.VectorF;

public class Player extends Box
{
	public static final float DURATION = 1F;
	
	public Player(World world)
	{
		super(world);
		
		setTouchable(true);
	}

	private float rotateTick;
	
	@Override
	public void update()
	{
		if (rotateTick > 0)
		{
			rotateTick -= Main.FIXED_DELTA;
			
			if (rotateTick <= 0)
				rotateTick = 0;
			
			rotation.z = rotateTick / DURATION * rotateTick / DURATION * 360;
		}
		
		updateMatrix();
	}
	
	@Override
	public void onTouch(VectorF intersection, VectorF normal)
	{
		rotateTick += DURATION;
	}
}
