package statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class ResultStatistic {
	private static int requeryNum = 0;
	private static int continuequeryNum = 0;
	private static int strategy1Num = 0;
	private static int strategy2Num = 0;
	
	private static int avgRequeryNum = 0;
	private static int avgContinuequeryNum = 0;
	private static int avgStrategy1Num = 0;
	private static int avgStrategy2Num = 0;
	
	public static void statisticOne(Scanner scanner){
		//除去第一行
		scanner.nextLine();
		
		for(int i = 0; i < 10; i++){
			String[] splits = scanner.nextLine().split(",");
			
			String requery = splits[8];
			String continuequery = splits[9];
			String strategy1 = splits[6];
			String strategy2 = splits[7];
			
			String result = splits[10];
			
			if(result.equals("胜")){
				strategy1Num ++;
				
				if(strategy1.equals(requery)){
					requeryNum ++;
				}
				else{
					continuequeryNum ++;
				}
				
			}
			else if(result.equals("负")){
				strategy2Num ++;
				
				if(strategy2.equals(requery)){
					requeryNum ++;
				}
				else{
					continuequeryNum ++;
				}
			}
			else if(result.equals("平")){
				
				if(Double.valueOf(continuequery) > Double.valueOf(requery)){
					requeryNum ++;
					if(strategy1.equals(requery)){
						strategy1Num ++;
						strategy2Num ++;
					}
				}
				else if(Double.valueOf(continuequery) < Double.valueOf(requery)){
					continuequeryNum ++;
					if(strategy1.equals(continuequery)){
						strategy1Num ++;
						strategy2Num ++;
					}
				}
				
			}
			
		}
		
		
	}
	
	public static void statisticOneWithND(Scanner scanner){
		//除去第一行
		scanner.nextLine();
		
		for(int i = 0; i < 10; i++){
			String[] splits = scanner.nextLine().split(",");
			
			String requery = splits[5];
			String continuequery = splits[6];
			String strategy1 = splits[3];
			String strategy2 = splits[4];
			
			String result = splits[7];
			
			if(result.equals("胜")){
				strategy1Num ++;
				
				if(strategy1.equals(requery)){
					requeryNum ++;
				}
				else{
					continuequeryNum ++;
				}
				
			}
			else if(result.equals("负")){
				strategy2Num ++;
				
				if(strategy2.equals(requery)){
					requeryNum ++;
				}
				else{
					continuequeryNum ++;
				}
			}
			else if(result.equals("平")){
				
				if(Double.valueOf(continuequery) > Double.valueOf(requery)){
					requeryNum ++;
					if(strategy1.equals(requery)){
						strategy1Num ++;
						strategy2Num ++;
					}
				}
				else if(Double.valueOf(continuequery) < Double.valueOf(requery)){
					continuequeryNum ++;
					if(strategy1.equals(continuequery)){
						strategy1Num ++;
						strategy2Num ++;
					}
				}
				
			}
			
		}
		
		
	}
	
	public static void statisticAll() throws FileNotFoundException{
		Scanner scanner = new Scanner(new File("result5/allresult.csv"));
		
		scanner.nextLine();
		
		for(int i = 0; i < 100; i++){
			String[] splits = scanner.nextLine().split(",");
			
			double avgRequery = Double.valueOf(splits[2]);
			double avgContinuequery = Double.valueOf(splits[3]);
			double avgStrategy1 = Double.valueOf(splits[4]);
			double avgStrategy2 = Double.valueOf(splits[5]);
			
			double[] nums = new double[4];
			
			nums[0] = avgRequery;
			nums[1] = avgContinuequery;
			nums[2] = avgStrategy1;
			nums[3] = avgStrategy2;
			
			Arrays.sort(nums);
			double min = nums[0];
			
			if(min == avgRequery){
				avgRequeryNum ++;
			}
			if(min == avgContinuequery){
				avgContinuequeryNum ++;
			}
			if(min == avgStrategy1){
				avgStrategy1Num ++;
			}
			if(min == avgStrategy2){
				avgStrategy2Num ++;
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		String root = "result5/";
		
		int start = 1;
		int end = 100;
		
		for(int i = start; i <= end; i++){
			System.out.println("---------------------" + i + "---------------------");
			File file = new File(root + i + ".csv");
			Scanner scanner = new Scanner(file);
			statisticOneWithND(scanner);
		}
		
		System.out.println(requeryNum + "," + continuequeryNum + "," +
							strategy1Num + "," + strategy2Num);
		
		statisticAll();
		
		System.out.println(avgRequeryNum + "," + avgContinuequeryNum + ","
							+ avgStrategy1Num + "," + avgStrategy2Num);
	}
}
