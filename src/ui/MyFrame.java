package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;

import algorithm.Change;
import algorithm.DataGetter;
import algorithm.PrintNetwork;
import algorithm.StatInfo;
import javafx.scene.chart.PieChart.Data;
import pojo.ExperimentRankBean;
import pojo.WS2NextWSNumBean;
import pojo.WebService;
import statistic.PrintPQBCDiagram;
import statistic.PrintRankResult;
import statistic.PrintStatisticResult;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayDeque;
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
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class MyFrame {

	private JFrame frame;
	private ChooseDataSetListener  chooseDataSetListener;
				//HJQ_比赛数据
	private Map<String,String> map1;//inst/con->con
	private Map<String,List<String>> map2;//inst->insts
				//HJQ_  服务name->服务
	private Map<String,WebService> serviceMap;
	private Map<String,WebService> RPT;
				//可以输入有某个inst作为input的webservice列表
	private Map<String,List<WebService>> IIT;
	private List<String> inputsFromChallenge;
	private List<String> outputsFromChallenge;
	
	private Map<String,WebService> enabledServices;
				//HJQ_inst --> list<WebSerivce>  可输出inst的list<webService>
	private Map<String,List<WebService>> OT;
				//HJQ_inst --> list<inst> inst可以匹配的inst列表
	private Map<String,List<String>> solveMap;
	private double continousQos;
	private double requeryQos;
				//HJQ_本质上就是所有的服务
	private List<WebService> testServiceList; 
	private int count;
	private Map<String,WebService> RPT1;
				//HJQ_改变的服务的总数
	private int sumOfChangedService;
	private int successTime;
				//HJQ_记录后向结果
	private String recordBackWard;
	private String recordSomething;
	private int realSum;//实际可以用的ws总数
	private int sumOfCanUseWS;//最后可达的ws,即count=0
	
	//为了测试10组相同的数据而复制的数据结构
				//HJQ_serviceMap的副本
	private Map<String,WebService> serviceMap1;
				//HJQ_RPT1的副本
 	private Map<String,WebService> RPT2;
	
	private byte[] buff;
	private FileOutputStream out1;
				//HJQ_记录策略1所有实验的耗时
	private StringBuilder recordTime1;
				//HJQ_记录策略2所有实验的耗时
	private StringBuilder recordTime2;
				//HJQ_记录重查所有实验的耗时
	private StringBuilder recordTime3;
				//HJQ_记录连续查询所有实验的耗时
	private StringBuilder recordTime4;
				//HJQ_记录经格式化的实验D信息
	private StringBuilder recordTime5;
				//HJQ_对比连续查询和重查,1为连续查询更优,0为两个效果一样,-1为重查更优
	private StringBuilder recordCompare;
				//HJQ_记录真实变化的服务数量
	private StringBuilder recordRealSum;
	
	//统计所有重查时间的总和
	private double requeryTime;
	//统计所有连续查询时间的总和
	private double continueTime;
	//统计所有策略1时间的总和
	private double strategy1Time;
	//统计所有策略2时间的总和
	private double strategy2Time;
	//统计所有最佳策略时间的总和
	private double bestStrategyTime;
	
	//统计实验D胜率,平率---每组
	private long winCount = 0;
	private long equalCount = 0;
	
	//统计100次实验D的胜率,平率--胜率>负率为胜
	private long allCount;
	private long allWinCount = 0;
	private long allEqualCount = 0; 
	
	//统计PQ的最大值
	private int PQMaxSize = 0;
	
	//策略1阈值
	private int strategy1Value = 10;
	//策略2阈值
	private int strategy2Value = 500;

	//策略1阈值
	private int strategy1ValueWithAD = 11;
	//策略2阈值
	private int strategy2ValueWithAD = 700;
	
	//防止有环
	private boolean dead;
	//画图用
	private String typePath = "before";
	
	//统计某个服务到终点服务的距离数
	private Map<String, Integer> wsToEndWSLengthMap = new HashMap<String, Integer>();
	
	//统计服务下一层会波及的服务数
	private Map<String, Integer> rankMap = new HashMap<String, Integer>();
	private List<WS2NextWSNumBean> ws2NextWSNumBeans = new ArrayList<WS2NextWSNumBean>();
	private List<ExperimentRankBean> tempExperimentRank = new ArrayList<ExperimentRankBean>();
	private List<Map<String, List<WebService>>> tempChanged;
	private int fileNum = 1;
	private int runTimes = 0;
	
	
	public MyFrame() throws FileNotFoundException {
		initialize();
		 map1 = new HashMap();//map1:类-父类，实例-类
		 map2 = new HashMap();//map2:类-实例组
		 serviceMap = new HashMap<String,WebService>();
		 RPT = new HashMap<String,WebService>();//元素和哪个服务提供
		 IIT = new HashMap<String,List<WebService>>();//inst***(input)-List
	     inputsFromChallenge = new ArrayList<String>();
	     outputsFromChallenge = new ArrayList<String>();
	     enabledServices = new HashMap();
	     OT =  new HashMap<String,List<WebService>>();//当前能提供某个inst的服务list
	     solveMap = new HashMap<String,List<String>>();//inst-能解决的instList;
	     testServiceList = new ArrayList<WebService>();
	     count=500;
	     RPT1 = new HashMap<String,WebService>();
	     recordBackWard="";
	     recordSomething="";
	     RPT2 = new HashMap<String,WebService>();
	     serviceMap1 = new HashMap<String,WebService>();
	     FileOutputStream out1 = new FileOutputStream("text.txt");	   
	     recordTime1=new StringBuilder();
	     recordTime2=new StringBuilder();
	     recordTime3=new StringBuilder();
	     recordTime4=new StringBuilder();
	     recordTime5=new StringBuilder();
	     recordCompare=new StringBuilder();
	 	 recordRealSum=new StringBuilder();
	 	 /*
	 	 //testing
	 	 constructByCode();
	 	 this.getOptimalWebServiceComposition(inputsFromChallenge, outputsFromChallenge, IIT, map1, map2, RPT);
		 */
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("服务组合动态性研究");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		chooseDataSetListener = new ChooseDataSetListener();
		
		JButton btnNewButton = new JButton("选择测试集");
		btnNewButton.addActionListener(chooseDataSetListener);
		btnNewButton.setBounds(10, 22, 102, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnqos = new JButton("更改qos");
		btnqos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//String  str1 = JOptionPane.showInputDialog("测试多少组？");
				//if(str1!=null){
					String  str2 = "";
					//String  str2 = JOptionPane.showInputDialog("变更多少个服务的qos？");
					if(str2!=null){							  
					     recordTime1.setLength(0);
					     recordTime2.setLength(0);
					     recordCompare.setLength(0);
					 	 recordRealSum.setLength(0);
						
						count=5000;
						
						//Integer.parseInt(str1);
						
						//sumOfChangedService=Integer.parseInt(str2);
						try {
							testManyTimeForHJQAuto("B");
							//testManyTimeAuto();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("mmmmmm");
						}
					//}
				}
			}
		});
		btnqos.setBounds(10, 106, 93, 23);
		frame.getContentPane().add(btnqos);
		
		JButton button = new JButton("增加服务");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button.setBounds(10, 140, 93, 23);
		frame.getContentPane().add(button);
		
		JButton button_1 = new JButton("删除服务");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button_1.setBounds(10, 181, 93, 23);
		frame.getContentPane().add(button_1);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(139, 10, 285, 252);
		frame.getContentPane().add(textArea);
		
		JButton btnNewButton_1 = new JButton("服务组合策略");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String  str1 = JOptionPane.showInputDialog("测试多少组？");
				if(str1!=null){					
						count=Integer.parseInt(str1);						
						try {
//							testManyTime1ForWin();
							testManyTime1ForWinHJQ();
							
							
//							//记录10组实验的排名结果
//							PrintRankResult.getInstance().print("前面六个代表某次实验的前3的服务及波及点数,此次变化中排名在前20的数目,PQ长度");
//							Collections.sort(tempExperimentRank);
//							Collections.reverse(tempExperimentRank);
//							
//							for(int j = 0; j < tempExperimentRank.size(); j++){
//								ExperimentRankBean bean = tempExperimentRank.get(j);
//								
//								String content = "";
//								
//								for(WS2NextWSNumBean temp : bean.getWs2NextWSNumBeans()){
//									int index = ws2NextWSNumBeans.indexOf(temp);
//									
//									content += temp.getWsName() + "(" + (index + 1) + "), " +
//												temp.getNextWSNum() + ",";
//								}
//								
//								content += bean.getRank20Num() + ",";
//								content += bean.getPQ() + System.lineSeparator();
//								PrintRankResult.getInstance().print(content);
//							}
							
							
						} catch (IOException e) {
							System.out.println("186");
							e.printStackTrace();
						}
					}
				}
			
		});
		btnNewButton_1.setBounds(10, 217, 93, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				map1.clear();
				map2.clear();
				serviceMap.clear();
				IIT.clear();
				inputsFromChallenge.clear();
				outputsFromChallenge.clear();
				RPT.clear();
				RPT1.clear();
				OT.clear();
				solveMap.clear();
				
				
				
				constructMap1Map2(map1,map2);
			    constructWebServiceList(serviceMap);
			    getQos(serviceMap);
			    constructIIT(serviceMap,IIT);
			    getChanllenge(inputsFromChallenge,outputsFromChallenge);
			    
			    getOptimalWebServiceComposition(inputsFromChallenge,outputsFromChallenge,
			    		IIT,map1,map2,RPT);
			    System.out.println("可达的服务数:"+sumOfCanUseWS);
			    backward(outputsFromChallenge,RPT,serviceMap);
			    
			    //统计每个服务科波及服务数并排序
			    //statisticOneLevel(false);
			    statisticOneLevel(true);
			    
//			    //统计某个服务到终点的距离数
//			    statisticWSToEndWSLengthMap();
//			    statisticOutputs(null);
//			    PrintStatisticResult.getInstance().print(0 + ",");
//				PrintStatisticResult.getInstance().print(0 + ",");
//				PrintStatisticResult.getInstance().print(0 + System.lineSeparator());
//			    
//			    //画图用
//			    PrintNetwork.printNetwork(RPT1, IIT);
//			    PrintNetwork.printOutputsNum(RPT1, IIT);
//			    
//			    Map<String , Double> qosChanged = DataGetter.readQOSChanged();
//			    ArrayList<WebService> qosChangedList = new ArrayList<WebService>();
//			    
//			    for(String WSName: qosChanged.keySet()){
//			    	if(serviceMap.get(WSName) != null){
//			    		double all = serviceMap.get(WSName).getAllResponseTime();
//			    		double oldQOS = serviceMap.get(WSName).getSelfResponseTime();
//			    		serviceMap.get(WSName).setSelfResponseTime(qosChanged.get(WSName));
//			    		serviceMap.get(WSName).setNewAllResponseTime(all - oldQOS + qosChanged.get(WSName));
//			    		
//			    		//serv947554374			    		
//			    		qosChangedList.add(serviceMap.get(WSName));
//			    	}
//			    }
//			    
//			    //画图用,输出qos变化服务
//			    PrintNetwork.printFirstChanged(qosChangedList);
//			    
//			    continuousQuery4(qosChangedList);
//			    
//			    typePath="last";
//			    backward(outputsFromChallenge,RPT,serviceMap);
//			    
//			    System.out.println(serviceMap.get("serv612685309").getCount()+"ppppp");
//			    System.out.println(serviceMap.get("serv1928620686").getCount()+"ppppp");
			   //
			    
			  //  reQuery();
//			    randomTest();
			    //testManyTime();
			}
		});
		btnRun.setBounds(10, 55, 93, 23);
		frame.getContentPane().add(btnRun);
	}
	
	//HJQ_一次查询sim_dijkstra 有策略,根据阈值调用连续查询和重查 HJQ改
	public double strategy(int choice,int sum,int realSum,List<WebService> qosChangedList){
		
		if(choice==1){
			if(realSum<=this.strategy1Value){
				//long startMili=System.currentTimeMillis();
				
				continuousQuery4(qosChangedList);
				
				//long endMili=System.currentTimeMillis();
				//System.out.println("continuousQuery结束 :"+endMili);
				//System.out.println("myStratege总耗时为:"+(endMili-startMili)+"毫秒");	
				
				//System.out.println("连续查询qos:" + continousQos);
				return 0;
			}	
		}else{
			if(sum<=this.strategy2Value){
				//long startMili=System.currentTimeMillis();
				
				continuousQuery4(qosChangedList);
				
				//long endMili=System.currentTimeMillis();
				//System.out.println("continuousQuery结束 :"+endMili);
				//System.out.println("myStratege总耗时为:"+(endMili-startMili)+"毫秒");	
				
				//System.out.println("连续查询qos:" + continousQos);
				return 0;
			}	
		}
		
		//用于统计恢复机制的时间
		double recoveryTimeBegin = 0;//恢复机制开始
		double recoveryTimeEnd = 0;//恢复机制结束
		
		//恢复机制结束
		recoveryTimeBegin = System.currentTimeMillis();   
		
		int sumOfRequest = outputsFromChallenge.size();
		WebService begin = serviceMap.get("Provide");
		WebService end = serviceMap.get("Request");
		end.setCount(end.getInputs().size());
		List<WebService> reachVertices = new ArrayList<WebService>();
		
		//恢复数据结构
		recoveryReQueryForHJQ();
		
        RPT.clear();
        
        //long startMili=System.currentTimeMillis();
        
        
		boolean find=false;
		reachVertices.add(begin);
		
		//恢复机制结束
		recoveryTimeEnd = System.currentTimeMillis();
		
		while(reachVertices.size()!=0){
			Collections.sort(reachVertices);
			
			WebService v = reachVertices.remove(0);
			for(Object output:v.getOutputs()){
															
				if(!RPT.containsKey(output.toString())){//新的inst
					//RPT.put(output.toString(),v);
					//System.out.println("新的inst:"+output.toString());
					//String con = map1.get(output).toString();
					List<String> matchList = solveMap.get(output.toString());//所以能被output解决的inst						
					if(matchList==null){
					 matchList = findInstances(output.toString(),map1,map2);
					 //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^&&&");
					}
					
					
					if(!output.toString().equals(matchList.get(0)))//使matchList变成A1-A2-A3-A4
						for(int i=1;i<matchList.size();i++){
							if(output.toString().equals(matchList.get(i))){
								String st = matchList.get(0);
								matchList.set(0, matchList.get(i));
								matchList.set(i, st);
								break;
							}									
						}
										
					//System.out.println("matchList的大小："+matchList.size());
					boolean firstPlace=true;
					for(String inst : matchList){
//						if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//							System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						//if(!firstPlace && RPT.containsKey(inst)) continue;
						
						if(!RPT.containsKey(inst)){
							 RPT.put(inst, v);
							 //System.out.println("新的inst*:"+inst);
						 }else{
							 continue;
						 }
						
						firstPlace=false;
						 List<WebService> haveSameInstWebServiceList = IIT.get(inst);
						 if(haveSameInstWebServiceList==null){
							 //System.out.println("没有任何服务需要："+inst);
							 continue;
						 }
//						 if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//								System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						 
						 //System.out.println("有"+haveSameInstWebServiceList.size()+"个服务需要"+inst);
						 for(WebService w: haveSameInstWebServiceList){						
							 w.subCount();
							 //if(w.getName().equals("Request")) System.out.println("还剩"+end.getCount()+"个元素");
							 
							 if(w.getCount()==0){
								 w.setAllResponseTime(v.getAllResponseTime()+w.getSelfResponseTime());									
								 //enabledServices.put(w.getName(), w);
								 if(w.getName().equals("Request"))
									 if(end.getCount()==0){
										 //System.out.println("关键点是"+v.getName());
										 //System.out.println("找到了全部要求的元素");
										 //System.out.println(end.getAllResponseTime());
										 find = true;
										 //return;
										 continue;
										 //break;
									 }
									 
								 reachVertices.add(w);
									 //System.out.println("服务"+w.getName()+"可用了");
									// System.out.println("服务"+w.getName()+"的AllRequestTime:"+w.getAllResponseTime());
									 /////////serv1027640201 serv404388610  404388610
							
								 
							 }
						 }
						 
					}
					
				}
			}
		}
		
		//System.out.println("RPT.size()"+RPT.size());
		
		//long endMili=System.currentTimeMillis();
		//System.out.println("continuousQuery结束 :"+endMili);
		//System.out.println("myStratege总耗时为:"+(endMili-startMili)+"毫秒");
		
//		
//		if(!find)
//			System.out.println("找不到");
//		else
//			System.out.println("最优解的qos:"+end.getAllResponseTime());
		//System.out.println("重查qos:" + serviceMap.get("Request").getAllResponseTime());
		
		continousQos = serviceMap.get("Request").getAllResponseTime();		
		
		return recoveryTimeEnd - recoveryTimeBegin;
	}
	
	public boolean isCriticalNode(WebService parent,WebService successor){
		double max=0;
		WebService ws=null;
		for(int i=0;i<successor.getInputs().size();i++){
			ws = RPT.get(successor.getInputs().get(i));
			if( ws == parent ){
				//System.out.println("这里证明了判断过"+parent.getName()+"i="+i);				
				if(max==0){
					max = parent.getNewAllResponseTime();
				}else{
					max = max-parent.getNewAllResponseTime()>0?max:parent.getNewAllResponseTime();
				}
			}else{
				if(ws.getNewAllResponseTime()==0){
					max = max - ws.getAllResponseTime()>0?max:ws.getAllResponseTime();
				}else{
					max = max - ws.getNewAllResponseTime()>0?max:ws.getNewAllResponseTime();
				}				
			}
			if(ws.getNewAllResponseTime()>0)
				System.out.println(ws.getName()+" "+ws.getNewAllResponseTime()+" ");
			else
				System.out.println(ws.getName()+" "+ws.getAllResponseTime()+" ");
		}
		System.out.println();
		if(max==parent.getNewAllResponseTime()) return true;
		else return false;
		
	}
	/**
	 * 找出能够提供inst的qos最好的服务
	 * @param successor
	 * @return
	 */
	public WebService getCriticalParent(WebService successor){
		Set<WebService> parentSet = new HashSet<WebService>();
		WebService w = null;
		List<WebService> parentList  = new ArrayList<WebService>();
		
		for(int i=0;i<successor.getInputs().size();i++){
			 w = RPT1.get(successor.getInputs().get(i));
			 //HJQ_改
			 if(w == null){
				 return null;
			 }
			 if(parentSet.add(w)){
				 parentList.add(w);
			 }
		}
		
		sortParents(parentList);
		
//		System.out.println("@@@@@@@@@");
//		for(int i=0;i<parentList.size();i++){
//			WebService ww = parentList.get(i);
//			if(ww.getNewAllResponseTime()>0)
//				System.out.println(ww.getName()+" "+ww.getNewAllResponseTime());
//			else
//				System.out.println(ww.getName()+" "+ww.getAllResponseTime());
//		}
//		System.out.println("@@@@@@@@@@");
		
		return parentList.get(parentList.size()-1);
	}
	/**
	 * 跑仅有qos变化的实验BC
	 * @param type
	 * @param sb
	 * @param OTOfCopy
	 * @param solveMapOfCopy
	 * @param RPT1OfCopy
	 * @throws IOException
	 */
	public void testManyTime(String type, StringBuilder sb, Map<String, List<WebService>> OTOfCopy, Map<String, List<String>> solveMapOfCopy, Map<String, WebService> RPT1OfCopy) throws IOException{

		requeryTime = 0;
		continueTime = 0;

		int time=0;		
		while(time<count){			
			time++;
			if(time%1000 == 0){
				System.out.println("第" + time + "组");
			}
			if(time==1){
				recordTime1.append("continuousQuery的用时"+System.getProperty("line.separator"));
				recordTime2.append("reQuery的用时"+System.getProperty("line.separator"));
				recordCompare.append("比较"+System.getProperty("line.separator"));
				recordRealSum.append("实际需要更新的服务个数"+System.getProperty("line.separator"));
			}
			//HJQ randomTest();
			randomTest(solveMapOfCopy, OTOfCopy, RPT1OfCopy);
		}
		
		//cjh add
		System.out.println("重查时间:"+requeryTime/count);
		System.out.println("连续查询时间:"+continueTime/count+System.lineSeparator());
		
		sb.append((requeryTime / count) + "," + (continueTime / count) + System.lineSeparator());
	}
	/**
	 * 自动完成仅有qos变化的实验BC
	 * @throws IOException
	 */
	public void testManyTimeAuto() throws IOException{
		// 复制solveMap
		Map<String, List<String>> solveMapOfCopy = DataGetter.copyStrToStrList(solveMap);

		// HJQ,复制OT
		Map<String, List<WebService>> OTOfCopy = null;

		// HJQ,复制PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);
		
		//复制RPT1和serviceMap
		copy();
		
		String type = "B";
		
		int[] testGroupB = new int[]{50,100,150,200,250,300,350,400,450,500,550,600,650,700,750,800};
		int[] testGroupC = new int[]{2,4,6,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
		
		//记录所有实验时间
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < testGroupB.length; i++){
			System.out.print("实验" + type + ":");
			if(type.equals("B")){
				this.sumOfChangedService = testGroupB[i];
				System.out.println(testGroupB[i]);
			}
			else if(type.equals("C")){
				this.sumOfChangedService = testGroupC[i];
				System.out.println(testGroupC[i]);
			}
			testManyTime(type, sb, OTOfCopy, solveMapOfCopy, RPT1OfCopy);
		}
		
		out1=new FileOutputStream("result" + type + ".csv");
		out1.write(sb.toString().getBytes());
		out1.close();
	}

	/**
	 * 手动构造服务数较少的数据集
	 * @throws FileNotFoundException
	 */
	public void constructByCode() throws FileNotFoundException{
		File file = new File("testing.txt");
		Scanner scanner = new Scanner(file);
		
		while(scanner.hasNextLine()){
			String oneLine = scanner.nextLine();
			
			String[] splits = oneLine.split(";");
			
			WebService webService = new WebService();
			
			webService.setName(splits[0]);
			webService.setInputs(Arrays.asList(splits[1].split(":")));
			webService.setOutputs(Arrays.asList(splits[2].split(":")));
			webService.setCount(webService.getInputs().size());
			webService.setSelfResponseTime(Double.valueOf(splits[3]));
			webService.setAllResponseTime(0);
			webService.setNewAllResponseTime(0);
			
			testServiceList.add(webService);
		}
		
		//处理始点
		List<String> outputs = testServiceList.get(0).getOutputs();
		
		for(String inst: outputs){
			this.inputsFromChallenge.add(inst);
		}
		
		//处理终点
		List<String> inputs = testServiceList.get(testServiceList.size()-1).getInputs();
		
		for(String inst: inputs){
			this.outputsFromChallenge.add(inst);
		}
		
		//填充IIT
		for(int i = 1; i < testServiceList.size()-1; i++){
			List<String> temp = testServiceList.get(i).getInputs();
			
			for(String input:temp){
				if(IIT.containsKey(input)){
					IIT.get(input).add(testServiceList.get(i));
				}
				else{
					List<WebService> webServices = new ArrayList<WebService>();
					webServices.add(testServiceList.get(i));
					IIT.put(input, webServices);
				}
			}
		}
		
		//填充map1
		this.map1 = new HashMap<String, String>();
		
		map1.put("a", "1");
		map1.put("b", "2");
		map1.put("c", "3");
		map1.put("d", "4");
		map1.put("e", "5");
		map1.put("f", "6");
		map1.put("g", "7");
		map1.put("h", "8");
		map1.put("v", "9");
		map1.put("t", "10");
		
		//填充map2
		this.map2 = new HashMap<String, List<String>>();
		
		for(String key: map1.keySet()){
			String con = map1.get(key);
			
			if(map2.containsKey(con)){
				map2.get(con).add(key);
			}
			else{
				List<String> insts = new ArrayList<String>();
				insts.add(key);
				map2.put(con, insts);
			}
		}
		//填充serviceMap
		for(WebService webService : testServiceList){
			serviceMap.put(webService.getName(), webService);
		}
	}
	/**
	 * 手动构造新增服务
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public List<WebService> constructInsertService(String fileName) throws FileNotFoundException{
		List<WebService> insertedList = new ArrayList<WebService>();
		
		File file = new File(fileName);
		Scanner scanner = new Scanner(file);
		
		while(scanner.hasNextLine()){
			String oneLine = scanner.nextLine();
			
			String[] splits = oneLine.split(";");
			
			WebService webService = new WebService();
			
			webService.setName(splits[0]);
			webService.setInputs(Arrays.asList(splits[1].split(":")));
			webService.setOutputs(Arrays.asList(splits[2].split(":")));
			webService.setCount(webService.getInputs().size());
			webService.setSelfResponseTime(Double.valueOf(splits[3]));
			webService.setAllResponseTime(0);
			webService.setNewAllResponseTime(0);
			
			insertedList.add(webService);
			testServiceList.add(webService);
		}
		
		//填充serviceMap
		for(WebService webService : insertedList){
			serviceMap.put(webService.getName(), webService);
		}
		
		testServiceList.remove(serviceMap.get("Provide"));
		testServiceList.remove(serviceMap.get("Request"));
		
		return insertedList;
	}
	/*
	public static void main(String[] args) throws Exception{
		MyFrame frame = new MyFrame();
		
		frame.run();
		
	}
	/**
	 * 用于测试
	 * @throws FileNotFoundException
	 * @throws CloneNotSupportedException
	 */
	public void run() throws FileNotFoundException, CloneNotSupportedException {
		/*
		 * //文件构造新服务 
		 * List<WebService> insertedList = constructInsertService("insertTesting.txt");
		 * 
		 * Map<String, List<WebService>> changedMap = new HashMap<String,
		 * List<WebService>>();
		 * 
		 * changedMap.put("1", insertedList);
		 * 
		 * adapt(changedMap);
		 * 
		 */
		
		// 代码构造新服务
		Change change = new Change(testServiceList,
				DataGetter.getServicesAllInst(testServiceList, "all"),
				DataGetter.getServicesAllInst(testServiceList, "all"),
				(int) StatInfo.instAverage(testServiceList, "input"),
				(int) StatInfo.instAverage(testServiceList, "output"), 30);
		
		Map<String, List<WebService>> changedMap = change.changeWebServices(9 - 2,4,3,2);
		
		System.out.println("----------------------------发生变化的服务------------------------------------");	
		System.out.println("-------------------------------删除----------------------------------------");
		
		List<WebService> webServicesOfDelete = changedMap.get("delete");
		
		for(WebService webService : webServicesOfDelete){
			System.out.println(webService);
		}
		
		System.out.println("-------------------------------新增----------------------------------------");
		
		List<WebService> webServicesOfAdd = changedMap.get("new");
		
		for(WebService webService : webServicesOfAdd){
			serviceMap.put(webService.getName(), webService);
			System.out.println(webService);
		}
		
		System.out.println("------------------------------qos变化---------------------------------------");
		
		List<WebService> webServicesOfQOSChange = changedMap.get("qosChange");
		
		for(WebService webService : webServicesOfQOSChange){
			System.out.println(webService);
		}
		
		adapt(changedMap);
	}

	/**
	 * 新增删除的预处理
	 * @param changedMap
	 * @return
	 */
	public List<WebService> adapt(Map<String, List<WebService>> changedMap){
		//需要用于连续查询处理的服务列表
		List<WebService> needCQList = new ArrayList<WebService>();
		
		//添加删除
		needCQList.addAll(adaptDeleteWS(changedMap.get("delete")));
		//添加新增
		needCQList.addAll(adaptInsertWS(changedMap.get("new")));
		//添加qos改变
		needCQList.addAll(changedMap.get("qosChange"));
		//---------------------------------------------------------------
		
		continuousQuery4(needCQList);
		
		System.out.println("----------------------------RPT1------------------------------------");
		for(String key:RPT1.keySet()){
			System.out.println("webservice:" + RPT1.get(key).getName() + "-->" + "inst:" + key);
		}

		System.out.println("----------------------------serviceMap------------------------------------");
		for(String webServiceName: serviceMap.keySet()){
			System.out.println(serviceMap.get(webServiceName));
		}
		
		recoveryReQueryForHJQ();
		reQuery();
		
		
		System.out.println("continuous最优解是:"+continousQos);
		System.out.println("requeryQos最优解是:"+requeryQos);
		
		return needCQList;
	}
	/**
	 * 新增的预处理
	 * @param insertedList
	 * @return
	 */
	private List<WebService> adaptInsertWS(List<WebService> insertedList) {
		//需要用于连续查询处理的服务列表
		List<WebService> needCQList = new ArrayList<WebService>();
		
		//相对于RPT1,新增的inst
		List<String > newInstances = new ArrayList<String>();
		
		//处理添加节点,完善IIT,OT,solvedmap,RPT
		for(WebService webService: insertedList){
			
			List<String> inputs = webService.getInputs();
			
			for(String input: inputs){
				//处理IIT
				if(IIT.containsKey(input)){
					IIT.get(input).add(webService);
				}
				else{
					List<WebService> webServices = new ArrayList<WebService>();
					
					webServices.add(webService);
					
					IIT.put(input, webServices);
				}
			}
			
			//标志是否可达
			boolean flag = isReachable(webService);
			
			//首次可达
			//首次可达要更新qos,不然后面处理不了
			if(flag){
				WebService criticalParent = getCriticalParent(webService);
				
				webService.setNewAllResponseTime(criticalParent.getAllResponseTime() + webService.getSelfResponseTime());
				
				needCQList.add(webService);
				
				//outputs的预处理
				outputsHandler(webService, newInstances);
			}
		}
		
		//处理新增节点后新增的inst输出(RPT原来没有),这些inst可能导致更多可达点进入RPT
		while(newInstances.size() != 0){
			String newInstance = newInstances.remove(0);
			
			//RPT1新增inst可满足的服务
			List<WebService> webServices = IIT.get(newInstance);
			
			//可能不存在inputs有matchedInst的服务
			if(webServices != null){
				//从qos小的开始处理
				Collections.sort(webServices);
				
				for(WebService webService: webServices){
					//选取不可达点
					if(webService.getCount() > 0){
						webService.subCount();
						// 导致可达
						if (webService.getCount() == 0) {
							WebService criticalParent = getCriticalParent(webService);
							//这里把一些误认为是可达点的服务排除在外
							if(criticalParent != null){
								webService.setNewAllResponseTime(
										criticalParent.getAllResponseTime() + webService.getSelfResponseTime());
								// 处理可达服务的outputs
								outputsHandler(webService, newInstances);
							}
							else{
								webService.setCount(webService.getCount() + 1);
							}

						}
					}
				}
			}
		}
		
		return needCQList;
	}
	/**
	 * 处理可达服务的输出inst
	 * @param webService
	 * @param isReachable
	 * @param newInstances
	 * @param newInstancesRecord 
	 */
	private void outputsHandler(WebService webService, List<String> newInstances) {
		
		if(webService.getName().equals("Request")){
			return;
		}	

		List<String> outputs = webService.getOutputs();
		
		//处理outputs inst,同时要处理它们的可匹配inst
		for (String output : outputs) {
			List<String> matchedListOfOutputs = solveMap.get(output);		

			// 处理solveMap
			if (matchedListOfOutputs == null) {
				matchedListOfOutputs = findInstances(output, map1, map2);
				
				solveMap.put(output, matchedListOfOutputs);
			}
			
			for (String matchedInstOfOutput : matchedListOfOutputs) {
				
				// 处理OT
				if (OT.containsKey(matchedInstOfOutput)) {
					List<WebService> webServices = OT.get(matchedInstOfOutput);
					if(!webServices.contains(webService)){
						webServices.add(webService);
					}
				} else {
					List<WebService> webServices = new ArrayList<WebService>();

					webServices.add(webService);

					OT.put(matchedInstOfOutput, webServices);
				}

				// 处理RPT1
				if (!RPT1.containsKey(matchedInstOfOutput)) {
					// 处理好多了一个输出的inst(相对于RPT1)
					RPT1.put(matchedInstOfOutput, webService);

					newInstances.add(matchedInstOfOutput);

				}
			}
		}
	}

	/**
	 * 判断某个点是否可达
	 * inputs是某个点的输入
	 * @param inputs
	 * @return
	 */
	private boolean isReachable(WebService webService) {
		List<String> inputs = webService.getInputs();
		
		boolean flag = true;
		//对于新增服务的所有input的所有可匹配inst都在RPT1中就是可达
		for (String input : inputs) {
			// 满足其中一个input,count--
			if(RPT1.containsKey(input)){
				webService.subCount();
			}
			else{
				flag = false;
			}
			
		}
		return flag;
	}
	/**
	 * 删除服务的预处理
	 * @param deletedList
	 */
	private List<WebService> adaptDeleteWS(List<WebService> deletedList) {

		List<WebService> copyDel = new ArrayList();
		for (WebService del : deletedList) {
			copyDel.add(del);
		}

		List<WebService> needUpdate = new ArrayList<WebService>();
		for (WebService ws : deletedList) {
			// 在IIT（输入-某服务）中删除ws相关的东西
			for (String input : (List<String>) ws.getInputs()) {
				List<WebService> les = IIT.get(input);// 接收input的list
				les.remove(ws);
				if (les.isEmpty()) {
					IIT.remove(input);
				}
			}
		}

		if (deletedList == null) {
			return new ArrayList<WebService>();
		}

		while (!deletedList.isEmpty()) {
			WebService ws = deletedList.remove(0);

			if (ws.getCount() == 0) {
				// 删除与ws的输出相关的东西
				for (String output : (List<String>) ws.getOutputs()) {
					// OT:输出-某服务
					List<WebService> les = OT.get(output);
					
					if(les != null){
						les.remove(ws);// 能提供output的list
					}
					else{
						continue;
					}

					if (les.isEmpty()) {
						// 这里表示删除了所有提供某个output的服务，所以k可能会造成一些服务的失效
						OT.remove(output);
						// OT中没有可以输出某output的服务，因此这是solveMap中也要删除output的一项
						//this.solveMap.remove(output);
						// 遍历所有有output这个输入的服务
						if (IIT.get(output) != null) {
							for (WebService loss : IIT.get(output)) {
								if (loss.getCount() == 0) {// 本来可到的，现在由于某一输入减少了，导致其不可达
									if (!copyDel.contains(loss)) {
										deletedList.add(loss);
										copyDel.add(loss);
									}
								}
								loss.setCount(loss.getCount() + 1);// 由于可达服务数减少，故将其count+1
							}
						}
					} else {
						// 当les中还有服务时，就要判断是否需要更新QOS
						Collections.sort(les);
						double outputQos = les.get(0).getAllResponseTime();
						if (outputQos > ws.getAllResponseTime()) {// 更新下一个服务的QOS

							List<WebService> subs = IIT.get(output);// 接收output的list

							if (subs != null) {
								for (WebService sub : subs) {
									if (sub.getCount() == 0) {// 只对有output输入的可达点操作
										double newQos = outputQos;
										for (String in : (List<String>) sub.getInputs()) {
											List<WebService> ps = OT.get(in);// 找每个输入的父亲
											Collections.sort(ps);// 目的是取父亲中Qos最少的Qos
											newQos = ps.get(0).getAllResponseTime() > newQos
													? ps.get(0).getAllResponseTime() : newQos;
										}
										sub.setNewAllResponseTime(newQos + sub.getSelfResponseTime());
										if (sub.getAllResponseTime() != sub.getNewAllResponseTime()) {
											needUpdate.add(sub);
										}
									}
								}
							}
						}
					}

					// 在RPT中更新相应的inst
					for (String match : solveMap.get(output)) {
						WebService w = RPT1.get(match);
						// 取出RPT里面inst对应的服务w如果w==ws那么说明该服务已经被删除，需要找一个服务来代替它
						if (ws.equals(w)) {
							RPT1.remove(match);
							for (String aa : solveMap.get(match)) {
								List<WebService> updates = OT.get(aa);

								// 成功找到替代的服务，将其放入RPT
								if (updates != null && !updates.isEmpty()) {
									Collections.sort(updates);
									RPT1.put(match, updates.get(0));
									break;
								}

							}
						}
					}
				}
			}
		}
		
		return needUpdate;

	}
	
	public void adaptIIT(Map<String, List<WebService>> addAndDeleteMap){
		
		for(WebService webService: addAndDeleteMap.get("new")){
			List<String> inputs = webService.getInputs();
			
			for(String input: inputs){
				//处理IIT
				if(IIT.containsKey(input)){
					IIT.get(input).add(webService);
				}
				else{
					List<WebService> webServices = new ArrayList<WebService>();
					
					webServices.add(webService);
					
					IIT.put(input, webServices);
				}
			}
		}
		for(WebService webService: addAndDeleteMap.get("delete")){
			for (String input : (List<String>)webService.getInputs()) {
				List<WebService> les = IIT.get(input);// 接收input的list
				les.remove(webService);
				if (les.isEmpty()) {
					IIT.remove(input);
				}
			}
		}
	}
	
	/**
	 * 测试多次实验,通过硬编码实现测5000次
	 * @param type 是指实验的类型
	 * @param sb 
	 * @param realInsts 
	 * @param allInsts 
	 * @param experimentC 
	 * @param experimentCC 
	 * @param experimentPQ 
	 * @throws IOException
	 */
	public void testManyTimeForHJQ(String type, StringBuilder sb, Map<String, List<WebService>> OTOfCopy, Map<String, List<WebService>> IITOfCopy,
			Map<String, WebService> RPT1OfCopy, List<String> allInsts, List<String> realInsts, Map<Integer, List<Double>> experimentCC, Map<Integer, List<Double>> experimentCR, Map<Integer, List<Double[]>> experimentPQ) throws IOException {
		requeryTime = 0;
		continueTime = 0;
		
		//根据实验内容来选择改变策略,即存放着全部服务还是仅仅是实际网络结构中的服务
		List<WebService> testServiceList = new ArrayList<WebService>();
		
		int time = 0;
		while (time < count) {
			this.dead = false;
			time++;
			if(time%1000 == 0){
				System.out.println("第" + time + "组");
			}
			if (time == 1) {
				recordTime1.append("continuousQuery的用时" + System.getProperty("line.separator"));
				recordTime2.append("reQuery的用时" + System.getProperty("line.separator"));
				recordCompare.append("比较" + System.getProperty("line.separator"));
				recordRealSum.append("实际需要更新的服务个数" + System.getProperty("line.separator"));
			}

			// 恢复testServiceList,随机产生变化服务需要
			testServiceList.clear();		
			if(type.equals("B")){
				for (String key : serviceMap.keySet()) {
					WebService webService = serviceMap.get(key);

					String name = webService.getName();

					if (!name.equals("Provide") && !name.equals("Request")) {
						testServiceList.add(webService);
					}
				}
			}else if(type.equals("C")){
				for(String key: RPT1OfCopy.keySet()){
					String name = RPT1OfCopy.get(key).getName();
					WebService webService = serviceMap.get(name);
					
					if (!name.equals("Provide") && !name.equals("Request")) {
						if(!testServiceList.contains(webService)){
							testServiceList.add(webService);
						}
					}
				}
			}
			
			// HJQ randomTest();
			randomTestForHJQ(type, testServiceList, OTOfCopy, IITOfCopy, RPT1OfCopy, allInsts, realInsts, experimentCC, experimentCR, experimentPQ);
			if(dead){
				time--;
			}
		}

		// cjh add
		System.out.println("重查时间:" + requeryTime / count);
		System.out.println("连续查询时间:" + continueTime / count);

		sb.append((requeryTime / count) + "," + (continueTime / count) + System.lineSeparator());
	}
	
	/**
	 * 自动跑新增删除变化的实验BC
	 * @throws IOException 
	 */
	public void testManyTimeForHJQAuto(String type) throws IOException{
		// HJQ,复制OT,IIT和RPT1
		// HJQ,复制OT
		Map<String, List<WebService>> OTOfCopy = null;

		try {
			OTOfCopy = DataGetter.copyStrToWsList(OT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// HJQ,复制IIT
		Map<String, List<WebService>> IITOfCopy = null;

		try {
			IITOfCopy = DataGetter.copyStrToWsList(IIT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		// HJQ,复制PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);

		// 复制RPT1和serviceMap
		copy();
		
		//所有inst
		List<String> allInsts = new ArrayList<String>();
		for(String instAndCon: map1.keySet()){
			if(instAndCon.contains("inst")){
				allInsts.add(instAndCon);
			}
		}
		
		//所有RPT1中的inst,就是说随机生成的新服务肯定会影响当前网络结构
		List<String > realInsts = new ArrayList<String>();
		realInsts.addAll(RPT1.keySet());
		
		
		int[] testGroup = null;
		
		if(type.equals("B")){
			//50,100,150,200,250,300,350,400,450,500,550,600,650,700,750,800,850,900,950,1000
			testGroup = new int[]{50,100,150,200,250,300,350,400,450,500,550,600,650,700,750,800,850,900,950,1000};
		}
		else if(type.equals("C")){
			testGroup = new int[]{2,4,6,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
		}
		
		//记录所有实验时间
		StringBuilder sb = new StringBuilder();
		//跑实验B同时跑实验C----记录实验C中的实验B结果
		//跑实验B同时跑实验C----记录实验C中的实验C结果
		Map<Integer, List<Double>> experimentCC = new HashMap<Integer, List<Double>>();
		Map<Integer, List<Double>> experimentCR = new HashMap<Integer, List<Double>>();
		
		//统计基于PQ的两个算法对比得出PQ相关的阈值
		Map<Integer, List<Double[]>> experimentPQ = new HashMap<Integer, List<Double[]>>();
		
		for(int i = 0; i < testGroup.length; i++){
			System.out.print("实验" + type + ":");
			System.out.println(testGroup[i]);
			
			this.sumOfChangedService = testGroup[i];
			testManyTimeForHJQ(type, sb, OTOfCopy, IITOfCopy, RPT1OfCopy, allInsts, realInsts, experimentCC, experimentCR,experimentPQ);
		}
		
		out1 = new FileOutputStream("result" + type + ".csv");
		out1.write(sb.toString().getBytes());
		out1.close();
		
		//记录实验C中的实验信息
		StringBuilder sc = new StringBuilder();
		
		//处理实验C的连续查询实验结果-----------------------------------------------------------------
		for(Integer key : experimentCC.keySet()){
			double sum = 0;
			int num = experimentCC.get(key).size();
			
			for(Double result : experimentCC.get(key)){
				sum += result;
			}
			
			sc.append(key + "," + sum/num + "," + num + System.lineSeparator());
			
		}
		
		out1 = new FileOutputStream("resultCC.csv");
		out1.write(sc.toString().getBytes());
		out1.close();
		
		//初始化
		sc = new StringBuilder();

		// 处理实验C的重查实验结果-----------------------------------------------------------------
		for (Integer key : experimentCR.keySet()) {
			double sum = 0;
			int num = experimentCR.get(key).size();

			for (Double result : experimentCR.get(key)) {
				sum += result;
			}

			sc.append(key + "," + sum / num + "," + num + System.lineSeparator());

		}

		out1 = new FileOutputStream("resultCR.csv");
		out1.write(sc.toString().getBytes());
		out1.close();

		// 处理实验C的连续查询实验结果-----------------------------------------------------------------
		for (Integer key : experimentPQ.keySet()) {
			double requerySum = 0;
			double continuequerySum = 0;
			int num = experimentPQ.get(key).size();

			for (Double[] result : experimentPQ.get(key)) {
				requerySum += result[0];
				continuequerySum += result[1];
			}

			PrintPQBCDiagram.getInstance().print(key + "," + requerySum / num + "," + 
												continuequerySum / num + "," + 
												num + System.lineSeparator());

		}

	}
	
	/**
	 * 一组实验,删除新增和qos改变三种一起上的测试
	 * @param type 
	 * @param rPT1OfCopy 
	 * @param IITOfCopy 
	 * @param OTOfCopy 
	 * @param RPT1OfCopy 
	 * @param allInsts 
	 * @param realInsts 
	 * @param experimentC 
	 * @param experimentCC 
	 * @param experimentPQ 
	 * @param iITOfCopy 
	 * @param oTOfCopy 
	 */
	public void randomTestForHJQ(String type, List<WebService> testServiceList, Map<String, List<WebService>> OTOfCopy, Map<String, List<WebService>> IITOfCopy,
			Map<String, WebService> RPT1OfCopy, List<String> allInsts, List<String> realInsts, Map<Integer, List<Double>> experimentCC, Map<Integer, List<Double>> experimentCR, Map<Integer, List<Double[]>> experimentPQ) {
		// HJQ_所有变化的服务
		List<WebService> qosChangedList = new ArrayList<WebService>();
		// HJQ_qosChangedList的副本,即qos发生变化的服务
		List<WebService> copyChangedList = new ArrayList<WebService>();
		
		//根据不同的实验取不同的inst集
		List<String> experimentNeed = null;
		
		if(type.equals("B")){
			experimentNeed = allInsts;
		}
		else if(type.equals("C")){
			experimentNeed = realInsts;
		}
		
		Change change = new Change(testServiceList, experimentNeed, allInsts, 
				(int) StatInfo.instAverage(testServiceList, "input"),
				(int) StatInfo.instAverage(testServiceList, "output"), 30);
		
		//比例
		int typeNumber = 5;
		
		//1:1:3
		int sum = sumOfChangedService;
		int deletedWSSum = sum / typeNumber;
		int addWSSum = sum / typeNumber;
		int changedWSSum = changedWSSum = (sum % typeNumber == 0) ? (sum / typeNumber * 3) : (sum / typeNumber * 3) + sum % typeNumber;
		
		Map<String, List<WebService>> changedMap = change.changeWebServices(sum, addWSSum, changedWSSum,
				deletedWSSum);
		
		// 处理copyChangedList
		for (WebService webService : changedMap.get("qosChange")) {
			WebService newWS = null;
			try {
				newWS = (WebService) webService.clone();
			} catch (CloneNotSupportedException e) { 
				e.printStackTrace();
			}
			copyChangedList.add(newWS);
		}
		
		//复制发生变化的服务
		Map<String, List<WebService>> changedMapOfCopy = copyChangedMap(changedMap);
		
		//统计实际的数量
		//跑实验B同时统计实验C
		this.realSum = 0;
		
		for(WebService webService : changedMap.get("qosChange")){
			if(webService.getCount() == 0){
				this.realSum ++;
			}
		}
		
		for(WebService webService : changedMap.get("delete")){
			if(webService.getCount() == 0){
				this.realSum ++;
			}
		}
		
		for(WebService webService : changedMap.get("new")){
			//默认是实际的
			boolean flag = true;
			
			for(String input : (List<String>)webService.getInputs()){
				if(!RPT1OfCopy.containsKey(input)){
					flag = false;
					break;
				}
			}
			
			if(flag){
				this.realSum++;
			}
			
		}
		
		// ---------------------------------------------------------------

		double totalTime = 0;
		long startMili = 0;
		long endMili = 0;
		List<Double> timeList = new ArrayList<Double>();
		double t1;// 连续查询的时间
		double t2;// 重查的时间

		// 下面将对变化后的服务列分别进行连续查询和重查,比较花费的时间
		
		/*---------------------------------continuequery--------------------------------*/
		
		for (int i = 0; i < 10; i++) {// 测出10组
			//统计PQ的长度
			this.PQMaxSize=0;
			
			// 还RPT1和serviceMap1
			recoveryForHJQ(RPT1OfCopy);//ServiceMap所有服务设为最初值,RPT1也是
			recoveryWithNewQosForHJQ(copyChangedList);//改变预定服务的qos值,因为上一行代码置为最初值
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			recoveryIITForCJH(IITOfCopy);
			recoveryOTForHJQ(OTOfCopy);
			
			startMili = System.nanoTime();// 当前时间对应的毫秒数
			
			qosChangedList.clear();
			//新增删除的预处理
			// 添加删除
			qosChangedList.addAll(adaptDeleteWS(addAndDeleteMap.get("delete")));
			// 添加新增
			qosChangedList.addAll(adaptInsertWS(addAndDeleteMap.get("new")));
			// 添加qos改变
			qosChangedList.addAll(changedMap.get("qosChange"));
			
			continuousQuery4(qosChangedList);
			endMili = System.nanoTime();
			timeList.add(new Double((endMili - startMili)/1E6));
			
			if(dead){
				return ;
			}
			
		}

		sortDoubleList(timeList);
		for (int i = 1; i <= 8; i++) {
			totalTime = totalTime + timeList.get(i);
		}

		t1 = totalTime / 8;

		//*System.out.println("continuousQueryTotal总耗时为:" + totalTime / 8 + "毫秒");

		recordTime1.append(totalTime / 8 + System.getProperty("line.separator"));

		//*System.out.println("continuous最优解是:" + continousQos);
		// cjh add
		continueTime += t1;
		
		/*---------------------------------requery--------------------------------*/
		
		totalTime = 0;
		timeList.clear();
		
		for (int i = 0; i < 10; i++) {
			recoveryReQueryForHJQ();
			recoveryIITForCJH(IITOfCopy);
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			
			startMili = System.nanoTime();// 当前时间对应的毫秒数
			adaptIIT(addAndDeleteMap);
			reQuery();
			endMili = System.nanoTime();
			timeList.add(new Double((endMili - startMili)/1E6));
		}

		sortDoubleList(timeList);
		for (int i = 1; i <= 8; i++) {
			totalTime = totalTime + timeList.get(i);
		}

		t2 = totalTime / 8;
		// cjh add
		requeryTime += t2;
		// 记录时间大小
		if (t1 < t2) {
			recordCompare.append(1 + System.getProperty("line.separator"));
		} else if (t1 == t2)
			recordCompare.append(0 + System.getProperty("line.separator"));
		else
			recordCompare.append(-1 + System.getProperty("line.separator"));

		//*System.out.println("最优解的qos:" + requeryQos);

		recordTime2.append(totalTime / 8 + System.getProperty("line.separator"));

		//*System.out.println("reQueryTotal总耗时为:" + totalTime / 8 + "毫秒");

		if (requeryQos == continousQos) {
			//*System.out.println("前后正确");
		} else {
			//*System.out.println("前后bubububububu正确");
		}

		// 记录实际需要更新的ws(count!=0):的个数
		recordRealSum.append(realSum + System.getProperty("line.separator"));
		//*System.out.println("实际需要更新的ws(count!=0):的个数：" + realSum);
		
		
		//跑实验B同时统计实验C的连续查询时间
		if(experimentCC.get(this.realSum) == null){
			List<Double> results = new ArrayList<Double>();
			results.add(t1);
			experimentCC.put(this.realSum, results);
		}
		else{
			experimentCC.get(this.realSum).add(t1);
		}
		
		// 跑实验B同时统计实验C的重查时间
		if (experimentCR.get(this.realSum) == null) {
			List<Double> results = new ArrayList<Double>();
			results.add(t2);
			experimentCR.put(this.realSum, results);
		} else {
			experimentCR.get(this.realSum).add(t2);
		}
		
		//统计基于PQ的两个算法对比得出PQ相关的阈值
		if (experimentPQ.get(this.PQMaxSize) == null) {
			List<Double[]> results = new ArrayList<Double[]>();
			
			Double[] times = new Double[2];
			
			times[0] = t2;
			times[1] = t1;
			
			results.add(times);
			experimentPQ.put(this.PQMaxSize, results);
		} else {
			Double[] times = new Double[2];
			
			times[0] = t2;
			times[1] = t1;
			
			experimentPQ.get(this.PQMaxSize).add(times);
		}
	}
	
	private void recoveryReQueryForHJQ() {
		for(String inst: IIT.keySet()){
			List<WebService> webServices = IIT.get(inst);
			
			for(WebService webService: webServices){
				webService.setCount(webService.getInputs().size());
				webService.setAllResponseTime(0);
				webService.setNewAllResponseTime(0);
			}
		}
	}

	private Map<String, List<WebService>> recoveryWithAddAndDeleteForHJQ(Map<String, List<WebService>> changedMapOfCopy) {
		Map<String, List<WebService>> addAndDeleteMap = new HashMap<String, List<WebService>>();
		
		List<WebService> deletedWS = new ArrayList<WebService>();
		
		for(WebService webService : changedMapOfCopy.get("delete")){
			WebService newWS = null;
			newWS = serviceMap.get(webService.getName());
			deletedWS.add(newWS);
		}
		
		
		List<WebService> addWS = new ArrayList<WebService>();
		
		for(WebService webService : changedMapOfCopy.get("new")){
			WebService newWS = null;
			try {
				newWS = (WebService) webService.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			addWS.add(newWS);
		}
		
		addAndDeleteMap.put("delete", deletedWS);
		addAndDeleteMap.put("new", addWS);
		
		return addAndDeleteMap;
		
	}

	private void recoveryIITForCJH(Map<String, List<WebService>> IITOfCopy) {
		if(IITOfCopy != null){
			this.IIT.clear();
			
			for(String key: IITOfCopy.keySet()){
				List<WebService> origins = IITOfCopy.get(key);
				
				List<WebService> services = new ArrayList<WebService>();
				
				for(WebService webService : origins){
					services.add(serviceMap.get(webService.getName()));
				}
				
				this.IIT.put(key, services);
			}
		}
		
	}

	private void recoveryOTForHJQ(Map<String, List<WebService>> OTOfCopy) {
		if(OTOfCopy != null){
			this.OT.clear();
			
			for(String key: OTOfCopy.keySet()){
				List<WebService> origins = OTOfCopy.get(key);
				
				List<WebService> services = new ArrayList<WebService>();
				
				for(WebService webService : origins){
					services.add(serviceMap.get(webService.getName()));
				}
				
				this.OT.put(key, services);
			}
			
		}
		
	}


	private Map<String, List<WebService>> copyChangedMap(Map<String, List<WebService>> changedMap) {
		Map<String, List<WebService>> result  = new HashMap<String, List<WebService>>();
		
		for(String key: changedMap.keySet()){
			List<WebService> oldValue = changedMap.get(key);
			
			List<WebService> value = new ArrayList<WebService>();
			
			for(WebService webService: oldValue){
				WebService newWS = null;
				try {
					newWS = (WebService) webService.clone();
				} catch (CloneNotSupportedException e) {
					
					e.printStackTrace();
				}
				
				value.add(newWS);
			}
			
			result.put(key, value);
		}
		
		return result;
	}

	//HJQ_ 将copyChangedList中的服务的新qos 覆盖掉serviceMap对应服务的qos
	public void recoveryWithNewQosForHJQ(List<WebService> copyChangedList){
		for(int i=0;i<copyChangedList.size();i++){
			WebService record = copyChangedList.get(i);
			WebService ws = serviceMap.get(record.getName());
			if(ws != null){
				ws.setAllResponseTime(record.getAllResponseTime());
				ws.setNewAllResponseTime(record.getNewAllResponseTime());
				ws.setSelfResponseTime(record.getSelfResponseTime());
			}
		}
	}
	
	//HJQ_ 从serviceMap1 恢复到 serviceMap
	public void recoveryForHJQ(Map<String, WebService> RPT1OfCopy){
		//HJQ_ 从serviceMap1 恢复到 serviceMap
		Set<String> key = serviceMap.keySet();
		String s;
        for (Iterator it = key.iterator(); it.hasNext();) {
            s = (String) it.next();           
            WebService ws = serviceMap.get(s);
            
            WebService origin = serviceMap1.get(s);
           
            ws.setAllResponseTime(origin.getAllResponseTime());
            ws.setCount(origin.getCount());
            ws.setCriticalParent(origin.getCriticalParent());
            ws.setInputs(origin.getInputs());
            ws.setName(origin.getName());
            ws.setNewAllResponseTime(origin.getNewAllResponseTime());
            ws.setOutputs(origin.getOutputs());
            ws.setSelfResponseTime(origin.getSelfResponseTime());
            ws.setSelfThroughput(origin.getSelfThroughput());

        }	
        
        this.RPT1.clear();
        for(String inst: RPT1OfCopy.keySet()){
        	this.RPT1.put(inst, serviceMap.get(RPT1OfCopy.get(inst).getName()));
        }
	}
	
	/**
	 * 新增删除的实验D的每组实验
	 * @param testServiceList
	 * @param OTOfCopy
	 * @param IITOfCopy
	 * @param RPT1OfCopy
	 * @param solveMapOfCopy 
	 * @param allInsts 
	 * @param sb 
	 */
	public void randomTest1ForHJQ(List<WebService> testServiceList, Map<String, List<WebService>> OTOfCopy, Map<String, List<WebService>> IITOfCopy,
			Map<String, WebService> RPT1OfCopy, Map<String, List<String>> solveMapOfCopy, List<String> allInsts, StringBuilder sb) {
		
		this.realSum = 0;
		
		// HJQ_所有变化的服务
		List<WebService> qosChangedList = new ArrayList<WebService>();
		// HJQ_qosChangedList的副本,即qos发生变化的服务
		List<WebService> copyChangedList = new ArrayList<WebService>();
		
		Change change = new Change(testServiceList, allInsts, allInsts, 
				(int) StatInfo.instAverage(testServiceList, "input"),
				(int) StatInfo.instAverage(testServiceList, "output"), 30);
		
		// 比例
		int typeNumber = 5;
		//随机产生变化服务数,<1000
		
		Random random = new Random();
		
		int sum = random.nextInt(1000);

		// 1:1:3
		int deletedWSSum = sum / typeNumber;
		int addWSSum = sum / typeNumber;
		int changedWSSum = changedWSSum = (sum % typeNumber == 0) ? (sum / typeNumber * 3) : (sum / typeNumber * 3) + sum % typeNumber;

		Map<String, List<WebService>> changedMap
		= change.changeWebServices(sum, addWSSum, changedWSSum,
				deletedWSSum);
		
//		//用于重新读取以前的变化情况
//		if(this.runTimes >= this.count){
//			this.fileNum ++;
//			this.runTimes = 0;
//		}
//		
//		try {
//			changedMap = DataGetter.readChanged(serviceMap, this.fileNum, this.runTimes);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		runTimes ++;
//		List<WS2NextWSNumBean> tempRanks = new ArrayList<WS2NextWSNumBean>();;
//		
		// 处理copyChangedList
		for (WebService webService : changedMap.get("qosChange")) {
			WebService newWS = null;
			try {
				newWS = (WebService) webService.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			copyChangedList.add(newWS);
		}
		
		//复制发生变化的服务
		Map<String, List<WebService>> changedMapOfCopy = copyChangedMap(changedMap);		
		
		//记录当前实验的网络结构
		for(String key: changedMap.keySet()){
			List<WebService> webServices = changedMap.get(key);
			
			for(WebService webService: webServices){
				if(key.equals("delete")){
					sb.append(key + "," + webService.getName() + System.lineSeparator());
					
					//统计变化服务的博几点排名
//					WS2NextWSNumBean ws2NextWSNumBean = new WS2NextWSNumBean();
//					ws2NextWSNumBean.setWsName(webService.getName());
//					ws2NextWSNumBean.setNextWSNum(rankMap.get(webService.getName()));
//					tempRanks.add(ws2NextWSNumBean);
				}
				else if(key.equals("qosChange")){
					sb.append(key + "," + webService.getName() + "," + webService.getSelfResponseTime() + System.lineSeparator());
				
//					//统计变化服务的博几点排名
//					WS2NextWSNumBean ws2NextWSNumBean = new WS2NextWSNumBean();
//					ws2NextWSNumBean.setWsName(webService.getName());
//					ws2NextWSNumBean.setNextWSNum(rankMap.get(webService.getName()));
//					tempRanks.add(ws2NextWSNumBean);
				}
				else{
					sb.append(key + "," + webService.getInfo() + System.lineSeparator());
				}
			}
		}
		
		int deletedRealSum = 0;
		int newRealSum = 0;
		int qosRealSum = 0;
		
		//统计实际的数量
		for(WebService webService : changedMap.get("qosChange")){
			if(webService.getCount() == 0){
				this.realSum ++;
				qosRealSum++;
			}
		}
		
		for(WebService webService : changedMap.get("delete")){
			if(webService.getCount() == 0){
				this.realSum ++;
				deletedRealSum++;
			}
		}
		
		for(WebService webService : changedMap.get("new")){
			//默认是实际的
			boolean flag = true;
			
			for(String input : (List<String>)webService.getInputs()){
				if(!RPT1OfCopy.containsKey(input)){
					flag = false;
					break;
				}
			}
			
			if(flag){
				this.realSum++;
				newRealSum++;
			}
			
		}
		
		// ---------------------------------------------------------------
		int groupNum = 100;
		int excludedNum = 10;
		
		double totalTime = 0;
		long startMili = 0;
		long endMili = 0;
		List<Double> timeList = new ArrayList<Double>();
		double t1;// 连续查询的时间
		double t2;// 重查的时间
		
//		//统计长度
//		statisticWSToEndWSLengthMap();
		
		/*------------连续查询--------------------------*/
		
		for (int i = 0; i < groupNum; i++) {// 测出10组
			//初始化PQMaxSize
			this.PQMaxSize = 0;
			
			// 还RPT1和serviceMap1
			recoveryForHJQ(RPT1OfCopy);//ServiceMap所有服务设为最初值,RPT1也是
			recoveryWithNewQosForHJQ(copyChangedList);//改变预定服务的qos值,因为上一行代码置为最初值
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			recoveryIITForCJH(IITOfCopy);
			recoveryOTForHJQ(OTOfCopy);
			
			qosChangedList.clear();
			
			startMili = System.nanoTime();// 当前时间对应的毫秒数
			
			//新增删除的预处理
			// 添加删除
			qosChangedList.addAll(adaptDeleteWS(addAndDeleteMap.get("delete")));
			
			// 添加新增
			qosChangedList.addAll(adaptInsertWS(addAndDeleteMap.get("new")));
			
			// 添加qos改变
			qosChangedList.addAll(changedMap.get("qosChange"));
			
			continuousQuery4(qosChangedList);
			endMili = System.nanoTime();
			timeList.add(new Double((endMili - startMili)/1E6));
			
			if(this.dead){
				return;
			}
			
		}

		sortDoubleList(timeList);
		for (int i=(excludedNum != 1 ?(excludedNum - 1) : 1);i<groupNum - excludedNum;i++) {
			totalTime = totalTime + timeList.get(i);
		}

		t1 = totalTime / (groupNum - 2 * excludedNum);
				
		/*------------重查--------------------------*/
		
		totalTime = 0;
		timeList.clear();
		
		for (int i = 0; i < groupNum; i++) {
			recoveryReQueryForHJQ();
			recoveryIITForCJH(IITOfCopy);
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			
			startMili = System.nanoTime();// 当前时间对应的毫秒数
			adaptIIT(addAndDeleteMap);
			reQuery();
			endMili = System.nanoTime();
			timeList.add(new Double((endMili - startMili)/1E6));
		}
		
//		//得出前三排名
//		Collections.sort(tempRanks);
//		int size = tempRanks.size();
//		int end = size - 1;
//		int start = (size - 3) >= 0? size - 3: 0;
//		
//		ExperimentRankBean experimentRankBean = new ExperimentRankBean();
//		for(int i = end; i >= start; i--){
//			experimentRankBean.getWs2NextWSNumBeans().add(tempRanks.get(i));
//		}
//		experimentRankBean.setPQ(PQMaxSize);
//		
//		//统计变化服务出现在前20的位置
//		int count = 0;
//		
//		for(WS2NextWSNumBean bean : tempRanks){
//			if(bean.getNextWSNum() != null 
//					&& bean.getNextWSNum() >= 
//					ws2NextWSNumBeans.get(20).getNextWSNum()){
//				count++;
//			}
//		}
//		experimentRankBean.setRank20Num(count);
//		System.out.println(experimentRankBean);
//		this.tempExperimentRank.add(experimentRankBean);
//		
//		//统计宽度
//		statisticOutputs(changedMap);
//		System.out.println("global dynamic ws num:" + sum);
//		System.out.println("actual dynamic ws num:" + realSum);
//		System.out.println(PQMaxSize);
//		
//		PrintStatisticResult.getInstance().print(sum + ",");
//		PrintStatisticResult.getInstance().print(realSum + ",");
//		PrintStatisticResult.getInstance().print(PQMaxSize + System.lineSeparator());
		
		
		sortDoubleList(timeList);
		for (int i=(excludedNum != 1 ?(excludedNum - 1) : 1);i<groupNum-excludedNum;i++) {
			totalTime = totalTime + timeList.get(i);
		}

		t2 = totalTime /(groupNum - 2 * excludedNum);
		
		//统计重查与连续查询时间
		this.requeryTime += t2;
		this.continueTime += t1;
		
		//统计最佳策略时间
		this.bestStrategyTime += ((t1>t2)? t2: t1);
		
		double strategy1Time = -1;// 记录策略1采用什么算法
		double strategy2Time = -1;// 记录策略2采用什么算法
		String result = "";// 记录策略1和策略2的对比结果
		// HJQ 实验D,统计策略1和策略2的胜率及平率
		// 策略1是以实际,策略2是以全局
		if ((realSum <= this.strategy1ValueWithAD && sum <= this.strategy2ValueWithAD)) {
			this.equalCount++;

			strategy1Time = t1;
			strategy2Time = t1;
			
			//统计策略时间
			this.strategy1Time += t1;
			this.strategy2Time += t1;

			result = "平" + System.getProperty("line.separator");
		} else if (realSum > this.strategy1ValueWithAD && sum > this.strategy2ValueWithAD) {
			this.equalCount++;

			strategy1Time = t2;
			strategy2Time = t2;
			
			//统计策略时间
			this.strategy1Time += t2;
			this.strategy2Time += t2;

			result = "平" + System.getProperty("line.separator");
		} else if ((realSum <= this.strategy1ValueWithAD && sum > this.strategy2ValueWithAD)) {
			strategy1Time = t1;
			strategy2Time = t2;
			
			//统计策略时间
			this.strategy1Time += t1;
			this.strategy2Time += t2;
			
			if (t1 < t2) {
				this.winCount++;
				// *System.out.println("策略1胜");
				result = "胜" + System.getProperty("line.separator");
			} else if (t1 == t2) {
				this.equalCount++;
				result = "平" + System.getProperty("line.separator");
			} else {
				// *System.out.println("策略1负");
				result = "负" + System.getProperty("line.separator");
			}
		} else if (realSum > this.strategy1ValueWithAD && sum <= this.strategy2ValueWithAD) {
			strategy1Time = t2;
			strategy2Time = t1;
			
			//统计策略时间
			this.strategy1Time += t2;
			this.strategy2Time += t1;
			
			if (t2 < t1) {
				this.winCount++;
				// *System.out.println("策略1胜");
				result = "胜" + System.getProperty("line.separator");
			} else if (t2 == t1) {
				this.equalCount++;
				result = "平" + System.getProperty("line.separator");
			} else {
				// *System.out.println("策略1负");
				result = "负" + System.getProperty("line.separator");
			}
		}

		// 输出相关信息至test.txt
		recordTime3.append(t2 + System.getProperty("line.separator"));
		recordTime4.append(t1 + System.getProperty("line.separator"));

		// 后台输出相关信息
		// *System.out.println("重查平均耗时为:"+t2+"毫秒");
		// *System.out.println("连续查询平均耗时为:"+t1+"毫秒");

		// 记录格式化后的实验D信息
		recordTime5.append(sum + ",");
		recordTime5.append(realSum + ",");
		recordTime5.append(deletedRealSum + ",");
		recordTime5.append(newRealSum + ",");
		recordTime5.append(qosRealSum + ",");
		recordTime5.append(PQMaxSize + ",");
		recordTime5.append(strategy1Time + ",");
		recordTime5.append(strategy2Time + ",");
		recordTime5.append(t2 + ",");
		recordTime5.append(t1 + ",");
		recordTime5.append(result);

		// *System.out.println("qos改变了的服务个数："+sum);
		// *System.out.println("实际需要更新的ws(count==0):的个数："+realSum);
		// *System.out.println("PQ队列的最大值："+PQMaxSize);
	}
	/**
	 * 实验D跑count组实验
	 * @param solveMapOfCopy
	 * @param OTOfCopy
	 * @param RPT1OfCopy
	 * @param realInsts 
	 * @param i
	 * @throws IOException
	 */
	public void testManyTime1ForHJQ(Map<String, List<String>> solveMapOfCopy, Map<String, List<WebService>> OTOfCopy, Map<String, List<WebService>> IITOfCopy,
			Map<String, WebService> RPT1OfCopy, List<String> allInsts, int i) throws IOException{
		//HJQ 统计胜率,平率
		this.winCount = 0;
		this.equalCount = 0;

		// 统计时间初始化
		this.requeryTime = 0;
		this.continueTime = 0;
		this.strategy1Time = 0;
		this.strategy2Time = 0;
		this.bestStrategyTime = 0;

		int time = 0;
		//记录每组实验信息
		StringBuilder sb = new StringBuilder();
		
		// 根据实验内容来选择改变策略,即存放着全部服务还是仅仅是实际网络结构中的服务
		List<WebService> testServiceList = new ArrayList<WebService>();

		while (time < count) {
			this.dead = false;
			time++;
			if (time % 5 == 0) {
				System.out.println("第" + time + "组");
			}
			if (time == 1) {
				recordTime1.append("Stratege1" + System.getProperty("line.separator"));
				recordTime2.append("Stratege2" + System.getProperty("line.separator"));
				recordTime3.append("requery" + System.getProperty("line.separator"));
				recordTime4.append("continuousQuery" + System.getProperty("line.separator"));
				recordTime5.append("变化服务个数,"
						+ "实际需要更新的ws的个数,"
						+ "实际删除的个数,"
						+ "实际新增的个数,"
						+ "实际qos变化的个数,"
						+ "PQ队列的最大长度,"
						+ "策略1(" + this.strategy1ValueWithAD
						+ "),"
						+ "策略2(" + this.strategy2ValueWithAD + "),"
						+ "重查,"
						+ "连续查询,"
						+ "结果"
						+ System.getProperty("line.separator"));
			}

			// 恢复testServiceList,随机产生变化服务需要
			testServiceList.clear();

			for (String key : serviceMap.keySet()) {
				WebService webService = serviceMap.get(key);

				String name = webService.getName();

				if (!name.equals("Provide") && !name.equals("Request")) {
					testServiceList.add(webService);
				}
			}

			// System.out.println("第"+time+"组");
			sb.append("第" + time + "组" + System.lineSeparator());
			randomTest1ForHJQ(testServiceList, OTOfCopy, IITOfCopy, RPT1OfCopy, solveMapOfCopy, allInsts, sb);
			sb.append(System.lineSeparator());
			
			if(this.dead){
				time--;
			}
			
		}

		// 记录平均时间
		recordTime5.append("重查平均时间,连续查询平均时间,策略1平均时间,策略2平均时间,最佳策略平均时间" + System.lineSeparator());
		recordTime5.append(this.requeryTime / this.count + ",");
		recordTime5.append(this.continueTime / this.count + ",");
		recordTime5.append(this.strategy1Time / this.count + ",");
		recordTime5.append(this.strategy2Time / this.count + ",");
		recordTime5.append(this.bestStrategyTime / this.count + System.lineSeparator());

		// 记录对比结果
		recordTime5.append("策略1胜于策略2:" + this.winCount + System.getProperty("line.separator"));
		recordTime5.append("策略1平于策略2:" + this.equalCount + System.getProperty("line.separator"));
		recordTime5.append("策略1负于策略2:" + (this.count - this.winCount - this.equalCount) + System.getProperty("line.separator"));
		
		PrintWriter pw = new PrintWriter(new File("result\\" + "changedServices" + i + ".csv"));
		
		pw.write(sb.toString());
		
		pw.close();
		
		buff=new byte[]{};
		out1=new FileOutputStream("result\\" + i + ".csv");
		
		
        buff=recordTime5.toString().getBytes();        
        out1.write(buff,0,buff.length);
		out1.close();
		
		//策略1是以实际,策略2是以全局
		System.out.println("策略1胜于策略2:" + this.winCount);
		System.out.println("策略1平于策略2:" + this.equalCount);
		System.out.println("策略1负于策略2:" + (this.count - this.winCount - this.equalCount));
	}
	/**
	 * 跑allCount次新增删除的实验D,每次实验若是胜>负就算本次实验胜
	 * @throws IOException
	 */
	public void testManyTime1ForWinHJQ() throws IOException{
		
		PrintWriter pw = new PrintWriter(new File("result\\" + "RPT.csv"));
		
		for(String key: RPT.keySet()){
			pw.println(key + "," + RPT.get(key).getInfo());
		}
		
		pw.close();

		// HJQ,复制IIT
		Map<String, List<WebService>> IITOfCopy = null;

		try {
			IITOfCopy = DataGetter.copyStrToWsList(IIT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// 复制solveMap
		Map<String, List<String>> solveMapOfCopy = DataGetter.copyStrToStrList(solveMap);

		// HJQ,复制OT
		Map<String, List<WebService>> OTOfCopy = null;

		// HJQ,复制PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);

		try {
			OTOfCopy = DataGetter.copyStrToWsList(OT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		//copy RPT1->RPT2 serviceMap1->serviceMap2
		copy();
		
		//所有inst
		List<String > allInsts = new ArrayList<String>();
		for(String instAndCon: map1.keySet()){
			if(instAndCon.contains("inst")){
				allInsts.add(instAndCon);
			}
		}
		
		//相当于启动100次程序
		this.allCount = 100;
		StringBuilder sb = new StringBuilder();
		
		sb.append("实验序号,实验结果,重查平均时间,连续查询平均时间,策略1平均时间,策略2平均时间,最佳策略平均时间" + System.lineSeparator());
		
		for(int i = 1; i <= allCount; i++){
			recordTime5 = new StringBuilder();
			System.out.println("第" + i + "次");

			testManyTime1ForHJQ(solveMapOfCopy, OTOfCopy, IITOfCopy, RPT1OfCopy, allInsts, i);
			
			long failCount = this.count - this.winCount - this.equalCount;
			
			if(this.winCount > failCount){
				this.allWinCount++;
				sb.append(i + ",胜,");
			}
			else if(this.winCount == failCount){
				this.allEqualCount++;
				sb.append(i + ",平,");
			}
			else{
				sb.append(i + ",负,");
			}
			
			sb.append(this.requeryTime / this.count + ",");
			sb.append(this.continueTime / this.count + ",");
			sb.append(this.strategy1Time / this.count + ",");
			sb.append(this.strategy2Time / this.count + ",");
			sb.append(this.bestStrategyTime / this.count + System.lineSeparator());
		}
		
		System.out.println("总胜率:" + this.allWinCount);
		System.out.println("总平率:" + this.allEqualCount);
		System.out.println("总负率:" + (this.allCount - this.allWinCount - this.allEqualCount));
		
		sb.append("总胜率:" + this.allWinCount + System.lineSeparator());
		sb.append("总平率:" + this.allEqualCount + System.lineSeparator());
		sb.append("总负率:" + (this.allCount - this.allWinCount - this.allEqualCount) + System.lineSeparator());
		
		pw = new PrintWriter(new File("result\\allresult.csv"));
		pw.write(sb.toString());
		pw.close();
		
	}

	/**
	 * 仅有qos变化的实验D跑count组实验
	 * @param solveMapOfCopy
	 * @param OTOfCopy
	 * @param RPT1OfCopy
	 * @param i
	 * @throws IOException
	 */
	public void testManyTime1(Map<String, List<String>> solveMapOfCopy, Map<String, List<WebService>> OTOfCopy, Map<String, WebService> RPT1OfCopy, int i) throws IOException{
		//HJQ 统计胜率,平率
		this.winCount = 0;
		this.equalCount = 0;
		//统计时间初始化
		this.requeryTime = 0;
		this.continueTime = 0;
		this.strategy1Time = 0;
		this.strategy2Time = 0;
		this.bestStrategyTime = 0;
		
		int time = 0;
		
		//初始化recordTime5
		recordTime5 = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		
		while(time<count){
			time++;
			if (time % 5 == 0) {
				System.out.println("第" + time + "组");
			}
			if(time==1){
				recordTime1.append("Stratege1"+System.getProperty("line.separator"));
				recordTime2.append("Stratege2"+System.getProperty("line.separator"));
				recordTime3.append("requery"+System.getProperty("line.separator"));	
				recordTime4.append("continuousQuery"+System.getProperty("line.separator"));
				recordTime5.append("qos改变了的服务个数,"
						+ "实际需要更新的ws(count==0):的个数,"
						+ "PQ队列的最大长度,"
						+ "策略1(" + this.strategy1Value + "),"
						+ "策略2(" + this.strategy2Value + "),"
						+ "重查,"
						+ "连续查询,"
						+ "结果"+System.getProperty("line.separator"));
			}
			
			//System.out.println("第"+time+"组");
			sb.append("第"+time+"组" + System.lineSeparator());
			randomTest1(solveMapOfCopy, OTOfCopy, RPT1OfCopy, sb);
			sb.append(System.lineSeparator());
			
		}
		
		//记录平均时间
		recordTime5.append("重查平均时间,连续查询平均时间,策略1平均时间,策略2平均时间,最佳策略平均时间" + System.lineSeparator());
		recordTime5.append(this.requeryTime / this.count + ",");
		recordTime5.append(this.continueTime / this.count + ",");
		recordTime5.append(this.strategy1Time / this.count + ",");
		recordTime5.append(this.strategy2Time / this.count + ",");
		recordTime5.append(this.bestStrategyTime / this.count + System.lineSeparator());
		
		//记录对比结果
		recordTime5.append("策略1胜于策略2:" + this.winCount + System.getProperty("line.separator"));
		recordTime5.append("策略1平于策略2:" + this.equalCount + System.getProperty("line.separator"));
		recordTime5.append("策略1负于策略2:" + (this.count - this.winCount - this.equalCount) + System.getProperty("line.separator"));
		
		PrintWriter pw = new PrintWriter(new File("result\\" + "changedServices" + i + ".csv"));
		
		pw.write(sb.toString());
		
		pw.close();
		
		buff=new byte[]{};
		out1=new FileOutputStream("result\\" + i + ".csv");
		
		/*		                              
        buff=recordTime1.toString().getBytes();        
        out1.write(buff,0,buff.length);
        buff=recordTime2.toString().getBytes();        
        out1.write(buff,0,buff.length);
        buff=recordTime3.toString().getBytes();        
        out1.write(buff,0,buff.length);
        buff=recordTime4.toString().getBytes();        
        out1.write(buff,0,buff.length);*/
        buff=recordTime5.toString().getBytes();        
        out1.write(buff,0,buff.length);
		out1.close();
		
		//策略1是以实际,策略2是以全局
		System.out.println("策略1胜于策略2:" + this.winCount);
		System.out.println("策略1平于策略2:" + this.equalCount);
		System.out.println("策略1负于策略2:" + (this.count - this.winCount - this.equalCount));
	}
	/**
	 * 跑allCount次仅有qos变化的实验D,每次实验若是胜>负就算本次实验胜
	 * @throws IOException
	 */
	public void testManyTime1ForWin() throws IOException{
		
		PrintWriter pw = new PrintWriter(new File("result\\" + "RPT.csv"));
		
		for(String key: RPT.keySet()){
			pw.println(key + "," + RPT.get(key).getInfo());
		}
		
		pw.close();
		
		// 复制solveMap
		Map<String, List<String>> solveMapOfCopy = DataGetter.copyStrToStrList(solveMap);

		// HJQ,复制OT
		Map<String, List<WebService>> OTOfCopy = null;

		// HJQ,复制PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);

		try {
			OTOfCopy = DataGetter.copyStrToWsList(OT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		//copy RPT1->RPT2 serviceMap1->serviceMap2
		copy();
		//相当于启动100次程序
		this.allCount = 100;
		StringBuilder sb = new StringBuilder();
		
		sb.append("实验序号,实验结果,重查平均时间,连续查询平均时间,策略1平均时间,策略2平均时间,最佳策略平均时间" + System.lineSeparator());
		
		for(int i = 1; i <= allCount; i++){
			System.out.println("第" + i + "次");
			testManyTime1(solveMapOfCopy, OTOfCopy, RPT1OfCopy, i);
			
			long failCount = this.count - this.winCount - this.equalCount;
			
			if(this.winCount > failCount){
				this.allWinCount++;
				sb.append(i + ",胜,");
			}
			else if(this.winCount == failCount){
				this.allEqualCount++;
				sb.append(i + ",平,");
			}
			else{
				sb.append(i + ",负,");
			}
			
			sb.append(this.requeryTime / this.count + ",");
			sb.append(this.continueTime / this.count + ",");
			sb.append(this.strategy1Time / this.count + ",");
			sb.append(this.strategy2Time / this.count + ",");
			sb.append(this.bestStrategyTime / this.count + System.lineSeparator());
			
		}
		
		System.out.println("总胜率:" + this.allWinCount);
		System.out.println("总平率:" + this.allEqualCount);
		System.out.println("总负率:" + (this.allCount - this.allWinCount - this.allEqualCount));
		
		sb.append("总胜率:" + this.allWinCount + System.lineSeparator());
		sb.append("总平率:" + this.allEqualCount + System.lineSeparator());
		sb.append("总负率:" + (this.allCount - this.allWinCount - this.allEqualCount) + System.lineSeparator());
		
		pw = new PrintWriter(new File("result\\allresult.csv"));
		pw.write(sb.toString());
		pw.close();
		
	}
	
	public void continuousQuery1(List<WebService> qosChangedList){
		List<WebService> PQ = new ArrayList<WebService>();
		Set webserviceSet = new HashSet();
		for(int i=0;i<qosChangedList.size();i++){
//			if(ws.getAllResponseTime()>0){
//				PQ.add(ws);
//			}
			PQ.add(qosChangedList.get(i));
		}
		WebService first;
		//System.out.println(ws.getNewAllResponseTime());
		while(!PQ.isEmpty()){
			sort(PQ);
//			System.out.println("***PQ****");
//			for(int i=0;i<PQ.size();i++){
//				double original = PQ.get(i).getAllResponseTime();
//				double newOne = PQ.get(i).getNewAllResponseTime();
//				double min =original>newOne?newOne:original;
//				System.out.println(PQ.get(i).getName()+"的min是:"+min);				
//				
//			}
//			System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
			
			
			
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	System.out.println("******到了Request******");
				continue;
			}
		    List<WebService> successorList;

		    if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//System.out.println("qos变小");
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					
//					if(first.getName().equals("serv1581459521")){//可以提供288的输入，我擦
//						for(int ii=0;ii<matchList.size();ii++){
//							System.out.print(matchList.get(ii)+" ");
//						}
//						System.out.println();
//					}
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						/*
						//判断是否需要更新RPT1
						WebService parent = RPT1.get(inst);
						if(parent==null){
							System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
						
						if(parent!=first){
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"取代"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"新的父亲是:"+first.getName());
								//System.out.println("****RPT1.size"+RPT1.size());
							}
						}
						*/
						////
						List<WebService> canOutputInstList = OT.get(inst);
						WebService newParent = null;
						for(int u=0;u<canOutputInstList.size();u++){
							WebService ww = canOutputInstList.get(u);
							if(newParent==null) 
								newParent = ww;
							else{								
								double newParentQos = newParent.getNewAllResponseTime()>0?newParent.getNewAllResponseTime():newParent.getAllResponseTime();
								double wwQos = ww.getNewAllResponseTime()>0?ww.getNewAllResponseTime():ww.getAllResponseTime();
								if(wwQos<newParentQos)
									newParent = ww;
							}
//							if(ww.getNewAllResponseTime()>0)
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getNewAllResponseTime());
//							else
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getAllResponseTime());
						}
						RPT1.put(inst, newParent);
						////
						
						
						
						
					    successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//判断是否需要更新/变优
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy有问题");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk有问题");
										//如果认识没错，数据集有缺陷
										continue;
									}
//									if(successor.getName().equals("serv1512027288"))
//										System.out.println("parent:"+first.getName());
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}
									}
								}
							}
						}
						
					}
				}
				
				
				
			}else{
				//System.out.println("qos变大");
				//first.setAllResponseTime(999999);
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);						
						
						List<WebService> canOutputInstList = OT.get(inst);
						WebService newParent = null;
						for(int u=0;u<canOutputInstList.size();u++){
							WebService ww = canOutputInstList.get(u);
							if(newParent==null) 
								newParent = ww;
							else{								
								double newParentQos = newParent.getNewAllResponseTime()>0?newParent.getNewAllResponseTime():newParent.getAllResponseTime();
								double wwQos = ww.getNewAllResponseTime()>0?ww.getNewAllResponseTime():ww.getAllResponseTime();
								if(wwQos<newParentQos)
									newParent = ww;
							}
//							if(ww.getNewAllResponseTime()>0)
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getNewAllResponseTime());
//							else
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getAllResponseTime());
						}
						if(newParent==first){
//							if(newParent.getNewAllResponseTime()>0)
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getAllResponseTime());
							//System.out.println(inst);
						}
						RPT1.put(inst, newParent);
						
						
						successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//判断是否需要更新/变优
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy有问题");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk数据集有问题");
										//如果认识没错，数据集有缺陷
										continue;
									}
//									if(successor.getName().equals("serv1512027288"))
//										System.out.println("parent:"+first.getName());
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}
									}
								}
							}
						}
						
					}
					
				}
				
				
			}
			first.setAllResponseTime(first.getNewAllResponseTime());;
		}
		
		System.out.print("continuous最优解是：");
		if(serviceMap.get("Request").getNewAllResponseTime()>0){
			System.out.println(serviceMap.get("Request").getNewAllResponseTime());
			continousQos=serviceMap.get("Request").getNewAllResponseTime();
		}
		else{
			System.out.println(serviceMap.get("Request").getAllResponseTime());
			continousQos=serviceMap.get("Request").getAllResponseTime();
		}
		//System.out.println("****RPT1.size"+RPT1.size());
		System.out.println("reQuery()");		
		reQuery();
	}
	
	public void continuousQuery2(WebService w){
		List<WebService> PQ = new ArrayList<WebService>();
		Set webserviceSet = new HashSet();
		PQ.add(w);
		WebService first;
		//System.out.println(ws.getNewAllResponseTime());
		while(!PQ.isEmpty()){
			sort(PQ);
//			System.out.println("***PQ****");
//			for(int i=0;i<PQ.size();i++){
//				double original = PQ.get(i).getAllResponseTime();
//				double newOne = PQ.get(i).getNewAllResponseTime();
//				double min =original>newOne?newOne:original;
//				System.out.println(PQ.get(i).getName()+"的min是:"+min);				
//				
//			}
//			System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
			
			
			
		    if(first.getName().equals("Request")){
				//first.setAllResponseTime(first.getNewAllResponseTime());
		    	//System.out.println("******到了Request******");
		    	first.setAllResponseTime(first.getNewAllResponseTime());
				continue;
			}
		    List<WebService> successorList;
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//System.out.println("qos变小");
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					
//					if(first.getName().equals("serv1581459521")){//可以提供288的输入，我擦
//						for(int ii=0;ii<matchList.size();ii++){
//							System.out.print(matchList.get(ii)+" ");
//						}
//						System.out.println();
//					}
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						//判断是否需要更新RPT1
						WebService parent = RPT1.get(inst);
						if(parent==null){
							System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
						
						if(parent!=first){
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"取代"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"新的父亲是:"+first.getName());
								//System.out.println("****RPT1.size"+RPT1.size());
							}
						}
						
					    
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//判断是否需要更新/变优
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy有问题");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk有问题");
										//如果认识没错，数据集有缺陷
										continue;
									}
//									if(successor.getName().equals("serv1512027288"))
//										System.out.println("parent:"+first.getName());
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}
									}
								}
							}
						}
						
					}
				}
				
				
				
			}else{
				//System.out.println("qos变大");
				//first.setAllResponseTime(999999);
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);		
						
						successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						
						
						List<WebService> canOutputInstList = OT.get(inst);
						WebService newParent = null;
						for(int u=0;u<canOutputInstList.size();u++){
							WebService ww = canOutputInstList.get(u);
							if(newParent==null) 
								newParent = ww;
							else{								
								double newParentQos = newParent.getNewAllResponseTime()>0?newParent.getNewAllResponseTime():newParent.getAllResponseTime();
								double wwQos = ww.getNewAllResponseTime()>0?ww.getNewAllResponseTime():ww.getAllResponseTime();
								if(wwQos<newParentQos)
									newParent = ww;
							}
//							if(ww.getNewAllResponseTime()>0)
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getNewAllResponseTime());
//							else
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getAllResponseTime());
						}
						if(newParent==first){
//							if(newParent.getNewAllResponseTime()>0)
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getAllResponseTime());
							//System.out.println(inst);
						}
						RPT1.put(inst, newParent);
						
						
						
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//判断是否需要更新/变优
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy有问题");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk数据集有问题");
										//如果认识没错，数据集有缺陷
										continue;
									}
//									if(successor.getName().equals("serv1512027288"))
//										System.out.println("parent:"+first.getName());
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}
									}
								}
							}
						}
						
					}
					
				}
				
				
			}
			first.setAllResponseTime(first.getNewAllResponseTime());
		}
		
		//System.out.print("continuous最优解是：");
		if(serviceMap.get("Request").getNewAllResponseTime()>0){
			//System.out.println(serviceMap.get("Request").getNewAllResponseTime());
			continousQos=serviceMap.get("Request").getNewAllResponseTime();
		}
		else{
			//System.out.println(serviceMap.get("Request").getAllResponseTime());
			continousQos=serviceMap.get("Request").getAllResponseTime();
		}
//		//System.out.println("****RPT1.size"+RPT1.size());
//		System.out.println("reQuery()");		
//		reQuery();
	}
	
	public void continuousQuery3(List<WebService> qosChangedList){//else改变了，加了正无穷
		List<WebService> PQ = new ArrayList<WebService>();
		Set webserviceSet = new HashSet();
		for(int i=0;i<qosChangedList.size();i++){
			PQ.add(qosChangedList.get(i));
		}
		WebService first;
		
		////////
		recordSomething="";
		///////
		
		while(!PQ.isEmpty()){
			sort(PQ);
			//System.out.println("***PQ****");
//			recordSomething=recordSomething+"***PQ****"+"\n";
//			for(int i=0;i<PQ.size();i++){
//				double original = PQ.get(i).getAllResponseTime();
//				double newOne = PQ.get(i).getNewAllResponseTime();
//				double min =original>newOne?newOne:original;
//				//System.out.println(PQ.get(i).getName()+"的min是:"+min);				
//				recordSomething=recordSomething+PQ.get(i).getName()+"的min是:"+min+"\n";
//			}
//			recordSomething=recordSomething+"******"+"\n";
			//System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
									
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	System.out.println("******到了Request******");
				continue;
			}
		    List<WebService> successorList;
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//System.out.println("qos变小");
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						/*
						//判断是否需要更新RPT1
						WebService parent = RPT1.get(inst);
						if(parent==null){
							System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
						
						if(parent!=first){
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"取代"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"新的父亲是:"+first.getName());
								//System.out.println("****RPT1.size"+RPT1.size());
							}
						}
						*/
						////
						List<WebService> canOutputInstList = OT.get(inst);
						WebService newParent = null;
						for(int u=0;u<canOutputInstList.size();u++){
							WebService ww = canOutputInstList.get(u);
							if(newParent==null) 
								newParent = ww;
							else{								
								double newParentQos = newParent.getNewAllResponseTime()>0?newParent.getNewAllResponseTime():newParent.getAllResponseTime();
								double wwQos = ww.getNewAllResponseTime()>0?ww.getNewAllResponseTime():ww.getAllResponseTime();
								if(wwQos<newParentQos)
									newParent = ww;
							}
//							if(ww.getNewAllResponseTime()>0)
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getNewAllResponseTime());
//							else
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getAllResponseTime());
						}
						RPT1.put(inst, newParent);
						////
							
					    successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//判断是否需要更新/变优
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy有问题");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());										
									}
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk有问题");
										//如果认识没错，数据集有缺陷
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											PQ.add(successor);										
										}
										continue;
									}

									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}
									}
								}
							}
						}
						
					}
				}
				
				first.setAllResponseTime(first.getNewAllResponseTime());
				
			}else{
				//System.out.println("qos变大");
				//first.setAllResponseTime(999999);
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);						
						
						List<WebService> canOutputInstList = OT.get(inst);
						WebService newParent = null;
						for(int u=0;u<canOutputInstList.size();u++){
							WebService ww = canOutputInstList.get(u);
							if(newParent==null) 
								newParent = ww;
							else{								
								double newParentQos = newParent.getNewAllResponseTime()>0?newParent.getNewAllResponseTime():newParent.getAllResponseTime();
								double wwQos = ww.getNewAllResponseTime()>0?ww.getNewAllResponseTime():ww.getAllResponseTime();
								if(wwQos<newParentQos)
									newParent = ww;
							}
//							if(ww.getNewAllResponseTime()>0)
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getNewAllResponseTime());
//							else
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getAllResponseTime());
						}
						if(newParent==first){//最优父亲还是自己
//							if(newParent.getNewAllResponseTime()>0)
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getAllResponseTime());
							//System.out.println(inst);
						}
						RPT1.put(inst, newParent);
						
						
						successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//判断是否需要更新/变优
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy有问题");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
									}
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk有问题");
										//如果认识没错，数据集有缺陷
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){//这里和上面加了这句
									//		recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											PQ.add(successor);										
										}
										continue;
									}
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}
									}
								}
							}
						}
						
					}
					
					
				}
				first.setAllResponseTime(first.getNewAllResponseTime());
				//PQ.add(first);
			}
			
		}
		
		System.out.print("continuous最优解是：");
		if(serviceMap.get("Request").getNewAllResponseTime()>0){
			System.out.println(serviceMap.get("Request").getNewAllResponseTime());
			continousQos=serviceMap.get("Request").getNewAllResponseTime();
		}
		else{
			System.out.println(serviceMap.get("Request").getAllResponseTime());
			continousQos=serviceMap.get("Request").getAllResponseTime();
		}
		//System.out.println("****RPT1.size"+RPT1.size());
		//System.out.println("Request的newAllqos"+serviceMap.get("Request").getNewAllResponseTime());
		//System.out.println("Request的allqos"+serviceMap.get("Request").getAllResponseTime());
		//System.out.println("dd"+getCriticalParent(serviceMap.get("Request")).getAllResponseTime());
		
		//System.out.println("continuousQuery的backward:");
		//backwardForRPT1(outputsFromChallenge,RPT1,serviceMap);
		recordBackWardForRPT1();
		System.out.println("reQuery()");		
		reQuery();
	}
	
	public void continuousQuery4(List<WebService> qosChangedList){//这个方法不更新没人需要的inst的最优parent
		
		List<WebService> PQ = new ArrayList<WebService>();
		//用于画图
		List<WebService> PQOfCopy = new ArrayList<WebService>();
		
		for(int i=0;i<qosChangedList.size();i++){
			if(qosChangedList.get(i).getCount()!=0) continue;
			PQ.add(qosChangedList.get(i));
		}
		WebService first;
				
		while(!PQ.isEmpty()){
			PQMaxSize++;
			if(PQMaxSize > 250){
				this.dead = true;
				return;
			}
			
					//HJQ_根据MIN(allqos,newallqos)大小排序
			sort(PQ);
//			System.out.println("***PQ****");
//			//recordSomething=recordSomething+"***PQ****"+"\n";
//			for(int i=0;i<PQ.size();i++){
//				double original = PQ.get(i).getAllResponseTime();
//				double newOne = PQ.get(i).getNewAllResponseTime();
//				double min =original>newOne?newOne:original;
//				(PQ.get(i).getName()+"的min是:"+min);				
//				//recordSomething=recordSomething+PQ.get(i).getName()+"的min是:"+min+"\n";
//			}
//			recordSomething=recordSomething+"******"+"\n";
//			System.out.println("*********");
			
		    first = PQ.get(0);
		    
			//用于画图
		    PQOfCopy.add(first);
		    
			PQ.remove(0);
						//HJQ_若最后仅仅剩下Request服务(我认为是Response),直接改变allqos,即完成连续查询						
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	//System.out.println("******到了Request******");		    					
				continue;
		    	//break;
		    }
		    
		    List<WebService> successorList;
		    			//HJQ_qos变小
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				
				//System.out.println("qos变小");
						//HJQ_
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
						//HJQ_找到可匹配服务列
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					
							//HJQ_首先,改变了qos的服务能提供的matchinsts,对于每一个inst属于matchinsts,若该服务更优
							//HJQ_改变为又该服务提供该inst.然后,
							//HJQ_对于每一个服务inputs含有inst的服务,根据一定规则更新newallqos
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						//不更新没人要的inst的父亲
						if(IIT.get(inst)==null) continue;
						
						//判断是否需要更新RPT1
									//HJQ_原来的就是最优???????????????
						WebService parent = RPT1.get(inst);
						if(parent==null){
							//System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
								//HJQ_parent与first同样可以提供inst,
								//HJQ_不等于自身
						if(parent!=first){
								//HJQ_newallqos大于0,表示服务改变了
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
								//HJQ_first更优
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"取代"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"新的父亲是:"+first.getName());
								//System.out.println("****RPT1.size"+RPT1.size());
							}
						}	
						////
						
						/*
						////
						List<WebService> canOutputInstList = OT.get(inst);
						WebService newParent = null;
						for(int u=0;u<canOutputInstList.size();u++){
							WebService ww = canOutputInstList.get(u);
							if(newParent==null) 
								newParent = ww;
							else{								
								double newParentQos = newParent.getNewAllResponseTime()>0?newParent.getNewAllResponseTime():newParent.getAllResponseTime();
								double wwQos = ww.getNewAllResponseTime()>0?ww.getNewAllResponseTime():ww.getAllResponseTime();
								if(wwQos<newParentQos)
									newParent = ww;
							}
//							if(ww.getNewAllResponseTime()>0)
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getNewAllResponseTime());
//							else
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getAllResponseTime());
						}
						RPT1.put(inst, newParent);
						////
						*/	
									//HJQ_从first_outputs可以找到的服务
					    successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						for(int u=0;u<successorList.size();u++){
										//HJQ_处理可达点
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);					
								//if(successor==null) System.out.println("kong");
												//HJQ_找出能够匹配successor的input的qos最好的服务
								WebService criticalParent = getCriticalParent(successor);
								
								//判断是否需要更新/变优
											//HJQ_最优父服务是否改变qos
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
											//HJQ_qos变化了的可达服务列包含了successor
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										//System.out.println("yy有问题");
											//HJQ_successor的newallqos设置为最优父服务的qos+自身qos
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());										
									}
								}else{
												//HJQ_上一服务first改变了,故下一服务successor变为改变qos
												//HJQ_qos变化了但不可达
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk有问题");
										//如果认识没错，数据集有缺陷
												//HJQ_successor的newallqos设置为最优父服务的qos+自身qos
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											
											PQ.add(successor);
										}
										continue;
									}
												//HJQ_successor的allqos设置为最优父服务的qos+自身qos
												//HJQ_qos没有变化了但不可达
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}	
									}
								}
							}
						}
						
					}
				}
				//HJQ_覆盖变化
				first.setAllResponseTime(first.getNewAllResponseTime());
				
			}else{
				//HJQ_qos变大
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						solveMap.put(output, matchList);
					}
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);						
						
						//不更新没人要的inst的父亲
						if(IIT.get(inst)==null) continue;
									//HJQ_变差了就要找最优?????????????????
						List<WebService> canOutputInstList = OT.get(inst);
						WebService newParent = null;
						
						for(int u=0;u<canOutputInstList.size();u++){
							WebService ww = canOutputInstList.get(u);
							
							if(newParent==null) 
								newParent = ww;
							else{								
								double newParentQos = newParent.getNewAllResponseTime()>0?newParent.getNewAllResponseTime():newParent.getAllResponseTime();
								double wwQos = ww.getNewAllResponseTime()>0?ww.getNewAllResponseTime():ww.getAllResponseTime();
								if(wwQos<newParentQos)
									newParent = ww;
							}
//							if(ww.getNewAllResponseTime()>0)
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getNewAllResponseTime());
//							else
//								System.out.println(ww.getName()+" "+ww.getCount()+" "+ww.getAllResponseTime());
						}
						if(newParent==first){//最优父亲还是自己
//							if(newParent.getNewAllResponseTime()>0)
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"的qos从"+first.getNewAllResponseTime()+"变为"+newParent.getAllResponseTime());
							//System.out.println(inst);
						}
						RPT1.put(inst, newParent);
						
						
						successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								//判断是否需要更新/变优
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										//System.out.println("yy有问题");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
									}
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk有问题");
										//如果认识没错，数据集有缺陷
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){//这里和上面加了这句
									//		recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											
											PQ.add(successor);									
										}
										continue;
									}
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
										
										PQ.add(successor);
//										if(successor.getName().equals("serv612685309") || successor.getName().equals("serv612685309")){
//											System.out.println("oooooo "+successor.getName()+" "+successor.getCount());
//										}
									}
								}
							}
						}
						
					}
					
					
				}
				first.setAllResponseTime(first.getNewAllResponseTime());
				
			}
			
		}
		
//		//用于画图
//		PrintNetwork.printPQ(PQOfCopy);
//		
//		PQOfCopy.removeAll(qosChangedList);
//		
//		PrintNetwork.printPQWithoutFirstChanged(PQOfCopy);
//		//*//
		
		//System.out.print("continuous最优解是：");
		if(serviceMap.get("Request").getNewAllResponseTime()>0){
			//System.out.println(serviceMap.get("Request").getNewAllResponseTime());
			continousQos=serviceMap.get("Request").getNewAllResponseTime();
		}
		else{
			//System.out.println(serviceMap.get("Request").getAllResponseTime());
			continousQos=serviceMap.get("Request").getAllResponseTime();
		}
		//System.out.println("****RPT1.size"+RPT1.size());
		//System.out.println("Request的newAllqos"+serviceMap.get("Request").getNewAllResponseTime());
		//System.out.println("Request的allqos"+serviceMap.get("Request").getAllResponseTime());
		//System.out.println("dd"+getCriticalParent(serviceMap.get("Request")).getAllResponseTime());
		
		//System.out.println("continuousQuery的backward:");
		//backwardForRPT1(outputsFromChallenge,RPT1,serviceMap);
		//recordBackWardForRPT1();
		//System.out.println("reQuery()");		
		//reQuery();
	}
	
	public void continuousQuery(WebService ws){
		List<WebService> PQ = new ArrayList<WebService>();
		if(ws.getAllResponseTime()>0){
			PQ.add(ws);
		}
		WebService first;
		while(!PQ.isEmpty()){
			sort(PQ);
			System.out.println("***PQ****");
			for(int i=0;i<PQ.size();i++){
				double original = PQ.get(i).getAllResponseTime();
				double newOne = PQ.get(i).getNewAllResponseTime();
				double min =original>newOne?newOne:original;
				System.out.println(PQ.get(i).getName()+"的min是:"+min);				
			}
			System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
		    if(first.getName().equals("Request")){
				//first.setAllResponseTime(first.getNewAllResponseTime());
		    	System.out.println("******到了Request******");
				continue;
			}
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//还不不明白下面的是否有必要
				//first.setAllResponseTime(first.getNewAllResponseTime());
				
				//判断是否是最优parent
				if(RPT.containsValue(first)){//这个方法有待测试----可以
					System.out.println(first.getName()+"曾是最优parent");
					for(int i=0;i<first.getOutputs().size();i++){
						//还要考虑output能解决的inst
						String output = first.getOutputs().get(i).toString();
						
						
						List<String> matchList = solveMap.get(output);//
						if(matchList==null){//可能为空，因为之前的算法导致这里可能为空							
							matchList = findInstances(output,map1,map2);										
						}
						for(int j=0;j<matchList.size();j++){
							
							if ( RPT.get(matchList.get(j)) == first ){
								System.out.println(first.getName()+"是"+matchList.get(j)+"的最优parent");
								//first为某个inst的最优父亲，因此需要判段是否更新后继节点								
								List<WebService> wsList = IIT.get(matchList.get(j));//需要inst的服务
								//还需要判断某个点是否可达
								if(wsList==null) continue;
								for(int k=0;k<wsList.size();k++){
									
									WebService successor = wsList.get(k);
									if(PQ.contains(successor)){//这里需要斟酌是原来的qos还是后来的qos
										if(wsList.get(k).getNewAllResponseTime()>0){//按照理想情况，这句话应该不需要
											//更新后继节点的Qos	
											//这里错了
											if(isCriticalNode(first,successor)){
												if(successor.getCriticalParent()!=null && successor.getCriticalParent()==first) {
													System.out.println(first.getName()+"已经是"+successor.getName()+"的关键parent");
													continue;													
												}
												System.out.println(successor.getName()+"旧的qos"+successor.getNewAllResponseTime());
												successor.setCriticalParent(first);
												successor.setNewAllResponseTime(successor.getNewAllResponseTime()-first.getAllResponseTime()+first.getNewAllResponseTime());											
												System.out.println(first.getName()+"是"+successor.getName()+"关键点");
												System.out.println(successor.getName()+"的新Qos是"+successor.getNewAllResponseTime());
												
											}else
												System.out.println(first.getName()+"不是"+successor.getName()+"关键点");
										}else{
											System.out.println("遇到不理想情况. "+wsList.get(k).getName()+"的newQos是"+wsList.get(k).getNewAllResponseTime());
										}
									}else{
										if(successor.getAllResponseTime()>0){//说明可达
											if(isCriticalNode(first,successor)){
												System.out.println(first.getName()+"是"+successor.getName()+"关键点");
												successor.setNewAllResponseTime(successor.getAllResponseTime()-first.getAllResponseTime()+first.getNewAllResponseTime());												
												successor.setCriticalParent(first);
												PQ.add(successor);																								System.out.println(first.getName()+"是"+successor.getName()+"关键点");
												System.out.println(successor.getName()+"的新Qos是"+successor.getNewAllResponseTime());
											}else{
												System.out.println(first.getName()+"不是关键点");
											}
										}else{
											System.out.println("yyyy "+successor.getName()+"的count:"+successor.getCount()+",不可达");
										}
									}
								}
							}else{
								//first不是某个inst的最优父亲，判断是否变成最优父亲
								System.out.println("kkkkkkkk");
								WebService parent = RPT.get(matchList.get(j));
								List<WebService> wsList = IIT.get(matchList.get(j));
								if(wsList==null) {
									System.out.println("wsList是null");
									continue;
								}
								for(int b=0;b<wsList.size();b++){
									WebService successor = wsList.get(b);
									if(successor.getCount()!=0) {
										System.out.println(successor.getName()+"的count:"+successor.getCount()+",不可达");
										continue;										
									}
									else{
										if(PQ.contains(successor)){//successor已经在队列中											
											if(parent.getNewAllResponseTime()>0){//因为是parent有可能更新过，也有可能没更新过
												System.out.println(")))))))))))))))))");
												if(parent.getNewAllResponseTime()>first.getNewAllResponseTime()){
													//first成为matchList.get(j)的新的parent
													System.out.println(first.getName()+"成为"+matchList.get(j)+"的新parent");
													RPT.put(matchList.get(j), first);
													if(isCriticalNode(first,successor)){
														if(successor.getCriticalParent()!=null && successor.getCriticalParent()==first) {
															System.out.println(first.getName()+"已经是"+successor.getName()+"的关键parent");
															continue;													
														}
														successor.setCriticalParent(first);
														
														successor.setNewAllResponseTime(successor.getNewAllResponseTime()-parent.getNewAllResponseTime()+first.getNewAllResponseTime());													
														System.out.println(first.getName()+"是"+successor.getName()+"关键点");
													}else{
														System.out.println(first.getName()+"不是关键点");
													}
													
												}else{
													//parent依然是最优的
													System.out.println(parent.getName()+"依然是最优的");
												}													
											}else{
												System.out.println("%%%%%%%%%%%%%%5");
												if(parent.getAllResponseTime()>first.getNewAllResponseTime()){
													//first成为matchList.get(j)的新的parent
													System.out.println(first.getName()+"成为"+matchList.get(j)+"的新parent");
													RPT.put(matchList.get(j), first);
													if(isCriticalNode(first,successor)){
														if(successor.getCriticalParent()!=null && successor.getCriticalParent()==first) {
															System.out.println(first.getName()+"已经是"+successor.getName()+"的关键parent");
															continue;													
														}
														successor.setNewAllResponseTime(successor.getNewAllResponseTime()-parent.getAllResponseTime()+first.getNewAllResponseTime());
														System.out.println(first.getName()+"是"+successor.getName()+"关键点");
													}else{
														System.out.println(first.getName()+"不是关键点");
													}												
												}else{
													//parent依然是最优的
												}	System.out.println(parent.getName()+"依然是最优的");
											}
										}else{//successor不在队列中
											if(parent.getNewAllResponseTime()>0){//因为是parent有可能更新过，也有可能没更新过
												System.out.println("&&&&&&&&&&&&&&&&&&&&&&");
												if(parent.getNewAllResponseTime()>first.getNewAllResponseTime()){
													//first成为matchList.get(j)的新的parent
													System.out.println(first.getName()+"成为"+matchList.get(j)+"的新parent");
													RPT.put(matchList.get(j), first);
													//这里理论上succesor没有newAllResponseTime 
													if(successor.getNewAllResponseTime()==0)
														if(isCriticalNode(first,successor)){
															successor.setNewAllResponseTime(successor.getAllResponseTime()-parent.getNewAllResponseTime()+first.getNewAllResponseTime());
															System.out.println(first.getName()+"是"+successor.getName()+"关键点");
															PQ.add(successor);
														}else{
															System.out.println(first.getName()+"不是关键点");
														}													
													else{
														System.out.println("231出现不理想情况");
													}
													
												}else{
													//parent依然是最优的
													System.out.println(parent.getName()+"依然是最优的");
												}													
											}else{
												System.out.println(")^^^^^^^^^^^^^^");
												if(parent.getAllResponseTime()>first.getNewAllResponseTime()){
													//first成为matchList.get(j)的新的parent
													System.out.println(first.getName()+"成为"+matchList.get(j)+"的新parent");
													RPT.put(matchList.get(j), first);
													if(successor.getNewAllResponseTime()==0)
														if(isCriticalNode(first,successor)){
															successor.setNewAllResponseTime(successor.getAllResponseTime()-first.getAllResponseTime()+first.getNewAllResponseTime());
															System.out.println(successor.getName()+"的新的qos:"+successor.getNewAllResponseTime());
															System.out.println(first.getName()+"是"+successor.getName()+"关键点");
															PQ.add(successor);
														}else{
															System.out.println(first.getName()+"不是关键点");
														}													
													else{
														System.out.println("244出现不理想情况");
													}							
												}else{
													//parent依然是最优的
												}	System.out.println(parent.getName()+"依然是最优的");
											}
										}
									}
								}
							}
						}
						
					}
				}
			}else{
				
			}
		}
		
		if(serviceMap.get("Request").getNewAllResponseTime()>0)
			System.out.println(serviceMap.get("Request").getNewAllResponseTime());
		else
			System.out.println(serviceMap.get("Request").getAllResponseTime());
		System.out.println("reQuery()");
		
		reQuery();
		
	}
	
	//HJQ_  List<WebService> PQ 根据    MIN(allqos,newallqos) 排序  同时两值相等且等于Request(相当于我认为的Response) return 1;
	public void sort(List<WebService> PQ){
		Collections.sort(PQ,new Comparator<WebService>(){

			public int compare(WebService arg0, WebService arg1) {
				if(arg0==null || arg1==null ) ;//System.out.println("pppppp");
				if(arg0==arg1)	;//System.out.println("vvvvvvv");
				double min1 = (arg0.getAllResponseTime()>arg0.getNewAllResponseTime())?arg0.getNewAllResponseTime():arg0.getAllResponseTime();
				double min2 = (arg1.getAllResponseTime()>arg1.getNewAllResponseTime())?arg1.getNewAllResponseTime():arg1.getAllResponseTime();
				if(min1>min2) 
					return 1;
				else 
					if(min1<min2)
						return -1;
					else 
						if(arg0.getName().equals("Request"))return 1;
						else if(arg0.getName().equals("Request")) return 1;
						else return 0;
			}
			
		});
	}
	
	//HJQ_  List<WebService> parentList  根据allqos排序
	public void sortParents(List<WebService> parentList){
		Collections.sort(parentList,new Comparator<WebService>(){

			public int compare(WebService arg0, WebService arg1) {
				// TODO Auto-generated method stub
				double qos1 = arg0.getNewAllResponseTime()>0?arg0.getNewAllResponseTime():arg0.getAllResponseTime();
				double qos2 = arg1.getNewAllResponseTime()>0?arg1.getNewAllResponseTime():arg1.getAllResponseTime();
				if(qos1>qos2) return 1;
				else return -1;
			}
			
		});
	}
		
	//HJQ_   List<Double> list  根据值排序
	public void sortDoubleList(List<Double> list){
		Collections.sort(list,new Comparator<Double>(){

			public int compare(Double arg0, Double arg1) {
				// TODO Auto-generated method stub
				if(arg0.doubleValue()>arg1.doubleValue()) return 1;
				else if(arg0.doubleValue() == arg1.doubleValue())
						return 0;
					else
						return -1;
			}
			
		});
	}
	
	//HJQ_重查     有一点区别于一次查询
	public void reQuery(){//改成找到了request就直接结束
		
		// System.out.println("reqQuery开始: "+startMili);
		
		int sumOfRequest = outputsFromChallenge.size();
		WebService begin = serviceMap.get("Provide");
		WebService end = serviceMap.get("Request");
		end.setCount(end.getInputs().size());
		List<WebService> reachVertices = new ArrayList<WebService>();
		
		//HJQ_所有服务初始化
		//以下省略一千行
		
        RPT.clear();
        
        //long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数  
        
		boolean find=false;
		reachVertices.add(begin);
		
		while(reachVertices.size()!=0){
			Collections.sort(reachVertices);
//			System.out.println("************");
//			for(WebService ww:reachVertices){
//				System.out.println(ww.getName()+"的返回时间"+ww.getAllResponseTime());
//			}
//			System.out.println("************");
						
			
			WebService v = reachVertices.remove(0);
			for(Object output:v.getOutputs()){
												
				
				if(!RPT.containsKey(output.toString())){//新的inst
					//RPT.put(output.toString(),v);
					//System.out.println("新的inst:"+output.toString());
					//String con = map1.get(output).toString();
										//HJQ_从第一次查询保存下来的solveMap中获取可匹配的List<inst>
					List<String> matchList = solveMap.get(output.toString());//所以能被output解决的inst	
										//HJQ_如果solveMap中不存在,重新查可匹配的List<inst>
					if(matchList==null){
					 matchList = findInstances(output.toString(),map1,map2);
					 //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^&&&");
					}
					
					//HJQ_??????
					if(!output.toString().equals(matchList.get(0)))//使matchList变成A1-A2-A3-A4
						for(int i=1;i<matchList.size();i++){
							if(output.toString().equals(matchList.get(i))){
								String st = matchList.get(0);
								matchList.set(0, matchList.get(i));
								matchList.set(i, st);
								break;
							}									
						}
										
					//System.out.println("matchList的大小："+matchList.size());
					boolean firstPlace=true;
					for(String inst : matchList){
//						if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//							System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						//if(!firstPlace && RPT.containsKey(inst)) continue;
						
						if(!RPT.containsKey(inst)){
							 RPT.put(inst, v);
							 //System.out.println("新的inst*:"+inst);
						 }else{
							 continue;
						 }
						
						firstPlace=false;
						 List<WebService> haveSameInstWebServiceList = IIT.get(inst);
						 if(haveSameInstWebServiceList==null){
							 //System.out.println("没有任何服务需要："+inst);
							 continue;
						 }
//						 if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//								System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						 
						 //System.out.println("有"+haveSameInstWebServiceList.size()+"个服务需要"+inst);
						 for(WebService w: haveSameInstWebServiceList){	
							 w.subCount();
							 //if(w.getName().equals("Request")) System.out.println("还剩"+end.getCount()+"个元素");
							 
							 if(w.getCount()==0){
								 w.setAllResponseTime(v.getAllResponseTime()+w.getSelfResponseTime());									
								 //enabledServices.put(w.getName(), w);
								 if(w.getName().equals("Request"))
									 if(end.getCount()==0){
										 //System.out.println("关键点是"+v.getName());
										 //System.out.println("找到了全部要求的元素");
										 //System.out.println(end.getAllResponseTime());
										 find = true;
										 //return;
										 continue;
										 //break;
									 }
									 
								 reachVertices.add(w);
									 //System.out.println("服务"+w.getName()+"可用了");
									// System.out.println("服务"+w.getName()+"的AllRequestTime:"+w.getAllResponseTime());
									 /////////serv1027640201 serv404388610  404388610
							
								 
							 }
						 }
						 
					}
					
				}
			}
		}
		
		//System.out.println("RPT.size()"+RPT.size());
		
		//long endMili=System.currentTimeMillis();
		//System.out.println("reQuery结束:"+endMili);
		//System.out.println("reQuery总耗时:"+(endMili-startMili)+"毫秒");
//		
//		if(!find)
//			System.out.println("找不到");
//		else
//			System.out.println("最优解的qos:"+end.getAllResponseTime());
		requeryQos=end.getAllResponseTime();
//		if(requeryQos==continousQos){
//			System.out.println("前后正确");
//			//successTime++;/////////////////////////
//			//count++;//////////			
//		}else{
//			System.out.println("前后bubububububu正确");
//			System.out.println(recordSomething);
//			System.out.println("wrong");
//			System.out.println(recordBackWard);
//			System.out.println("true");
//			backward(outputsFromChallenge,RPT,serviceMap);
//			////////////////
////			if(successTime!=0)
////				while(true){
////					System.out.println(successTime);
////				}
//			////////////////////
//			//System.out.println("reQuery的backward:");
//			//backward(outputsFromChallenge,RPT,serviceMap);;		
//		}
		
		
//		WebService w=null;
//		List<WebService> qosChangedList = new ArrayList<WebService>();
//		Set<WebService> wsSet = new HashSet<WebService>();
//		Map<String,Double> newQosMap = new HashMap<String,Double>();
//		sumOfChangedService=0;
//		
//		
//		
//		while(count>00){
//			while(sumOfChangedService<200){
//				Random random = new Random();
//				int r = random.nextInt(testServiceList.size());
//				//System.out.println("r="+r);
//				//System.out.println("testServiceList.size()"+testServiceList.size());
//			    w = testServiceList.get(r);
//				if(w.getCount()!=0 || w.getName().equals("Request") || w.getName().equals("Provide")) {
//					//System.out.println("w的count!=0,不能用");
//					continue;				
//				}
//				boolean odd=false;
//				odd = random.nextInt(100)%2!=0?true:false;
//				//int newQos = (int)(random.nextDouble()*w.getSelfResponseTime()*2);
//				int newQos = (int)(random.nextDouble()*30);
//				//if(newQos==0) continue;
//				if(odd){
//					if(w.getSelfResponseTime()-newQos<=0) continue;
//					else newQos = (int)w.getSelfResponseTime()-newQos;
//				}else{
//					newQos = (int)w.getSelfResponseTime()+newQos;
//				}
//				//System.out.println(w.getName()+" oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);			
//				if(wsSet.contains(w)) continue;
//				w.setNewAllResponseTime(w.getAllResponseTime()-w.getSelfResponseTime()+newQos);
//				w.setSelfResponseTime(newQos);
//				if(wsSet.add(w)){					
//					qosChangedList.add(w);
//				}
//				newQosMap.put(w.getName(), new Double(newQos));
//				sumOfChangedService++;
//				
//			}
//			count--;
//			System.out.println("count=="+count);
//			//continuousQuery1(qosChangedList);		
//				
//			continuousQuery4(qosChangedList);
//			
//			reQuery();
//		}
		
		//System.out.println(enabledServices.size());
	}
		
	/**
	 * 仅有qos变化的实验D的每组实验
	 * 有把不可达点算进变化的服务列的情况下,对比策略1,策略2的效率
	 * 策略1位根据实际变化的对最终结果有影响服务为阈值调用连续查询,策略2则是根据实际变化的服务为阈值调用连续查询
	 * @param oTOfCopy 
	 * @param solveMapOfCopy 
	 * @param RPT1OfCopy 
	 * @param sb 
	 */
	public void randomTest1(Map<String, List<String>> solveMapOfCopy, Map<String, List<WebService>> OTOfCopy, Map<String, WebService> RPT1OfCopy, StringBuilder sb){
		WebService w=null;
		
		List<WebService> qosChangedList = new ArrayList<WebService>();
		
		Set<WebService> wsSet = new HashSet<WebService>();

		Map<String, Double> newQosMap = new HashMap<String, Double>();

		Random random = new Random();

		int sum = (int) (random.nextDouble() * 1000);

		List<WebService> copyChangedList = new ArrayList<WebService>();

		realSum = 0;
		int copy = sum;

		while (sum > 0) {

			int r = random.nextInt(testServiceList.size());
			// System.out.println("r="+r);
			// System.out.println("testServiceList.size()"+testServiceList.size());
			w = testServiceList.get(r);
			// if(w.getCount()!=0 || w.getName().equals("Request") ||
			// w.getName().equals("Provide")) {
			// //System.out.println("w的count!=0,不能用");
			// continue;
			// }
			if (w.getName().equals("Request") || w.getName().equals("Provide")) {
				// System.out.println("w的count!=0,不能用");
				continue;
			}

			boolean odd = false;
			odd = random.nextInt(100) % 2 != 0 ? true : false;
			// int newQos =
			// (int)(random.nextDouble()*w.getSelfResponseTime()*2);
			int newQos = (int) (random.nextDouble() * 30);
			// if(newQos==0) continue;
			if (odd) {
				if (w.getSelfResponseTime() - newQos <= 0)
					continue;
				else
					newQos = (int) w.getSelfResponseTime() - newQos;
			} else {
				newQos = (int) w.getSelfResponseTime() + newQos;
			}
			// System.out.println(w.getName()+"
			// oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);

			if (wsSet.contains(w))
				continue;
			// HJQ_相比最原始的方法,多了这个判断
			// HJQ_把不可达的也算进去,即是改进前的算法
			if (w.getCount() != 0) {
				// 把不可达的也算进去
				w.setSelfResponseTime(newQos);
				wsSet.add(w);
				qosChangedList.add(w);
				sum--;

				// copy qosChangedList
				WebService ws = new WebService();
				ws.setName(w.getName());
				ws.setSelfResponseTime(w.getSelfResponseTime());
				copyChangedList.add(ws);

				continue;

				// 把不可达的不算进去
				// continue;
			} else {
				realSum++;
			}

			w.setNewAllResponseTime(w.getAllResponseTime() - w.getSelfResponseTime() + newQos);
			w.setSelfResponseTime(newQos);
			if (wsSet.add(w)) {
				qosChangedList.add(w);

				// copy qosChangedList
				WebService ws = new WebService();
				ws.setName(w.getName());
				ws.setSelfResponseTime(w.getSelfResponseTime());
				ws.setAllResponseTime(w.getAllResponseTime());
				ws.setNewAllResponseTime(w.getNewAllResponseTime());
				copyChangedList.add(ws);

			}
			newQosMap.put(w.getName(), new Double(newQos));
			sum--;
			}
			
			//复制qosChangedList
			List<WebService> qosChangedListOfCopy = null;
			try {
				qosChangedListOfCopy = DataGetter.copyWsList(qosChangedList);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//记录变化的服务信息
			for(int i = 0; i < qosChangedListOfCopy.size(); i++){
				WebService temp = qosChangedListOfCopy.get(i);
				if(temp.getCount() == 0){
					sb.append(temp.getName() + "," + temp.getSelfResponseTime() + System.getProperty("line.separator"));
				}
			}
			
			int groupNum = 100;
			int excludedNum = 10;
			
			double totalTime=0;
			long startMili=0;
			long endMili=0;
			List<Double> timeList = new ArrayList<Double>();  
			double t3;//重跑的时间
			double t4;//连续查询的时间
			/*****************连续查询*******************/
			
			totalTime=0;
			timeList.clear();
			
			for(int i=0;i<groupNum;i++){//测出groupNum组
				//reset PQMaxSize
				this.PQMaxSize = 0;
				
				//还RPT1和serviceMap1
				recoveryForHJQ(RPT1OfCopy);				
				recoveryWithNewQos(copyChangedList);
				recoveryQosChangedList(qosChangedList, qosChangedListOfCopy);
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// 当前时间对应的毫秒数		
							//HJQ_调用策略1
				continuousQuery4(qosChangedList);			
				endMili=System.nanoTime();
				
				timeList.add(new Double((endMili-startMili)/1E6));
				//System.out.println("endMili-startMili: " + (endMili-startMili));
				//System.out.println("recoveryTime: " + recoveryTime);
				//System.out.println("endMili-startMili - recoveryTime: " + (endMili-startMili - recoveryTime));
				//System.out.println("strategy1总耗时为:"+(endMili-startMili - recoveryTime)+"毫秒");
												
			}
			
			sortDoubleList(timeList);
			//System.out.println("连续查询:" + timeList);
			for(int i=(excludedNum != 1 ?(excludedNum - 1) : 1);i<groupNum - excludedNum;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t4=totalTime/(groupNum - 2 * excludedNum);
			
			/*********************重新跑一遍*****************/
			
			totalTime=0;
			timeList.clear();
			//统计某服务到终点服务的距离数
//			System.out.println("PQ max num:" + PQMaxSize);
			for(int i=0;i<groupNum;i++){//测出groupNum组
				//恢复count
				recoveryReQueryForHJQ();
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// 当前时间对应的毫秒数				
				reQuery();				
				endMili=System.nanoTime();
				timeList.add(new Double((endMili-startMili)/1E6));
				//System.out.println("reQuery总耗时为:"+(endMili-startMili)+"毫秒");											
			}

			
			sortDoubleList(timeList);
			//System.out.println("重查:" + timeList);
			for(int i=(excludedNum != 1 ?(excludedNum - 1) : 1);i<groupNum-excludedNum;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t3=totalTime/(groupNum - 2 * excludedNum);
			
			//统计重查与连续查询时间
			this.requeryTime += t3;
			this.continueTime += t4;
			
			//统计最佳策略时间
			this.bestStrategyTime += ((t4>t3)? t3: t4);
			
			double strategy1Time = -1;// 记录策略1采用什么算法
			double strategy2Time = -1;// 记录策略2采用什么算法
			String result = "";//记录策略1和策略2的对比结果
			//HJQ 实验D,统计策略1和策略2的胜率及平率
			//策略1是以实际,策略2是以全局
			if((realSum <= this.strategy1Value && copy <= this.strategy2Value)){
				this.equalCount ++;
				
				strategy1Time = t4;
				strategy2Time = t4;
				
				//统计策略时间
				this.strategy1Time += t4;
				this.strategy2Time += t4;
				
				result = "平" + System.getProperty("line.separator");
			}
			else if(realSum > this.strategy1Value && copy > this.strategy2Value){
				this.equalCount ++;
				
				strategy1Time = t3;
				strategy2Time = t3;
				
				//统计策略时间
				this.strategy1Time += t3;
				this.strategy2Time += t3;
				
				result = "平" + System.getProperty("line.separator");
			}
			else if((realSum <= this.strategy1Value && copy > this.strategy2Value)){
				strategy1Time = t4;
				strategy2Time = t3;
				
				//统计策略时间
				this.strategy1Time += t4;
				this.strategy2Time += t3;
				
				if(t4 < t3){
					this.winCount ++;
					//*System.out.println("策略1胜");
					result = "胜" + System.getProperty("line.separator");
				}
				else if(t4 == t3){
					this.equalCount ++;
					result = "平" + System.getProperty("line.separator");
				}
				else{
					//*System.out.println("策略1负");
					result = "负" + System.getProperty("line.separator");
				}
			}else if(realSum > this.strategy1Value && copy <= this.strategy2Value){
				strategy1Time = t3;
				strategy2Time = t4;
				
				//统计策略时间
				this.strategy1Time += t3;
				this.strategy2Time += t4;
				
				if(t3 < t4){
					this.winCount ++;
					//*System.out.println("策略1胜");
					result = "胜" + System.getProperty("line.separator");
				}
				else if(t3 == t4){
					this.equalCount ++;
					result = "平" + System.getProperty("line.separator");
				}
				else{
					//*System.out.println("策略1负");
					result = "负" + System.getProperty("line.separator");
				}
			}
			
			//输出相关信息至test.txt
			recordTime3.append(t3+System.getProperty("line.separator"));
			recordTime4.append(t4+System.getProperty("line.separator"));
			
			//后台输出相关信息
			//*System.out.println("重查平均耗时为:"+t3+"毫秒");
			//*System.out.println("连续查询平均耗时为:"+t4+"毫秒");
			
			//记录格式化后的实验D信息
			recordTime5.append(copy + ",");
			recordTime5.append(realSum + ",");
			recordTime5.append(PQMaxSize + ",");
			recordTime5.append(strategy1Time + ",");
			recordTime5.append(strategy2Time + ",");			
			recordTime5.append(t3 + ",");
			recordTime5.append(t4 + ",");
			recordTime5.append(result);
			
		//*System.out.println("qos改变了的服务个数："+copy);
		//*System.out.println("实际需要更新的ws(count==0):的个数："+realSum);
		//*System.out.println("PQ队列的最大值："+PQMaxSize);
	}

	
	private void recoverysolveMap(Map<String, List<String>> solveMapOfCopy) {
		this.solveMap.clear();

		for (String key : solveMapOfCopy.keySet()) {
			List<String> newList = new ArrayList<String>();

			for (String inst : solveMapOfCopy.get(key)) {
				newList.add(inst);
			}
			
			this.solveMap.put(key, newList);
		}
	}

	private void recoveryQosChangedList(List<WebService> qosChangedList, List<WebService> qosChangedListOfCopy) {
		if(qosChangedListOfCopy != null){
			qosChangedList.clear();
			
			for(WebService webService : qosChangedListOfCopy){
				qosChangedList.add(serviceMap.get(webService.getName()));
			}
		}
		
	}

	/**
	 * 对比重查和连续查询,感觉仅仅改变qos值,没有删除,增添服务的测试,没有把不可达点算进变化的服务列
	 * @param rPT1OfCopy 
	 * @param oTOfCopy 
	 * @param solveMapOfCopy 
	 */
	public void randomTest(Map<String, List<String>> solveMapOfCopy, Map<String, List<WebService>> OTOfCopy, Map<String, WebService> RPT1OfCopy){
		WebService w=null;
					//HJQ_所有变化的服务
		List<WebService> qosChangedList = new ArrayList<WebService>();
					//HJQ_所有变化的服务
		Set<WebService> wsSet = new HashSet<WebService>();
					//HJQ_ 服务名 <--> qos
		Map<String,Double> newQosMap = new HashMap<String,Double>();
					//HJQ_qosChangedList的副本
		List<WebService> copyChangedList = new ArrayList<WebService>();
		
		int sum;
		
		realSum=0;
		
			sum=sumOfChangedService;
			while(sum>0){
				Random random = new Random();
				int r = random.nextInt(testServiceList.size());
				//System.out.println("r="+r);
				//System.out.println("testServiceList.size()"+testServiceList.size());
			    w = testServiceList.get(r);
//				if(w.getCount()!=0 || w.getName().equals("Request") || w.getName().equals("Provide")) {
//					//System.out.println("w的count!=0,不能用");
//					continue;				
//				}
				if(w.getName().equals("Request") || w.getName().equals("Provide")) {
					//System.out.println("w的count!=0,不能用");
					continue;				
				}
				
				boolean odd=false;
				odd = random.nextInt(100)%2!=0?true:false;
				//int newQos = (int)(random.nextDouble()*w.getSelfResponseTime()*2);
				int newQos = (int)(random.nextDouble()*30);
				//if(newQos==0) continue;
					//HJQ_奇数就是qos变差,减   偶数就是qos变好,加
				if(odd){
					if(w.getSelfResponseTime()-newQos<=0) continue;
					else newQos = (int)w.getSelfResponseTime()-newQos;
				}else{
					newQos = (int)w.getSelfResponseTime()+newQos;
				}
				//System.out.println(w.getName()+" oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);			
				
				
				if(wsSet.contains(w)) continue;
	   						//HJQ_没有把不可达的也算进去,即改进后的算法
				if(w.getCount()!=0){
					//把不可达的也算进去
					//实验C需要把以下注释掉
					w.setSelfResponseTime(newQos);
					wsSet.add(w);
					qosChangedList.add(w);
					sum--;
					
					//copy qosChangedList
					WebService ws = new WebService();
					ws.setName(w.getName());
					ws.setSelfResponseTime(w.getSelfResponseTime());					
					copyChangedList.add(ws);
					
					//把不可达的不算进去
					continue;					
				}else{
					realSum++;
				}
				   			//HJQ_newallqos = allqos-selfqos+newqos
				w.setNewAllResponseTime(w.getAllResponseTime()-w.getSelfResponseTime()+newQos);
							//HJQ_selfqos = newqos
				w.setSelfResponseTime(newQos);
				
				if(wsSet.add(w)){					
					qosChangedList.add(w);
					
					//copy qosChangedList
					WebService ws = new WebService();
					ws.setName(w.getName());
					ws.setSelfResponseTime(w.getSelfResponseTime());					
					ws.setAllResponseTime(w.getAllResponseTime());
					ws.setNewAllResponseTime(w.getNewAllResponseTime());
					copyChangedList.add(ws);
					
				}
				newQosMap.put(w.getName(), new Double(newQos));
				sum--;
			}
			
			//复制qosChangedList
			List<WebService> qosChangedListOfCopy = null;
			try {
				qosChangedListOfCopy = DataGetter.copyWsList(qosChangedList);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//复制RPT1和serviceMap
			//copy();
			
			double totalTime=0;
			long startMili=0;
			long endMili=0;
			List<Double> timeList = new ArrayList<Double>();  
			double t1;//连续查询的时间
			double t2;//重查的时间
			
			//下面将对变化后的服务列分别进行连续查询和重查,比较花费的时间
			
			for(int i=0;i<10;i++){//测出10组
				this.PQMaxSize = 0;
				//还RPT1和serviceMap1
				recoveryForHJQ(RPT1OfCopy);				
				recoveryWithNewQos(copyChangedList);
				recoveryQosChangedList(qosChangedList, qosChangedListOfCopy);
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// 当前时间对应的毫秒数				
				continuousQuery4(qosChangedList);				
				endMili=System.nanoTime();
				timeList.add(new Double((endMili-startMili)/1E6));
				//System.out.println("continuousQuery总耗时为:"+(endMili-startMili)+"毫秒");
				
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t1=totalTime/8;
			
			//System.out.println("continuousQuery结束 :"+endMili);
			//*System.out.println("PQ最大长度是" + this.PQMaxSize);
			//*System.out.println("continuousQueryTotal总耗时为:"+totalTime/8+"毫秒");
			
			recordTime1.append(totalTime/8+System.getProperty("line.separator"));
			
			//*System.out.println("continuous最优解是:"+continousQos);
			//cjh add
			continueTime+=t1;
			
			totalTime=0;
			timeList.clear();
			
			for(int i=0;i<10;i++){
				//HJQ
				recoveryReQueryForHJQ();
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// 当前时间对应的毫秒数						
				reQuery();
				endMili=System.nanoTime();
				//System.out.println("reQuery总耗时为:"+(endMili-startMili)+"毫秒");
				timeList.add(new Double((endMili-startMili)/1E6));
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}	
			
			t2=totalTime/8;
			//cjh add
			requeryTime+= t2;
			//记录时间大小
			if(t1<t2){
				recordCompare.append(1+System.getProperty("line.separator"));
			}else 
				if(t1==t2) recordCompare.append(0+System.getProperty("line.separator"));
				else recordCompare.append(-1+System.getProperty("line.separator"));
			
			//*System.out.println("最优解的qos:"+requeryQos);
			
			recordTime2.append(totalTime/8+System.getProperty("line.separator"));
			
			//*System.out.println("reQueryTotal总耗时为:"+totalTime/8+"毫秒");
			
			if(requeryQos==continousQos){
				//System.out.println("前后正确");		
			}else{
				//System.out.println("前后bubububububu正确");		
			}
			
			//记录实际需要更新的ws(count!=0):的个数
			recordRealSum.append(realSum+System.getProperty("line.separator"));
			
			//*System.out.println("实际需要更新的ws(count!=0):的个数："+realSum);
	}
	
	//HJQ_ 将copyChangedList中的服务的新qos 覆盖掉serviceMap对应服务的qos
	public void recoveryWithNewQos(List<WebService> copyChangedList){
		for(int i=0;i<copyChangedList.size();i++){
			WebService record = copyChangedList.get(i);
			WebService ws = serviceMap.get(record.getName());
			if(ws != null){
				ws.setAllResponseTime(record.getAllResponseTime());
				ws.setNewAllResponseTime(record.getNewAllResponseTime());
				ws.setSelfResponseTime(record.getSelfResponseTime());
			}
		}
	}
	
	//HJQ_ 从serviceMap1 恢复到 serviceMap
	public void recovery(){
		//HJQ_ 从serviceMap1 恢复到 serviceMap
		Set<String> key = serviceMap.keySet();
		String s;
        for (Iterator it = key.iterator(); it.hasNext();) {
            s = (String) it.next();           
            WebService ws = serviceMap.get(s);
            
            WebService origin = serviceMap1.get(s);
           
            ws.setAllResponseTime(origin.getAllResponseTime());
            ws.setCount(origin.getCount());
            ws.setCriticalParent(origin.getCriticalParent());
            ws.setInputs(origin.getInputs());
            ws.setName(origin.getName());
            ws.setNewAllResponseTime(origin.getNewAllResponseTime());
            ws.setOutputs(origin.getOutputs());
            ws.setSelfResponseTime(origin.getSelfResponseTime());
            ws.setSelfThroughput(origin.getSelfThroughput());
            
        }	
        
        Set<String> key1 = RPT1.keySet();
		String ss;
        for (Iterator it = key1.iterator(); it.hasNext();) {
            ss = (String) it.next();                       
            
            WebService origin = RPT2.get(ss);
                       
//            WebService copy = new WebService();
//            copy.setAllResponseTime(origin.getAllResponseTime());
//            copy.setCount(origin.getCount());
//            copy.setCriticalParent(origin.getCriticalParent());
//            copy.setInputs(origin.getInputs());
//            copy.setName(origin.getName());
//            copy.setNewAllResponseTime(origin.getNewAllResponseTime());
//            copy.setOutputs(origin.getOutputs());
//            copy.setSelfResponseTime(origin.getSelfResponseTime());
//            copy.setSelfThroughput(origin.getSelfThroughput());
//            
//            RPT1.put(ss, copy);
            WebService ww = serviceMap.get(origin.getName());
            RPT1.put(ss, ww);
        }
	}
	
	 //HJQ_复制RPT1 - > RPT2  复制serviceMap - > serviceMap1
	public void copy(){
		//HJQ_复制serviceMap - > serviceMap1
		Set<String> key = serviceMap.keySet();
		String s;
        for (Iterator it = key.iterator(); it.hasNext();) {
            s = (String) it.next();           
            WebService ws = serviceMap.get(s);
                                    
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
            
            serviceMap1.put(s, copy);
        }	
        //HJQ_复制RPT1 - > RPT2
        Set<String> key1 = RPT1.keySet();
		String ss;
        for (Iterator it = key1.iterator(); it.hasNext();) {
            ss = (String) it.next();           
            WebService ws = RPT1.get(ss);
                                   
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
            
            RPT2.put(ss, copy);
        }
        
	}
	
	//HJQ_RPT后向搜索
	public  void backward(List<String> outputsFromChallenge,Map<String,WebService> RPT,Map<String,WebService> serviceMap){
		List<WebService> parents = new ArrayList<WebService>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();
		Set<WebService> parentSet = new HashSet<WebService>();
		Map<String,ArrayList<String>> childParents = new HashMap<String,ArrayList<String>>();

		for(int i=0;i<outputsFromChallenge.size();i++){
			if(RPT.get(outputsFromChallenge.get(i)).getName().equals("serv1859188453"));
			//*System.out.println("存在2031");
			if(parentSet.add(RPT.get(outputsFromChallenge.get(i)))){
				parents.add(RPT.get(outputsFromChallenge.get(i)));
				parents2.add(RPT.get(outputsFromChallenge.get(i)));
			}
			//System.out.println(RPT.get(outputsFromChallenge.get(i)));
		}
		//System.out.println("layerList.size="+layerList.size());
		
		while(!parents.isEmpty()){
			//System.out.println("kkk");
			Collections.sort(parents);
			Collections.sort(parents2);
			WebService  ws = parents.get(parents.size()-1);
			parents.remove(parents.size()-1);
			//System.out.println(ws);
			for(int i=0;i<ws.getInputs().size();i++){
				if(RPT.get(ws.getInputs().get(i)).getName().equals("serv1723601037"));
					//*System.out.println("存在201");
				if(parentSet.add(RPT.get(ws.getInputs().get(i)))){
					parents.add(RPT.get(ws.getInputs().get(i)));
					parents2.add(RPT.get(ws.getInputs().get(i)));		
				}
			}
			
		}
		System.out.println("**backward**");
		for(int i=0;i<parents2.size();i++){
			System.out.println(parents2.get(i).getName()+" "+parents2.get(i).getSelfResponseTime()+" "+parents2.get(i).getAllResponseTime());
		}
		System.out.println("Request");
		System.out.println("END-backward");
		
//		
//		//画图用
//		if(typePath.equals("before")){
//			PrintNetwork.printOptimalPathBefore(parents2, RPT1, IIT);
//		}
//		else if(typePath.equals("last")){
//			PrintNetwork.printOptimalPathLast(parents2, RPT1, IIT);
//		}

		
//		parents2.add(serviceMap.get("Request"));
//		for(int i=parents2.size()-1;i>=0;i--){
//			parentSet.clear();
//			for(int j=0;j<parents2.get(i).getInputs().size();j++){
//				WebService w = RPT.get(parents2.get(i).getInputs().get(j));
//				if( parentSet.add(w) ){
//					ArrayList<String> p = childParents.get(parents2.get(i).getName());
//					if(p==null){
//						p = new ArrayList<String>();
//						p.add(w.getName());
//						childParents.put(parents2.get(i).getName(), p);
//					}else{
//						p.add(w.getName());
//						childParents.put(parents2.get(i).getName(), p);
//					}		
//				}
//			}
//		}
//		
//		for(int i=parents2.size()-1;i>0;i--){
//			List<String> p = childParents.get(parents2.get(i).getName());
//			System.out.println(parents2.get(i).getName()+"的parents是");
//			for(int k=0;k<p.size();k++){
//				System.out.print(p.get(k)+" ");
//			}
//			System.out.println();
//		}

	}
	
	//HJQ_后向搜索RPT1
	public  void backwardForRPT1(List<String> outputsFromChallenge,Map<String,WebService> RPT1,Map<String,WebService> serviceMap){
		List<WebService> parents = new ArrayList<WebService>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();
		Set<WebService> parentSet = new HashSet<WebService>();
		Map<String,ArrayList<String>> childParents = new HashMap<String,ArrayList<String>>();
		for(int i=0;i<outputsFromChallenge.size();i++){
			if(RPT1.get(outputsFromChallenge.get(i)).getName().equals("serv1859188453"))
				//System.out.println("存在2031");
			if(parentSet.add(RPT1.get(outputsFromChallenge.get(i)))){
				parents.add(RPT1.get(outputsFromChallenge.get(i)));
				parents2.add(RPT1.get(outputsFromChallenge.get(i)));
			}
			//System.out.println(RPT.get(outputsFromChallenge.get(i)));
		}
		//System.out.println("layerList.size="+layerList.size());
		
		
		while(!parents.isEmpty()){
			//System.out.println("kkk");
			Collections.sort(parents);
			Collections.sort(parents2);
			WebService  ws = parents.get(parents.size()-1);
			parents.remove(parents.size()-1);
			//System.out.println(ws);
			for(int i=0;i<ws.getInputs().size();i++){
				if(RPT1.get(ws.getInputs().get(i)).getName().equals("serv1723601037"))
					//System.out.println("存在201");
				if(parentSet.add(RPT1.get(ws.getInputs().get(i)))){
					parents.add(RPT1.get(ws.getInputs().get(i)));
					parents2.add(RPT1.get(ws.getInputs().get(i)));		
				}
			}
			
		}
		System.out.println("**backward**");
		for(int i=0;i<parents2.size();i++){
			System.out.println(parents2.get(i).getName()+" "+parents2.get(i).getSelfResponseTime()+" "+parents2.get(i).getAllResponseTime());
		}
		System.out.println("Request");
		System.out.println("**END-backward**");
	}
	
	//HJQ_保存RPT1最终结果(每个服务的qos以及最终结果的图结构)
	public void recordBackWardForRPT1(){
		//HJQ_后向搜索
		List<WebService> parents = new ArrayList<WebService>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();//HJQ_真正的result
		Set<WebService> parentSet = new HashSet<WebService>();
		
		Map<String,ArrayList<String>> childParents = new HashMap<String,ArrayList<String>>();
		
		for(int i=0;i<outputsFromChallenge.size();i++){			
			if(parentSet.add(RPT1.get(outputsFromChallenge.get(i)))){
				parents.add(RPT1.get(outputsFromChallenge.get(i)));
				parents2.add(RPT1.get(outputsFromChallenge.get(i)));
			}
			//System.out.println(RPT.get(outputsFromChallenge.get(i)));
		}
		//System.out.println("layerList.size="+layerList.size());
		
		
		while(!parents.isEmpty()){
			//System.out.println("kkk");
			Collections.sort(parents);
			Collections.sort(parents2);
			WebService  ws = parents.get(parents.size()-1);
			parents.remove(parents.size()-1);
			//System.out.println(ws);
			for(int i=0;i<ws.getInputs().size();i++){				
				if(parentSet.add(RPT1.get(ws.getInputs().get(i)))){
					parents.add(RPT1.get(ws.getInputs().get(i)));
					parents2.add(RPT1.get(ws.getInputs().get(i)));		
				}
			}
			
		}
		//HJQ_保存结果
		recordBackWard=null;
		recordBackWard=recordBackWard+"**backward**"+"\n";
		for(int i=0;i<parents2.size();i++){
			recordBackWard=recordBackWard+parents2.get(i).getName()+" "+parents2.get(i).getSelfResponseTime()+" "+parents2.get(i).getAllResponseTime()+"\n";			
		}
		recordBackWard=recordBackWard+"Request"+"\n"+"**END-backward*"+"\n";		
		
		//HJQ_往结果添加Request服务(相当于我的Response)
		parents2.add(serviceMap.get("Request"));
		
		for(int i=parents2.size()-1;i>=0;i--){
			parentSet.clear();
			//HJQ_遍历每一个inputs的inst
			for(int j=0;j<parents2.get(i).getInputs().size();j++){
				//HJQ_从终点往前找服务
				WebService w = RPT.get(parents2.get(i).getInputs().get(j));
				//HJQ_保存服务名        后一个服务是前一个服务的父亲
				if( parentSet.add(w) ){
					ArrayList<String> p = childParents.get(parents2.get(i).getName());
					if(p==null){
						p = new ArrayList<String>();
						p.add(w.getName());
						childParents.put(parents2.get(i).getName(), p);
					}else{
						p.add(w.getName());
						childParents.put(parents2.get(i).getName(), p);
					}		
				}
			}
		}
		//HJQ_保存到   recordBackWard
		for(int i=parents2.size()-1;i>0;i--){
			List<String> p = childParents.get(parents2.get(i).getName());
			recordBackWard=recordBackWard+parents2.get(i).getName()+"的parents是"+"\n";
			for(int k=0;k<p.size();k++){				
				recordBackWard=recordBackWard+p.get(k)+" ";
			}			
			recordBackWard=recordBackWard+"\n";
		}
		
	}
	
	//HJQ_获取挑战的请求和回应
	public  void getChanllenge(List<String> inputsFromChallenge,List<String> outputsFromChallenge){
		File xmlFile = chooseDataSetListener.getChallenge();  
		System.out.println(xmlFile.getPath()); 
		
		 if(xmlFile.exists()){  
		        SAXReader reader = new SAXReader();
		        System.out.println("exist");       
		        try {  
    	                //读入文档流  
    		                Document document = reader.read(xmlFile);  
    		            //获取根节点  
    		                Element root = document.getRootElement();
    		                //System.out.println(root.getName().toString());
    		        		                
    		                for ( Iterator i = root.elementIterator("semExtension"); i.hasNext();) {
    		                    Element foo = (Element) i.next();
    		                    //System.out.println(foo.attributeValue("name"));
    		                                        
    		                      for ( Iterator j = foo.elementIterator("semMessageExt"); j.hasNext();){    		     
    		                    	  //input
    		                    	  Element foo1 = (Element) j.next();    		                    	    		                    	  
    		                    	  //System.out.println("http://www.ws-challenge.org/wsc08.owl#".toString().length());    		                    	  
    		                    	  for ( Iterator k = foo1.elementIterator("semExt"); k.hasNext();){
    		                    		  Element foo2 = (Element) k.next();
    		                    		  for ( Iterator f = foo2.elementIterator("ontologyRef"); f.hasNext();){
    		                    			  Element foo3 = (Element) f.next();    		                    			  
    		                    			  inputsFromChallenge.add(foo3.getStringValue().toString().substring(38));
    		                    		  }      		                    		  
    		                    	  }
    		                    	  
    		                    	  //output
    		                    	  foo1 = (Element) j.next();    		                    	    		                    	      		                    	      		                    	  
    		                    	  for ( Iterator k = foo1.elementIterator("semExt"); k.hasNext();){
    		                    		  Element foo2 = (Element) k.next();
    		                    		  for ( Iterator f = foo2.elementIterator("ontologyRef"); f.hasNext();){
    		                    			  Element foo3 = (Element) f.next();
    		                    			  //System.out.println(foo3.getName().toString());    		                    			 
    		                    			  outputsFromChallenge.add(foo3.getStringValue().toString().substring(38));
    		                    		  }      		                    		  
    		                    	  }
    		                    	  
    		                      }  
    		                    	  
    		                }    		               
    		                         		                         		                
		        }catch(Exception ex){
		        	ex.printStackTrace();
		        }
		        }
		//Test
		 	
//			for(int i=0;i<outputsFromChallenge.size();i++){
//				System.out.println(outputsFromChallenge.get(i));
//			}
	}
	
	//HJQ_构造IIT
	public  void constructIIT(Map<String,WebService>serviceMap,Map<String,List<WebService>> IIT){
		for(String serviceName : serviceMap.keySet()){
			WebService webservice = serviceMap.get(serviceName);
			//遍历input元素
			Iterator it = webservice.getInputs().iterator();
			while(it.hasNext()){
				String input = it.next().toString();
				//System.out.println(input);
				if(!IIT.containsKey(input)){
					List<WebService> serviceList = new ArrayList<WebService>();
					serviceList.add(webservice);
					IIT.put(input, serviceList);
				}else{
					List<WebService> serviceList = IIT.get(input);
					serviceList.add(webservice);
					IIT.put(input, serviceList);
				}
			}
		}
		//Test
//		List<WebService> list = IIT.get("inst777562170");
//		for(int i=0;i<list.size();i++){
//			System.out.println(list.get(i).getName());
//		}
	}
	
	//HJQ_?????
	//HJQ_从名字看是得到最佳的的服务组合     包含一次查询的代码端, 但至于OT和solveMap在此处不知有何用处
	public  void getOptimalWebServiceComposition(List<String> inputsFromChallenge, List<String> outputsFromChallenge,
			Map<String,List<WebService>> IIT,Map<String,String> map1,Map<String,List<String>> map2,Map<String,WebService> RPT){
			int sumOfRequest = outputsFromChallenge.size();
			WebService begin = new WebService();
			WebService end = new WebService();
			List<WebService> reachVertices = new ArrayList<WebService>();
			
			//HJQ_构造终始点
			begin.setOutputs(inputsFromChallenge);
			begin.setName("Provide");
			begin.setInputs(new ArrayList<WebService>());
			begin.setSelfResponseTime(0);
			begin.setAllResponseTime(0);
			end.setName("Request");
			end.setInputs(outputsFromChallenge);
			serviceMap.put("Provide",begin);
			serviceMap.put("Request", end);
			
			//插入end的inputs到IIT
			for(Object input: end.getInputs()){
				if(!IIT.containsKey(input)){
					List<WebService> serviceList = new ArrayList<WebService>();
					serviceList.add(end);
					IIT.put(input.toString(), serviceList);
				}else{
					List<WebService> serviceList = IIT.get(input);
					serviceList.add(end);
					IIT.put(input.toString(), serviceList);
				}
			}
			
			System.out.println("本次挑战需要：");
			for(int i=0;i<end.getInputs().size();i++){
				System.out.println(end.getInputs().get(i));
			}
			
			boolean find=false;
			reachVertices.add(begin);
			end.setCount(sumOfRequest);
			System.out.println(end.getCount());
			while(reachVertices.size()!=0){
				Collections.sort(reachVertices);
//				System.out.println("************");
//				for(WebService ww:reachVertices){
//					System.out.println(ww.getName()+"的返回时间"+ww.getAllResponseTime());
//				}
//				System.out.println("************");
				
				
				WebService v = reachVertices.remove(0);
				sumOfCanUseWS++;//HJQ_
				for(Object output:v.getOutputs()){
					
					///*********填充OT*********
					List<String> matchList1 = findInstances(output.toString(),map1,map2);//所以能被output解决的inst	
					
					//HJQ_填充OT
					for(int i=0;i<matchList1.size();i++){
						List<WebService> l = OT.get(matchList1.get(i));
						if(l==null){
							l = new ArrayList<WebService>();
							l.add(v);
							OT.put(matchList1.get(i), l);
						}else{
							l.add(v);
						}
					}
					//***********************
					
					if(!RPT.containsKey(output.toString())){//新的inst
						//RPT.put(output.toString(),v);
						//RPT1.put(output.toString(),v);
						//System.out.println("新的inst:"+output.toString());
						//String con = map1.get(output).toString();
						List<String> matchList = findInstances(output.toString(),map1,map2);//所以能被output解决的inst						
						
						//*********完全填充solveMap*********
						for(int o=0;o<matchList.size();o++)
							if(!solveMap.containsKey(matchList.get(o))){
								List<String> matchList2 = findInstances(matchList.get(o),map1,map2);//所以能被output解决的inst
								solveMap.put(matchList.get(o), matchList2);
							}					
						//*********************************
						
						//HJQ_把相同的inst 换到第一位 
						if(!output.toString().equals(matchList.get(0)))//使matchList变成A1-A2-A3-A4  ?????
							for(int i=1;i<matchList.size();i++){
								if(output.toString().equals(matchList.get(i))){
									String st = matchList.get(0);
									matchList.set(0, matchList.get(i));
									matchList.set(i, st);
									break;
								}									
							}
						//solveMap.put(output.toString(), matchList);
						
						//System.out.println("matchList的大小："+matchList.size());
						boolean firstPlace=true;
						for(String inst : matchList){
							
							if(!RPT.containsKey(inst)){
								 RPT.put(inst, v);
								 RPT1.put(inst, v);
								 //System.out.println("新的inst*:"+inst);
							 }else{
								 continue;
							 }
							
							//if(!firstPlace && RPT.containsKey(inst)) continue;
							firstPlace=false;
							 List<WebService> haveSameInstWebServiceList = IIT.get(inst);
							 if(haveSameInstWebServiceList==null){
								 //System.out.println("没有任何服务需要："+inst);
								 continue;
							 }
							 
							 
							 //System.out.println("有"+haveSameInstWebServiceList.size()+"个服务需要"+inst+" "+v.getName() );
							 for(WebService w: haveSameInstWebServiceList){						
								 w.subCount();
								 ///if(w.getName().equals("serv612685309")) System.out.println(w.getName()+"还剩"+w.getCount()+"个元素");
								 if(w.getName().equals("Request")) System.out.println("还剩"+end.getCount()+"个元素");
								 if(w.getCount()==0){
									 w.setAllResponseTime(v.getAllResponseTime()+w.getSelfResponseTime());									
									 //enabledServices.put(w.getName(), w);
									 
									 if(w.getName().equals("Request"))
										 if(w.getCount()==0){
											 System.out.println("找到了全部要求的元素");
											 System.out.println(end.getAllResponseTime());
											 find = true;										 
											 //return;
											 continue;
										 }
									 
										 reachVertices.add(w);
										 //System.out.println("服务"+w.getName()+"可用了");
										// System.out.println("服务"+w.getName()+"的AllRequestTime:"+w.getAllResponseTime());
										 /////////serv1027640201 serv404388610  404388610
										
										 
										 
										 /////////
									 
								 }
							 }
							 
						}
						
					}
				}
			}
			if(!find)
				System.out.println("找不到");
			System.out.println("RPT大小:"+RPT.size());
	}
	
	//HJQ_获取出webService的QOS
	public  void getQos(Map<String,WebService> serviceMap){
		File xmlFile = chooseDataSetListener.getServiceLevelAgreement();  
		///System.out.println(xmlFile.getPath()); 
		
		 if(xmlFile.exists()){  
		        SAXReader reader = new SAXReader();
		        System.out.println("exist");       
		        try {  
    	                //读入文档流  
    		                Document document = reader.read(xmlFile);  
    		            //获取根节点  
    		                Element root = document.getRootElement();
    		                //System.out.println(root.getName().toString());
    		                int sumOfQos=0;    		                
    		                for ( Iterator i = root.elementIterator("Obligations"); i.hasNext();) {
    		                    Element foo = (Element) i.next();
    		                    //System.out.println(foo.attributeValue("name"));
    		                                        
    		                      for ( Iterator j = foo.elementIterator("ServiceLevelObjective"); j.hasNext();){
    		                    	//Responsetime  
    		                    	  Element foo1 = (Element) j.next();    		                    	
    		                    	  //String name = "ServiceLevelObjectiveThroughput";
    		                    	  //第一个是ServiceLevelObjectiveResponsetime，第二个是ServiceLevelObjectiveThroughput
    		                    	  String serviceName = foo1.attributeValue("name").substring(33);
    		                    	  //System.out.println(serviceName.substring(33)+"dd");
    		                    	  if(!serviceMap.containsKey(serviceName)){    		                    		 
    		                    		  continue;
    		                    	  }
    		                    	  for ( Iterator k = foo1.elementIterator("Expression"); k.hasNext();){
    		                    		  Element foo2 = (Element) k.next();
    		                    		  for ( Iterator f = foo2.elementIterator("Predicate"); f.hasNext();){
    		                    			  Element foo3 = (Element) f.next();
    		                    			  //System.out.println(foo3.getName().toString());
    		                    			  for ( Iterator h = foo3.elementIterator("Value"); h.hasNext();){
        		                    			  Element foo4 = (Element) h.next();        		                    			  
        		                    			  //System.out.println(foo4.getStringValue());
        		                    			  serviceMap.get(serviceName).setSelfResponseTime(Double.parseDouble(foo4.getStringValue()));
    		                    			  }
    		                    		  }  
    		                    		  sumOfQos++;
    		                    	  }
    		                    	  
    		                    	  //Throughput
    		                    	  foo1 = (Element) j.next();    		                    	 
    		                    	  for ( Iterator k = foo1.elementIterator("Expression"); k.hasNext();){
    		                    		  Element foo2 = (Element) k.next();
    		                    		  for ( Iterator f = foo2.elementIterator("Predicate"); f.hasNext();){
    		                    			  Element foo3 = (Element) f.next();
    		                    			  //System.out.println(foo3.getName().toString());
    		                    			  for ( Iterator h = foo3.elementIterator("Value"); h.hasNext();){
        		                    			  Element foo4 = (Element) h.next();        		                    			  
        		                    			  //System.out.println(foo4.getStringValue());
        		                    			  serviceMap.get(serviceName).setSelfThroughput(Double.parseDouble(foo4.getStringValue()));
    		                    			  }
    		                    		  }    		                    		      		                    	
    		                    		  sumOfQos++;
    		                    	  }     
    		                    	  
    		                      }  
    		                    	  
    		                }
    		                System.out.println("有"+serviceMap.size()+"服务，对应"+sumOfQos/2+"组qos");
    		                         		                         		                
		        }catch(Exception ex){
		        	ex.printStackTrace();
		        }
		        }
	}
	
	//HJQ_获取webService列表,即读取所有webService
	public  void constructWebServiceList(Map<String,WebService> serviceMap){
		File xmlFile =  chooseDataSetListener.getServices();
		///System.out.println(xmlFile.getPath()); 
		
		 if(xmlFile.exists()){  
		        SAXReader reader = new SAXReader();
		        System.out.println("exist");       
		        try {  
    	                //读入文档流  
    		                Document document = reader.read(xmlFile);  
    		            //获取根节点  
    		                Element root = document.getRootElement();
    		                //System.out.println(root.getName().toString());
    		                int sumofservice=0;    		                
    		                for ( Iterator i = root.elementIterator("service"); i.hasNext();) {
    		                    Element foo = (Element) i.next();
    		                    //System.out.println(foo.attributeValue("name"));
    		                    WebService webservice = new WebService();
    		                    webservice.setInputs(new ArrayList());
    		                    webservice.setOutputs(new ArrayList());
    		                    webservice.setName(foo.attributeValue("name"));
    		                      //System.out.print(foo.attributeValue("ID"));
    		                    
    		                      for ( Iterator j = foo.elementIterator("inputs"); j.hasNext();){
    		                    	  Element foo1 = (Element) j.next();
    		                    	  //System.out.println(foo1.getName());
    		                    	  for ( Iterator k = foo1.elementIterator("instance"); k.hasNext();){
    		                    		  Element foo2 = (Element) k.next();
    		                    		  //System.out.println(foo2.getName()+" "+ foo2.attributeValue("name"));
    		                    		  webservice.getInputs().add(foo2.attributeValue("name"));    		                    		  
    		                    	  }
    		                    	  webservice.setCount(webservice.getInputs().size());
    		                      }
    		                      
    		                      for ( Iterator j = foo.elementIterator("outputs"); j.hasNext();){
    		                    	  Element foo1 = (Element) j.next();
    		                    	 // System.out.println(foo1.getName());
    		                    	  for ( Iterator k = foo1.elementIterator("instance"); k.hasNext();){
    		                    		  Element foo2 = (Element) k.next();
    		                    		  //System.out.println(foo2.getName()+" "+ foo2.attributeValue("name"));
    		                    		  webservice.getOutputs().add(foo2.attributeValue("name"));    		                    		  
    		                    	  }    		    		                    	    		                    	      		                
    		                      }
    		                      //System.out.println(webservice.getName());
    		                      serviceMap.put(webservice.getName(),webservice);
    		                      
    		                      sumofservice++;    		                      
    		                 }
    		                 
    		                //填充testServiceList
    		                Set<String> key = serviceMap.keySet();
    		                for (Iterator it = key.iterator(); it.hasNext();) {
    		                    String s = (String) it.next();
    		                    WebService ws = serviceMap.get(s);    		                    
    		                    testServiceList.add(ws);
    		                }
    		                
    		                
    		                //This is for test
    		                System.out.println("服务数量："+serviceMap.size());
//    		                for(int i=0;i<webserviceList.get(571).getInputs().size();i++){
//    		                	System.out.println(webserviceList.get(571).getInputs().get(i).toString());
//    		                }
    		                
		        }catch(Exception ex){
		        	ex.printStackTrace();
		        }
	}
	}
	
	//HJQ_寻找可匹配的
	public  List<String> findInstances(String instance,Map<String,String> map1,Map<String,List<String>> map2){
		List<String> instances = new ArrayList<String>();
		String con = map1.get(instance).toString();
		while(con!=null){
			if(map2.containsKey(con)){
				instances.addAll((Collection)(map2.get(con)));
				con = (String)map1.get(con.substring(1));
			}
		}
//		for(int i=0;i<instances.size();i++){
//			System.out.print(instances.get(i)+" ");
//		}
//		System.out.println();
		return instances;
		
		
	}
	
	//HJQ_读取数据
	public void constructMap1Map2(Map map1,Map map2){
		//map1:类-父类，实例-类
				//map2:类-实例组
		        
				File xmlFile = chooseDataSetListener.getTaxonomy();
				System.out.println(xmlFile.getPath()); 
				
				 if(xmlFile.exists()){  
				        SAXReader reader = new SAXReader();
				        System.out.println("exist");
			    
				        try {  
		    	                //读入文档流  
		    		                Document document = reader.read(xmlFile);  
		    		            //获取根节点  
		    		                Element root = document.getRootElement();
		    		                System.out.println(root.getName().toString());
		    		                int sumofCon=0;
		    		                int sumofIns=0;
		    		                for ( Iterator i = root.elementIterator("Class"); i.hasNext();) {
		    		                    Element foo = (Element) i.next();
		    		                      //System.out.print(foo.attributeValue("ID"));    		                      
		    		                      for ( Iterator j = foo.elementIterator("subClassOf"); j.hasNext();){
		    		                    	  Element foo1 = (Element) j.next();
		        		                      //System.out.println(foo1.attributeValue("resource"));
		    		                    	  if(map1.containsKey((foo.attributeValue("ID")))){
		        		                    	  System.out.println(foo.attributeValue("ID").toString());
		        		                      }
		        		                      map1.put(foo.attributeValue("ID"), foo1.attributeValue("resource"));
		        		                      sumofCon++;
		    		                      }
		    		                      
		    		                 }
		    		                for ( Iterator i = root.elementIterator("Thing"); i.hasNext();) {
		    		                    Element foo = (Element) i.next();
		    		                      //System.out.print(foo.attributeValue("ID"));
		    		                      for ( Iterator j = foo.elementIterator("type"); j.hasNext();){
		    		                    	  Element foo1 = (Element) j.next();
		        		                      //System.out.println(foo1.attributeValue("resource"));
		        		                      if(map1.containsKey((foo.attributeValue("ID")))){
		        		                    	  System.out.println(foo.attributeValue("ID").toString());
		        		                      }
		        		                      map1.put(foo.attributeValue("ID"), foo1.attributeValue("resource"));
		        		                      
		        		                      //得到一个Map(con****,list<instance>)
		        		                      if(map2.containsKey(foo1.attributeValue("resource"))){
		        		                    	  //已经有某个con的实例，所以把新的ins要加到list去
		        		                    	  List instances = (List)map2.get(foo1.attributeValue("resource"));
		        		                    	  instances.add(foo.attributeValue("ID"));
		        		                    	  map2.put( foo1.attributeValue("resource"), instances);
		        		                      }else{
		        		                    	  List instances  =  new ArrayList();
		        		                    	  instances.add(foo.attributeValue("ID"));
		        		                    	  map2.put( foo1.attributeValue("resource"), instances);
		        		                      }
		    		                      }
		    		                      sumofIns++;
		    		                 }
		    		                System.out.println("all con : " + sumofCon);
		    		                System.out.println("all inst : " + sumofIns);
		    		                System.out.println("end");    		             
		    		                System.out.println("test:"+map1.get("inst1814744254").toString());
		    		                System.out.println(map1.size());
		    		                System.out.println(map2.size());
		    		                
		    		                findInstances("inst1544797788",map1,map2);
		    		                
				        }catch(Exception ex){
				        	
				        }
			}
				
				 
		}

	/**
	 * 统计宽度--4种情况
	 * @param changedMap 
	 * @param changedMap 
	 */
	public void statisticOutputs(Map<String, List<WebService>> changedMap){
		statisticAvgGlobalOutputs(changedMap);
	    statisticAvgGlobalOutputsNoMatch(changedMap);
	    statisticAvgActualOutputs(changedMap);
	    statisticAvgActualOutputsNoMatch(changedMap);
	} 
	
	/**
	 * 统计某服务到终点服务的距离
	 */
	public void statisticWSToEndWSLengthMap(){
		wsToEndWSLengthMap.clear();
		
		//System.out.println("statisticWSToEndWSLengthMap() start.........");
		WebService now = null;
		wsToEndWSLengthMap.put(serviceMap.get("Request").getName(), 1);
		
		Queue<WebService> queue = new ArrayDeque<WebService>();
		queue.add(serviceMap.get("Request"));
		
		while(!queue.isEmpty()){
//			Object[] obs = queue.toArray();
//			for(int i = 0; i < obs.length; i++){
//				System.out.print( ((WebService)obs[i]).getName() + ",");
//			}
//			System.out.println();
			now = queue.poll();
			
			int childLength = (wsToEndWSLengthMap.get(now.getName()) == null)? 0:wsToEndWSLengthMap.get(now.getName());
			
			for(String input: (List<String>)now.getInputs()){
				WebService parent = RPT1.get(input);
				
				int parentLength = (wsToEndWSLengthMap.get(parent.getName()) == null)? 0:wsToEndWSLengthMap.get(parent.getName());
				
				wsToEndWSLengthMap.put(parent.getName(), parentLength + childLength);
				
				if(!parent.getName().equals("Provide") && !queue.contains(parent)){
					queue.add(parent);
				}
			}
			
		}
		
		double size = wsToEndWSLengthMap.size();
		double sum = 0;
		
		for(String name: wsToEndWSLengthMap.keySet()){
//			System.out.println(name + "<" + wsToEndWSLengthMap.get(name) + ">");
			sum += wsToEndWSLengthMap.get(name);
		}
		
		System.out.println("average distance to endWS: " + (sum/size));
		PrintStatisticResult.getInstance().print(sum/size + ",");
		//System.out.println("statisticWSToEndWSLengthMap() end!!!!!");
	}
	
	
	public boolean judgeNewOrDelete(Map<String, List<WebService>> changedMap, String wsName, String type){
		for (WebService ws : changedMap.get(type)) {
			if (ws.getName().equals(wsName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 统计实际拓扑中的平均宽度(算上可匹配)
	 * @param changedMap 
	 */

	public void statisticAvgActualOutputs(Map<String, List<WebService>> changedMap){
		Set<WebService> hasHandledWS = new HashSet<WebService>();
		
		double sum = 0;
		
		for(WebService ws : serviceMap.values()){
			
			if(ws.getCount() == 0){
				if(!hasHandledWS.add(ws)){
					continue;
				}
				
				Set<String> insts = new HashSet<String>();
				
				if(ws.getOutputs() != null){
					for(String inst : (List<String>)ws.getOutputs()){
						insts.add(inst);
						if(solveMap.get(inst) != null){
							insts.addAll(solveMap.get(inst));
						}
					}
				}
				
				sum += insts.size();
			}
		}
		
		if(changedMap != null){
			for(List<WebService> list : IIT.values()){
				for(WebService ws : list){
					
					if(ws.getCount() == 0){
						if(judgeNewOrDelete(changedMap, ws.getName(), "delete")){
							Iterator<WebService> iterator = hasHandledWS.iterator();
							
							while(iterator.hasNext()){
								WebService temp = iterator.next();
								if(temp.getName().equals(ws.getName())){
									iterator.remove();
									break;
								}
							}
							
							Set<String> insts = new HashSet<String>();
							
							if(ws.getOutputs() != null){
								for(String inst : (List<String>)ws.getOutputs()){
									insts.add(inst);
									if(solveMap.get(inst) != null){
										insts.addAll(solveMap.get(inst));
									}
								}
							}
							
							sum -= insts.size();
						}else{
							if(!hasHandledWS.add(ws)){
								continue;
							}
							
							Set<String> insts = new HashSet<String>();
							
							if(ws.getOutputs() != null){
								for(String inst : (List<String>)ws.getOutputs()){
									insts.add(inst);
									if(solveMap.get(inst) != null){
										insts.addAll(solveMap.get(inst));
									}
								}
							}
							
							sum += insts.size();
						}
					}
				}
			}
		}
		
		int size = hasHandledWS.size();
		
		System.out.println("average actual outputs:" + sum/size);
		PrintStatisticResult.getInstance().print(sum/size + ",");
	}
	/**
	 * 统计实际拓扑中的平均宽度(不算上可匹配)
	 * @param changedMap 
	 */
	public void statisticAvgActualOutputsNoMatch(Map<String, List<WebService>> changedMap){
		Set<WebService> hasHandledWS = new HashSet<WebService>();
		
		double sum = 0;
		
		for(WebService ws : serviceMap.values()){
			
			if(ws.getCount() == 0){
				if(!hasHandledWS.add(ws)){
					continue;
				}
				
				Set<String> insts = new HashSet<String>();
				
				if(ws.getOutputs() != null){
					insts.addAll(ws.getOutputs());
				}
				
				sum += insts.size();
			}
		}
		
		if(changedMap != null){
			for(List<WebService> list : IIT.values()){
				for(WebService ws : list){
					
					if(ws.getCount() == 0){
						if(judgeNewOrDelete(changedMap, ws.getName(), "delete")){
							Iterator<WebService> iterator = hasHandledWS.iterator();
							
							while(iterator.hasNext()){
								WebService temp = iterator.next();
								if(temp.getName().equals(ws.getName())){
									iterator.remove();
									break;
								}
							}
							
							Set<String> insts = new HashSet<String>();
							
							if(ws.getOutputs() != null){
								insts.addAll(ws.getOutputs());
							}
							
							sum -= insts.size();
						}else{
							if(!hasHandledWS.add(ws)){
								continue;
							}
							
							Set<String> insts = new HashSet<String>();
							
							if(ws.getOutputs() != null){
								insts.addAll(ws.getOutputs());
							}
							
							sum += insts.size();
						}
					}
				}
			}
		}
		
		int size = hasHandledWS.size();
		
		System.out.println("average actual outputs(no match):" + sum/size);
		PrintStatisticResult.getInstance().print(sum/size + ",");
	}
	/**
	 * 统计全局拓扑中的平均宽度(算上可匹配)
	 * @param changedMap 
	 */
	public void statisticAvgGlobalOutputs(Map<String, List<WebService>> changedMap){
		Set<WebService> hasHandledWS = new HashSet<WebService>();
		
		double sum = 0;
		
		for(WebService ws : serviceMap.values()){
			if(!hasHandledWS.add(ws)){
				continue;
			}
			
			Set<String> insts = new HashSet<String>();
			
			if(ws.getOutputs() != null){
				for(String inst : (List<String>)ws.getOutputs()){
					insts.add(inst);
					if(solveMap.get(inst) != null){
						insts.addAll(solveMap.get(inst));
					}
				}
			}
			
			sum += insts.size();
		}
		
		if(changedMap != null){
			for (List<WebService> list : IIT.values()) {
				for (WebService ws : list) {

					if(judgeNewOrDelete(changedMap, ws.getName(), "delete")){
						Iterator<WebService> iterator = hasHandledWS.iterator();
						
						while(iterator.hasNext()){
							WebService temp = iterator.next();
							if(temp.getName().equals(ws.getName())){
								iterator.remove();
								break;
							}
						}
						
						Set<String> insts = new HashSet<String>();

						if (ws.getOutputs() != null) {
							for (String inst : (List<String>) ws.getOutputs()) {
								insts.add(inst);
								if (solveMap.get(inst) != null) {
									insts.addAll(solveMap.get(inst));
								}
							}
						}
						
						sum -= insts.size();
					}else{
						if (!hasHandledWS.add(ws)) {
							continue;
						}

						Set<String> insts = new HashSet<String>();

						if (ws.getOutputs() != null) {
							for (String inst : (List<String>) ws.getOutputs()) {
								insts.add(inst);
								if (solveMap.get(inst) != null) {
									insts.addAll(solveMap.get(inst));
								}
							}
						}
						sum += insts.size();
					}
				}
			}
		}
		
		int size = hasHandledWS.size();
		
		System.out.println("average global outputs:" + sum/size);
		PrintStatisticResult.getInstance().print(sum/size + ",");
	}
	/**
	 * 统计全局拓扑中的平均宽度(不算上可匹配)
	 * @param changedMap 
	 */
	public void statisticAvgGlobalOutputsNoMatch(Map<String, List<WebService>> changedMap){
		Set<WebService> hasHandledWS = new HashSet<WebService>();
		
		double sum = 0;
		
		for(WebService ws : serviceMap.values()){
			if (!hasHandledWS.add(ws)) {
				continue;
			}
			
			Set<String> insts = new HashSet<String>();
			
			if(ws.getOutputs() != null){
				insts.addAll(ws.getOutputs());
			}
			
			sum += insts.size();
		}
		
		if(changedMap != null){
			for (List<WebService> list : IIT.values()) {
				for (WebService ws : list) {

					if(judgeNewOrDelete(changedMap, ws.getName(), "delete")){
						Iterator<WebService> iterator = hasHandledWS.iterator();
						
						while(iterator.hasNext()){
							WebService temp = iterator.next();
							if(temp.getName().equals(ws.getName())){
								iterator.remove();
								break;
							}
						}
						
						Set<String> insts = new HashSet<String>();

						if(ws.getOutputs() != null){
							insts.addAll(ws.getOutputs());
						}
						sum -= insts.size();
					}
					else{
						if (!hasHandledWS.add(ws)) {
							continue;
						}

						Set<String> insts = new HashSet<String>();

						if(ws.getOutputs() != null){
							insts.addAll(ws.getOutputs());
						}
						sum += insts.size();
					}
				}
			}
		}
		
		int size = hasHandledWS.size();
		
		System.out.println("average global outputs(no match):" + sum/size);
		PrintStatisticResult.getInstance().print(sum/size + ",");
	}

	public void statisticOneLevel(boolean reachFlag){
		ws2NextWSNumBeans.clear();
		rankMap.clear();
		
		for(WebService ws : serviceMap.values()){
			
			if(reachFlag){
				if(ws.getCount() != 0){
					continue;
				}
			}
			
			WS2NextWSNumBean ws2NextWSNumBean = new WS2NextWSNumBean();
			ws2NextWSNumBean.setWsName(ws.getName());
			
			//找出可以output的所有inst
			Set<String> insts = new HashSet<String>();
			
			if(ws.getOutputs() != null){
				for(String inst : (List<String>)ws.getOutputs()){
					insts.add(inst);
					if(solveMap.get(inst) != null){
						insts.addAll(solveMap.get(inst));
					}
				}
			}
			
			//找出所有可能的波及点
			Set<WebService> webServices = new HashSet<WebService>();
			
			Iterator<String> iterator = insts.iterator();
			
			while(iterator.hasNext()){
				String inst = iterator.next();
				
				if(IIT.get(inst) != null){
					if(reachFlag){
						for(WebService temp : IIT.get(inst)){
							if(temp.getCount() == 0 && temp != ws){
								webServices.add(temp);
							}
						}
					}
					else{
						webServices.addAll(IIT.get(inst));
					}
				}
			}
			
			//找到了
			ws2NextWSNumBean.setNextWSNum(webServices.size());
			
			ws2NextWSNumBeans.add(ws2NextWSNumBean);
			rankMap.put(ws2NextWSNumBean.getWsName(), ws2NextWSNumBean.getNextWSNum());
		}
		//排序
		Collections.sort(ws2NextWSNumBeans);
		Collections.reverse(ws2NextWSNumBeans);
		
		if(reachFlag){
			PrintRankResult.getInstance().print("前20排名" + System.lineSeparator());
		}
		else{
			PrintRankResult.getInstance().print("全局" + System.lineSeparator());
		}
		
		//输出前全部到文件
		for(int i = 0; i < ws2NextWSNumBeans.size(); i++){
			PrintRankResult.getInstance().print
					((i + 1) + "," +  ws2NextWSNumBeans.get(i).toString() + System.lineSeparator());
		}
		
	}
}
