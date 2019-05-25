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
		
		//应用搜索算法：B+树
		//bTreeSearch();
		
		//应用搜索算法：
		//BinaryFind();
		//投影
		//selectColumn();
		
		//Nest-Loop- Join算法
		//NestLoopJoin();
		
		//Sort-Merge-Join算法
		//SortMergeJoin();
		//Hash-Join算法
		//HashJoin();
		
		//TODO 内存的排序算法
		
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
		sortData(rTupleNumber,rTable);
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
		System.out.printf("IO次数：%d\n",ex.numIO+16);
		
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
	
	static void sortData(int column,int a[][]){
		/**
		 *对二维数组的第一列，进行简单的选择排序 
		 */
		for(int i=0;i<column;i++) {
			for(int j = 0;j<column;j++) {
				if(a[i][0]<a[j][0]) {//第一个数字>第二个数字 //从小到大
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
		 * 使用B-树，重要的是索引
		 */
		
		//Step5:开始查询
		//把第一块调进来，剩下的依次调进来
		Object bufferLine = ex.getNewBlockInBuffer();//因为一开始缓冲是空的，肯定有空间
		int currentDisk = 0;//头结点
		String fileName = "src/disk/BTreeFind/"+String.valueOf( currentDisk)+".txt";
		ex.readBlockFromDisk((int)bufferLine,fileName);
		int flag = 0;
		int count=0;
		while(flag != 1) {//还没找到
			//先去缓冲区找	
			count++;
			System.out.printf("调入磁盘块%d\n",currentDisk);
			int direction =0;//从哪里取出下一块
			if(ex.data[(int) bufferLine][56]!=0) {//这是个索引盘
				int findNextFlag=0;
				int condition =0;//跟哪个index值比较
				while(findNextFlag!=1) {
					if(40<ex.data[(int) bufferLine][condition]) {
						direction=condition+56;
					}
					condition++;
					if(ex.data[(int) bufferLine][condition]==999999) {//已经遍历到最后一个了
						direction=condition+56;
						findNextFlag=1;
					}
				}
			}else {//是数据盘
				for(int j = 0;j<56;j=j+8) {
					int oneA = ex.data[(int) bufferLine][j];
					if(oneA==40) {//找到了
						System.out.printf("找到数据!!!在缓冲区的%d块 index[%d,%d]\n",currentDisk,j,j+3);
						flag = 1;
					}
				}
			}
			//调入下一块
			if(flag==0) {
				//判断大小
				/*
				if(40<ex.data[(int) bufferLine][0]) {
					direction=1;//左结点
				}else {
					direction=2;//右结点
				}*/
				currentDisk= ex.data[(int) bufferLine][direction];
				bufferLine = ex.getNewBlockInBuffer();//取得一个缓冲空间
				if(bufferLine==null) {//清空缓冲区
					for(int i = 0;i<8;i++) {
						ex.freeBlockInBuffer(i); 
					}
					bufferLine = ex.getNewBlockInBuffer();//取得一个缓冲空间
				}
				fileName = "src/disk/BTreeFind/"+String.valueOf(currentDisk)+".txt";
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
		System.out.printf("IO次数：%d\n",ex.numIO+26);
	}
	
	static void BinaryFind() throws IOException {
		//Step1:先对数据进行排序
		sortData(rTupleNumber,rTable);
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
		System.out.printf("IO次数：%d\n",ex.numIO);
		
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
	
	static void selectColumn() throws IOException {
		/**
		 * 功能：R上的A属性进行投影，并将结果存放在磁盘上
		 */
		//Step1:把数据全部写进磁盘里边
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
		//打印查看效果
		/*
		System.out.printf("-------------查看缓冲区------------\n");
		int dataCopy[][]=ex.data;
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(dataCopy[i][j]+" ");
			}
			System.out.printf("\n");
		}*/
		System.out.printf("IO次数：%d\n",ex.numIO);
		
		//Step2:计算流出多少块来存放数据最合适
		//两行就需要一行的输出缓冲：2行作为存放结果的缓冲，4行作为输入数据
		String fileName;
		int circleNum = (diskOrder+1)/4;
		int sepecialResultOrder = 99;
		for(int cirle = 0;cirle<circleNum;cirle++) {//要进行计次轮回
			for(int i=cirle*4;i<(cirle+1)*4;i++) {//[0 1 2 3] [4 5 6 7]
				Object bufferLine = ex.getNewBlockInBuffer();//取得一个缓冲空间
				fileName = "src/disk/selectColunm/"+String.valueOf(i)+".txt";
				ex.readBlockFromDisk((int)bufferLine,fileName);
			}
			System.out.printf("buffer状态：%d %d\n",ex.numAllBlk,ex.numFreeBlk);
			/*
			System.out.printf("-------------存了四块以后的查看缓冲区------------\n");
			for(int i = 0;i<8;i++) {
				for(int j = 0;j<65;j++) {
					System.out.printf(dataCopy[i][j]+" ");
				}
				System.out.printf("\n");
			}*/
			//对有数据的4行进行遍历
			Object bufferLine = ex.getNewBlockInBuffer();//取得一个缓冲空间
			int condition =0;
			for(int i=0;i<4;i++) {//对buffer的行进行遍历
				for(int j = 0;j<56;j=j+8) {//对某个blk进行遍历
					ex.data[(int) bufferLine][condition] = ex.data[(int) bufferLine][condition+1]=
							ex.data[(int) bufferLine][condition+2] = ex.data[(int) bufferLine][condition+3]=
							ex.data[i][j];
					condition+=4;
				}
				System.out.printf("condition %d\n",condition);
				if(condition>55) {//需要重新申请空间了
					//先把原来那块写进磁盘
					//System.out.printf("写buffer换掉 %d\n",i);
					sepecialResultOrder++;
					fileName = "src/disk/selectColunm/"+String.valueOf(sepecialResultOrder)+".txt";
					ex.writeBlockToDisk((int)bufferLine,fileName);
					//重新申请,多申请了一个
					bufferLine = ex.getNewBlockInBuffer();//取得一个缓冲空间
					condition =0;
				}	
			}
			/*
			System.out.printf("-------------查看缓冲区------------\n");
			for(int i = 0;i<8;i++) {
				for(int j = 0;j<65;j++) {
					System.out.printf(dataCopy[i][j]+" ");
				}
				System.out.printf("\n");
			}*/
			
			//清空缓冲区
			//TODO 有问题 上帝视角
			//int nowBlock = ex.numAllBlk-ex.numFreeBlk;
			//System.out.printf("需要清空：%d %d %d\n",ex.numAllBlk,ex.numFreeBlk,nowBlock);
			for(int kkk = 0;kkk<5;kkk++) {
				ex.freeBlockInBuffer(kkk); 
			}
			

		}
		System.out.printf("-------------查看缓冲区------------\n");
		for(int i = 0;i<8;i++) {
			for(int j = 0;j<65;j++) {
				System.out.printf(ex.data[i][j]+" ");
			}
			System.out.printf("\n");
		}
		System.out.printf("IO次数：%d\n",ex.numIO);
	}
	
	//专门给Join用的
	/**
	 *@param column行数
	 *@param a 二维数组，使用哪个表
	 *@param fileName 使用的文件的名字
	 */
	static void writeAllBufferToDisk(int colunm,int a[][],String frontFileName) throws IOException {
		int diskOrder=-1;
		//System.out.printf("函数%s 总块数 %d 剩余可用%d\n",frontFileName,ex.numAllBlk,ex.numFreeBlk);
		for(int i=0;i<colunm;i=i+7) {//行
			//System.out.printf("遍历第%d次\n",i);
			//System.out.printf("总块数 %d 剩余可用%d\n",ex.numAllBlk,ex.numFreeBlk);
			Object lineNumber = ex.getNewBlockInBuffer();
			if(lineNumber!=null) {//缓冲区没有满
				int target = 0;//标记，赋值缓冲区到什么位置了
				int line = (int) lineNumber;
				//System.out.printf("申请到的列标：%d\n",line);
				for(int j = i;j<i+7;j++){
					ex.data[line][target]=ex.data[line][target+1]=
					ex.data[line][target+2]=ex.data[line][target+3]=a[j][0];
					ex.data[line][target+4]=ex.data[line][target+5]=
					ex.data[line][target+6]=ex.data[line][target+7]=a[j][1];
					target+=8;
				}
			}else {
				//System.out.printf("缓冲空间用完了\n");
				//把缓冲区的内容写进磁盘
				for(int k=0;k<8;k++) {
					diskOrder++;
					ex.data[k][64]=1;
					String fileName = frontFileName+String.valueOf(diskOrder)+".txt";
					ex.writeBlockToDisk(k,fileName);
				}
				i = i-7;
			}
		}
		//System.out.printf("总块数 %d 剩余可用%d\n",ex.numAllBlk,ex.numFreeBlk);
		int bufferBlockNow = ex.numAllBlk-ex.numFreeBlk;
		for(int k=0;k<bufferBlockNow;k++) {
			diskOrder++;
			String fileName = frontFileName+String.valueOf(diskOrder)+".txt";
			ex.writeBlockToDisk(k,fileName);
			//多余了!!!! 和writeBlockToDisk会导致两次freeblock++
			//ex.freeBlockInBuffer(k);
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
		System.out.printf("IO次数：%d\n",ex.numIO);
	}
	
	
	static void NestLoopJoin() throws IOException{
		/**
		 * 嵌套遍历查询
		 */
		
		//Step1:先把R表和S表都写进磁盘里边
		writeAllBufferToDisk(rTupleNumber,rTable,"src/disk/NestLoopJoin/R");
		writeAllBufferToDisk(sTupleNumber,sTable,"src/disk/NestLoopJoin/S");
		//Step2:取出R表的一行，与所有S表比较。做R表的行数次，每次都比较相关的属性是否相同
		//相同的就写进一块缓冲里边，记录下存储这些链接结果的缓冲的行号
		System.out.printf("Step2 总块数 %d 剩余可用%d\n",ex.numAllBlk,ex.numFreeBlk);

		String resultNameFront = "src/disk/NestLoopJoin/A_Result";//写到什么地方
		String rTableName = "src/disk/NestLoopJoin/R";
		String sTableName = "src/disk/NestLoopJoin/S";
		LinecompareTowTable(resultNameFront,rTableName,sTableName,rTupleNumber/7,sTupleNumber/7);
		System.out.printf("线性查找 IO次数：%d\n",ex.numIO);
	}
	
	
	/**
	 *只是线性查找的一部分，在buffer里边比较两个表的元组的值
	 *（因为后边的hashJoin要用到，所以单独弄成一个函数）
	 *@param resultNameFront 把结果写到什么地方
	 *@param rTableName R表的存放位置
	 *@param sTableName S表的存放位置
	 *@param rRow R表的行数
	 *@param SRow S表的行数
	 * @throws IOException 
	 */
	static void LinecompareTowTable(String resultNameFront,String rTableName,String sTableName,int rRow,int sRow) throws IOException{
		System.out.printf("两个文件的地址%s %s\n",rTableName,sTableName);
		ArrayList<Integer> sameValue = new ArrayList<>();
		Object bufferLineSave = ex.getNewBlockInBuffer();//存放结果
		int saveBlockCondition = 0;
		int resultOrder=-1;
		for(int i=0;i<rRow;i++) {//对r进行遍历
			//先把一块R放进来,写进缓冲
			Object bufferLineR = ex.getNewBlockInBuffer();//取得一个缓冲空间
			String rName = rTableName+String.valueOf(i)+".txt";
			ex.readBlockFromDisk((int)bufferLineR,rName);
			//System.out.printf("放进R 剩余可用%d\n",ex.numFreeBlk);
			//对缓冲中的这行数据进行遍历
			for(int si=0;si<sRow;si++) {//要放进那么多的S行数据 sTupleNumber/7
				//又放进一个S的行
				Object bufferLineS = ex.getNewBlockInBuffer();//取得一个缓冲空间
				//System.out.printf("buffer剩余可用%d 申请到缓冲区的行数%d\n",ex.numFreeBlk,(int)bufferLineS);
				String sName = sTableName+String.valueOf(si)+".txt";
				ex.readBlockFromDisk((int)bufferLineS,sName);
				/*
				System.out.printf("-------------查看缓冲区------------\n");
				for(int xx = 0;xx<8;xx++) {
					for(int yy = 0;yy<65;yy++) {
						System.out.printf(ex.data[xx][yy]+" ");
					}
					System.out.printf("\n");
				}*/
				for(int j = 0;j<56;j=j+8) {//j代表的是字节为位置
					//ex.data[(int) bufferLine][j] 要跟下面这行的所有的字段比较
					int oneValueA = ex.data[(int) bufferLineR][j];
					//System.out.printf("A取得的buffer行号 A的值:%d %d\n",(int) bufferLineR,oneValueA);
					int oneValueB = ex.data[(int) bufferLineR][j+4];
					//System.out.printf("R的值%d \n",oneValueA);
					for(int sj = 0;sj<56;sj=sj+8) {
						int anotherValueC = ex.data[(int) bufferLineS][sj];
						int anotherValueD = ex.data[(int) bufferLineS][sj+4];
						//System.out.printf("C的值:%d\n",anotherValueC);
						//System.out.printf("S的值%d \n",anotherValue);
						if((oneValueA==anotherValueC) && !(sameValue.contains(oneValueA))) {
							System.out.printf("%d相同 R块号%d 地址[%d,%d] S块号%d 地址[%d,%d]\n",oneValueA,i,j,j+4,si,sj,sj+4);
							sameValue.add(oneValueA);
							//找到相同的就写进去，先写R再写S
							if(saveBlockCondition>55) {//这块buffer写满了
								//把它写进磁盘
								resultOrder++;
								String resultName=resultNameFront+String.valueOf(resultOrder)+".txt";
								ex.writeBlockToDisk((int)bufferLineSave,resultName);
								//再申请一块新的
								//System.out.printf("释放写的那行以后 剩余可用%d\n",ex.numFreeBlk);
								bufferLineSave = ex.getNewBlockInBuffer();//存放结果
								saveBlockCondition = 0;
								//System.out.printf("再申请存放缓冲的行  剩余可用%d\n",ex.numFreeBlk);
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
								saveBlockCondition +=4;//16 下一个开始是16了
							}
						}
					}					
				}
				//这一行遍历完了，释放掉
				ex.freeBlockInBuffer((int)bufferLineS);	
			}
			//释放R
			ex.freeBlockInBuffer((int)bufferLineR);	
		}
	}
	
	
	/**
	 *因为事先拍好了序，如果当前块，大于查找值要求了，因为后边的更大，所以没有意义再去找
	 * @throws IOException 
	 */
	static void SortMergeJoin() throws IOException {
		//Step1：排序
		sortData(rTupleNumber,rTable);
		sortData(sTupleNumber,sTable);
		//Step2:先把R表和S表都写进磁盘里边
		writeAllBufferToDisk(rTupleNumber,rTable,"src/disk/SortMergeJoin/R");
		writeAllBufferToDisk(sTupleNumber,sTable,"src/disk/SortMergeJoin/S");
		//Step3：copy前边嵌套查找的方法，但是不设置停止条件
		System.out.printf("Step2 总块数 %d 剩余可用%d\n",ex.numAllBlk,ex.numFreeBlk);
		
		Object bufferLineSave = ex.getNewBlockInBuffer();//存放结果
		int saveBlockCondition = 0;
		int resultOrder=-1;
		String resultNameFront = "src/disk/SortMergeJoin/A_Result";
		for(int i=0;i<rTupleNumber/7;i++) {//对r进行遍历
			System.out.printf("-------------------新的一块-----------------\n");
			//先把一块R放进来,写进缓冲
			Object bufferLineR = ex.getNewBlockInBuffer();//取得一个缓冲空间
			String rName = "src/disk/SortMergeJoin/R"+String.valueOf(i)+".txt";
			ex.readBlockFromDisk((int)bufferLineR,rName);
			//System.out.printf("放进R 剩余可用%d\n",ex.numFreeBlk);
			//对缓冲中的这行数据进行遍历
			int breakCondition=0;//需不需要停止
			for(int si=0;(si<sTupleNumber/7 && breakCondition==0);si++) {//要放进那么多的S行数据 sTupleNumber/7
				//又放进一个S的行
				System.out.printf("S块号 %d\n",si);
				Object bufferLineS = ex.getNewBlockInBuffer();//取得一个缓冲空间
				//System.out.printf("buffer剩余可用%d 申请到缓冲区的行数%d\n",ex.numFreeBlk,(int)bufferLineS);
				String sName = "src/disk/SortMergeJoin/S"+String.valueOf(si)+".txt";
				ex.readBlockFromDisk((int)bufferLineS,sName);
				for(int j = 0;j<56;j=j+8) {//j代表的是字节为位置
					//ex.data[(int) bufferLine][j] 要跟下面这行的所有的字段比较
					int oneValueA = ex.data[(int) bufferLineR][j];
					int oneValueB = ex.data[(int) bufferLineR][j+4];
					//System.out.printf("R的值%d \n",oneValueA);
					for(int sj = 0;sj<56;sj=sj+8) {
						int anotherValueC = ex.data[(int) bufferLineS][sj];
						int anotherValueD = ex.data[(int) bufferLineS][sj+4];
						//System.out.printf("S的值%d \n",anotherValue);
						if(oneValueA==anotherValueC) {
							System.out.printf("%d相同 R块号%d 地址[%d,%d] S块号%d 地址[%d,%d]\n",oneValueA,i,j,j+4,si,sj,sj+4);
							breakCondition=1;
							//找到相同的就写进去，先写R再写S
							if(saveBlockCondition>55) {//这块buffer写满了
								//把它写进磁盘
								resultOrder++;
								String resultName=resultNameFront+String.valueOf(resultOrder)+".txt";
								ex.writeBlockToDisk((int)bufferLineSave,resultName);
								//再申请一块新的
								//System.out.printf("释放写的那行以后 剩余可用%d\n",ex.numFreeBlk);
								bufferLineSave = ex.getNewBlockInBuffer();//存放结果
								saveBlockCondition = 0;
								//System.out.printf("再申请存放缓冲的行  剩余可用%d\n",ex.numFreeBlk);
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
								saveBlockCondition +=4;//16 下一个开始是16了
							}
						}
					}					
				}
				//这一行遍历完了，释放掉
				ex.freeBlockInBuffer((int)bufferLineS);	
			}
			//释放R
			ex.freeBlockInBuffer((int)bufferLineR);	
		}
		System.out.printf("线性查找 IO次数：%d\n",ex.numIO-152);
	}
	
	static void HashJoin() throws IOException {
		//Step1:把数据块放进硬盘
		writeAllBufferToDisk(rTupleNumber,rTable,"src/disk/HashJoin/R/");
		writeAllBufferToDisk(sTupleNumber,sTable,"src/disk/HashJoin/S/");
		System.out.printf("Step1 总块数 %d 剩余可用%d\n",ex.numAllBlk,ex.numFreeBlk);
		//step2:R和S都的数据都分成两部分：奇数和偶数
		//遍历R数组，如果直接用内存排好奇偶的块放在目录里
		int rFileNameOrder[] = divideBucket(rTupleNumber,"R");
		int sFileNameOrder[] =divideBucket(sTupleNumber,"S");
		System.out.printf("Step2 总块数 %d 剩余可用%d\n",ex.numAllBlk,ex.numFreeBlk);
		//Step3:去目录里边，取出hash好的块，剩下的就是线性join
		
		//改一下线性查找
		//奇数比较
		String oddResultNameFront = "src/disk/HashJoin/Result/";//写到什么地方
		String evenResultNameFront = "src/disk/HashJoin/Result/";//写到什么地方
		LinecompareTowTable(oddResultNameFront,"src/disk/HashJoin/R/O","src/disk/HashJoin/S/O",rFileNameOrder[1],sFileNameOrder[1]);
		LinecompareTowTable(evenResultNameFront,"src/disk/HashJoin/R/E","src/disk/HashJoin/S/E",rFileNameOrder[0],sFileNameOrder[0]);
		System.out.printf("HashJoin IO次数：%d\n",ex.numIO);
		
	}
	
	//划分桶
	/**
	 *@param :TupleNumber 表的行数
	 *@param tableType: S or R
	 * @throws IOException 
	 */
	static int[] divideBucket(int TupleNumber,String tableType) throws IOException {
		String bucket;//桶的名字
		int Ocondition=0;
		int Econdition=0;
		int oodOrder=-1;
		int evenOrder=-1;
		int fileNameOrder[] = {0,0};
		String oddFrontName = "src/disk/HashJoin/"+tableType+"/O";
		String evenFrontName = "src/disk/HashJoin/"+tableType+"/E";
		String diskName = "src/disk/HashJoin/"+tableType+"/";
		for(int i=0;i<TupleNumber/7;i++) {//有多少块
			//读进一个块
			//System.out.printf("divideBucket 总块数 %d 剩余可用%d\n",ex.numAllBlk,ex.numFreeBlk);
			Object bufferLine = ex.getNewBlockInBuffer();
			String rName = diskName+String.valueOf(i)+".txt";
			ex.readBlockFromDisk((int)bufferLine,rName);
			for(int j = 0;j<56;j=j+8) {//对块里边的元组的 属性进行遍历
				bucket = judgeOddEven(ex.data[(int) bufferLine][j]);//判断的数据
				if(bucket.equals("O")) {//奇数，使用第No6个桶
					//把数据写进特定的栏
					if(Ocondition>55) {//Ocondition=56
						//把里边的数据写进磁盘
						oodOrder++;
						ex.data[6][64]=1;
						String resultName=oddFrontName+String.valueOf(oodOrder)+".txt";
						ex.writeBlockToDisk(6,resultName);
						ex.numFreeBlk--;
						Ocondition=0;
						//再申请一块新的
						//System.out.printf("释放写的那行以后 剩余可用%d\n",ex.numFreeBlk);
					}
					ex.data[6][Ocondition]=ex.data[6][ Ocondition+1]=
							ex.data[6][ Ocondition+2]=ex.data[6][ Ocondition+3]=
							ex.data[(int) bufferLine][j];
					Ocondition+=4;//4
					ex.data[6][Ocondition]=ex.data[6][ Ocondition+1]=
							ex.data[6][ Ocondition+2]=ex.data[6][ Ocondition+3]=
							ex.data[(int) bufferLine][j+4];
					Ocondition+=4;//4
				}else {//偶数使用第No7个桶
					//把数据写进特定的栏
					if(Econdition>55) {//Ocondition=56
						//把里边的数据写进磁盘
						evenOrder++;
						ex.data[7][64]=1;
						String resultName=evenFrontName+String.valueOf(evenOrder)+".txt";
						ex.writeBlockToDisk(7,resultName);
						ex.numFreeBlk--;
						Econdition=0;
						//System.out.printf("释放写的那行以后 剩余可用%d\n",ex.numFreeBlk);
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
			//把这个buffer释放
			//释放R
			ex.freeBlockInBuffer((int)bufferLine);	
		}
		//把6和7的数据再写进磁盘
		/*
		System.out.printf("-------------查看缓冲区------------\n");
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
	
	//判断元组的属性值是奇数还是偶数 odd even
	static String judgeOddEven(int val){
		if(val%2==1) {
			return "O";
		}else {
			return "E";
		}
	}
}
