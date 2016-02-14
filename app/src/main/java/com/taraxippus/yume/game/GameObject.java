package com.taraxippus.yume.game;

import com.taraxippus.yume.*;
import com.taraxippus.yume.render.*;

public class GameObject
{
	public final World world;
	public Shape shape;
	private Pass pass = Pass.SCENE_PRE;
	
	public GameObject(World world)
	{
		this.world = world;
	}
	
	public void init()
	{
		this.shape = createShape();
	}
	
	public Shape createShape()
	{
		return null;
	}
	
	public void update()
	{
		
	}
	
	public void render(Renderer renderer)
	{
		if (shape != null)
			shape.render();
	}
	
	public Pass getPass()
	{
		return pass;
	}
	
	public GameObject setPass(Pass pass)
	{
		this.pass = pass;
		
		return this;
	}
	
	public void delete()
	{
		if (shape != null)
			shape.delete();
	}
}
