import java.util.*;
import java.io.*;

public class BFS extends Search
{
	public void search(List<String> lines)
	{
		init(lines);
		
		String algorithm = lines.get(0);
		String start = lines.get(1);
		String destination = lines.get(2);
		
		Deque<Node> queue = new LinkedList<Node>();
		List<Node> visited = new ArrayList<Node>();
		
		Node startNode = new Node(start);
		
		queue.addLast(startNode);
		
		while (!queue.isEmpty())
		{
			Node n = queue.remove();
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
						if(!isVisited(visited, childNode) )
						{	
							if(!queueContains(queue, childNode))
							{	
								queue.addLast(childNode);
							}
							else
							{
								updateQueue(queue, childNode);
							}
						}
					}
				}	
			}
		}	
	}
}
