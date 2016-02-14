package com.taraxippus.yume.render;
import android.opengl.*;
import com.taraxippus.yume.*;

public enum Pass
{
	SCENE_PRE,
	SCENE_POST,
	REFLECTION,
	POST;
	
	private static final Program[] programs = new Program[Pass.values().length];
	private static final Framebuffer[] framebuffers = new Framebuffer[Pass.values().length];
	
	private static final int[][] attributes = new int[Pass.values().length][];
	
	static
	{
		for (int i = 0; i < programs.length; ++i)
			programs[i] = new Program();
			
		for (int i = 0; i < framebuffers.length; ++i)
			framebuffers[i] = new Framebuffer();
			
		attributes[SCENE_PRE.ordinal()] = new int[] {3, 3};
		attributes[SCENE_POST.ordinal()] = new int[] {3, 3};
		attributes[REFLECTION.ordinal()] = new int[] {3, 3};
		attributes[POST.ordinal()] = new int[] {2};
	}
	
	public static void init(Main main)
	{
		programs[SCENE_PRE.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene, "a_Position", "a_Normal");
		framebuffers[SCENE_PRE.ordinal()].init(true, main.renderer.width, main.renderer.height);
		
		programs[SCENE_POST.ordinal()].init(main, R.raw.vertex_scene, R.raw.fragment_scene, "a_Position", "a_Normal");
		
		programs[REFLECTION.ordinal()].init(main, R.raw.vertex_reflection, R.raw.fragment_reflection, "a_Position", "a_Normal");
		
		programs[POST.ordinal()].init(main, R.raw.vertex_post, R.raw.fragment_post, "a_Position");
		
		POST.getProgram().use();
		GLES20.glUniform1i(POST.getProgram().getUniform("u_Texture"), 0);
	}
	
	public static void delete()
	{
		for (Program program : programs)
			if (program.initialized())
				program.delete();
				
		for (Framebuffer framebuffer : framebuffers)
			if (framebuffer.initialized())
				framebuffer.delete();
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
	
	public void onRender(Renderer renderer)
	{
		this.getProgram().use();
		
		switch (this)
		{
			case SCENE_PRE:
				this.getFramebuffer().bind();
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye, 0);
				break;
			
			case SCENE_POST:
				GLES20.glDepthMask(false);
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye, 0);
				break;
				
			case REFLECTION:
				GLES20.glDepthMask(true);
				GLES20.glUniform3fv(getProgram().getUniform("u_Eye"), 1, renderer.main.camera.eye, 0);
				break;
				
			case POST:
				GLES20.glDisable(GLES20.GL_STREAM_DRAW);
				Framebuffer.release(renderer);
				SCENE_PRE.getFramebuffer().bindTexture(0);
				break;
		}
	}
}
