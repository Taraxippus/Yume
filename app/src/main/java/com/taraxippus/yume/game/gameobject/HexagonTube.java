package com.taraxippus.yume.game.gameobject;
import android.opengl.GLES20;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Shape;
import com.taraxippus.yume.util.VectorF;

public class HexagonTube extends SceneObject
{
	public static final int xCount = 100, zCount = 10;
	public final float[] heights = new float[xCount * zCount];
	
	public HexagonTube(World world)
	{
		super(world);
		
		for (int i = 0; i < heights.length; ++i)
			heights[i] = world.random.nextFloat() * 0.5F + 0.5F;
			
		setPass(Pass.HEXAGON_OUTLINE);
	}

	@Override
	public void update()
	{
		super.update();
		
		rotate(0, 0, Main.FIXED_DELTA * 10);
	}
	
	public static final float RATIO = (float) (Math.sqrt(3) / 2F);
	
	@Override
	public Shape createShape()
	{
		final Shape shape = new Shape();

		final float[] vertices = new float[8 * (7 * 2 + 6 * 4) * xCount * zCount];

		float height = (float) Math.PI * 2 / (xCount);
		float width = 1F / (3F * zCount);
		
		int offset0 = 0, offset1, offset2;
		int x, z, i;
		float posX, posZ, scale;
		for (x = 0; x < xCount; ++x) 
			for (z = 0; z < zCount; ++z) 
			{
				posX = x * height;
				posZ = -0.5F + (3F * z + (x % 2) * 1.5F) * width;
				scale = 1 - heights[x * zCount + z] * 0.3F;
				
				offset0 = 38 * 8 * (x * zCount + z);
				offset1 = offset0 + 7 * 8;
				offset2 = offset0;
				
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;

				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height * 2) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height * 2) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height * 2) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height * 2) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width * 2;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
					
				for (i = 0; i < 5; ++i)
				{
					System.arraycopy(vertices, offset2 + 8 * (i + 1), vertices, offset2 + i * 32 + 14 * 8, 16);
					System.arraycopy(vertices, offset2 + 8 * (i + 8), vertices, offset2 + i * 32 + 14 * 8 + 16, 16);
				}
				
				System.arraycopy(vertices, offset2 + 8 * 6, vertices, offset2 + 5 * 32 + 14 * 8, 8);
				System.arraycopy(vertices, offset2 + 8, vertices, offset2 + 5 * 32 + 14 * 8 + 8, 8);
				System.arraycopy(vertices, offset2 + 8 * 13, vertices, offset2 + 5 * 32 + 14 * 8 + 16, 8);
				System.arraycopy(vertices, offset2 + 8 * 8, vertices, offset2 + 5 * 32 + 14 * 8 + 24, 8);
			}

		offset0 = 0;

		final short[] indices = new short[3 * xCount * zCount * (6 * 2 + 6 * 4)];

		for (x = 0; x < xCount; ++x) 
			for (z = 0; z < zCount; ++z) 
			{
				for (i = 1; i < 6; ++i)
				{
					indices[offset0++] = (short) ((x * zCount + z) * 38);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + i + 1);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + i);
					
					indices[offset0++] = (short) ((x * zCount + z) * 38 + 7);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + i + 7);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + i + 8);
				}
				
				indices[offset0++] = (short) ((x * zCount + z) * 38);
				indices[offset0++] = (short) ((x * zCount + z) * 38 + 1);
				indices[offset0++] = (short) ((x * zCount + z) * 38 + 6);

				indices[offset0++] = (short) ((x * zCount + z) * 38 + 7);
				indices[offset0++] = (short) ((x * zCount + z) * 38 + 13);
				indices[offset0++] = (short) ((x * zCount + z) * 38 + 8);
				
				for (i = 0; i < 6; ++i)
				{
					indices[offset0++] = (short) ((x * zCount + z) * 38 + 14 + i * 4);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + 14 + i * 4 + 1);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + 14 + i * 4 + 2);

					indices[offset0++] = (short) ((x * zCount + z) * 38 + 14 + i * 4 + 1);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + 14 + i * 4 + 3);
					indices[offset0++] = (short) ((x * zCount + z) * 38 + 14 + i * 4 + 2);
				}
			}
			
		shape.initGenerateNormals(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
	
	@Override
	public Shape createOutlineShape()
	{
		final Shape shape = new Shape();

		final float[] vertices = new float[8 * (7 * 2) * xCount * zCount];

		float height = (float) Math.PI * 2 / (xCount);
		float width = 1F / (3F * zCount);
		
		VectorF tmp = VectorF.obtain();
		
		int offset0 = 0, offset1;
		int x, z, i;
		float posX, posZ, scale;
		for (x = 0; x < xCount; ++x) 
			for (z = 0; z < zCount; ++z) 
			{
				posX = x * height;
				posZ = -0.5F + (3F * z + (x % 2) * 1.5F) * width;
				scale = 1 -  heights[x * zCount + z] * 0.3F;
				
				offset0 = 14 * 8 * (x * zCount + z);
				offset1 = offset0 + 7 * 8;
				
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height * 2) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height * 2) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height * 2) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height * 2) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width * 2;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 5;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
			}
			
		tmp.release();

		offset0 = 0;

		final short[] indices = new short[3 * xCount * zCount * (6 * 2 + 6 * 4)];

		for (x = 0; x < xCount; ++x) 
			for (z = 0; z < zCount; ++z) 
			{
				for (i = 1; i < 6; ++i)
				{
					indices[offset0++] = (short) ((x * zCount + z) * 14);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i + 1);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i);

					indices[offset0++] = (short) ((x * zCount + z) * 14 + 7);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i + 7);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i + 8);
					
					
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i + 7);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i + 8);

					indices[offset0++] = (short) ((x * zCount + z) * 14 + i + 8);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i);
					indices[offset0++] = (short) ((x * zCount + z) * 14 + i + 1);
				}

				indices[offset0++] = (short) ((x * zCount + z) * 14);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 1);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 6);

				indices[offset0++] = (short) ((x * zCount + z) * 14 + 7);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 13);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 8);
				
				
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 13);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 6);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 8);

				indices[offset0++] = (short) ((x * zCount + z) * 14 + 8);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 6);
				indices[offset0++] = (short) ((x * zCount + z) * 14 + 1);
		}
		
		shape.initGenerateNormals(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
}
