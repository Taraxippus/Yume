package com.taraxippus.yume.render;
import android.opengl.*;
import com.taraxippus.yume.*;
import android.graphics.*;
import java.util.*;

public enum Pass
{
	HEXAGON_OUTLINE,
	HEXAGON,
	SCENE,
	SCENE_OUTLINE,
	PARTICLE,
	BLOOM1,
	BLOOM2,
	BLOOM3,
	BLOOM4,
	BLOOM5,
	BLOOM6,
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

		attributes[HEXAGON_OUTLINE.ordinal()] = new int[] {3, 3, 2};
		attributes[HEXAGON.ordinal()] = new int[] {3, 2};
		attributes[SCENE_OUTLINE.ordinal()] = new int[] {3, 3};
		attributes[SCENE.ordinal()] = new int[] {3};
		attributes[PARTICLE.ordinal()] = new int[] {4, 4, 2};
		attributes[BLOOM1.ordinal()] = new int[] {2};
		attributes[BLOOM2.ordinal()] = BLOOM1.getAttributes();
		attributes[BLOOM3.ordinal()] = BLOOM1.getAttributes();
		attributes[BLOOM4.ordinal()] = BLOOM1.getAttributes();
		attributes[BLOOM5.ordinal()] = BLOOM1.getAttributes();
		attributes[BLOOM6.ordinal()] = BLOOM1.getAttributes();
		attributes[POST.ordinal()] = BLOOM1.getAttributes();
	}
	
	public static void init(Main main)
	{
		programs[HEXAGON_OUTLINE.ordinal()].init(main, R.raw.vertex_hexagon_outline, R.raw.fragment_scene, "a_Position", "a_Normal", "a_Direction");
		programs[HEXAGON.ordinal()].init(main, R.raw.vertex_hexagon, R.raw.fragment_hexagon, "a_Position", "a_Direction");
		programs[SCENE_OUTLINE.ordinal()].init(main, R.raw.vertex_scene_outline, R.raw.fragment_scene, "a_Position", "a_Normal");
		programs[SCENE.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene, "a_Position");
		programs[PARTICLE.ordinal()].init(main, R.raw.vertex_particle, R.raw.fragment_particle, "a_Position", "a_Color", "a_Direction");
		
		programs[BLOOM1.ordinal()].init(main, R.raw.vertex_bloom, R.raw.fragment_bloom1, "a_Position");
		programs[BLOOM2.ordinal()].init(main, R.raw.vertex_bloom, R.raw.fragment_bloom2, "a_Position");
		programs[BLOOM3.ordinal()] = BLOOM2.getProgram();
		programs[BLOOM4.ordinal()] = BLOOM2.getProgram();
		programs[BLOOM5.ordinal()] = BLOOM1.getProgram();
		programs[BLOOM6.ordinal()] = BLOOM2.getProgram();
		
		programs[POST.ordinal()].init(main, R.raw.vertex_post, R.raw.fragment_post, "a_Position");
		
		framebuffer[SCENE.ordinal()].init(true, main.renderer.width, main.renderer.height);
		framebuffer[BLOOM1.ordinal()].init(false, main.renderer.width / 4, main.renderer.height / 4);
		framebuffer[BLOOM2.ordinal()].init(false, main.renderer.width / 4, main.renderer.height / 4);
		framebuffer[BLOOM5.ordinal()].init(false, main.renderer.width / 2, main.renderer.height / 2);
		framebuffer[BLOOM6.ordinal()].init(false, main.renderer.width / 2, main.renderer.height / 2);
		
		HEXAGON_OUTLINE.getProgram().use();
		GLES20.glUniform1f(HEXAGON_OUTLINE.getProgram().getUniform("u_Fog"), main.game.FOG);
		
		HEXAGON.getProgram().use();
		GLES20.glUniform1f(HEXAGON.getProgram().getUniform("u_Fog"), main.game.FOG);
		
		SCENE_OUTLINE.getProgram().use();
		GLES20.glUniform1f(SCENE_OUTLINE.getProgram().getUniform("u_Fog"), main.game.FOG);
		
		SCENE.getProgram().use();
		GLES20.glUniform1f(SCENE.getProgram().getUniform("u_Fog"), main.game.FOG);
		
		PARTICLE.getProgram().use();
		GLES20.glUniform1i(PARTICLE.getProgram().getUniform("u_Texture"), 0);

		POST.getProgram().use();
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Texture"), 1);
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Bloom"), 3);
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Bloom2"), 6);
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Dither"), 7);
		
		final int[] colors = new int[main.renderer.width * main.renderer.height];
		int gray;
		final Random random = new Random();
		
		for (int i = 0; i < colors.length; ++i)
		{
			gray = random.nextInt(256);
			colors[i] = Color.rgb(gray, gray, gray);
		}
			
		dither.init(Bitmap.createBitmap(colors, 0, main.renderer.width, main.renderer.width, main.renderer.height, Bitmap.Config.RGB_565), GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
		
		SCENE.getFramebuffer().bindTexture(1);
		BLOOM1.getFramebuffer().bindTexture(2);
		BLOOM2.getFramebuffer().bindTexture(3);
		BLOOM5.getFramebuffer().bindTexture(5);
		BLOOM6.getFramebuffer().bindTexture(6);
		dither.bind(7);
	}
	
	public static void delete()
	{
		for (Program program : programs)
			if (program.initialized())
				program.delete();
				
		for (Framebuffer framebuffer : Pass.framebuffer)
			if (framebuffer.initialized())
				framebuffer.delete();
				
		if (dither.initialized())
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
		return this != HEXAGON_OUTLINE;
	}
	
	public Pass getParent()
	{
		switch (this)
		{
			case HEXAGON_OUTLINE:
				return HEXAGON;

			case SCENE_OUTLINE:
				return SCENE;
				
			default:
				return this;
		}
	}
	
	public static void onRenderFrame(Renderer renderer)
	{
		SCENE.getFramebuffer().bind();
		GLES20.glEnable(GLES20.GL_BLEND);
	}
	
	public void onRender(Renderer renderer)
	{
		this.getProgram().use();
		
		switch (this)
		{
			case HEXAGON:
				GLES20.glUniform1f(getProgram().getUniform("u_Time"), (float) (renderer.main.world.time % (Math.PI * 2 * 10)));
			case SCENE:
				GLES20.glDepthMask(true);
				break;
				
			case HEXAGON_OUTLINE:
				GLES20.glUniform1f(getProgram().getUniform("u_Time"), (float) (renderer.main.world.time % (Math.PI * 2 * 10)));
				break;

			case PARTICLE:
				GLES20.glDepthMask(false);
				break;

			case BLOOM1:
				GLES20.glDisable(GLES20.GL_BLEND);
				BLOOM1.getFramebuffer().bind();
				GLES20.glUniform1i(getProgram().getUniform("u_Texture"), 1);
				GLES20.glUniform2f(getProgram().getUniform("u_Dir"), 0, 1F / SCENE.getFramebuffer().height);
				
				break;
				
			case BLOOM2:
				BLOOM2.getFramebuffer().bind();
				GLES20.glUniform1i(getProgram().getUniform("u_Texture"), 2);
				GLES20.glUniform2f(getProgram().getUniform("u_Dir"), 1F / BLOOM1.getFramebuffer().width, 0);
	
				break;
				
			case BLOOM3:
				BLOOM1.getFramebuffer().bind();
				GLES20.glUniform1i(getProgram().getUniform("u_Texture"), 3);
				GLES20.glUniform2f(getProgram().getUniform("u_Dir"), 0, 1F / BLOOM1.getFramebuffer().height);

				break;
				
			case BLOOM4:
				BLOOM2.getFramebuffer().bind();
				GLES20.glUniform1i(getProgram().getUniform("u_Texture"), 2);
				GLES20.glUniform2f(getProgram().getUniform("u_Dir"), 1F / BLOOM1.getFramebuffer().width, 0);

				break;
				
			case BLOOM5:
				BLOOM5.getFramebuffer().bind();
				GLES20.glUniform1i(getProgram().getUniform("u_Texture"), 1);
				GLES20.glUniform2f(getProgram().getUniform("u_Dir"), 0, 1F / BLOOM1.getFramebuffer().height);

				break;

			case BLOOM6:
				BLOOM6.getFramebuffer().bind();
				GLES20.glUniform1i(getProgram().getUniform("u_Texture"), 5);
				GLES20.glUniform2f(getProgram().getUniform("u_Dir"), 1F / BLOOM1.getFramebuffer().width, 0);

				break;
				
			case POST:
				GLES20.glDepthMask(true);
				Framebuffer.release(renderer);
				
				GLES20.glUniform1f(getProgram().getUniform("u_VignetteFactor"), 0.8F + 0.2F / renderer.main.timeFactor);
				GLES20.glUniform2f(POST.getProgram().getUniform("u_InvResolution"), 1F / SCENE.getFramebuffer().width, 1F / SCENE.getFramebuffer().height);
				
				break;
		}
	}
}
