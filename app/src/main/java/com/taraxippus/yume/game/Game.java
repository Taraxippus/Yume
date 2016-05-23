package com.taraxippus.yume.game;

import android.view.MotionEvent;
import android.view.View;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.gameobject.Box;
import com.taraxippus.yume.game.gameobject.FullscreenQuad;
import com.taraxippus.yume.game.gameobject.Sphere;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.SimplexNoise;
import com.taraxippus.yume.util.VectorF;
import com.taraxippus.yume.game.gameobject.HexagonPlane;

public class Game implements View.OnTouchListener
{
	public final Main main;
	public final SimplexNoise noiseGenerator = new SimplexNoise(1024, 0.75F, 0);
	public final VectorF light = new VectorF(-0.25F, -1, 0.1F).normalize();

	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		main.camera.init();

		main.world.add(new HexagonPlane(main.world).scale(10, 3, 10).translate(0, -2, 0));

		main.world.add(new Box(main.world).scale(1, 1, 1).rotate(0, 45, 0).translate(5, 0, 5));
		main.world.add(new Box(main.world).scale(1, 1, 1).translate(10, 0, 0));
		main.world.add(new Box(main.world).scale(1, 1, 1).translate(0, 0, -10));
		
		main.world.add(new Sphere(main.world).scale(1, 1, 1).translate(5, 0, 10));
		main.world.add(new Sphere(main.world).scale(2, 2, 2).translate(0, 0.5F, 0));
		main.world.add(new Sphere(main.world).scale(3, 3, 3).translate(-5, 1F, 0));
	
		main.world.add(new FullscreenQuad(main.world, Pass.POST));
	}
	
	public void update()
	{
		main.world.update();
	}
	
	public void updateReal()
	{
		if (pointerRight != -1)
		{
			main.camera.position.add(VectorF.obtain()
			.set(newXRight - lastXRight, 0, newYRight - lastYRight)
			.rotateX(main.camera.rotation.x)
			.rotateY(main.camera.rotation.y)
			.multiplyBy(Main.FIXED_DELTA * 25)
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
					main.camera.rotation.y += (lastXLeft - event.getX(index) / v.getWidth()) * 180;
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
					
				else if (pointer == pointerRight)
					pointerRight = -1;
					
				break;
		}

        return true;
    }
}
