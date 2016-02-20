package com.taraxippus.yume.game;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.render.*;
import java.util.*;

public class World
{
	public static final float SCALE = 0.5F;
	public static final float GRAVITY = -9.81F * SCALE;
	
	public final Main main;
	public final ArrayList<GameObject>[] gameObjects = new ArrayList[Pass.values().length];
	public final ArrayList<SceneObject> sceneObjects = new ArrayList<>();
	public final ArrayList<MovingObject> movingObjects = new ArrayList<>();
	
	public final List<GameObject> gameObjects_add = Collections.synchronizedList(new ArrayList<GameObject>());
	public final List<GameObject> gameObjects_remove = Collections.synchronizedList(new ArrayList<GameObject>());
	
	public float time = 0;
	
	public World(Main main)
	{
		this.main = main;
		
		for (int i = 0; i < gameObjects.length; ++i)
			gameObjects[i] = new ArrayList<>();
	}
	
	public void addLater(GameObject gameObject)
	{
		gameObjects_add.add(gameObject);
	}
	
	public void add(GameObject gameObject)
	{	
		if (gameObject == null || isDestroying)
			return;
			
		gameObject.init();
		gameObjects[gameObject.getPass().ordinal()].add(gameObject);
		
		if (gameObject instanceof SceneObject)
			sceneObjects.add((SceneObject) gameObject);
			
		if (gameObject instanceof MovingObject)
			movingObjects.add((MovingObject) gameObject);
	}
	
	public void removeLater(GameObject gameObject)
	{
		gameObjects_remove.add(gameObject);
	}
	
	public void remove(GameObject gameObject)
	{
		if (gameObject == null || isDestroying)
			return;
		
		gameObject.delete();
		gameObjects[gameObject.getPass().ordinal()].remove(gameObject);
		
		if (gameObject instanceof SceneObject)
			sceneObjects.remove(gameObject);
			
		if (gameObject instanceof MovingObject)
			movingObjects.remove(gameObject);
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
		for (GameObject gameObject : gameObjects_add)
		{
			this.add(gameObject);
			gameObjects_add.remove(gameObject);
		}
		
		for (GameObject gameObject : gameObjects_remove)
		{
			this.remove(gameObject);
			gameObjects_remove.remove(gameObject);
		}
		
		Collections.sort(gameObjects[pass.ordinal()]);
		
		for (GameObject gameObject : gameObjects[pass.ordinal()])
			gameObject.render(renderer);
	}

	public boolean isDestroying = false;
	
	public void delete()
	{
		isDestroying = true;
		
		for (ArrayList<GameObject> list : gameObjects)
			for (GameObject gameObject : list)
				gameObject.delete();
			
		isDestroying = false;
	}
}
