import java.io.*;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Matching
{
	public static void main(String args[])
	{
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

	private static void command(String input)
	{
		if (input.matches("\\@\\s.*")) print_slot(Integer.parseInt(input.substring(2)));
		else if (input.matches("\\?\\s.*")) search_pattern(input.substring(2));
		else load_txt(input.substring(2));
	}
	
	private static Hashtable<Integer,AVL_Tree<String, ArrayList<Integer>>> hashtable = new Hashtable<>(100);
	
	private static int get_hash(String input) 
	{
		int hash = 0;
		for(int i = 0; i < input.length(); i++) hash += input.charAt(i);
		return hash % 100;
	}
	
	
	/**
	 * print strings in given hash table slot
	 */
	private static void print_slot(int index_number)
	{
		if(hashtable.containsKey(index_number)) {
			hashtable.get(index_number).traversal();;
		}else {
			System.out.println("EMPTY");
		}
	}
	

	/**
	 * search pattern and print its locations
	 */
	private static void search_pattern(String pattern)
	{
		String subpattern = pattern.substring(0, 6);
		LinkedList<ArrayList<Integer>> locations = new LinkedList<>();
		int remainder = (pattern.length()-1) % 6 + 1; //
		if(hashtable.containsKey(get_hash(subpattern))) {
			locations = (LinkedList<ArrayList<Integer>>)hashtable.get(get_hash(subpattern)).search(subpattern).clone(); //locations containing first 6 letters
		}
		Iterator<ArrayList<Integer>> iterator = locations.iterator();
		for(int i = 0; i < (pattern.length()-remainder)/6; i++) {
			subpattern = pattern.substring(6*i + remainder, 6*(i+1) +remainder);
			LinkedList<ArrayList<Integer>> templist;
			if(hashtable.containsKey(get_hash(subpattern))) {
				templist = hashtable.get(get_hash(subpattern)).search(subpattern); //locations containing subpattern
			}else {
				System.out.println("(0, 0)");
				return;
			}
			ArrayList<Integer> temp;
			while(iterator.hasNext()) {
				temp = (ArrayList<Integer>)iterator.next().clone();
				temp.set(1, 6 * i + temp.get(1) + remainder); //check intersection
				if(!templist.contains(temp)) iterator.remove(); //remove if don't intersect
			}
			iterator = locations.iterator();
		}
		
		//no such pattern
		if(locations.size() == 0) {
			System.out.println("(0, 0)");
			return;
		}
		
		ArrayList<Integer> location = iterator.next();
		//print locations (a1, b1) (a2, b) ....... (an, bn) with such pattern
		System.out.print("(" + location.get(0) +", " + location.get(1) +")" );
		while(iterator.hasNext()) {
			location = iterator.next();
			System.out.print(" (" + location.get(0)+", " + location.get(1) +")" );
		}
		System.out.println();
	}
	
	/**
	 * load data
	 */
	private static void load_txt(String filename)
	{
		try {
			File file = new File(filename);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			hashtable = new Hashtable<>(100);
			int count = 1;
			while((line = br.readLine()) != null){
				for(int i = 0; i < line.length()-5; i++) {
					ArrayList<Integer> item = new ArrayList<>(Arrays.asList(count, i+1));
					String substring = line.substring(i, i+6);
					if(hashtable.containsKey(get_hash(substring))) {
						hashtable.get(get_hash(substring)).insert(substring, item);
					}else {
						hashtable.put(get_hash(substring), new AVL_Tree<>(substring, item));
					}
				}
				count ++;
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
}

/**
 * In this case, S is 6 digit string, T is ArrayList [a, b] which indicates string's location
 */
class AVL_Node<S,T>
{
	private S key;
    private LinkedList<T> item;
    private AVL_Node<S,T> left;
    private AVL_Node<S,T> right;
    private int height;
    
    public AVL_Node(S key, T item) {
        this.key = key;
        this.item = new LinkedList<T>();
        this.item.add(item);
        this.height = 1;
    }
    
    public final S getKey() {
    	return key;
    }  
    public final void setKey(S key) {
    	this.key = key;
    }
    
    public final LinkedList<T> getItem() {
    	return item;
    }  
    public final void setItem(LinkedList<T> item) {
    	this.item = item;
    }
    
    public final void setLeft(AVL_Node<S,T> left) {
    	this.left = left;
    }   
    public final AVL_Node<S,T> getLeft() {
    	return this.left;
    }
    
    public final void setRight(AVL_Node<S,T> right) {
    	this.right = right;
    }    
    public final AVL_Node<S,T> getRight() {
    	return this.right;
    }
    
    public final int getHeight() {
    	return height;
    }   
    public final void setHeight(int height) {
    	this.height = height;
    }
}

class AVL_Tree<S extends Comparable<S>,T>
{
	AVL_Node<S,T> root;
	
	public AVL_Tree(S key, T item) {
		root = new AVL_Node<S, T>(key, item);
	}
	
	public void insert(S key, T item) {
		root = insert(root, key, item);
	}
	
	private AVL_Node<S,T> insert(AVL_Node<S,T> node, S key, T item) {
		if(node == null) {;
			return new AVL_Node<S,T>(key, item);
		}
		if(key.compareTo(node.getKey()) == 0) {
			node.getItem().add(item);
		}else if(key.compareTo(node.getKey())<0) {
			node.setLeft(insert(node.getLeft(), key, item));
			update_height(node);
			if(height(node.getLeft()) - height(node.getRight()) == 2) {
				if(height(node.getLeft().getRight())-height(node.getLeft().getLeft())==1){
					node.setLeft(Lrotate(node.getLeft()));
					node = Rrotate(node);
				}else {
					node = Rrotate(node);
				}
			}
		}else if(key.compareTo(node.getKey())>0) {
			node.setRight(insert(node.getRight(), key, item));
			update_height(node);
			if(height(node.getRight()) - height(node.getLeft()) == 2) {
				if(height(node.getRight().getLeft())-height(node.getRight().getRight())==1){
					node.setRight(Rrotate(node.getRight()));
					node = Lrotate(node);
				}else {
					node = Lrotate(node);
				}
			}
		}
		return node;
	}
	
	private AVL_Node<S, T> Lrotate(AVL_Node<S, T> node){
		AVL_Node<S, T> newnode = node.getRight();
		node.setRight(newnode.getLeft());
		newnode.setLeft(node);
		update_height(node);
		update_height(newnode);
		return newnode;
	}
	
	private AVL_Node<S, T> Rrotate(AVL_Node<S, T> node){
		AVL_Node<S, T> newnode = node.getLeft();
		node.setLeft(newnode.getRight());
		newnode.setRight(node);		
		update_height(node);
		update_height(newnode);
		return newnode;
	}
	
	public void traversal() {
		String result = traversal(root);
		System.out.println(result.substring(0, result.length()-1));
	}
	
	private void update_height(AVL_Node<S, T> node) {
		int newheight = height(node.getLeft()) > height(node.getRight()) ?  height(node.getLeft())+1:height(node.getRight())+1;
		node.setHeight(newheight);
	}
	
	private String traversal(AVL_Node<S,T> node) {
		if(node == null) return "";
		String result = node.getKey() + " "+ traversal(node.getLeft()) + traversal(node.getRight());
		return result;
	}
	
	public LinkedList<T> search(S key){
		return search(root, key);
	}
	
	private LinkedList<T> search(AVL_Node<S,T> node, S key){
		if(node == null) return new LinkedList<T>();
		if(key.compareTo(node.getKey())>0) {
			return search(node.getRight(), key);
		}else if(key.compareTo(node.getKey())<0){
			return search(node.getLeft(), key);
		}
		return node.getItem();
	}
	
	private int height(AVL_Node<S, T> node) {
	if(node ==null) return 0;
	return node.getHeight();
	}
}
