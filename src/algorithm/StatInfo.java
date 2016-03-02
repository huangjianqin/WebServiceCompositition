package algorithm;

import java.util.List;
import java.util.Map;

import pojo.WebService;

public class StatInfo {
	/**
	 * �Է����б���г���ͳ��
	 * */
	
	
	/**
	 * ���ط����б���Qos����ֵ
	 * @param services:�����б�
	 * @param type:��ֵ������,��ѡ��:"max","min"
	 * */
	public static double extremeQos(List<WebService> services,String type){
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (WebService webService : services) {
			if(min > webService.getSelfResponseTime() && (webService.getSelfResponseTime()!=0.0)){
				//����Qos����0�������
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
	 * ���ط�������˻�������˵�ƽ����
	 * @param services:�����б�
	 * @param type:ƽ����������,��ѡ��:"input","output"
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
