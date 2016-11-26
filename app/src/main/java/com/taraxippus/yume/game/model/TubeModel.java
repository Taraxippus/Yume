package com.taraxippus.yume.game.model;

import android.opengl.Matrix;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.VectorF;

public class TubeModel extends Model
{
	final int intersectionsX, intersectionsZ;
	final float[] modelMatrix;

	public TubeModel(float[] modelMatrix, int intersectionsX, int intersectionsZ)
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
		float[] vertices = new float[3 * (intersectionsX + 1) * (intersectionsZ + 1)];

		int offset = 0;
		float delta;
		VectorF tmp = VectorF.obtain();

		int x, z;
		
			for (x = 0; x <= intersectionsX; ++x)
			{
				for (z = 0; z <= intersectionsZ; ++z)
				{
					delta = Track.getDelta((float) z / intersectionsZ);

					tmp.set(0.21F * (float) Math.sin(Math.PI * 2 * x / intersectionsX), 0.21F * (float) Math.cos(Math.PI * 2 * x / intersectionsX), -0.5F + (float) z / intersectionsZ)
						.multiplyBy(modelMatrix)
						.multiplyBy(delta)
						.add(0.21F * (float) Math.sin(Math.PI * 2 * x / intersectionsX) * (1F - delta), 
							 0.21F * (float) Math.cos(Math.PI * 2 * x / intersectionsX) * (1F - delta), 
							 (-0.5F + (float) z / intersectionsZ) * (1F - delta));
					tmp.put(vertices, offset);
					offset += 3;
				}
			}
		

		tmp.release();

		return vertices;
	}

	@Override
	public short[] getIndices()
	{
		short[] indices = new short[6 * (intersectionsX * intersectionsZ)];

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
			}
		}

		return indices;
	}
}
