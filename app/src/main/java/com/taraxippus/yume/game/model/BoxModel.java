package com.taraxippus.yume.game.model;

import com.taraxippus.yume.render.Pass;

public class BoxModel extends Model
{
	public BoxModel()
	{
		super(Pass.SCENE_OUTLINE);
		
		this.generateOutlineNormals = false;
	}
	
	@Override
	public float[] getVertices()
	{
		return new float[]
		{
			-0.5F, -0.5F, -0.5F,
			0.5F, -0.5F, -0.5F,
			-0.5F, -0.5F, 0.5F,
			0.5F, -0.5F, 0.5F,
			-0.5F, 0.5F, -0.5F,
			0.5F, 0.5F, -0.5F,
			-0.5F, 0.5F, 0.5F,
			0.5F, 0.5F, 0.5F
		};
	}


	@Override
	public float[] getOutlineVertices()
	{
		return new float[]
		{
			-0.5F, -0.5F, -0.5F,
			-1, -1, -1,

			0.5F, -0.5F, -0.5F,
			1, -1, -1,

			-0.5F, -0.5F, 0.5F,
			-1, -1, 1,

			0.5F, -0.5F, 0.5F,
			1, -1, 1,


			-0.5F, 0.5F, -0.5F,
			-1, 1, -1,

			0.5F, 0.5F, -0.5F,
			1, 1, -1,

			-0.5F, 0.5F, 0.5F,
			-1, 1, 1,

			0.5F, 0.5F, 0.5F,
			1, 1, 1,

		};
	}
	
	@Override
	public short[] getIndices()
	{
		return new short[]
		{
			0, 1, 2,
			1, 3, 2,

			4, 6, 5,
			5, 6, 7,

			0, 4, 1,
			1, 4, 5,

			1, 5, 3,
			3, 5, 7,

			3, 7, 2,
			2, 7, 6,

			2, 6, 0, 
			0, 6, 4
		};
	}
}
