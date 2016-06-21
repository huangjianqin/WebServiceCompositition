package algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import pojo.WebService;

public class DataGetter {

	/**
	 * 获得所有服务的inst
	 * 
	 * @param services:服务列表
	 * @param type:inst的类型,可选项为:"input","output","all"
	 * @return List<String> inst列表,已去重
	 */
	public static List<String> getServicesAllInst(List<WebService> services, String type) {

		Set<String> inFilter = new HashSet<String>();
		Set<String> ouFilter = new HashSet<String>();

		List<String> inInsts = new ArrayList<String>();
		List<String> outInsts = new ArrayList<String>();

		for (WebService ws : services) {

			for (Object inst : ws.getInputs()) {
				String instStr = inst.toString().trim();
				if (instStr.length() == 0) {
					continue;
				}
				if (inFilter.add(instStr)) {
					inInsts.add(instStr);
				}
			}

			for (Object inst : ws.getOutputs()) {
				String instStr = inst.toString().trim();
				if (instStr.length() == 0) {
					continue;
				}

				if (ouFilter.add(instStr)) {
					outInsts.add(instStr);
				}
			}
		}

		if (type.equals("input")) {
			return inInsts;
		} else if (type.equals("output")) {
			return outInsts;
		} else {
			inFilter.addAll(ouFilter);
			List<String> all = new ArrayList<String>(inFilter);
			return all;
		}

	}

	/**
	 * 获取所有能与instance匹配的inst列表
	 */
	public static List<String> findInstances(String instance, Map<String, String> map1,
			Map<String, List<String>> map2) {
		List<String> instances = new ArrayList<String>();

		String con = map1.get(instance).toString();

		while (con != null) {
			if (map2.containsKey(con)) {
				instances.addAll((Collection) (map2.get(con)));
				con = (String) map1.get(con.substring(1));
			}
		}

		return instances;
	}

	/**
	 * 获取String - Webservice结构的副本,此时的输入count为输入端参数个数的值
	 */
	public static Map<String, WebService> copyStrToWsWithOld(Map<String, WebService> origin) {
		Map<String, WebService> result = new HashMap<String, WebService>();
		Set<String> key1 = origin.keySet();
		String ss;
		for (Iterator it = key1.iterator(); it.hasNext();) {
			ss = (String) it.next();
			WebService ws = origin.get(ss);

			WebService copy = new WebService();
			copy.setAllResponseTime(ws.getAllResponseTime());
			copy.setCount(ws.getInputs().size());
			copy.setCriticalParent(ws.getCriticalParent());
			copy.setInputs(ws.getInputs());
			copy.setName(ws.getName());
			copy.setNewAllResponseTime(ws.getNewAllResponseTime());
			copy.setOutputs(ws.getOutputs());
			copy.setSelfResponseTime(ws.getSelfResponseTime());
			copy.setSelfThroughput(ws.getSelfThroughput());
			result.put(ss, copy);
		}
		return result;
	}

	/**
	 * 获取String - Webservice结构的副本,此时的输入count为实际拓扑中的值
	 */
	public static Map<String, WebService> copyStrToWsWithNew(Map<String, WebService> origin) {
		Map<String, WebService> result = new HashMap<String, WebService>();
		Set<String> key1 = origin.keySet();
		String ss;
		for (Iterator it = key1.iterator(); it.hasNext();) {
			ss = (String) it.next();
			WebService ws = origin.get(ss);

			WebService copy = new WebService();
			copy.setAllResponseTime(ws.getAllResponseTime());
			copy.setCount(ws.getCount());
			copy.setCriticalParent(ws.getCriticalParent());
			copy.setInputs(ws.getInputs());
			copy.setName(ws.getName());
			copy.setNewAllResponseTime(ws.getNewAllResponseTime());
			copy.setOutputs(ws.getOutputs());
			copy.setSelfResponseTime(ws.getSelfResponseTime());
			copy.setSelfThroughput(ws.getSelfThroughput());
			result.put(ss, copy);
		}
		return result;
	}

	public static Map<String, List<WebService>> copyStrToWsList(Map<String, List<WebService>> origin)
			throws CloneNotSupportedException {
		Map<String, List<WebService>> result = new HashMap<String, List<WebService>>();

		for (String key : origin.keySet()) {

			for (WebService ws : origin.get(key)) {
				WebService newWs;
				newWs = (WebService) ws.clone();
				if (result.containsKey(key)) {
					result.get(key).add(newWs);
				} else {
					List<WebService> list = new ArrayList<WebService>();
					list.add(newWs);
					result.put(key, list);
				}
			}
		}
		return result;
	}

	/**
	 * HJQ新增,用于复制类似于solveMap的数据结构
	 * 
	 * @param origin
	 * @return
	 */
	public static Map<String, List<String>> copyStrToStrList(Map<String, List<String>> origin) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		for (String key : origin.keySet()) {

			List<String> newList = new ArrayList<String>();

			for (String inst : origin.get(key)) {
				newList.add(inst);
			}

			result.put(key, newList);
		}
		return result;
	}

	/**
	 * 将String - WebService结构转化成列表
	 */
	public List<WebService> tranStWsToList(Map<String, WebService> serviceMap) {
		List<WebService> result = new ArrayList<WebService>();

		for (String key : serviceMap.keySet()) {
			result.add(serviceMap.get(key));
		}

		return result;
	}

	/**
	 * 找出能够提供inst的qos最好的服务
	 * 
	 * @param successor
	 * @return
	 */
	public static WebService getCriticalParent(WebService successor, Map<String, WebService> RPT) {
		Set<WebService> parentSet = new HashSet<WebService>();
		WebService w = null;
		List<WebService> parentList = new ArrayList<WebService>();

		for (int i = 0; i < successor.getInputs().size(); i++) {
			w = RPT.get(successor.getInputs().get(i));

			if (parentSet.add(w)) {
				parentList.add(w);
			}
		}

		sortParents(parentList);

		return parentList.get(parentList.size() - 1);
	}

	/**
	 * 对优先队列进行排序
	 */
	public static void sort(List<WebService> PQ) {
		Collections.sort(PQ, new Comparator<WebService>() {

			public int compare(WebService arg0, WebService arg1) {
				if (arg0 == null || arg1 == null)
					System.out.println("PQ排序中出现空指针");
				if (arg0 == arg1)
					System.out.println("PQ排序中出现相同指针");
				double min1 = (arg0.getAllResponseTime() > arg0.getNewAllResponseTime()) ? arg0.getNewAllResponseTime()
						: arg0.getAllResponseTime();
				double min2 = (arg1.getAllResponseTime() > arg1.getNewAllResponseTime()) ? arg1.getNewAllResponseTime()
						: arg1.getAllResponseTime();
				if (min1 > min2)
					return 1;
				else if (min1 < min2)
					return -1;
				else if (arg0.getName().equals("Request"))
					return 1;
				else if (arg1.getName().equals("Request"))
					return 1;
				else
					return 0;
			}

		});
	}

	// HJQ_ List<WebService> parentList 根据allqos排序
	private static void sortParents(List<WebService> parentList) {
		Collections.sort(parentList, new Comparator<WebService>() {

			public int compare(WebService arg0, WebService arg1) {
				double qos1 = arg0.getNewAllResponseTime() > 0 ? arg0.getNewAllResponseTime()
						: arg0.getAllResponseTime();
				double qos2 = arg1.getNewAllResponseTime() > 0 ? arg1.getNewAllResponseTime()
						: arg1.getAllResponseTime();
				if (qos1 > qos2)
					return 1;
				else
					return -1;
			}

		});
	}

	public static List<WebService> copyWsList(List<WebService> list) throws CloneNotSupportedException {
		List<WebService> result = new ArrayList<WebService>();

		for (WebService webService : list) {
			WebService copy = (WebService) webService.clone();
			result.add(copy);
		}

		return result;
	}

	public static Map<String, List<WebService>> readChanged(Map<String, WebService> serviceMap, int fileNum,
			int runTimes) throws Exception {
		File file = new File("result2/changedServices" + fileNum + ".csv");
		Scanner scanner = new Scanner(file);
		Map<String, List<WebService>> wsMap = new HashMap<String, List<WebService>>();
		int times = 0;
		
		wsMap.put("new", new ArrayList<WebService>());
		wsMap.put("qosChange", new ArrayList<WebService>());
		wsMap.put("delete", new ArrayList<WebService>());
		
		while (scanner.hasNext()) {
			
			String group = scanner.nextLine();

			// 移动到某一组数据的开始位置
			if (group.startsWith("第")) {
				if(times == runTimes){
					break;
				}
				
				times ++;
			}
			
		}

		String s = scanner.nextLine();
		while (!"".equals(s)) {
			String[] ss = s.split(",", 2);
			if ("new".equals(ss[0])) {
				String name = ss[1].split(",", 2)[0];
				String body = ss[1].split(",", 2)[1];
				String qos = body.split(",", 3)[0];
				String resp = body.split(",", 3)[1];
				String inout = body.split(",", 3)[2];

				String[] in = inout.split(",", 2)[0].split(":");
				String[] out = { "" };
				if (inout.split(",", 2).length == 2) {
					out = inout.split(",", 2)[1].split(":");
				}
				List<String> input = Arrays.asList(in);
				List<String> output = Arrays.asList(out);

				WebService ws = new WebService();
				ws.setCount(input.size());
				ws.setInputs(input);
				ws.setName(name);
				ws.setOutputs(output);
				ws.setSelfResponseTime(Double.parseDouble(qos));
				ws.setAllResponseTime(Double.parseDouble(resp));

				wsMap.get("new").add(ws);
			} else if ("qosChange".equals(ss[0])) {
				String name = ss[1].split(",", 2)[0];
				String qos = ss[1].split(",", 2)[1];
				WebService ws = serviceMap.get(name);
				ws.setNewAllResponseTime(ws.getAllResponseTime() - ws.getSelfResponseTime() + Double.parseDouble(qos));
				ws.setSelfResponseTime(Double.parseDouble(qos));
				wsMap.get("qosChange").add(ws);
			} else if ("delete".equals(ss[0])) {
				String name = ss[1];
				WebService ws = serviceMap.get(name);
				wsMap.get("delete").add(ws);
			}
			s = scanner.nextLine();
		}

		scanner.close();

		return wsMap;
	}

	public static Map<String , Double> readQOSChanged(){
		Map<String , Double> qosChanged = new HashMap<String, Double>();
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("qosChangedServices.csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(scanner.hasNextLine()){
			String[] contents = scanner.nextLine().split(",");
			
			String WSName = contents[0];
			double newQOS = Double.valueOf(contents[1]);
			
			qosChanged.put(WSName, newQOS);
		}
		
		return qosChanged;
	}
}
