package com.taraxippus.yume.game.path;

public class Node implements Comparable<Node>
{
	final int x;
	final int z;
	
	Node parent;
	int depth;
	float cost;
	float heuristic;
	
	public Node(int x, int z)
	{
		this.x = x;
		this.z = z;
	}
	
	public int setParent(Node parent)
	{
		depth = parent.depth + 1;
		this.parent = parent;

		return depth;
	}
	
	public int compareTo(Node o) 
	{
		return (int) (heuristic + cost - (o.heuristic + o.cost));
	}
}
