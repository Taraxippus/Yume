package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;
import android.view.*;
import android.view.LayoutInflater.*;

public class MovingObject extends Box implements IMover
{
	public static final float JUMP_DURATION = 0.5F;
	public static final float JUMP_PAUSE = 0.15F;
	
	public Path path;
	public Path.Step lastStep, nextStep;

	public MovingObject(World world)
	{
		super(world);

		this.translate(0, 0.5F * 0.75F, 0);
		this.scale(0.75F, 0.75F, 0.75F);

		this.specularity = 50F;
	}

	float jumpTick;
	float lastRotationY;
	float nextRotationY;
	
	@Override
	public void update()
	{
		super.update();

		if (jumpTick > 0)
		{
			jumpTick -= Main.FIXED_DELTA;

			float delta = (getJumpDuration() - jumpTick) / JUMP_DURATION;

			this.position.set(
				nextStep.x * delta + lastStep.x * (1 - delta), 
				0.5F * scale.y + (0.5F * World.GRAVITY * delta * delta + -World.GRAVITY * 0.5F * delta),
				nextStep.z * delta + lastStep.z * (1 - delta));

			this.rotation.x = delta * 90F;
			
			this.updateMatrix();

			if (jumpTick <= 0)
			{
				this.position.set(nextStep.x, 0.5F * scale.y, nextStep.z);
				this.rotation.x = 0;
				this.updateMatrix();
				
				nextStep();
			}
		}
		else if (jumpTick < 0)
		{
			jumpTick += Main.FIXED_DELTA;
			
			this.rotation.y = lastRotationY * -jumpTick / getJumpPause() + nextRotationY * (1 + jumpTick / getJumpPause());
			this.updateMatrix();
			
			if (jumpTick >= 0)
			{
				this.rotation.y = nextRotationY;
				this.updateMatrix();
				
				jumpTick = getJumpDuration();
			}
		}
	}
	
	public void nextStep()
	{
		if (path != null && path.hasNext())
		{
			lastStep = nextStep;
			nextStep = path.nextStep();
			if (lastStep == null)
			{
				lastStep = nextStep;
				nextStep = path.nextStep();
			}
			jumpTick = -getJumpPause();
			
			lastRotationY = rotation.y;
			nextRotationY = faceStep(nextStep);
			
			if (nextRotationY - lastRotationY > 180)
			{
				nextRotationY -= 360;
			}
			else if (nextRotationY - lastRotationY < -180)
			{
				nextRotationY += 360;
			}
		}
		else
		{
			jumpTick = 0;
			lastStep = null;
			nextStep= null;
			
			if (path != null)
				onPathFinished();
		}
	}
	
	public float faceStep(Path.Step step)
	{
		final int dX = step.x - Math.round(position.x);
		final int dZ = step.z - Math.round(position.z);
		
		if (dX == 0 && dZ == 1)
			return 0;
			
		else if (dX == 0 && dZ == -1)
			return 180;
		
		else if (dX == 1 && dZ == 0)
			return 90;

		else if (dX == -1 && dZ == 0)
			return -90;
			
		else
			return rotation.y;
	
	}
	
	public void onPathFinished()
	{
		path.finish();
	}
	
	public float getJumpDuration()
	{
		return JUMP_DURATION;
	}
	
	public float getJumpPause()
	{
		return JUMP_PAUSE;
	}

	public void setPath(Path path)
	{
		this.path = path;

		if (jumpTick == 0)
			nextStep();
	}
	
	public void checkPath()
	{
		if (path != null && !path.finished)
			setPath(world.main.game.pathFinder.findPath(this, nextStep.x, nextStep.z, path.getTarget().x, path.getTarget().z, false));
	}
}
