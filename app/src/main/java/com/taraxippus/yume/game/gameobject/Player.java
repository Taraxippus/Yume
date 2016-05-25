package com.taraxippus.yume.game.gameobject;

import android.opengl.GLES20;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.render.Shape;
import com.taraxippus.yume.util.VectorF;

public class Player extends SceneObject
{
	public static final float DURATION = 1F;
	
	public Player(World world)
	{
		super(world);
		
		scale(1.5F, 0.4F, 2.5F);
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
	
	public static final float[] vertices = new float[]
	{
		0, 0, -0.5F,
		0, 0, 0,
		-0.15F, 0.5F, 0.2F, 
		0, 0, 0,
		0.15F, 0.5F, 0.2F,
		0, 0, 0,
		-0.5F, -0.2F, 0.4F,
		0, 0, 0,
		0.5F, -0.2F, 0.4F,
		0, 0, 0,
		-0.3F, 0, 0.5F,
		0, 0, 0,
		0.3F, 0, 0.5F,
		0, 0, 0,
		0, -0.2F, 0.4F,
		0, 0, 0
	};
	
	public static final short[] indices = new short[]
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
	
	@Override
	public Shape createShape()
	{
		Shape shape = new Shape();

		shape.initGenerateFlatNormals(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
	
	@Override
	public Shape createOutlineShape()
	{
		Shape shape = new Shape();

		shape.initGenerateNormals(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
}
