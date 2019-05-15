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
	static ArrayList<int[][]> BinaryFindBlockList = new ArrayList<>();// 存放所有的块
	static ArrayList<int[]> BinaryIndex = new ArrayList<>();
	static int soilder = 0;
	
	
	public static void main(String[] args) throws IOException {
		int bufsize = 520;//buffer缓冲区的大小
		int blksize = 64;//块的大小
		ex.initBuffer(bufsize, blksize);//初始化缓冲区
		//生成R和S的数据
		createTableData();
		//应用搜索算法：线性搜索  R.A=40
		//LineSearch();
		
		//TODO 应用搜索算法：B+树
		//bTreeSearch();
		
		//应用搜索算法：二分搜索
		//BinaryFind();
		//TODO 投影
		
		//TODO 连接
		
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
					String fileName = "src/disk/lineFind/"+String.valueOf(diskOrder)+".txt";
					ex.writeBlockToDisk(k,fileName);
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
					String fileName = "src/disk/lineFind/"+String.valueOf(diskOrder)+".txt";
					ex.readBlockFromDisk(i,fileName);
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
	
	static void bTreeSearch() {
		/**
		 * 使用B-树，重要的是索引
		 */
	}
	
	static void BinaryFind() throws IOException {
		//Step1:先对数据进行排序
		sortData();
		//Step2：对数据进行分块
		for(int i = 0;i<rTupleNumber;i=i+7) {
			//System.out.printf("新的一轮 i=%d\n",i);
			int count=0;
			int diskOne[][] = new int[7][2];
			for(int j = i;count<7;j++) {
				//创建一个新的数组
				//System.out.printf("%d行数据 %d %d  写入第%d行\n",j,rTable[j][0],rTable[j][1],j%7);
				diskOne[j%7][0] = rTable[j][0];
				diskOne[j%7][1] = rTable[j][1];
				count++;
			}
			BinaryFindBlockList.add(diskOne);
			//把组好的数组放置好
		}
		//检查块的信息是否正确
		/*
		for(int i =0;i<BinaryFindBlockList.size();i++) {
			//遍历打印二维数组
			System.out.printf("遍历第%d块\n",i);
			for(int j = 0;j<7;j++) {
				System.out.printf("["+BinaryFindBlockList.get(i)[j][0]+","+BinaryFindBlockList.get(i)[j][1]+"]");
				System.out.printf(" ");
			}
			System.out.printf("\n");
		}
		*/
		//Step3：TODO 对块构建索引,二叉树
		createBinaryTree(0,15);//一共16块 112/7
		//检验建的Index
		System.out.println("index情况");
		for(int i = 0;i<BinaryIndex.size();i++) {
			System.out.println(Arrays.toString(BinaryIndex.get(i)));
		}
		
		//Step4:放进磁盘
		//int diskOrder=-1;
		for(int i = BinaryIndex.size()-1;i>-1;i--) {//倒着来
			System.out.println("index "+Arrays.toString(BinaryIndex.get(i)));
			for(int arrayNumber = 1;arrayNumber<3;arrayNumber++) {
				int nodeOrder = BinaryIndex.get(i)[arrayNumber];//[14, 1000, 15]
				if(nodeOrder!=1000) {//真的有结点
					int saveBlock[][] = BinaryFindBlockList.get(nodeOrder);//一共有七行
					Object lineNumber = ex.getNewBlockInBuffer();//取得一个缓冲空间
					if(lineNumber==null) {
						for(int k=0;k<8;k++) {
							String fileName = "src/disk/BinaryFind/"+String.valueOf(ex.data[k][56])+".txt";
							ex.writeBlockToDisk(k,fileName);
						}
						//重新申请一个缓冲空间
						lineNumber = ex.getNewBlockInBuffer();//取得一个缓冲空间
					}
					int line = (int) lineNumber;
					//System.out.printf("申请到的列标：%d\n",line);
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
		System.out.printf("线性查找 IO次数：%d\n",ex.numIO);
		//把顶点写进去
		int nodeOrder = BinaryIndex.get(0)[0];
		int saveBlock[][] = BinaryFindBlockList.get(nodeOrder);//一共有七行
		Object lineNumber = ex.getNewBlockInBuffer();
		if(lineNumber==null) {
			for(int k=0;k<8;k++) {
				String fileName = "src/disk/BinaryFind/"+String.valueOf(nodeOrder)+".txt";
				ex.writeBlockToDisk(k,fileName);
			}
			//重新申请一个缓冲空间
			lineNumber = ex.getNewBlockInBuffer();//取得一个缓冲空间
		}
		int line = (int) lineNumber;
		//System.out.printf("申请到的列标：%d\n",line);
		for(int j = 0;j<56;j=j+8){
			ex.data[line][j]=ex.data[line][j+1]=
			ex.data[line][j+2]=ex.data[line][j+3]=saveBlock[j/8][0];
			ex.data[line][j+4]=ex.data[line][j+5]=
			ex.data[line][j+6]=ex.data[line][j+7]=saveBlock[j/8][1];
		}
		ex.data[line][56]=nodeOrder;
		//把缓冲区的再写进去
		//System.out.printf("还剩块数：%d",ex.numAllBlk-ex.numFreeBlk);
		int blokNow = ex.numAllBlk-ex.numFreeBlk;
		//不能写成 for(int k=0; k<ex.numAllBlk-ex.numFreeBlk; k++) {
		//因为可用的块数是static，是动态变化的
		for(int k=0; k<blokNow; k++) {
			String fileName = "src/disk/BinaryFind/"+String.valueOf(ex.data[k][56])+".txt";
			//System.out.printf("清空块数：%d\n",k);
			ex.writeBlockToDisk(k,fileName);
		}
		
		//Step5:开始查询
		//把第一块调进来，剩下的依次调进来
		Object bufferLine = ex.getNewBlockInBuffer();//因为一开始缓冲是空的，肯定有空间
		int currentDisk = BinaryIndex.get(0)[0];//头结点
		String fileName = "src/disk/BinaryFind/"+String.valueOf( currentDisk)+".txt";
		ex.readBlockFromDisk((int)bufferLine,fileName);
		int flag = 0;
		while(flag!=1) {//还没找到
			//先去缓冲区找	
			System.out.printf("调入磁盘块%d\n",currentDisk);
			int direction =0;
			for(int j = 0;j<56;j=j+8) {
				int oneA = ex.data[(int) bufferLine][j];
				if(oneA==40) {//找到了
					System.out.printf("找到数据!!!在缓冲区的%d块 index[%d,%d]\n",currentDisk,j,j+3);
					flag = 1;
				}
			}
			//调入下一块
			if(flag==0) {
				//判断大小
				if(40<ex.data[(int) bufferLine][0]) {
					direction=1;//左结点
				}else {
					direction=2;//右结点
				}
				currentDisk= getNextDiskOrder(currentDisk,direction);
				bufferLine = ex.getNewBlockInBuffer();//取得一个缓冲空间
				if(bufferLine==null) {//清空缓冲区
					for(int i = 0;i<8;i++) {
						ex.freeBlockInBuffer(i); 
					}
					bufferLine = ex.getNewBlockInBuffer();//取得一个缓冲空间
				}
				fileName = "src/disk/BinaryFind/"+String.valueOf(currentDisk)+".txt";
				ex.readBlockFromDisk((int)bufferLine,fileName);
			}
		}
		
		//打印查看效果
		System.out.printf("-------------查看缓冲区------------\n");
		int dataCopy[][]=ex.data;
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(dataCopy[i][j]+" ");
			}
			System.out.printf("\n");
		}
		System.out.printf("线性查找 IO次数：%d\n",ex.numIO+8);
		
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
		//选出中间的那块
		//BinaryIndex
		//System.out.printf("士兵：%d start+end %d %d\n",soilder,start,end);
		if(start!=end && start<=end) {
			int oneIndex[] = new int[3];
			int mid = start+(end-start)/2;// 15/2 =7 ,取第8块
			int left;
			int right;
			if(mid-1<start) {//没有左结点
				left = 1000;
			}else {
				left = start+(mid-1-start)/2;
			}
			if(mid+1>end) {//没有右结点
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
		 * 功能：R上的A属性进行投影，并将结果存放在磁盘上
		 */
		
		
		
	}
}
