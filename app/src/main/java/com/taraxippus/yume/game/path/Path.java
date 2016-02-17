package com.taraxippus.yume.game.path;
import java.util.*;

public class Path
{
	public final ArrayList<Step> steps = new ArrayList<>();
	
	public int currentStep = 0;
	public boolean finished;
	
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
		steps.add(0, new Step(x, y, z));
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
		
		public Step(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
