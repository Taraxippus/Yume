package com.taraxippus.yume.game.gameobject;
import android.opengl.GLES20;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.render.Shape;
import com.taraxippus.yume.util.VectorF;

public class HexagonTube extends SceneObject
{
	public HexagonTube(World world)
	{
		super(world);
		setPass(Pass.HEXAGON_OUTLINE);
	}

	@Override
	public void update()
	{
		super.update();
		
		rotate(0, 0, Main.FIXED_DELTA * 10);
	}
}
