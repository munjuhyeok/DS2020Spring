import java.io.*;
import java.util.*;

public class Subway {
	/**
	 * list of station codes for given station name
	 */
	static HashMap<String, ArrayList<String>> code_list = new HashMap<>();
	/**
	 * get station name for given station code
	 */
	static HashMap<String, String> station_name = new HashMap<>();	
	/**
	 * Adjacency List
	 * station with same name and different line number is considered as distinct node with weight 5
	 * AdjList.get(start_node).get(end_node) = weight
	 * (nodes are represented by its code)
	 */
	static HashMap<String, HashMap<String, Integer>> AdjList= new HashMap<>();
	
	public static void main(String args[]) {
		load_data(args[0]);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0)
					break;
				
				command(input);
			}
			catch (IOException e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}
	
	static void command(String input) {
		String [] temp = input.split(" ");
		String start_node = temp[0];
		String end_node = temp[1];
		System.out.println(dijkstra(start_node, end_node));
	}

	/**
	 * return path and weight sum using dijkstra
	 */
	static String dijkstra(String start_node, String end_node) {
		ArrayList<String> mark = new ArrayList<>(); //add station code to mark if explored
		HashMap<String, Integer> distance = new HashMap<>();
		HashMap<String, String> parent = new HashMap<>(); //get parents(in station name) for given station code
		ArrayList<String> start_nodes = code_list.get(start_node); //get list of station code for given station name(it can be transfer station)
		for(String v : start_nodes) {
			mark.add(v);
			for(String u : AdjList.get(v).keySet()) {
				if(start_nodes.contains(u)) continue;
				if (!distance.containsKey(u)) {
					distance.put(u, AdjList.get(v).get(u));
					parent.put(u, v);
				} else if (distance.get(u) > distance.get(v) + AdjList.get(v).get(u)) {
					distance.put(u, AdjList.get(v).get(u));
					parent.put(u, v);
				}
			}
		}

		while(true) { //retainAll로 바꾸기
			String v = null;
			int min_dist = Integer.MAX_VALUE;
			
			for(String u : distance.keySet()) { //find the smallest distance node v s.t. v is not marked
				if (mark.contains(u)) continue;
				int dist = distance.get(u);
				if (dist < min_dist) {
					min_dist = dist;
					v = u;
				}				
			}
			mark.add(v); //mark v
			
			//Node<String> V = new Node<>(v,);//
			
			if(!AdjList.containsKey(v)) continue;
			for(String u : AdjList.get(v).keySet()) { 
				if (mark.contains(u)) continue;
				if (!distance.containsKey(u)) {
					distance.put(u, distance.get(v) + AdjList.get(v).get(u));
					parent.put(u, v);
				} else if (distance.get(u) > distance.get(v) + AdjList.get(v).get(u)) {
					distance.put(u, distance.get(v) + AdjList.get(v).get(u));
					parent.put(u, v);
				}
			}
			if(code_list.get(end_node).contains(v)) return Parents(v, parent) + "\n" + Integer.toString(distance.get(v));
		}
	}
	
	
	/**
	 * get list of station passed through
	 */
	static String Parents(String node, HashMap<String, String> parent) {
		String result = station_name.get(node);
		String temp;
		while(parent.containsKey(node)) {
			temp = node;
			node = parent.get(node);
			String temp_name = station_name.get(temp);
			String node_name = station_name.get(node);
			if(temp_name.contentEquals(node_name)) {
				result = "[" + node_name + "]" + result.substring(node_name.length());
				continue;
			}
			result = node_name + " " + result;
		}
		return result;
	}
	
	/**
	 * load data and build AdjList
	 */
	static void load_data(String filename) {
		try {
			File file = new File(filename);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null){ //building vertex
				if (line.equals("")) break;
				add_node(line);
			}
			while((line = br.readLine()) != null){ //building edge
				connect_node(line);
			}
			br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	/**
	 * build nodes and connect transfer station
	 */
	static void add_node(String line) {
		String[] temp = line.split(" ");
		String code = temp[0];
		String name = temp[1];
		if (code_list.containsKey(name)) { //check if it's transfer station
			AdjList.put(code, new HashMap<String, Integer>());
			 //connect node with same station name and different station code(transfer station) with weight 5 edge
			for(String other_code : code_list.get(name)) {
				AdjList.get(code).put(other_code, 5);
				AdjList.get(other_code).put(code, 5);
			}
			code_list.get(name).add(code);
			station_name.put(code, name);
		}else
		{
			code_list.put(name, new ArrayList<String>(Arrays.asList(code)));
			station_name.put(code, name);
			AdjList.put(code, new HashMap<String, Integer>());
		}
		
	}

	/**
	 * build edge(connect nodes)
	 */
	static void connect_node(String line) {
		String[] temp = line.split(" ");
		String from = temp[0];
		String to = temp[1];
		int weight = Integer.parseInt(temp[2]);
		AdjList.get(from).put(to, weight);
	}
}