package com.taraxippus.yume.render;
import android.opengl.*;
import com.taraxippus.yume.*;
import android.graphics.*;
import java.util.*;

public enum Pass
{
	SCENE_REFLECTION,
	GRID_REFLECTION,
	PARTICLE_REFLECTION,
	SCENE,
	GRID,
	PARTICLE,
	POST;
	
	public static final float REFLECTION_ALPHA_START = 0.9F;
	public static final float REFLECTION_ALPHA_FACTOR = 1 / 8F;
	
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
			
		attributes[SCENE_REFLECTION.ordinal()] = new int[] {3, 3};
		attributes[GRID_REFLECTION.ordinal()] = new int[] {3, 3};
		attributes[PARTICLE_REFLECTION.ordinal()] = new int[] {4, 4, 2};
		
		attributes[SCENE.ordinal()] = new int[] {3, 3};
		attributes[GRID.ordinal()] = new int[] {3, 3};
		attributes[PARTICLE.ordinal()] = new int[] {4, 4, 2};
		attributes[POST.ordinal()] = new int[] {2};
	}
	
	public static void init(Main main)
	{
		programs[SCENE_REFLECTION.ordinal()].init(main, R.raw.vertex_reflection, R.raw.fragment_reflection, "a_Position", "a_Normal");
		programs[GRID_REFLECTION.ordinal()].init(main, R.raw.vertex_reflection, R.raw.fragment_grid_reflection, "a_Position", "a_Normal");
		programs[PARTICLE_REFLECTION.ordinal()].init(main, R.raw.vertex_particle_reflection, R.raw.fragment_particle_reflection, "a_Position", "a_Color", "a_Direction");
		
		programs[SCENE.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene, "a_Position", "a_Normal");
		programs[GRID.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_grid, "a_Position", "a_Normal");
		programs[PARTICLE.ordinal()].init(main, R.raw.vertex_particle, R.raw.fragment_particle, "a_Position", "a_Color", "a_Direction");
		
		programs[POST.ordinal()].init(main, R.raw.vertex_post, R.raw.fragment_post, "a_Position");
		
		framebuffers[SCENE.ordinal()].init(true, main.renderer.width, main.renderer.height);
		
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
		return this == SCENE_REFLECTION || this == SCENE || this == POST;
	}
	
	public Pass getParent()
	{
		switch (this)
		{
			case GRID:
				return SCENE;
				
			case GRID_REFLECTION:
				return SCENE_REFLECTION;
				
			case PARTICLE:
				return SCENE;

			case PARTICLE_REFLECTION:
				return SCENE_REFLECTION;
				
				
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
			case SCENE_REFLECTION:
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaStart"), REFLECTION_ALPHA_START);
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaFactor"), REFLECTION_ALPHA_FACTOR);
				
				//GLES20.glUniform3f(getProgram().getUniform("u_Eye"), renderer.main.camera.eye.x, renderer.main.camera.eye.y, renderer.main.camera.eye.z);
				
				break;
				
			case GRID_REFLECTION:
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaStart"), REFLECTION_ALPHA_START);
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaFactor"), REFLECTION_ALPHA_FACTOR);

				//GLES20.glUniform3f(getProgram().getUniform("u_Eye"), renderer.main.camera.eye.x, renderer.main.camera.eye.y, renderer.main.camera.eye.z);

				break;
				
			case PARTICLE_REFLECTION:
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaStart"), REFLECTION_ALPHA_START);
				GLES20.glUniform1f(getProgram().getUniform("u_AlphaFactor"), REFLECTION_ALPHA_FACTOR);
				

				break;
				
			case SCENE:
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				GLES20.glDepthMask(true);
				GLES20.glCullFace(GLES20.GL_BACK);
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light"), 1, renderer.main.game.light.getVec40(), 0);

				break;

			case GRID:
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				GLES20.glDepthMask(true);
				GLES20.glCullFace(GLES20.GL_BACK);
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye.getVec40(), 0);
				GLES20.glUniform3fv(getProgram().getUniform("u_Light"), 1, renderer.main.game.light.getVec40(), 0);
				GLES20.glUniform4f(getProgram().getUniform("u_Center"), renderer.main.game.player.position.x, renderer.main.game.player.position.y, renderer.main.game.player.position.z, 7.5F);
				
				break;
				
			case PARTICLE:
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				
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
