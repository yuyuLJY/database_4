package database_4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class mainP {
	static int sTupleNumber = 224;
	static int rTupleNumber = 112;
	static int sTable[][] = new int[sTupleNumber][2];
	static int rTable[][] = new int[rTupleNumber][2];
	static extmem ex = new extmem();
	
	public static void main(String[] args) throws IOException {
		int bufsize = 520;//buffer�������Ĵ�С
		int blksize = 64;//��Ĵ�С
		ex.initBuffer(bufsize, blksize);//��ʼ��������
		//����R��S������
		createTableData();
		//TODO Ӧ�������㷨����������  R.A=40
		LineSearch();
		
		//TODO Ӧ�������㷨����������
		//TODO Ӧ�������㷨��B+��
		//TODO ͶӰ 
		//TODO 
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
					ex.writeBlockToDisk(k,diskOrder);
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
					ex.readBlockFromDisk(i,diskOrder);
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
	
}
