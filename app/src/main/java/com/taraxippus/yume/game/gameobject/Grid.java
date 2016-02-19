package com.taraxippus.yume.game.gameobject;

import android.opengl.*;
import com.taraxippus.yume.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;

public class Grid extends SceneObject
{
	public static final float MAX_ALPHA = 0.25F;
	public static final float ANIMATION_DURATION = 0.5F;
	
	final VectorF size = new VectorF(1, 1, 1);

	public Grid(World world, VectorF size)
	{
		super(world);

		this.size.set(size);
		this.scale(this.size.x, this.size.y, this.size.z);
		
		this.specularity = 50F;
		this.alpha = 0;
		this.setEnabled(false);
	}

	float visibilityTick;
	
	@Override
	public void update()
	{
		if (visibilityTick > 0)
		{
			visibilityTick -= Main.FIXED_DELTA;
			
			alpha = (1 - visibilityTick / ANIMATION_DURATION) * MAX_ALPHA;
			
			if (visibilityTick <= 0)
			{
				visibilityTick = 0;
				alpha = MAX_ALPHA;
			}
		}
		else if (visibilityTick < 0)
		{
			visibilityTick += Main.FIXED_DELTA;
			
			alpha = (-visibilityTick / ANIMATION_DURATION) * MAX_ALPHA;
			
			if (visibilityTick >= 0)
			{
				visibilityTick = 0;
				enabled = false;
			}
		}
		
		super.update();
	}

	public void toggleVisibility()
	{
		if (this.visibilityTick == 0)
		{
			if (this.enabled)
			{
				this.visibilityTick = -ANIMATION_DURATION;
			}
			else
			{
				this.enabled = true;
				this.visibilityTick = ANIMATION_DURATION;
			}
		}
		else
		{
			this.visibilityTick = -this.visibilityTick;
		}
	}

	public float getRadius()
	{
		return (float) Math.sqrt(scale.x * scale.x * 0.5 * 0.5 + scale.y * scale.y * 0.5 * 0.5 + scale.z * scale.z * 0.5 * 0.5);
	}

	@Override
	public Shape createShape()
	{
		final float[] vertices = new float[(3 + 3) * (2 * (int)(size.x * 2 + size.z * 2 + 4) + 2 * (int)(size.x * 2 + size.y * 2 + 4) + 2 * (int)(size.z * 2 + size.y * 2 + 4))];
		int offset = 0;
		
		int x, y, z;
		
		for (x = 0; x <= size.x; ++x)
		{
			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = -0.4999F;
			vertices[offset++] = -0.5F;
			
			vertices[offset++] = 0;
			vertices[offset++] = 1;
			vertices[offset++] = 0;
			
			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = -0.4999F;
			vertices[offset++] = 0.5F;

			vertices[offset++] = 0;
			vertices[offset++] = 1;
			vertices[offset++] = 0;
			
			
			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = 0.4999F;
			vertices[offset++] = -0.5F;

			vertices[offset++] = 0;
			vertices[offset++] = -1;
			vertices[offset++] = 0;

			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = 0.4999F;
			vertices[offset++] = 0.5F;

			vertices[offset++] = 0;
			vertices[offset++] = -1;
			vertices[offset++] = 0;
		}
		for (z = 0; z <= size.z; ++z)
		{
			vertices[offset++] = -0.5F;
			vertices[offset++] = -0.4999F;
			vertices[offset++] = z / size.z - 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 1;
			vertices[offset++] = 0;

			vertices[offset++] = 0.5F;
			vertices[offset++] = -0.4999F;
			vertices[offset++] = z / size.z - 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 1;
			vertices[offset++] = 0;
			
			
			vertices[offset++] = -0.5F;
			vertices[offset++] = 0.4999F;
			vertices[offset++] = z / size.z - 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = -1;
			vertices[offset++] = 0;

			vertices[offset++] = 0.5F;
			vertices[offset++] = 0.4999F;
			vertices[offset++] = z / size.z - 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = -1;
			vertices[offset++] = 0;
		}
		
		for (x = 0; x <= size.x; ++x)
		{
			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = -0.5F;
			vertices[offset++] = -0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = 1;

			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = 0.5F;
			vertices[offset++] = -0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = 1;

			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = -0.5F;
			vertices[offset++] = 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = -1;

			vertices[offset++] = x / size.x * 0.9998F - 0.4999F;
			vertices[offset++] = 0.5F;
			vertices[offset++] = 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = -1;
		}
		for (y = 0; y <= size.y; ++y)
		{
			vertices[offset++] = -0.5F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = -0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = 1;

			vertices[offset++] = 0.5F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = -0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = 1;


			vertices[offset++] = -0.5F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = -1;

			vertices[offset++] = 0.5F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = 0.4999F;

			vertices[offset++] = 0;
			vertices[offset++] = 0;
			vertices[offset++] = -1;
		}
		
		for (z = 0; z <= size.z; ++z)
		{
			vertices[offset++] = -0.4999F;
			vertices[offset++] = -0.5F;
			vertices[offset++] = z / size.z * 0.9998F - 0.4999F;

			vertices[offset++] = 1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;

			vertices[offset++] = -0.4999F;
			vertices[offset++] = 0.5F;
			vertices[offset++] = z / size.z * 0.9998F - 0.4999F;

			vertices[offset++] = 1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;

			vertices[offset++] = 0.4999F;
			vertices[offset++] = -0.5F;
			vertices[offset++] = z / size.z * 0.9998F - 0.4999F;

			vertices[offset++] = -1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;

			vertices[offset++] = 0.4999F;
			vertices[offset++] = 0.5F;
			vertices[offset++] = z / size.z * 0.9998F - 0.4999F;

			vertices[offset++] = -1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;
		}
		for (y = 0; y <= size.y; ++y)
		{
			vertices[offset++] = -0.4999F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = -0.5F;

			vertices[offset++] = 1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;

			vertices[offset++] = -0.4999F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = 0.5F;

			vertices[offset++] = 1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;


			vertices[offset++] = 0.4999F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = -0.5F;

			vertices[offset++] = -1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;

			vertices[offset++] = 0.4999F;
			vertices[offset++] = y / size.y * 0.9998F - 0.4999F;
			vertices[offset++] = 0.5F;

			vertices[offset++] = -1;
			vertices[offset++] = 0;
			vertices[offset++] = 0;
		}
		
		Shape shape = new Shape();
		shape.init(GLES20.GL_LINES, vertices, vertices.length / (3 + 3), getPass().getAttributes());

		return shape;
	}
}
