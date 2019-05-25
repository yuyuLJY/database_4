package database_4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Btree<Key extends Comparable<Key>, Value> 
{
  // max children per B-tree node = M-1
  // (must be even and greater than 2)
	static ArrayList<ArrayList<String>> indexList = new ArrayList<>();// 存放所有state
	static int diskNo = -1;
	static int parentNo = 0;
	//static Map<Integer, String> index = new HashMap<>();
	//static Stack<Integer> parentStack = new Stack<Integer>();
	
  private static final int M = 8;//结点最多可以存3个数据
 
  private Node root;    // root of the B-tree
  private int height;   // height of the B-tree
  private int n;      // number of key-value pairs in the B-tree
 
  // helper B-tree node data type
  //TODO m要来干什么？？
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
 
  //有两类结点，分成内部结点和外部结点
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
 
  /**
   * Initializes an empty B-tree.
   */
  public Btree() 
  {
    root = new Node(0);
  }
 
  /**
   * Returns true if this symbol table is empty.
   * @return {@code true} if this symbol table is empty; {@code false} otherwise
   */
  public boolean isEmpty() 
  {
    return size() == 0;
  }
 
  /**
   * Returns the number of key-value pairs in this symbol table.
   * @return the number of key-value pairs in this symbol table
   */
  public int size() 
  {
    return n;
  }
 
  /**
   * Returns the height of this B-tree (for debugging).
   *
   * @return the height of this B-tree
   */
  public int height() 
  {
    return height;
  }
 
 
  /**
   * Returns the value associated with the given key.
   *
   * @param key the key
   * @return the value associated with the given key if the key is in the symbol table
   *     and {@code null} if the key is not in the symbol table
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public Value get(Key key) 
  {
    if (key == null) 
    {
      throw new NullPointerException("key must not be null");
    }
    return search(root, key, height);
  }
 
  @SuppressWarnings("unchecked")
  private Value search(Node x, Key key, int ht) 
  {
	  //TODO 
    Entry[] children = x.children;
    for(int i=0;i<x.m;i++) {
    	//System.out.println("key value next:"+x.m+" "+children[i].key+" "+children[i].val+" "+children[i].next);
    }
 
    // external node到最底层叶子结点，遍历
    if (ht == 0) 
    {
      for (int j = 0; j < x.m; j++)       
      {
    	  //System.out.println("高度"+ht+" 值："+children[j].key);
        if (eq(key, children[j].key)) 
        {
        	//System.out.printf("找到了\n");	
          return (Value) children[j].val;
        }
      }
    }
 
    // internal node递归查找next地址
    else
    {	//System.out.printf("孩子的个数%d\n",x.m);
      for (int j = 0; j < x.m; j++) 
      {
    	  //System.out.printf("高度 %d 判断1：%d %d\n",ht,j+1,x.m);
    	  //System.out.println("高度 "+ht+" 判断2："+key+" "+children[j+1].key+" "+less(key, children[j+1].key));
        if (j+1 == x.m || less(key, children[j+1].key)){//已经把孩子遍历完了 or 
        	//System.out.println("执行return");
          return search(children[j].next, key, ht-1);
        }
      }
    }
    return null;
  }
 
 
  /**
   * Inserts the key-value pair into the symbol table, overwriting the old value
   * with the new value if the key is already in the symbol table.
   * If the value is {@code null}, this effectively deletes the key from the symbol table.
   *
   * @param key the key
   * @param val the value
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public void put(Key key, Value val) 
  {
    if (key == null) 
    {
      throw new NullPointerException("key must not be null");
    }
    Node u = insert(root, key, val, height); //分裂后生成的右结点
    n++;
    if (u == null) 
    {
      return;
    }
 
    // need to split root重组root
    Node t = new Node(2);
    t.children[0] = new Entry(root.children[0].key, null, root);
    t.children[1] = new Entry(u.children[0].key, null, u);
    root = t;
    height++;
  }
 
  private Node insert(Node h, Key key, Value val, int ht) 
  {
    int j;
    Entry t = new Entry(key, val, null);
 
    // external node外部结点，也是叶子结点，在树的最底层，存的是内容value
    if (ht == 0) 
    {
      for (j = 0; j < h.m; j++) 
      {
        if (less(key, h.children[j].key)) 
        {
          break;
        }
      }
    }
 
    // internal node内部结点，存的是next地址
    else
    {
      for (j = 0; j < h.m; j++) 
      {
        if ((j+1 == h.m) || less(key, h.children[j+1].key)) 
        {
          Node u = insert(h.children[j++].next, key, val, ht-1);
          if (u == null) 
          {
            return null;
          }
          t.key = u.children[0].key;
          t.next = u;
          break;
        }
      }
    }
 
    for (int i = h.m; i > j; i--)
    {
      h.children[i] = h.children[i-1];
    }
    h.children[j] = t;
    h.m++;
    if (h.m < M) 
    {
      return null;
    }
    else    
    {  //分裂结点
      return split(h);
    }
  }
 
  // split node in half
  private Node split(Node h) 
  {
    Node t = new Node(M/2);
    h.m = M/2;
    for (int j = 0; j < M/2; j++)
    {
      t.children[j] = h.children[M/2+j];     
    }
    return t;  
  }
 
  /**
   * Returns a string representation of this B-tree (for debugging).
   *
   * @return a string representation of this B-tree.
   */
  public String toString() 
  {
    try {
		return toString(root, height, "") + "\n";
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
  }
 
  private String toString(Node h, int ht, String indent) throws IOException 
  {
    StringBuilder s = new StringBuilder();
    Entry[] children = h.children;
 
    if (ht == 0) {//叶子结点，没有括号
    	
    	ArrayList<String> oneList = new ArrayList<>();
    	ArrayList<String> copyDataList = new ArrayList<>();
    	diskNo++;
        //int parent = parentStack.get(parentStack.size()-1);//
        //System.out.println("叶子结点-父亲是："+parent);
        //String smallIndex = index.get(parent);
        //int addNewIndex = diskNo;
        //index.put(parent,smallIndex+String.valueOf(addNewIndex));       
      for (int j = 0; j < h.m; j++) 
      {
    	//h.m 叶子结点有多少个量
        s.append(indent + children[j].key + " " + children[j].val + "\n");
        oneList.add(String.valueOf(children[j].key));
        copyDataList.add(String.valueOf(children[j].key));
        copyDataList.add(String.valueOf(children[j].val));
      }
      treeBlockWriteTodisk(0,copyDataList,indexList.size());//把数据写进磁盘
      indexList.add(oneList);
    }
    else{//中间结点，有括号
     //重新申请一个list
    	//TODO 
       
       ArrayList<String> oneList = new ArrayList<>();
       diskNo++;//这个新申请的块的块号
       //index.put(diskNo, "");
       //parentStack.push(diskNo);
       System.out.println("有几个儿子："+h.m+"第几层 :"+indexList.size());
       for(int j = 1; j < h.m; j++) {//先把自己的这一层弄清楚
    	   oneList.add(String.valueOf(children[j].key));
       }
       //
       treeBlockWriteTodisk(1,oneList,indexList.size());
       indexList.add(oneList);
       /*
       //完成填写index
       if(diskNo!=0) {
       int parent = parentStack.get(parentStack.size()-2);//从顶开始数的第二个
       System.out.println("非叶子节点-父亲是："+parent);
       String smallIndex = index.get(parent);
       int addNewIndex = parentStack.get(parentStack.size()-1);
       System.out.println("非叶子节点-当前节点是："+addNewIndex);
       index.put(parent,smallIndex+String.valueOf(addNewIndex));
       }*/
      for (int j = 0; j < h.m; j++) 
      {
        if (j > 0) 
        {
          s.append(indent + "(" + children[j].key + ")\n");
        }
        //System.out.println("---"+children[j].key);
        s.append(toString(children[j].next, ht-1, indent + "   "));
        //返回了上一级
        //parentStack.pop();
      }
      
    }
    return s.toString();
  }
 
  //把文件写进磁盘
  static void treeBlockWriteTodisk(int choice,ArrayList<String> List,int order) throws IOException{
	  File fout = new File("src/disk/BTreeFind1/"+String.valueOf(order)+".txt");
	  if(choice==0) {//叶子结点
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(fout);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				 
				//0 表示的是元组 1 表示的是后继的地址
				//0 12 34
				for (int i = 0; i <List.size(); i++) {
					for(int j=0;j<4;j++) {
						String s = String.valueOf(List.get(i));
						bw.write(s);
						bw.newLine();
					}
				}
				for(int k=List.size()*4;k<65;k++) {
					String s = "0";
					bw.write(s);
					bw.newLine();
				}
				bw.close();
			} catch (FileNotFoundException e) {
				System.out.println("Writing Block Failed!\n");
				e.printStackTrace();
			}
	  }else {//非叶子结点
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(fout);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				 
				//0 表示的是元组 1 表示的是后继的地址
				//0 12 34
				for (int i = 0; i <List.size(); i++) {
					String s = String.valueOf(List.get(i));
					bw.write(s);
					bw.newLine();
				}
				for(int k=List.size();k<65;k++) {
					String s = "0";
					bw.write(s);
					bw.newLine();
				}
				bw.close();
			} catch (FileNotFoundException e) {
				System.out.println("Writing Block Failed!\n");
				e.printStackTrace();
			}
	  }
  }
 
  // comparison functions - make Comparable instead of Key to avoid casts
  private boolean less(Comparable k1, Comparable k2) 
  {
    return k1.compareTo(k2) < 0;
  }
 
  private boolean eq(Comparable k1, Comparable k2) 
  {
    return k1.compareTo(k2) == 0;
  }
 
 
  /**
   * Unit tests the {@code BTree} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) 
  {
    
	Btree<Integer, Integer> st = new Btree<Integer, Integer>();
    //插入数据，从文件中读取R表的第一行(即A的值)
	int valueA;
	String fileName = "src/table/Rtable.txt";
	try {
		FileReader fr = new FileReader(fileName);
		BufferedReader bf = new BufferedReader(fr);
		String str;
		// 按行读取字符串
		int i = 0;
		while ((str = bf.readLine()) != null) {
			String s[] = str.split(" ");
			//valueA = Integer.valueOf(s[0]);
			st.put(Integer.valueOf(s[0]),Integer.valueOf(s[1]));
		}
		bf.close();
		fr.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
  
    System.out.println(st);
    
    System.out.println("indexList长度Main"+indexList.size());
    for(int i =0;i<indexList.size();i++) {
    	System.out.println("......"+i+"......");
    	ArrayList<String> oneList = indexList.get(i);
    	for(String s :oneList) {
    		System.out.println(s);
    	}
    	System.out.println("............");
    	System.out.println();
    }
    /*
    System.out.println("-------------------------index------------------------");
    for(Integer key :index.keySet()) {
    	System.out.println("key:"+key+" value: "+index.get(key));
    }
    System.out.println();
    */
  }
}

