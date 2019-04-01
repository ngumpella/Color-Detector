/**
 * This is used in DisjointSets<T> to store actual data in the same sets
 * @author Nikhita Gumpella
 * @version 1.0 November 26th, 2018
 * CS310 Fall 2018
 */

import java.util.AbstractCollection;
import java.util.Iterator;

public class Set<T> extends AbstractCollection<T>
{
   /**
	 * Fields for the linked list set
	 * The list will have a head and a tail
	 * size will also be kept track of
 	*/
	private Node<T> head;
	private Node<T> tail;
	private int size;

   /**
 	* A private Node class for our linked list set
 	*/
	private class Node<T>
	{
	   /**
		* Fields for the Node
		* a next node and a T value to hold the data
 		*/	
		private Node<T> next;
		private T data;

		private Node(T value)
		{
			this.data = value;
			this.next = null;
		}
	}

   /**
	 * Constructor for the linked list set
	 * O(1)
 	*/
	public Set()
	{
		this.head = null;
		this.tail = null;
		this.size = 0;
	}
	
   /**
	 * Adds an item to the end of the set
	 * @param T the item to be added
	 * @return true when successful
	 * O(1)
 	*/
	public boolean add(T item)
	{
		Node<T> node = new Node<T>(item);

		if(size == 0)
		{
			head = node;
			size++;
		}

		else
		{
			tail.next = node;
			size++;
		}
		tail = node;
		return true;
	}
	
   /**
	 * Adds a set to the end of another set
	 * @param Set<T> the set to be added
	 * @return true when successful
	 * O(1)
 	*/
	public boolean addAll(Set<T> other)
	{   
		tail.next = other.head;
		tail = other.tail;
		size += other.size();
		other.clear();
		return true; 
	}
	
   /**
	 * Clears a set
	 * O(1)
 	*/
	public void clear()
	{
		head = null;
		size = 0;
	}
	
   /**
	 * Keeps track of size
	 * @return the size
	 * O(1)
 	*/
	public int size()
	{
		return size;
	}
	
   /**
	 * Iterator that has next() and hasNext()
	 * @return an iterator
	 * O(1)
 	*/
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			Node<T> current = head;
			//O(1)
			public T next()
			{
				T temp = current.data;
				current = current.next;
				return temp;
			}
			
			//O(1)
			public boolean hasNext()
			{
				return current != null; 
			}
		};
	}
	
	//main method just for your testing
	//edit as much as you want
	public static void main(String[] args)
	{
		Set<String> aset = new Set<String>();
		aset.add("back");
		aset.add("to"); 
		aset.add("save");
		aset.add("the");
		aset.add("universe");
		System.out.println("aset: " + aset);
		System.out.println("aset size: " + aset.size() + "\n");

		Set<String> bset = new Set<String>();
		bset.add("hold");
		bset.add("me");
		bset.add("tight");	
		System.out.println("bset: " + bset);
		System.out.println("bset size: " + bset.size() + "\n");

		aset.addAll(bset);
		System.out.println("aset: " + aset);
		System.out.println("aset size: " + aset.size());
		System.out.println("bset: " + bset);
		System.out.println("bset size: "+ bset.size() + "\n");

		aset.clear();
		System.out.println("aset: " + aset);
		System.out.println("aset size: " + aset.size());
	}
}