package database_4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class mainP {
	static int sTupleNumber = 224;
	static int rTupleNumber = 112;
	static extmem ex = new extmem();
	static int sTable[][] = new int[sTupleNumber][2];
	static int rTable[][] = new int[rTupleNumber][2];
	static ArrayList<int[][]> BinaryFindBlockList = new ArrayList<>();// ������еĿ�
	static ArrayList<int[]> BinaryIndex = new ArrayList<>();
	static int soilder = 0;
	
	
	public static void main(String[] args) throws IOException {
		int bufsize = 520;//buffer�������Ĵ�С
		int blksize = 64;//��Ĵ�С
		ex.initBuffer(bufsize, blksize);//��ʼ��������
		//����R��S������
		createTableData();
		//Ӧ�������㷨����������  R.A=40
		//LineSearch();
		
		//TODO Ӧ�������㷨��B+��
		//bTreeSearch();
		
		//Ӧ�������㷨����������
		//BinaryFind();
		//TODO ͶӰ
		
		//TODO ����
		
		/*
		System.out.println("-------------����Main-----------------");
		sortData();
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(rTable[i][j]+" ");
			}
			System.out.print("\n");
		}*/
	}
	
	static void createTableData() {
		/*
		 *����R��S��ĳ�ʼ���ݡ�
		 *Ȼ��д��txt��ߣ��´�ֱ�Ӵ�txt��߶�����
		 **/
		/*
		int sTupleNumber = 224;
		int rTupleNumber = 112;
		int sTable[][] = new int[sTupleNumber][2];
		int rTable[][] = new int[rTupleNumber][2];
		//TODO ��дs��:C��ֵ��Ϊ[20, 60]��D��ֵ��Ϊ[1, 1000]
		
		Random random = new Random();
		//int s = random.nextInt(max) % (max - min + 1) + min;
		for(int i=0;i<sTupleNumber;i++) {
			for(int j=0;j<2;j++) {
				if(j==0) {//[20, 60]
					int value = random.nextInt(60) % (60 - 20 + 1) + 20;
					sTable[i][0] = value;
				}else{//[1, 1000]
					int value = random.nextInt(1000) % (1000 - 1 + 1) + 1;
					sTable[i][1] = value;
				}
			}
		}
		
		//TODO ��дr��:A��ֵ��Ϊ[1, 40]��B��ֵ��Ϊ[1, 1000]
		for(int i=0;i<rTupleNumber;i++) {
			for(int j=0;j<2;j++) {
				if(j==0) {//[1, 40]
					int value = random.nextInt(40) % (40 - 1 + 1) + 1;
					rTable[i][0] = value;
				}else{//[1, 1000]
					int value = random.nextInt(1000) % (1000 - 1 + 1) + 1;
					rTable[i][1] = value;
				}
			}
		}
		
	    //����õ������ӡ����
		System.out.println("��ӡR���ֵ");
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(rTable[i][j]+" ");
			}
			System.out.print("\n");
		}
		*/

		String sfileName = "src/table/Stable.txt";
		String rfileName = "src/table/Rtable.txt";
		rTable = readData(rfileName,rTupleNumber);
		sTable = readData(sfileName,sTupleNumber);
		//��֤��ȡ��Ч��
		/*
		System.out.println("��ӡS���ֵ");
		for(int i=0;i<sTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(sTable[i][j]+" ");
			}
			System.out.print("\n");
		}
		System.out.println("----------------------------------");
		System.out.println("��ӡR���ֵ");
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(rTable[i][j]+" ");
			}
			System.out.print("\n");
		}*/
	}
	
	static int[][] readData(String fileName,int TupleNumber) {
		//TODO ��ȡ�ļ���ߵ�table��Ϣ
		int table[][] = new int[TupleNumber][2];
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			// ���ж�ȡ�ַ���
			int i = 0;
			while ((str = bf.readLine()) != null) {
				String s[] = str.split(" ");
				table[i][0] = Integer.valueOf(s[0]);
				table[i][1] = Integer.valueOf(s[1]);
				i++;
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return table;
	}

	static void LineSearch() throws IOException {
		/**
		 * ����������R.A=40��������Ȼ���մ�С˳��Ž����̣�װ��
		 */
		System.out.println("-------------����LineSearch-----------------");
		//Step1:�Ƚ�������
		sortData();
		/*
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(rTable[i][j]+" ");
			}
			System.out.print("\n");
		}*/
		//Step2����д�����壬��д������
		int diskOrder=-1;
		for(int i=0;i<rTupleNumber;i=i+7) {//��
			//System.out.printf("������%d��\n",i);
			Object lineNumber = ex.getNewBlockInBuffer();
			if(lineNumber!=null) {//������û����
				int target = 0;//��ǣ���ֵ��������ʲôλ����
				int line = (int) lineNumber;
				//System.out.printf("���뵽���б꣺%d\n",line);
				for(int j = i;j<i+7;j++){
					ex.data[line][target]=ex.data[line][target+1]=
					ex.data[line][target+2]=ex.data[line][target+3]=rTable[j][0];
					ex.data[line][target+4]=ex.data[line][target+5]=
					ex.data[line][target+6]=ex.data[line][target+7]=rTable[j][1];
					target+=8;
				}
			}else {
				//System.out.printf("����ռ�������\n");
				//�ѻ�����������д������
				for(int k=0;k<8;k++) {
					diskOrder++;
					String fileName = "src/disk/lineFind/"+String.valueOf(diskOrder)+".txt";
					ex.writeBlockToDisk(k,fileName);
				}
				i = i-7;
			}
		}
		//Step3����ʼ����R.A=40
		//�����������м�¼�ĵط�������Ҳ���������ջ��������ٴӻ�����������
		int flag = 0;
		while(flag!=1) {//��û�ҵ�
			//��ȥ��������
			for(int i = 0;i<8;i++) {
				if(ex.data[i][64]==1) {//������
					for(int j = 0;j<56;j=j+8) {
						int oneA = ex.data[i][j];
						if(oneA==40) {//�ҵ���
							System.out.printf("�ҵ�����!!!�ڻ�������%d�� index[%d,%d]\n",i,j,j+3);
							flag = 1;
						}
					}
				}
			}
			//������û��,���ͷŻ��������Ӵ���д��������
			if(flag==0) {
				for(int i = 0;i<8;i++) {
					ex.freeBlockInBuffer(i); 
				}
				//�����Ƕ���8�飬���ǲ�һ����8��
				for(int i = 0;i<8 && diskOrder>=0;i++) {
					String fileName = "src/disk/lineFind/"+String.valueOf(diskOrder)+".txt";
					ex.readBlockFromDisk(i,fileName);
					diskOrder--;
				}
			}
		}
		System.out.printf("���Բ��� IO������%d\n",ex.numIO);
		
		//��ӡ�鿴Ч��
		System.out.printf("-------------�鿴������------------\n");
		int dataCopy[][]=ex.data;
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(dataCopy[i][j]+" ");
			}
			System.out.printf("\n");
		}
	}
	
	static void sortData(){
		/**
		 *�Զ�ά����ĵ�һ�У����м򵥵�ѡ������ 
		 */
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<rTupleNumber;j++) {
				if(rTable[i][0]>rTable[j][0]) {//��һ������>�ڶ�������
					int temp0=rTable[i][0];
					rTable[i][0] = rTable[j][0];
					rTable[j][0] = temp0;
					int temp1=rTable[i][1];
					rTable[i][1] = rTable[j][1];
					rTable[j][1] = temp1;
				}
			}
		}
	}
	
	static void bTreeSearch() {
		/**
		 * ʹ��B-������Ҫ��������
		 */
	}
	
	static void BinaryFind() throws IOException {
		//Step1:�ȶ����ݽ�������
		sortData();
		//Step2�������ݽ��зֿ�
		for(int i = 0;i<rTupleNumber;i=i+7) {
			//System.out.printf("�µ�һ�� i=%d\n",i);
			int count=0;
			int diskOne[][] = new int[7][2];
			for(int j = i;count<7;j++) {
				//����һ���µ�����
				//System.out.printf("%d������ %d %d  д���%d��\n",j,rTable[j][0],rTable[j][1],j%7);
				diskOne[j%7][0] = rTable[j][0];
				diskOne[j%7][1] = rTable[j][1];
				count++;
			}
			BinaryFindBlockList.add(diskOne);
			//����õ�������ú�
		}
		//�������Ϣ�Ƿ���ȷ
		/*
		for(int i =0;i<BinaryFindBlockList.size();i++) {
			//������ӡ��ά����
			System.out.printf("������%d��\n",i);
			for(int j = 0;j<7;j++) {
				System.out.printf("["+BinaryFindBlockList.get(i)[j][0]+","+BinaryFindBlockList.get(i)[j][1]+"]");
				System.out.printf(" ");
			}
			System.out.printf("\n");
		}
		*/
		//Step3��TODO �Կ鹹������,������
		createBinaryTree(0,15);//һ��16�� 112/7
		//���齨��Index
		System.out.println("index���");
		for(int i = 0;i<BinaryIndex.size();i++) {
			System.out.println(Arrays.toString(BinaryIndex.get(i)));
		}
		
		//Step4:�Ž�����
		//int diskOrder=-1;
		for(int i = BinaryIndex.size()-1;i>-1;i--) {//������
			System.out.println("index "+Arrays.toString(BinaryIndex.get(i)));
			for(int arrayNumber = 1;arrayNumber<3;arrayNumber++) {
				int nodeOrder = BinaryIndex.get(i)[arrayNumber];//[14, 1000, 15]
				if(nodeOrder!=1000) {//����н��
					int saveBlock[][] = BinaryFindBlockList.get(nodeOrder);//һ��������
					Object lineNumber = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
					if(lineNumber==null) {
						for(int k=0;k<8;k++) {
							String fileName = "src/disk/BinaryFind/"+String.valueOf(ex.data[k][56])+".txt";
							ex.writeBlockToDisk(k,fileName);
						}
						//��������һ������ռ�
						lineNumber = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
					}
					int line = (int) lineNumber;
					//System.out.printf("���뵽���б꣺%d\n",line);
					for(int j = 0;j<56;j=j+8){
						ex.data[line][j]=ex.data[line][j+1]=
						ex.data[line][j+2]=ex.data[line][j+3]=saveBlock[j/8][0];
						ex.data[line][j+4]=ex.data[line][j+5]=
						ex.data[line][j+6]=ex.data[line][j+7]=saveBlock[j/8][1];
					}
					ex.data[line][56]=nodeOrder;
				}
			}			
		}
		System.out.printf("���Բ��� IO������%d\n",ex.numIO);
		//�Ѷ���д��ȥ
		int nodeOrder = BinaryIndex.get(0)[0];
		int saveBlock[][] = BinaryFindBlockList.get(nodeOrder);//һ��������
		Object lineNumber = ex.getNewBlockInBuffer();
		if(lineNumber==null) {
			for(int k=0;k<8;k++) {
				String fileName = "src/disk/BinaryFind/"+String.valueOf(nodeOrder)+".txt";
				ex.writeBlockToDisk(k,fileName);
			}
			//��������һ������ռ�
			lineNumber = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
		}
		int line = (int) lineNumber;
		//System.out.printf("���뵽���б꣺%d\n",line);
		for(int j = 0;j<56;j=j+8){
			ex.data[line][j]=ex.data[line][j+1]=
			ex.data[line][j+2]=ex.data[line][j+3]=saveBlock[j/8][0];
			ex.data[line][j+4]=ex.data[line][j+5]=
			ex.data[line][j+6]=ex.data[line][j+7]=saveBlock[j/8][1];
		}
		ex.data[line][56]=nodeOrder;
		//�ѻ���������д��ȥ
		//System.out.printf("��ʣ������%d",ex.numAllBlk-ex.numFreeBlk);
		int blokNow = ex.numAllBlk-ex.numFreeBlk;
		//����д�� for(int k=0; k<ex.numAllBlk-ex.numFreeBlk; k++) {
		//��Ϊ���õĿ�����static���Ƕ�̬�仯��
		for(int k=0; k<blokNow; k++) {
			String fileName = "src/disk/BinaryFind/"+String.valueOf(ex.data[k][56])+".txt";
			//System.out.printf("��տ�����%d\n",k);
			ex.writeBlockToDisk(k,fileName);
		}
		
		//Step5:��ʼ��ѯ
		//�ѵ�һ���������ʣ�µ����ε�����
		Object bufferLine = ex.getNewBlockInBuffer();//��Ϊһ��ʼ�����ǿյģ��϶��пռ�
		int currentDisk = BinaryIndex.get(0)[0];//ͷ���
		String fileName = "src/disk/BinaryFind/"+String.valueOf( currentDisk)+".txt";
		ex.readBlockFromDisk((int)bufferLine,fileName);
		int flag = 0;
		while(flag!=1) {//��û�ҵ�
			//��ȥ��������	
			System.out.printf("������̿�%d\n",currentDisk);
			int direction =0;
			for(int j = 0;j<56;j=j+8) {
				int oneA = ex.data[(int) bufferLine][j];
				if(oneA==40) {//�ҵ���
					System.out.printf("�ҵ�����!!!�ڻ�������%d�� index[%d,%d]\n",currentDisk,j,j+3);
					flag = 1;
				}
			}
			//������һ��
			if(flag==0) {
				//�жϴ�С
				if(40<ex.data[(int) bufferLine][0]) {
					direction=1;//����
				}else {
					direction=2;//�ҽ��
				}
				currentDisk= getNextDiskOrder(currentDisk,direction);
				bufferLine = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
				if(bufferLine==null) {//��ջ�����
					for(int i = 0;i<8;i++) {
						ex.freeBlockInBuffer(i); 
					}
					bufferLine = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
				}
				fileName = "src/disk/BinaryFind/"+String.valueOf(currentDisk)+".txt";
				ex.readBlockFromDisk((int)bufferLine,fileName);
			}
		}
		
		//��ӡ�鿴Ч��
		System.out.printf("-------------�鿴������------------\n");
		int dataCopy[][]=ex.data;
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(dataCopy[i][j]+" ");
			}
			System.out.printf("\n");
		}
		System.out.printf("���Բ��� IO������%d\n",ex.numIO+8);
		
	}
	
	static int getNextDiskOrder(int currentDisk,int direction) {
		int nextDisk=0;
		for(int[] s :BinaryIndex) {
			if(s[0]==currentDisk) {
				nextDisk = s[direction];
			}
		}
		return nextDisk;
	}
	
	static int createBinaryTree(int start,int end) {
		//ѡ���м���ǿ�
		//BinaryIndex
		//System.out.printf("ʿ����%d start+end %d %d\n",soilder,start,end);
		if(start!=end && start<=end) {
			int oneIndex[] = new int[3];
			int mid = start+(end-start)/2;// 15/2 =7 ,ȡ��8��
			int left;
			int right;
			if(mid-1<start) {//û������
				left = 1000;
			}else {
				left = start+(mid-1-start)/2;
			}
			if(mid+1>end) {//û���ҽ��
				right = 1000;
			}else {
				right = (end-(mid+1))/2;
			}
			oneIndex[0] = mid;
			oneIndex[1] = left;
			oneIndex[2] = mid+1+right;
			BinaryIndex.add(oneIndex);
			//soilder++;
			createBinaryTree(start,mid-1);
			createBinaryTree(mid+1,end);
			return mid;
		}
		//soilder++;
		return 1;
	}
	
	static void selectColumn() {
		/**
		 * ���ܣ�R�ϵ�A���Խ���ͶӰ�������������ڴ�����
		 */
		
		
		
	}
}
