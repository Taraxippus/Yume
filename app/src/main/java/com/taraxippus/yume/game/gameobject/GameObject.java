package com.taraxippus.yume.game.gameobject;

import com.taraxippus.yume.game.*;
import com.taraxippus.yume.render.*;

public class GameObject implements Comparable<GameObject>
{
	public final World world;
	public Shape shape;
	private Pass pass = Pass.SCENE;
	
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

	public float getDepth()
	{
		return 0;
	}
	
	@Override
	public int compareTo(GameObject o)
	{
		return (int) Math.signum(this.getDepth() - o.getDepth());
	}
}
