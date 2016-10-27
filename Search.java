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
