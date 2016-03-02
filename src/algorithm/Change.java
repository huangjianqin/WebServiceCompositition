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
	 * 构造函数
	 * 
	 * @param testServiceList:需要改变的服务的列表
	 * @param qosChangeBound:qos变化的区间:±qosChangeBound
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
	 * @param changeNum:需要改变的数量
	 * @param isGlobal:改变的服务是否是从全局选取。
	 * @param newLimit,changeLimit,deleteLimit:分别是新增服务的数目,改变Qos服务的数目,删除服务的数目
	 * @return changedServices:改变的服务列表
	 */
	public Map<String, List<WebService>> changeWebServices(int changeNum, int newLimit, int changeLimit,
			int deleteLimit) {
		// WebService w = null;

		// 改变的服务集合,有三种类型,分别是：new,delete,qoschange
		Map<String, List<WebService>> changedServices = new HashMap<String, List<WebService>>();
		changedServices.put("qosChange", new ArrayList<WebService>());
		changedServices.put("new", new ArrayList<WebService>());
		changedServices.put("delete", new ArrayList<WebService>());

		int hasNew = 0;
		int qosChange = 0;
		int hasDelete = 0;

		Set<WebService> filterSet = new HashSet<WebService>(); // 过滤改变的服务,避免重复

		Collections.shuffle(testServiceList);
		while ((hasNew + qosChange + hasDelete) < changeNum) {
			//System.out.println("sum is " + (hasNew + qosChange + hasDelete));
			int index = testServiceList.size() - 1;

			WebService changeWs = testServiceList.get(index);
			
			if (changeWs.getName().equals("Request") || changeWs.getName().equals("Provide")) {
				testServiceList.remove(index);
				continue; // 如果选择的服务是终点或者起点,则跳过。
			}
			
			testServiceList.remove(index);

			if (qosChange < changeLimit) {
				// 0:Qos改变
				changeQos(changeWs); // 对changeWs的服务进行改变
				changedServices.get("qosChange").add(changeWs);
				qosChange += 1;
			} else if (hasNew < newLimit) {
				// 1:增加服务
				Random random = new Random();
				int inputNum = random.nextInt(this.inputMean) + 1;
				int outputNum = random.nextInt(this.outputMean) + 1;

				WebService newWs = genNewWebService(this.insts, inputNum, outputNum);
				changedServices.get("new").add(newWs);
				hasNew += 1;
			} else if (hasDelete < deleteLimit) {
				// 2:删除服务
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
	 * 功能:改变一个服务的Qos
	 * 
	 * @param ws:需要改变Qos的服务
	 */
	private void changeQos(WebService ws) {

		Random signalRandom = new Random();
		boolean isBetter = signalRandom.nextBoolean();
		int signal = isBetter ? -1 : 1; // true:Qos变小 false:Qos变大

		Random gapRandom = new Random();
		int qosGap = (int) (gapRandom.nextDouble() * this.qosChangeBound) * signal;

		double oldSelfQos = ws.getSelfResponseTime();
		double newSelfQos = -1;

		if (isBetter) {
			// 如果Qos变好,即Qos变小
			if ((oldSelfQos + qosGap) <= 0) {
				qosGap = (int) (oldSelfQos * gapRandom.nextDouble() * signal);
			}
		} else {
			// do nothing:不用处理Qos出现大于0的情况
		}

		newSelfQos = oldSelfQos + qosGap; // ws自身的新的Qos

		if (ws.getAllResponseTime() + qosGap <= 0) {
			ws.setNewAllResponseTime(ws.getAllResponseTime());
		} else {
			//System.out.println(ws.getName() + " 的qosGap是 " + qosGap);
			ws.setNewAllResponseTime(ws.getAllResponseTime() + qosGap);
		}
		ws.setSelfResponseTime(newSelfQos);
	}

	/**
	 * 功能:生成一个新的服务
	 * 
	 * @param insts:用于随机生成服务的inst列表
	 * @param inputNum:输入端参数的数量
	 * @param outputNum:输出端参数的数量
	 * @return WebService:新生成的服务
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
		newWs.setInputs(inputs); // 将随机生成的输入列表赋给newWs

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
		newWs.setOutputs(outputs); // 将随机生成的输出列表赋给newWs

		Random qosRandom = new Random();
		double newQos = qosRandom.nextDouble() * this.maxQos;
		newWs.setSelfResponseTime(newQos); // 设置新的Qos
		newWs.setCount(inputNum);

		return newWs;
	}

	// 主函数
	public static void main(String[] args) {

	}

	private List<WebService> testServiceList;
	private List<String> insts; // 所有insts的列表
	private int inputMean; // 服务节点input的平均数
	private int outputMean; // 服务节点输出inst的平均数
	private int qosChangeBound; // Qos改变（增加或减少）的上限
	private int newWsId = 1; // 生成的新服务的名字,每生成一个加1
	private double maxQos; // 所有服务里的最大Qos

}
