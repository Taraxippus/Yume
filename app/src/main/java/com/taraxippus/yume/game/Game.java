package com.taraxippus.yume.game;

import android.view.MotionEvent;
import android.view.View;
import com.taraxippus.yume.Main;
import com.taraxippus.yume.game.gameobject.Collectable;
import com.taraxippus.yume.game.gameobject.FullscreenQuad;
import com.taraxippus.yume.game.gameobject.Player;
import com.taraxippus.yume.game.gameobject.Projectile;
import com.taraxippus.yume.game.gameobject.Ring;
import com.taraxippus.yume.game.gameobject.SceneObject;
import com.taraxippus.yume.game.gameobject.SpeedPad;
import com.taraxippus.yume.game.model.BoxModel;
import com.taraxippus.yume.game.model.HexagonTubeModel;
import com.taraxippus.yume.game.model.Model;
import com.taraxippus.yume.game.model.PlayerModel;
import com.taraxippus.yume.game.model.RingModel;
import com.taraxippus.yume.game.model.SpeedPadModel;
import com.taraxippus.yume.game.model.SphereModel;
import com.taraxippus.yume.game.track.Track;
import com.taraxippus.yume.render.Pass;
import com.taraxippus.yume.util.VectorF;

public class Game implements View.OnTouchListener
{
	public boolean DEBUG_CAMERA = false;
	public static final float GRAVITY = -9.81F;
	
	public final Main main;
	public final float FOG = 0.01F;
	
	public Player player;
	
	public final Model playerModel = new PlayerModel();
	public final Model boxModel = new BoxModel();
	public final Model sphereModel = new SphereModel(50, 50);
	public final Model ringModel = new RingModel(10, 1.0F);
	public final Model ringModel2 = new RingModel(8, 0.66F);
	public final Model speedPadModel = new SpeedPadModel();

	public Track first;
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		main.camera.init();
		main.camera.position.set(0, 0, 0);
		main.camera.update();
		
		first = Track.createTrack(main.world);
		main.world.add(player = (Player) new Player(main.world, first).setModel(playerModel).setColor(0xFF8803));
		
		for (int i = 0; i < Track.trackLength; ++i)
			main.world.add(new Collectable(main.world, first).setModel(boxModel).translate(0, 0, i + main.world.random.nextFloat()));
		
		for (int i = 0; i < Track.trackLength; ++i)
			main.world.add(new SpeedPad(main.world, first).setModel(speedPadModel).translate(main.world.random.nextFloat() * 0.125F - 0.0625F, 0.00625F / Track.SIZE, i + 0.5F));
		
		for (int i = 0; i < 4; ++i)
			main.world.add(new Ring(main.world, first).setModel(i == 0 ? ringModel : ringModel2).translate(0, 0, i * Track.trackLength / 5F).rotate(0, 0, main.world.random.nextFloat() * 360));
			
		main.world.add(new FullscreenQuad(main.world, Pass.POST, Pass.BLOOM1, Pass.BLOOM2, Pass.BLOOM3, Pass.BLOOM4, Pass.BLOOM5, Pass.BLOOM6, Pass.COMBINE, Pass.MOTION));
		
		if (!DEBUG_CAMERA)
			main.camera.setTarget(player);
	}
	
	public void update()
	{
		if (pointerRight != -1)
		{
			if (DEBUG_CAMERA)
				main.camera.position.add(VectorF.obtain()
										 .set(newXRight - lastXRight, 0, newYRight - lastYRight)
										 .rotateX(main.camera.rotation.x)
										 .rotateY(main.camera.rotation.y)
										 .multiplyBy(Main.FIXED_DELTA * 4.5F)
										 .release());
			else
				player.velocity.add(VectorF.obtain()
									.set((newXRight - lastXRight) * 0.1F, 0, newYRight - lastYRight)
									.rotateY(player.rotation.y)
									.multiplyBy(0.025F)
									.release());
		}
		
		main.camera.update();
		main.world.update();
	}
	
	public void updateReal()
	{
		
	}
	
	public void delete()
	{
		main.world.delete();
	}
	
	public void onTap()
	{
		main.world.addLater(new Projectile(main.world, first).setVelocity(VectorF.obtain().set(0, 0, -0.01F).rotateY(player.rotation.y).release()).setModel(sphereModel).translate(player.trackPosition));
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
					main.timeFactor = DEBUG_CAMERA ? 0.5F : 1.0F;
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
					player.tilt += (lastXLeft - event.getX(index) / v.getWidth()) * 180;
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
