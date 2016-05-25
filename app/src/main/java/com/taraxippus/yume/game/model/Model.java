package com.taraxippus.yume.game.model;

import android.opengl.GLES20;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Shape;

public abstract class Model
{
	public final Pass pass;
	final Shape shape = new Shape();
	final Shape outlineShape = new Shape();
	private float[] vertices, verticesOutline;
	private short[] indices, indicesOutline;
	private int usesShape, usesOutline;
	boolean flatShape = true, generateShapeNormals = true, generateOutlineNormals = true;
	boolean freeSpace = true;
	
	public Model(Pass pass)
	{
		this.pass = pass;
	}
	
	public void init()
	{
		if (vertices == null)
		{
			this.vertices = getVertices();
			this.indices = getIndices();
		}
		if (verticesOutline == null)
		{
			this.verticesOutline = getOutlineVertices();
			this.indicesOutline = getOutlineIndices();
		}
	}

	public Shape getShape()
	{
		if (!shape.initialized())
			createShape();

		usesShape++;
		return shape;
	}

	public Shape getOutlineShape()
	{
		if (!outlineShape.initialized())
			createOutlineShape();

		usesOutline++;
		return outlineShape;
	}

	public void deleteShape()
	{
		usesShape--;

		if (usesShape == 0 && shape.initialized())
			shape.delete();
	}

	public void deleteOutlineShape()
	{
		usesOutline--;

		if (usesOutline == 0 && outlineShape.initialized())
			outlineShape.delete();
	}

	public Shape createShape()
	{
		init();
		
		if (flatShape)
			shape.initGenerateFlatNormals(GLES20.GL_TRIANGLES, vertices, indices, pass.getAttributes());
		
		else if (generateShapeNormals)
			shape.initGenerateNormals(GLES20.GL_TRIANGLES, vertices, indices, pass.getAttributes());
		
		else
			shape.init(GLES20.GL_TRIANGLES, vertices, indices, pass.getAttributes());
		
			
		if (freeSpace)
		{
			vertices = null;
			indices = null;
		}
		return shape;
	}

	public Shape createOutlineShape()
	{
		init();
		
		if (generateOutlineNormals)
			outlineShape.initGenerateNormals(GLES20.GL_TRIANGLES, verticesOutline, indicesOutline, pass.getAttributes());
		
		else
			outlineShape.init(GLES20.GL_TRIANGLES, verticesOutline, indicesOutline, pass.getAttributes());
		
		if (freeSpace)
		{
			verticesOutline = null;
			indicesOutline = null;
		}
		
		return shape;
	}

	public abstract float[] getVertices();
	public abstract short[] getIndices();
	
	public float[] getOutlineVertices()
	{
		return vertices;
	}
	
	public short[] getOutlineIndices()
	{
		return indices;
	}
}
