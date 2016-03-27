package com.taraxippus.yume.game;

import android.opengl.*;
import com.taraxippus.yume.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.game.level.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;
import com.taraxippus.yume.game.particle.*;

public class Game
{
	public static final boolean DARK_MODE = false;
	
	public final Main main;

	public final VectorF light = new VectorF();
	
	public Box room;
	
	public Grid grid;
	public Player player;
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		light.set(main.level.getWidth() / 2F, main.level.getHeight() / 2F, main.level.getLength() / 2F);
		main.camera.init();
		
		main.world.add(this.room = (Box) new Box(main.world, true).translate(main.level.getWidth() / 2F - 0.5F, main.level.getHeight() / 2F - 0.5F, main.level.getLength() / 2F - 0.5F).setSpecularity(0, 0).scale(main.level.getWidth(), main.level.getHeight(), main.level.getLength()).setAlpha(0.5F).setDepthOffset(1500));
		
		main.world.add(this.player = (Player) new Player(main.world).translate(main.level.getWidth() / 2F, 0, main.level.getLength() / 2F));
		main.world.add(this.grid = (Grid) new Grid(main.world, new VectorF(main.level.getWidth(), main.level.getHeight(), main.level.getLength())).setColor(0x00CCFF).setSpecularity(0, 0).translate(main.level.getWidth() / 2F - 0.5F, main.level.getHeight() / 2F - 0.5F, main.level.getLength() / 2F - 0.5F).setDepthOffset(1501));
		
		main.world.add(new FloatingBox(main.world).translate(main.level.getWidth() / 2F, main.level.getHeight() / 2F + 3, main.level.getLength() / 2F));
		//main.world.add(new ParticleEmitter(main.world, 250).setRadius(0, 0, 0.25F, 0.5F).setVelocity(0.5F, 1.5F, 300, 360).translate(main.level.getWidth() / 2F, 0, main.level.getLength() / 2));
		
		main.world.add(new Jumper(main.world).translate(main.level.getWidth() - 1, 0, main.level.getLength() - 1));
		//main.world.add(new Jumper(main.world).translate(main.level.getWidth() - 2, 0, main.level.getLength() - 2));
		//main.world.add(new Jumper(main.world).translate(main.level.getWidth() - 3, 0, main.level.getLength() - 3));
		
		//main.world.add(new Follower(main.world).follow(player).translate(main.level.getWidth() / 2 - 1, 0, main.level.getLength() / 2));
		
		main.world.add(new FullscreenQuad(main.world, Pass.POST));
		
		if (DARK_MODE)
		{
			this.room.setColor(0x555555);
			this.grid.setColor(0xFF8800);
			this.player.setColor(0x00CCFF);
			
			GLES20.glClearColor(0x55 / 255F, 0x55 / 255F, 0x55 / 255F, 1);
		}
		
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
		intersection.add(normal);
		intersection.roundInt();
		
		if (intersection.x < main.level.getWidth() && intersection.x >= 0 && intersection.y < main.level.getHeight() && intersection.y >= 0 && intersection.z < main.level.getLength() && intersection.z >= 0)
		{
			if (player.selected)
			{
				player.findPath(intersection.x, intersection.y, intersection.z);
			}
			else
			{
				main.world.addLater(new Box(main.world)
				{
					@Override
					public void onTouch(VectorF intersection, VectorF normal)
					{
						if (!player.selected)
							onWallTouched(new VectorF(this.position.x, this.position.y, this.position.z), normal);
							
						else
							player.findPath(position.x + normal.x, position.y + normal.y, position.z + normal.z);
					}
					
					@Override
					public void onLongTouch(VectorF intersection, VectorF normal)
					{
						if (!player.selected)
						{
							world.removeLater(this);

							main.level.setBlocked(Math.round(position.x), Math.round(position.y), Math.round(position.z), (byte) 0);
						}
					}
					
				}.setTouchable(true).setColor(0xAAAAAA).translate(intersection.x, intersection.y, intersection.z));
			
				main.level.setBlocked((int) (intersection.x), (int) (intersection.y), (int) (intersection.z), (byte) 1);
			}
		}
	}
	
	public void delete()
	{
		main.world.delete();
	}
}
