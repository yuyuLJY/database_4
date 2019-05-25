package database_4;


public class NewBTree<Key extends Comparable<Key>, Value> {
	  //要实现了comparable接口，才能进行比较
	  private static final int M = 5;

	  private Node root;    // B-树的树根
	  private int height;   // height of the B-tree
	  private int n;      // number of key-value pairs in the B-tree？？
	  
	  //TODO m要来有什么用
	  private static final class Node 
	  {
	    private int m;               // number of children
	    private Entry[] children = new Entry[M];  // the array of children
	 
	    // create a node with k children
	    private Node(int k) 
	    {
	      m = k;
	    }
	  }
	  
	  // internal nodes: only use key and next
	  // external nodes: only use key and value
	  private static class Entry 
	  {
	    private Comparable key;
	    private Object val;
	    private Node next;   // helper field to iterate over array entries
	    public Entry(Comparable key, Object val, Node next) 
	    {
	      this.key = key;
	      this.val = val;
	      this.next = next;
	    }
	  }
	  
	  
	  
	  
	  public static void main(String[] args) {
		  
	  }
}
