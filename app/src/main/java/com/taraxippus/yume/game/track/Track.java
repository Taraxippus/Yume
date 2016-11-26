package com.taraxippus.yume.game.track;

import android.opengl.Matrix;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.model.HexagonTubeModel;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.game.model.TubeModel;
import com.taraxippus.yume.util.Quaternion;
import com.taraxippus.yume.util.VectorF;

public abstract class Track
{
	public static final float SIZE = 7.5F;	
	public static final float WIDTH = 0.2F;	
	
	public static final float TRACK_ALPHA = 0.25F;	
	public static final float TRACK_SIDE_ALPHA = 0.5F;	
	
	public static final int TRACK_COLOR = 0x00CCFF;	
	public static final int TRACK_SOLID_COLOR = 0x888888;	
	public static final int TRACK_SIDE_COLOR = 0x03CCFF;	
	
	public static final float TRACK_OFFSET = -1500;	
	public static final float TRACK_SIDE_OFFSET = -1000;	
	
	public static final Model hexagonTubeModel = new HexagonTubeModel(90, 10);
	
	public final World world;
	private Track lastPiece = this, nextPiece = this;	
	float z;
	boolean tube;
	public final float[] lastMatrix = new float[16];
	public final float[] nextMatrix = new float[16];
	public final Quaternion lastRotation = new Quaternion();
	public final Quaternion nextRotation = new Quaternion();
	
	public Track(World world, float z, boolean tube)
	{
		this.world = world;
		this.z = z;
		this.tube = tube;
	}
	
	public void create()
	{
		if (lastPiece == this)
		{
			Matrix.setIdentityM(lastMatrix, 0);
			Matrix.scaleM(lastMatrix, 0, SIZE, SIZE, SIZE);
			
			lastRotation.set(Quaternion.identity);
		}
		else
		{
			System.arraycopy(lastPiece.nextMatrix, 0, lastMatrix, 0, 16);
			Matrix.translateM(lastMatrix, 0, 0, 0, 1);
			lastRotation.set(lastPiece.nextRotation);
		}
			
		System.arraycopy(lastMatrix, 0, nextMatrix, 0, 16);
		nextRotation.set(lastRotation);
	}
	
	public abstract void addGameObjects();
	
	public static float getDistance(VectorF position, VectorF position1)
	{
		VectorF tmp = VectorF.obtain();
		
		tmp.set(position);
		tmp.z = (tmp.z + trackLength) % trackLength;
		tmp.subtract(position1.x, position1.y, (position1.z + trackLength) % trackLength);
		tmp.y = 0;
		
		return tmp.release().length();
	}
	
	public Track getPiece(VectorF position)
	{
		if (loop)
			while (position.z < 0)
				position.z += trackLength;
				
		return position.z % trackLength < z ? (lastPiece == this ? this : lastPiece.getPiece(position)) : position.z % trackLength > z + 1 ? (nextPiece == this ? this : nextPiece.getPiece(position)) : this;
	}
	
	public void rotate(VectorF position, float[] matrix)
	{
		Quaternion tmp = new Quaternion();
		float delta = getDelta(position.z % trackLength - z);
		
		tmp.slerp(lastRotation, nextRotation, delta).normalize();
		
		VectorF axis = VectorF.obtain();
		float angle = tmp.angleAxis(axis);
		Matrix.rotateM(matrix, 0, (float) (angle / Math.PI * 180), axis.x, axis.y, axis.z);
		
		axis.release();
		tmp.release();
	}
	
	public VectorF getPosition(VectorF position, VectorF out)
	{
		out.set(position);
		out.z = out.z % trackLength;
		out.z -= z + 0.5F;
		VectorF tmp = VectorF.obtain();
		tmp.set(out);
		
		float delta = getDelta(out.z + 0.5F);
		
		out.multiplyBy(lastMatrix);
		tmp.multiplyBy(nextMatrix);
		
		out.multiplyBy(1 - delta).add(tmp.multiplyBy(delta));
		
		tmp.release();
		return out;
	}
	
	public static final String track = "ssdsuusdrsrsssTsrsr";
	public static final boolean loop = true;
	public static final float trackLength = loop ? track.replace("T", "").length() : Integer.MAX_VALUE;
	
	public static Track createTrack(World world)
	{
		Track piece, first = null, last = null;
		boolean tube = true;
		int offset = 0;
		for (int i = 0; i < track.length(); ++i)
		{
			if (track.charAt(i) == 'T')
			{
				tube = !tube;
				continue;
			}
			
			if (track.charAt(i) == 'r')
				piece = new TrackCurveRight(world, offset, tube);
			else if (track.charAt(i) == 'l')
				piece = new TrackCurveLeft(world, offset, tube);
			else if (track.charAt(i) == 'u')
				piece = new TrackCurveUp(world, offset, tube);
			else if (track.charAt(i) == 'd')
				piece = new TrackCurveDown(world, offset, tube);
			else if (track.charAt(i) == 'R')
				piece = new TrackRollRight(world, offset, tube);
			else if (track.charAt(i) == 'L')
				piece = new TrackRollLeft(world, offset, tube);
			else
				piece = new TrackStraight(world, offset, tube);
				
			if (last != null)
			{
				piece.lastPiece = last;
				last.nextPiece = piece;
				last.create();
			}
			else
				first = piece;
				
			last = piece;
			offset++;
		}
		
		last.create();
		
		if (loop)
		{
			last.nextPiece = first;
			first.lastPiece = last;
		}
		
		while (last != null)
		{
			last.addGameObjects();
			if (last == first)
				break;
			last = last.lastPiece;
		}
		
		return first;
	}
	
	private static final float start = 1F / (1 + (float) Math.pow(Math.E, 6));
	private static final float range = 1F / (1F / (1 + (float) Math.pow(Math.E, -6)) - start);
	
	public static float getDelta(float x)
	{
		return (1F / (1 + (float) Math.pow(Math.E, 6 - 12 * x)) - start) * range;
	}
}
