package com.taraxippus.yume.render;
import android.opengl.*;
import com.taraxippus.yume.*;
import android.graphics.*;
import java.util.*;

public enum Pass
{
	SCENE,
	PARTICLE,
	REFLECTION,
	GRID,
	PARTICLE_REFLECTION,
	POST;
	
	public static final float REFLECTION_ALPHA_START = 0.95F;
	public static final float REFLECTION_ALPHA_FACTOR = 1 / 16F;
	
	private static final Program[] programs = new Program[Pass.values().length];
	private static final Framebuffer[] framebuffers = new Framebuffer[Pass.values().length];
	
	private static final int[][] attributes = new int[Pass.values().length][];
	
	public static final Texture dither = new Texture();
	
	static
	{
		for (int i = 0; i < programs.length; ++i)
			programs[i] = new Program();
			
		for (int i = 0; i < framebuffers.length; ++i)
			framebuffers[i] = new Framebuffer();
			
		attributes[SCENE.ordinal()] = new int[] {3, 3};
		attributes[PARTICLE.ordinal()] = new int[] {4, 4, 2};
		attributes[REFLECTION.ordinal()] = new int[] {3, 3};
		attributes[GRID.ordinal()] = new int[] {3, 3};
		attributes[PARTICLE_REFLECTION.ordinal()] = new int[] {4, 4, 2};
		attributes[POST.ordinal()] = new int[] {2};
	}
	
	public static void init(Main main)
	{
		programs[SCENE.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene, "a_Position", "a_Normal");
		framebuffers[SCENE.ordinal()].init(true, main.renderer.width, main.renderer.height);
		programs[PARTICLE.ordinal()].init(main, R.raw.vertex_particle, R.raw.fragment_particle, "a_Position", "a_Color", "a_Direction");
		
		programs[REFLECTION.ordinal()].init(main, R.raw.vertex_reflection, R.raw.fragment_reflection, "a_Position", "a_Normal");
		programs[GRID.ordinal()].init(main, R.raw.vertex_reflection, R.raw.fragment_grid, "a_Position", "a_Normal");
		programs[PARTICLE_REFLECTION.ordinal()].init(main, R.raw.vertex_particle_reflection, R.raw.fragment_particle_reflection, "a_Position", "a_Color", "a_Direction");
		
		programs[POST.ordinal()].init(main, R.raw.vertex_post, R.raw.fragment_post, "a_Position");
		
		POST.getProgram().use();
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Texture"), 0);
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Dither"), 1);
		
		final int[] colors = new int[main.renderer.width * main.renderer.height];
		final int gray;
		final Random random = new Random();
		
		for (int i = 0; i < colors.length; ++i)
		{
			gray = random.nextInt(256);
			colors[i] = Color.rgb(gray, gray, gray);
		}
			
		dither.init(Bitmap.createBitmap(main.getResources().getDisplayMetrics(), colors, main.renderer.width, main.renderer.height, Bitmap.Config.RGB_565), GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
	}
	
	public static void delete()
	{
		for (Program program : programs)
			if (program.initialized())
				program.delete();
				
		for (Framebuffer framebuffer : framebuffers)
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
		return framebuffers[this.ordinal()];
	}
	
	public boolean inOrder()
	{
		return this != Pass.GRID && this != Pass.PARTICLE && this != Pass.PARTICLE_REFLECTION;
	}
	
	public Pass getParent()
	{
		switch (this)
		{
			case PARTICLE:
				return SCENE;
				
			case GRID:
				return REFLECTION;
				
			case PARTICLE_REFLECTION:
				return REFLECTION;
			
			default:
				return this;
		}
	}
	
	public void onRender(Renderer renderer)
	{
		this.getProgram().use();
		
		switch (this)
		{
			case SCENE:
				if (renderer.currentPass != Pass.SCENE)
					this.getFramebuffer().bind();
					
				GLES20.glDepthMask(true);
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light"), 1, renderer.main.game.light.getVec40(), 0);
				
				break;
				
			case PARTICLE:
				
				//GLES20.glDepthMask(false);
				
				break;
		
			case REFLECTION:
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaStart"), REFLECTION_ALPHA_START);
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaFactor"), REFLECTION_ALPHA_FACTOR);
				
				//GLES20.glUniform3f(getProgram().getUniform("u_Eye"), renderer.main.camera.eye.x, renderer.main.camera.eye.y, renderer.main.camera.eye.z);
				
				break;
				
			case GRID:
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaStart"), REFLECTION_ALPHA_START);
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaFactor"), REFLECTION_ALPHA_FACTOR);

				//GLES20.glUniform3f(getProgram().getUniform("u_Eye"), renderer.main.camera.eye.x, renderer.main.camera.eye.y, renderer.main.camera.eye.z);

				break;
				
			case POST:
				GLES20.glDepthMask(true);
				GLES20.glCullFace(GLES20.GL_BACK);
				Framebuffer.release(renderer);
				
				SCENE.getFramebuffer().bindTexture(0);
				dither.bind(1);
				
				GLES20.glUniform2f(getProgram().getUniform("u_InvResolution"), 1F / SCENE.getFramebuffer().width, 1F / SCENE.getFramebuffer().height);
				GLES20.glUniform1f(getProgram().getUniform("u_VignetteFactor"), 0.6F + 0.2F / renderer.main.timeFactor);
				
				break;
		}
	}
}
