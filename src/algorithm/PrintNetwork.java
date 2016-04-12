package algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pojo.WebService;
import sun.dc.pr.PRError;

public class PrintNetwork {
	private static String NETWORK_PATH = "network/network.csv";
	private static String OUTPUTS_NUM_PATH = "network/outputsNum.csv";
	private static String PQ_PATH = "network/PQ.csv";
	private static String OPTIMAL_PATH_BEFORE_PATH = "network/OptimalPath_before.csv";
	private static String OPTIMAL_PATH_LAST_PATH = "network/OptimalPath_last.csv";
	private static String PQ_WITHOUT_FIRST_CHANGED_PATH = "network/PQWithoutFirstChanged.csv";
	private static String FIRST_CHANGED_PATH = "network/FirstChanged.csv";
	
	public static void printNetwork(Map<String, WebService> RPT1, Map<String, List<WebService>> IIT) {
		StringBuilder networkInfo = new StringBuilder();
		
		for (String out : RPT1.keySet()) {
			WebService provider = RPT1.get(out);
			
			if (IIT.get(out) != null) {
				for (WebService ws : IIT.get(out)) {
					if (ws.getCount() == 0) {
						String info = provider.getName() + "," + out + "," + ws.getName() + System.lineSeparator();
						networkInfo.append(info);
					}
				}
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(NETWORK_PATH));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		pw.write(networkInfo.toString());

		pw.close();
	}
	
	public static void printOutputsNum(Map<String, WebService> RPT1, Map<String, List<WebService>> IIT) {
		Map<String , Set<String>> OutputsNum = new HashMap<String, Set<String>>();
		
		for (String out : RPT1.keySet()) {
			WebService provider = RPT1.get(out);
			
			if (IIT.get(out) != null) {
				
				if(OutputsNum.get(provider.getName()) == null){
					Set<String > temp = new HashSet<String>();
					OutputsNum.put(provider.getName(), temp);
				}
				
				for (WebService ws : IIT.get(out)) {
					if (ws.getCount() == 0) {
						OutputsNum.get(provider.getName()).add(ws.getName());
					}
				}
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(OUTPUTS_NUM_PATH));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		for(String WSName : OutputsNum.keySet()){
			pw.println(WSName + "," + OutputsNum.get(WSName).size());
		}

		pw.close();
	}

	
	public static void printPQ(List<WebService> PQ) {
		StringBuilder PQInfo = new StringBuilder();

		for (int i = 0; i < PQ.size() - 1; i++) {
			PQInfo.append(PQ.get(i).getName() + System.lineSeparator());
		}

		PQInfo.append(PQ.get(PQ.size() - 1).getName());

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(PQ_PATH));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		pw.write(PQInfo.toString());

		pw.close();
	}
	
	public static void printOptimalPathBefore(List<WebService> keyWebservices, Map<String, WebService> RPT1,
			Map<String, List<WebService>> IIT) {
		printOptimalPath(keyWebservices, RPT1, IIT, OPTIMAL_PATH_BEFORE_PATH);
	}
	
	public static void printOptimalPathLast(List<WebService> keyWebservices, Map<String, WebService> RPT1,
			Map<String, List<WebService>> IIT) {
		printOptimalPath(keyWebservices, RPT1, IIT, OPTIMAL_PATH_LAST_PATH);
	}
	
	public static void printOptimalPath(List<WebService> keyWebservices, Map<String, WebService> RPT1,
			Map<String, List<WebService>> IIT, String typePath) {
		StringBuilder networkInfo = new StringBuilder();

		for (String out : RPT1.keySet()) {
			WebService provider = RPT1.get(out);

			if (IIT.get(out) != null) {
				for (WebService ws : IIT.get(out)) {
					if (ws.getCount() == 0 && (keyWebservices.contains(ws) || ws.getName().equals("Request"))) {
						String info = provider.getName() + "," + out + "," + ws.getName() + System.lineSeparator();
						networkInfo.append(info);
					}
				}
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(typePath));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		pw.write(networkInfo.toString());

		pw.close();
	}

	public static void printFirstChanged(List<WebService> qosChangedWS){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(FIRST_CHANGED_PATH));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		for(WebService ws: qosChangedWS){
			pw.println(ws.getName());
		}

		pw.close();
		
	}
	
	public static void printFirstChanged(Map<String , List<WebService>> changedMap){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(FIRST_CHANGED_PATH));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		for(String key: changedMap.keySet()){
			for(WebService ws: changedMap.get(key)){
				pw.println(ws.getName());
			}
		}

		pw.close();
		
	}
	
	public static void printPQWithoutFirstChanged(List<WebService> PQ){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(PQ_WITHOUT_FIRST_CHANGED_PATH));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		for(WebService ws : PQ){
			pw.println(ws.getName());
		}

		pw.close();
	}
}
