package com.taraxippus.yume.game.path;

import com.taraxippus.yume.game.level.*;
import java.util.*;
import org.w3c.dom.*;

public class PathFinder
{
	public final Level level;
	
	private final ArrayList<Node> closed = new ArrayList<>();
	private final ArrayList<Node> open = new ArrayList<>();
	private final Node[] nodes;
	
	private final int maxSearchDistance;
	private final boolean allowDiagonalMovement;

	public PathFinder(Level level, int maxSearchDistance, boolean allowDiagonalMovement)
	{
		this.level = level;
		this.maxSearchDistance = maxSearchDistance;
		this.allowDiagonalMovement = allowDiagonalMovement;
		
		this.nodes = new Node[level.getWidth() * level.getLength()];
		
		for (int i = 0; i < this.nodes.length; ++i)
			this.nodes[i] = new Node(i / level.getLength(), i % level.getLength());
	}
	
	public Path findPath(IMover mover, float sX, float sZ, float tX, float tZ, boolean addFirst)
	{
		return findPath(mover, Math.round(sX), Math.round(sZ), Math.round(tX), Math.round(tZ), addFirst);
	}
	
	public Path findPath(IMover mover, int sX, int sZ, int tX, int tZ, boolean addFirst)
	{
		if (sX == tX && sZ == tZ || level.isBlocked(mover, tX, tZ))
			return null;
		
		open.clear();
		closed.clear();
		
		getNode(sX, sZ).cost = 0;
		getNode(sX, sZ).depth = 0;
		getNode(tX, tZ).parent = null;
		
		open.add(getNode(sX, sZ));
		
		Node current, neighbour;
		int x, z;
		int nX, nZ;
		float nextStepCost;
		
		int maxDepth = 0;
		while ((maxDepth < maxSearchDistance) && (open.size() != 0))
		{
			current = open.get(0);
			
			if (current == getNode(tX, tZ))
			{
				break;
			}

			open.remove(current);
			closed.add(current);
			
			for (x = -1; x <= 1; x++) 
			{
				for (z = -1; z <= 1; ++z) 
				{
				
					if ((x == 0) && (z == 0)) 
						continue;

					if (!allowDiagonalMovement && x != 0 && z != 0) 
							continue;
						
					nX = current.x + x;
					nZ = current.z + z;

					if (isValidLocation(mover, sX, sZ, nX, nZ))
					{
						nextStepCost = current.cost + level.getCost(mover, current.x, current.z, nX, nZ);
						neighbour = getNode(nX, nZ);
						level.pathFinderVisited(nX, nZ);

						if (nextStepCost < neighbour.cost) 
						{
							open.remove(neighbour);
							closed.remove(neighbour);
						}

						if (!open.contains(neighbour) && !closed.contains(neighbour)) 
						{
							neighbour.cost = nextStepCost;
							neighbour.heuristic = getHeuristicCost(mover, nX, nZ, tX, tZ);
							
							maxDepth = Math.max(maxDepth, neighbour.setParent(current));
							
							open.add(neighbour);
							Collections.sort(open);
						}
					}
				}
			}
		}
		
		if (getNode(tX, tZ).parent == null) 
		{
			return null;
		}

		Path path = new Path();
		Node target = getNode(tX, tZ);
		
		while (target != getNode(sX, sZ)) 
		{
			path.addStep(target.x, target.z);
			target = target.parent;
		}
		if (addFirst)
			path.addStep(sX, sZ);

		return path;
	}
	
	protected boolean isValidLocation(IMover mover, int sX, int sZ, int x, int z) 
	{
		boolean invalid = (x < 0) || (z < 0) || (x >= level.getWidth()) || (z >= level.getLength());

		if ((!invalid) && ((sX != x) || (sZ != z))) 
			invalid = level.isBlocked(mover, x, z);

		return !invalid;
	}
	
	protected Node getNode(int x, int z)
	{
		return nodes[x * level.getLength() + z];
	}
	
	public float getHeuristicCost(IMover mover, int sX, int sZ, int tX, int tZ)
	{
		float dX = tX - sX;
		float dZ = tZ - sZ;

		return (float) Math.sqrt(dX * dX + dZ * dZ);
	}
}
