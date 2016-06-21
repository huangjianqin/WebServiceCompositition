package statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PrintPQBCDiagram {
	private static final PrintPQBCDiagram printer = new PrintPQBCDiagram();
	private RandomAccessFile randomAccessFile;
	
	public PrintPQBCDiagram() {
		File file = new File("PQBCDiagram.csv");
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.print("PQ长度,重查,连续查询,出现次数" + System.lineSeparator());
	}
	
	public static PrintPQBCDiagram getInstance(){
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
