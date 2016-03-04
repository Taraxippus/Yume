package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.path.*;
import com.taraxippus.yume.util.*;
import com.taraxippus.yume.game.particle.*;

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

		this.translate(0, -(0.5F - 0.75F * 0.5F), 0);
		this.scale(0.75F, 0.75F, 0.75F);

		this.specularityExponent = 50F;
		this.specularityFactor = 0.25F;
		
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
				(nextStep.x + nextStep.gravity.x * (0.5F - scale.x * 0.5F)) * delta + (lastStep.x + lastStep.gravity.x * (0.5F - scale.x * 0.5F)) * (1 - delta) + jump * -nextStep.gravity.x, 
				(nextStep.y + nextStep.gravity.y * (0.5F - scale.y * 0.5F)) * delta + (lastStep.y + lastStep.gravity.y * (0.5F - scale.y * 0.5F)) * (1 - delta) + jump * -nextStep.gravity.y,
				(nextStep.z + nextStep.gravity.z * (0.5F - scale.z * 0.5F)) * delta + (lastStep.z + lastStep.gravity.z * (0.5F - scale.y * 0.5F)) * (1 - delta) + jump * -nextStep.gravity.z);

			this.rotation.x = delta * 90F;
			
			this.updateMatrix();

			if (jumpTick <= 0)
			{
				this.position.set(nextStep.x + nextStep.gravity.x * (0.5F - scale.x * 0.5F), nextStep.y + nextStep.gravity.y * (0.5F - scale.y * 0.5F), nextStep.z + nextStep.gravity.z * (0.5F - scale.z * 0.5F));
				this.rotation.x = 0;
				this.updateMatrix();
				
				ParticleEmitter pe = (ParticleEmitter) new ParticleEmitter(world, 20)
				.setRespawn(false)
				.setRange(80, 90)
					.translate(position.x + scale.x * 0.5F * nextStep.gravity.x, position.y + scale.y * 0.5F * nextStep.gravity.y, position.z + scale.z * 0.5F * nextStep.gravity.z)
				.rotatePre(rotationPre.x, rotationPre.y, rotationPre.z)
				.rotate(rotation.x, rotation.y, rotation.z);
				
				world.addLater(pe);
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
			
			if (lastStep.gravity != nextStep.gravity || Math.abs(nextRotationY - lastRotationY) == 180)
				rotation.y = lastRotationY = nextRotationY;
			
			VectorF lastRotationPre = rotationPre.copy();
				
			if (nextStep.gravity.y == 1)
				rotationPre.set(180, 0, 180);
			else if (nextStep.gravity.y == -1)
				rotationPre.set(0, 0, 0);
				
			else if (nextStep.gravity.z == 1)
				rotationPre.set(-90, 0, 0);
			else if (nextStep.gravity.z == -1)
				rotationPre.set(90, 0, 0);
				
			else if (nextStep.gravity.x == 1)
				rotationPre.set(0, 0, 90);
			else if (nextStep.gravity.x == -1)
				rotationPre.set(0, 0, -90);
				
			if (rotationPre.x - lastRotationPre.x > 180)
				rotationPre.x -= 360;
			else if (rotationPre.x - lastRotationPre.x < -180)
				rotationPre.x += 360;
			
			if (rotationPre.y - lastRotationPre.y > 180)
				rotationPre.y -= 360;
			else if (rotationPre.y - lastRotationPre.y < -180)
				rotationPre.y += 360;
				
			if (rotationPre.z - lastRotationPre.z > 180)
				rotationPre.z -= 360;
			else if (rotationPre.z - lastRotationPre.z < -180)
				rotationPre.z += 360;
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
		
		if (dY != 0 && step.gravity.y == 0)
		{
			if (step.gravity.x == -1)
				return dY == -1 ? 90 : -90;
			else if (step.gravity.x == 1)
				return dY == -1 ? -90 : 90;
				
			else if (step.gravity.z == -1)
				return dY == -1 ? 0 : 180;
			else if (step.gravity.z == 1)
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
