package database_4;

import java.util.Random;

public class mainP {
	public static void main(String[] args) {
		extmem t = new extmem();
		int bufSize = 520;//buffer�������Ĵ�С
		int blkSize = 64;//��Ĵ�С
		t.initBuffer(bufSize, blkSize);//��ʼ��������
		//TODO ����R��S������
		createTableData();
		//TODO
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
		
		//TODO ֱ�Ӵ��ļ���߶���ȥ
		
	}
}
