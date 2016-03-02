package algorithm;

import java.util.List;
import java.util.Map;

import pojo.WebService;

public class StatInfo {
	/**
	 * 对服务列表进行常规统计
	 * */
	
	
	/**
	 * 返回服务列表中Qos的最值
	 * @param services:服务列表
	 * @param type:最值的类型,可选项:"max","min"
	 * */
	public static double extremeQos(List<WebService> services,String type){
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (WebService webService : services) {
			if(min > webService.getSelfResponseTime() && (webService.getSelfResponseTime()!=0.0)){
				//避免Qos等于0的情况。
				min = webService.getSelfResponseTime();
			}
			if(max < webService.getSelfResponseTime()){
				max = webService.getSelfResponseTime();
			}
		}
		
		if (type.equals("min")) {
			return min;
		}
		else{
			return max;
		}
		
	}
	
	/**
	 * 返回服务输入端或者输出端的平均数
	 * @param services:服务列表
	 * @param type:平均数的类型,可选项:"input","output"
	 * @param double
	 * */
	public static double instAverage(List<WebService> services,String type){
		double inputMean = 0.0;
		double outputMean = 0.0;
		
		int inSum = 0;
		int outSum = 0;
		for(WebService ws:services){
			inSum += ws.getInputs().size();
			outSum += ws.getOutputs().size();
		}
		
		inputMean = inSum * 1.0 / services.size();
		outputMean = outSum * 1.0 / services.size();
		
		if(type.equals("input")){
			return inputMean;
		}
		else{
			return outputMean;
		}
		
	}
	
	
}
