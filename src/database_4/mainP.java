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
		int bufsize = 520;//buffer缓冲区的大小
		int blksize = 64;//块的大小
		ex.initBuffer(bufsize, blksize);//初始化缓冲区
		//生成R和S的数据
		createTableData();
		//TODO 应用搜索算法：线性搜索  R.A=40
		LineSearch();
		
		//TODO 应用搜索算法：二分搜索
		//TODO 应用搜索算法：B+树
		//TODO 投影 
		//TODO 
		/*
		System.out.println("-------------函数Main-----------------");
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

		String sfileName = "src/table/Stable.txt";
		String rfileName = "src/table/Rtable.txt";
		rTable = readData(rfileName,rTupleNumber);
		sTable = readData(sfileName,sTupleNumber);
		//验证读取的效果
		/*
		System.out.println("打印S表的值");
		for(int i=0;i<sTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(sTable[i][j]+" ");
			}
			System.out.print("\n");
		}
		System.out.println("----------------------------------");
		System.out.println("打印R表的值");
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(rTable[i][j]+" ");
			}
			System.out.print("\n");
		}*/
	}
	
	static int[][] readData(String fileName,int TupleNumber) {
		//TODO 读取文件里边的table信息
		int table[][] = new int[TupleNumber][2];
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			// 按行读取字符串
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
		 * 线性搜索，R.A=40。先排序，然后按照大小顺序放进磁盘，装满
		 */
		System.out.println("-------------函数LineSearch-----------------");
		//Step1:先进行排序
		sortData();
		/*
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<2;j++) {
				System.out.print(rTable[i][j]+" ");
			}
			System.out.print("\n");
		}*/
		//Step2：先写进缓冲，再写进磁盘
		int diskOrder=-1;
		for(int i=0;i<rTupleNumber;i=i+7) {//行
			//System.out.printf("遍历第%d次\n",i);
			Object lineNumber = ex.getNewBlockInBuffer();
			if(lineNumber!=null) {//缓冲区没有满
				int target = 0;//标记，赋值缓冲区到什么位置了
				int line = (int) lineNumber;
				//System.out.printf("申请到的列标：%d\n",line);
				for(int j = i;j<i+7;j++){
					ex.data[line][target]=ex.data[line][target+1]=
					ex.data[line][target+2]=ex.data[line][target+3]=rTable[j][0];
					ex.data[line][target+4]=ex.data[line][target+5]=
					ex.data[line][target+6]=ex.data[line][target+7]=rTable[j][1];
					target+=8;
				}
			}else {
				//System.out.printf("缓冲空间用完了\n");
				//把缓冲区的内容写进磁盘
				for(int k=0;k<8;k++) {
					diskOrder++;
					ex.writeBlockToDisk(k,diskOrder);
				}
				i = i-7;
			}
		}
		//Step3：开始查找R.A=40
		//遍历缓冲区有记录的地方，如果找不到，就清空缓冲区，再从缓冲区读进来
		int flag = 0;
		while(flag!=1) {//还没找到
			//先去缓冲区找
			for(int i = 0;i<8;i++) {
				if(ex.data[i][64]==1) {//有数据
					for(int j = 0;j<56;j=j+8) {
						int oneA = ex.data[i][j];
						if(oneA==40) {//找到了
							System.out.printf("找到数据!!!在缓冲区的%d块 index[%d,%d]\n",i,j,j+3);
							flag = 1;
						}
					}
				}
			}
			//缓冲区没有,先释放缓冲区，从磁盘写进缓冲区
			if(flag==0) {
				for(int i = 0;i<8;i++) {
					ex.freeBlockInBuffer(i); 
				}
				//理想是读进8块，但是不一定有8块
				for(int i = 0;i<8 && diskOrder>=0;i++) {
					ex.readBlockFromDisk(i,diskOrder);
					diskOrder--;
				}
			}
		}
		System.out.printf("线性查找 IO次数：%d\n",ex.numIO);
		
		//打印查看效果
		System.out.printf("-------------查看缓冲区------------\n");
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
		 *对二维数组的第一列，进行简单的选择排序 
		 */
		for(int i=0;i<rTupleNumber;i++) {
			for(int j = 0;j<rTupleNumber;j++) {
				if(rTable[i][0]>rTable[j][0]) {//第一个数字>第二个数字
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
