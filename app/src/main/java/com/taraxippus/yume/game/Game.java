package com.taraxippus.yume.game;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.game.level.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;
import com.taraxippus.yume.game.path.*;

public class Game
{
	public static final int HEIGHT = 24;
	
	public final Main main;
	public final Level level;
	
	public final PathFinder pathFinder;
	
	public final Plane floor = new Plane(new VectorF(0, 1, 0), new VectorF());
	public final VectorF light = new VectorF();
	
	public Grid grid;
	public Player player;
	
	public Game(Main main)
	{
		this.main = main;
		
		this.level = new Level(this);
		this.pathFinder = new PathFinder(level, 1000, false);
		this.light.set(level.getWidth() / 2F, HEIGHT / 2F, level.getLength() / 2F);
	}
	
	public void init()
	{
		main.camera.init();
		
		main.world.add(new Box(main.world, true, true).translate(level.getWidth() / 2F - 0.5F, HEIGHT / 2F, level.getLength() / 2F - 0.5F).scale(level.getWidth(), HEIGHT, level.getLength()).setPass(Pass.SCENE_POST));
		
		main.world.add(this.player = (Player) new Player(main.world).translate(level.getWidth() / 2F, 0, level.getLength() / 2F));
		main.world.add(this.grid = (Grid) new Grid(main.world, new VectorF(level.getWidth(), HEIGHT, level.getLength())).setColor(0x00CCFF).translate(level.getWidth() / 2F - 0.5F, HEIGHT / 2F, level.getLength() / 2F - 0.5F).setPass(Pass.SCENE_POST));
		
		main.world.add(new FloatingBox(main.world).translate(1, 1.5F, 1).rotate(0, 45F, 0));
		main.world.add(new Jumper(main.world).translate(level.getWidth() - 1, 0, level.getLength() - 1));
		main.world.add(new Jumper(main.world).translate(level.getWidth() - 2, 0, level.getLength() - 2));
		main.world.add(new Jumper(main.world).translate(level.getWidth() - 3, 0, level.getLength() - 3));
		
		main.world.add(new FullscreenQuad(main.world, Pass.POST));
		
		main.camera.setTarget(player);
	}
	
	public void update()
	{
		main.world.update();
	}
	
	public void updateReal()
	{
		main.camera.update();
	}
	
	public void onFloorTouched(VectorF intersection)
	{
		intersection.roundInt();
		
		if (intersection.x < level.getWidth() && intersection.x >= 0 && intersection.z < level.getLength() && intersection.z >= 0)
		{
			if (player.selected)
			{
				player.setPath(pathFinder.findPath(player, player.position.x, player.position.z, intersection.x, intersection.z, true));
			}
			else
			{
				main.world.addLater(new Box(main.world)
				{
					public void onTouch()
					{
						world.removeLater(this);
						level.setBlocked(Math.round(position.x), Math.round(position.z), false);
					}
				}.setTouchable(true).setColor(0xFFCC00).translate(intersection.x, 0.5F, intersection.z));
			
				level.setBlocked((int)intersection.x, (int)intersection.z, true);
			}
		}
	}
	
	public void delete()
	{
		main.world.delete();
	}
}
