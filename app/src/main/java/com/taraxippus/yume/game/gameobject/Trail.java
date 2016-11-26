package com.taraxippus.yume.game.gameobject;

import android.opengl.*;

import com.taraxippus.yume.R;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;
import java.nio.*;
import java.util.*;

public class Trail extends SceneObject
{
	public FloatBuffer vertices;
	public final Player player;
	
	public Trail(World world, Player player)
	{
		super(world);
		
		this.player = player;
		this.depthOffset = 1000;
		this.setPass(Pass.SCENE);
		this.renderAlways = true;
		this.setColor(0xFF8800);
		this.setAlpha(0.8F);
	}

	@Override
	public Shape createShape()
	{
		vertices = FloatBuffer.allocate(Player.TRAIL_SIZE * 4 * 3);
		buffer();

		ShortBuffer indices = ShortBuffer.allocate((Player.TRAIL_SIZE - 1) * 2 * 12);
		for (int i = 0; i < Player.TRAIL_SIZE - 1; ++i)
		{
			indices.put((short) (i * 4));
			indices.put((short) (i * 4 + 1));
			indices.put((short) (i * 4 + 4));

			indices.put((short) (i * 4 + 1));
			indices.put((short) (i * 4 + 5));
			indices.put((short) (i * 4 + 4));


			indices.put((short) (i * 4));
			indices.put((short) (i * 4 + 4));
			indices.put((short) (i * 4 + 1));

			indices.put((short) (i * 4 + 1));
			indices.put((short) (i * 4 + 4));
			indices.put((short) (i * 4 + 5));
			
			
			
			indices.put((short) (i * 4 + 2));
			indices.put((short) (i * 4 + 3));
			indices.put((short) (i * 4 + 6));

			indices.put((short) (i * 4 + 3));
			indices.put((short) (i * 4 + 7));
			indices.put((short) (i * 4 + 6));


			indices.put((short) (i * 4 + 2));
			indices.put((short) (i * 4 + 6));
			indices.put((short) (i * 4 + 3));

			indices.put((short) (i * 4 + 3));
			indices.put((short) (i * 4 + 6));
			indices.put((short) (i * 4 + 7));
		}

		Shape shape = new Shape();
		shape.init(GLES20.GL_TRIANGLES, vertices, indices, true, getPass().getAttributes());
		return shape;
	}

	@Override
	public void render(Renderer renderer)
	{
		if (shape != null)
		{
			buffer();
			shape.buffer(vertices, null);
		}

		super.render(renderer);
	}
	
	public void buffer()
	{
		vertices.position(0);
		
		if (player.lastPosition[0] == null)
		{
			for (int i = 0; i < vertices.capacity(); ++i)
				vertices.put(0);
			
			return;
		}
		
		VectorF tmp = VectorF.obtain();
		for (int i = 0; i < Player.TRAIL_SIZE * 2 - 1; i += 2)
		{
			player.lastPosition[i].put(vertices);
			tmp.set(player.lastPosition[i]).subtract(player.lastPosition[i + 1]).normalize().multiplyBy(i * 0.05F / Player.TRAIL_SIZE).add(player.lastPosition[i]).put(vertices);
			
			tmp.set(player.lastPosition[i + 1]).subtract(player.lastPosition[i]).normalize().multiplyBy(i * 0.05F / Player.TRAIL_SIZE).add(player.lastPosition[i + 1]).put(vertices);
			player.lastPosition[i + 1].put(vertices);
		}
		
		tmp.release();
	}

	@Override
	public void delete()
	{
		super.delete();

		vertices.limit(0);
	}
}
