package com.taraxippus.yume.game;

import com.taraxippus.yume.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.game.level.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;
import com.taraxippus.yume.game.path.*;

public class Game
{
	public final Main main;
	public final Level level;
	
	public final VectorF light = new VectorF();
	
	public Box room;
	
	public Grid grid;
	public Player player;
	
	public Game(Main main)
	{
		this.main = main;
		
		this.level = new Level(this);
		this.light.set(level.getWidth() / 2F, level.getHeight() / 2F, level.getLength() / 2F);
	}
	
	public void init()
	{
		main.camera.init();
		
		main.world.add(this.room = (Box) new Box(main.world, true, true).translate(level.getWidth() / 2F - 0.5F, level.getHeight() / 2F, level.getLength() / 2F - 0.5F).scale(level.getWidth(), level.getHeight(), level.getLength()).setPass(Pass.SCENE_POST));
		
		main.world.add(this.player = (Player) new Player(main.world).translate(level.getWidth() / 2F, 0, level.getLength() / 2F));
		main.world.add(this.grid = (Grid) new Grid(main.world, new VectorF(level.getWidth(), level.getHeight(), level.getLength())).setColor(0x00CCFF).translate(level.getWidth() / 2F - 0.5F, level.getHeight() / 2F, level.getLength() / 2F - 0.5F).setPass(Pass.SCENE_POST));
		
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
	
	public void onWallTouched(VectorF intersection, VectorF normal)
	{
		intersection.roundInt();
		
		//if (intersection.x < level.getWidth() && intersection.x >= 0 && intersection.y < level.getHeight() && intersection.y >= 0 && intersection.z < level.getLength() && intersection.z >= 0)
		{
			if (player.selected)
			{
				player.findPath(intersection.x, intersection.y, intersection.z);
			}
			else
			{
				main.world.addLater(new Box(main.world)
				{
//					@Override
//					public void onTouch(VectorF intersection, VectorF normal)
//					{
//						if (!player.selected)
//							onWallTouched(new VectorF(this.position.x, this.position.y + 0.5F, this.position.z), normal);
//							
//						else
//							player.findPath(position.x + normal.x, position.y - 0.5F + normal.y, position.z + normal.z);
//					}
//					
//					@Override
//					public void onDoubleTouch(VectorF intersection, VectorF normal)
//					{
//						if (!player.selected)
//						{
//							world.removeLater(this);
//
//							level.setBlocked(Math.round(position.x), Math.round(position.y - 0.5F), Math.round(position.z), false);
//						}
//					}
					
				}.setTouchable(true).setColor(0xAAAAAA).translate(intersection.x, intersection.y + 0.5F, intersection.z));
			
				//level.setBlocked((int) (intersection.x + normal.x), (int) (intersection.y + normal.y), (int) (intersection.z + normal.z), true);
			}
		}
	}
	
	public void delete()
	{
		main.world.delete();
	}
}
