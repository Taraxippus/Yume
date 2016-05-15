package com.taraxippus.yume.game.particle;

import android.opengl.*;

import com.taraxippus.yume.R;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;
import java.nio.*;
import java.util.*;

public class ParticleEmitter extends SceneObject
{
	public FloatBuffer vertices;
	public final Particle[] particles;
	
    public final VectorF position = new VectorF();

    public final VectorF color_start_min = new VectorF(0.9, 0.9F, 0.9);
    public final VectorF color_start_max = new VectorF(1, 1, 1);

    public final VectorF color_end_min = new VectorF(0.75F, 0.75F, 0.75F);
    public final VectorF color_end_max = new VectorF(1F, 1, 1);

    private final Random random = new Random();

	public boolean alive = true;
    public boolean respawn = true;
    public int textureResource = R.drawable.snow_particle;
    public int particleCount;

    public float minLifetime = 0.5F, maxLifetime = 3;
    public float minVelocity = 0.5F, maxVelocity = 1.5F;
    public float minRotVelocity = 0F, maxRotVelocity = 0F;
    public float minRangeX = -180 / 8F, maxRangeX = 180 / 8F;
    public float minMinSize = 0, maxMinSize = 0.05F, minMaxSize = 0.075F, maxMaxSize = 0.09F;
    public float minOffsetX, maxOffsetX, minOffsetY, maxOffsetY, minOffsetZ, maxOffsetZ;
    public float minMinRadius = 0, maxMinRadius = 0, minMaxRadius = 0, maxMaxRadius = 0;

    public float acceleration = 0.999F;

    private static final Texture defaultTexture = new Texture();
    private Texture texture = new Texture();

	public ParticleEmitter(World world, int count)
	{
		super(world);
		
		particles = new Particle[count];
		for (int i = 0; i < count; ++i)
			particles[i] = new Particle(this);
			
		particleCount = count;
		
		this.alpha = 1F;
	}
	
	public ParticleEmitter setLifeTime(float min, float max)
    {
        this.minLifetime = min;
        this.maxLifetime = max;

        return this;
    }

    public ParticleEmitter setVelocity(float min, float max, float rotMin, float rotMax)
    {
        this.minVelocity = min;
        this.maxVelocity = max;

        this.minRotVelocity = rotMin;
        this.maxRotVelocity = rotMax;

        return this;
    }

    public ParticleEmitter setRange(float minX, float maxX)
    {
        this.minRangeX = minX;
        this.maxRangeX = maxX;

        return this;
    }

    public ParticleEmitter setRadius(float minMinRadius, float maxMinRadius, float minMaxRadius, float maxMaxRadius)
    {
        this.minMinRadius = minMinRadius;
        this.maxMinRadius = maxMinRadius;

        this.minMaxRadius = minMaxRadius;
        this.maxMaxRadius = maxMaxRadius;

        return this;
    }

    public ParticleEmitter setSize(float min1, float max1, float min2, float max2)
    {
        this.minMinSize = min1;
        this.maxMinSize = max1;

        this.minMaxSize = min2;
        this.maxMaxSize = max2;

        return this;
    }

    public ParticleEmitter setAcceleration(float acceleration)
    {
        this.acceleration = acceleration;

        return this;
    }

    public ParticleEmitter setRespawn(boolean respawn)
    {
        this.respawn = respawn;

        return this;
    }

    public ParticleEmitter setOffset(float minX, float maxX, float minY, float maxY, float minZ, float maxZ)
    {
        this.minOffsetX = minX;
        this.maxOffsetX = maxX;
        this.minOffsetY = minY;
        this.maxOffsetY = maxY;
        this.minOffsetZ = minZ;
        this.maxOffsetZ = maxZ;

        return this;
    }

	@Override
	public void init()
	{
		for (Particle p : particles)
			spawn(p);
		
		if (!respawn)
			alive = false;

        if (!defaultTexture.initialized())
            defaultTexture.init(world.main.resourceHelper.getBitmap(R.drawable.snow_particle), GLES20.GL_LINEAR_MIPMAP_LINEAR, GLES20.GL_LINEAR, GLES20.GL_REPEAT);

        if (textureResource == R.drawable.snow_particle)
            texture = defaultTexture;
        else
            texture.init(world.main.resourceHelper.getBitmap(textureResource), GLES20.GL_LINEAR_MIPMAP_LINEAR, GLES20.GL_LINEAR, GLES20.GL_REPEAT);

		super.init();
	}
	
	public void spawn(Particle particle)
    {
        if (alive)
        {
            particle.rotation = 360 * random.nextFloat();
            particle.minRadius = this.minMinRadius + (this.maxMinRadius - this.minMinRadius) * random.nextFloat();
            particle.maxRadius = this.minMaxRadius + (this.maxMaxRadius - this.minMaxRadius) * random.nextFloat();
            particle.rotPosition.set(particle.prevRotPosition.set(0, 0, particle.minRadius).rotateY(particle.rotation));
            particle.position.set(particle.prevPosition.set(this.position).add(minOffsetX + random.nextFloat() * (maxOffsetX - minOffsetX), minOffsetY + random.nextFloat() * (maxOffsetY - minOffsetY), minOffsetZ + random.nextFloat() * (maxOffsetZ - minOffsetZ)));
            particle.velocity.set(0, minVelocity + random.nextFloat() * (maxVelocity - minVelocity), 0).rotateX(minRangeX + random.nextFloat() * (maxRangeX - minRangeX)).rotateY(random.nextFloat() * 360);
			particle.maxLifeTime = minLifetime + random.nextFloat() * (maxLifetime - minLifetime);
            particle.rotVelocity = minRotVelocity + (maxRotVelocity - minRotVelocity) * random.nextFloat();
            particle.lifeTime = particle.maxLifeTime;
            particle.minSize = minMinSize + random.nextFloat() * (maxMinSize - minMinSize);
            particle.maxSize = minMaxSize + random.nextFloat() * (maxMaxSize - minMaxSize);
            particle.acceleration = acceleration;

            particle.color_start.set(color_start_max).subtract(color_start_min).multiplyBy(random.nextFloat()).add(color_start_min);
            particle.color_end.set(color_end_max).subtract(color_end_min).multiplyBy(random.nextFloat()).add(color_end_min);
        }
        else
        {
            particle.position.set(Float.NaN, Float.NaN, Float.NaN);
            particle.rotPosition.set(Float.NaN, Float.NaN, Float.NaN);

            particle.alive = false;
            this.particleCount--;

            if (this.particleCount == 0)
                world.removeLater(this);
        }
    }
	
	@Override
	public Shape createShape()
	{
		vertices = FloatBuffer.allocate(particles.length * 10 * 4);
		buffer(1);
		
		ShortBuffer indices = ShortBuffer.allocate(particles.length * 12);
		for (int i = 0; i < particles.length; ++i)
		{
			indices.put((short) (i * 4));
			indices.put((short) (i * 4 + 1));
			indices.put((short) (i * 4 + 2));
			
			indices.put((short) (i * 4 + 1));
			indices.put((short) (i * 4 + 3));
			indices.put((short) (i * 4 + 2));
			
			
			indices.put((short) (i * 4));
			indices.put((short) (i * 4 + 2));
			indices.put((short) (i * 4 + 1));

			indices.put((short) (i * 4 + 1));
			indices.put((short) (i * 4 + 2));
			indices.put((short) (i * 4 + 3));
		}
		
		Shape shape = new Shape();
		shape.init(GLES20.GL_TRIANGLES, vertices, indices, true, getPass().getAttributes());
		return shape;
	}

	@Override
	public void update()
	{
		for (Particle p : particles)
			p.update();
		
		super.update();
	}
	
	@Override
	public void render(Renderer renderer)
	{
		if (shape != null)
		{
			buffer(renderer.partial);
			shape.buffer(vertices, null);
		}

        texture.bind(0);

		super.render(renderer);
	}

	@Override
	public Pass getPass()
	{
		return Pass.PARTICLE;
	}
	
	public void buffer(float partial)
	{
		vertices.position(0);
		
		for (Particle p : particles)
			p.buffer(vertices, partial);
	}

	@Override
	public void delete()
	{
		super.delete();
		
		vertices.limit(0);

        if (texture != defaultTexture)
            texture.delete();
	}
}
