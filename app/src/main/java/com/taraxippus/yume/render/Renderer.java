package com.taraxippus.yume.render;


import android.opengl.*;
import android.os.*;
import android.util.*;
import com.taraxippus.yume.*;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;

import javax.microedition.khronos.egl.EGLConfig;

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
		
		GLES20.glClearColor(186.0F / 255.0F, 221.0F / 255.0F, 1.0F, 1);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDepthMask(true);

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

	public long lastTime;
	private float accumulator;
	private float accumulatorReal;
	
	private long lastFPSUpdate;
	private int frames;
	
	public float partial;
	public Pass currentPass;
	
	@Override
	public void onDrawFrame(GL10 p1)
	{
		if (lastTime == 0)
			lastTime = SystemClock.elapsedRealtime();
			
		float delta = (SystemClock.elapsedRealtime() - lastTime) / 1000F;
		lastTime = SystemClock.elapsedRealtime();
		
		accumulatorReal += delta;
		while (accumulatorReal >= Main.FIXED_DELTA)
		{
			main.game.updateReal();
			accumulatorReal -= Main.FIXED_DELTA;
		}
		
		delta *= main.timeFactor;
		
		accumulator += delta;
		while (accumulator >= Main.FIXED_DELTA)
		{
			main.game.update();
			accumulator -= Main.FIXED_DELTA;
		}
		
		partial = accumulator / Main.FIXED_DELTA;
		
		currentPass = null;
        try
        {
            Pass.onRenderFrame(this);
            for (Pass pass : Pass.values())
            {
                if (!pass.inOrder())
                    continue;

                pass.onRender(this);
                currentPass = pass;
                main.world.render(this, pass);
            }
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }

		frames++;
		
		if (SystemClock.elapsedRealtime() - lastFPSUpdate >= 1000)
		{
			main.runOnUiThread(new Runnable()
			{
					@Override
					public void run()
					{
						main.textView.setText("" + frames);
						frames = 0;
					}
			});
	
			lastFPSUpdate = SystemClock.elapsedRealtime();
		}
	}
	
	final float[] mvpMatrix = new float[16];
	final float[] normalMatrix = new float[16];
	
	public void uniform(float[] modelMatrix, Pass pass)
	{
		switch (pass)
		{
			case HEXAGON_OUTLINE:
			case SCENE_OUTLINE:
				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_VP"), 1, false, main.camera.projectionViewMatrix, 0);

				Matrix.invertM(normalMatrix, 0, modelMatrix, 0);
				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_N"), 1, true, normalMatrix, 0);

				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_M"), 1, false, modelMatrix, 0);
				break;
				
			case PARTICLE:
				Matrix.multiplyMM(mvpMatrix, 0, main.camera.viewMatrix, 0, modelMatrix, 0);
				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_MV"), 1, false, mvpMatrix, 0);

				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_P"), 1, false, main.camera.projectionMatrix, 0);
				break;

			default:
				Matrix.multiplyMM(mvpMatrix, 0, main.camera.projectionViewMatrix, 0, modelMatrix, 0);
				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_MVP"), 1, false, mvpMatrix, 0);

				Matrix.invertM(normalMatrix, 0, modelMatrix, 0);
				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_N"), 1, true, normalMatrix, 0);

				GLES20.glUniformMatrix4fv(pass.getProgram().getUniform("u_M"), 1, false, modelMatrix, 0);
				break;
		}
	}
	
	public void delete()
	{
		Pass.delete();
		main.game.delete();
	}
}
