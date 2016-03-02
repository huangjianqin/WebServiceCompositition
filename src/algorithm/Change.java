package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import pojo.WebService;

public class Change {

	/**
	 * ���캯��
	 * 
	 * @param testServiceList:��Ҫ�ı�ķ�����б�
	 * @param qosChangeBound:qos�仯������:��qosChangeBound
	 */
	public Change(List<WebService> testServiceList, List<String> insts, int inputMean, int outputMean,
			int qosChangeBound) {
		super();
		this.testServiceList = testServiceList;
		this.insts = insts;
		this.inputMean = inputMean;
		this.outputMean = outputMean;
		this.qosChangeBound = qosChangeBound;
		this.maxQos = StatInfo.extremeQos(testServiceList, "max");
	}

	/**
	 * @param changeNum:��Ҫ�ı������
	 * @param isGlobal:�ı�ķ����Ƿ��Ǵ�ȫ��ѡȡ��
	 * @param newLimit,changeLimit,deleteLimit:�ֱ��������������Ŀ,�ı�Qos�������Ŀ,ɾ���������Ŀ
	 * @return changedServices:�ı�ķ����б�
	 */
	public Map<String, List<WebService>> changeWebServices(int changeNum, int newLimit, int changeLimit,
			int deleteLimit) {
		// WebService w = null;

		// �ı�ķ��񼯺�,����������,�ֱ��ǣ�new,delete,qoschange
		Map<String, List<WebService>> changedServices = new HashMap<String, List<WebService>>();
		changedServices.put("qosChange", new ArrayList<WebService>());
		changedServices.put("new", new ArrayList<WebService>());
		changedServices.put("delete", new ArrayList<WebService>());

		int hasNew = 0;
		int qosChange = 0;
		int hasDelete = 0;

		Set<WebService> filterSet = new HashSet<WebService>(); // ���˸ı�ķ���,�����ظ�

		Collections.shuffle(testServiceList);
		while ((hasNew + qosChange + hasDelete) < changeNum) {
			//System.out.println("sum is " + (hasNew + qosChange + hasDelete));
			int index = testServiceList.size() - 1;

			WebService changeWs = testServiceList.get(index);
			
			if (changeWs.getName().equals("Request") || changeWs.getName().equals("Provide")) {
				testServiceList.remove(index);
				continue; // ���ѡ��ķ������յ�������,��������
			}
			
			testServiceList.remove(index);

			if (qosChange < changeLimit) {
				// 0:Qos�ı�
				changeQos(changeWs); // ��changeWs�ķ�����иı�
				changedServices.get("qosChange").add(changeWs);
				qosChange += 1;
			} else if (hasNew < newLimit) {
				// 1:���ӷ���
				Random random = new Random();
				int inputNum = random.nextInt(this.inputMean) + 1;
				int outputNum = random.nextInt(this.outputMean) + 1;

				WebService newWs = genNewWebService(this.insts, inputNum, outputNum);
				changedServices.get("new").add(newWs);
				hasNew += 1;
			} else if (hasDelete < deleteLimit) {
				// 2:ɾ������
				changedServices.get("delete").add(changeWs);
				hasDelete += 1;
			} else {
				// do nothing
				continue;
			}

		}

		return changedServices;
	}

	/**
	 * ����:�ı�һ�������Qos
	 * 
	 * @param ws:��Ҫ�ı�Qos�ķ���
	 */
	private void changeQos(WebService ws) {

		Random signalRandom = new Random();
		boolean isBetter = signalRandom.nextBoolean();
		int signal = isBetter ? -1 : 1; // true:Qos��С false:Qos���

		Random gapRandom = new Random();
		int qosGap = (int) (gapRandom.nextDouble() * this.qosChangeBound) * signal;

		double oldSelfQos = ws.getSelfResponseTime();
		double newSelfQos = -1;

		if (isBetter) {
			// ���Qos���,��Qos��С
			if ((oldSelfQos + qosGap) <= 0) {
				qosGap = (int) (oldSelfQos * gapRandom.nextDouble() * signal);
			}
		} else {
			// do nothing:���ô���Qos���ִ���0�����
		}

		newSelfQos = oldSelfQos + qosGap; // ws������µ�Qos

		if (ws.getAllResponseTime() + qosGap <= 0) {
			ws.setNewAllResponseTime(ws.getAllResponseTime());
		} else {
			//System.out.println(ws.getName() + " ��qosGap�� " + qosGap);
			ws.setNewAllResponseTime(ws.getAllResponseTime() + qosGap);
		}
		ws.setSelfResponseTime(newSelfQos);
	}

	/**
	 * ����:����һ���µķ���
	 * 
	 * @param insts:����������ɷ����inst�б�
	 * @param inputNum:����˲���������
	 * @param outputNum:����˲���������
	 * @return WebService:�����ɵķ���
	 */
	private WebService genNewWebService(List<String> insts, int inputNum, int outputNum) {

		Set<String> filterSet = new HashSet<String>();

		WebService newWs = new WebService();
		newWs.setAllResponseTime(Double.MAX_VALUE);
		newWs.setNewAllResponseTime(Double.MAX_VALUE);
		newWs.setName("newWebService " + this.newWsId);
		this.newWsId += 1;

		int hasInput = 0;
		List<String> inputs = new ArrayList<String>();
		while (hasInput < inputNum) {
			Random inputRandom = new Random();
			String input = insts.get(inputRandom.nextInt(insts.size()));
			if (filterSet.add(input)) {
				inputs.add(input);
				hasInput += 1;
			}
		}
		newWs.setInputs(inputs); // ��������ɵ������б���newWs

		int hasOutput = 0;
		List<String> outputs = new ArrayList<String>();
		while (hasOutput < outputNum) {
			Random outputRandom = new Random();
			String output = insts.get(outputRandom.nextInt(insts.size()));
			if (filterSet.add(output)) {
				outputs.add(output);
				hasOutput += 1;
			}
		}
		newWs.setOutputs(outputs); // ��������ɵ�����б���newWs

		Random qosRandom = new Random();
		double newQos = qosRandom.nextDouble() * this.maxQos;
		newWs.setSelfResponseTime(newQos); // �����µ�Qos
		newWs.setCount(inputNum);

		return newWs;
	}

	// ������
	public static void main(String[] args) {

	}

	private List<WebService> testServiceList;
	private List<String> insts; // ����insts���б�
	private int inputMean; // ����ڵ�input��ƽ����
	private int outputMean; // ����ڵ����inst��ƽ����
	private int qosChangeBound; // Qos�ı䣨���ӻ���٣�������
	private int newWsId = 1; // ���ɵ��·��������,ÿ����һ����1
	private double maxQos; // ���з���������Qos

}
