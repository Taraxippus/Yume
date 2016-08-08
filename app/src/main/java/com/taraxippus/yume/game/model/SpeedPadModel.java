package com.taraxippus.yume.game.model;

import com.taraxippus.yume.render.Pass;

public class SpeedPadModel extends Model
{

	public SpeedPadModel()
	{
		super(Pass.SCENE_OUTLINE);
	}
	
	@Override
	public float[] getVertices()
	{
		return new float[]
		{
			-0.5F, 0.5F, -0.5F,
			0.5F, 0.5F, -0.5F,
			0.0F, 0.5F, 0.5F,
			-0.25F, 0.5F, -0.5F,
			0.25F, 0.5F, -0.5F,
			0.0F, 0.5F, -0.25F,
			
			-0.5F, -0.5F, -0.5F,
			0.5F, -0.5F, -0.5F,
			0.0F, -0.5F, 0.5F,
			-0.25F, -0.5F, -0.5F,
			0.25F, -0.5F, -0.5F,
			0.0F, -0.5F, -0.25F
		};
	}

	@Override
	public float[] getOutlineVertices()
	{
		return new float[]
		{
			-0.5F, 0.5F, -0.5F,
			0, 0, 0,
			0.5F, 0.5F, -0.5F,
			0, 0, 0,
			0.0F, 0.5F, 0.5F,
			0, 0, 0,
			-0.25F, 0.5F, -0.5F,
			0, 0, 0,
			0.25F, 0.5F, -0.5F,
			0, 0, 0,
			0.0F, 0.5F, -0.25F,
			0, 0, 0,

			-0.5F, -0.5F, -0.5F,
			0, 0, 0,
			0.5F, -0.5F, -0.5F,
			0, 0, 0,
			0.0F, -0.5F, 0.5F,
			0, 0, 0,
			-0.25F, -0.5F, -0.5F,
			0, 0, 0,
			0.25F, -0.5F, -0.5F,
			0, 0, 0,
			0.0F, -0.5F, -0.25F,
			0, 0, 0,
		};
	}
	
	@Override
	public short[] getIndices()
	{
		return new short[]
		{
			0, 5, 3,
			0, 2, 5,
			5, 2, 1,
			4, 5, 1,
			6, 9, 11,
			6, 11, 8, 
			11, 7, 8,
			10, 7, 11,
			
			6, 0, 9,
			9, 0, 3,
			9, 3, 11,
			11, 3, 5,
			11, 5, 10,
			10, 5, 4,
			10, 4, 7,
			7, 4, 1,
			7, 1, 8,
			8, 1, 2,
			8, 0, 6,
			8, 2, 0,
		};
	}
}
