package com.taraxippus.yume.game.track;

import android.opengl.Matrix;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.model.HexagonTubeModel;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.util.VectorF;

public abstract class Track
{
	public static final float LENGTH = 50;	
	public static final float WIDTH = 10;	
	
	public static final float TRACK_ALPHA = 1.0F;	
	public static final float TRACK_SIDE_ALPHA = 1.0F;	
	
	public static final int TRACK_COLOR = 0xFFFFFF;	
	public static final int TRACK_SIDE_COLOR = 0x00CCFF;	
	
	public static final Model hexagonTubeModel = new HexagonTubeModel(100, 10);
	
	public final World world;
	private Track lastPiece = this, nextPiece = this;	
	private float z;
	public float[] lastMatrix;
	public float[] nextMatrix = new float[16];
	
	public Track(World world, float z)
	{
		this.world = world;
		this.z = z;
	}
	
	public void create()
	{
		if (lastPiece == this)
		{
			lastMatrix = new float[16];
			Matrix.setIdentityM(lastMatrix, 0);
		}
		else
			lastMatrix = lastPiece.nextMatrix;
			
		System.arraycopy(lastMatrix, 0, nextMatrix, 0, 16);
		Matrix.translateM(nextMatrix, 0, 0, 0, LENGTH);
	}
	
	public abstract void addGameObjects();
	
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
		return position.z < z ? lastPiece : position.z > z + LENGTH ? nextPiece : this;
	}
	
	public VectorF getRotation(VectorF position, VectorF out)
	{
		out.set(position);
		out.z -= z;
		VectorF tmp = VectorF.obtain();
		tmp.set(out);

		float delta = 1F / (1 + (float) Math.pow(Math.E, 6 - 12 * (out.z)));
		out.setEuler(lastMatrix);
		tmp.setEuler(nextMatrix);

		out.multiplyBy(1 - delta).add(tmp.multiplyBy(delta));

		tmp.release();
		return out;
	}
	
	public VectorF getPosition(VectorF position, VectorF out)
	{
		out.set(position);
		out.z -= z;
		VectorF tmp = VectorF.obtain();
		tmp.set(out);
		
		float delta = 1F / (1 + (float) Math.pow(Math.E, 6 - 12 * (out.z)));
		out.multiplyBy(lastMatrix);
		tmp.multiplyBy(nextMatrix);
		
		out.multiplyBy(1 - delta).add(tmp.multiplyBy(delta));
		
		tmp.release();
		return out;
	}
	
	public static Track createTrack(World world)
	{
		Track piece, last = null;
		for (int i = 0; i < 5; ++i)
		{
			if (i % 2 == 1)
				piece = new TrackRollRight(world, i * LENGTH);
			else
				piece = new TrackStraight(world, i * LENGTH);
			
			if (last != null)
			{
				piece.lastPiece = last;
				
				last.nextPiece = piece;
				last.create();
			}
			last = piece;
		}
		
		last.create();
		
		while (last != null)
		{
			last.addGameObjects();
			if (last.lastPiece == last)
				break;
			last = last.lastPiece;
		}
		
		return last;
	}
}
