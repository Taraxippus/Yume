package com.taraxippus.yume.render;
import android.opengl.*;
import com.taraxippus.yume.*;

public class Framebuffer
{
	final int[] framebuffer = new int[1];
	final Texture color = new Texture();
	final Texture depth = new Texture();
	
	public boolean hasDepth;
	int width, height;
	
	public Framebuffer()
	{
		
	}
	
	public void init(boolean hasDepth, int width, int height)
	{
		if (initialized())
			delete();
		
		this.hasDepth = hasDepth;
		this.width = width;
		this.height = height;
			
		GLES20.glGenFramebuffers(1, framebuffer, 0);
		
		color.init(width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, GLES20.GL_RGBA, GLES20.GL_LINEAR, GLES20.GL_LINEAR, GLES20.GL_CLAMP_TO_EDGE);
		
		if (hasDepth)
			depth.init(width, height, GLES20.GL_DEPTH_COMPONENT16, GLES20.GL_UNSIGNED_SHORT, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_NEAREST, GLES20.GL_NEAREST, GLES20.GL_CLAMP_TO_EDGE);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[0]);
		
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, color.texture[0], 0);
		
		if (hasDepth)
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, depth.texture[0], 0);

		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE || !initialized())
			throw new RuntimeException("Error creating framebuffer: " + status);
	}
	
	public boolean initialized()
	{
		return framebuffer[0] != 0 && color.initialized() && (!hasDepth || depth.initialized());
	}
	
	public void bind()
	{
		bind(true);
	}
		
	public void bind(boolean clear)
	{
		if (!initialized())
			throw new RuntimeException("Tried to bind an uninitialized, deleted or corrupt framebuffer");

		GLES20.glViewport(0, 0, width, height);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[0]);
	
		if (clear)
			GLES20.glClear(hasDepth ? GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT : GLES20.GL_COLOR_BUFFER_BIT);
	}
	
	public void bindTexture(int texture)
	{
		color.bind(texture);
	}
	
	public void bindDepthTexture(int texture)
	{
		depth.bind(texture);
	}
	
	public static void release(Renderer renderer)
	{
		GLES20.glViewport(0, 0, renderer.width, renderer.height);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	public void delete()
	{
		if (!initialized())
			throw new RuntimeException("Tried to use an uninitialized, deleted or corrupt framebuffer");
		
		GLES20.glDeleteFramebuffers(1, framebuffer, 0);
		color.delete();
		
		if (hasDepth)
			depth.delete();
		
		framebuffer[0] = 0;
	}
}
