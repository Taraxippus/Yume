package com.taraxippus.yume.game;

import com.taraxippus.yume.*;
import com.taraxippus.yume.render.*;
import java.util.*;

public class World
{
	public final Main main;
	public final ArrayList<GameObject>[] gameObjects = new ArrayList[Pass.values().length];
	
	public float time = 0;
	
	public World(Main main)
	{
		this.main = main;
		
		for (int i = 0; i < gameObjects.length; ++i)
			gameObjects[i] = new ArrayList<>();
	}
	
	public void add(GameObject gameObject)
	{
		gameObject.init();
		gameObjects[gameObject.getPass().ordinal()].add(gameObject);
	}
	
	public void remove(GameObject gameObject)
	{
		gameObject.delete();
		gameObjects[gameObject.getPass().ordinal()].remove(gameObject);
	}
	
	public void update()
	{
		time += Main.FIXED_DELTA;
		
		for (ArrayList<GameObject> list : gameObjects)
			for (GameObject gameObject : list)
				gameObject.update();
	}

	public void render(Renderer renderer, Pass pass)
	{
		for (GameObject gameObject : gameObjects[pass.ordinal()])
			gameObject.render(renderer);
	}

	public void delete()
	{
		for (ArrayList<GameObject> list : gameObjects)
			for (GameObject gameObject : list)
				gameObject.delete();
	}
}
