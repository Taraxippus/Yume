package com.taraxippus.yume.game.model;

import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import java.util.Random;

public class HexagonTubeModel extends Model
{
	public final int xCount, zCount;
	public final float[] heights;
	public final float width, height;
	
	public final Random random = new Random();
	
	public HexagonTubeModel(int xCount, int zCount)
	{
		super(Pass.HEXAGON_OUTLINE);
		
		this.xCount = xCount;
		this.zCount = zCount;
		this.heights = new float[xCount * zCount];
		
		for (int i = 0; i < heights.length; ++i)
			heights[i] = random.nextFloat() * 0.25F + 0.5F;
			
		this.height = (float) Math.PI * 2F / (xCount);
		this.width = 1F / (3F * zCount);
	}
	
	@Override
	public float[] getVertices()
	{
		final float[] vertices = new float[5 * (7 * 2) * xCount * zCount];

		int offset0 = 0, offset1;
		int x, z;
		float posX, posZ, scale;
		for (x = 0; x < xCount; ++x) 
			for (z = 0; z < zCount; ++z) 
			{
				posX = x * height;
				posZ = -0.5F + (3F * z + (x % 2) * 1.5F) * width;
				scale = 1 -  heights[x * zCount + z] * 0.3F;
			
				offset0 = 14 * 5 * (x * zCount + z);
				offset1 = offset0 + 7 * 5;
				
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width;
				offset1 += 2;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ;
				offset1 += 2;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height * 2) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height * 2) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset1 += 2;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height * 2) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height * 2) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height * 2) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset1 += 2;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX + height) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX + height) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX + height) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width * 2;
				offset1 += 2;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset1 += 2;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
				vertices[offset0++] = (float) Math.cos(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.cos(posX) * 0.5F;
				vertices[offset0++] = (float) Math.sin(posX) * 0.5F * scale;
				vertices[offset1++] = (float) Math.sin(posX) * 0.5F;
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset1 += 2;
				vertices[offset0++] = posX + height;
				vertices[offset0++] = posZ + width;
			}

		return vertices;
	}
	
	@Override
	public float[] getOutlineVertices()
	{
		final float[] vertices = new float[9 * (7 * 2) * xCount * zCount];

		int offset0 = 0, offset1;
		int x, z;
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

		return vertices;
	}
	
	@Override
	public short[] getIndices()
	{
		final short[] indices = new short[3 * xCount * zCount * (6 * 2 + 6 * 2)];

		int x, z, i, offset0 = 0;
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
		
		return indices;
	}
}
