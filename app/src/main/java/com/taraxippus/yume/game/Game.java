package com.taraxippus.yume.game;

import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.gameobject.Box;
import com.taraxippus.yume.game.gameobject.FullscreenQuad;
import com.taraxippus.yume.game.gameobject.Jumper;
import com.taraxippus.yume.game.gameobject.Player;
import com.taraxippus.yume.game.gameobject.SnowPlane;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.SimplexNoise;
import com.taraxippus.yume.util.VectorF;

public class Game
{
	public final Main main;
	public final SimplexNoise noiseGenerator = new SimplexNoise(256, 0.8F, 0);
	public final VectorF light = new VectorF(0, -1, 0);

	public Player player;
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		main.camera.init();

		main.world.add(new Box(main.world).scale(100, 1, 100).translate(0, -2, 0));

//		main.world.add(new Chunk(main.world, VectorF.obtain().set(0, 0, 0).release()));
		main.world.add(new SnowPlane(main.world, 30));
		main.world.add(new SnowPlane(main.world, 50));

		main.world.add(this.player = new Player(main.world));
		main.world.add(new Jumper(main.world));

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
	
	public void delete()
	{
		main.world.delete();
	}
}
