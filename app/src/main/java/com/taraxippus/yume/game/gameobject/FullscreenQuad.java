package com.taraxippus.yume.game.gameobject;

import android.opengl.GLES20;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Shape;
import java.util.Arrays;

public class FullscreenQuad extends GameObject
{
	Pass[] passes;
	
	public FullscreenQuad(World world, Pass... passes)
	{
		super(world);
		
		this.passes = passes;
		this.setPass(passes[0]);
	}

	@Override
	public boolean renderPass(Pass pass)
	{
		for (Pass p : passes)
			if (p == pass)
				return true;
			
		return false;
	}
	
	public static final float[] vertices = new float[] 
	{
		-1, -1,
		1, -1,
		-1, 1,
		1, 1
	};
	
	@Override
	public Shape createShape()
	{
		Shape shape = new Shape();
		shape.init(GLES20.GL_TRIANGLE_STRIP, vertices, 4, getPass().getAttributes());
		return shape;
	}
	
}
