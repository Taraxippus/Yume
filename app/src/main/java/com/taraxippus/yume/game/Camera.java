package com.taraxippus.yume.game;

import android.opengl.*;
import com.taraxippus.yume.*;
import com.taraxippus.yume.util.*;
import com.taraxippus.yume.game.gameobject.*;
import android.widget.*;

public class Camera
{
	public static final float Z_NEAR = 0.1F;
	public static final float Z_FAR = 100;
	public static final float FOV = 60;
	
	public static final float FOLLOW_SMOOTHNESS = 10;
	public static final boolean FOLLOW_ROTATION = true;
	
	public final Main main;
	
	public final float[] viewMatrix = new float[16];
	public final float[] projectionMatrix = new float[16];
	public final float[] projectionViewMatrix = new float[16];
	public final float[] invProjectionViewMatrix = new float[16];
	
	public float zoom = 1;
	
	public final VectorF position = new VectorF();
	public final VectorF rotationPre = new VectorF();
	public final VectorF rotation = new VectorF(-5, 180, 0);
	public final VectorF rotationPost = new VectorF();
	public final VectorF eye = new VectorF();
	public final VectorF up = new VectorF();
	
	public SceneObject target;
	
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
			
			this.rotationPre.multiplyBy(FOLLOW_SMOOTHNESS * 10).add(target.rotationPre).divideBy(FOLLOW_SMOOTHNESS * 10 + 1);
			this.rotationPost.multiplyBy(FOLLOW_SMOOTHNESS * 5).add(target.rotation).divideBy(FOLLOW_SMOOTHNESS * 5 + 1);
		}
			
		updateView();
	}
	
	public void setTarget(SceneObject target)
	{
		this.target = target;
		this.position.set(target.position);
		this.rotationPre.set(target.rotationPre);
		this.rotationPost.set(target.rotation);
	}
	
	public void onResize(int width, int height)
	{
		Matrix.perspectiveM(projectionMatrix, 0, FOV, (float) width / height, Z_NEAR, Z_FAR);
		//Matrix.frustumM(projectionMatrix, 0, (float) -width / height, (float) width / height, -1, 1, Z_NEAR, Z_FAR);
		this.updateViewProjection();
	}

	public void updateView()
	{
		if (FOLLOW_ROTATION)
		{
			Matrix.setIdentityM(viewMatrix, 0);
			Matrix.rotateM(viewMatrix, 0, rotationPre.y, 0, 1, 0);
			Matrix.rotateM(viewMatrix, 0, rotationPre.x, 1, 0, 0);
			Matrix.rotateM(viewMatrix, 0, rotationPre.z, 0, 0, 1);

			this.eye.set(0, 1, 0).multiplyBy(5 * zoom)
				.rotateX(rotation.x)
				.rotateY(rotation.y + rotationPost.y)
				.rotateZ(rotation.z + (target != null && target.rotationPre.z == 180 ? -180 : 0))
				.multiplyBy(viewMatrix)
				.add(position);

			this.eye.x = Math.min(Math.max(this.eye.x, -0.05F), this.main.level.getWidth() - 0.95F);
			this.eye.y = Math.min(Math.max(this.eye.y, -0.05F), this.main.level.getHeight() - 0.95F);
			this.eye.z = Math.min(Math.max(this.eye.z, -0.05F), this.main.level.getLength() - 0.95F);

			this.up.set(0, target != null && target.rotationPre.z == 180 ? -1 : 1, 0).multiplyBy(viewMatrix);

			Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, position.x, position.y, position.z, up.x, up.y, up.z);
			this.updateViewProjection();
		}
		else
		{
		
			this.eye.set(0, 1, 0).multiplyBy(5 * zoom)
				.rotateX(rotation.x)
				.rotateY(rotation.y)
				.rotateZ(rotation.z)
				.add(position);

			Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, position.x, position.y, position.z, 0, 1, 0);
			this.updateViewProjection();
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
    private final VectorF tmp = new VectorF();
	
    public void calculateFrustumPlanes()
    {
        leftPlane.set(tmp.set(value(41) + value(11), value(42) + value(12), value(43) + value(13)), value(44) + value(14));
        rightPlane.set(tmp.set(value(41) - value(11), value(42) - value(12), value(43) - value(13)), value(44) - value(14));

        bottomPlane.set(tmp.set(value(41) + value(21), value(42) + value(22), value(43) + value(23)), value(44) + value(24));
        topPlane.set(tmp.set(value(41) - value(21), value(42) - value(22), value(43) - value(23)), value(44) - value(24));

        nearPlane.set(tmp.set(value(41) + value(31), value(42) + value(32), value(43) + value(33)), value(44) + value(34));
        farPlane.set(tmp.set(value(41) - value(31), value(42) - value(32), value(43) - value(33)), value(44) - value(34));
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
