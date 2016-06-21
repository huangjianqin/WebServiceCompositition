package statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import sun.security.jca.GetInstance;

public class PrintStatisticResult {
	private static final PrintStatisticResult printer = new PrintStatisticResult();
	private RandomAccessFile randomAccessFile;
	
	public PrintStatisticResult() {
		File file = new File("statistic/result.csv");
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		print("ƽ������,ȫ��ƽ�����(��ƥ��),ȫ��ƽ�����(����ƥ��),"
				+ "ʵ��ƽ�����(��ƥ��),ʵ��ƽ�����(����ƥ��),ȫ�ֱ仯��,ʵ�ʱ仯��,PQ����" + System.lineSeparator());
	}
	
	public static PrintStatisticResult getInstance(){
		return printer;
	}
	
	public void print(String content){
		try {
			randomAccessFile.seek(randomAccessFile.length());
			
			randomAccessFile.write(content.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
