package com.taraxippus.yume.render;


import android.opengl.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;

import javax.microedition.khronos.egl.EGLConfig;
import com.taraxippus.yume.*;

public class Renderer implements GLSurfaceView.Renderer
{
	final Main main;
	
	public int width, height;
	
	public Renderer(Main main)
	{
		this.main = main;
	}
	
	@Override
	public void onSurfaceCreated(GL10 p1, EGLConfig p2)
	{
		width = main.view.getWidth();
		height = main.view.getHeight();
		
		GLES20.glClearColor(1F, 1F, 1F, 1);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		Pass.init(main);
		main.game.init();
	}

	@Override
	public void onSurfaceChanged(GL10 p1, int width, int height)
	{
		this.width = width;
		this.height = height;
		
		GLES20.glViewport(0, 0, width, height);
		
		main.camera.onResize(width, height);
	}

	private long lastTime;
	private float accumulator;
	
	public float partial;
	
	@Override
	public void onDrawFrame(GL10 p1)
	{
		if (lastTime == 0)
			lastTime = System.currentTimeMillis();
			
		float delta = (System.currentTimeMillis() - lastTime) / 1000F;
		lastTime = System.currentTimeMillis();
		
		delta *= main.timeFactor;
		
		accumulator += delta;
		while (accumulator >= Main.FIXED_DELTA)
		{
			main.world.update();
			accumulator -= Main.FIXED_DELTA;
		}
		
		partial = accumulator / Main.FIXED_DELTA;
		
		for (Pass pass : Pass.values())
		{
			pass.onRender(this);
			main.world.render(this, pass);
		}
	}
	
	final float[] mvpMatrix = new float[16];
	final float[] normalMatrix = new float[16];
	
	public void uniform(float[] modelMatrix, Pass pass)
	{
		Matrix.multiplyMM(mvpMatrix, 0, main.camera.viewProjectionMatrix, 0, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_MVP"), 1, false, mvpMatrix, 0);
		
		Matrix.invertM(normalMatrix, 0, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_N"), 1, true, normalMatrix, 0);
		
		GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_M"), 1, false, modelMatrix, 0);
	}
	
	public void delete()
	{
		Pass.delete();
		main.game.delete();
	}
}
