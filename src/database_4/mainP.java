package database_4;

import java.util.Random;

public class mainP {
	public static void main(String[] args) {
		extmem t = new extmem();
		int bufSize = 520;//buffer缓冲区的大小
		int blkSize = 64;//块的大小
		t.initBuffer(bufSize, blkSize);//初始化缓冲区
		//TODO 生成R和S的数据
		createTableData();
		//TODO
	}
	
	static void createTableData() {
		/*
		 *创建R和S表的初始数据。
		 *然后写进txt里边，下次直接从txt里边读出来
		 **/
		/*
		int sTupleNumber = 224;
		int rTupleNumber = 112;
		int sTable[][] = new int[sTupleNumber][2];
		int rTable[][] = new int[rTupleNumber][2];
		//TODO 填写s表:C的值域为[20, 60]，D的值域为[1, 1000]
		
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
		
		//TODO 填写r表:A的值域为[1, 40]，B的值域为[1, 1000]
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
		
	    //把造好的数组打印出来
		System.out.println("打印R表的值");
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(rTable[i][j]+" ");
			}
			System.out.print("\n");
		}
		*/
		
		//TODO 直接从文件里边读进去
		
	}
}
