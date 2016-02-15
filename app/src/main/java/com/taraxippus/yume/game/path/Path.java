package com.taraxippus.yume.game.path;
import java.util.*;

public class Path
{
	public ArrayList<Step> steps = new ArrayList<>();
	
	public int currentStep = 0;
	
	public boolean hasNext()
	{
		return currentStep < steps.size();
	}
	
	public Step nextStep()
	{
		return steps.get(currentStep++);
	}
	
	public void addStep(int x, int z)
	{
		steps.add(0, new Step(x, z));
		currentStep = 0;
	}
	
	public static class Step
	{
		public final int x;
		public final int z;
		
		public Step(int x, int z)
		{
			this.x = x;
			this.z = z;
		}
	}
}
