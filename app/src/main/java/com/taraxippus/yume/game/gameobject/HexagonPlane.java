package com.taraxippus.yume.game.gameobject;
import android.opengl.GLES20;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Shape;
import com.taraxippus.yume.util.VectorF;

public class HexagonPlane extends SceneObject
{
	public static final int xCount = 25, zCount = 10;
	
	public HexagonPlane(World world)
	{
		super(world);
	}

	public static final float RATIO = (float) (Math.sqrt(3) / 2F);
	
	private static final VectorF[] normals = new VectorF[]
	{
		new VectorF(0, 0, -1).rotateY(30).normalize(),
		new VectorF(1, 0, 0),
		new VectorF(0, 0, 1).rotateY(-30).normalize(),
		new VectorF(0, 0, 1).rotateY(30).normalize(),
		new VectorF(-1, 0, 0),
		new VectorF(0, 0, -1).rotateY(-30).normalize(),
	};
	
	@Override
	public Shape createShape()
	{
		final Shape shape = new Shape();

		final float[] vertices = new float[6 * (7 * 2 + 6 * 4) * xCount * zCount];

		float width = 1F / (2.5F * zCount + 0.5F);
		float height = width * RATIO;//1F / (xCount + 1);

		int offset0 = 0, offset1, offset2;
		int x, z, i;
		float posX, posZ, scale;
		for (x = 0; x < xCount; ++x) 
			for (z = 0; z < zCount; ++z) 
			{
				posX = -0.5F + x * height;
				posZ = -0.5F + (3F * z + (x % 2) * 1.5F) * width;
				scale = world.random.nextFloat() * 0.9F + 0.1F;
				
				offset0 = 38 * 6 * (x * zCount + z);
				offset1 = offset0 + 7 * 6;
				offset2 = offset0;
				
				vertices[offset1++] = vertices[offset0++] = posX + height;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width;
				offset0 += 3; offset1 += 3;
				
				vertices[offset1++] = vertices[offset0++] = posX + height;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX + height * 2;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX + height * 2;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX + height;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width * 2;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 3;
				
				for (i = 0; i < 7; i++)
				{
					vertices[offset2 + i * 6 + 4] = 1;
					vertices[offset2 + (i + 7) * 6 + 4] = -1;
				}
					
				for (i = 0; i < 5; ++i)
				{
					System.arraycopy(vertices, offset2 + 6 * (i + 1), vertices, offset2 + i * 24 + 14 * 6, 12);
					System.arraycopy(vertices, offset2 + 6 * (i + 8), vertices, offset2 + i * 24 + 14 * 6 + 12, 12);
				}
				
				System.arraycopy(vertices, offset2 + 6 * 6, vertices, offset2 + 5 * 24 + 14 * 6, 3);
				System.arraycopy(vertices, offset2 + 6, vertices, offset2 + 5 * 24 + 14 * 6 + 6, 3);
				System.arraycopy(vertices, offset2 + 6 * 13, vertices, offset2 + 5 * 24 + 14 * 6 + 12, 3);
				System.arraycopy(vertices, offset2 + 6 * 8, vertices, offset2 + 5 * 24 + 14 * 6 + 18, 3);
				
				offset2 += 6 * 14;
				
				for (i = 0; i < 6; i++)
				{
					vertices[offset2 + i * 24 + 3] = normals[i].x;
					vertices[offset2 + i * 24 + 4] = normals[i].y;
					vertices[offset2 + i * 24 + 5] = normals[i].z;
					
					vertices[offset2 + i * 24 + 9] = normals[i].x;
					vertices[offset2 + i * 24 + 10] = normals[i].y;
					vertices[offset2 + i * 24 + 11] = normals[i].z;
					
					vertices[offset2 + i * 24 + 15] = normals[i].x;
					vertices[offset2 + i * 24 + 16] = normals[i].y;
					vertices[offset2 + i * 24 + 17] = normals[i].z;
					
					vertices[offset2 + i * 24 + 21] = normals[i].x;
					vertices[offset2 + i * 24 + 22] = normals[i].y;
					vertices[offset2 + i * 24 + 23] = normals[i].z;
				}
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
			
		shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
	
	private static final VectorF[] outlineNormals = new VectorF[]
	{
		new VectorF(0, 1, 0),
		new VectorF(0, -1, 0),
		
		new VectorF(1, 0, 0),
		new VectorF(0, 0, 1),
		new VectorF(0, 0, 1),
		new VectorF(-1, 0, 0),
		new VectorF(0, 0, -1),
	};
	
	@Override
	public Shape createOutlineShape()
	{
		final Shape shape = new Shape();

		final float[] vertices = new float[6 * (7 * 2 + 6 * 4) * xCount * zCount];

		float width = 1F / (2.5F * zCount + 0.5F);
		float height = width * RATIO;//1F / (xCount + 1);

		int offset0 = 0, offset1, offset2;
		int x, z, i;
		float posX, posZ, scale;
		for (x = 0; x < xCount; ++x) 
			for (z = 0; z < zCount; ++z) 
			{
				posX = -0.5F + x * height;
				posZ = -0.5F + (3F * z + (x % 2) * 1.5F) * width;
				scale = world.random.nextFloat() * 0.9F + 0.1F;

				offset0 = 38 * 6 * (x * zCount + z);
				offset1 = offset0 + 7 * 6;
				offset2 = offset0;

				vertices[offset1++] = vertices[offset0++] = posX + height;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width;
				offset0 += 3; offset1 += 3;

				vertices[offset1++] = vertices[offset0++] = posX + height;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX + height * 2;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX + height * 2;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX + height;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width * 2;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2 * 3;
				offset0 += 3; offset1 += 3;
				vertices[offset1++] = vertices[offset0++] = posX;
				vertices[offset1++] = -(vertices[offset0++] = 0.5F * scale);
				vertices[offset1++] = vertices[offset0++] = posZ + width / 2;
				offset0 += 3; offset1 += 3;

				for (i = 0; i < 7; i++)
				{
					vertices[offset2 + i * 6 + 4] = 1;
					vertices[offset2 + (i + 7) * 6 + 4] = -1;
				}

				for (i = 0; i < 5; ++i)
				{
					System.arraycopy(vertices, offset2 + 6 * (i + 1), vertices, offset2 + i * 24 + 14 * 6, 12);
					System.arraycopy(vertices, offset2 + 6 * (i + 8), vertices, offset2 + i * 24 + 14 * 6 + 12, 12);
				}

				System.arraycopy(vertices, offset2 + 6 * 6, vertices, offset2 + 5 * 24 + 14 * 6, 3);
				System.arraycopy(vertices, offset2 + 6, vertices, offset2 + 5 * 24 + 14 * 6 + 6, 3);
				System.arraycopy(vertices, offset2 + 6 * 13, vertices, offset2 + 5 * 24 + 14 * 6 + 12, 3);
				System.arraycopy(vertices, offset2 + 6 * 8, vertices, offset2 + 5 * 24 + 14 * 6 + 18, 3);

				offset2 += 6 * 14;

				for (i = 0; i < 6; i++)
				{
					vertices[offset2 + i * 24 + 3] = normals[i].x;
					vertices[offset2 + i * 24 + 4] = normals[i].y;
					vertices[offset2 + i * 24 + 5] = normals[i].z;

					vertices[offset2 + i * 24 + 9] = normals[i].x;
					vertices[offset2 + i * 24 + 10] = normals[i].y;
					vertices[offset2 + i * 24 + 11] = normals[i].z;

					vertices[offset2 + i * 24 + 15] = normals[i].x;
					vertices[offset2 + i * 24 + 16] = normals[i].y;
					vertices[offset2 + i * 24 + 17] = normals[i].z;

					vertices[offset2 + i * 24 + 21] = normals[i].x;
					vertices[offset2 + i * 24 + 22] = normals[i].y;
					vertices[offset2 + i * 24 + 23] = normals[i].z;
				}
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

		shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

		return shape;
	}
}
