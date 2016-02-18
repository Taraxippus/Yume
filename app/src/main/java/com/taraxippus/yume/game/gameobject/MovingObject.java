package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.level.*;
import com.taraxippus.yume.game.path.*;

public class MovingObject extends Box implements IMover
{
	public static final float JUMP_DURATION = 0.5F;
	public static final float JUMP_PAUSE = 0.15F;
	
	public final PathFinder pathFinder;
	public Path path;
	public Path.Step lastStep, nextStep;

	public MovingObject(World world)
	{
		super(world);

		this.translate(0, 0.5F - 0.75F * 0.5F, 0);
		this.scale(0.75F, 0.75F, 0.75F);

		this.specularity = 50F;
		
		this.pathFinder = new PathFinder(world.main.game.level, 1000, false);
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
			float jump = 0.5F * World.GRAVITY * delta * delta + -World.GRAVITY * 0.5F * delta;
			
			this.position.set(
				(nextStep.x + nextStep.gravity.x * (0.5F - scale.x * 0.5F)) * delta + (lastStep.x + lastStep.gravity.x * (0.5F - scale.x * 0.5F)) * (1 - delta) + jump * lastStep.oppositeGravity.x, 
				(nextStep.y + nextStep.gravity.y * (0.5F - scale.y * 0.5F)) * delta + (lastStep.y + lastStep.gravity.y * (0.5F - scale.y * 0.5F)) * (1 - delta) + jump * lastStep.oppositeGravity.y,
				(nextStep.z + nextStep.gravity.z * (0.5F - scale.z * 0.5F)) * delta + (lastStep.z + lastStep.gravity.z * (0.5F - scale.y * 0.5F)) * (1 - delta) + jump * lastStep.oppositeGravity.z);

			this.rotation.x = delta * 90F;
			
			this.updateMatrix();

			if (jumpTick <= 0)
			{
				this.position.set(nextStep.x + nextStep.gravity.x * (0.5F - scale.x * 0.5F), nextStep.y + nextStep.gravity.y * (0.5F - scale.y * 0.5F), nextStep.z + nextStep.gravity.z * (0.5F - scale.z * 0.5F));
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
				nextRotationY -= 360;
			
			else if (nextRotationY - lastRotationY < -180)
				nextRotationY += 360;
			
			if (Math.abs(nextRotationY - lastRotationY) == 180)
				lastRotationY = nextRotationY;
			
			if (nextStep.gravity == Level.yP)
				rotationPre.set(180, 0, 180);
			else if (nextStep.gravity == Level.yN)
				rotationPre.set(0, 0, 0);
				
			else if (nextStep.gravity == Level.zP)
				rotationPre.set(-90, 0, 0);
			else if (nextStep.gravity == Level.zN)
				rotationPre.set(90, 0, 0);
				
			else if (nextStep.gravity == Level.xP)
				rotationPre.set(0, 0, 90);
			else if (nextStep.gravity == Level.xN)
				rotationPre.set(0, 0, -90);
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
		final int dY = step.y - Math.round(position.y);
		final int dZ = step.z - Math.round(position.z);
		
		if (dY != 0)
		{
			if (step.gravity == Level.xN)
				return dY == -1 ? 90 : -90;
			else if (step.gravity == Level.xP)
				return dY == -1 ? -90 : 90;
				
			if (step.gravity == Level.zN)
				return dY == -1 ? 0 : 180;
			else if (step.gravity == Level.zP)
				return dY == -1 ? 180 : 0;
		}
			
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
	
	public void findPath(float tX, float tY, float tZ)
	{
		setPath(pathFinder.findPath(this, position.x, position.y, position.z, tX, tY, tZ, jumpTick == 0));
	}
	
	public void checkPath()
	{
		if (path != null && !path.finished)
			setPath(pathFinder.findPath(this, nextStep.x, nextStep.y, nextStep.z, path.getTarget().x, path.getTarget().y, path.getTarget().z, false));
	}
}
