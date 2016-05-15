package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.particle.ParticleEmitter;
import com.taraxippus.yume.util.Position;
import com.taraxippus.yume.util.VectorF;

public class MovingObject extends Box
{
	public static final float JUMP_DURATION = 0.5F;
	public static final float JUMP_PAUSE = 0.15F;

	public float jumpLength = 1F;

	public MovingObject(World world)
	{
		super(world);

		this.translate(0, 0, 0);

		this.specularityExponent = 50F;
		this.specularityFactor = 0.25F;
	}

	float jumpTick;
	float lastRotationY;
	float nextRotationY;

	Position lastStep, nextStep;

	@Override
	public void update()
	{
		super.update();

		if (jumpTick > 0)
		{
			jumpTick -= Main.FIXED_DELTA;

			float delta = (getJumpDuration() - jumpTick) / getJumpDuration();
			float jump = 0.5F * World.GRAVITY * delta * delta + -World.GRAVITY * 0.5F * delta;
			
			this.position.set(
				nextStep.position.x * delta + lastStep.position.x * (1 - delta) + jump * -(nextStep.gravity.x * delta + lastStep.gravity.x * (1 - delta)),
				nextStep.position.y * delta + lastStep.position.y * (1 - delta) + jump * -(nextStep.gravity.y * delta + lastStep.gravity.y * (1 - delta)),
				nextStep.position.z * delta + lastStep.position.z * (1 - delta) + jump * -(nextStep.gravity.z * delta + lastStep.gravity.z * (1 - delta)));

			this.rotation.x = delta * 90F;
			
			this.updateMatrix();

			if (jumpTick <= 0)
			{
				this.position.set(nextStep.position.x + nextStep.gravity.x * (0.5F - scale.x * 0.5F), nextStep.position.y + nextStep.gravity.y * (0.5F - scale.y * 0.5F), nextStep.position.z + nextStep.gravity.z * (0.5F - scale.z * 0.5F));
				this.rotation.x = 0;
				this.updateMatrix();

				jumpTick = 0;
				onJumpFinished();
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

	public void move(float direction)
	{
		if (jumpTick != 0)
			return;

		lastRotationY = rotation.y;
		nextRotationY = direction;

		VectorF tmp = VectorF.obtain();

		lastStep = new Position(world, this.position);
		nextStep = new Position(world, tmp.set(0, 0, jumpLength).rotateY(direction).add(position));

		VectorF.release(tmp);

		if (nextRotationY - lastRotationY > 180)
			nextRotationY -= 360;

		else if (nextRotationY - lastRotationY < -180)
			nextRotationY += 360;

		jumpTick = -getJumpPause();
	}

	public void onJumpFinished()
	{
		ParticleEmitter pe = (ParticleEmitter) new ParticleEmitter(world, 20)
				.setRespawn(false)
				.setRange(80, 90)
				.setVelocity(2.5F, 3.5F, 0, 0)
				.setAcceleration(0.99F)
				.translate(position.x + scale.x * 0.5F * nextStep.gravity.x, position.y + scale.y * 0.5F * nextStep.gravity.y, position.z + scale.z * 0.5F * nextStep.gravity.z)
				.rotate(rotation.x, rotation.y, rotation.z);

		world.addLater(pe);
	}

	public float getJumpDuration()
	{
		return JUMP_DURATION;
	}
	
	public float getJumpPause()
	{
		return JUMP_PAUSE;
	}
}
