package com.taraxippus.yume.game;

import android.opengl.*;
import com.taraxippus.yume.*;
import com.taraxippus.yume.util.*;
import com.taraxippus.yume.game.gameobject.*;
import android.widget.*;

public class Camera
{
	public static final float Z_NEAR = 0.05F;
	public static final float Z_FAR = 12F;
	public static final float Z_FAR_DEBUG = 10;
	public static final float FOV = 60;
	
	public static final float FOLLOW_SMOOTHNESS = 1F;

	public final Main main;
	
	public final float[] tmpMatrix = new float[16];
	public final float[] viewMatrix = new float[16];
	public final float[] projectionMatrix = new float[16];
	public final float[] projectionViewMatrix = new float[16];
	public final float[] invProjectionViewMatrix = new float[16];
	
	public float zoom = 0.3F;
	
	public final VectorF position = new VectorF();
	public final VectorF rotation = new VectorF(-5, 180, 0);
	public final VectorF eye = new VectorF();
	
	public Player target;
	
	public Camera(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		updateView();
	}
	
	public void update()
	{
		if (target != null)
		{
			this.position.multiplyBy(FOLLOW_SMOOTHNESS).add(target.position).divideBy(FOLLOW_SMOOTHNESS + 1);
			this.rotation.y = (rotation.y * FOLLOW_SMOOTHNESS + target.rotation.y) / (FOLLOW_SMOOTHNESS + 1);
		}
			
		this.rotation.x = Math.max(Math.min(270, rotation.x), -90);
		
		if (target == null)
			this.rotation.y = (this.rotation.y + 180) % 360 - 180;
		
		updateView();
		updateProjection();
		this.updateViewProjection();
	}
	
	public void setTarget(Player target)
	{
		if (target == null && this.target != null)
			this.position.set(eye);
			
		else if (target != null)
			this.position.set(target.position);
			
		this.target = target;
	}
	
	float ratio;
	
	public void onResize(int width, int height)
	{
		this.ratio = width / (float) height;
		
		this.updateProjection();
		this.updateViewProjection();
	}

	public void updateProjection()
	{
		if (target != null)
			Matrix.perspectiveM(projectionMatrix, 0, FOV * Math.min(2, target.velocity.lengthSquared() / 4.0F + 1), ratio, Z_NEAR, Z_FAR);
		
			else
			Matrix.perspectiveM(projectionMatrix, 0, FOV, ratio, Z_NEAR, main.game.DEBUG_CAMERA ? Z_FAR_DEBUG : Z_FAR);
	}
	
	public void updateView()
	{
		if (target != null)
		{
			this.eye.set(0, 0, zoom)
				.rotateX(rotation.x)
				.rotateY(rotation.y)
				.rotateZ(rotation.z);

			Matrix.setIdentityM(viewMatrix, 0);
			Matrix.translateM(viewMatrix, 0, position.x, position.y, position.z);
			target.track.rotate(target.trackPosition, viewMatrix);
			Matrix.invertM(viewMatrix, 0, viewMatrix, 0);
			Matrix.setLookAtM(tmpMatrix, 0, eye.x, eye.y, eye.z, 0, 0, 0, 0, 1, 0);
			Matrix.multiplyMM(viewMatrix, 0, tmpMatrix, 0, viewMatrix, 0);
			
			this.eye.add(position);
		}
		else
		{
			this.eye.set(position);
			
			Matrix.setIdentityM(viewMatrix, 0);
			
			Matrix.rotateM(viewMatrix, 0, -rotation.z, 0, 0, 1);
			Matrix.rotateM(viewMatrix, 0, -rotation.x, 1, 0, 0);
			Matrix.rotateM(viewMatrix, 0, -rotation.y, 0, 1, 0);
			Matrix.translateM(viewMatrix, 0, -position.x, -position.y, -position.z);
		}
	}
	
	public void updateViewProjection()
	{
		Matrix.multiplyMM(projectionViewMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		Matrix.invertM(invProjectionViewMatrix, 0, projectionViewMatrix, 0);
		
		this.calculateFrustumPlanes();
	}
	
	final float[] rayWorld1 = new float[4];
    final float[] rayWorld2 = new float[4];
    final float[] rayClip1 = new float[] {0, 0, -1, 1};
    final float[] rayClip2 = new float[] {0, 0, 1, 1};

    public Ray unProject(float touchX, float touchY)
    {
        touchX = 2F * touchX / main.renderer.width - 1F;
        touchY = 1F - 2F * touchY / main.renderer.height;

        rayClip1[0] = rayClip2[0] = touchX;
        rayClip1[1] = rayClip2[1] = touchY;

        Matrix.multiplyMV(rayWorld1, 0, invProjectionViewMatrix, 0, rayClip1, 0);
        Matrix.multiplyMV(rayWorld2, 0, invProjectionViewMatrix, 0, rayClip2, 0);

        if (rayWorld1[3] != 0 && rayWorld2[3] != 0)
        {
            rayWorld1[0] = rayWorld1[0] / rayWorld1[3];
            rayWorld1[1] = rayWorld1[1] / rayWorld1[3];
            rayWorld1[2] = rayWorld1[2] / rayWorld1[3];
            rayWorld1[3] = 1;
			
            rayWorld2[0] = rayWorld2[0] / rayWorld2[3];
            rayWorld2[1] = rayWorld2[1] / rayWorld2[3];
            rayWorld2[2] = rayWorld2[2] / rayWorld2[3];
            rayWorld2[3] = 1;
        }

        return new Ray(rayWorld1[0], rayWorld1[1], rayWorld1[2], rayWorld2[0] - rayWorld1[0], rayWorld2[1] - rayWorld1[1], rayWorld2[2] - rayWorld1[2]);
    }

	private final Plane leftPlane = new Plane(), rightPlane = new Plane(), topPlane = new Plane(), bottomPlane = new Plane(), nearPlane = new Plane(), farPlane = new Plane();

    public void calculateFrustumPlanes()
    {
		VectorF tmp = VectorF.obtain();

        leftPlane.set(tmp.set(value(41) + value(11), value(42) + value(12), value(43) + value(13)), value(44) + value(14));
        rightPlane.set(tmp.set(value(41) - value(11), value(42) - value(12), value(43) - value(13)), value(44) - value(14));

        bottomPlane.set(tmp.set(value(41) + value(21), value(42) + value(22), value(43) + value(23)), value(44) + value(24));
        topPlane.set(tmp.set(value(41) - value(21), value(42) - value(22), value(43) - value(23)), value(44) - value(24));

        nearPlane.set(tmp.set(value(41) + value(31), value(42) + value(32), value(43) + value(33)), value(44) + value(34));
        farPlane.set(tmp.set(value(41) - value(31), value(42) - value(32), value(43) - value(33)), value(44) - value(34));

		VectorF.release(tmp);
    }

    private float value(int rowColumn)
    {
        return projectionViewMatrix[(rowColumn % 10 - 1) * 4 + rowColumn / 10 - 1];
    }

    public boolean insideFrustum(VectorF v, float distance)
    {
        return nearPlane.distance(v) >= -distance
			&& farPlane.distance(v) >= -distance
			&& topPlane.distance(v) >= -distance
			&& bottomPlane.distance(v) >= -distance
			&& leftPlane.distance(v) >= -distance
			&& rightPlane.distance(v) >= -distance;
    }

}
