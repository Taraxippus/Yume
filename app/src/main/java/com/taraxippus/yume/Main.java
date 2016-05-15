package com.taraxippus.yume;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taraxippus.yume.game.Camera;
import com.taraxippus.yume.game.Game;
import com.taraxippus.yume.game.World;
import com.taraxippus.yume.game.gameobject.SceneObject;
import com.taraxippus.yume.render.ConfigChooser;
import com.taraxippus.yume.render.Renderer;
import com.taraxippus.yume.util.Ray;
import com.taraxippus.yume.util.ResourceHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class Main extends Activity implements View.OnTouchListener
{
	public static final String FILENAME = "save.save";
	
	public static final float FIXED_DELTA = 1 / 120F;
	public float timeFactor = 1;
	
	public final ResourceHelper resourceHelper = new ResourceHelper(this);
	public final Renderer renderer = new Renderer(this);
	public final Game game = new Game(this);
	public final World world = new World(this);
	public final Camera camera = new Camera(this);

	public GLSurfaceView view;
	public TextView textView;
	
	private ScaleGestureDetector scaleDetector;
	private GestureDetector gestureDetector;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
		gestureDetector = new GestureDetector(this, new GestureListener());
		
		view = new GLSurfaceView(this);
		view.setOnTouchListener(this);
		
		view.setPreserveEGLContextOnPause(true);
		view.setEGLContextClientVersion(2);
		view.setEGLConfigChooser(new ConfigChooser(this));
		
		view.setRenderer(renderer);
		view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
        setContentView(view);
		
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		
		textView = new TextView(this);
		textView.setTextColor(0xFFFFFFFF);
		textView.setShadowLayer(padding, -1, -1, 0xFF000000);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		textView.setText("60");
		textView.setPadding(padding, padding / 2, padding, padding);
		addContentView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) 
	{
        super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
				view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		view.onResume();
		
		renderer.lastTime = 0;
		
		byte[] bytes = null;
		FileInputStream fis = null;
		try
		{
			fis = openFileInput(FILENAME);
			
			if (fis != null)
			{
				bytes = new byte[fis.available()];
				fis.read(bytes);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		finally
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
		
		if (bytes != null && bytes.length > 0)
		{
			ByteBuffer buffer = ByteBuffer.wrap(bytes);

			world.load(buffer);
		}
		
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		view.onPause();
		
		ByteBuffer buffer = ByteBuffer.allocate(world.getBytes());
		world.save(buffer);
		
		FileOutputStream fos = null;
		try
		{
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(buffer.array());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fos != null)
					fos.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onDestroy()
	{
		renderer.delete();
		
		super.onDestroy();
	}
	
	private static final int INVALID_POINTER_ID = -1;
	
	private int activePointerIdLeft = INVALID_POINTER_ID, activePointerIdRight = INVALID_POINTER_ID;
	private float lastTouchXLeft, lastTouchYLeft, lastTouchYRight;

	@Override
	public boolean onTouch(View view, MotionEvent ev)
	{
		scaleDetector.onTouchEvent(ev);
		gestureDetector.onTouchEvent(ev);

		final int action = MotionEventCompat.getActionMasked(ev); 

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				{
					final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
					final float x = MotionEventCompat.getX(ev, pointerIndex); 
					final float y = MotionEventCompat.getY(ev, pointerIndex); 

					if (x < view.getWidth() / 2)
					{
						if (activePointerIdLeft == INVALID_POINTER_ID)
						{
							lastTouchXLeft = x;
							lastTouchYLeft = y;

							activePointerIdLeft = MotionEventCompat.getPointerId(ev, 0);
						}
					}
					else
					{
						if (activePointerIdRight == INVALID_POINTER_ID)
						{
							lastTouchYRight = y;

							activePointerIdRight = MotionEventCompat.getPointerId(ev, 0);
						}
					}

					break;
				}

			case MotionEvent.ACTION_MOVE:
				{
					final int pointerIndex = MotionEventCompat.getActionIndex(ev);
					final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
					float x, y, dx, dy;

					if (pointerIndex == activePointerIdLeft)
					{
						x = MotionEventCompat.getX(ev, pointerIndex);
						y = MotionEventCompat.getY(ev, pointerIndex);

						dx = x - lastTouchXLeft;
						dy = y - lastTouchYLeft;

						camera.rotation.y -= dx * 0.1F;
						camera.rotation.x -= dy * 0.1F;

						camera.rotation.x = Math.min(180 - 1F, Math.max(camera.rotation.x, 1F));

						lastTouchXLeft = x;
						lastTouchYLeft = y;
					}
					else if (pointerId == activePointerIdRight)
					{
						y = MotionEventCompat.getY(ev, pointerIndex);
						dy = y - lastTouchYRight;
						camera.target.jumpLength = Math.abs(dy / 50F);
						camera.target.move(camera.rotation.y - (dy < 0 ? 180 : 0));
					}

					break;
				}

			case MotionEvent.ACTION_POINTER_UP:
			{
				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
				final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

				if (pointerId == activePointerIdLeft)
					activePointerIdLeft = INVALID_POINTER_ID;

				else if (pointerId == activePointerIdRight)
					activePointerIdRight = INVALID_POINTER_ID;

				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			{
				activePointerIdLeft = INVALID_POINTER_ID;
				activePointerIdRight = INVALID_POINTER_ID;

				break;
			}
		}       
		return true;
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener 
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector) 
		{
			if (activePointerIdLeft == INVALID_POINTER_ID || activePointerIdRight == INVALID_POINTER_ID)
			{
				camera.zoom /= detector.getScaleFactor();
				camera.zoom = Math.max(1F, Math.min(camera.zoom, 2.5F));
			}

			return true;
		}
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());
			
			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);
			
			if (touched != null)
				touched.onTouch(viewRay.intersection, viewRay.normal);
			
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());

			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);

			if (touched != null)
				touched.onLongTouch(viewRay.intersection, viewRay.normal);
		}

		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());
		
			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);

			if (touched != null)
				touched.onSingleTouch(viewRay.intersection, viewRay.normal);
				
			return touched != null;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			final Ray viewRay = camera.unProject(e.getX(), e.getY());
			
			SceneObject touched = viewRay.intersectsFirst(world.sceneObjects);

			if (touched != null)
				touched.onDoubleTouch(viewRay.intersection, viewRay.normal);

			return touched != null;
		}

		
	}
}
