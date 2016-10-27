import java.util.*;
import java.io.*;

public class UCS extends Search
{
	public void search(List<String> lines)
	{
		init(lines);
		
		String algorithm = lines.get(0);
		String start = lines.get(1);
		String destination = lines.get(2);
		int noLiveTraffic = Integer.parseInt(lines.get(3));
		
		Node startNode = new Node(start);
		
		Comparator<Node> comparator = new UCSComparator();
		PriorityQueue<Node> pqueue = new PriorityQueue<Node>(noLiveTraffic, comparator);
		List<Node> visited = new ArrayList<Node>();
		pqueue.add(startNode);
		
		while (!pqueue.isEmpty())
		{
			Node n = pqueue.remove();
			visited.add(n);
			
			if(destination.equals(n.state))
			{
				System.out.println("The solution is...\n");
				printSolution(n, start);
				break; // We found the solution. So time to exit!!!!
			}
			
			else
			{
				List<String> successors = getChildStates(n.state);
				if(successors != null)
				{
					for(String successor: successors)
					{
						int c = n.cost + (isUnitCost(algorithm) ? 1 : getCost(n.state, successor));
						Node childNode = new Node(successor, n, c);	
						if(!isVisited(visited, childNode))	// Avoid loops!
						{
							if(isStateExists(pqueue, successor))	// Replace the node in case we get a shorter path to that node
							{
								updateQueue(pqueue, childNode);
							}
							else
							{
								pqueue.add(childNode);
							}
						}
					}
				}
			}
		}	
	}
}

class UCSComparator implements Comparator<Node>
{
	@Override
	public int compare(Node n1, Node n2)
	{
		if(n1.cost != n2.cost)
		{
			return n1.cost - n2.cost;
		}
		else
		{
			long res = n1.ts - n2.ts;
			return res > 0 ? 1 : -1;
		}	
	}
}