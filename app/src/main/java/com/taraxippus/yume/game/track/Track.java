package com.taraxippus.yume.game.track;

import com.taraxippus.yume.game.World;
import com.taraxippus.yume.util.VectorF;
import com.taraxippus.yume.util.Position;
import android.opengl.Matrix;

public abstract class Track
{
	public static final float SIZE = 50;	
	public final World world;
	private Track lastPiece, nextPiece;	
	private float z;
	float[] lastMatrix;
	float[] nextMatrix = new float[16];
	
	public Track(World world, float z)
	{
		this.world = world;
		this.z = z;
	}
	
	public void create()
	{
		if (lastPiece == null)
		{
			lastMatrix = new float[16];
			Matrix.setIdentityM(lastMatrix, 0);
		}
		else
			lastMatrix = lastPiece.lastMatrix;
			
		System.arraycopy(lastMatrix, 0, nextMatrix, 0, 16);
		Matrix.translateM(nextMatrix, 0, 0, 0, SIZE);
	}
	
	public Track getLastPiece()
	{
		return lastPiece;
	}
	
	public Track getNextPiece()
	{
		return nextPiece;
	}
	
	public Track getPiece(VectorF position)
	{
		return position.z < z ? lastPiece : position.z > z + SIZE ? nextPiece : this;
	}
	
	public VectorF getPosition(VectorF position)
	{
		position.z -= z;
		VectorF tmp = VectorF.obtain();
		tmp.set(position);
		
		float delta = 1F / (1 + (float) Math.pow(Math.E, 6 - 12 * (position.z)));
		position.multiplyBy(lastMatrix);
		tmp.multiplyBy(nextMatrix);
		
		position.multiplyBy(1 - delta).add(tmp.multiplyBy(delta));
		
		tmp.release();
		return position;
	}
	
	public static void createTrack(World world)
	{
		Track piece, last = null;
		for (int i = 0; i < 10; ++i)
		{
			piece = new TrackStraight(world, i * SIZE);
			piece.lastPiece = last;
			if (last != null)
			{
				last.nextPiece = piece;
				last.create();
			}
		}
		
		last.create();
	}
}
