/*
Implementation of various search algorithms.
*/
import java.util.*;
import java.io.*;

abstract class Search
{
	private static HashMap<String, List<String>> successorOrder = new HashMap<String, List<String>>();
	private static HashMap<String, Integer> heuristic = new HashMap<String, Integer>();
	private static HashMap<String, HashMap<String, Integer>> costs = new HashMap<String, HashMap<String, Integer>>();
	
	public abstract void search(List<String> lines);
	
	private static void addSuccessor(String parent, String child)
	{
		List<String> clist = null;
		if(!successorOrder.containsKey(parent))
		{
			clist = new ArrayList<String>();
		}
		else
		{
			clist = successorOrder.get(parent);
		}
		if(!clist.contains(child))
		{	
			clist.add(child);
		}
		successorOrder.put(parent, clist);
	}
	
	private static void addCost(String parent, String child, Integer cost)
	{
		HashMap<String, Integer> hmap = null;
		if(!costs.containsKey(parent))
		{
			hmap = new HashMap<String, Integer>();
		}
		else
		{
			hmap = costs.get(parent);
		}
		if(!(hmap.containsKey(child)))
		{	
			hmap.put(child, cost);
		}
		costs.put(parent, hmap);
	}
	
	private static void addHeuristic(String str, Integer i)
	{
		heuristic.put(str, i);
	}
	
	public static List<String> getChildStates(String parent)
	{
		List<String> clist = null;
		if(successorOrder.containsKey(parent))
		{
			clist = successorOrder.get(parent);
		}
		return clist;
	}
	
	public static Integer getCost(String parent, String child)
	{
		if(costs.containsKey(parent))
		{
			HashMap<String, Integer> hmap = costs.get(parent);
			if(hmap.containsKey(child))
			{
				return hmap.get(child);
			}
		}
		return Integer.MAX_VALUE;
	}
	
	public static Integer getHeuristic(String str)
	{
		return heuristic.get(str);
	}
	
	public static void printSuccessorOrder()
	{
		System.out.println(successorOrder);
	}
	
	public static void printCosts()
	{
		System.out.println(costs);
	}
	
	public static void printHeuristics()
	{
		for(String s: heuristic.keySet())
		{
			System.out.println(s + ":" + getHeuristic(s));
		}
	}
	
	public void populateSuccessors(List<String> lines)
	{
		int noLiveTraffic = Integer.parseInt(lines.get(3));
		for(int i = 4; i < 4 + noLiveTraffic; ++i)
		{
			String[] info = lines.get(i).split(" ");
			addSuccessor(info[0], info[1]);
			addCost(info[0], info[1], Integer.parseInt(info[2]));
		}
		//printSuccessorOrder();
		//printCosts();
	}
	
	public void populateHeuristics(List<String> lines)
	{
		int noLiveTraffic = Integer.parseInt(lines.get(3));
		int noSundayTraffic = Integer.parseInt(lines.get(4 + noLiveTraffic));
		for(int i = 5 + noLiveTraffic; i < 5 + noLiveTraffic + noSundayTraffic; ++i)
		{
			String[] info = lines.get(i).split(" ");
			addHeuristic(info[0], Integer.parseInt(info[1])); // this is the parent of the current node
		}
		//printHeuristics();
	}
	
	public void init(List<String> lines)
	{
		populateSuccessors(lines);
		populateHeuristics(lines);	
	}
	
	public static List<Node> getPath(Node goal, String start)
	{
		List<Node> path = new ArrayList<Node>();
		while(!goal.state.equals(start))
		{
			path.add(0, goal);
			goal = goal.parent;
		}
		path.add(0, goal); // Add the root too!
		return path;
	}
	
	public void printSolution(Node goal, String start)
	{
		List<Node> path = getPath(goal, start);
		for(Node n: path)
		{
			System.out.println(n.state + " " + n.cost);
		}
		writeOutput(path);
	}
	
	public void writeOutput(List<Node> path)
	{
		Writer writer = null;
		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt")));
			for(Node n: path)
			{
				writer.write(n.state + " " + n.cost + System.lineSeparator());
			}
		} 
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}
	}
	
	public boolean isVisited(List<Node> visited, Node node)
	{
		Iterator<Node> it = visited.iterator();
		while(it.hasNext())
		{
			Node n = it.next();
			if(node.state.equals(n.state))
			{
				if(n.cost <= node.cost)
					return true;
				/*else
					return false;*/
			}
		}
		return false; 
	}
	
	public boolean queueContains(Queue<Node> q, Node node)
	{
		Iterator<Node> it = q.iterator();
		while(it.hasNext())
		{
			Node n = it.next();
			if(node.state.equals(n.state))
			{
				return true;
			}
		}
		return false; 
	}
	
	public void updateQueue(Queue<Node> pq, Node n)
	{
		Node toReplace = null;
		boolean isReplace = false;
		Iterator<Node> it = pq.iterator();
		
		while(it.hasNext())
		{
			toReplace = it.next();
			if(n.state.equals(toReplace.state))
			{
				if(n.cost < toReplace.cost)
				{
					isReplace = true;
					break;
				}
			}
		}
		if(isReplace)
		{
			pq.remove(toReplace);
			n.updateTS();
			pq.add(n);
		}
	}
	
	public boolean isUnitCost(String algorithm)
	{
		List<String> unitCostAlgorithms = new ArrayList<String>();
		unitCostAlgorithms.add("BFS");
		unitCostAlgorithms.add("DFS");
		
		if(unitCostAlgorithms.contains(algorithm))
			return true;
		
		return false;
	}
	
	public void printQueue(Queue<Node> pq)
	{
		System.out.println("Contents of the Priority Queue are: ");
		Iterator<Node> it = pq.iterator();
		while(it.hasNext())
		{
			System.out.println(it.next());
		}
	}
	
	public boolean isStateExists(Queue<Node> pq, String state)
	{
		Iterator<Node> it = pq.iterator();
		while(it.hasNext())
		{
			Node cur = it.next();
			if(state.equals(cur.state))
				return true;
		}
		return false;
	}
}

class SearchFactory
{
	public static Search getSearch(String algorithm)
	{
		Search search = null;
		switch(algorithm)
		{
			case "BFS":
				search = new BFS();
				break;
			
			case "DFS":
				search = new DFS();
				break;
				
			case "UCS":
				search = new UCS();
				break;
				
			case "A*":
				search = new AStar();
				break;
				
			default:
				throw new IllegalArgumentException("Invalid Algorithm");
		}
		
		return search;
	}
}

class BFS extends Search
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

class DFS extends Search
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
		
		queue.addFirst(startNode);
		
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
					Collections.reverse(successors);
					for(String successor: successors)
					{
						int c = n.cost + (isUnitCost(algorithm) ? 1 : getCost(n.state, successor));
						Node childNode = new Node(successor, n, c);
						if(!isVisited(visited, childNode) )
						{	
							if(!queueContains(queue, childNode))
							{	
								queue.addFirst(childNode);
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


class UCS extends Search
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

class AStar extends Search
{
	public void search(List<String> lines)
	{
		init(lines);
		
		String algorithm = lines.get(0);
		String start = lines.get(1);
		String destination = lines.get(2);
		int noLiveTraffic = Integer.parseInt(lines.get(3));
		
		Node startNode = new Node(start);
		
		Comparator<Node> comparator = new AStarComparator();
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
	
public class MainClass
{
	static String fileName = "input.txt";
	
	public List<String> getInput(String fileName)
	{
		BufferedReader br = null;
		List<String> lines = new ArrayList<String>();
		
		try
		{
			br = new BufferedReader(new FileReader(fileName));
			
			String line = br.readLine(); // Read the first line!
			while(line != null)
			{
				lines.add(line.trim());
				line = br.readLine();
			}
			//System.out.println(lines.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return lines;
	}
	
	public void parseInput(List<String> lines)
	{
		String algo = lines.get(0);
		Search s = SearchFactory.getSearch(algo);
		s.search(lines);
	}
	
	public static void main(String[] args)
	{
		MainClass obj = new MainClass();
		obj.parseInput(obj.getInput(fileName));
	}
}

class Node
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

class AStarComparator implements Comparator<Node>
{
	@Override
	public int compare(Node n1, Node n2)
	{
		int x = (Search.getHeuristic(n1.state) + n1.cost);
		int y = (Search.getHeuristic(n2.state) + n2.cost);
		if(x != y)
		{
			return x - y;
		}
		else
		{
			long res = n1.ts - n2.ts;
			return res > 0 ? 1 : -1;
		}
	}
}