package com.taraxippus.yume.game.chunk;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.gameobject.GameObject;
import com.taraxippus.yume.render.Renderer;
import com.taraxippus.yume.render.Shape;
import com.taraxippus.yume.util.VectorF;

public class Chunk extends GameObject
{
    public static int SIZE = 1;
    public static float RADIUS = (float) Math.sqrt(SIZE * SIZE * 3);

    public final float[] modelMatrix = new float[16];
    public final VectorF position = new VectorF();

    public Chunk(World world, VectorF position)
    {
        super(world);
        this.position.set(position);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, position.x * SIZE, position.y * 64, position.z * 64);
    }

    @Override
    public void render(Renderer renderer)
    {
        if (!world.main.camera.insideFrustum(position, RADIUS))
            return;

        if (renderer.currentPass != getPass())
            getPass().onRender(renderer);

        renderer.uniform(modelMatrix, getPass());
        GLES20.glUniform4f(getPass().getProgram().getUniform("u_Color"), 0.5F,  0.5F,  0.5F, 1);
        GLES20.glUniform2f(getPass().getProgram().getUniform("u_Specularity"), 20, 1);

        super.render(renderer);

        if (renderer.currentPass != getPass())
            getPass().getParent().onRender(renderer);
    }

    @Override
    public Shape createShape()
    {
        Shape shape = new Shape();

        final float[] heights = new float[(Chunk.SIZE + 3) * (Chunk.SIZE + 3)];

        int x, z;
        for (int i = 0; i < (Chunk.SIZE + 3) * (Chunk.SIZE + 3); i++)
        {
            x = i % (Chunk.SIZE + 3) - 1;
            z = i / (Chunk.SIZE + 3) - 1;

            heights[i] = world.main.game.noiseGenerator.getNoise(x, z) * 5;
        }

        final float[] heightMapVertexData = new float[(Chunk.SIZE + 1) * (Chunk.SIZE + 1) * 6];

        int offset = 0;

        float length, Ay, By, Cy, Dy;

        for (z = 0; z <= Chunk.SIZE; ++z)
        {
            for (x = 0; x <= Chunk.SIZE; ++x)
            {
                heightMapVertexData[offset++] = x;
                heightMapVertexData[offset++] = heights[(z + 1) * (Chunk.SIZE + 3) + x + 1];
                heightMapVertexData[offset++] = z;

                Ay = heights[(z + 1) * (Chunk.SIZE + 3) + x + 2];
                By = heights[(z + 2) * (Chunk.SIZE + 3) + x + 1];
                Cy = heights[(z + 1) * (Chunk.SIZE + 3) + x];
                Dy = heights[z * (Chunk.SIZE + 3) + x + 1];

                length = (float)Math.sqrt((Cy - Ay) * (Cy - Ay) + (Dy - By) * (Dy - By) + 4);

                heightMapVertexData[offset++] = (Cy - Ay) / length;
                heightMapVertexData[offset++] = (2) / length;
                heightMapVertexData[offset++] = (Dy - By) / length;
            }
        }

        final short[] heightMapIndexData = new short[2 * (Chunk.SIZE + 1) * Chunk.SIZE + 2 * (Chunk.SIZE - 1)];

        offset = 0;

        for (z = 0; z < Chunk.SIZE; ++z)
        {
            if (z > 0)
            {
                heightMapIndexData[offset++] = (short) (z * (Chunk.SIZE + 1));
            }

            for (x = 0; x <= Chunk.SIZE; ++x)
            {
                heightMapIndexData[offset++] = (short) ((z * (Chunk.SIZE + 1)) + x);
                heightMapIndexData[offset++] = (short) (((z + 1) * (Chunk.SIZE + 1)) + x);
            }

            if (z < Chunk.SIZE - 1)
            {
                heightMapIndexData[offset++] = (short) (((z + 1) * (Chunk.SIZE + 1)) + Chunk.SIZE);
            }
        }

        shape.init(GLES20.GL_TRIANGLE_STRIP, heightMapVertexData, heightMapIndexData, getPass().getAttributes());

        return shape;
    }

    @Override
    public float getDepth()
    {
        return super.getDepth() + VectorF.obtain().set(position).subtract(world.main.camera.eye).release().length();
    }
}
