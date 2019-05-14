package database_4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class extmem {
	static int BLOCK_AVAILABLE = 0;
	static int BLOCK_UNAVAILABLE = 1;
	public int numIO; /* Number of IO's*/
	public int bufSize; /* Buffer size*/
	public int blkSize; /* Block size */
	public int numAllBlk; /* Number of blocks that can be kept in the buffer */
	public int numFreeBlk; /* Number of available blocks in the buffer */
	public int data[][];//public int data; /* Starting address of the buffer */
	
	public int[][] initBuffer(int bufsize, int blksize) {
		/**No.1
		 * 初始化缓冲区，其输入参数
		 * @param bufSize为缓冲区大小（单位：字节）
		 * @param blkSize为块的大小（单位：字节）
		 * @return  若缓冲区初始化成功，则该函数返回指向该缓冲区的地址；否则，返回NULL。
		 */
		numIO = 0;
		bufSize = bufsize;//512+8
		blkSize = blksize+1;//65
		numAllBlk = bufSize / blkSize ;
		numFreeBlk = numAllBlk;
		System.out.printf("缓冲区行和列%d %d\n",bufSize/blkSize,blkSize);
		data = new int[bufSize/blkSize][bufSize];
		return data;
	}
	
	void freeBuffer() {
		/**No.2
		 * 释放缓冲区buf占用的内存空间。
		 */
		data = null;
	}
	
	public Object getNewBlockInBuffer(){
		/**No.3
		 * 在缓冲区buf中申请一个新的块。
		 * @return 若申请成功，则返回该块的地址的块数号；否则，返回NULL
		 */
		int blk=0;
		for(int i = 0;i<numAllBlk;i++) {//遍历所有的缓冲块
			//System.out.printf("%d 块的标记 %d\n",i,data[i][blkSize-1]);
			if(data[i][blkSize-1]==BLOCK_AVAILABLE) {//可以用
				data[i][blkSize-1] = BLOCK_UNAVAILABLE;//标记这个块不可以用了
				numFreeBlk--;
				return i;
			}
		}
		return null;
	}
	
	public void freeBlockInBuffer(int blk) {
		/**No.4
		 * @param blk缓冲区中块的地址的块数号
		 * 解除块blk对缓冲区内存的占用，即将blk占据的内存区域标记为可用
		 */
		//Step1:将该条缓冲里边的东西删掉
		for(int i=0;i<bufSize;i++) {
			data[blk][i]=0;
		}
		//Step2:解除占用标志位
		data[blk][blkSize] = BLOCK_AVAILABLE;//重新标记这个块可以用了
		numFreeBlk++;
	}
	
	public int dropBlockOnDisk(int addr) {
		/**No.5
		 * 从磁盘上删除地址为addr的磁盘块内的数据。
		 * @return 若删除成功，则返回0；否则，返回-1。
		 */
		//TODO 
		return 0;
	}
	
	public Object readBlockFromDisk(int blk,int addr){
		/**No.6
		 * 将磁盘上地址为addr的磁盘块读入缓冲区buf。同时，缓冲区buf的I/O次数加1。
		 *@param addr磁盘上地址
		 *@return 若读取成功，则返回缓冲区内该块的地址blk；否则，返回NULL。
		 */
		//如果缓冲区没有位置了，从磁盘读取不成功
	    if (numFreeBlk == 0)
	    {
	        System.out.println("Buffer Overflows!\n");
	        return null;
	    }
	
	    //找到一个空的缓冲区
	    //int blk = (int) getNewBlockInBuffer();
	    data[blk][blkSize] = BLOCK_UNAVAILABLE;//标记这个块不可以用了
	    //把disk的东西读进缓存区	
		String fileName = "src/disk/lineFind/"+String.valueOf(addr)+".txt";
	    try {
			FileReader fr = new FileReader(fileName);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			// 按行读取字符串
			int i= 0;
			while ((str = bf.readLine()) != null) {
				data[blk][i] = Integer.valueOf(str);//把内容写进缓冲区
				i++;
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			System.out.println("Reading Block Failed!\n");
			e.printStackTrace();
			return null;
		}
	    //更改一些信息
	    numFreeBlk--;
	    numIO++;
		return blk;
	}
	
	public int writeBlockToDisk(int blkPtr, int addr) throws IOException {
		/**No.7 
		 * 将缓冲区buf内的块blk写入磁盘上地址为addr的磁盘块。同时，缓冲区buf的I/O次数加1。
		 * @param blkPtr为buf块的位置
		 * @param addr为磁盘上地址
		 * @return 若写入成功，则返回0；否则，返回-1。
		 */
		//创建文件夹，写文件
		String address = String.valueOf(addr);
		File fout = new File("src/disk/lineFind/"+address+".txt");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			 
			//0 表示的是元组 1 表示的是后继的地址
			//0 12 34
			for (int i = 0; i <65; i++) {
				String s = String.valueOf(data[blkPtr][i]);
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Writing Block Failed!\n");
			e.printStackTrace();
	        return -1;
		}
		//Step1:将该条缓冲里边的东西删掉
		for(int i=0;i<bufSize;i++) {
			data[blkPtr][i]=0;
		}
		data[blkPtr][blkSize]= BLOCK_AVAILABLE;//64是是否可用的标志位
	    numFreeBlk++;
	    numIO++;
		return 0;
	}
}
