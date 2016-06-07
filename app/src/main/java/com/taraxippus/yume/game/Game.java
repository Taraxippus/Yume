package com.taraxippus.yume.game;

import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.gameobject.Collectable;
import com.taraxippus.yume.game.gameobject.FullscreenQuad;
import com.taraxippus.yume.game.gameobject.HexagonTube;
import com.taraxippus.yume.game.gameobject.Player;
import com.taraxippus.yume.game.gameobject.SceneObject;
import com.taraxippus.yume.game.model.BoxModel;
import com.taraxippus.yume.game.model.HexagonTubeModel;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.game.model.PlayerModel;
import com.taraxippus.yume.game.model.SpeedPadModel;
import com.taraxippus.yume.game.model.SphereModel;
import com.taraxippus.yume.game.model.TrackModel;
import com.taraxippus.yume.game.model.TrackSideModel;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.SimplexNoise;
import com.taraxippus.yume.util.VectorF;

public class Game implements View.OnTouchListener
{
	private boolean DEBUG_CAMERA = true;
	
	public final Main main;
	public final SimplexNoise noiseGenerator = new SimplexNoise(1024, 0.75F, 0);
	public final VectorF light = new VectorF(-0.25F, -1, 0.1F).normalize();

	public SceneObject player;
	
	public final Model playerModel = new PlayerModel();
	public final Model hexagonTubeModel = new HexagonTubeModel(100, 10);
	public final Model boxModel = new BoxModel();
	public final Model sphereModel = new SphereModel(50, 50);
	public final Model speedPadModel = new SpeedPadModel();
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		main.camera.init();
		main.camera.position.set(0, 2, 0);
		main.camera.update();
		
		
//		for (int i = 0; i < 10; ++i)
//			main.world.add(new Collectable(main.world).setModel(boxModel).scale(0.5F, 0.5F, 0.5F).translate(0, 0, -i * 5 + 25));
//		
//		main.world.add(new SceneObject(main.world, speedPadModel).scale(1.0F, 0.1F, 0.75F).translate(-3, 0.2F, 2).setAlpha(0.75F).setColor(0x00CCFF));
//		main.world.add(new SceneObject(main.world, speedPadModel).scale(1.0F, 0.1F, 0.75F).translate(0, 0.2F, 2).setAlpha(0.75F).setColor(0x00CCFF));
//		main.world.add(new SceneObject(main.world, speedPadModel).scale(1.0F, 0.1F, 0.75F).translate(3, 0.2F, 2).setAlpha(0.75F).setColor(0x00CCFF));
//		
		main.world.add(player = new Player(main.world).setModel(playerModel).translate(0, 0.5F, 25).setColor(0xFF8800));
		
		float[] matrix = new float[16];
		
		Matrix.setIdentityM(matrix, 0);
		Model trackModel = new TrackModel(matrix, 1, 1);
		Model trackSideModel = new TrackSideModel(matrix, 1, 1);
		main.world.add(new SceneObject(main.world, trackModel).scale(10.0F, 10.0F, 50.0F).translate(0, 0, -50));
		main.world.add(new SceneObject(main.world, trackSideModel).scale(10.0F, 10.0F, 50.0F).translate(0, 0, -50).setColor(0x00CCFF));
		main.world.add(new HexagonTube(main.world, false).setModel(hexagonTubeModel).translate(0, 0, -50).scale(50, 50, 50));
		
		float[] matrix1 = new float[16];

		Matrix.setIdentityM(matrix1, 0);
		Matrix.rotateM(matrix1, 0, 90, 0, 0, 1);
		Model trackModel1 = new TrackModel(matrix1, 5, 20);
		Model trackSideModel1 = new TrackSideModel(matrix1, 5, 20);
		main.world.add(new SceneObject(main.world, trackModel1).scale(10.0F, 10.0F, 50.0F).translate(0, 0, 0));
		main.world.add(new SceneObject(main.world, trackSideModel1).scale(10.0F, 10.0F, 50.0F).translate(0, 0, 0).setColor(0x00CCFF));
		main.world.add(new HexagonTube(main.world, false).setModel(hexagonTubeModel).translate(0, 0, 0).scale(50, 50, 50));
		
		main.world.add(new SceneObject(main.world, trackModel1).scale(10.0F, 10.0F, 50.0F).rotate(0, 0, 90).translate(0, 0, 50));
		main.world.add(new SceneObject(main.world, trackSideModel1).scale(10.0F, 10.0F, 50.0F).rotate(0, 0, 90).translate(0, 0, 50).setColor(0x00CCFF));
		main.world.add(new HexagonTube(main.world, false).setModel(hexagonTubeModel).translate(0, 0, 50).scale(50, 50, 50));
		
		main.world.add(new SceneObject(main.world, trackModel).scale(10.0F, 10.0F, 50.0F).rotate(0, 0, 180).translate(0, 0, 100));
		main.world.add(new SceneObject(main.world, trackSideModel).scale(10.0F, 10.0F, 50.0F).rotate(0, 0, 180).translate(0, 0, 100).setColor(0x00CCFF));
		main.world.add(new HexagonTube(main.world, false).setModel(hexagonTubeModel).translate(0, 0, 100).scale(50, 50, 50));
		
		main.world.add(new FullscreenQuad(main.world, Pass.POST));
		
		if (!DEBUG_CAMERA)
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
			if (DEBUG_CAMERA)
				main.camera.position.add(VectorF.obtain()
				.set(newXRight - lastXRight, 0, newYRight - lastYRight)
				.rotateX(main.camera.rotation.x)
				.rotateY(main.camera.rotation.y)
				.multiplyBy(Main.FIXED_DELTA * 45)
				.release());
			else
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
				if (event.getPointerCount() == 3)
				{
					DEBUG_CAMERA = !DEBUG_CAMERA;
					main.camera.setTarget(DEBUG_CAMERA ? null : player);
					break;
				}
				
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
					if (DEBUG_CAMERA)
						main.camera.rotation.y += (lastXLeft - event.getX(index) / v.getWidth()) * 180;
					
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
