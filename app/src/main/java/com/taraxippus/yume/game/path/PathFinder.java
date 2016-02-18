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
		
		this.nodes = new Node[level.getWidth() * level.getHeight() * level.getLength()];
		
		for (int i = 0; i < this.nodes.length; ++i)
			this.nodes[i] = new Node(i / (level.getLength() * level.getHeight()), (i / level.getLength()) % level.getHeight(), i % level.getLength());
	}
	
	public Path findPath(IMover mover, float sX, float sY, float sZ, float tX, float tY, float tZ, boolean addFirst)
	{
		return findPath(mover, Math.round(sX), Math.round(sY), Math.round(sZ), Math.round(tX), Math.round(tY), Math.round(tZ), addFirst);
	}
	
	public Path findPath(IMover mover, int sX, int sY, int sZ, int tX, int tY, int tZ, boolean addFirst)
	{
		if (!isValidLocation(mover, sX, sY, sZ, tX, tY, tZ))
			return null;
			
		open.clear();
		closed.clear();
		
		getNode(sX, sY, sZ).cost = 0;
		getNode(sX, sY, sZ).depth = 0;
		getNode(tX, tY, tZ).parent = null;
		
		open.add(getNode(sX, sY, sZ));
		
		Node current, neighbour;
		int x, y, z;
		int nX, nY, nZ;
		float nextStepCost;
		
		int maxDepth = 0;
		while (maxDepth < maxSearchDistance && open.size() != 0)
		{
			current = open.get(0);
			
			if (current == getNode(tX, tY, tZ))
			{
				break;
			}

			open.remove(current);
			closed.add(current);
			
			for (x = -1; x <= 1; x++) 
			{
				for (y = -1; y <= 1; y++) 
				{
					for (z = -1; z <= 1; ++z) 
					{
						if (x == 0 && y == 0 && z == 0) 
							continue;

						if (!allowDiagonalMovement && x != 0 && z != 0) 
							continue;

						nX = current.x + x;
						nY = current.y + y;
						nZ = current.z + z;

						if (isValidLocation(mover, sX, sY, sZ, nX, nY, nZ))
						{
							nextStepCost = current.cost + level.getCost(mover, current.x, current.y, current.z, nX, nY, nZ);
							neighbour = getNode(nX, nY, nZ);
							level.pathFinderVisited(nX, nY, nZ);

							if (nextStepCost < neighbour.cost) 
							{
								open.remove(neighbour);
								closed.remove(neighbour);
							}

							if (!open.contains(neighbour) && !closed.contains(neighbour)) 
							{
								neighbour.cost = nextStepCost;
								neighbour.heuristic = getHeuristicCost(mover, nX, nY, nZ, tX, tY, tZ);

								maxDepth = Math.max(maxDepth, neighbour.setParent(current));

								open.add(neighbour);
								Collections.sort(open);
							}
						}
					}
				}
			}
		}
		
		if (getNode(tX, tY, tZ).parent == null) 
		{
			return null;
		}

		Path path = new Path(level, mover);
		Node target = getNode(tX, tY, tZ);
		
		while (target != getNode(sX, sY, sZ)) 
		{
			path.addStep(target.x, target.y, target.z);
			target = target.parent;
		}
		
		if (addFirst)
			path.addStep(sX, sY, sZ);

		return path;
	}
	
	protected boolean isValidLocation(IMover mover, int sX, int sY, int sZ, int x, int y, int z) 
	{
		boolean invalid = x < 0 || y < 0 || z < 0 || x >= level.getWidth() || y >= level.getHeight() || z >= level.getLength();

		if (!invalid && (sX != x || sY != y || sZ != z)) 
			invalid = level.isBlocked(mover, x, y, z);

		return !invalid;
	}
	
	protected Node getNode(int x, int y, int z)
	{
		return nodes[x * level.getLength() * level.getHeight() + y * level.getLength() + z];
	}
	
	public float getHeuristicCost(IMover mover, int sX, int sY, int sZ, int tX, int tY, int tZ)
	{
		float dX = tX - sX;
		float dY = tY - sY;
		float dZ = tZ - sZ;

		return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
	}
}
