package com.taraxippus.yume.game;

import com.taraxippus.yume.*;
import com.taraxippus.yume.render.*;

public class Game
{
	public final Main main;
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		main.camera.init();
		
		main.world.add(new Box(main.world, true, true).translate(0, 7.5F, 0).scale(25, 15, 25).setPass(Pass.SCENE_POST));
		main.world.add(new Box(main.world, true).setColor(0xFF8800).translate(0, 0.5F, 0).rotate(0, 45F, 0));
		main.world.add(new FloatingBox(main.world).setColor(0x00CCFF).translate(10, 1.5F, 10).rotate(0, 45F, 0));
		
		main.world.add(new FullscreenQuad(main.world, Pass.POST));
	}
	
	public void update()
	{
		
	}
	
	public void delete()
	{
		main.world.delete();
	}
}
