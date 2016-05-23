package com.taraxippus.yume.game.gameobject;
import android.opengl.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.render.*;

public class Sphere extends SceneObject
{
	public static final int rings = 50;
	public static final int sectors = 50;
	
	public Sphere(World world)
	{
		super(world);
	}
	
	@Override
	public Shape createShape()
	{
		final Shape shape = new Shape();

		final float[] vertices = new float[rings * sectors * 6];
		
		final float R = 1.0F / (float) (rings - 1);
		final float S = 1.0F / (float) (sectors - 1);
		int r, s, offset = 0;
		float x, y, z;

		for (r = 0; r < rings; r++) 
			for (s = 0; s < sectors; s++) 
			{
				y = (float) (Math.sin(-Math.PI / 2 + Math.PI * r * R));
				x = (float) (Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
				z = (float) (Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

				vertices[offset++] = x * 0.5F;
				vertices[offset++] = y * 0.5F;
				vertices[offset++] = z * 0.5F;

				vertices[offset++] = x;
				vertices[offset++] = y;
				vertices[offset++] = z;
			}

		offset = 0;

		final short[] indices = new short[(rings - 1) * (sectors - 1) * 6];

		for (r = 0; r < rings - 1; r++)
			for (s = 0; s < sectors - 1; s++)
			{
				indices[offset++] = (short) (r * sectors + s);
				indices[offset++] = (short) ((r + 1) * sectors + (s + 1));
				indices[offset++] = (short) (r * sectors + (s + 1));
				
				indices[offset++] = (short) (r * sectors + s);
				indices[offset++] = (short) ((r + 1) * sectors + s);
				indices[offset++] = (short) ((r + 1) * sectors + (s + 1));
			}
			
		shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());
			
		return shape;
	}
}
