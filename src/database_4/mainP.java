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
		
		//Ӧ�������㷨��B+��
		//bTreeSearch();
		
		//Ӧ�������㷨��
		//BinaryFind();
		//ͶӰ
		//selectColumn();
		
		//Nest-Loop- Join�㷨
		//NestLoopJoin();
		
		//Sort-Merge-Join�㷨
		//SortMergeJoin();
		//Hash-Join�㷨
		//HashJoin();
		
		//TODO �ڴ�������㷨
		
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
		sortData(rTupleNumber,rTable);
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
		System.out.printf("IO������%d\n",ex.numIO+16);
		
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
	
	static void sortData(int column,int a[][]){
		/**
		 *�Զ�ά����ĵ�һ�У����м򵥵�ѡ������ 
		 */
		for(int i=0;i<column;i++) {
			for(int j = 0;j<column;j++) {
				if(a[i][0]<a[j][0]) {//��һ������>�ڶ������� //��С����
					int temp0=a[i][0];
					a[i][0] = a[j][0];
					a[j][0] = temp0;
					int temp1=a[i][1];
					a[i][1] = a[j][1];
					a[j][1] = temp1;
				}
			}
		}
	}
	
	static void bTreeSearch() {
		/**
		 * ʹ��B-������Ҫ��������
		 */
		
		//Step5:��ʼ��ѯ
		//�ѵ�һ���������ʣ�µ����ε�����
		Object bufferLine = ex.getNewBlockInBuffer();//��Ϊһ��ʼ�����ǿյģ��϶��пռ�
		int currentDisk = 0;//ͷ���
		String fileName = "src/disk/BTreeFind/"+String.valueOf( currentDisk)+".txt";
		ex.readBlockFromDisk((int)bufferLine,fileName);
		int flag = 0;
		int count=0;
		while(flag != 1) {//��û�ҵ�
			//��ȥ��������	
			count++;
			System.out.printf("������̿�%d\n",currentDisk);
			int direction =0;//������ȡ����һ��
			if(ex.data[(int) bufferLine][56]!=0) {//���Ǹ�������
				int findNextFlag=0;
				int condition =0;//���ĸ�indexֵ�Ƚ�
				while(findNextFlag!=1) {
					if(40<ex.data[(int) bufferLine][condition]) {
						direction=condition+56;
					}
					condition++;
					if(ex.data[(int) bufferLine][condition]==999999) {//�Ѿ����������һ����
						direction=condition+56;
						findNextFlag=1;
					}
				}
			}else {//��������
				for(int j = 0;j<56;j=j+8) {
					int oneA = ex.data[(int) bufferLine][j];
					if(oneA==40) {//�ҵ���
						System.out.printf("�ҵ�����!!!�ڻ�������%d�� index[%d,%d]\n",currentDisk,j,j+3);
						flag = 1;
					}
				}
			}
			//������һ��
			if(flag==0) {
				//�жϴ�С
				/*
				if(40<ex.data[(int) bufferLine][0]) {
					direction=1;//����
				}else {
					direction=2;//�ҽ��
				}*/
				currentDisk= ex.data[(int) bufferLine][direction];
				bufferLine = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
				if(bufferLine==null) {//��ջ�����
					for(int i = 0;i<8;i++) {
						ex.freeBlockInBuffer(i); 
					}
					bufferLine = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
				}
				fileName = "src/disk/BTreeFind/"+String.valueOf(currentDisk)+".txt";
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
		System.out.printf("IO������%d\n",ex.numIO+26);
	}
	
	static void BinaryFind() throws IOException {
		//Step1:�ȶ����ݽ�������
		sortData(rTupleNumber,rTable);
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
		System.out.printf("IO������%d\n",ex.numIO);
		
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
	
	static void selectColumn() throws IOException {
		/**
		 * ���ܣ�R�ϵ�A���Խ���ͶӰ�������������ڴ�����
		 */
		//Step1:������ȫ��д���������
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
					String fileName = "src/disk/selectColunm/"+String.valueOf(diskOrder)+".txt";
					ex.writeBlockToDisk(k,fileName);
				}
				i = i-7;
			}
		}
		int bufferBlockNow = ex.numAllBlk-ex.numFreeBlk;
		for(int k=0;k<bufferBlockNow;k++) {
			diskOrder++;
			String fileName = "src/disk/selectColunm/"+String.valueOf(diskOrder)+".txt";
			ex.writeBlockToDisk(k,fileName);
			ex.freeBlockInBuffer(k);
		}
		//��ӡ�鿴Ч��
		/*
		System.out.printf("-------------�鿴������------------\n");
		int dataCopy[][]=ex.data;
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(dataCopy[i][j]+" ");
			}
			System.out.printf("\n");
		}*/
		System.out.printf("IO������%d\n",ex.numIO);
		
		//Step2:�����������ٿ���������������
		//���о���Ҫһ�е�������壺2����Ϊ��Ž���Ļ��壬4����Ϊ��������
		String fileName;
		int circleNum = (diskOrder+1)/4;
		int sepecialResultOrder = 99;
		for(int cirle = 0;cirle<circleNum;cirle++) {//Ҫ���мƴ��ֻ�
			for(int i=cirle*4;i<(cirle+1)*4;i++) {//[0 1 2 3] [4 5 6 7]
				Object bufferLine = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
				fileName = "src/disk/selectColunm/"+String.valueOf(i)+".txt";
				ex.readBlockFromDisk((int)bufferLine,fileName);
			}
			System.out.printf("buffer״̬��%d %d\n",ex.numAllBlk,ex.numFreeBlk);
			/*
			System.out.printf("-------------�����Ŀ��Ժ�Ĳ鿴������------------\n");
			for(int i = 0;i<8;i++) {
				for(int j = 0;j<65;j++) {
					System.out.printf(dataCopy[i][j]+" ");
				}
				System.out.printf("\n");
			}*/
			//�������ݵ�4�н��б���
			Object bufferLine = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
			int condition =0;
			for(int i=0;i<4;i++) {//��buffer���н��б���
				for(int j = 0;j<56;j=j+8) {//��ĳ��blk���б���
					ex.data[(int) bufferLine][condition] = ex.data[(int) bufferLine][condition+1]=
							ex.data[(int) bufferLine][condition+2] = ex.data[(int) bufferLine][condition+3]=
							ex.data[i][j];
					condition+=4;
				}
				System.out.printf("condition %d\n",condition);
				if(condition>55) {//��Ҫ��������ռ���
					//�Ȱ�ԭ���ǿ�д������
					//System.out.printf("дbuffer���� %d\n",i);
					sepecialResultOrder++;
					fileName = "src/disk/selectColunm/"+String.valueOf(sepecialResultOrder)+".txt";
					ex.writeBlockToDisk((int)bufferLine,fileName);
					//��������,��������һ��
					bufferLine = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
					condition =0;
				}	
			}
			/*
			System.out.printf("-------------�鿴������------------\n");
			for(int i = 0;i<8;i++) {
				for(int j = 0;j<65;j++) {
					System.out.printf(dataCopy[i][j]+" ");
				}
				System.out.printf("\n");
			}*/
			
			//��ջ�����
			//TODO ������ �ϵ��ӽ�
			//int nowBlock = ex.numAllBlk-ex.numFreeBlk;
			//System.out.printf("��Ҫ��գ�%d %d %d\n",ex.numAllBlk,ex.numFreeBlk,nowBlock);
			for(int kkk = 0;kkk<5;kkk++) {
				ex.freeBlockInBuffer(kkk); 
			}
			

		}
		System.out.printf("-------------�鿴������------------\n");
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(ex.data[i][j]+" ");
			}
			System.out.printf("\n");
		}
		System.out.printf("IO������%d\n",ex.numIO);
	}
	
	//ר�Ÿ�Join�õ�
	/**
	 *@param column����
	 *@param a ��ά���飬ʹ���ĸ���
	 *@param fileName ʹ�õ��ļ�������
	 */
	static void writeAllBufferToDisk(int colunm,int a[][],String frontFileName) throws IOException {
		int diskOrder=-1;
		//System.out.printf("����%s �ܿ��� %d ʣ�����%d\n",frontFileName,ex.numAllBlk,ex.numFreeBlk);
		for(int i=0;i<colunm;i=i+7) {//��
			//System.out.printf("������%d��\n",i);
			//System.out.printf("�ܿ��� %d ʣ�����%d\n",ex.numAllBlk,ex.numFreeBlk);
			Object lineNumber = ex.getNewBlockInBuffer();
			if(lineNumber!=null) {//������û����
				int target = 0;//��ǣ���ֵ��������ʲôλ����
				int line = (int) lineNumber;
				//System.out.printf("���뵽���б꣺%d\n",line);
				for(int j = i;j<i+7;j++){
					ex.data[line][target]=ex.data[line][target+1]=
					ex.data[line][target+2]=ex.data[line][target+3]=a[j][0];
					ex.data[line][target+4]=ex.data[line][target+5]=
					ex.data[line][target+6]=ex.data[line][target+7]=a[j][1];
					target+=8;
				}
			}else {
				//System.out.printf("����ռ�������\n");
				//�ѻ�����������д������
				for(int k=0;k<8;k++) {
					diskOrder++;
					ex.data[k][64]=1;
					String fileName = frontFileName+String.valueOf(diskOrder)+".txt";
					ex.writeBlockToDisk(k,fileName);
				}
				i = i-7;
			}
		}
		//System.out.printf("�ܿ��� %d ʣ�����%d\n",ex.numAllBlk,ex.numFreeBlk);
		int bufferBlockNow = ex.numAllBlk-ex.numFreeBlk;
		for(int k=0;k<bufferBlockNow;k++) {
			diskOrder++;
			String fileName = frontFileName+String.valueOf(diskOrder)+".txt";
			ex.writeBlockToDisk(k,fileName);
			//������!!!! ��writeBlockToDisk�ᵼ������freeblock++
			//ex.freeBlockInBuffer(k);
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
		System.out.printf("IO������%d\n",ex.numIO);
	}
	
	
	static void NestLoopJoin() throws IOException{
		/**
		 * Ƕ�ױ�����ѯ
		 */
		
		//Step1:�Ȱ�R���S��д���������
		writeAllBufferToDisk(rTupleNumber,rTable,"src/disk/NestLoopJoin/R");
		writeAllBufferToDisk(sTupleNumber,sTable,"src/disk/NestLoopJoin/S");
		//Step2:ȡ��R���һ�У�������S��Ƚϡ���R��������Σ�ÿ�ζ��Ƚ���ص������Ƿ���ͬ
		//��ͬ�ľ�д��һ�黺����ߣ���¼�´洢��Щ���ӽ���Ļ�����к�
		System.out.printf("Step2 �ܿ��� %d ʣ�����%d\n",ex.numAllBlk,ex.numFreeBlk);

		String resultNameFront = "src/disk/NestLoopJoin/A_Result";//д��ʲô�ط�
		String rTableName = "src/disk/NestLoopJoin/R";
		String sTableName = "src/disk/NestLoopJoin/S";
		LinecompareTowTable(resultNameFront,rTableName,sTableName,rTupleNumber/7,sTupleNumber/7);
		System.out.printf("���Բ��� IO������%d\n",ex.numIO);
	}
	
	
	/**
	 *ֻ�����Բ��ҵ�һ���֣���buffer��߱Ƚ��������Ԫ���ֵ
	 *����Ϊ��ߵ�hashJoinҪ�õ������Ե���Ū��һ��������
	 *@param resultNameFront �ѽ��д��ʲô�ط�
	 *@param rTableName R��Ĵ��λ��
	 *@param sTableName S��Ĵ��λ��
	 *@param rRow R�������
	 *@param SRow S�������
	 * @throws IOException 
	 */
	static void LinecompareTowTable(String resultNameFront,String rTableName,String sTableName,int rRow,int sRow) throws IOException{
		System.out.printf("�����ļ��ĵ�ַ%s %s\n",rTableName,sTableName);
		ArrayList<Integer> sameValue = new ArrayList<>();
		Object bufferLineSave = ex.getNewBlockInBuffer();//��Ž��
		int saveBlockCondition = 0;
		int resultOrder=-1;
		for(int i=0;i<rRow;i++) {//��r���б���
			//�Ȱ�һ��R�Ž���,д������
			Object bufferLineR = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
			String rName = rTableName+String.valueOf(i)+".txt";
			ex.readBlockFromDisk((int)bufferLineR,rName);
			//System.out.printf("�Ž�R ʣ�����%d\n",ex.numFreeBlk);
			//�Ի����е��������ݽ��б���
			for(int si=0;si<sRow;si++) {//Ҫ�Ž���ô���S������ sTupleNumber/7
				//�ַŽ�һ��S����
				Object bufferLineS = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
				//System.out.printf("bufferʣ�����%d ���뵽������������%d\n",ex.numFreeBlk,(int)bufferLineS);
				String sName = sTableName+String.valueOf(si)+".txt";
				ex.readBlockFromDisk((int)bufferLineS,sName);
				/*
				System.out.printf("-------------�鿴������------------\n");
				for(int xx = 0;xx<8;xx++) {
					for(int yy = 0;yy<65;yy++) {
						System.out.printf(ex.data[xx][yy]+" ");
					}
					System.out.printf("\n");
				}*/
				for(int j = 0;j<56;j=j+8) {//j��������ֽ�Ϊλ��
					//ex.data[(int) bufferLine][j] Ҫ���������е����е��ֶαȽ�
					int oneValueA = ex.data[(int) bufferLineR][j];
					//System.out.printf("Aȡ�õ�buffer�к� A��ֵ:%d %d\n",(int) bufferLineR,oneValueA);
					int oneValueB = ex.data[(int) bufferLineR][j+4];
					//System.out.printf("R��ֵ%d \n",oneValueA);
					for(int sj = 0;sj<56;sj=sj+8) {
						int anotherValueC = ex.data[(int) bufferLineS][sj];
						int anotherValueD = ex.data[(int) bufferLineS][sj+4];
						//System.out.printf("C��ֵ:%d\n",anotherValueC);
						//System.out.printf("S��ֵ%d \n",anotherValue);
						if((oneValueA==anotherValueC) && !(sameValue.contains(oneValueA))) {
							System.out.printf("%d��ͬ R���%d ��ַ[%d,%d] S���%d ��ַ[%d,%d]\n",oneValueA,i,j,j+4,si,sj,sj+4);
							sameValue.add(oneValueA);
							//�ҵ���ͬ�ľ�д��ȥ����дR��дS
							if(saveBlockCondition>55) {//���bufferд����
								//����д������
								resultOrder++;
								String resultName=resultNameFront+String.valueOf(resultOrder)+".txt";
								ex.writeBlockToDisk((int)bufferLineSave,resultName);
								//������һ���µ�
								//System.out.printf("�ͷ�д�������Ժ� ʣ�����%d\n",ex.numFreeBlk);
								bufferLineSave = ex.getNewBlockInBuffer();//��Ž��
								saveBlockCondition = 0;
								//System.out.printf("�������Ż������  ʣ�����%d\n",ex.numFreeBlk);
							}else {
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
										oneValueA;
								saveBlockCondition +=4;//4
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
										oneValueB;
								saveBlockCondition +=4;//8
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
												anotherValueC;
								saveBlockCondition +=4;//12
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
												anotherValueD;
								saveBlockCondition +=4;//16 ��һ����ʼ��16��
							}
						}
					}					
				}
				//��һ�б������ˣ��ͷŵ�
				ex.freeBlockInBuffer((int)bufferLineS);	
			}
			//�ͷ�R
			ex.freeBlockInBuffer((int)bufferLineR);	
		}
	}
	
	
	/**
	 *��Ϊ�����ĺ����������ǰ�飬���ڲ���ֵҪ���ˣ���Ϊ��ߵĸ�������û��������ȥ��
	 * @throws IOException 
	 */
	static void SortMergeJoin() throws IOException {
		//Step1������
		sortData(rTupleNumber,rTable);
		sortData(sTupleNumber,sTable);
		//Step2:�Ȱ�R���S��д���������
		writeAllBufferToDisk(rTupleNumber,rTable,"src/disk/SortMergeJoin/R");
		writeAllBufferToDisk(sTupleNumber,sTable,"src/disk/SortMergeJoin/S");
		//Step3��copyǰ��Ƕ�ײ��ҵķ��������ǲ�����ֹͣ����
		System.out.printf("Step2 �ܿ��� %d ʣ�����%d\n",ex.numAllBlk,ex.numFreeBlk);
		
		Object bufferLineSave = ex.getNewBlockInBuffer();//��Ž��
		int saveBlockCondition = 0;
		int resultOrder=-1;
		String resultNameFront = "src/disk/SortMergeJoin/A_Result";
		for(int i=0;i<rTupleNumber/7;i++) {//��r���б���
			System.out.printf("-------------------�µ�һ��-----------------\n");
			//�Ȱ�һ��R�Ž���,д������
			Object bufferLineR = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
			String rName = "src/disk/SortMergeJoin/R"+String.valueOf(i)+".txt";
			ex.readBlockFromDisk((int)bufferLineR,rName);
			//System.out.printf("�Ž�R ʣ�����%d\n",ex.numFreeBlk);
			//�Ի����е��������ݽ��б���
			int breakCondition=0;//�費��Ҫֹͣ
			for(int si=0;(si<sTupleNumber/7 && breakCondition==0);si++) {//Ҫ�Ž���ô���S������ sTupleNumber/7
				//�ַŽ�һ��S����
				System.out.printf("S��� %d\n",si);
				Object bufferLineS = ex.getNewBlockInBuffer();//ȡ��һ������ռ�
				//System.out.printf("bufferʣ�����%d ���뵽������������%d\n",ex.numFreeBlk,(int)bufferLineS);
				String sName = "src/disk/SortMergeJoin/S"+String.valueOf(si)+".txt";
				ex.readBlockFromDisk((int)bufferLineS,sName);
				for(int j = 0;j<56;j=j+8) {//j��������ֽ�Ϊλ��
					//ex.data[(int) bufferLine][j] Ҫ���������е����е��ֶαȽ�
					int oneValueA = ex.data[(int) bufferLineR][j];
					int oneValueB = ex.data[(int) bufferLineR][j+4];
					//System.out.printf("R��ֵ%d \n",oneValueA);
					for(int sj = 0;sj<56;sj=sj+8) {
						int anotherValueC = ex.data[(int) bufferLineS][sj];
						int anotherValueD = ex.data[(int) bufferLineS][sj+4];
						//System.out.printf("S��ֵ%d \n",anotherValue);
						if(oneValueA==anotherValueC) {
							System.out.printf("%d��ͬ R���%d ��ַ[%d,%d] S���%d ��ַ[%d,%d]\n",oneValueA,i,j,j+4,si,sj,sj+4);
							breakCondition=1;
							//�ҵ���ͬ�ľ�д��ȥ����дR��дS
							if(saveBlockCondition>55) {//���bufferд����
								//����д������
								resultOrder++;
								String resultName=resultNameFront+String.valueOf(resultOrder)+".txt";
								ex.writeBlockToDisk((int)bufferLineSave,resultName);
								//������һ���µ�
								//System.out.printf("�ͷ�д�������Ժ� ʣ�����%d\n",ex.numFreeBlk);
								bufferLineSave = ex.getNewBlockInBuffer();//��Ž��
								saveBlockCondition = 0;
								//System.out.printf("�������Ż������  ʣ�����%d\n",ex.numFreeBlk);
							}else {
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
										oneValueA;
								saveBlockCondition +=4;//4
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
										oneValueB;
								saveBlockCondition +=4;//8
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
												anotherValueC;
								saveBlockCondition +=4;//12
								ex.data[(int) bufferLineSave][saveBlockCondition]=ex.data[(int) bufferLineSave][saveBlockCondition+1]=
										ex.data[(int) bufferLineSave][saveBlockCondition+2]=ex.data[(int) bufferLineSave][saveBlockCondition+3]=
												anotherValueD;
								saveBlockCondition +=4;//16 ��һ����ʼ��16��
							}
						}
					}					
				}
				//��һ�б������ˣ��ͷŵ�
				ex.freeBlockInBuffer((int)bufferLineS);	
			}
			//�ͷ�R
			ex.freeBlockInBuffer((int)bufferLineR);	
		}
		System.out.printf("���Բ��� IO������%d\n",ex.numIO-152);
	}
	
	static void HashJoin() throws IOException {
		//Step1:�����ݿ�Ž�Ӳ��
		writeAllBufferToDisk(rTupleNumber,rTable,"src/disk/HashJoin/R/");
		writeAllBufferToDisk(sTupleNumber,sTable,"src/disk/HashJoin/S/");
		System.out.printf("Step1 �ܿ��� %d ʣ�����%d\n",ex.numAllBlk,ex.numFreeBlk);
		//step2:R��S�������ݶ��ֳ������֣�������ż��
		//����R���飬���ֱ�����ڴ��ź���ż�Ŀ����Ŀ¼��
		int rFileNameOrder[] = divideBucket(rTupleNumber,"R");
		int sFileNameOrder[] =divideBucket(sTupleNumber,"S");
		System.out.printf("Step2 �ܿ��� %d ʣ�����%d\n",ex.numAllBlk,ex.numFreeBlk);
		//Step3:ȥĿ¼��ߣ�ȡ��hash�õĿ飬ʣ�µľ�������join
		
		//��һ�����Բ���
		//�����Ƚ�
		String oddResultNameFront = "src/disk/HashJoin/Result/";//д��ʲô�ط�
		String evenResultNameFront = "src/disk/HashJoin/Result/";//д��ʲô�ط�
		LinecompareTowTable(oddResultNameFront,"src/disk/HashJoin/R/O","src/disk/HashJoin/S/O",rFileNameOrder[1],sFileNameOrder[1]);
		LinecompareTowTable(evenResultNameFront,"src/disk/HashJoin/R/E","src/disk/HashJoin/S/E",rFileNameOrder[0],sFileNameOrder[0]);
		System.out.printf("HashJoin IO������%d\n",ex.numIO);
		
	}
	
	//����Ͱ
	/**
	 *@param :TupleNumber �������
	 *@param tableType: S or R
	 * @throws IOException 
	 */
	static int[] divideBucket(int TupleNumber,String tableType) throws IOException {
		String bucket;//Ͱ������
		int Ocondition=0;
		int Econdition=0;
		int oodOrder=-1;
		int evenOrder=-1;
		int fileNameOrder[] = {0,0};
		String oddFrontName = "src/disk/HashJoin/"+tableType+"/O";
		String evenFrontName = "src/disk/HashJoin/"+tableType+"/E";
		String diskName = "src/disk/HashJoin/"+tableType+"/";
		for(int i=0;i<TupleNumber/7;i++) {//�ж��ٿ�
			//����һ����
			//System.out.printf("divideBucket �ܿ��� %d ʣ�����%d\n",ex.numAllBlk,ex.numFreeBlk);
			Object bufferLine = ex.getNewBlockInBuffer();
			String rName = diskName+String.valueOf(i)+".txt";
			ex.readBlockFromDisk((int)bufferLine,rName);
			for(int j = 0;j<56;j=j+8) {//�Կ���ߵ�Ԫ��� ���Խ��б���
				bucket = judgeOddEven(ex.data[(int) bufferLine][j]);//�жϵ�����
				if(bucket.equals("O")) {//������ʹ�õ�No6��Ͱ
					//������д���ض�����
					if(Ocondition>55) {//Ocondition=56
						//����ߵ�����д������
						oodOrder++;
						ex.data[6][64]=1;
						String resultName=oddFrontName+String.valueOf(oodOrder)+".txt";
						ex.writeBlockToDisk(6,resultName);
						ex.numFreeBlk--;
						Ocondition=0;
						//������һ���µ�
						//System.out.printf("�ͷ�д�������Ժ� ʣ�����%d\n",ex.numFreeBlk);
					}
					ex.data[6][Ocondition]=ex.data[6][ Ocondition+1]=
							ex.data[6][ Ocondition+2]=ex.data[6][ Ocondition+3]=
							ex.data[(int) bufferLine][j];
					Ocondition+=4;//4
					ex.data[6][Ocondition]=ex.data[6][ Ocondition+1]=
							ex.data[6][ Ocondition+2]=ex.data[6][ Ocondition+3]=
							ex.data[(int) bufferLine][j+4];
					Ocondition+=4;//4
				}else {//ż��ʹ�õ�No7��Ͱ
					//������д���ض�����
					if(Econdition>55) {//Ocondition=56
						//����ߵ�����д������
						evenOrder++;
						ex.data[7][64]=1;
						String resultName=evenFrontName+String.valueOf(evenOrder)+".txt";
						ex.writeBlockToDisk(7,resultName);
						ex.numFreeBlk--;
						Econdition=0;
						//System.out.printf("�ͷ�д�������Ժ� ʣ�����%d\n",ex.numFreeBlk);
					}
					ex.data[7][Econdition]=ex.data[7][Econdition+1]=
							ex.data[7][Econdition+2]=ex.data[7][Econdition+3]=
							ex.data[(int) bufferLine][j];
					Econdition+=4;//4
					ex.data[7][Econdition]=ex.data[7][Econdition+1]=
							ex.data[7][Econdition+2]=ex.data[7][Econdition+3]=
							ex.data[(int) bufferLine][j+4];
					Econdition+=4;//4
				}
			}
			//�����buffer�ͷ�
			//�ͷ�R
			ex.freeBlockInBuffer((int)bufferLine);	
		}
		//��6��7��������д������
		/*
		System.out.printf("-------------�鿴������------------\n");
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(ex.data[i][j]+" ");
			}
			System.out.printf("\n");
		}*/
		
		if(ex.data[7][64]==1) {
			evenOrder++;
			String resultName=evenFrontName+String.valueOf(evenOrder)+".txt";
			ex.writeBlockToDisk(7,resultName);
			ex.numFreeBlk--;
		}else {
			oodOrder++;
			String resultName=oddFrontName+String.valueOf(oodOrder)+".txt";
			ex.writeBlockToDisk(6,resultName);
			ex.numFreeBlk--;
		}
		
		fileNameOrder[0] = evenOrder;
		fileNameOrder[1] = oodOrder;
		return fileNameOrder;
	}
	
	//�ж�Ԫ�������ֵ����������ż�� odd even
	static String judgeOddEven(int val){
		if(val%2==1) {
			return "O";
		}else {
			return "E";
		}
	}
}
