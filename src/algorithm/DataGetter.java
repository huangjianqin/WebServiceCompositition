package algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pojo.WebService;

public class DataGetter {
	
	/**
	 * ������з����inst
	 * @param services:�����б�
	 * @param type:inst������,��ѡ��Ϊ:"input","output","all"
	 * @return List<String> inst�б�,��ȥ��
	 * */
	public static List<String> getServicesAllInst(List<WebService> services,String type) {
		
		Set<String> inFilter = new HashSet<String>();
		Set<String> ouFilter = new HashSet<String>();
		
		List<String> inInsts = new ArrayList<String>();
		List<String> outInsts = new ArrayList<String>();
		
		for(WebService ws : services){
			
			for(Object inst : ws.getInputs()){
				String instStr = inst.toString().trim();
				if(instStr.length()==0){
					continue;
				}
				if(inFilter.add(instStr)){
					inInsts.add(instStr);
				}
			}
			
			for(Object inst : ws.getOutputs()){
				String instStr = inst.toString().trim();
				if(instStr.length()==0){
					continue;
				}
				
				if(ouFilter.add(instStr)){
					outInsts.add(instStr);
				}
			}
		}
		
		if(type.equals("input")){
			return inInsts;
		}
		else if(type.equals("output")){
			return outInsts;
		}
		else{
			inFilter.addAll(ouFilter);
			List<String> all = new ArrayList<String>(inFilter);
			return all;
		}
		
		
	}
	
	/**
	 * ��ȡ��������instanceƥ���inst�б�
	 * */
	public static List<String> findInstances(String instance,Map<String,String> map1,Map<String,List<String>> map2){
		List<String> instances = new ArrayList<String>();
		
		String con = map1.get(instance).toString();
		
		while(con!=null){
			if(map2.containsKey(con)){
				instances.addAll((Collection)(map2.get(con)));
				con = (String)map1.get(con.substring(1));
			}
		}
		
		return instances;
	}
	
	/**
	 * ��ȡString - Webservice�ṹ�ĸ���,��ʱ������countΪ����˲���������ֵ
	 * */
	public static Map<String,WebService> copyStrToWsWithOld(Map<String,WebService> origin){
		Map<String , WebService> result = new HashMap<String, WebService>();
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
	 * ��ȡString - Webservice�ṹ�ĸ���,��ʱ������countΪʵ�������е�ֵ
	 * */
	public static Map<String,WebService> copyStrToWsWithNew(Map<String,WebService> origin){
		Map<String , WebService> result = new HashMap<String, WebService>();
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
	
	public static Map<String , List<WebService>> copyStrToWsList(Map<String , List<WebService>> origin) throws CloneNotSupportedException{
		Map<String , List<WebService>> result = new HashMap<String, List<WebService>>();
		
		for(String key : origin.keySet()){
			
			for(WebService ws : origin.get(key)){
				WebService newWs;
					newWs = (WebService)ws.clone();
					if(result.containsKey(key)){
						result.get(key).add(newWs);
					}
					else{
						List<WebService> list = new ArrayList<WebService>();
						list.add(newWs);
						result.put(key , list);
					}
			}
		}
		return result;
	}
	/**
	 * HJQ����,���ڸ���������solveMap�����ݽṹ
	 * @param origin
	 * @return
	 */
	public static Map<String , List<String>> copyStrToStrList(Map<String , List<String>> origin){
		Map<String , List<String>> result = new HashMap<String, List<String>>();
		
		for(String key : origin.keySet()){
			
			List<String> newList = new ArrayList<String>();
			
			for(String inst : origin.get(key)){
				newList.add(inst);
			}
			
			result.put(key, newList);
		}
		return result;
	}
	
	/**
	 * ��String - WebService�ṹת�����б�
	 * */
	public List<WebService> tranStWsToList(Map<String , WebService> serviceMap){
		List<WebService> result = new ArrayList<WebService>();
		
		for(String key : serviceMap.keySet()){
			result.add(serviceMap.get(key));
		}
		
		return result;
	}
	
	/**
	 * �ҳ��ܹ��ṩinst��qos��õķ���
	 * @param successor
	 * @return
	 */
	public static WebService getCriticalParent(WebService successor,Map<String , WebService> RPT){
		Set<WebService> parentSet = new HashSet<WebService>();
		WebService w = null;
		List<WebService> parentList  = new ArrayList<WebService>();
		
		for(int i=0;i<successor.getInputs().size();i++){
			 w = RPT.get(successor.getInputs().get(i));	
			 
			 if(parentSet.add(w)){
				 parentList.add(w);
			 }
		}
		
		sortParents(parentList);
		
		return parentList.get(parentList.size()-1);
	}
	
	/**
	 *�����ȶ��н������� 
	 * */
	public static void sort(List<WebService> PQ){
		Collections.sort(PQ,new Comparator<WebService>(){

			public int compare(WebService arg0, WebService arg1) {
				if(arg0==null || arg1==null ) System.out.println("PQ�����г��ֿ�ָ��");
				if(arg0==arg1)	System.out.println("PQ�����г�����ָͬ��");
				double min1 = (arg0.getAllResponseTime()>arg0.getNewAllResponseTime())?arg0.getNewAllResponseTime():arg0.getAllResponseTime();
				double min2 = (arg1.getAllResponseTime()>arg1.getNewAllResponseTime())?arg1.getNewAllResponseTime():arg1.getAllResponseTime();
				if(min1>min2) 
					return 1;
				else 
					if(min1<min2)
						return -1;
					else 
						if(arg0.getName().equals("Request"))return 1;
						else if(arg1.getName().equals("Request")) return 1;	
						else return 0;
			}
			
		});
	}
	
	//HJQ_  List<WebService> parentList  ����allqos����
	private static void sortParents(List<WebService> parentList){
		Collections.sort(parentList,new Comparator<WebService>(){

			public int compare(WebService arg0, WebService arg1) {
				double qos1 = arg0.getNewAllResponseTime()>0?arg0.getNewAllResponseTime():arg0.getAllResponseTime();
				double qos2 = arg1.getNewAllResponseTime()>0?arg1.getNewAllResponseTime():arg1.getAllResponseTime();
				if(qos1>qos2) return 1;
				else return -1;
			}
			
		});
	}
	
	public static List<WebService> copyWsList(List<WebService> list) throws CloneNotSupportedException{
		List<WebService> result = new ArrayList<WebService>();
		
		for (WebService webService : list) {
			WebService copy = (WebService)webService.clone();
			result.add(copy);
		}
		
		return result;
	}
	
}
