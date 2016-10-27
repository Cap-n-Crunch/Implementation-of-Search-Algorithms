/*
Implementation of various search algorithms.
*/
import java.util.*;
import java.io.*;

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