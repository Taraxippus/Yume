package com.taraxippus.yume.util;

import com.taraxippus.yume.game.gameobject.*;
import java.util.*;

public class Ray
{
    public final VectorF point = new VectorF();
    public final VectorF direction = new VectorF();

    public final VectorF tmp1 = new VectorF();

	public static final VectorF MIN = new VectorF(-0.5F, -0.5F, -0.5F);
	public static final VectorF MAX = new VectorF(0.5F, 0.5F, 0.5F);
	
	public float distance;
	
    public Ray() {}

    public Ray(VectorF point, VectorF direction)
    {
        this.point.set(point);
        this.direction.set(direction);
        this.direction.normalize();
    }

    public Ray(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        this.point.set(x1, y1, z1);
        this.direction.set(x2, y2, z2);
        this.direction.normalize();
    }

    public boolean intersects(Plane p, VectorF result)
    {
        final float DotProduct = direction.dot(p.normal);

        if (DotProduct == 0)
        {
            return false;
        }

        final float distance = p.normal.dot(tmp1.set(p.point).subtract(point)) / DotProduct;

        if (Float.isNaN(distance) || distance < 0)
        {
            return false;
        }

        result.set(getPoint(distance));
        return true;
    }

    public SceneObject intersectsFirst(ArrayList<SceneObject> entities, VectorF intersection)
    {
        float distance = this.distance = -10000;
        SceneObject first = null;

        for (SceneObject entity : entities)
        {
            if (entity.touchable && this.intersects(entity.invModelMatrix, MIN, MAX) && this.distance > distance)
            {
                distance = this.distance;
                first = entity;
            }
        }

        this.distance = distance;

        intersection.set(getPoint(-distance));
        return first;
    }

    public boolean intersects(float[] matrix, VectorF min, VectorF max)
    {
        float t1, t2;
        float tMin = -10000;
        float tMax = 10000;

        VectorF point;
        VectorF point2;
        VectorF direction;

        if (matrix == null)
        {
            point = this.point;
            direction = this.direction;
        }
        else
        {
            point = this.point.copy().multiplyBy(matrix);
            point2 = this.point.copy().add(this.direction).multiplyBy(matrix);
            direction = point.copy().subtract(point2).normalize();
        }

        if (direction.x != 0.0)
        {
            t1 = (min.x - point.x) / direction.x;
            t2 = (max.x - point.x) / direction.x;

            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        }

        if (direction.y != 0.0)
        {
            t1 = (min.y - point.y) / direction.y;
            t2 = (max.y - point.y) / direction.y;

            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        }

        if (direction.z != 0.0)
        {
            t1 = (min.z - point.z) / direction.z;
            t2 = (max.z - point.z) / direction.z;

            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        }

        distance = tMin;

        return tMax >= tMin;
    }

    public VectorF getPoint(float t)
    {
        return tmp1.set(direction).multiplyBy(t).add(point);
    }
}
