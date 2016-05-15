package com.taraxippus.yume.render;
import android.opengl.*;
import com.taraxippus.yume.*;
import android.graphics.*;
import java.util.*;

public enum Pass
{
	SCENE,
	PARTICLE,
	SNOW,
	POST;

	private static final Program[] programs = new Program[Pass.values().length];
	private static final Framebuffer[] framebuffer = new Framebuffer[Pass.values().length];
	
	private static final int[][] attributes = new int[Pass.values().length][];
	
	public static final Texture dither = new Texture();
	
	static
	{
		for (int i = 0; i < programs.length; ++i)
			programs[i] = new Program();
			
		for (int i = 0; i < framebuffer.length; ++i)
			framebuffer[i] = new Framebuffer();

		attributes[SCENE.ordinal()] = new int[] {3, 3};
		attributes[PARTICLE.ordinal()] = new int[] {4, 4, 2};
		attributes[SNOW.ordinal()] = new int[] {3, 2};
		attributes[POST.ordinal()] = new int[] {2};
	}
	
	public static void init(Main main)
	{
		programs[SCENE.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene, "a_Position", "a_Normal");
		programs[PARTICLE.ordinal()].init(main, R.raw.vertex_particle, R.raw.fragment_particle, "a_Position", "a_Color", "a_Direction");
		programs[SNOW.ordinal()].init(main, R.raw.vertex_snow, R.raw.fragment_snow, "a_Position", "a_UV");

		programs[POST.ordinal()].init(main, R.raw.vertex_post, R.raw.fragment_post, "a_Position");
		
		framebuffer[SCENE.ordinal()].init(true, main.renderer.width, main.renderer.height);

		PARTICLE.getProgram().use();
		GLES20.glUniform1i(PARTICLE.getProgram().getUniform("u_Texture"), 0);

		SNOW.getProgram().use();
		GLES20.glUniform1i(SNOW.getProgram().getUniform("u_Texture"), 0);

		POST.getProgram().use();
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Texture"), 0);
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Dither"), 1);
		
		final int[] colors = new int[main.renderer.width * main.renderer.height];
		int gray;
		final Random random = new Random();
		
		for (int i = 0; i < colors.length; ++i)
		{
			gray = random.nextInt(256);
			colors[i] = Color.rgb(gray, gray, gray);
		}
			
		dither.init(Bitmap.createBitmap(colors, 0, main.renderer.width, main.renderer.width, main.renderer.height, Bitmap.Config.RGB_565), GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
	}
	
	public static void delete()
	{
		for (Program program : programs)
			if (program.initialized())
				program.delete();
				
		for (Framebuffer framebuffer : Pass.framebuffer)
			if (framebuffer.initialized())
				framebuffer.delete();
				
		dither.delete();
	}
	
	public Program getProgram()
	{
		return programs[this.ordinal()];
	}
	
	public int[] getAttributes()
	{
		return attributes[this.ordinal()];
	}
	
	public Framebuffer getFramebuffer()
	{
		return framebuffer[this.ordinal()];
	}
	
	public boolean inOrder()
	{
		return this == SCENE || this == POST || this == SNOW;
	}
	
	public Pass getParent()
	{
		switch (this)
		{
			case PARTICLE:
				return SCENE;

			default:
				return this;
		}
	}
	
	public static void onRenderFrame(Renderer renderer)
	{
		SCENE.getFramebuffer().bind();
	}
	
	public void onRender(Renderer renderer)
	{
		this.getProgram().use();
		
		switch (this)
		{
			case SCENE:
				GLES20.glDepthMask(true);
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light"), 1, renderer.main.game.light.getVec40(), 0);

				break;

			case PARTICLE:
				GLES20.glDepthMask(false);

				break;

			case SNOW:
				GLES20.glDepthMask(false);

				break;

			case POST:
				GLES20.glDepthMask(true);
				Framebuffer.release(renderer);
				
				SCENE.getFramebuffer().bindTexture(0);
				dither.bind(1);
				
				GLES20.glUniform2f(getProgram().getUniform("u_InvResolution"), 1F / SCENE.getFramebuffer().width, 1F / SCENE.getFramebuffer().height);
				GLES20.glUniform1f(getProgram().getUniform("u_VignetteFactor"), 0.6F + 0.2F / renderer.main.timeFactor);
				
				break;
		}
	}
}
