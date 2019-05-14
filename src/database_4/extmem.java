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
		 * ��ʼ�������������������
		 * @param bufSizeΪ��������С����λ���ֽڣ�
		 * @param blkSizeΪ��Ĵ�С����λ���ֽڣ�
		 * @return  ����������ʼ���ɹ�����ú�������ָ��û������ĵ�ַ�����򣬷���NULL��
		 */
		numIO = 0;
		bufSize = bufsize;//512+8
		blkSize = blksize+1;//65
		numAllBlk = bufSize / blkSize ;
		numFreeBlk = numAllBlk;
		System.out.printf("�������к���%d %d\n",bufSize/blkSize,blkSize);
		data = new int[bufSize/blkSize][bufSize];
		return data;
	}
	
	void freeBuffer() {
		/**No.2
		 * �ͷŻ�����bufռ�õ��ڴ�ռ䡣
		 */
		data = null;
	}
	
	public Object getNewBlockInBuffer(){
		/**No.3
		 * �ڻ�����buf������һ���µĿ顣
		 * @return ������ɹ����򷵻ظÿ�ĵ�ַ�Ŀ����ţ����򣬷���NULL
		 */
		int blk=0;
		for(int i = 0;i<numAllBlk;i++) {//�������еĻ����
			//System.out.printf("%d ��ı�� %d\n",i,data[i][blkSize-1]);
			if(data[i][blkSize-1]==BLOCK_AVAILABLE) {//������
				data[i][blkSize-1] = BLOCK_UNAVAILABLE;//�������鲻��������
				numFreeBlk--;
				return i;
			}
		}
		return null;
	}
	
	public void freeBlockInBuffer(int blk) {
		/**No.4
		 * @param blk�������п�ĵ�ַ�Ŀ�����
		 * �����blk�Ի������ڴ��ռ�ã�����blkռ�ݵ��ڴ�������Ϊ����
		 */
		//Step1:������������ߵĶ���ɾ��
		for(int i=0;i<bufSize;i++) {
			data[blk][i]=0;
		}
		//Step2:���ռ�ñ�־λ
		data[blk][blkSize] = BLOCK_AVAILABLE;//���±��������������
		numFreeBlk++;
	}
	
	public int dropBlockOnDisk(int addr) {
		/**No.5
		 * �Ӵ�����ɾ����ַΪaddr�Ĵ��̿��ڵ����ݡ�
		 * @return ��ɾ���ɹ����򷵻�0�����򣬷���-1��
		 */
		//TODO 
		return 0;
	}
	
	public Object readBlockFromDisk(int blk,int addr){
		/**No.6
		 * �������ϵ�ַΪaddr�Ĵ��̿���뻺����buf��ͬʱ��������buf��I/O������1��
		 *@param addr�����ϵ�ַ
		 *@return ����ȡ�ɹ����򷵻ػ������ڸÿ�ĵ�ַblk�����򣬷���NULL��
		 */
		//���������û��λ���ˣ��Ӵ��̶�ȡ���ɹ�
	    if (numFreeBlk == 0)
	    {
	        System.out.println("Buffer Overflows!\n");
	        return null;
	    }
	
	    //�ҵ�һ���յĻ�����
	    //int blk = (int) getNewBlockInBuffer();
	    data[blk][blkSize] = BLOCK_UNAVAILABLE;//�������鲻��������
	    //��disk�Ķ�������������	
		String fileName = "src/disk/lineFind/"+String.valueOf(addr)+".txt";
	    try {
			FileReader fr = new FileReader(fileName);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			// ���ж�ȡ�ַ���
			int i= 0;
			while ((str = bf.readLine()) != null) {
				data[blk][i] = Integer.valueOf(str);//������д��������
				i++;
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			System.out.println("Reading Block Failed!\n");
			e.printStackTrace();
			return null;
		}
	    //����һЩ��Ϣ
	    numFreeBlk--;
	    numIO++;
		return blk;
	}
	
	public int writeBlockToDisk(int blkPtr, int addr) throws IOException {
		/**No.7 
		 * ��������buf�ڵĿ�blkд������ϵ�ַΪaddr�Ĵ��̿顣ͬʱ��������buf��I/O������1��
		 * @param blkPtrΪbuf���λ��
		 * @param addrΪ�����ϵ�ַ
		 * @return ��д��ɹ����򷵻�0�����򣬷���-1��
		 */
		//�����ļ��У�д�ļ�
		String address = String.valueOf(addr);
		File fout = new File("src/disk/lineFind/"+address+".txt");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			 
			//0 ��ʾ����Ԫ�� 1 ��ʾ���Ǻ�̵ĵ�ַ
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
		//Step1:������������ߵĶ���ɾ��
		for(int i=0;i<bufSize;i++) {
			data[blkPtr][i]=0;
		}
		data[blkPtr][blkSize]= BLOCK_AVAILABLE;//64���Ƿ���õı�־λ
	    numFreeBlk++;
	    numIO++;
		return 0;
	}
}
