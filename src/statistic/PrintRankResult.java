package statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PrintRankResult {
	private static final PrintRankResult printer = new PrintRankResult();
	private RandomAccessFile randomAccessFile;
	
	public PrintRankResult() {
		File file = new File("statistic/rank.csv");
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static PrintRankResult getInstance(){
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
