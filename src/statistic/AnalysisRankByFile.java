package statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pojo.WS2NextWSNumBean;
import pojo.WebService;

public class AnalysisRankByFile {
	private Map<String, Integer> rankMap = new HashMap<String, Integer>();
	private Map<String, Integer> oneLevelChildNumMap = new HashMap<String, Integer>();
	
	
	public AnalysisRankByFile() throws FileNotFoundException {
		init();
	}
	
	/**
	 * 读取记录排名的文件,并缓存好相应的数据
	 * @throws FileNotFoundException 
	 */
	private void init() throws FileNotFoundException{
		File file = new File("statistic/reachableRank.csv");
		Scanner scanner = new Scanner(file);
		
		scanner.nextLine();
		while(scanner.hasNextLine()){
			String[] infos = scanner.nextLine().split(",");
			
			String rank = infos[0];
			String WSName = infos[1];
			String oneLevelChildNum = infos[2];
			
			rankMap.put(WSName, Integer.valueOf(rank));
			oneLevelChildNumMap.put(WSName, Integer.valueOf(oneLevelChildNum));
			
		}
	}
	
	public void analysis() throws Exception{
		int num = 0;
		int begin = 1;
		int end = 100;
		
		StringBuilder fileContent = new StringBuilder();
		
		
		fileContent.append("变化服务数,实际变化服务数,重查,连续查询,PQ队列长度,一层后继数累加,"
				+ "变化中排名第一,排名第二,排名第三" + System.lineSeparator());
		
		for(int i = begin; i <= end; i++){
			Map<String , String[]> group2RsultInfo = readResult(i);
			Map<String, List<String>> group2DynamicWSInfo = readChanged(i);
			
			for(int j = 1; j <= 10; j++){
				//序号++
				num ++;
				
				String[] resultInfo = group2RsultInfo.get("" + j);
				List<String> dynamicWSInfo = group2DynamicWSInfo.get("" + j);
				
				List<WS2NextWSNumBean> ws2NextWSNumBeans = new ArrayList<WS2NextWSNumBean>();
				int oneLevelChildSum = 0;
				
				for(String WSName: dynamicWSInfo){
					
					WS2NextWSNumBean bean = new WS2NextWSNumBean();
					
					bean.setWsName(WSName);
					bean.setNextWSNum(oneLevelChildNumMap.get(WSName));
					
					if(oneLevelChildNumMap.get(WSName) != null){
						oneLevelChildSum += oneLevelChildNumMap.get(WSName);
					}
					
					ws2NextWSNumBeans.add(bean);
				}
				
				Collections.sort(ws2NextWSNumBeans);
				Collections.reverse(ws2NextWSNumBeans);
				
				//生成文件内容
				fileContent.append(resultInfo[0] + ",");
				fileContent.append(resultInfo[1] + ",");
				fileContent.append(resultInfo[2] + ",");
				fileContent.append(resultInfo[3] + ",");
				fileContent.append(resultInfo[4] + ",");
				fileContent.append(oneLevelChildSum + ",");
				
				fileContent.append(
						ws2NextWSNumBeans.get(0).getWsName() + 
						"(" + rankMap.get(ws2NextWSNumBeans.get(0).getWsName()) + ";" + 
						oneLevelChildNumMap.get(ws2NextWSNumBeans.get(0).getWsName())
						+ "),");
				
				if(ws2NextWSNumBeans.size() < 2){
					fileContent.append(System.lineSeparator());
					continue;
				}
				
				fileContent.append(
						ws2NextWSNumBeans.get(1).getWsName() + 
						"(" + rankMap.get(ws2NextWSNumBeans.get(1).getWsName()) + ";" + 
						oneLevelChildNumMap.get(ws2NextWSNumBeans.get(1).getWsName())
						+ "),");		
				
				if(ws2NextWSNumBeans.size() < 3){
					fileContent.append(System.lineSeparator());
					continue;
				}
				
				fileContent.append(
						ws2NextWSNumBeans.get(2).getWsName() + 
						"(" + rankMap.get(ws2NextWSNumBeans.get(2).getWsName()) + ";" + 
						oneLevelChildNumMap.get(ws2NextWSNumBeans.get(2).getWsName())
						+ ")" + System.lineSeparator());
			}
		}
		
		File file = new File("statistic/rank.csv");
		PrintWriter pw = new PrintWriter(file);
		pw.println(fileContent.toString());
		pw.close();		
	}
	
	private Map<String, String[]> readResult(int fileNum) throws FileNotFoundException{
		File file = new File("result2/" + fileNum + ".csv");
		Scanner scanner = new Scanner(file);
		
		Map<String , String[]> group2RsultInfo = new HashMap<String, String[]>();
		
		//移动到数据那行
		scanner.nextLine();
		
		for(int i = 1; i <= 10; i++){
			String[] results = scanner.nextLine().split(",");
			
			String dynamicWSNum = results[0];
			String actualDynamicWSNum = results[1];
			String PQLength = results[5];
			String requeryTime = results[8];
			String continueQueryTime = results[9];
			
			String[] resultInfo = new String[5];
			resultInfo[0] = dynamicWSNum;
			resultInfo[1] = actualDynamicWSNum;
			resultInfo[2] = requeryTime;
			resultInfo[3] = continueQueryTime;
			resultInfo[4] = PQLength;
			
			group2RsultInfo.put(String.valueOf(i), resultInfo);
		}
		
		
		return group2RsultInfo;
	}
	
	/**
	 * 读取储存实验变化服务
	 * @param fileNum
	 * @return
	 * @throws Exception
	 */
	private Map<String, List<String>> readChanged(int fileNum) throws Exception {
		File file = new File("result2/changedServices" + fileNum + ".csv");
		Scanner scanner = new Scanner(file);
		Map<String, List<String>> group2DynamicWSInfo = new HashMap<String, List<String>>();
		
		int groupNum = 0;
		
		while (scanner.hasNext()) {
			
			String group = scanner.nextLine();

			// 移动到某一组数据的开始位置
			if (group.startsWith("第")) {
				groupNum ++;
				
				List<String > dynamicWSInfo = new ArrayList<String>();
				group2DynamicWSInfo.put(String.valueOf(groupNum), dynamicWSInfo);
				
				String s = scanner.nextLine();
				while (!"".equals(s)) {
					String[] ss = s.split(",");
					
					String WSName = ss[1];
					
					dynamicWSInfo.add(WSName);
					
					s = scanner.nextLine();
				}

			}
			
		}
		
		scanner.close();

		return group2DynamicWSInfo;
	}
	
	public static void main(String[] args) throws Exception{
		AnalysisRankByFile analysisRankByFile = new AnalysisRankByFile();
		analysisRankByFile.analysis();
	}
	
}
