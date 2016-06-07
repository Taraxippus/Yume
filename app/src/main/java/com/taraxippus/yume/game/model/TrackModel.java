package com.taraxippus.yume.game.model;

import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.VectorF;
import android.opengl.GLES20;

public class TrackModel extends Model
{
	final int intersectionsX, intersectionsZ;
	final float[] modelMatrix;
	
	private static final float HEIGHT = 0.05F;
	private static final float WIDTH = 0.9F;
	
	public TrackModel(float[] modelMatrix, int intersectionsX, int intersectionsZ)
	{
		super(Pass.SCENE_OUTLINE);
		
		this.modelMatrix = modelMatrix;
		this.intersectionsX = intersectionsX;
		this.intersectionsZ = intersectionsZ;
	}
	
	@Override
	public float[] getVertices()
	{
		float[] vertices = new float[6 * (intersectionsX + 1) * (intersectionsZ + 1) * 2];
		
		int offset = 0;
		float delta;
		VectorF tmp = VectorF.obtain();
		
		int x, y, z;
		for (y = 0; y < 2; ++y)
		{
			for (x = 0; x <= intersectionsX; ++x)
			{
				for (z = 0; z <= intersectionsZ; ++z)
				{
					delta = 1F / (1 + (float) Math.pow(Math.E, 6 - 12 * ((float) z / intersectionsZ)));
					
					tmp.set((-0.5F + (float) x / intersectionsX) * WIDTH, (-0.5F + y) * HEIGHT, -0.5F + (float) z / intersectionsZ)
					.multiplyBy(modelMatrix)
					.multiplyBy(delta)
					.add((-0.5F + (float) x / intersectionsX) * WIDTH * (1F - delta), 
					(-0.5F + y) * HEIGHT * (1F - delta), 
					(-0.5F + (float) z / intersectionsZ) * (1F - delta));
					tmp.put(vertices, offset);
					offset += 6;
				}
			}
		}
		
		tmp.release();
		
		return vertices;
	}

	@Override
	public short[] getIndices()
	{
		short[] indices = new short[6 * (intersectionsX * intersectionsZ * 2 + intersectionsZ * 2)];
		
		int offset = 0;
		
		int x, z;
		for (x = 0; x < intersectionsX; ++x)
		{
			for (z = 0; z < intersectionsZ; ++z)
			{
				indices[offset++] = (short) (x * (intersectionsZ + 1) + z);
				indices[offset++] = (short) ((x + 1) * (intersectionsZ + 1) + z);
				indices[offset++] = (short) (x * (intersectionsZ + 1) + z + 1);
				
				indices[offset++] = (short) (x * (intersectionsZ + 1) + z + 1);
				indices[offset++] = (short) ((x + 1) * (intersectionsZ + 1) + z);
				indices[offset++] = (short) ((x + 1) * (intersectionsZ + 1) + z + 1);
				
				
				indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + x * (intersectionsZ + 1) + z);
				indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + x * (intersectionsZ + 1) + z + 1);
				indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + (x + 1) * (intersectionsZ + 1) + z);
				
				indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + x * (intersectionsZ + 1) + z + 1);
				indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + (x + 1) * (intersectionsZ + 1) + z + 1);
				indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + (x + 1) * (intersectionsZ + 1) + z);
				
			}
		}
		
		for (z = 0; z < intersectionsZ; ++z)
		{
			indices[offset++] = (short) (z);
			indices[offset++] = (short) (z + 1);
			indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + z);
			
			indices[offset++] = (short) (z + 1);
			indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + z + 1);
			indices[offset++] = (short) ((intersectionsX + 1) * (intersectionsZ + 1) + z);
			
			indices[offset++] = (short) (intersectionsX * (intersectionsZ + 1) + z);
			indices[offset++] = (short) ((intersectionsX * 2 + 1) * (intersectionsZ + 1) + z);
			indices[offset++] = (short) (intersectionsX * (intersectionsZ + 1) + z + 1);
			
			indices[offset++] = (short) (intersectionsX * (intersectionsZ + 1) + z + 1);
			indices[offset++] = (short) ((intersectionsX * 2 + 1) * (intersectionsZ + 1) + z);
			indices[offset++] = (short) ((intersectionsX * 2 + 1) * (intersectionsZ + 1) + z + 1);
			
		}
		
		return indices;
	}
}
