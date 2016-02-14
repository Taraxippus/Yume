package com.taraxippus.yume;

import android.app.*;
import android.opengl.*;
import android.os.*;
import android.support.v4.view.*;
import android.view.*;
import com.taraxippus.yume.game.*;
import com.taraxippus.yume.render.*;
import com.taraxippus.yume.util.*;

public class Main extends Activity implements View.OnTouchListener
{
	public static final float FIXED_DELTA = 1 / 60F;
	public final float timeFactor = 1;
	
	public final ResourceHelper resourceHelper = new ResourceHelper(this);
	public final Renderer renderer = new Renderer(this);
	public final Game game = new Game(this);
	public final World world = new World(this);
	public final Camera camera = new Camera(this);
	
	public GLSurfaceView view;
	
	private ScaleGestureDetector scaleDetector;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
		
		view = new GLSurfaceView(this);
		view.setOnTouchListener(this);
		
		view.setPreserveEGLContextOnPause(true);
		view.setEGLContextClientVersion(2);
		view.setEGLConfigChooser(new ConfigChooser(this));
		
		view.setRenderer(renderer);
		view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
        setContentView(view);
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

					camera.rotY -= dx * 0.1F;
					camera.rotX -= dy * 0.1F;

					camera.rotX = Math.min(-5, Math.max(camera.rotX, -90));
					
					camera.updateView();

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

			camera.updateView();
			
			return true;
		}
	}
}
