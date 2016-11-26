package com.taraxippus.yume.game.model;

import android.opengl.Matrix;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.VectorF;

public class TrackSideModel extends Model
{
	final int intersectionsX, intersectionsZ;
	final float[] modelMatrix;

	private static final float HEIGHT = 0.005F;
	private static final float WIDTH = 0.075F * Track.WIDTH;
	private static final float HEIGHT_OFFSET = 0.005F;
	private static final float MAX_WIDTH = Track.WIDTH;
	
	public TrackSideModel(float[] modelMatrix, int intersectionsX, int intersectionsZ)
	{
		super(Pass.SCENE_OUTLINE);

		if (modelMatrix != null)
			this.modelMatrix = modelMatrix;
		else
		{
			this.modelMatrix = new float[16];
			Matrix.setIdentityM(this.modelMatrix, 0);
		}
		this.intersectionsX = intersectionsX;
		this.intersectionsZ = intersectionsZ;
	}

	@Override
	public float[] getVertices()
	{
		float[] vertices = new float[3 * (intersectionsX + 1) * (intersectionsZ + 1) * 4];

		int offset = 0;
		VectorF tmp = VectorF.obtain();

		int x, y, z;
		float delta;
		for (y = 0; y < 2; ++y)
		{
			for (x = 0; x <= intersectionsX; ++x)
			{
				for (z = 0; z <= intersectionsZ; ++z)
				{
					delta = Track.getDelta((float) z / intersectionsZ);
					
					tmp.set((-0.5F + (float) x / intersectionsX * WIDTH) * MAX_WIDTH, (-0.5F + y) * HEIGHT + HEIGHT_OFFSET, -0.5F + (float) z / intersectionsZ)
						.multiplyBy(modelMatrix)
						.multiplyBy(delta)
						.add(((-0.5F + (float) x / intersectionsX * WIDTH)) * MAX_WIDTH * (1F - delta), 
							 ((-0.5F + y) * HEIGHT + HEIGHT_OFFSET) * (1F - delta), 
							 (-0.5F + (float) z / intersectionsZ) * (1F - delta));
					tmp.put(vertices, offset);
					offset += 3;
				}
			}
		}
		
		for (y = 0; y < 2; ++y)
		{
			for (x = 0; x <= intersectionsX; ++x)
			{
				for (z = 0; z <= intersectionsZ; ++z)
				{
					delta = Track.getDelta((float) z / intersectionsZ);
					
					tmp.set((-0.5F + (float) x / intersectionsX * WIDTH + (1 - WIDTH)) * MAX_WIDTH, (-0.5F + y) * HEIGHT + HEIGHT_OFFSET, -0.5F + (float) z / intersectionsZ)
						.multiplyBy(modelMatrix)
						.multiplyBy(delta)
						.add((-0.5F + (float) x / intersectionsX * WIDTH + (1 - WIDTH)) * MAX_WIDTH * (1F - delta), 
							 ((-0.5F + y) * HEIGHT + HEIGHT_OFFSET) * (1F - delta), 
							 (-0.5F + (float) z / intersectionsZ) * (1F - delta));
					tmp.put(vertices, offset);
					offset += 3;
				}
			}
		}

		tmp.release();

		return vertices;
	}
	
	@Override
	public short[] getIndices()
	{
		short[] indices = new short[6 * 2 * (intersectionsX * intersectionsZ * 2 + intersectionsZ * 2)];

		int offset = 0;

		int x, y, z;
		for (y = 0; y <= (intersectionsX + 1) * (intersectionsZ + 1) * 2; y += (intersectionsX + 1) * (intersectionsZ + 1) * 2)
		{
			for (x = 0; x < intersectionsX; ++x)
			{
				for (z = 0; z < intersectionsZ; ++z)
				{
					indices[offset++] = (short) (y + x * (intersectionsZ + 1) + z);
					indices[offset++] = (short) (y + (x + 1) * (intersectionsZ + 1) + z);
					indices[offset++] = (short) (y +x * (intersectionsZ + 1) + z + 1);

					indices[offset++] = (short) (y + x * (intersectionsZ + 1) + z + 1);
					indices[offset++] = (short) (y + (x + 1) * (intersectionsZ + 1) + z);
					indices[offset++] = (short) (y + (x + 1) * (intersectionsZ + 1) + z + 1);


					indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + x * (intersectionsZ + 1) + z);
					indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + x * (intersectionsZ + 1) + z + 1);
					indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + (x + 1) * (intersectionsZ + 1) + z);

					indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + x * (intersectionsZ + 1) + z + 1);
					indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + (x + 1) * (intersectionsZ + 1) + z + 1);
					indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + (x + 1) * (intersectionsZ + 1) + z);

				}
			}

			for (z = 0; z < intersectionsZ; ++z)
			{
				indices[offset++] = (short) (y + z);
				indices[offset++] = (short) (y + z + 1);
				indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + z);
				
				indices[offset++] = (short) (y + z + 1);
				indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + z + 1);
				indices[offset++] = (short) (y + (intersectionsX + 1) * (intersectionsZ + 1) + z);
				
				indices[offset++] = (short) (y + intersectionsX * (intersectionsZ + 1) + z);
				indices[offset++] = (short) (y + (intersectionsX * 2 + 1) * (intersectionsZ + 1) + z);
				indices[offset++] = (short) (y + intersectionsX * (intersectionsZ + 1) + z + 1);

				indices[offset++] = (short) (y + intersectionsX * (intersectionsZ + 1) + z + 1);
				indices[offset++] = (short) (y + (intersectionsX * 2 + 1) * (intersectionsZ + 1) + z);
				indices[offset++] = (short) (y + (intersectionsX * 2 + 1) * (intersectionsZ + 1) + z + 1);
			}
		}

		return indices;
	}
}
