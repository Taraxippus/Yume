package com.taraxippus.yume;

import android.app.*;
import android.content.*;
import android.opengl.*;
import android.os.*;
import android.support.v4.view.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.game.gameobject.*;
import com.taraxippus.yume.game.level.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;
import java.io.*;
import java.nio.*;

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
	public final Level level = new Level(this);
	
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
			view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
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

			level.load(buffer);
		}
		
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		
		ByteBuffer buffer = ByteBuffer.allocate(level.getBytes());
		level.save(buffer);
		
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
	
	private int activePointerId = INVALID_POINTER_ID;
	private float lastTouchX, lastTouchY;

	@Override
	public boolean onTouch(View view, MotionEvent ev)
	{
		scaleDetector.onTouchEvent(ev);
		gestureDetector.onTouchEvent(ev);

		final int action = MotionEventCompat.getActionMasked(ev); 

		switch (action)
		{ 
			case MotionEvent.ACTION_DOWN:
				{
					final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
					final float x = MotionEventCompat.getX(ev, pointerIndex); 
					final float y = MotionEventCompat.getY(ev, pointerIndex); 

					lastTouchX = x;
					lastTouchY = y;
					
					activePointerId = MotionEventCompat.getPointerId(ev, 0);
					break;
				}

			case MotionEvent.ACTION_MOVE:
				{
					final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);  

					final float x = MotionEventCompat.getX(ev, pointerIndex);
					final float y = MotionEventCompat.getY(ev, pointerIndex);

			
					final float dx = x - lastTouchX;
					final float dy = y - lastTouchY;

					camera.rotation.y -= dx * 0.1F;
					camera.rotation.x -= dy * 0.1F;

					camera.rotation.x = Math.min(180 - 1F, Math.max(camera.rotation.x, 1F));
					
					lastTouchX = x;
					lastTouchY = y;

					break;
				}

			case MotionEvent.ACTION_UP: 
				{
					activePointerId = INVALID_POINTER_ID;
					break;
				}

			case MotionEvent.ACTION_CANCEL:
				{
					activePointerId = INVALID_POINTER_ID;
					break;
				}

			case MotionEvent.ACTION_POINTER_UP: 
				{

					final int pointerIndex = MotionEventCompat.getActionIndex(ev); 
					final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex); 

					if (pointerId == activePointerId)
					{
						final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
						lastTouchX = MotionEventCompat.getX(ev, newPointerIndex); 
						lastTouchY = MotionEventCompat.getY(ev, newPointerIndex); 
						activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
					}
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
			camera.zoom /= detector.getScaleFactor();
			camera.zoom = Math.max(1F, Math.min(camera.zoom, 2.5F));

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

			else if (viewRay.intersects(game.room.modelMatrix, game.room.invModelMatrix, true))
				game.onWallTouched(viewRay.intersection.set(viewRay.normal).multiplyBy(-0.5001F).add(viewRay.getPoint(viewRay.distance)), viewRay.normal);
				
			
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
