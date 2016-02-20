package com.taraxippus.yume.game.path;
import com.taraxippus.yume.game.level.*;
import com.taraxippus.yume.util.*;
import java.util.*;
import android.view.*;

public class Path
{
	public final ArrayList<Step> steps = new ArrayList<>();
	
	public int currentStep = 0;
	public boolean finished;
	
	public final Level level;
	public final IMover mover;
	
	public Path(Level level, IMover mover)
	{
		this.level = level;
		this.mover = mover;
	}
	
	public boolean hasNext()
	{
		return !finished && currentStep < steps.size();
	}
	
	public Step nextStep()
	{
		return steps.get(currentStep++);
	}
	
	public Step getTarget()
	{
		return steps.get(steps.size() - 1);
	}
	
	public void addStep(int x, int y, int z)
	{
		final VectorF gravity = level.getGravity(mover, x, y, z);
		steps.add(0, new Step(x, y, z, gravity));
		currentStep = 0;
	}
	
	public void finish()
	{
		this.finished = true;
	}
	
	public static class Step
	{
		public final int x;
		public final int y;
		public final int z;
		
		public final VectorF gravity;
		
		public Step(int x, int y, int z, VectorF gravity)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			
			this.gravity = gravity;
		}
	}
}
