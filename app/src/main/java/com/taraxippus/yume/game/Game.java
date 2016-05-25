package com.taraxippus.yume.game;

import android.view.MotionEvent;
import android.view.View;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.gameobject.FullscreenQuad;
import com.taraxippus.yume.game.gameobject.HexagonTube;
import com.taraxippus.yume.game.gameobject.Player;
import com.taraxippus.yume.game.gameobject.SceneObject;
import com.taraxippus.yume.game.model.Box;
import com.taraxippus.yume.game.model.HexagonTubeModel;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.game.model.PlayerModel;
import com.taraxippus.yume.game.model.Sphere;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.SimplexNoise;
import com.taraxippus.yume.util.VectorF;

public class Game implements View.OnTouchListener
{
	public final Main main;
	public final SimplexNoise noiseGenerator = new SimplexNoise(1024, 0.75F, 0);
	public final VectorF light = new VectorF(-0.25F, -1, 0.1F).normalize();

	public SceneObject player;
	
	public final Model playerModel = new PlayerModel();
	public final Model hexagonTubeModel = new HexagonTubeModel(100, 10);
	public final Model boxModel = new Box();
	public final Model sphereModel = new Sphere(50, 50);
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		main.camera.init();
		main.camera.position.set(0, 2, 0);
		main.camera.update();
		
		main.world.add(new HexagonTube(main.world).setModel(hexagonTubeModel).scale(50, 50, 50).translate(0, 0, 50));
		main.world.add(new HexagonTube(main.world).setModel(hexagonTubeModel).scale(50, 50, 50));
		main.world.add(new SceneObject(main.world, boxModel).scale(10, 0.1F, 100).translate(0, 0, 25).setAlpha(0.75F));
		main.world.add(new SceneObject(main.world, boxModel).scale(1, 0.1F, 100).translate(-5, 1, 25).setAlpha(0.5F).setColor(0x00CCFF));
		main.world.add(new SceneObject(main.world, boxModel).scale(1, 0.1F, 100).translate(5, 1, 25).setAlpha(0.5F).setColor(0x00CCFF));
		
		main.world.add(new SceneObject(main.world, sphereModel).scale(1, 1, 1).translate(0, 2, 0));
		
		main.world.add(player = new Player(main.world).setModel(playerModel).translate(0, 1, 25).setColor(0xFF8800));
		
		main.world.add(new FullscreenQuad(main.world, Pass.POST));
		
		main.camera.setTarget(player);
	}
	
	public void update()
	{
		main.world.update();
	}
	
	public void updateReal()
	{
		if (pointerRight != -1)
		{
			player.position.add(VectorF.obtain()
			.set(newXRight - lastXRight, 0, newYRight - lastYRight)
			.rotateY(player.rotation.y)
			.multiplyBy(Main.FIXED_DELTA * 45)
			.release());
		}
		
		main.camera.update();
	}
	
	public void delete()
	{
		main.world.delete();
	}
	
	int pointerLeft = -1;
	int pointerRight = -1;
	
	float lastXLeft, lastYLeft;
	float lastXRight, lastYRight, newXRight, newYRight;
	
	@Override
    public boolean onTouch(View v, MotionEvent event)
    {
		int index = event.getActionIndex();
		int pointer = event.getPointerId(index);
		
        switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				if (event.getX(index) < v.getWidth() / 2F)
				{
					if (pointerLeft == -1)
					{
						pointerLeft = pointer;
						lastXLeft = event.getX(index) / (float) v.getWidth();
						lastYLeft = event.getY(index) / (float) v.getHeight();
					}
				}
				else if (pointerRight == -1)
				{
					pointerRight = pointer;
					newXRight = lastXRight = event.getX(index) / (float) v.getWidth();
					lastYRight = lastYRight = event.getY(index) / (float) v.getHeight();
				}
				
				break;
			case MotionEvent.ACTION_MOVE:
				index = event.findPointerIndex(pointerLeft);
				
				if (index != -1)
				{
					player.rotation.y += (lastXLeft - event.getX(index) / v.getWidth()) * 180;
					main.camera.rotation.x += (lastYLeft - event.getY(index) / v.getHeight()) * 180;
					
					lastXLeft = event.getX(index) / (float) v.getWidth();
					lastYLeft = event.getY(index) / (float) v.getHeight();
				}
				
				index = event.findPointerIndex(pointerRight);
				
				if (index != -1)
				{
					newXRight = event.getX(index) / (float) v.getWidth();
					newYRight = event.getY(index) / (float) v.getHeight();
				}
				break;
				
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				pointerLeft = -1;
				pointerRight = -1;
				break;
				
			case MotionEvent.ACTION_POINTER_UP:
				if (pointer == pointerLeft)
					pointerLeft = -1;
					
				if (pointer == pointerRight)
					pointerRight = -1;
					
				break;
		}

        return true;
    }
}
