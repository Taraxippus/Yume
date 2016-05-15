package com.taraxippus.yume.game.gameobject;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.taraxippus.yume.R;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Renderer;
import com.taraxippus.yume.render.Shape;
import com.taraxippus.yume.render.Texture;

public class SnowPlane extends GameObject
{
    private static final Texture texture = new Texture();

    private final int planes;
    private final float speed, offset;
    public final float[] modelMatrix = new float[16];

    public SnowPlane(World world, int planes)
    {
        super(world);

        this.planes = planes;
        this.speed = (world.random.nextFloat() + 1) / 10F;
        this.offset = world.random.nextFloat();
    }

    public Pass getPass()
    {
        return Pass.SNOW;
    }

    @Override
    public void init()
    {
        super.init();

        if (!texture.initialized())
            texture.init(world.main.resourceHelper.getBitmap(R.drawable.snow_plane), GLES20.GL_LINEAR, GLES20.GL_LINEAR, GLES20.GL_REPEAT);

    }

    @Override
    public void render(Renderer renderer)
    {
        if (renderer.currentPass != getPass())
            getPass().onRender(renderer);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, world.main.camera.position.x, world.main.camera.position.y, world.main.camera.position.z);

        renderer.uniform(modelMatrix, getPass());
        texture.bind(0);

        GLES20.glUniform1f(getPass().getProgram().getUniform("u_Time"), (world.time * speed + offset) % 2);

        super.render(renderer);

        if (renderer.currentPass != getPass())
            getPass().getParent().onRender(renderer);
    }

    @Override
    public Shape createShape()
    {
        Shape shape = new Shape();

        float[] vertices = new float[planes * 4 * 5];

        int offset = 0;
        float radius = (float) (planes / (2 * Math.PI)) * 4;
        float x, z;
        for (int i = 0; i < planes; ++i)
        {
            x = (float) Math.cos(i / (float) planes * Math.PI * 2) * radius;
            z = (float) Math.sin(i / (float) planes * Math.PI * 2) * radius;

            vertices[offset++] = x;
            vertices[offset++] = 0;
            vertices[offset++] = z;
            vertices[offset++] = 0;
            vertices[offset++] = 0;

            vertices[offset++] = x;
            vertices[offset++] = 16;
            vertices[offset++] = z;
            vertices[offset++] = 0;
            vertices[offset++] = 1;

            x = (float) Math.cos((i + 1) / (float) planes * Math.PI * 2) * radius;
            z = (float) Math.sin((i + 1) / (float) planes * Math.PI * 2) * radius;

            vertices[offset++] = x;
            vertices[offset++] = 0;
            vertices[offset++] = z;
            vertices[offset++] = 1;
            vertices[offset++] = 0;

            vertices[offset++] = x;
            vertices[offset++] = 16;
            vertices[offset++] = z;
            vertices[offset++] = 1;
            vertices[offset++] = 1;
        }

        offset = 0;
        short[] indices = new short[12 * planes];

        for (int i = 0; i < planes; ++i)
        {
            indices[offset++] = (short) (i * 4);
            indices[offset++] = (short) (i * 4 + 2);
            indices[offset++] = (short) (i * 4 + 1);

            indices[offset++] = (short) (i * 4 + 1);
            indices[offset++] = (short) (i * 4 + 2);
            indices[offset++] = (short) (i * 4 + 3);
        }

        shape.init(GLES20.GL_TRIANGLES, vertices, indices, getPass().getAttributes());

        return shape;
    }

}
