package com.taraxippus.yume.game.gameobject;

import android.opengl.GLES20;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Renderer;
import com.taraxippus.yume.util.VectorF;

public class Player extends OnTrackObject
{
	public static final int TRAIL_SIZE = 200;	
	public final VectorF velocity = new VectorF();
	public final VectorF[] lastPosition = new VectorF[2 * TRAIL_SIZE];
	
	public static final float DURATION = 0.5F;

	public float tilt;
	
	public Player(World world, Track track)
	{
		super(world, track);
		
		scale(0.15F, 0.04F, 0.25F);
		setTouchable(true);
		translate(0, 0, 0.5F);
		rotation.y = 180;
		
		//world.add(new Trail(world, this));
	}

	private float rotateTick;
	
	@Override
	public void update()
	{
		tilt *= 0.9F;
		
		this.trackPosition.add(velocity.x * Main.FIXED_DELTA, velocity.y * Main.FIXED_DELTA, velocity.z * Main.FIXED_DELTA);
		trackPosition.y = 0.02F + (float) Math.cos(world.time * 1.55790F) * 0.005F;
		
		this.velocity.multiplyBy(0.99F);
		
		if (trackPosition.x < -Track.WIDTH / 2F)
		{
			trackPosition.x = -Track.WIDTH / 2F;
			
			velocity.multiplyBy(0.95F);
		}
		else if (trackPosition.x > Track.WIDTH / 2F)
		{
			trackPosition.x = Track.WIDTH / 2F;

			velocity.multiplyBy(0.95F);
		}
		
		rotation.z = 0;
		
		if (rotateTick > 0)
		{
			rotateTick -= Main.FIXED_DELTA;
			
			if (rotateTick <= 0)
				rotateTick = 0;
			
			rotation.z = rotateTick / DURATION * rotateTick / DURATION * 360;
		}
		
		rotation.z += (float) (Math.sin(world.time * 2F) * 5 + Math.cos(world.time * 0.356F) * 0.25F + tilt);
		
		updateMatrix();

//		if (lastPosition[0] == null)
//		{
//			for (int i = 0; i < lastPosition.length - 1; i += 2)
//			{
//				lastPosition[i] = new VectorF(-0.01F, -0.2F, 0.45).multiplyBy(modelMatrix);
//				lastPosition[i + 1] = new VectorF(0.01F, -0.2F, 0.45).multiplyBy(modelMatrix);
//			}
//		}
//		
//		for (int i = lastPosition.length - 1; i >= 2; i--)
//			lastPosition[i].set(lastPosition[i - 2]).subtract(lastPosition[i - 1 - (i % 2) * 2]).normalize().multiplyBy(0.01F).add(lastPosition[i - 2]);
//				
//		lastPosition[0] = new VectorF(-0.01F, -0.2F, 0.45F).multiplyBy(modelMatrix);
//		lastPosition[1] = new VectorF(0.01F, -0.2F, 0.45F).multiplyBy(modelMatrix);
	}
	

	@Override
	public void render(Renderer renderer)
	{
		super.render(renderer);
		
//		Pass.BARRIER.onRender(renderer);
//
//		renderer.uniform(modelMatrix, normalMatrix, Pass.BARRIER);
//		GLES20.glUniform4f(Pass.BARRIER.getProgram().getUniform("u_Color"), 1.0F, 1.0F, 1.0F, 0.5F);
//		
//		if (outlineShape != null)
//			outlineShape.render();
//		
//		getPass().getParent().onRender(renderer);
	}
	
	@Override
	public void onTouch(VectorF intersection, VectorF normal)
	{
		rotateTick += DURATION;
	}

	@Override
	public String toString()
	{
		return trackPosition.toString();
	}
}
