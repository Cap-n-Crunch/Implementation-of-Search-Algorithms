public class Node
{
	String state;
	Node parent;
	int cost;
	long ts;
	
	public Node(String state)
	{
		this.state = state;
		this.parent = null;
		this.cost = 0;
		this.ts = System.nanoTime();
	}
	public Node(String state, Node parent, int cost)
	{
		this.state = state;
		this.parent = parent;
		this.cost = cost;
		this.ts = System.nanoTime();
	}
	
	public void updateTS()
	{
		this.ts = System.nanoTime();
	}
	
	public boolean equals(Node n1) 
	{
		if(this.parent == null && n1.parent == null)
		{
			return ((this.state.equals(n1.state)) && (this.cost == n1.cost));
		}
		else if(this.parent == null)
		{
			return ((this.state.equals(n1.state)) && (n1.parent == null) && (this.cost == n1.cost));
		}
		else if(n1.parent == null)
		{
			return ((this.state.equals(n1.state)) && (this.parent == null) && (this.cost == n1.cost));
		}
		else
			return ((this.state.equals(n1.state)) && (this.parent.equals(n1.parent)) && (this.cost == n1.cost));
	}
	
	@Override
	public String toString()
	{
		if(parent != null)
			return state + " " + parent.state + " " + cost + " " + ts;
		return state + " " + parent + " " + cost + " " + ts;
	}
}