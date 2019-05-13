package database_4;

public class mainP {
	public static void main(String[] args) {
		extmem t = new extmem();
		int bufSize = 520;//buffer缓冲区的大小
		int blkSize = 64;//块的大小
		t.initBuffer(bufSize, blkSize);//初始化缓冲区
		//TODO 生成R和S的数据
		//TODO
	}
	
	public void createTableData() {
		/*
		 *创建R和S表的初始数据。
		 *然后写进txt里边，下次直接从txt里边读出来
		 **/
	}
}
