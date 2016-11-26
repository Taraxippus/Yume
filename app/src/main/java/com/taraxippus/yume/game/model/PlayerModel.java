package com.taraxippus.yume.game.model;

import com.taraxippus.yume.render.Pass;

public class PlayerModel extends Model
{
	public PlayerModel()
	{
		super(Pass.SCENE_OUTLINE);
	}

	@Override
	public float[] getVertices()
	{
		return new float[]
		{
			0, 0, -0.5F,
			-0.15F, 0.5F, 0.2F, 
			0.15F, 0.5F, 0.2F,
			-0.5F, -0.2F, 0.4F,
			0.5F, -0.2F, 0.4F,
			-0.3F, 0, 0.5F,
			0.3F, 0, 0.5F,
			0, -0.2F, 0.4F,
		};
	}
	
	@Override
	public short[] getIndices()
	{
		return new short[]
		{
			0, 4, 7,
			0, 7, 3,
			0, 3, 1,
			0, 1, 2,
			0, 2, 4,
			3, 7, 5,
			7, 4, 6,
			3, 5, 1,
			1, 5, 7,
			1, 7, 2,
			7, 6, 2,
			2, 6, 4
		};
		
	}
}
