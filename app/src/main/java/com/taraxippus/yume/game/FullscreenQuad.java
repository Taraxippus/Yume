package com.taraxippus.yume.game;
import com.taraxippus.yume.render.*;
import android.opengl.*;
import java.nio.*;

public class FullscreenQuad extends GameObject
{
	public FullscreenQuad(World world, Pass pass)
	{
		super(world);
		
		this.setPass(pass);
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
