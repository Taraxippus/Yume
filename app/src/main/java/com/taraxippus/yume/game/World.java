package com.taraxippus.yume.game;

import android.view.animation.RotateAnimation;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.VectorF;

import java.nio.ByteBuffer;
import java.util.*;

public class World
{
	public static final float GRAVITY = -9.81F;
	
	public final Main main;
	public final Random random = new Random();
	public final ArrayList<GameObject>[] gameObjects = new ArrayList[Pass.values().length];
	public final ArrayList<SceneObject> sceneObjects = new ArrayList<>();
	
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
		for (int i = 0; i < Pass.values().length; ++i)
			if (gameObject.renderPass(Pass.values()[i]))
				gameObjects[i].add(gameObject);
		
		if (gameObject instanceof SceneObject)
			sceneObjects.add((SceneObject) gameObject);
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
		for (int i = 0; i < Pass.values().length; ++i)
			if (gameObject.renderPass(Pass.values()[i]))
				gameObjects[i].remove(gameObject);
		
		if (gameObject instanceof SceneObject)
			sceneObjects.remove(gameObject);
	}
	
	public void update()
	{
		time += Main.FIXED_DELTA;
		
		for (ArrayList<GameObject> list : gameObjects)
			for (GameObject gameObject : list)
				if (!(gameObject instanceof SceneObject && ((SceneObject) gameObject).noUpdate))
					gameObject.update();
	}

	public void render(Renderer renderer, Pass pass)
	{
		for (int i = 0; i < gameObjects_add.size();)
		{
			this.add(gameObjects_add.get(0));
			gameObjects_add.remove(0);
		}
		
		for (int i = 0; i < gameObjects_remove.size();)
		{
			this.remove(gameObjects_remove.get(0));
			gameObjects_remove.remove(0);
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

	VectorF gravity = new VectorF(0, -1, 0);

	public VectorF getGravity(float x, float z)
	{
		return gravity;
	}

	public ByteBuffer save(ByteBuffer buffer)
	{
		buffer.putInt(0);

		return buffer;
	}

	public ByteBuffer load(ByteBuffer buffer)
	{
		int version = buffer.getInt();

		if (version >= 0)
		{

		}

		return buffer;
	}

	public int getBytes()
	{
		return Integer.SIZE / Byte.SIZE;
	}
}
