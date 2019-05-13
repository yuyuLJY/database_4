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
		 * ��ʼ�������������������
		 * @param bufSizeΪ��������С����λ���ֽڣ�
		 * @param blkSizeΪ��Ĵ�С����λ���ֽڣ�
		 * @return  ����������ʼ���ɹ�����ú�������ָ��û������ĵ�ַ�����򣬷���NULL��
		 */
		int data[][] = new int[bufSize][bufSize/bufSize];
		return data;
	}
	
	void freeBuffer() {
		/**No.2
		 * �ͷŻ�����bufռ�õ��ڴ�ռ䡣
		 */
	}
	
	public int getNewBlockInBuffer(){
		/**No.3
		 * �ڻ�����buf������һ���µĿ顣
		 * @return ������ɹ����򷵻ظÿ�ĵ�ַ�Ŀ����ţ����򣬷���NULL
		 */
		int blk=0;
		return blk;
	}
	
	public void freeBlockInBuffer(int blk) {
		/**No.4
		 * @param blk�������п�ĵ�ַ�Ŀ�����
		 * �����blk�Ի������ڴ��ռ�ã�����blkռ�ݵ��ڴ�������Ϊ����
		 */
	
	}
	
	public int dropBlockOnDisk(int addr) {
		/**No.5
		 * �Ӵ�����ɾ����ַΪaddr�Ĵ��̿��ڵ����ݡ�
		 * @return ��ɾ���ɹ����򷵻�0�����򣬷���-1��
		 */
		return 0;
	}
	
	public void readBlockFromDisk(int addr){
		/**No.6
		 * �������ϵ�ַΪaddr�Ĵ��̿���뻺����buf��ͬʱ��������buf��I/O������1��
		 *@param addr�����ϵ�ַ
		 *@return ����ȡ�ɹ����򷵻ػ������ڸÿ�ĵ�ַ(->�ĳɿ���ߵ�����)�����򣬷���NULL��
		 */
		
	}
	
	public int writeBlockToDisk(int blkPtr, int addr) {
		/**No.7 
		 * ��������buf�ڵĿ�blkд������ϵ�ַΪaddr�Ĵ��̿顣ͬʱ��������buf��I/O������1��
		 * @param blkPtrΪbuf���λ��
		 * @param addrΪ�����ϵ�ַ
		 * @return ��д��ɹ����򷵻�0�����򣬷���-1��
		 */
		return 1;
	}
}
