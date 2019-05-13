package database_4;

public class extmem {
	static int BLOCK_AVAILABLE = 0;
	static int BLOCK_UNAVAILABLE = 1;
	public int numIO; /* Number of IO's*/
	public int bufSize; /* Buffer size*/
	public int blkSize; /* Block size */
	public int numAllBlk; /* Number of blocks that can be kept in the buffer */
	public int numFreeBlk; /* Number of available blocks in the buffer */
	//public int data; /* Starting address of the buffer */
	
	public int[][] initBuffer(int bufSize, int blkSize) {
		/**No.1
		 * 初始化缓冲区，其输入参数
		 * @param bufSize为缓冲区大小（单位：字节）
		 * @param blkSize为块的大小（单位：字节）
		 * @return  若缓冲区初始化成功，则该函数返回指向该缓冲区的地址；否则，返回NULL。
		 */
		int data[][] = new int[bufSize][bufSize/bufSize];
		return data;
	}
	
	void freeBuffer() {
		/**No.2
		 * 释放缓冲区buf占用的内存空间。
		 */
	}
	
	public int getNewBlockInBuffer(){
		/**No.3
		 * 在缓冲区buf中申请一个新的块。
		 * @return 若申请成功，则返回该块的地址的块数号；否则，返回NULL
		 */
		int blk=0;
		return blk;
	}
	
	public void freeBlockInBuffer(int blk) {
		/**No.4
		 * @param blk缓冲区中块的地址的块数号
		 * 解除块blk对缓冲区内存的占用，即将blk占据的内存区域标记为可用
		 */
	
	}
	
	public int dropBlockOnDisk(int addr) {
		/**No.5
		 * 从磁盘上删除地址为addr的磁盘块内的数据。
		 * @return 若删除成功，则返回0；否则，返回-1。
		 */
		return 0;
	}
	
	public void readBlockFromDisk(int addr){
		/**No.6
		 * 将磁盘上地址为addr的磁盘块读入缓冲区buf。同时，缓冲区buf的I/O次数加1。
		 *@param addr磁盘上地址
		 *@return 若读取成功，则返回缓冲区内该块的地址(->改成块里边的内容)；否则，返回NULL。
		 */
		
	}
	
	public int writeBlockToDisk(int blkPtr, int addr) {
		/**No.7 
		 * 将缓冲区buf内的块blk写入磁盘上地址为addr的磁盘块。同时，缓冲区buf的I/O次数加1。
		 * @param blkPtr为buf块的位置
		 * @param addr为磁盘上地址
		 * @return 若写入成功，则返回0；否则，返回-1。
		 */
		return 1;
	}
}
