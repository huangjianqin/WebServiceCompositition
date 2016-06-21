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
				//HJQ_��������
	private Map<String,String> map1;//inst/con->con
	private Map<String,List<String>> map2;//inst->insts
				//HJQ_  ����name->����
	private Map<String,WebService> serviceMap;
	private Map<String,WebService> RPT;
				//����������ĳ��inst��Ϊinput��webservice�б�
	private Map<String,List<WebService>> IIT;
	private List<String> inputsFromChallenge;
	private List<String> outputsFromChallenge;
	
	private Map<String,WebService> enabledServices;
				//HJQ_inst --> list<WebSerivce>  �����inst��list<webService>
	private Map<String,List<WebService>> OT;
				//HJQ_inst --> list<inst> inst����ƥ���inst�б�
	private Map<String,List<String>> solveMap;
	private double continousQos;
	private double requeryQos;
				//HJQ_�����Ͼ������еķ���
	private List<WebService> testServiceList; 
	private int count;
	private Map<String,WebService> RPT1;
				//HJQ_�ı�ķ��������
	private int sumOfChangedService;
	private int successTime;
				//HJQ_��¼������
	private String recordBackWard;
	private String recordSomething;
	private int realSum;//ʵ�ʿ����õ�ws����
	private int sumOfCanUseWS;//���ɴ��ws,��count=0
	
	//Ϊ�˲���10����ͬ�����ݶ����Ƶ����ݽṹ
				//HJQ_serviceMap�ĸ���
	private Map<String,WebService> serviceMap1;
				//HJQ_RPT1�ĸ���
 	private Map<String,WebService> RPT2;
	
	private byte[] buff;
	private FileOutputStream out1;
				//HJQ_��¼����1����ʵ��ĺ�ʱ
	private StringBuilder recordTime1;
				//HJQ_��¼����2����ʵ��ĺ�ʱ
	private StringBuilder recordTime2;
				//HJQ_��¼�ز�����ʵ��ĺ�ʱ
	private StringBuilder recordTime3;
				//HJQ_��¼������ѯ����ʵ��ĺ�ʱ
	private StringBuilder recordTime4;
				//HJQ_��¼����ʽ����ʵ��D��Ϣ
	private StringBuilder recordTime5;
				//HJQ_�Ա�������ѯ���ز�,1Ϊ������ѯ����,0Ϊ����Ч��һ��,-1Ϊ�ز����
	private StringBuilder recordCompare;
				//HJQ_��¼��ʵ�仯�ķ�������
	private StringBuilder recordRealSum;
	
	//ͳ�������ز�ʱ����ܺ�
	private double requeryTime;
	//ͳ������������ѯʱ����ܺ�
	private double continueTime;
	//ͳ�����в���1ʱ����ܺ�
	private double strategy1Time;
	//ͳ�����в���2ʱ����ܺ�
	private double strategy2Time;
	//ͳ��������Ѳ���ʱ����ܺ�
	private double bestStrategyTime;
	
	//ͳ��ʵ��Dʤ��,ƽ��---ÿ��
	private long winCount = 0;
	private long equalCount = 0;
	
	//ͳ��100��ʵ��D��ʤ��,ƽ��--ʤ��>����Ϊʤ
	private long allCount;
	private long allWinCount = 0;
	private long allEqualCount = 0; 
	
	//ͳ��PQ�����ֵ
	private int PQMaxSize = 0;
	
	//����1��ֵ
	private int strategy1Value = 10;
	//����2��ֵ
	private int strategy2Value = 500;

	//����1��ֵ
	private int strategy1ValueWithAD = 11;
	//����2��ֵ
	private int strategy2ValueWithAD = 700;
	
	//��ֹ�л�
	private boolean dead;
	//��ͼ��
	private String typePath = "before";
	
	//ͳ��ĳ�������յ����ľ�����
	private Map<String, Integer> wsToEndWSLengthMap = new HashMap<String, Integer>();
	
	//ͳ�Ʒ�����һ��Შ���ķ�����
	private Map<String, Integer> rankMap = new HashMap<String, Integer>();
	private List<WS2NextWSNumBean> ws2NextWSNumBeans = new ArrayList<WS2NextWSNumBean>();
	private List<ExperimentRankBean> tempExperimentRank = new ArrayList<ExperimentRankBean>();
	private List<Map<String, List<WebService>>> tempChanged;
	private int fileNum = 1;
	private int runTimes = 0;
	
	
	public MyFrame() throws FileNotFoundException {
		initialize();
		 map1 = new HashMap();//map1:��-���࣬ʵ��-��
		 map2 = new HashMap();//map2:��-ʵ����
		 serviceMap = new HashMap<String,WebService>();
		 RPT = new HashMap<String,WebService>();//Ԫ�غ��ĸ������ṩ
		 IIT = new HashMap<String,List<WebService>>();//inst***(input)-List
	     inputsFromChallenge = new ArrayList<String>();
	     outputsFromChallenge = new ArrayList<String>();
	     enabledServices = new HashMap();
	     OT =  new HashMap<String,List<WebService>>();//��ǰ���ṩĳ��inst�ķ���list
	     solveMap = new HashMap<String,List<String>>();//inst-�ܽ����instList;
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
		frame = new JFrame("������϶�̬���о�");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		chooseDataSetListener = new ChooseDataSetListener();
		
		JButton btnNewButton = new JButton("ѡ����Լ�");
		btnNewButton.addActionListener(chooseDataSetListener);
		btnNewButton.setBounds(10, 22, 102, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnqos = new JButton("����qos");
		btnqos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//String  str1 = JOptionPane.showInputDialog("���Զ����飿");
				//if(str1!=null){
					String  str2 = "";
					//String  str2 = JOptionPane.showInputDialog("������ٸ������qos��");
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
		
		JButton button = new JButton("���ӷ���");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button.setBounds(10, 140, 93, 23);
		frame.getContentPane().add(button);
		
		JButton button_1 = new JButton("ɾ������");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button_1.setBounds(10, 181, 93, 23);
		frame.getContentPane().add(button_1);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(139, 10, 285, 252);
		frame.getContentPane().add(textArea);
		
		JButton btnNewButton_1 = new JButton("������ϲ���");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String  str1 = JOptionPane.showInputDialog("���Զ����飿");
				if(str1!=null){					
						count=Integer.parseInt(str1);						
						try {
//							testManyTime1ForWin();
							testManyTime1ForWinHJQ();
							
							
//							//��¼10��ʵ����������
//							PrintRankResult.getInstance().print("ǰ����������ĳ��ʵ���ǰ3�ķ��񼰲�������,�˴α仯��������ǰ20����Ŀ,PQ����");
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
			    System.out.println("�ɴ�ķ�����:"+sumOfCanUseWS);
			    backward(outputsFromChallenge,RPT,serviceMap);
			    
			    //ͳ��ÿ������Ʋ���������������
			    //statisticOneLevel(false);
			    statisticOneLevel(true);
			    
//			    //ͳ��ĳ�������յ�ľ�����
//			    statisticWSToEndWSLengthMap();
//			    statisticOutputs(null);
//			    PrintStatisticResult.getInstance().print(0 + ",");
//				PrintStatisticResult.getInstance().print(0 + ",");
//				PrintStatisticResult.getInstance().print(0 + System.lineSeparator());
//			    
//			    //��ͼ��
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
//			    //��ͼ��,���qos�仯����
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
	
	//HJQ_һ�β�ѯsim_dijkstra �в���,������ֵ����������ѯ���ز� HJQ��
	public double strategy(int choice,int sum,int realSum,List<WebService> qosChangedList){
		
		if(choice==1){
			if(realSum<=this.strategy1Value){
				//long startMili=System.currentTimeMillis();
				
				continuousQuery4(qosChangedList);
				
				//long endMili=System.currentTimeMillis();
				//System.out.println("continuousQuery���� :"+endMili);
				//System.out.println("myStratege�ܺ�ʱΪ:"+(endMili-startMili)+"����");	
				
				//System.out.println("������ѯqos:" + continousQos);
				return 0;
			}	
		}else{
			if(sum<=this.strategy2Value){
				//long startMili=System.currentTimeMillis();
				
				continuousQuery4(qosChangedList);
				
				//long endMili=System.currentTimeMillis();
				//System.out.println("continuousQuery���� :"+endMili);
				//System.out.println("myStratege�ܺ�ʱΪ:"+(endMili-startMili)+"����");	
				
				//System.out.println("������ѯqos:" + continousQos);
				return 0;
			}	
		}
		
		//����ͳ�ƻָ����Ƶ�ʱ��
		double recoveryTimeBegin = 0;//�ָ����ƿ�ʼ
		double recoveryTimeEnd = 0;//�ָ����ƽ���
		
		//�ָ����ƽ���
		recoveryTimeBegin = System.currentTimeMillis();   
		
		int sumOfRequest = outputsFromChallenge.size();
		WebService begin = serviceMap.get("Provide");
		WebService end = serviceMap.get("Request");
		end.setCount(end.getInputs().size());
		List<WebService> reachVertices = new ArrayList<WebService>();
		
		//�ָ����ݽṹ
		recoveryReQueryForHJQ();
		
        RPT.clear();
        
        //long startMili=System.currentTimeMillis();
        
        
		boolean find=false;
		reachVertices.add(begin);
		
		//�ָ����ƽ���
		recoveryTimeEnd = System.currentTimeMillis();
		
		while(reachVertices.size()!=0){
			Collections.sort(reachVertices);
			
			WebService v = reachVertices.remove(0);
			for(Object output:v.getOutputs()){
															
				if(!RPT.containsKey(output.toString())){//�µ�inst
					//RPT.put(output.toString(),v);
					//System.out.println("�µ�inst:"+output.toString());
					//String con = map1.get(output).toString();
					List<String> matchList = solveMap.get(output.toString());//�����ܱ�output�����inst						
					if(matchList==null){
					 matchList = findInstances(output.toString(),map1,map2);
					 //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^&&&");
					}
					
					
					if(!output.toString().equals(matchList.get(0)))//ʹmatchList���A1-A2-A3-A4
						for(int i=1;i<matchList.size();i++){
							if(output.toString().equals(matchList.get(i))){
								String st = matchList.get(0);
								matchList.set(0, matchList.get(i));
								matchList.set(i, st);
								break;
							}									
						}
										
					//System.out.println("matchList�Ĵ�С��"+matchList.size());
					boolean firstPlace=true;
					for(String inst : matchList){
//						if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//							System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						//if(!firstPlace && RPT.containsKey(inst)) continue;
						
						if(!RPT.containsKey(inst)){
							 RPT.put(inst, v);
							 //System.out.println("�µ�inst*:"+inst);
						 }else{
							 continue;
						 }
						
						firstPlace=false;
						 List<WebService> haveSameInstWebServiceList = IIT.get(inst);
						 if(haveSameInstWebServiceList==null){
							 //System.out.println("û���κη�����Ҫ��"+inst);
							 continue;
						 }
//						 if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//								System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						 
						 //System.out.println("��"+haveSameInstWebServiceList.size()+"��������Ҫ"+inst);
						 for(WebService w: haveSameInstWebServiceList){						
							 w.subCount();
							 //if(w.getName().equals("Request")) System.out.println("��ʣ"+end.getCount()+"��Ԫ��");
							 
							 if(w.getCount()==0){
								 w.setAllResponseTime(v.getAllResponseTime()+w.getSelfResponseTime());									
								 //enabledServices.put(w.getName(), w);
								 if(w.getName().equals("Request"))
									 if(end.getCount()==0){
										 //System.out.println("�ؼ�����"+v.getName());
										 //System.out.println("�ҵ���ȫ��Ҫ���Ԫ��");
										 //System.out.println(end.getAllResponseTime());
										 find = true;
										 //return;
										 continue;
										 //break;
									 }
									 
								 reachVertices.add(w);
									 //System.out.println("����"+w.getName()+"������");
									// System.out.println("����"+w.getName()+"��AllRequestTime:"+w.getAllResponseTime());
									 /////////serv1027640201 serv404388610  404388610
							
								 
							 }
						 }
						 
					}
					
				}
			}
		}
		
		//System.out.println("RPT.size()"+RPT.size());
		
		//long endMili=System.currentTimeMillis();
		//System.out.println("continuousQuery���� :"+endMili);
		//System.out.println("myStratege�ܺ�ʱΪ:"+(endMili-startMili)+"����");
		
//		
//		if(!find)
//			System.out.println("�Ҳ���");
//		else
//			System.out.println("���Ž��qos:"+end.getAllResponseTime());
		//System.out.println("�ز�qos:" + serviceMap.get("Request").getAllResponseTime());
		
		continousQos = serviceMap.get("Request").getAllResponseTime();		
		
		return recoveryTimeEnd - recoveryTimeBegin;
	}
	
	public boolean isCriticalNode(WebService parent,WebService successor){
		double max=0;
		WebService ws=null;
		for(int i=0;i<successor.getInputs().size();i++){
			ws = RPT.get(successor.getInputs().get(i));
			if( ws == parent ){
				//System.out.println("����֤�����жϹ�"+parent.getName()+"i="+i);				
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
	 * �ҳ��ܹ��ṩinst��qos��õķ���
	 * @param successor
	 * @return
	 */
	public WebService getCriticalParent(WebService successor){
		Set<WebService> parentSet = new HashSet<WebService>();
		WebService w = null;
		List<WebService> parentList  = new ArrayList<WebService>();
		
		for(int i=0;i<successor.getInputs().size();i++){
			 w = RPT1.get(successor.getInputs().get(i));
			 //HJQ_��
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
	 * �ܽ���qos�仯��ʵ��BC
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
				System.out.println("��" + time + "��");
			}
			if(time==1){
				recordTime1.append("continuousQuery����ʱ"+System.getProperty("line.separator"));
				recordTime2.append("reQuery����ʱ"+System.getProperty("line.separator"));
				recordCompare.append("�Ƚ�"+System.getProperty("line.separator"));
				recordRealSum.append("ʵ����Ҫ���µķ������"+System.getProperty("line.separator"));
			}
			//HJQ randomTest();
			randomTest(solveMapOfCopy, OTOfCopy, RPT1OfCopy);
		}
		
		//cjh add
		System.out.println("�ز�ʱ��:"+requeryTime/count);
		System.out.println("������ѯʱ��:"+continueTime/count+System.lineSeparator());
		
		sb.append((requeryTime / count) + "," + (continueTime / count) + System.lineSeparator());
	}
	/**
	 * �Զ���ɽ���qos�仯��ʵ��BC
	 * @throws IOException
	 */
	public void testManyTimeAuto() throws IOException{
		// ����solveMap
		Map<String, List<String>> solveMapOfCopy = DataGetter.copyStrToStrList(solveMap);

		// HJQ,����OT
		Map<String, List<WebService>> OTOfCopy = null;

		// HJQ,����PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);
		
		//����RPT1��serviceMap
		copy();
		
		String type = "B";
		
		int[] testGroupB = new int[]{50,100,150,200,250,300,350,400,450,500,550,600,650,700,750,800};
		int[] testGroupC = new int[]{2,4,6,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
		
		//��¼����ʵ��ʱ��
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < testGroupB.length; i++){
			System.out.print("ʵ��" + type + ":");
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
	 * �ֶ�������������ٵ����ݼ�
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
		
		//����ʼ��
		List<String> outputs = testServiceList.get(0).getOutputs();
		
		for(String inst: outputs){
			this.inputsFromChallenge.add(inst);
		}
		
		//�����յ�
		List<String> inputs = testServiceList.get(testServiceList.size()-1).getInputs();
		
		for(String inst: inputs){
			this.outputsFromChallenge.add(inst);
		}
		
		//���IIT
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
		
		//���map1
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
		
		//���map2
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
		//���serviceMap
		for(WebService webService : testServiceList){
			serviceMap.put(webService.getName(), webService);
		}
	}
	/**
	 * �ֶ�������������
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
		
		//���serviceMap
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
	 * ���ڲ���
	 * @throws FileNotFoundException
	 * @throws CloneNotSupportedException
	 */
	public void run() throws FileNotFoundException, CloneNotSupportedException {
		/*
		 * //�ļ������·��� 
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
		
		// ���빹���·���
		Change change = new Change(testServiceList,
				DataGetter.getServicesAllInst(testServiceList, "all"),
				DataGetter.getServicesAllInst(testServiceList, "all"),
				(int) StatInfo.instAverage(testServiceList, "input"),
				(int) StatInfo.instAverage(testServiceList, "output"), 30);
		
		Map<String, List<WebService>> changedMap = change.changeWebServices(9 - 2,4,3,2);
		
		System.out.println("----------------------------�����仯�ķ���------------------------------------");	
		System.out.println("-------------------------------ɾ��----------------------------------------");
		
		List<WebService> webServicesOfDelete = changedMap.get("delete");
		
		for(WebService webService : webServicesOfDelete){
			System.out.println(webService);
		}
		
		System.out.println("-------------------------------����----------------------------------------");
		
		List<WebService> webServicesOfAdd = changedMap.get("new");
		
		for(WebService webService : webServicesOfAdd){
			serviceMap.put(webService.getName(), webService);
			System.out.println(webService);
		}
		
		System.out.println("------------------------------qos�仯---------------------------------------");
		
		List<WebService> webServicesOfQOSChange = changedMap.get("qosChange");
		
		for(WebService webService : webServicesOfQOSChange){
			System.out.println(webService);
		}
		
		adapt(changedMap);
	}

	/**
	 * ����ɾ����Ԥ����
	 * @param changedMap
	 * @return
	 */
	public List<WebService> adapt(Map<String, List<WebService>> changedMap){
		//��Ҫ����������ѯ����ķ����б�
		List<WebService> needCQList = new ArrayList<WebService>();
		
		//���ɾ��
		needCQList.addAll(adaptDeleteWS(changedMap.get("delete")));
		//�������
		needCQList.addAll(adaptInsertWS(changedMap.get("new")));
		//���qos�ı�
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
		
		
		System.out.println("continuous���Ž���:"+continousQos);
		System.out.println("requeryQos���Ž���:"+requeryQos);
		
		return needCQList;
	}
	/**
	 * ������Ԥ����
	 * @param insertedList
	 * @return
	 */
	private List<WebService> adaptInsertWS(List<WebService> insertedList) {
		//��Ҫ����������ѯ����ķ����б�
		List<WebService> needCQList = new ArrayList<WebService>();
		
		//�����RPT1,������inst
		List<String > newInstances = new ArrayList<String>();
		
		//������ӽڵ�,����IIT,OT,solvedmap,RPT
		for(WebService webService: insertedList){
			
			List<String> inputs = webService.getInputs();
			
			for(String input: inputs){
				//����IIT
				if(IIT.containsKey(input)){
					IIT.get(input).add(webService);
				}
				else{
					List<WebService> webServices = new ArrayList<WebService>();
					
					webServices.add(webService);
					
					IIT.put(input, webServices);
				}
			}
			
			//��־�Ƿ�ɴ�
			boolean flag = isReachable(webService);
			
			//�״οɴ�
			//�״οɴ�Ҫ����qos,��Ȼ���洦����
			if(flag){
				WebService criticalParent = getCriticalParent(webService);
				
				webService.setNewAllResponseTime(criticalParent.getAllResponseTime() + webService.getSelfResponseTime());
				
				needCQList.add(webService);
				
				//outputs��Ԥ����
				outputsHandler(webService, newInstances);
			}
		}
		
		//���������ڵ��������inst���(RPTԭ��û��),��Щinst���ܵ��¸���ɴ�����RPT
		while(newInstances.size() != 0){
			String newInstance = newInstances.remove(0);
			
			//RPT1����inst������ķ���
			List<WebService> webServices = IIT.get(newInstance);
			
			//���ܲ�����inputs��matchedInst�ķ���
			if(webServices != null){
				//��qosС�Ŀ�ʼ����
				Collections.sort(webServices);
				
				for(WebService webService: webServices){
					//ѡȡ���ɴ��
					if(webService.getCount() > 0){
						webService.subCount();
						// ���¿ɴ�
						if (webService.getCount() == 0) {
							WebService criticalParent = getCriticalParent(webService);
							//�����һЩ����Ϊ�ǿɴ��ķ����ų�����
							if(criticalParent != null){
								webService.setNewAllResponseTime(
										criticalParent.getAllResponseTime() + webService.getSelfResponseTime());
								// ����ɴ�����outputs
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
	 * ����ɴ��������inst
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
		
		//����outputs inst,ͬʱҪ�������ǵĿ�ƥ��inst
		for (String output : outputs) {
			List<String> matchedListOfOutputs = solveMap.get(output);		

			// ����solveMap
			if (matchedListOfOutputs == null) {
				matchedListOfOutputs = findInstances(output, map1, map2);
				
				solveMap.put(output, matchedListOfOutputs);
			}
			
			for (String matchedInstOfOutput : matchedListOfOutputs) {
				
				// ����OT
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

				// ����RPT1
				if (!RPT1.containsKey(matchedInstOfOutput)) {
					// ����ö���һ�������inst(�����RPT1)
					RPT1.put(matchedInstOfOutput, webService);

					newInstances.add(matchedInstOfOutput);

				}
			}
		}
	}

	/**
	 * �ж�ĳ�����Ƿ�ɴ�
	 * inputs��ĳ���������
	 * @param inputs
	 * @return
	 */
	private boolean isReachable(WebService webService) {
		List<String> inputs = webService.getInputs();
		
		boolean flag = true;
		//�����������������input�����п�ƥ��inst����RPT1�о��ǿɴ�
		for (String input : inputs) {
			// ��������һ��input,count--
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
	 * ɾ�������Ԥ����
	 * @param deletedList
	 */
	private List<WebService> adaptDeleteWS(List<WebService> deletedList) {

		List<WebService> copyDel = new ArrayList();
		for (WebService del : deletedList) {
			copyDel.add(del);
		}

		List<WebService> needUpdate = new ArrayList<WebService>();
		for (WebService ws : deletedList) {
			// ��IIT������-ĳ������ɾ��ws��صĶ���
			for (String input : (List<String>) ws.getInputs()) {
				List<WebService> les = IIT.get(input);// ����input��list
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
				// ɾ����ws�������صĶ���
				for (String output : (List<String>) ws.getOutputs()) {
					// OT:���-ĳ����
					List<WebService> les = OT.get(output);
					
					if(les != null){
						les.remove(ws);// ���ṩoutput��list
					}
					else{
						continue;
					}

					if (les.isEmpty()) {
						// �����ʾɾ���������ṩĳ��output�ķ�������k���ܻ����һЩ�����ʧЧ
						OT.remove(output);
						// OT��û�п������ĳoutput�ķ����������solveMap��ҲҪɾ��output��һ��
						//this.solveMap.remove(output);
						// ����������output�������ķ���
						if (IIT.get(output) != null) {
							for (WebService loss : IIT.get(output)) {
								if (loss.getCount() == 0) {// �����ɵ��ģ���������ĳһ��������ˣ������䲻�ɴ�
									if (!copyDel.contains(loss)) {
										deletedList.add(loss);
										copyDel.add(loss);
									}
								}
								loss.setCount(loss.getCount() + 1);// ���ڿɴ���������٣��ʽ���count+1
							}
						}
					} else {
						// ��les�л��з���ʱ����Ҫ�ж��Ƿ���Ҫ����QOS
						Collections.sort(les);
						double outputQos = les.get(0).getAllResponseTime();
						if (outputQos > ws.getAllResponseTime()) {// ������һ�������QOS

							List<WebService> subs = IIT.get(output);// ����output��list

							if (subs != null) {
								for (WebService sub : subs) {
									if (sub.getCount() == 0) {// ֻ����output����Ŀɴ�����
										double newQos = outputQos;
										for (String in : (List<String>) sub.getInputs()) {
											List<WebService> ps = OT.get(in);// ��ÿ������ĸ���
											Collections.sort(ps);// Ŀ����ȡ������Qos���ٵ�Qos
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

					// ��RPT�и�����Ӧ��inst
					for (String match : solveMap.get(output)) {
						WebService w = RPT1.get(match);
						// ȡ��RPT����inst��Ӧ�ķ���w���w==ws��ô˵���÷����Ѿ���ɾ������Ҫ��һ��������������
						if (ws.equals(w)) {
							RPT1.remove(match);
							for (String aa : solveMap.get(match)) {
								List<WebService> updates = OT.get(aa);

								// �ɹ��ҵ�����ķ��񣬽������RPT
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
				//����IIT
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
				List<WebService> les = IIT.get(input);// ����input��list
				les.remove(webService);
				if (les.isEmpty()) {
					IIT.remove(input);
				}
			}
		}
	}
	
	/**
	 * ���Զ��ʵ��,ͨ��Ӳ����ʵ�ֲ�5000��
	 * @param type ��ָʵ�������
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
		
		//����ʵ��������ѡ��ı����,�������ȫ�������ǽ�����ʵ������ṹ�еķ���
		List<WebService> testServiceList = new ArrayList<WebService>();
		
		int time = 0;
		while (time < count) {
			this.dead = false;
			time++;
			if(time%1000 == 0){
				System.out.println("��" + time + "��");
			}
			if (time == 1) {
				recordTime1.append("continuousQuery����ʱ" + System.getProperty("line.separator"));
				recordTime2.append("reQuery����ʱ" + System.getProperty("line.separator"));
				recordCompare.append("�Ƚ�" + System.getProperty("line.separator"));
				recordRealSum.append("ʵ����Ҫ���µķ������" + System.getProperty("line.separator"));
			}

			// �ָ�testServiceList,��������仯������Ҫ
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
		System.out.println("�ز�ʱ��:" + requeryTime / count);
		System.out.println("������ѯʱ��:" + continueTime / count);

		sb.append((requeryTime / count) + "," + (continueTime / count) + System.lineSeparator());
	}
	
	/**
	 * �Զ�������ɾ���仯��ʵ��BC
	 * @throws IOException 
	 */
	public void testManyTimeForHJQAuto(String type) throws IOException{
		// HJQ,����OT,IIT��RPT1
		// HJQ,����OT
		Map<String, List<WebService>> OTOfCopy = null;

		try {
			OTOfCopy = DataGetter.copyStrToWsList(OT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// HJQ,����IIT
		Map<String, List<WebService>> IITOfCopy = null;

		try {
			IITOfCopy = DataGetter.copyStrToWsList(IIT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		// HJQ,����PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);

		// ����RPT1��serviceMap
		copy();
		
		//����inst
		List<String> allInsts = new ArrayList<String>();
		for(String instAndCon: map1.keySet()){
			if(instAndCon.contains("inst")){
				allInsts.add(instAndCon);
			}
		}
		
		//����RPT1�е�inst,����˵������ɵ��·���϶���Ӱ�쵱ǰ����ṹ
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
		
		//��¼����ʵ��ʱ��
		StringBuilder sb = new StringBuilder();
		//��ʵ��Bͬʱ��ʵ��C----��¼ʵ��C�е�ʵ��B���
		//��ʵ��Bͬʱ��ʵ��C----��¼ʵ��C�е�ʵ��C���
		Map<Integer, List<Double>> experimentCC = new HashMap<Integer, List<Double>>();
		Map<Integer, List<Double>> experimentCR = new HashMap<Integer, List<Double>>();
		
		//ͳ�ƻ���PQ�������㷨�Աȵó�PQ��ص���ֵ
		Map<Integer, List<Double[]>> experimentPQ = new HashMap<Integer, List<Double[]>>();
		
		for(int i = 0; i < testGroup.length; i++){
			System.out.print("ʵ��" + type + ":");
			System.out.println(testGroup[i]);
			
			this.sumOfChangedService = testGroup[i];
			testManyTimeForHJQ(type, sb, OTOfCopy, IITOfCopy, RPT1OfCopy, allInsts, realInsts, experimentCC, experimentCR,experimentPQ);
		}
		
		out1 = new FileOutputStream("result" + type + ".csv");
		out1.write(sb.toString().getBytes());
		out1.close();
		
		//��¼ʵ��C�е�ʵ����Ϣ
		StringBuilder sc = new StringBuilder();
		
		//����ʵ��C��������ѯʵ����-----------------------------------------------------------------
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
		
		//��ʼ��
		sc = new StringBuilder();

		// ����ʵ��C���ز�ʵ����-----------------------------------------------------------------
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

		// ����ʵ��C��������ѯʵ����-----------------------------------------------------------------
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
	 * һ��ʵ��,ɾ��������qos�ı�����һ���ϵĲ���
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
		// HJQ_���б仯�ķ���
		List<WebService> qosChangedList = new ArrayList<WebService>();
		// HJQ_qosChangedList�ĸ���,��qos�����仯�ķ���
		List<WebService> copyChangedList = new ArrayList<WebService>();
		
		//���ݲ�ͬ��ʵ��ȡ��ͬ��inst��
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
		
		//����
		int typeNumber = 5;
		
		//1:1:3
		int sum = sumOfChangedService;
		int deletedWSSum = sum / typeNumber;
		int addWSSum = sum / typeNumber;
		int changedWSSum = changedWSSum = (sum % typeNumber == 0) ? (sum / typeNumber * 3) : (sum / typeNumber * 3) + sum % typeNumber;
		
		Map<String, List<WebService>> changedMap = change.changeWebServices(sum, addWSSum, changedWSSum,
				deletedWSSum);
		
		// ����copyChangedList
		for (WebService webService : changedMap.get("qosChange")) {
			WebService newWS = null;
			try {
				newWS = (WebService) webService.clone();
			} catch (CloneNotSupportedException e) { 
				e.printStackTrace();
			}
			copyChangedList.add(newWS);
		}
		
		//���Ʒ����仯�ķ���
		Map<String, List<WebService>> changedMapOfCopy = copyChangedMap(changedMap);
		
		//ͳ��ʵ�ʵ�����
		//��ʵ��Bͬʱͳ��ʵ��C
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
			//Ĭ����ʵ�ʵ�
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
		double t1;// ������ѯ��ʱ��
		double t2;// �ز��ʱ��

		// ���潫�Ա仯��ķ����зֱ����������ѯ���ز�,�Ƚϻ��ѵ�ʱ��
		
		/*---------------------------------continuequery--------------------------------*/
		
		for (int i = 0; i < 10; i++) {// ���10��
			//ͳ��PQ�ĳ���
			this.PQMaxSize=0;
			
			// ��RPT1��serviceMap1
			recoveryForHJQ(RPT1OfCopy);//ServiceMap���з�����Ϊ���ֵ,RPT1Ҳ��
			recoveryWithNewQosForHJQ(copyChangedList);//�ı�Ԥ�������qosֵ,��Ϊ��һ�д�����Ϊ���ֵ
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			recoveryIITForCJH(IITOfCopy);
			recoveryOTForHJQ(OTOfCopy);
			
			startMili = System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����
			
			qosChangedList.clear();
			//����ɾ����Ԥ����
			// ���ɾ��
			qosChangedList.addAll(adaptDeleteWS(addAndDeleteMap.get("delete")));
			// �������
			qosChangedList.addAll(adaptInsertWS(addAndDeleteMap.get("new")));
			// ���qos�ı�
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

		//*System.out.println("continuousQueryTotal�ܺ�ʱΪ:" + totalTime / 8 + "����");

		recordTime1.append(totalTime / 8 + System.getProperty("line.separator"));

		//*System.out.println("continuous���Ž���:" + continousQos);
		// cjh add
		continueTime += t1;
		
		/*---------------------------------requery--------------------------------*/
		
		totalTime = 0;
		timeList.clear();
		
		for (int i = 0; i < 10; i++) {
			recoveryReQueryForHJQ();
			recoveryIITForCJH(IITOfCopy);
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			
			startMili = System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����
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
		// ��¼ʱ���С
		if (t1 < t2) {
			recordCompare.append(1 + System.getProperty("line.separator"));
		} else if (t1 == t2)
			recordCompare.append(0 + System.getProperty("line.separator"));
		else
			recordCompare.append(-1 + System.getProperty("line.separator"));

		//*System.out.println("���Ž��qos:" + requeryQos);

		recordTime2.append(totalTime / 8 + System.getProperty("line.separator"));

		//*System.out.println("reQueryTotal�ܺ�ʱΪ:" + totalTime / 8 + "����");

		if (requeryQos == continousQos) {
			//*System.out.println("ǰ����ȷ");
		} else {
			//*System.out.println("ǰ��bubububububu��ȷ");
		}

		// ��¼ʵ����Ҫ���µ�ws(count!=0):�ĸ���
		recordRealSum.append(realSum + System.getProperty("line.separator"));
		//*System.out.println("ʵ����Ҫ���µ�ws(count!=0):�ĸ�����" + realSum);
		
		
		//��ʵ��Bͬʱͳ��ʵ��C��������ѯʱ��
		if(experimentCC.get(this.realSum) == null){
			List<Double> results = new ArrayList<Double>();
			results.add(t1);
			experimentCC.put(this.realSum, results);
		}
		else{
			experimentCC.get(this.realSum).add(t1);
		}
		
		// ��ʵ��Bͬʱͳ��ʵ��C���ز�ʱ��
		if (experimentCR.get(this.realSum) == null) {
			List<Double> results = new ArrayList<Double>();
			results.add(t2);
			experimentCR.put(this.realSum, results);
		} else {
			experimentCR.get(this.realSum).add(t2);
		}
		
		//ͳ�ƻ���PQ�������㷨�Աȵó�PQ��ص���ֵ
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

	//HJQ_ ��copyChangedList�еķ������qos ���ǵ�serviceMap��Ӧ�����qos
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
	
	//HJQ_ ��serviceMap1 �ָ��� serviceMap
	public void recoveryForHJQ(Map<String, WebService> RPT1OfCopy){
		//HJQ_ ��serviceMap1 �ָ��� serviceMap
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
	 * ����ɾ����ʵ��D��ÿ��ʵ��
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
		
		// HJQ_���б仯�ķ���
		List<WebService> qosChangedList = new ArrayList<WebService>();
		// HJQ_qosChangedList�ĸ���,��qos�����仯�ķ���
		List<WebService> copyChangedList = new ArrayList<WebService>();
		
		Change change = new Change(testServiceList, allInsts, allInsts, 
				(int) StatInfo.instAverage(testServiceList, "input"),
				(int) StatInfo.instAverage(testServiceList, "output"), 30);
		
		// ����
		int typeNumber = 5;
		//��������仯������,<1000
		
		Random random = new Random();
		
		int sum = random.nextInt(1000);

		// 1:1:3
		int deletedWSSum = sum / typeNumber;
		int addWSSum = sum / typeNumber;
		int changedWSSum = changedWSSum = (sum % typeNumber == 0) ? (sum / typeNumber * 3) : (sum / typeNumber * 3) + sum % typeNumber;

		Map<String, List<WebService>> changedMap
		= change.changeWebServices(sum, addWSSum, changedWSSum,
				deletedWSSum);
		
//		//�������¶�ȡ��ǰ�ı仯���
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
		// ����copyChangedList
		for (WebService webService : changedMap.get("qosChange")) {
			WebService newWS = null;
			try {
				newWS = (WebService) webService.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			copyChangedList.add(newWS);
		}
		
		//���Ʒ����仯�ķ���
		Map<String, List<WebService>> changedMapOfCopy = copyChangedMap(changedMap);		
		
		//��¼��ǰʵ�������ṹ
		for(String key: changedMap.keySet()){
			List<WebService> webServices = changedMap.get(key);
			
			for(WebService webService: webServices){
				if(key.equals("delete")){
					sb.append(key + "," + webService.getName() + System.lineSeparator());
					
					//ͳ�Ʊ仯����Ĳ���������
//					WS2NextWSNumBean ws2NextWSNumBean = new WS2NextWSNumBean();
//					ws2NextWSNumBean.setWsName(webService.getName());
//					ws2NextWSNumBean.setNextWSNum(rankMap.get(webService.getName()));
//					tempRanks.add(ws2NextWSNumBean);
				}
				else if(key.equals("qosChange")){
					sb.append(key + "," + webService.getName() + "," + webService.getSelfResponseTime() + System.lineSeparator());
				
//					//ͳ�Ʊ仯����Ĳ���������
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
		
		//ͳ��ʵ�ʵ�����
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
			//Ĭ����ʵ�ʵ�
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
		double t1;// ������ѯ��ʱ��
		double t2;// �ز��ʱ��
		
//		//ͳ�Ƴ���
//		statisticWSToEndWSLengthMap();
		
		/*------------������ѯ--------------------------*/
		
		for (int i = 0; i < groupNum; i++) {// ���10��
			//��ʼ��PQMaxSize
			this.PQMaxSize = 0;
			
			// ��RPT1��serviceMap1
			recoveryForHJQ(RPT1OfCopy);//ServiceMap���з�����Ϊ���ֵ,RPT1Ҳ��
			recoveryWithNewQosForHJQ(copyChangedList);//�ı�Ԥ�������qosֵ,��Ϊ��һ�д�����Ϊ���ֵ
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			recoveryIITForCJH(IITOfCopy);
			recoveryOTForHJQ(OTOfCopy);
			
			qosChangedList.clear();
			
			startMili = System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����
			
			//����ɾ����Ԥ����
			// ���ɾ��
			qosChangedList.addAll(adaptDeleteWS(addAndDeleteMap.get("delete")));
			
			// �������
			qosChangedList.addAll(adaptInsertWS(addAndDeleteMap.get("new")));
			
			// ���qos�ı�
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
				
		/*------------�ز�--------------------------*/
		
		totalTime = 0;
		timeList.clear();
		
		for (int i = 0; i < groupNum; i++) {
			recoveryReQueryForHJQ();
			recoveryIITForCJH(IITOfCopy);
			Map<String, List<WebService>> addAndDeleteMap = recoveryWithAddAndDeleteForHJQ(changedMapOfCopy);
			
			startMili = System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����
			adaptIIT(addAndDeleteMap);
			reQuery();
			endMili = System.nanoTime();
			timeList.add(new Double((endMili - startMili)/1E6));
		}
		
//		//�ó�ǰ������
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
//		//ͳ�Ʊ仯���������ǰ20��λ��
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
//		//ͳ�ƿ��
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
		
		//ͳ���ز���������ѯʱ��
		this.requeryTime += t2;
		this.continueTime += t1;
		
		//ͳ����Ѳ���ʱ��
		this.bestStrategyTime += ((t1>t2)? t2: t1);
		
		double strategy1Time = -1;// ��¼����1����ʲô�㷨
		double strategy2Time = -1;// ��¼����2����ʲô�㷨
		String result = "";// ��¼����1�Ͳ���2�ĶԱȽ��
		// HJQ ʵ��D,ͳ�Ʋ���1�Ͳ���2��ʤ�ʼ�ƽ��
		// ����1����ʵ��,����2����ȫ��
		if ((realSum <= this.strategy1ValueWithAD && sum <= this.strategy2ValueWithAD)) {
			this.equalCount++;

			strategy1Time = t1;
			strategy2Time = t1;
			
			//ͳ�Ʋ���ʱ��
			this.strategy1Time += t1;
			this.strategy2Time += t1;

			result = "ƽ" + System.getProperty("line.separator");
		} else if (realSum > this.strategy1ValueWithAD && sum > this.strategy2ValueWithAD) {
			this.equalCount++;

			strategy1Time = t2;
			strategy2Time = t2;
			
			//ͳ�Ʋ���ʱ��
			this.strategy1Time += t2;
			this.strategy2Time += t2;

			result = "ƽ" + System.getProperty("line.separator");
		} else if ((realSum <= this.strategy1ValueWithAD && sum > this.strategy2ValueWithAD)) {
			strategy1Time = t1;
			strategy2Time = t2;
			
			//ͳ�Ʋ���ʱ��
			this.strategy1Time += t1;
			this.strategy2Time += t2;
			
			if (t1 < t2) {
				this.winCount++;
				// *System.out.println("����1ʤ");
				result = "ʤ" + System.getProperty("line.separator");
			} else if (t1 == t2) {
				this.equalCount++;
				result = "ƽ" + System.getProperty("line.separator");
			} else {
				// *System.out.println("����1��");
				result = "��" + System.getProperty("line.separator");
			}
		} else if (realSum > this.strategy1ValueWithAD && sum <= this.strategy2ValueWithAD) {
			strategy1Time = t2;
			strategy2Time = t1;
			
			//ͳ�Ʋ���ʱ��
			this.strategy1Time += t2;
			this.strategy2Time += t1;
			
			if (t2 < t1) {
				this.winCount++;
				// *System.out.println("����1ʤ");
				result = "ʤ" + System.getProperty("line.separator");
			} else if (t2 == t1) {
				this.equalCount++;
				result = "ƽ" + System.getProperty("line.separator");
			} else {
				// *System.out.println("����1��");
				result = "��" + System.getProperty("line.separator");
			}
		}

		// ��������Ϣ��test.txt
		recordTime3.append(t2 + System.getProperty("line.separator"));
		recordTime4.append(t1 + System.getProperty("line.separator"));

		// ��̨��������Ϣ
		// *System.out.println("�ز�ƽ����ʱΪ:"+t2+"����");
		// *System.out.println("������ѯƽ����ʱΪ:"+t1+"����");

		// ��¼��ʽ�����ʵ��D��Ϣ
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

		// *System.out.println("qos�ı��˵ķ��������"+sum);
		// *System.out.println("ʵ����Ҫ���µ�ws(count==0):�ĸ�����"+realSum);
		// *System.out.println("PQ���е����ֵ��"+PQMaxSize);
	}
	/**
	 * ʵ��D��count��ʵ��
	 * @param solveMapOfCopy
	 * @param OTOfCopy
	 * @param RPT1OfCopy
	 * @param realInsts 
	 * @param i
	 * @throws IOException
	 */
	public void testManyTime1ForHJQ(Map<String, List<String>> solveMapOfCopy, Map<String, List<WebService>> OTOfCopy, Map<String, List<WebService>> IITOfCopy,
			Map<String, WebService> RPT1OfCopy, List<String> allInsts, int i) throws IOException{
		//HJQ ͳ��ʤ��,ƽ��
		this.winCount = 0;
		this.equalCount = 0;

		// ͳ��ʱ���ʼ��
		this.requeryTime = 0;
		this.continueTime = 0;
		this.strategy1Time = 0;
		this.strategy2Time = 0;
		this.bestStrategyTime = 0;

		int time = 0;
		//��¼ÿ��ʵ����Ϣ
		StringBuilder sb = new StringBuilder();
		
		// ����ʵ��������ѡ��ı����,�������ȫ�������ǽ�����ʵ������ṹ�еķ���
		List<WebService> testServiceList = new ArrayList<WebService>();

		while (time < count) {
			this.dead = false;
			time++;
			if (time % 5 == 0) {
				System.out.println("��" + time + "��");
			}
			if (time == 1) {
				recordTime1.append("Stratege1" + System.getProperty("line.separator"));
				recordTime2.append("Stratege2" + System.getProperty("line.separator"));
				recordTime3.append("requery" + System.getProperty("line.separator"));
				recordTime4.append("continuousQuery" + System.getProperty("line.separator"));
				recordTime5.append("�仯�������,"
						+ "ʵ����Ҫ���µ�ws�ĸ���,"
						+ "ʵ��ɾ���ĸ���,"
						+ "ʵ�������ĸ���,"
						+ "ʵ��qos�仯�ĸ���,"
						+ "PQ���е���󳤶�,"
						+ "����1(" + this.strategy1ValueWithAD
						+ "),"
						+ "����2(" + this.strategy2ValueWithAD + "),"
						+ "�ز�,"
						+ "������ѯ,"
						+ "���"
						+ System.getProperty("line.separator"));
			}

			// �ָ�testServiceList,��������仯������Ҫ
			testServiceList.clear();

			for (String key : serviceMap.keySet()) {
				WebService webService = serviceMap.get(key);

				String name = webService.getName();

				if (!name.equals("Provide") && !name.equals("Request")) {
					testServiceList.add(webService);
				}
			}

			// System.out.println("��"+time+"��");
			sb.append("��" + time + "��" + System.lineSeparator());
			randomTest1ForHJQ(testServiceList, OTOfCopy, IITOfCopy, RPT1OfCopy, solveMapOfCopy, allInsts, sb);
			sb.append(System.lineSeparator());
			
			if(this.dead){
				time--;
			}
			
		}

		// ��¼ƽ��ʱ��
		recordTime5.append("�ز�ƽ��ʱ��,������ѯƽ��ʱ��,����1ƽ��ʱ��,����2ƽ��ʱ��,��Ѳ���ƽ��ʱ��" + System.lineSeparator());
		recordTime5.append(this.requeryTime / this.count + ",");
		recordTime5.append(this.continueTime / this.count + ",");
		recordTime5.append(this.strategy1Time / this.count + ",");
		recordTime5.append(this.strategy2Time / this.count + ",");
		recordTime5.append(this.bestStrategyTime / this.count + System.lineSeparator());

		// ��¼�ԱȽ��
		recordTime5.append("����1ʤ�ڲ���2:" + this.winCount + System.getProperty("line.separator"));
		recordTime5.append("����1ƽ�ڲ���2:" + this.equalCount + System.getProperty("line.separator"));
		recordTime5.append("����1���ڲ���2:" + (this.count - this.winCount - this.equalCount) + System.getProperty("line.separator"));
		
		PrintWriter pw = new PrintWriter(new File("result\\" + "changedServices" + i + ".csv"));
		
		pw.write(sb.toString());
		
		pw.close();
		
		buff=new byte[]{};
		out1=new FileOutputStream("result\\" + i + ".csv");
		
		
        buff=recordTime5.toString().getBytes();        
        out1.write(buff,0,buff.length);
		out1.close();
		
		//����1����ʵ��,����2����ȫ��
		System.out.println("����1ʤ�ڲ���2:" + this.winCount);
		System.out.println("����1ƽ�ڲ���2:" + this.equalCount);
		System.out.println("����1���ڲ���2:" + (this.count - this.winCount - this.equalCount));
	}
	/**
	 * ��allCount������ɾ����ʵ��D,ÿ��ʵ������ʤ>�����㱾��ʵ��ʤ
	 * @throws IOException
	 */
	public void testManyTime1ForWinHJQ() throws IOException{
		
		PrintWriter pw = new PrintWriter(new File("result\\" + "RPT.csv"));
		
		for(String key: RPT.keySet()){
			pw.println(key + "," + RPT.get(key).getInfo());
		}
		
		pw.close();

		// HJQ,����IIT
		Map<String, List<WebService>> IITOfCopy = null;

		try {
			IITOfCopy = DataGetter.copyStrToWsList(IIT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// ����solveMap
		Map<String, List<String>> solveMapOfCopy = DataGetter.copyStrToStrList(solveMap);

		// HJQ,����OT
		Map<String, List<WebService>> OTOfCopy = null;

		// HJQ,����PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);

		try {
			OTOfCopy = DataGetter.copyStrToWsList(OT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		//copy RPT1->RPT2 serviceMap1->serviceMap2
		copy();
		
		//����inst
		List<String > allInsts = new ArrayList<String>();
		for(String instAndCon: map1.keySet()){
			if(instAndCon.contains("inst")){
				allInsts.add(instAndCon);
			}
		}
		
		//�൱������100�γ���
		this.allCount = 100;
		StringBuilder sb = new StringBuilder();
		
		sb.append("ʵ�����,ʵ����,�ز�ƽ��ʱ��,������ѯƽ��ʱ��,����1ƽ��ʱ��,����2ƽ��ʱ��,��Ѳ���ƽ��ʱ��" + System.lineSeparator());
		
		for(int i = 1; i <= allCount; i++){
			recordTime5 = new StringBuilder();
			System.out.println("��" + i + "��");

			testManyTime1ForHJQ(solveMapOfCopy, OTOfCopy, IITOfCopy, RPT1OfCopy, allInsts, i);
			
			long failCount = this.count - this.winCount - this.equalCount;
			
			if(this.winCount > failCount){
				this.allWinCount++;
				sb.append(i + ",ʤ,");
			}
			else if(this.winCount == failCount){
				this.allEqualCount++;
				sb.append(i + ",ƽ,");
			}
			else{
				sb.append(i + ",��,");
			}
			
			sb.append(this.requeryTime / this.count + ",");
			sb.append(this.continueTime / this.count + ",");
			sb.append(this.strategy1Time / this.count + ",");
			sb.append(this.strategy2Time / this.count + ",");
			sb.append(this.bestStrategyTime / this.count + System.lineSeparator());
		}
		
		System.out.println("��ʤ��:" + this.allWinCount);
		System.out.println("��ƽ��:" + this.allEqualCount);
		System.out.println("�ܸ���:" + (this.allCount - this.allWinCount - this.allEqualCount));
		
		sb.append("��ʤ��:" + this.allWinCount + System.lineSeparator());
		sb.append("��ƽ��:" + this.allEqualCount + System.lineSeparator());
		sb.append("�ܸ���:" + (this.allCount - this.allWinCount - this.allEqualCount) + System.lineSeparator());
		
		pw = new PrintWriter(new File("result\\allresult.csv"));
		pw.write(sb.toString());
		pw.close();
		
	}

	/**
	 * ����qos�仯��ʵ��D��count��ʵ��
	 * @param solveMapOfCopy
	 * @param OTOfCopy
	 * @param RPT1OfCopy
	 * @param i
	 * @throws IOException
	 */
	public void testManyTime1(Map<String, List<String>> solveMapOfCopy, Map<String, List<WebService>> OTOfCopy, Map<String, WebService> RPT1OfCopy, int i) throws IOException{
		//HJQ ͳ��ʤ��,ƽ��
		this.winCount = 0;
		this.equalCount = 0;
		//ͳ��ʱ���ʼ��
		this.requeryTime = 0;
		this.continueTime = 0;
		this.strategy1Time = 0;
		this.strategy2Time = 0;
		this.bestStrategyTime = 0;
		
		int time = 0;
		
		//��ʼ��recordTime5
		recordTime5 = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		
		while(time<count){
			time++;
			if (time % 5 == 0) {
				System.out.println("��" + time + "��");
			}
			if(time==1){
				recordTime1.append("Stratege1"+System.getProperty("line.separator"));
				recordTime2.append("Stratege2"+System.getProperty("line.separator"));
				recordTime3.append("requery"+System.getProperty("line.separator"));	
				recordTime4.append("continuousQuery"+System.getProperty("line.separator"));
				recordTime5.append("qos�ı��˵ķ������,"
						+ "ʵ����Ҫ���µ�ws(count==0):�ĸ���,"
						+ "PQ���е���󳤶�,"
						+ "����1(" + this.strategy1Value + "),"
						+ "����2(" + this.strategy2Value + "),"
						+ "�ز�,"
						+ "������ѯ,"
						+ "���"+System.getProperty("line.separator"));
			}
			
			//System.out.println("��"+time+"��");
			sb.append("��"+time+"��" + System.lineSeparator());
			randomTest1(solveMapOfCopy, OTOfCopy, RPT1OfCopy, sb);
			sb.append(System.lineSeparator());
			
		}
		
		//��¼ƽ��ʱ��
		recordTime5.append("�ز�ƽ��ʱ��,������ѯƽ��ʱ��,����1ƽ��ʱ��,����2ƽ��ʱ��,��Ѳ���ƽ��ʱ��" + System.lineSeparator());
		recordTime5.append(this.requeryTime / this.count + ",");
		recordTime5.append(this.continueTime / this.count + ",");
		recordTime5.append(this.strategy1Time / this.count + ",");
		recordTime5.append(this.strategy2Time / this.count + ",");
		recordTime5.append(this.bestStrategyTime / this.count + System.lineSeparator());
		
		//��¼�ԱȽ��
		recordTime5.append("����1ʤ�ڲ���2:" + this.winCount + System.getProperty("line.separator"));
		recordTime5.append("����1ƽ�ڲ���2:" + this.equalCount + System.getProperty("line.separator"));
		recordTime5.append("����1���ڲ���2:" + (this.count - this.winCount - this.equalCount) + System.getProperty("line.separator"));
		
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
		
		//����1����ʵ��,����2����ȫ��
		System.out.println("����1ʤ�ڲ���2:" + this.winCount);
		System.out.println("����1ƽ�ڲ���2:" + this.equalCount);
		System.out.println("����1���ڲ���2:" + (this.count - this.winCount - this.equalCount));
	}
	/**
	 * ��allCount�ν���qos�仯��ʵ��D,ÿ��ʵ������ʤ>�����㱾��ʵ��ʤ
	 * @throws IOException
	 */
	public void testManyTime1ForWin() throws IOException{
		
		PrintWriter pw = new PrintWriter(new File("result\\" + "RPT.csv"));
		
		for(String key: RPT.keySet()){
			pw.println(key + "," + RPT.get(key).getInfo());
		}
		
		pw.close();
		
		// ����solveMap
		Map<String, List<String>> solveMapOfCopy = DataGetter.copyStrToStrList(solveMap);

		// HJQ,����OT
		Map<String, List<WebService>> OTOfCopy = null;

		// HJQ,����PRT1
		Map<String, WebService> RPT1OfCopy = null;

		RPT1OfCopy = DataGetter.copyStrToWsWithNew(RPT1);

		try {
			OTOfCopy = DataGetter.copyStrToWsList(OT);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		//copy RPT1->RPT2 serviceMap1->serviceMap2
		copy();
		//�൱������100�γ���
		this.allCount = 100;
		StringBuilder sb = new StringBuilder();
		
		sb.append("ʵ�����,ʵ����,�ز�ƽ��ʱ��,������ѯƽ��ʱ��,����1ƽ��ʱ��,����2ƽ��ʱ��,��Ѳ���ƽ��ʱ��" + System.lineSeparator());
		
		for(int i = 1; i <= allCount; i++){
			System.out.println("��" + i + "��");
			testManyTime1(solveMapOfCopy, OTOfCopy, RPT1OfCopy, i);
			
			long failCount = this.count - this.winCount - this.equalCount;
			
			if(this.winCount > failCount){
				this.allWinCount++;
				sb.append(i + ",ʤ,");
			}
			else if(this.winCount == failCount){
				this.allEqualCount++;
				sb.append(i + ",ƽ,");
			}
			else{
				sb.append(i + ",��,");
			}
			
			sb.append(this.requeryTime / this.count + ",");
			sb.append(this.continueTime / this.count + ",");
			sb.append(this.strategy1Time / this.count + ",");
			sb.append(this.strategy2Time / this.count + ",");
			sb.append(this.bestStrategyTime / this.count + System.lineSeparator());
			
		}
		
		System.out.println("��ʤ��:" + this.allWinCount);
		System.out.println("��ƽ��:" + this.allEqualCount);
		System.out.println("�ܸ���:" + (this.allCount - this.allWinCount - this.allEqualCount));
		
		sb.append("��ʤ��:" + this.allWinCount + System.lineSeparator());
		sb.append("��ƽ��:" + this.allEqualCount + System.lineSeparator());
		sb.append("�ܸ���:" + (this.allCount - this.allWinCount - this.allEqualCount) + System.lineSeparator());
		
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
//				System.out.println(PQ.get(i).getName()+"��min��:"+min);				
//				
//			}
//			System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
			
			
			
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	System.out.println("******����Request******");
				continue;
			}
		    List<WebService> successorList;

		    if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//System.out.println("qos��С");
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("���solveMap");
						solveMap.put(output, matchList);
					}
					
//					if(first.getName().equals("serv1581459521")){//�����ṩ288�����룬�Ҳ�
//						for(int ii=0;ii<matchList.size();ii++){
//							System.out.print(matchList.get(ii)+" ");
//						}
//						System.out.println();
//					}
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						/*
						//�ж��Ƿ���Ҫ����RPT1
						WebService parent = RPT1.get(inst);
						if(parent==null){
							System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
						
						if(parent!=first){
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"ȡ��"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"�µĸ�����:"+first.getName());
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
								
								//�ж��Ƿ���Ҫ����/����
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy������");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk������");
										//�����ʶû�����ݼ���ȱ��
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
				//System.out.println("qos���");
				//first.setAllResponseTime(999999);
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("���solveMap");
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
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getAllResponseTime());
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
								
								//�ж��Ƿ���Ҫ����/����
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy������");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk���ݼ�������");
										//�����ʶû�����ݼ���ȱ��
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
		
		System.out.print("continuous���Ž��ǣ�");
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
//				System.out.println(PQ.get(i).getName()+"��min��:"+min);				
//				
//			}
//			System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
			
			
			
		    if(first.getName().equals("Request")){
				//first.setAllResponseTime(first.getNewAllResponseTime());
		    	//System.out.println("******����Request******");
		    	first.setAllResponseTime(first.getNewAllResponseTime());
				continue;
			}
		    List<WebService> successorList;
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//System.out.println("qos��С");
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("���solveMap");
						solveMap.put(output, matchList);
					}
					
//					if(first.getName().equals("serv1581459521")){//�����ṩ288�����룬�Ҳ�
//						for(int ii=0;ii<matchList.size();ii++){
//							System.out.print(matchList.get(ii)+" ");
//						}
//						System.out.println();
//					}
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						//�ж��Ƿ���Ҫ����RPT1
						WebService parent = RPT1.get(inst);
						if(parent==null){
							System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
						
						if(parent!=first){
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"ȡ��"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"�µĸ�����:"+first.getName());
								//System.out.println("****RPT1.size"+RPT1.size());
							}
						}
						
					    
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//�ж��Ƿ���Ҫ����/����
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy������");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk������");
										//�����ʶû�����ݼ���ȱ��
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
				//System.out.println("qos���");
				//first.setAllResponseTime(999999);
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("���solveMap");
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
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getAllResponseTime());
							//System.out.println(inst);
						}
						RPT1.put(inst, newParent);
						
						
						
						
						for(int u=0;u<successorList.size();u++){
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);
								//if(successor==null) System.out.println("kong");
								WebService criticalParent = getCriticalParent(successor);
								
								//�ж��Ƿ���Ҫ����/����
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy������");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime())
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk���ݼ�������");
										//�����ʶû�����ݼ���ȱ��
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
		
		//System.out.print("continuous���Ž��ǣ�");
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
	
	public void continuousQuery3(List<WebService> qosChangedList){//else�ı��ˣ�����������
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
//				//System.out.println(PQ.get(i).getName()+"��min��:"+min);				
//				recordSomething=recordSomething+PQ.get(i).getName()+"��min��:"+min+"\n";
//			}
//			recordSomething=recordSomething+"******"+"\n";
			//System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
									
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	System.out.println("******����Request******");
				continue;
			}
		    List<WebService> successorList;
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//System.out.println("qos��С");
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("���solveMap");
						solveMap.put(output, matchList);
					}
					
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						/*
						//�ж��Ƿ���Ҫ����RPT1
						WebService parent = RPT1.get(inst);
						if(parent==null){
							System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
						
						if(parent!=first){
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"ȡ��"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"�µĸ�����:"+first.getName());
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
								
								//�ж��Ƿ���Ҫ����/����
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy������");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());										
									}
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk������");
										//�����ʶû�����ݼ���ȱ��
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											PQ.add(successor);										
										}
										continue;
									}

									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
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
				//System.out.println("qos���");
				//first.setAllResponseTime(999999);
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						//System.out.println("���solveMap");
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
						if(newParent==first){//���Ÿ��׻����Լ�
//							if(newParent.getNewAllResponseTime()>0)
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getAllResponseTime());
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
								
								//�ж��Ƿ���Ҫ����/����
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										System.out.println("yy������");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
									}
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk������");
										//�����ʶû�����ݼ���ȱ��
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){//���������������
									//		recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											PQ.add(successor);										
										}
										continue;
									}
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
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
		
		System.out.print("continuous���Ž��ǣ�");
		if(serviceMap.get("Request").getNewAllResponseTime()>0){
			System.out.println(serviceMap.get("Request").getNewAllResponseTime());
			continousQos=serviceMap.get("Request").getNewAllResponseTime();
		}
		else{
			System.out.println(serviceMap.get("Request").getAllResponseTime());
			continousQos=serviceMap.get("Request").getAllResponseTime();
		}
		//System.out.println("****RPT1.size"+RPT1.size());
		//System.out.println("Request��newAllqos"+serviceMap.get("Request").getNewAllResponseTime());
		//System.out.println("Request��allqos"+serviceMap.get("Request").getAllResponseTime());
		//System.out.println("dd"+getCriticalParent(serviceMap.get("Request")).getAllResponseTime());
		
		//System.out.println("continuousQuery��backward:");
		//backwardForRPT1(outputsFromChallenge,RPT1,serviceMap);
		recordBackWardForRPT1();
		System.out.println("reQuery()");		
		reQuery();
	}
	
	public void continuousQuery4(List<WebService> qosChangedList){//�������������û����Ҫ��inst������parent
		
		List<WebService> PQ = new ArrayList<WebService>();
		//���ڻ�ͼ
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
			
					//HJQ_����MIN(allqos,newallqos)��С����
			sort(PQ);
//			System.out.println("***PQ****");
//			//recordSomething=recordSomething+"***PQ****"+"\n";
//			for(int i=0;i<PQ.size();i++){
//				double original = PQ.get(i).getAllResponseTime();
//				double newOne = PQ.get(i).getNewAllResponseTime();
//				double min =original>newOne?newOne:original;
//				(PQ.get(i).getName()+"��min��:"+min);				
//				//recordSomething=recordSomething+PQ.get(i).getName()+"��min��:"+min+"\n";
//			}
//			recordSomething=recordSomething+"******"+"\n";
//			System.out.println("*********");
			
		    first = PQ.get(0);
		    
			//���ڻ�ͼ
		    PQOfCopy.add(first);
		    
			PQ.remove(0);
						//HJQ_��������ʣ��Request����(����Ϊ��Response),ֱ�Ӹı�allqos,�����������ѯ						
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	//System.out.println("******����Request******");		    					
				continue;
		    	//break;
		    }
		    
		    List<WebService> successorList;
		    			//HJQ_qos��С
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				
				//System.out.println("qos��С");
						//HJQ_
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
						//HJQ_�ҵ���ƥ�������
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						System.out.println("���solveMap");
						solveMap.put(output, matchList);
					}
					
							//HJQ_����,�ı���qos�ķ������ṩ��matchinsts,����ÿһ��inst����matchinsts,���÷������
							//HJQ_�ı�Ϊ�ָ÷����ṩ��inst.Ȼ��,
							//HJQ_����ÿһ������inputs����inst�ķ���,����һ���������newallqos
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						//������û��Ҫ��inst�ĸ���
						if(IIT.get(inst)==null) continue;
						
						//�ж��Ƿ���Ҫ����RPT1
									//HJQ_ԭ���ľ�������???????????????
						WebService parent = RPT1.get(inst);
						if(parent==null){
							//System.out.println(inst+" "+"k="+k+" "+first.getName()+" "+first.getCount());
							continue;
						}
								//HJQ_parent��firstͬ�������ṩinst,
								//HJQ_����������
						if(parent!=first){
								//HJQ_newallqos����0,��ʾ����ı���
							double parentQos = parent.getNewAllResponseTime()>0?parent.getNewAllResponseTime():parent.getAllResponseTime();
								//HJQ_first����
							if(parentQos>first.getNewAllResponseTime()){
								//System.out.println(first.getName()+"ȡ��"+parent.getName());
								RPT1.put(inst, first);
								//System.out.println(first.getName());
								//System.out.println(inst+"�µĸ�����:"+first.getName());
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
									//HJQ_��first_outputs�����ҵ��ķ���
					    successorList = IIT.get(inst);
						if(successorList==null) continue;
						
						for(int u=0;u<successorList.size();u++){
										//HJQ_����ɴ��
							if(successorList.get(u).getCount()==0){
								WebService successor = successorList.get(u);					
								//if(successor==null) System.out.println("kong");
												//HJQ_�ҳ��ܹ�ƥ��successor��input��qos��õķ���
								WebService criticalParent = getCriticalParent(successor);
								
								//�ж��Ƿ���Ҫ����/����
											//HJQ_���Ÿ������Ƿ�ı�qos
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
											//HJQ_qos�仯�˵Ŀɴ�����а�����successor
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										//System.out.println("yy������");
											//HJQ_successor��newallqos����Ϊ���Ÿ������qos+����qos
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());										
									}
								}else{
												//HJQ_��һ����first�ı���,����һ����successor��Ϊ�ı�qos
												//HJQ_qos�仯�˵����ɴ�
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk������");
										//�����ʶû�����ݼ���ȱ��
												//HJQ_successor��newallqos����Ϊ���Ÿ������qos+����qos
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											
											PQ.add(successor);
										}
										continue;
									}
												//HJQ_successor��allqos����Ϊ���Ÿ������qos+����qos
												//HJQ_qosû�б仯�˵����ɴ�
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
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
				//HJQ_���Ǳ仯
				first.setAllResponseTime(first.getNewAllResponseTime());
				
			}else{
				//HJQ_qos���
				for(int i=0;i<first.getOutputs().size();i++){
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						solveMap.put(output, matchList);
					}
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);						
						
						//������û��Ҫ��inst�ĸ���
						if(IIT.get(inst)==null) continue;
									//HJQ_����˾�Ҫ������?????????????????
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
						if(newParent==first){//���Ÿ��׻����Լ�
//							if(newParent.getNewAllResponseTime()>0)
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getNewAllResponseTime());
//							else
//								System.out.println(inst+"��qos��"+first.getNewAllResponseTime()+"��Ϊ"+newParent.getAllResponseTime());
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
								//�ж��Ƿ���Ҫ����/����
								double criticalParentQos = criticalParent.getNewAllResponseTime()>0?criticalParent.getNewAllResponseTime():criticalParent.getAllResponseTime();
								if(PQ.contains(successor)){
									if(successor.getNewAllResponseTime()<=0)
										//System.out.println("yy������");
									if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
										successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
									}
								}else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk������");
										//�����ʶû�����ݼ���ȱ��
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){//���������������
									//		recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											
											PQ.add(successor);									
										}
										continue;
									}
									if(successor.getAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
									//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
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
		
//		//���ڻ�ͼ
//		PrintNetwork.printPQ(PQOfCopy);
//		
//		PQOfCopy.removeAll(qosChangedList);
//		
//		PrintNetwork.printPQWithoutFirstChanged(PQOfCopy);
//		//*//
		
		//System.out.print("continuous���Ž��ǣ�");
		if(serviceMap.get("Request").getNewAllResponseTime()>0){
			//System.out.println(serviceMap.get("Request").getNewAllResponseTime());
			continousQos=serviceMap.get("Request").getNewAllResponseTime();
		}
		else{
			//System.out.println(serviceMap.get("Request").getAllResponseTime());
			continousQos=serviceMap.get("Request").getAllResponseTime();
		}
		//System.out.println("****RPT1.size"+RPT1.size());
		//System.out.println("Request��newAllqos"+serviceMap.get("Request").getNewAllResponseTime());
		//System.out.println("Request��allqos"+serviceMap.get("Request").getAllResponseTime());
		//System.out.println("dd"+getCriticalParent(serviceMap.get("Request")).getAllResponseTime());
		
		//System.out.println("continuousQuery��backward:");
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
				System.out.println(PQ.get(i).getName()+"��min��:"+min);				
			}
			System.out.println("*********");
			
		    first = PQ.get(0);
			PQ.remove(0);
		    if(first.getName().equals("Request")){
				//first.setAllResponseTime(first.getNewAllResponseTime());
		    	System.out.println("******����Request******");
				continue;
			}
			if(first.getNewAllResponseTime()<first.getAllResponseTime()){
				//����������������Ƿ��б�Ҫ
				//first.setAllResponseTime(first.getNewAllResponseTime());
				
				//�ж��Ƿ�������parent
				if(RPT.containsValue(first)){//��������д�����----����
					System.out.println(first.getName()+"��������parent");
					for(int i=0;i<first.getOutputs().size();i++){
						//��Ҫ����output�ܽ����inst
						String output = first.getOutputs().get(i).toString();
						
						
						List<String> matchList = solveMap.get(output);//
						if(matchList==null){//����Ϊ�գ���Ϊ֮ǰ���㷨�����������Ϊ��							
							matchList = findInstances(output,map1,map2);										
						}
						for(int j=0;j<matchList.size();j++){
							
							if ( RPT.get(matchList.get(j)) == first ){
								System.out.println(first.getName()+"��"+matchList.get(j)+"������parent");
								//firstΪĳ��inst�����Ÿ��ף������Ҫ�ж��Ƿ���º�̽ڵ�								
								List<WebService> wsList = IIT.get(matchList.get(j));//��Ҫinst�ķ���
								//����Ҫ�ж�ĳ�����Ƿ�ɴ�
								if(wsList==null) continue;
								for(int k=0;k<wsList.size();k++){
									
									WebService successor = wsList.get(k);
									if(PQ.contains(successor)){//������Ҫ������ԭ����qos���Ǻ�����qos
										if(wsList.get(k).getNewAllResponseTime()>0){//���������������仰Ӧ�ò���Ҫ
											//���º�̽ڵ��Qos	
											//�������
											if(isCriticalNode(first,successor)){
												if(successor.getCriticalParent()!=null && successor.getCriticalParent()==first) {
													System.out.println(first.getName()+"�Ѿ���"+successor.getName()+"�Ĺؼ�parent");
													continue;													
												}
												System.out.println(successor.getName()+"�ɵ�qos"+successor.getNewAllResponseTime());
												successor.setCriticalParent(first);
												successor.setNewAllResponseTime(successor.getNewAllResponseTime()-first.getAllResponseTime()+first.getNewAllResponseTime());											
												System.out.println(first.getName()+"��"+successor.getName()+"�ؼ���");
												System.out.println(successor.getName()+"����Qos��"+successor.getNewAllResponseTime());
												
											}else
												System.out.println(first.getName()+"����"+successor.getName()+"�ؼ���");
										}else{
											System.out.println("�������������. "+wsList.get(k).getName()+"��newQos��"+wsList.get(k).getNewAllResponseTime());
										}
									}else{
										if(successor.getAllResponseTime()>0){//˵���ɴ�
											if(isCriticalNode(first,successor)){
												System.out.println(first.getName()+"��"+successor.getName()+"�ؼ���");
												successor.setNewAllResponseTime(successor.getAllResponseTime()-first.getAllResponseTime()+first.getNewAllResponseTime());												
												successor.setCriticalParent(first);
												PQ.add(successor);																								System.out.println(first.getName()+"��"+successor.getName()+"�ؼ���");
												System.out.println(successor.getName()+"����Qos��"+successor.getNewAllResponseTime());
											}else{
												System.out.println(first.getName()+"���ǹؼ���");
											}
										}else{
											System.out.println("yyyy "+successor.getName()+"��count:"+successor.getCount()+",���ɴ�");
										}
									}
								}
							}else{
								//first����ĳ��inst�����Ÿ��ף��ж��Ƿ������Ÿ���
								System.out.println("kkkkkkkk");
								WebService parent = RPT.get(matchList.get(j));
								List<WebService> wsList = IIT.get(matchList.get(j));
								if(wsList==null) {
									System.out.println("wsList��null");
									continue;
								}
								for(int b=0;b<wsList.size();b++){
									WebService successor = wsList.get(b);
									if(successor.getCount()!=0) {
										System.out.println(successor.getName()+"��count:"+successor.getCount()+",���ɴ�");
										continue;										
									}
									else{
										if(PQ.contains(successor)){//successor�Ѿ��ڶ�����											
											if(parent.getNewAllResponseTime()>0){//��Ϊ��parent�п��ܸ��¹���Ҳ�п���û���¹�
												System.out.println(")))))))))))))))))");
												if(parent.getNewAllResponseTime()>first.getNewAllResponseTime()){
													//first��ΪmatchList.get(j)���µ�parent
													System.out.println(first.getName()+"��Ϊ"+matchList.get(j)+"����parent");
													RPT.put(matchList.get(j), first);
													if(isCriticalNode(first,successor)){
														if(successor.getCriticalParent()!=null && successor.getCriticalParent()==first) {
															System.out.println(first.getName()+"�Ѿ���"+successor.getName()+"�Ĺؼ�parent");
															continue;													
														}
														successor.setCriticalParent(first);
														
														successor.setNewAllResponseTime(successor.getNewAllResponseTime()-parent.getNewAllResponseTime()+first.getNewAllResponseTime());													
														System.out.println(first.getName()+"��"+successor.getName()+"�ؼ���");
													}else{
														System.out.println(first.getName()+"���ǹؼ���");
													}
													
												}else{
													//parent��Ȼ�����ŵ�
													System.out.println(parent.getName()+"��Ȼ�����ŵ�");
												}													
											}else{
												System.out.println("%%%%%%%%%%%%%%5");
												if(parent.getAllResponseTime()>first.getNewAllResponseTime()){
													//first��ΪmatchList.get(j)���µ�parent
													System.out.println(first.getName()+"��Ϊ"+matchList.get(j)+"����parent");
													RPT.put(matchList.get(j), first);
													if(isCriticalNode(first,successor)){
														if(successor.getCriticalParent()!=null && successor.getCriticalParent()==first) {
															System.out.println(first.getName()+"�Ѿ���"+successor.getName()+"�Ĺؼ�parent");
															continue;													
														}
														successor.setNewAllResponseTime(successor.getNewAllResponseTime()-parent.getAllResponseTime()+first.getNewAllResponseTime());
														System.out.println(first.getName()+"��"+successor.getName()+"�ؼ���");
													}else{
														System.out.println(first.getName()+"���ǹؼ���");
													}												
												}else{
													//parent��Ȼ�����ŵ�
												}	System.out.println(parent.getName()+"��Ȼ�����ŵ�");
											}
										}else{//successor���ڶ�����
											if(parent.getNewAllResponseTime()>0){//��Ϊ��parent�п��ܸ��¹���Ҳ�п���û���¹�
												System.out.println("&&&&&&&&&&&&&&&&&&&&&&");
												if(parent.getNewAllResponseTime()>first.getNewAllResponseTime()){
													//first��ΪmatchList.get(j)���µ�parent
													System.out.println(first.getName()+"��Ϊ"+matchList.get(j)+"����parent");
													RPT.put(matchList.get(j), first);
													//����������succesorû��newAllResponseTime 
													if(successor.getNewAllResponseTime()==0)
														if(isCriticalNode(first,successor)){
															successor.setNewAllResponseTime(successor.getAllResponseTime()-parent.getNewAllResponseTime()+first.getNewAllResponseTime());
															System.out.println(first.getName()+"��"+successor.getName()+"�ؼ���");
															PQ.add(successor);
														}else{
															System.out.println(first.getName()+"���ǹؼ���");
														}													
													else{
														System.out.println("231���ֲ��������");
													}
													
												}else{
													//parent��Ȼ�����ŵ�
													System.out.println(parent.getName()+"��Ȼ�����ŵ�");
												}													
											}else{
												System.out.println(")^^^^^^^^^^^^^^");
												if(parent.getAllResponseTime()>first.getNewAllResponseTime()){
													//first��ΪmatchList.get(j)���µ�parent
													System.out.println(first.getName()+"��Ϊ"+matchList.get(j)+"����parent");
													RPT.put(matchList.get(j), first);
													if(successor.getNewAllResponseTime()==0)
														if(isCriticalNode(first,successor)){
															successor.setNewAllResponseTime(successor.getAllResponseTime()-first.getAllResponseTime()+first.getNewAllResponseTime());
															System.out.println(successor.getName()+"���µ�qos:"+successor.getNewAllResponseTime());
															System.out.println(first.getName()+"��"+successor.getName()+"�ؼ���");
															PQ.add(successor);
														}else{
															System.out.println(first.getName()+"���ǹؼ���");
														}													
													else{
														System.out.println("244���ֲ��������");
													}							
												}else{
													//parent��Ȼ�����ŵ�
												}	System.out.println(parent.getName()+"��Ȼ�����ŵ�");
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
	
	//HJQ_  List<WebService> PQ ����    MIN(allqos,newallqos) ����  ͬʱ��ֵ����ҵ���Request(�൱������Ϊ��Response) return 1;
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
	
	//HJQ_  List<WebService> parentList  ����allqos����
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
		
	//HJQ_   List<Double> list  ����ֵ����
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
	
	//HJQ_�ز�     ��һ��������һ�β�ѯ
	public void reQuery(){//�ĳ��ҵ���request��ֱ�ӽ���
		
		// System.out.println("reqQuery��ʼ: "+startMili);
		
		int sumOfRequest = outputsFromChallenge.size();
		WebService begin = serviceMap.get("Provide");
		WebService end = serviceMap.get("Request");
		end.setCount(end.getInputs().size());
		List<WebService> reachVertices = new ArrayList<WebService>();
		
		//HJQ_���з����ʼ��
		//����ʡ��һǧ��
		
        RPT.clear();
        
        //long startMili=System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����  
        
		boolean find=false;
		reachVertices.add(begin);
		
		while(reachVertices.size()!=0){
			Collections.sort(reachVertices);
//			System.out.println("************");
//			for(WebService ww:reachVertices){
//				System.out.println(ww.getName()+"�ķ���ʱ��"+ww.getAllResponseTime());
//			}
//			System.out.println("************");
						
			
			WebService v = reachVertices.remove(0);
			for(Object output:v.getOutputs()){
												
				
				if(!RPT.containsKey(output.toString())){//�µ�inst
					//RPT.put(output.toString(),v);
					//System.out.println("�µ�inst:"+output.toString());
					//String con = map1.get(output).toString();
										//HJQ_�ӵ�һ�β�ѯ����������solveMap�л�ȡ��ƥ���List<inst>
					List<String> matchList = solveMap.get(output.toString());//�����ܱ�output�����inst	
										//HJQ_���solveMap�в�����,���²��ƥ���List<inst>
					if(matchList==null){
					 matchList = findInstances(output.toString(),map1,map2);
					 //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^&&&");
					}
					
					//HJQ_??????
					if(!output.toString().equals(matchList.get(0)))//ʹmatchList���A1-A2-A3-A4
						for(int i=1;i<matchList.size();i++){
							if(output.toString().equals(matchList.get(i))){
								String st = matchList.get(0);
								matchList.set(0, matchList.get(i));
								matchList.set(i, st);
								break;
							}									
						}
										
					//System.out.println("matchList�Ĵ�С��"+matchList.size());
					boolean firstPlace=true;
					for(String inst : matchList){
//						if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//							System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						//if(!firstPlace && RPT.containsKey(inst)) continue;
						
						if(!RPT.containsKey(inst)){
							 RPT.put(inst, v);
							 //System.out.println("�µ�inst*:"+inst);
						 }else{
							 continue;
						 }
						
						firstPlace=false;
						 List<WebService> haveSameInstWebServiceList = IIT.get(inst);
						 if(haveSameInstWebServiceList==null){
							 //System.out.println("û���κη�����Ҫ��"+inst);
							 continue;
						 }
//						 if(inst.equals("inst1219347607") || inst.equals("inst1934560603"))
//								System.out.println("^^^^^^^^^^^^^^^^^^"+inst);
						 
						 //System.out.println("��"+haveSameInstWebServiceList.size()+"��������Ҫ"+inst);
						 for(WebService w: haveSameInstWebServiceList){	
							 w.subCount();
							 //if(w.getName().equals("Request")) System.out.println("��ʣ"+end.getCount()+"��Ԫ��");
							 
							 if(w.getCount()==0){
								 w.setAllResponseTime(v.getAllResponseTime()+w.getSelfResponseTime());									
								 //enabledServices.put(w.getName(), w);
								 if(w.getName().equals("Request"))
									 if(end.getCount()==0){
										 //System.out.println("�ؼ�����"+v.getName());
										 //System.out.println("�ҵ���ȫ��Ҫ���Ԫ��");
										 //System.out.println(end.getAllResponseTime());
										 find = true;
										 //return;
										 continue;
										 //break;
									 }
									 
								 reachVertices.add(w);
									 //System.out.println("����"+w.getName()+"������");
									// System.out.println("����"+w.getName()+"��AllRequestTime:"+w.getAllResponseTime());
									 /////////serv1027640201 serv404388610  404388610
							
								 
							 }
						 }
						 
					}
					
				}
			}
		}
		
		//System.out.println("RPT.size()"+RPT.size());
		
		//long endMili=System.currentTimeMillis();
		//System.out.println("reQuery����:"+endMili);
		//System.out.println("reQuery�ܺ�ʱ:"+(endMili-startMili)+"����");
//		
//		if(!find)
//			System.out.println("�Ҳ���");
//		else
//			System.out.println("���Ž��qos:"+end.getAllResponseTime());
		requeryQos=end.getAllResponseTime();
//		if(requeryQos==continousQos){
//			System.out.println("ǰ����ȷ");
//			//successTime++;/////////////////////////
//			//count++;//////////			
//		}else{
//			System.out.println("ǰ��bubububububu��ȷ");
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
//			//System.out.println("reQuery��backward:");
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
//					//System.out.println("w��count!=0,������");
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
	 * ����qos�仯��ʵ��D��ÿ��ʵ��
	 * �аѲ��ɴ������仯�ķ����е������,�ԱȲ���1,����2��Ч��
	 * ����1λ����ʵ�ʱ仯�Ķ����ս����Ӱ�����Ϊ��ֵ����������ѯ,����2���Ǹ���ʵ�ʱ仯�ķ���Ϊ��ֵ����������ѯ
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
			// //System.out.println("w��count!=0,������");
			// continue;
			// }
			if (w.getName().equals("Request") || w.getName().equals("Provide")) {
				// System.out.println("w��count!=0,������");
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
			// HJQ_�����ԭʼ�ķ���,��������ж�
			// HJQ_�Ѳ��ɴ��Ҳ���ȥ,���ǸĽ�ǰ���㷨
			if (w.getCount() != 0) {
				// �Ѳ��ɴ��Ҳ���ȥ
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

				// �Ѳ��ɴ�Ĳ����ȥ
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
			
			//����qosChangedList
			List<WebService> qosChangedListOfCopy = null;
			try {
				qosChangedListOfCopy = DataGetter.copyWsList(qosChangedList);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//��¼�仯�ķ�����Ϣ
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
			double t3;//���ܵ�ʱ��
			double t4;//������ѯ��ʱ��
			/*****************������ѯ*******************/
			
			totalTime=0;
			timeList.clear();
			
			for(int i=0;i<groupNum;i++){//���groupNum��
				//reset PQMaxSize
				this.PQMaxSize = 0;
				
				//��RPT1��serviceMap1
				recoveryForHJQ(RPT1OfCopy);				
				recoveryWithNewQos(copyChangedList);
				recoveryQosChangedList(qosChangedList, qosChangedListOfCopy);
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����		
							//HJQ_���ò���1
				continuousQuery4(qosChangedList);			
				endMili=System.nanoTime();
				
				timeList.add(new Double((endMili-startMili)/1E6));
				//System.out.println("endMili-startMili: " + (endMili-startMili));
				//System.out.println("recoveryTime: " + recoveryTime);
				//System.out.println("endMili-startMili - recoveryTime: " + (endMili-startMili - recoveryTime));
				//System.out.println("strategy1�ܺ�ʱΪ:"+(endMili-startMili - recoveryTime)+"����");
												
			}
			
			sortDoubleList(timeList);
			//System.out.println("������ѯ:" + timeList);
			for(int i=(excludedNum != 1 ?(excludedNum - 1) : 1);i<groupNum - excludedNum;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t4=totalTime/(groupNum - 2 * excludedNum);
			
			/*********************������һ��*****************/
			
			totalTime=0;
			timeList.clear();
			//ͳ��ĳ�����յ����ľ�����
//			System.out.println("PQ max num:" + PQMaxSize);
			for(int i=0;i<groupNum;i++){//���groupNum��
				//�ָ�count
				recoveryReQueryForHJQ();
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����				
				reQuery();				
				endMili=System.nanoTime();
				timeList.add(new Double((endMili-startMili)/1E6));
				//System.out.println("reQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");											
			}

			
			sortDoubleList(timeList);
			//System.out.println("�ز�:" + timeList);
			for(int i=(excludedNum != 1 ?(excludedNum - 1) : 1);i<groupNum-excludedNum;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t3=totalTime/(groupNum - 2 * excludedNum);
			
			//ͳ���ز���������ѯʱ��
			this.requeryTime += t3;
			this.continueTime += t4;
			
			//ͳ����Ѳ���ʱ��
			this.bestStrategyTime += ((t4>t3)? t3: t4);
			
			double strategy1Time = -1;// ��¼����1����ʲô�㷨
			double strategy2Time = -1;// ��¼����2����ʲô�㷨
			String result = "";//��¼����1�Ͳ���2�ĶԱȽ��
			//HJQ ʵ��D,ͳ�Ʋ���1�Ͳ���2��ʤ�ʼ�ƽ��
			//����1����ʵ��,����2����ȫ��
			if((realSum <= this.strategy1Value && copy <= this.strategy2Value)){
				this.equalCount ++;
				
				strategy1Time = t4;
				strategy2Time = t4;
				
				//ͳ�Ʋ���ʱ��
				this.strategy1Time += t4;
				this.strategy2Time += t4;
				
				result = "ƽ" + System.getProperty("line.separator");
			}
			else if(realSum > this.strategy1Value && copy > this.strategy2Value){
				this.equalCount ++;
				
				strategy1Time = t3;
				strategy2Time = t3;
				
				//ͳ�Ʋ���ʱ��
				this.strategy1Time += t3;
				this.strategy2Time += t3;
				
				result = "ƽ" + System.getProperty("line.separator");
			}
			else if((realSum <= this.strategy1Value && copy > this.strategy2Value)){
				strategy1Time = t4;
				strategy2Time = t3;
				
				//ͳ�Ʋ���ʱ��
				this.strategy1Time += t4;
				this.strategy2Time += t3;
				
				if(t4 < t3){
					this.winCount ++;
					//*System.out.println("����1ʤ");
					result = "ʤ" + System.getProperty("line.separator");
				}
				else if(t4 == t3){
					this.equalCount ++;
					result = "ƽ" + System.getProperty("line.separator");
				}
				else{
					//*System.out.println("����1��");
					result = "��" + System.getProperty("line.separator");
				}
			}else if(realSum > this.strategy1Value && copy <= this.strategy2Value){
				strategy1Time = t3;
				strategy2Time = t4;
				
				//ͳ�Ʋ���ʱ��
				this.strategy1Time += t3;
				this.strategy2Time += t4;
				
				if(t3 < t4){
					this.winCount ++;
					//*System.out.println("����1ʤ");
					result = "ʤ" + System.getProperty("line.separator");
				}
				else if(t3 == t4){
					this.equalCount ++;
					result = "ƽ" + System.getProperty("line.separator");
				}
				else{
					//*System.out.println("����1��");
					result = "��" + System.getProperty("line.separator");
				}
			}
			
			//��������Ϣ��test.txt
			recordTime3.append(t3+System.getProperty("line.separator"));
			recordTime4.append(t4+System.getProperty("line.separator"));
			
			//��̨��������Ϣ
			//*System.out.println("�ز�ƽ����ʱΪ:"+t3+"����");
			//*System.out.println("������ѯƽ����ʱΪ:"+t4+"����");
			
			//��¼��ʽ�����ʵ��D��Ϣ
			recordTime5.append(copy + ",");
			recordTime5.append(realSum + ",");
			recordTime5.append(PQMaxSize + ",");
			recordTime5.append(strategy1Time + ",");
			recordTime5.append(strategy2Time + ",");			
			recordTime5.append(t3 + ",");
			recordTime5.append(t4 + ",");
			recordTime5.append(result);
			
		//*System.out.println("qos�ı��˵ķ��������"+copy);
		//*System.out.println("ʵ����Ҫ���µ�ws(count==0):�ĸ�����"+realSum);
		//*System.out.println("PQ���е����ֵ��"+PQMaxSize);
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
	 * �Ա��ز��������ѯ,�о������ı�qosֵ,û��ɾ��,�������Ĳ���,û�аѲ��ɴ������仯�ķ�����
	 * @param rPT1OfCopy 
	 * @param oTOfCopy 
	 * @param solveMapOfCopy 
	 */
	public void randomTest(Map<String, List<String>> solveMapOfCopy, Map<String, List<WebService>> OTOfCopy, Map<String, WebService> RPT1OfCopy){
		WebService w=null;
					//HJQ_���б仯�ķ���
		List<WebService> qosChangedList = new ArrayList<WebService>();
					//HJQ_���б仯�ķ���
		Set<WebService> wsSet = new HashSet<WebService>();
					//HJQ_ ������ <--> qos
		Map<String,Double> newQosMap = new HashMap<String,Double>();
					//HJQ_qosChangedList�ĸ���
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
//					//System.out.println("w��count!=0,������");
//					continue;				
//				}
				if(w.getName().equals("Request") || w.getName().equals("Provide")) {
					//System.out.println("w��count!=0,������");
					continue;				
				}
				
				boolean odd=false;
				odd = random.nextInt(100)%2!=0?true:false;
				//int newQos = (int)(random.nextDouble()*w.getSelfResponseTime()*2);
				int newQos = (int)(random.nextDouble()*30);
				//if(newQos==0) continue;
					//HJQ_��������qos���,��   ż������qos���,��
				if(odd){
					if(w.getSelfResponseTime()-newQos<=0) continue;
					else newQos = (int)w.getSelfResponseTime()-newQos;
				}else{
					newQos = (int)w.getSelfResponseTime()+newQos;
				}
				//System.out.println(w.getName()+" oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);			
				
				
				if(wsSet.contains(w)) continue;
	   						//HJQ_û�аѲ��ɴ��Ҳ���ȥ,���Ľ�����㷨
				if(w.getCount()!=0){
					//�Ѳ��ɴ��Ҳ���ȥ
					//ʵ��C��Ҫ������ע�͵�
					w.setSelfResponseTime(newQos);
					wsSet.add(w);
					qosChangedList.add(w);
					sum--;
					
					//copy qosChangedList
					WebService ws = new WebService();
					ws.setName(w.getName());
					ws.setSelfResponseTime(w.getSelfResponseTime());					
					copyChangedList.add(ws);
					
					//�Ѳ��ɴ�Ĳ����ȥ
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
			
			//����qosChangedList
			List<WebService> qosChangedListOfCopy = null;
			try {
				qosChangedListOfCopy = DataGetter.copyWsList(qosChangedList);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//����RPT1��serviceMap
			//copy();
			
			double totalTime=0;
			long startMili=0;
			long endMili=0;
			List<Double> timeList = new ArrayList<Double>();  
			double t1;//������ѯ��ʱ��
			double t2;//�ز��ʱ��
			
			//���潫�Ա仯��ķ����зֱ����������ѯ���ز�,�Ƚϻ��ѵ�ʱ��
			
			for(int i=0;i<10;i++){//���10��
				this.PQMaxSize = 0;
				//��RPT1��serviceMap1
				recoveryForHJQ(RPT1OfCopy);				
				recoveryWithNewQos(copyChangedList);
				recoveryQosChangedList(qosChangedList, qosChangedListOfCopy);
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����				
				continuousQuery4(qosChangedList);				
				endMili=System.nanoTime();
				timeList.add(new Double((endMili-startMili)/1E6));
				//System.out.println("continuousQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");
				
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t1=totalTime/8;
			
			//System.out.println("continuousQuery���� :"+endMili);
			//*System.out.println("PQ��󳤶���" + this.PQMaxSize);
			//*System.out.println("continuousQueryTotal�ܺ�ʱΪ:"+totalTime/8+"����");
			
			recordTime1.append(totalTime/8+System.getProperty("line.separator"));
			
			//*System.out.println("continuous���Ž���:"+continousQos);
			//cjh add
			continueTime+=t1;
			
			totalTime=0;
			timeList.clear();
			
			for(int i=0;i<10;i++){
				//HJQ
				recoveryReQueryForHJQ();
				recoverysolveMap(solveMapOfCopy);
				recoveryOTForHJQ(OTOfCopy);
				
				startMili=System.nanoTime();// ��ǰʱ���Ӧ�ĺ�����						
				reQuery();
				endMili=System.nanoTime();
				//System.out.println("reQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");
				timeList.add(new Double((endMili-startMili)/1E6));
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}	
			
			t2=totalTime/8;
			//cjh add
			requeryTime+= t2;
			//��¼ʱ���С
			if(t1<t2){
				recordCompare.append(1+System.getProperty("line.separator"));
			}else 
				if(t1==t2) recordCompare.append(0+System.getProperty("line.separator"));
				else recordCompare.append(-1+System.getProperty("line.separator"));
			
			//*System.out.println("���Ž��qos:"+requeryQos);
			
			recordTime2.append(totalTime/8+System.getProperty("line.separator"));
			
			//*System.out.println("reQueryTotal�ܺ�ʱΪ:"+totalTime/8+"����");
			
			if(requeryQos==continousQos){
				//System.out.println("ǰ����ȷ");		
			}else{
				//System.out.println("ǰ��bubububububu��ȷ");		
			}
			
			//��¼ʵ����Ҫ���µ�ws(count!=0):�ĸ���
			recordRealSum.append(realSum+System.getProperty("line.separator"));
			
			//*System.out.println("ʵ����Ҫ���µ�ws(count!=0):�ĸ�����"+realSum);
	}
	
	//HJQ_ ��copyChangedList�еķ������qos ���ǵ�serviceMap��Ӧ�����qos
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
	
	//HJQ_ ��serviceMap1 �ָ��� serviceMap
	public void recovery(){
		//HJQ_ ��serviceMap1 �ָ��� serviceMap
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
	
	 //HJQ_����RPT1 - > RPT2  ����serviceMap - > serviceMap1
	public void copy(){
		//HJQ_����serviceMap - > serviceMap1
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
        //HJQ_����RPT1 - > RPT2
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
	
	//HJQ_RPT��������
	public  void backward(List<String> outputsFromChallenge,Map<String,WebService> RPT,Map<String,WebService> serviceMap){
		List<WebService> parents = new ArrayList<WebService>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();
		Set<WebService> parentSet = new HashSet<WebService>();
		Map<String,ArrayList<String>> childParents = new HashMap<String,ArrayList<String>>();

		for(int i=0;i<outputsFromChallenge.size();i++){
			if(RPT.get(outputsFromChallenge.get(i)).getName().equals("serv1859188453"));
			//*System.out.println("����2031");
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
					//*System.out.println("����201");
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
//		//��ͼ��
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
//			System.out.println(parents2.get(i).getName()+"��parents��");
//			for(int k=0;k<p.size();k++){
//				System.out.print(p.get(k)+" ");
//			}
//			System.out.println();
//		}

	}
	
	//HJQ_��������RPT1
	public  void backwardForRPT1(List<String> outputsFromChallenge,Map<String,WebService> RPT1,Map<String,WebService> serviceMap){
		List<WebService> parents = new ArrayList<WebService>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();
		Set<WebService> parentSet = new HashSet<WebService>();
		Map<String,ArrayList<String>> childParents = new HashMap<String,ArrayList<String>>();
		for(int i=0;i<outputsFromChallenge.size();i++){
			if(RPT1.get(outputsFromChallenge.get(i)).getName().equals("serv1859188453"))
				//System.out.println("����2031");
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
					//System.out.println("����201");
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
	
	//HJQ_����RPT1���ս��(ÿ�������qos�Լ����ս����ͼ�ṹ)
	public void recordBackWardForRPT1(){
		//HJQ_��������
		List<WebService> parents = new ArrayList<WebService>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();//HJQ_������result
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
		//HJQ_������
		recordBackWard=null;
		recordBackWard=recordBackWard+"**backward**"+"\n";
		for(int i=0;i<parents2.size();i++){
			recordBackWard=recordBackWard+parents2.get(i).getName()+" "+parents2.get(i).getSelfResponseTime()+" "+parents2.get(i).getAllResponseTime()+"\n";			
		}
		recordBackWard=recordBackWard+"Request"+"\n"+"**END-backward*"+"\n";		
		
		//HJQ_��������Request����(�൱���ҵ�Response)
		parents2.add(serviceMap.get("Request"));
		
		for(int i=parents2.size()-1;i>=0;i--){
			parentSet.clear();
			//HJQ_����ÿһ��inputs��inst
			for(int j=0;j<parents2.get(i).getInputs().size();j++){
				//HJQ_���յ���ǰ�ҷ���
				WebService w = RPT.get(parents2.get(i).getInputs().get(j));
				//HJQ_���������        ��һ��������ǰһ������ĸ���
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
		//HJQ_���浽   recordBackWard
		for(int i=parents2.size()-1;i>0;i--){
			List<String> p = childParents.get(parents2.get(i).getName());
			recordBackWard=recordBackWard+parents2.get(i).getName()+"��parents��"+"\n";
			for(int k=0;k<p.size();k++){				
				recordBackWard=recordBackWard+p.get(k)+" ";
			}			
			recordBackWard=recordBackWard+"\n";
		}
		
	}
	
	//HJQ_��ȡ��ս������ͻ�Ӧ
	public  void getChanllenge(List<String> inputsFromChallenge,List<String> outputsFromChallenge){
		File xmlFile = chooseDataSetListener.getChallenge();  
		System.out.println(xmlFile.getPath()); 
		
		 if(xmlFile.exists()){  
		        SAXReader reader = new SAXReader();
		        System.out.println("exist");       
		        try {  
    	                //�����ĵ���  
    		                Document document = reader.read(xmlFile);  
    		            //��ȡ���ڵ�  
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
	
	//HJQ_����IIT
	public  void constructIIT(Map<String,WebService>serviceMap,Map<String,List<WebService>> IIT){
		for(String serviceName : serviceMap.keySet()){
			WebService webservice = serviceMap.get(serviceName);
			//����inputԪ��
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
	//HJQ_�����ֿ��ǵõ���ѵĵķ������     ����һ�β�ѯ�Ĵ����, ������OT��solveMap�ڴ˴���֪�к��ô�
	public  void getOptimalWebServiceComposition(List<String> inputsFromChallenge, List<String> outputsFromChallenge,
			Map<String,List<WebService>> IIT,Map<String,String> map1,Map<String,List<String>> map2,Map<String,WebService> RPT){
			int sumOfRequest = outputsFromChallenge.size();
			WebService begin = new WebService();
			WebService end = new WebService();
			List<WebService> reachVertices = new ArrayList<WebService>();
			
			//HJQ_������ʼ��
			begin.setOutputs(inputsFromChallenge);
			begin.setName("Provide");
			begin.setInputs(new ArrayList<WebService>());
			begin.setSelfResponseTime(0);
			begin.setAllResponseTime(0);
			end.setName("Request");
			end.setInputs(outputsFromChallenge);
			serviceMap.put("Provide",begin);
			serviceMap.put("Request", end);
			
			//����end��inputs��IIT
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
			
			System.out.println("������ս��Ҫ��");
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
//					System.out.println(ww.getName()+"�ķ���ʱ��"+ww.getAllResponseTime());
//				}
//				System.out.println("************");
				
				
				WebService v = reachVertices.remove(0);
				sumOfCanUseWS++;//HJQ_
				for(Object output:v.getOutputs()){
					
					///*********���OT*********
					List<String> matchList1 = findInstances(output.toString(),map1,map2);//�����ܱ�output�����inst	
					
					//HJQ_���OT
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
					
					if(!RPT.containsKey(output.toString())){//�µ�inst
						//RPT.put(output.toString(),v);
						//RPT1.put(output.toString(),v);
						//System.out.println("�µ�inst:"+output.toString());
						//String con = map1.get(output).toString();
						List<String> matchList = findInstances(output.toString(),map1,map2);//�����ܱ�output�����inst						
						
						//*********��ȫ���solveMap*********
						for(int o=0;o<matchList.size();o++)
							if(!solveMap.containsKey(matchList.get(o))){
								List<String> matchList2 = findInstances(matchList.get(o),map1,map2);//�����ܱ�output�����inst
								solveMap.put(matchList.get(o), matchList2);
							}					
						//*********************************
						
						//HJQ_����ͬ��inst ������һλ 
						if(!output.toString().equals(matchList.get(0)))//ʹmatchList���A1-A2-A3-A4  ?????
							for(int i=1;i<matchList.size();i++){
								if(output.toString().equals(matchList.get(i))){
									String st = matchList.get(0);
									matchList.set(0, matchList.get(i));
									matchList.set(i, st);
									break;
								}									
							}
						//solveMap.put(output.toString(), matchList);
						
						//System.out.println("matchList�Ĵ�С��"+matchList.size());
						boolean firstPlace=true;
						for(String inst : matchList){
							
							if(!RPT.containsKey(inst)){
								 RPT.put(inst, v);
								 RPT1.put(inst, v);
								 //System.out.println("�µ�inst*:"+inst);
							 }else{
								 continue;
							 }
							
							//if(!firstPlace && RPT.containsKey(inst)) continue;
							firstPlace=false;
							 List<WebService> haveSameInstWebServiceList = IIT.get(inst);
							 if(haveSameInstWebServiceList==null){
								 //System.out.println("û���κη�����Ҫ��"+inst);
								 continue;
							 }
							 
							 
							 //System.out.println("��"+haveSameInstWebServiceList.size()+"��������Ҫ"+inst+" "+v.getName() );
							 for(WebService w: haveSameInstWebServiceList){						
								 w.subCount();
								 ///if(w.getName().equals("serv612685309")) System.out.println(w.getName()+"��ʣ"+w.getCount()+"��Ԫ��");
								 if(w.getName().equals("Request")) System.out.println("��ʣ"+end.getCount()+"��Ԫ��");
								 if(w.getCount()==0){
									 w.setAllResponseTime(v.getAllResponseTime()+w.getSelfResponseTime());									
									 //enabledServices.put(w.getName(), w);
									 
									 if(w.getName().equals("Request"))
										 if(w.getCount()==0){
											 System.out.println("�ҵ���ȫ��Ҫ���Ԫ��");
											 System.out.println(end.getAllResponseTime());
											 find = true;										 
											 //return;
											 continue;
										 }
									 
										 reachVertices.add(w);
										 //System.out.println("����"+w.getName()+"������");
										// System.out.println("����"+w.getName()+"��AllRequestTime:"+w.getAllResponseTime());
										 /////////serv1027640201 serv404388610  404388610
										
										 
										 
										 /////////
									 
								 }
							 }
							 
						}
						
					}
				}
			}
			if(!find)
				System.out.println("�Ҳ���");
			System.out.println("RPT��С:"+RPT.size());
	}
	
	//HJQ_��ȡ��webService��QOS
	public  void getQos(Map<String,WebService> serviceMap){
		File xmlFile = chooseDataSetListener.getServiceLevelAgreement();  
		///System.out.println(xmlFile.getPath()); 
		
		 if(xmlFile.exists()){  
		        SAXReader reader = new SAXReader();
		        System.out.println("exist");       
		        try {  
    	                //�����ĵ���  
    		                Document document = reader.read(xmlFile);  
    		            //��ȡ���ڵ�  
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
    		                    	  //��һ����ServiceLevelObjectiveResponsetime���ڶ�����ServiceLevelObjectiveThroughput
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
    		                System.out.println("��"+serviceMap.size()+"���񣬶�Ӧ"+sumOfQos/2+"��qos");
    		                         		                         		                
		        }catch(Exception ex){
		        	ex.printStackTrace();
		        }
		        }
	}
	
	//HJQ_��ȡwebService�б�,����ȡ����webService
	public  void constructWebServiceList(Map<String,WebService> serviceMap){
		File xmlFile =  chooseDataSetListener.getServices();
		///System.out.println(xmlFile.getPath()); 
		
		 if(xmlFile.exists()){  
		        SAXReader reader = new SAXReader();
		        System.out.println("exist");       
		        try {  
    	                //�����ĵ���  
    		                Document document = reader.read(xmlFile);  
    		            //��ȡ���ڵ�  
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
    		                 
    		                //���testServiceList
    		                Set<String> key = serviceMap.keySet();
    		                for (Iterator it = key.iterator(); it.hasNext();) {
    		                    String s = (String) it.next();
    		                    WebService ws = serviceMap.get(s);    		                    
    		                    testServiceList.add(ws);
    		                }
    		                
    		                
    		                //This is for test
    		                System.out.println("����������"+serviceMap.size());
//    		                for(int i=0;i<webserviceList.get(571).getInputs().size();i++){
//    		                	System.out.println(webserviceList.get(571).getInputs().get(i).toString());
//    		                }
    		                
		        }catch(Exception ex){
		        	ex.printStackTrace();
		        }
	}
	}
	
	//HJQ_Ѱ�ҿ�ƥ���
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
	
	//HJQ_��ȡ����
	public void constructMap1Map2(Map map1,Map map2){
		//map1:��-���࣬ʵ��-��
				//map2:��-ʵ����
		        
				File xmlFile = chooseDataSetListener.getTaxonomy();
				System.out.println(xmlFile.getPath()); 
				
				 if(xmlFile.exists()){  
				        SAXReader reader = new SAXReader();
				        System.out.println("exist");
			    
				        try {  
		    	                //�����ĵ���  
		    		                Document document = reader.read(xmlFile);  
		    		            //��ȡ���ڵ�  
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
		        		                      
		        		                      //�õ�һ��Map(con****,list<instance>)
		        		                      if(map2.containsKey(foo1.attributeValue("resource"))){
		        		                    	  //�Ѿ���ĳ��con��ʵ�������԰��µ�insҪ�ӵ�listȥ
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
	 * ͳ�ƿ��--4�����
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
	 * ͳ��ĳ�����յ����ľ���
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
	 * ͳ��ʵ�������е�ƽ�����(���Ͽ�ƥ��)
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
	 * ͳ��ʵ�������е�ƽ�����(�����Ͽ�ƥ��)
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
	 * ͳ��ȫ�������е�ƽ�����(���Ͽ�ƥ��)
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
	 * ͳ��ȫ�������е�ƽ�����(�����Ͽ�ƥ��)
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
			
			//�ҳ�����output������inst
			Set<String> insts = new HashSet<String>();
			
			if(ws.getOutputs() != null){
				for(String inst : (List<String>)ws.getOutputs()){
					insts.add(inst);
					if(solveMap.get(inst) != null){
						insts.addAll(solveMap.get(inst));
					}
				}
			}
			
			//�ҳ����п��ܵĲ�����
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
			
			//�ҵ���
			ws2NextWSNumBean.setNextWSNum(webServices.size());
			
			ws2NextWSNumBeans.add(ws2NextWSNumBean);
			rankMap.put(ws2NextWSNumBean.getWsName(), ws2NextWSNumBean.getNextWSNum());
		}
		//����
		Collections.sort(ws2NextWSNumBeans);
		Collections.reverse(ws2NextWSNumBeans);
		
		if(reachFlag){
			PrintRankResult.getInstance().print("ǰ20����" + System.lineSeparator());
		}
		else{
			PrintRankResult.getInstance().print("ȫ��" + System.lineSeparator());
		}
		
		//���ǰȫ�����ļ�
		for(int i = 0; i < ws2NextWSNumBeans.size(); i++){
			PrintRankResult.getInstance().print
					((i + 1) + "," +  ws2NextWSNumBeans.get(i).toString() + System.lineSeparator());
		}
		
	}
}
