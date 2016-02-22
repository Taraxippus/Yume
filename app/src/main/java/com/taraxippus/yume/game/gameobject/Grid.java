package com.taraxippus.yume.game.gameobject;

import android.opengl.*;
import com.taraxippus.yume.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;

public class Grid extends SceneObject
{
	public static final float MAX_ALPHA = 0.95F;
	public static final float ANIMATION_DURATION = 0.5F;
	
	public static final float WIDTH = 0.0015F;
	public static final float EPSILON = WIDTH;
	
	final VectorF size = new VectorF(1, 1, 1);

	public Grid(World world, VectorF size)
	{
		super(world);

		this.size.set(size);
		this.scale(this.size.x, this.size.y, this.size.z);
		
		this.specularityExponent = 50F;
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
		final float[] vertices = new float[(3 + 3) * 24 * (int)(size.x + 1 + size.y + 1 + size.z + 1)];
		int offset = 0;
		
		int x, y, z;
		
		for (x = 0; x <= size.x; ++x)
		{
			offset = addLine(vertices, offset,
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -(0.5F - EPSILON),
							 -0.5F,
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -(0.5F - EPSILON),
							 0.5F,
							 0, 1, 0, -1, 0, 0);
			
			offset = addLine(vertices, offset,
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 (0.5F - EPSILON),
							 -0.5F,
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 (0.5F - EPSILON),
							 0.5F,
							 0, -1, 0, 1, 0, 0);
							 
			offset = addLine(vertices, offset,
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -0.5F,
							 -(0.5F - EPSILON),
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0.5F,
							 -(0.5F - EPSILON),
							 0, 0, 1, 1, 0, 0);
							 
			offset = addLine(vertices, offset,
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -0.5F,
							 (0.5F - EPSILON),
							 x / size.x * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0.5F,
							 (0.5F - EPSILON),
							 0, 0, -1, -1, 0, 0);
		}
		for (y = 0; y <= size.y; ++y)
		{
			offset = addLine(vertices, offset,
							 -(0.5F - EPSILON),
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -0.5F,
							 -(0.5F - EPSILON),
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0.5F,
							 1, 0, 0, 0, 1, 0);
							 
			offset = addLine(vertices, offset,
							 (0.5F - EPSILON),
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -0.5F,
							 (0.5F - EPSILON),
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0.5F,
							 -1, 0, 0, 0, -1, 0);
							 
			offset = addLine(vertices, offset,
							 -0.5F,
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -(0.5F - EPSILON),
							 0.5F,
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -(0.5F - EPSILON),
							 0, 0, 1, 0, -1, 0);
							 
			offset = addLine(vertices, offset,
							 -0.5F,
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 (0.5F - EPSILON),
							 0.5F,
							 y / size.y * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 (0.5F - EPSILON),
							 0, 0, -1, 0, 1, 0);
		}
		for (z = 0; z <= size.z; ++z)
		{
			offset = addLine(vertices, offset,
							 -0.5F,
							 -(0.5F - EPSILON),
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0.5F,
							 -(0.5F - EPSILON),
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0, 1, 0, 0, 0, 1);

			offset = addLine(vertices, offset,
							 -0.5F,
							 (0.5F - EPSILON),
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0.5F,
							 (0.5F - EPSILON),
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 0, -1, 0, 0, 0, -1);
							 
			offset = addLine(vertices, offset,
							 -(0.5F - EPSILON),
							 -0.5F,
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -(0.5F - EPSILON),
							 0.5F,
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 1, 0, 0, 0, 0, -1);
							 
			offset = addLine(vertices, offset,
							 (0.5F - EPSILON),
							 -0.5F,
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 (0.5F - EPSILON),
							 0.5F,
							 z / size.z * (1F - EPSILON * 2) - (0.5F - EPSILON),
							 -1, 0, 0, 0, 0, 1);
		}
		
		Shape shape = new Shape();
		shape.init(GLES20.GL_TRIANGLES, vertices, vertices.length / (3 + 3), getPass().getAttributes());

		return shape;
	}
	
	public int addLine(float[] vertices, int offset, float x1, float y1, float z1, float x2, float y2, float z2, float normalX, float normalY, float normalZ, float widthFactor, float heightFactor, float lengthFactor)
	{
		vertices[offset++] = x1 - WIDTH * widthFactor;
		vertices[offset++] = y1 - WIDTH * heightFactor;
		vertices[offset++] = z1 - WIDTH * lengthFactor;

		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;

		vertices[offset++] = x1 + WIDTH * widthFactor;
		vertices[offset++] = y1 + WIDTH * heightFactor;
		vertices[offset++] = z1 + WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;

		vertices[offset++] = x2 - WIDTH * widthFactor;
		vertices[offset++] = y2 - WIDTH * heightFactor;
		vertices[offset++] = z2 - WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		
		
		vertices[offset++] = x2 - WIDTH * widthFactor;
		vertices[offset++] = y2 - WIDTH * heightFactor;
		vertices[offset++] = z2 - WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		vertices[offset++] = x1 + WIDTH * widthFactor;
		vertices[offset++] = y1 + WIDTH * heightFactor;
		vertices[offset++] = z1 + WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		vertices[offset++] = x2 + WIDTH * widthFactor;
		vertices[offset++] = y2 + WIDTH * heightFactor;
		vertices[offset++] = z2 + WIDTH * lengthFactor;
		
		vertices[offset++] = normalX;
		vertices[offset++] = normalY;
		vertices[offset++] = normalZ;
		
		return offset;
	}
}
