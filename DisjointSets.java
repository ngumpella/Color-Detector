/**
 * Disjoint Sets class, using union by size and path compression
 * @author Nikhita Gumpella
 * @version 1.0 November 30th, 2018
 * CS310 Fall 2018
 */

import java.util.ArrayList; 

public class DisjointSets<T>
{
	private int[] s; //the sets
	private ArrayList<Set<T>> sets; //the actual data for the sets

   /**
	 * Constructor for DisjointSets
	 * @param ArrayList<T> 
	 */
	public DisjointSets(ArrayList<T> data)
	{
		s = new int[data.size()];
		sets = new ArrayList<Set<T>>();

		for(int i = 0; i < data.size(); i++)
		{
			//filling in s
			s[i] = -1;

			//filling in sets
			Set<T> temp = new Set<T>();
			if(data.get(i) != null)
			{
				temp.add(data.get(i));
				sets.add(temp);
			}
		}
	}

   /**
	 * Computes the union of two sets using rank union by size
	 * If two sets are equal, root1 is the new root
	 * @param int root1 and root2, the roots of the sets to be unioned
	 * @throws IllegalArgumentException if there are non-roots provided
	 * @return the new root of the unioned set
	 * O(1)
 	*/	
	public int union(int root1, int root2)
	{
		//making two sets with the given roots
		Set<T> set1 = get(root1);
		Set<T> set2 = get(root2);

		if(root1 < 0 || root1 >= s.length || root2 < 0 || root2 >= s.length)
		{
			throw new IllegalArgumentException();
		}

		if(s[root1] >= 0 || s[root2] >= 0)
		{
			throw new IllegalArgumentException();
		}

		if(root1 == root2)
		{
			return root1;
		}

		else if(root1 < root2)
		{
			set1.addAll(set2);
			s[root1] += s[root2];
			s[root2] = root1;
			return root1;
		} 

		else
		{
			set2.addAll(set1);
			s[root2] += s[root1];
			s[root1] = root2;
			return root2;
		}
	}

   /**
	 * Finds and returns the root
	 * Must implement path compression
	 * I got this from the textbook!
	 * @param int x, looking for x's root
	 * @return the root
	 * O(1)
 	*/	
	public int find(int x)
	{
		if(x < 0 || x >= s.length )
		{
			throw new IllegalArgumentException();
		}

		if(s[x] < 0)
		{
			return x;
		}
		else
		{
			return find(s[x]);
		}
	}

   /**
	 * Gets all the data in the same set
	 * @param int the root we want all the data from
	 * @return the set connected to root
	 * O(1)
 	*/	
	public Set<T> get(int root)
	{
		return sets.get(root);
	}
	
	//main method just for your testing
	//edit as much as you want
	public static void main(String[] args)
	{
		ArrayList<Integer> arr = new ArrayList<>();
		for(int i = 0; i < 10; i++)
			arr.add(i);
		
		DisjointSets<Integer> ds = new DisjointSets<>(arr);
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 1
		System.out.println(ds.union(0, 1)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 0
		System.out.println("-----");
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 2
		System.out.println(ds.union(0, 2)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 0
		System.out.println("-----");
		System.out.println(ds.get(0)); //should be [0, 1, 2]
		System.out.println(ds.get(1)); //should be []
		System.out.println(ds.get(3)); //should be [3]
	}
}