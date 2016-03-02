package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pojo.WebService;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MyFrame {

	private JFrame frame;
	private ChooseDataSetListener  chooseDataSetListener;
	private Map<String,String> map1;
	private Map<String,List<String>> map2;
	private Map<String,WebService> serviceMap;
	private Map<String,WebService> RPT;
	private Map<String,List<WebService>> IIT;
	private List<String> inputsFromChallenge;
	private List<String> outputsFromChallenge;
	private Map<String,WebService> enabledServices;
	private Map<String,List<WebService>> OT;
	private Map<String,List<String>> solveMap;
	private double continousQos;
	private double requeryQos;
	private List<WebService> testServiceList; 
	private int count;
	private Map<String,WebService> RPT1;
	private int sumOfChangedService;
	private int successTime;
	private String recordBackWard;
	private String recordSomething;
	private int realSum;//实际可以用的ws总数
	private int sumOfCanUseWS;//最后可达的ws,即count=0
	
	//为了测试10组相同的数据而复制的数据结构
	private Map<String,WebService> serviceMap1;
	private Map<String,WebService> RPT2;
	
	private byte[] buff;
	private FileOutputStream out1;
	private StringBuilder recordTime1;
	private StringBuilder recordTime2;
	private StringBuilder recordTime3;
	private StringBuilder recordCompare;
	private StringBuilder recordRealSum;
	
	private double win = 0;
	private double ping = 0;
	
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
	     recordCompare=new StringBuilder();
	 	 recordRealSum=new StringBuilder();
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
				String  str1 = JOptionPane.showInputDialog("测试多少组？");
				if(str1!=null){
					String  str2 = JOptionPane.showInputDialog("变更多少个服务的qos？");
					if(str2!=null){							  
					     recordTime1.setLength(0);
					     recordTime2.setLength(0);
					     recordCompare.setLength(0);
					 	 recordRealSum.setLength(0);
						
						count=Integer.parseInt(str1);	//变更的组数
						sumOfChangedService=Integer.parseInt(str2);	//改变的服务总数
						
						try {
							testManyTime();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("mmmmmm");
						}
					}
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
							testManyTime1();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("186");
							e.printStackTrace();
						}
					}
				}
			
		});
		btnNewButton_1.setBounds(10, 217, 93, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnRun = new JButton("Run1");
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
		
	public void strategy(int choice,int sum,int realSum,List<WebService> qosChangedList){
		
		if(choice==1){
			if(realSum<=22){
				//long startMili=System.currentTimeMillis();
				continuousQuery4(qosChangedList);
				//long endMili=System.currentTimeMillis();
				//System.out.println("continuousQuery结束 :"+endMili);
				//System.out.println("myStratege总耗时为:"+(endMili-startMili)+"毫秒");			
				return;
			}	
		}else{
			if(sum<=600){
				//long startMili=System.currentTimeMillis();
				continuousQuery4(qosChangedList);
				//long endMili=System.currentTimeMillis();
				//System.out.println("continuousQuery结束 :"+endMili);
				//System.out.println("myStratege总耗时为:"+(endMili-startMili)+"毫秒");			
				return;
			}	
		}
			
		   
		
		int sumOfRequest = outputsFromChallenge.size();
		WebService begin = serviceMap.get("Provide");
		WebService end = serviceMap.get("Request");
		end.setCount(end.getInputs().size());
		List<WebService> reachVertices = new ArrayList<WebService>();
		
		Set<String> key = serviceMap.keySet();
		String s;
		WebService ws;
        for (Iterator it = key.iterator(); it.hasNext();) {
            s = (String) it.next();
            ws = serviceMap.get(s);
            ws.setCount(ws.getInputs().size());
            ws.setAllResponseTime(0);
            ws.setNewAllResponseTime(0);
        }	
			
        RPT.clear();      
        
     
        //long startMili=System.currentTimeMillis();
        
        
		boolean find=false;
		reachVertices.add(begin);
		
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
		
		continousQos = serviceMap.get("Request").getAllResponseTime();		

		
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
	
	public void testManyTime() throws IOException{
		
		copy();	//复制RPT1和serviceMap
		
		int time=0;		
		while(time<count){			
			time++;
			System.out.println("第"+time+"组");	
			if(time==1){
				recordTime1.append("continuousQuery的用时"+System.getProperty("line.separator"));
				recordTime2.append("reQuery的用时"+System.getProperty("line.separator"));
				recordCompare.append("比较"+System.getProperty("line.separator"));
				recordRealSum.append("实际需要更新的服务个数"+System.getProperty("line.separator"));
			}
			randomTest();					
		}
		
		
		//输出结果
		buff=new byte[]{};
		if(out1==null) out1=new FileOutputStream("text.txt");
		
		buff=recordRealSum.toString().getBytes();                       
        out1.write(buff,0,buff.length);
        buff=recordTime1.toString().getBytes();        
        out1.write(buff,0,buff.length);
        buff=recordTime2.toString().getBytes();        
        out1.write(buff,0,buff.length);
        buff=recordCompare.toString().getBytes();        
        out1.write(buff,0,buff.length);
		out1.close();
	}
	
	public void testManyTime1() throws IOException{
		copy();
		int time=0;
		while(time<count){			
			time++;
			if(time==1){
				recordTime1.append("myStratege"+System.getProperty("line.separator"));
				recordTime2.append("oldStratege"+System.getProperty("line.separator"));
				recordTime3.append("requery"+System.getProperty("line.separator"));				
			}
			
			System.out.println("第"+time+"组");	
			randomTest1();					
		}
		buff=new byte[]{};
		if(out1==null) out1=new FileOutputStream("text.txt");
				                              
        buff=recordTime1.toString().getBytes();        
        out1.write(buff,0,buff.length);
        buff=recordTime2.toString().getBytes();        
        out1.write(buff,0,buff.length);
        buff=recordTime3.toString().getBytes();        
        out1.write(buff,0,buff.length);
		out1.close();
		
		System.out.println("策略1胜:" + this.win);
		System.out.println("策略1 2 平:" + this.ping);
		System.out.println("策略1负:" + (this.count - this.win - this.ping));
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
	
	public WebService getCriticalParent(WebService successor){
			Set<WebService> parentSet = new HashSet<WebService>();
			WebService w = null;
			List<WebService> parentList  = new ArrayList<WebService>();
			
			for(int i=0;i<successor.getInputs().size();i++){
				 w = RPT1.get(successor.getInputs().get(i));			
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

	//暂时没用到的
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
	
	//暂时没有用到的
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
	
	//
	public void continuousQuery4(List<WebService> qosChangedList){
		//这个方法不更新没人需要的inst的最优parent
		List<WebService> PQ = new ArrayList<WebService>();
		
		for(int i=0;i<qosChangedList.size();i++){
			if(qosChangedList.get(i).getCount()!=0){ 
				continue;
			}
			PQ.add(qosChangedList.get(i));
		}
		
		WebService first;
				
		while(!PQ.isEmpty()){
			sort(PQ);
			
//			System.out.println("***PQ****");
//			//recordSomething=recordSomething+"***PQ****"+"\n";
//			for(int i=0;i<PQ.size();i++){
//				double original = PQ.get(i).getAllResponseTime();
//				double newOne = PQ.get(i).getNewAllResponseTime();
//				double min =original>newOne?newOne:original;
//				System.out.println(PQ.get(i).getName()+"的min是:"+min);				
//				//recordSomething=recordSomething+PQ.get(i).getName()+"的min是:"+min+"\n";
//			}
//			recordSomething=recordSomething+"******"+"\n";
//			System.out.println("*********");
			
		    first = PQ.remove(0);
									
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	//System.out.println("******到了Request******");		
				System.out.println("更新到了终点 ， 此时终点的AllQos为: " + first.getAllResponseTime());
				continue;
		    	//break;
		    }
		    
		    List<WebService> successorList;
			if(first.getNewAllResponseTime() < first.getAllResponseTime()){
				
				//System.out.println("qos变小");
				
				for(int i=0;i<first.getOutputs().size();i++){
					
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						System.out.println("填充solveMap");
						solveMap.put(output, matchList);
					}
					
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						//不更新没人要的inst的父亲
						if(IIT.get(inst)==null){
							continue;
						} 
							
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
						
					    successorList = IIT.get(inst);
					    
						if(successorList==null)
							continue;
						
						for(int u=0;u<successorList.size();u++){
							
							//如果该后继点可达
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
								}
								
								else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk有问题");
										//如果认识没错，数据集有缺陷
										
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"的allqos为"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											PQ.add(successor);			//newQos!=allQos:该WS为affected服务							
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
			
			//qos变坏的情况
			else{	
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
						
						//不更新没人要的inst的父亲
						if(IIT.get(inst)==null) 
							continue;
						
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
								}
								else{
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
										//System.out.println("测试条件：后继点的QOS没有大于0 : " + successor.getNewAllResponseTime());
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
		
		
		System.out.println("终点此时的newQos为： " + serviceMap.get("Request").getNewAllResponseTime());
		System.out.println("终点此时的allQos为： " + serviceMap.get("Request").getAllResponseTime());
		System.out.println("此时的requryQos为： " + this.requeryQos);
		System.out.println("此时的continousQos为： " + this.continousQos);
		
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
	
	public void sort(List<WebService> PQ){
		Collections.sort(PQ,new Comparator<WebService>(){

			public int compare(WebService arg0, WebService arg1) {
				if(arg0==null || arg1==null ) System.out.println("pppppp");
				if(arg0==arg1)	System.out.println("vvvvvvv");
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
		
	public void sortDoubleList(List<Double> list){
		Collections.sort(list,new Comparator<Double>(){

			public int compare(Double arg0, Double arg1) {
				// TODO Auto-generated method stub
				if(arg0>arg1) return 1;
				else if(arg0==arg1)
						return 0;
					else
						return -1;
			}
			
		});
	}
		
	
	public void reQuery(){//改成找到了request就直接结束
		
	    	//System.out.println("reqQuery开始: "+startMili);
		
		int sumOfRequest = outputsFromChallenge.size();
		WebService begin = serviceMap.get("Provide");
		WebService end = serviceMap.get("Request");
		end.setCount(end.getInputs().size());
		List<WebService> reachVertices = new ArrayList<WebService>();
		
		Set<String> key = serviceMap.keySet();
		String s;
        for (Iterator it = key.iterator(); it.hasNext();) {
            s = (String) it.next();
            WebService ws = serviceMap.get(s);
            ws.setCount(ws.getInputs().size());
            ws.setAllResponseTime(0);
            ws.setNewAllResponseTime(0);
        }	
			
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
		
	
	public void randomTest1(){
		WebService w=null;
		List<WebService> qosChangedList = new ArrayList<WebService>();
		Set<WebService> wsSet = new HashSet<WebService>();
		Map<String,Double> newQosMap = new HashMap<String,Double>();
		Random random = new Random();
		int sum = (int)(random.nextDouble()*1000);
		List<WebService> copyChangedList = new ArrayList<WebService>();
		
		realSum=0;
		int copy = sum;
		
			while(sum>0){
				
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
				if(odd){
					if(w.getSelfResponseTime()-newQos<=0) continue;
					else newQos = (int)w.getSelfResponseTime()-newQos;
				}else{
					newQos = (int)w.getSelfResponseTime()+newQos;
				}
				//System.out.println(w.getName()+" oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);			
				
				
				
				if(wsSet.contains(w)) continue;
				if(w.getCount()!=0){
					//把不可达的也算进去
					w.setSelfResponseTime(newQos);
					wsSet.add(w);
					qosChangedList.add(w);
					sum--;
					
					//copy qosChangedList
					WebService ws = new WebService();
					ws.setName(w.getName());
					ws.setSelfResponseTime(w.getSelfResponseTime());					
					copyChangedList.add(ws);
					
					continue;
					
					//把不可达的不算进去
//					continue;					
				}else{
					realSum++;
				}
				
				w.setNewAllResponseTime(w.getAllResponseTime()-w.getSelfResponseTime()+newQos);
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
			
			
			
			double totalTime=0;
			long startMili=0;
			long endMili=0;
			List<Double> timeList = new ArrayList<Double>();  
			double t1;//策略1的时间,实际结构
			double t2;//策略2的时间,动态
			double t3;//重跑的时间
			
			
			for(int i=0;i<10;i++){//测出10组
				//还RPT1和serviceMap1
				recovery();				
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// 当前时间对应的毫秒数				
				strategy(1,copy,realSum,qosChangedList);				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery总耗时为:"+(endMili-startMili)+"毫秒");
												
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t1=totalTime/8;
						
			recordTime1.append(totalTime/8+System.getProperty("line.separator"));
			
			/*****************Stratege2*******************/
			for(int i=0;i<10;i++){//测出10组
				//还RPT1和serviceMap1
				recovery();				
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// 当前时间对应的毫秒数				
				strategy(2,copy,realSum,qosChangedList);				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery总耗时为:"+(endMili-startMili)+"毫秒");
												
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t2=totalTime/8;
						
			recordTime2.append(totalTime/8+System.getProperty("line.separator"));
			
			//*********************重新跑一遍*****************/
			for(int i=0;i<10;i++){//测出10组
				//还RPT1和serviceMap1
				recovery();				
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// 当前时间对应的毫秒数				
				reQuery();				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery总耗时为:"+(endMili-startMili)+"毫秒");											
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t3=totalTime/8;
						
			recordTime3.append(totalTime/8+System.getProperty("line.separator"));
			
			if(t1 < t2){
				this.win++;
			}
			else if(t1 == t2){
				this.ping++;
			}
			
			
			//long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
			//System.out.println("continuousQuery开始: "+startMili);
						
			//选择策略myStratege		
			//strategy(1,copy,realSum,qosChangedList);
			
			//选择策略oldStratge
			//strategy(2,copy,realSum,qosChangedList);
			
			//long endMili=System.currentTimeMillis();
			//System.out.println("continuousQuery结束 :"+endMili);
			//System.out.println("myStratege总耗时为:"+(endMili-startMili)+"毫秒");
			
												
		
		//System.out.println("continuous最优解是:"+continousQos);
		//reQuery();
		System.out.println("qos改变了的服务个数："+copy);
		System.out.println("实际需要更新的ws(count!=0):的个数："+realSum);
		
	}
	
	
	public void randomTest(){
		WebService w=null;
		
		List<WebService> qosChangedList = new ArrayList<WebService>();	//Qos改变了的ws集合
		Set<WebService> wsSet = new HashSet<WebService>();	//wsSet,过滤效果
		Map<String,Double> newQosMap = new HashMap<String,Double>();
		List<WebService> copyChangedList = new ArrayList<WebService>();  //无关紧要的变量
		
		int sum;
		realSum=0;
		
			sum=sumOfChangedService;
			
			while(sum>0){
				Random random = new Random();
				int r = random.nextInt(testServiceList.size());	//从中选取一个ws改变
				
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
				}	//如果是初始节点 或者 是 最终节点 ， 则不进行处理。
				
				//odd 变量用于确定 该WS的QOS是变好 还是 变坏~~
				boolean odd=false;
				odd = random.nextInt(100)%2 !=0 ? true:false;
				
				//int newQos = (int)(random.nextDouble()*w.getSelfResponseTime()*2);
				
				int newQos = (int)(random.nextDouble()*30);
				
				//if(newQos==0) continue;
				
				//odd 为true 时 ， Qos变好
				if(odd){
					if(w.getSelfResponseTime()-newQos<=0) continue;
					else newQos = (int)w.getSelfResponseTime()-newQos;
				}
				//否则Qos变坏
				else{
					newQos = (int)w.getSelfResponseTime()+newQos;
				}
				
				//System.out.println(w.getName()+" oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);			
				
				
				if(wsSet.contains(w)) continue;
				
				if(w.getCount()!=0){
					//把不可达的也算进去
//					w.setSelfResponseTime(newQos);
//					wsSet.add(w);
//					qosChangedList.add(w);
//					sum--;
//					
//					//copy qosChangedList
//					WebService ws = new WebService();
//					ws.setName(w.getName());
//					ws.setSelfResponseTime(w.getSelfResponseTime());					
//					copyChangedList.add(ws);
//					
//					continue;
					
					//把不可达的不算进去
					continue;					
				}
				else{
					realSum++;
				}
				
				w.setNewAllResponseTime(w.getAllResponseTime()-w.getSelfResponseTime()+newQos);
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
			
			
			//----------------------------------------------------------------------------
			double totalTime=0;
			long startMili=0;
			long endMili=0;
			List<Double> timeList = new ArrayList<Double>();  
			double t1;//连续查询的时间
			double t2;//重查的时间
			
			
			for(int i=0;i<10;i++){//测出10组
				//还RPT1和serviceMap1
				recovery();
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// 当前时间对应的毫秒数				
				continuousQuery4(qosChangedList);				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery总耗时为:"+(endMili-startMili)+"毫秒");
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t1=totalTime/8;
			
			//System.out.println("continuousQuery结束 :"+endMili);
			System.out.println("continuousQueryTotal总耗时为:"+totalTime/8+"毫秒");
			
			recordTime1.append(totalTime/8+System.getProperty("line.separator"));
			
			System.out.println("continuous最优解是:"+continousQos);
			
			totalTime=0;
			timeList.clear();
			
			for(int i=0;i<10;i++){
				startMili=System.currentTimeMillis();// 当前时间对应的毫秒数						
				reQuery();
				endMili=System.currentTimeMillis();
				System.out.println("reQuery总耗时为:"+(endMili-startMili)+"毫秒");
				timeList.add(new Double(endMili-startMili));			
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}	
			
			t2=totalTime/8;
			
			//记录时间大小
			if(t1<t2){
				recordCompare.append(1+System.getProperty("line.separator"));
			}
			else 
				if(t1==t2) recordCompare.append(0+System.getProperty("line.separator"));
				else recordCompare.append(-1+System.getProperty("line.separator"));
			
			System.out.println("最优解的qos:"+requeryQos);
			
			recordTime2.append(totalTime/8+System.getProperty("line.separator"));
			
			System.out.println("reQueryTotal总耗时为:"+totalTime/8+"毫秒");
			
			if(requeryQos==continousQos){
				System.out.println("前后正确");		
			}else{
				System.out.println("前后bubububububu正确");		
			}
			
			//记录实际需要更新的ws(count!=0):的个数
			recordRealSum.append(realSum+System.getProperty("line.separator"));
			
			System.out.println("实际需要更新的ws(count!=0):的个数："+realSum);
			//-------------------------------------------------------------------------
	}
	
	
	public void recoveryWithNewQos(List<WebService> copyChangedList){
		
		for(int i=0;i<copyChangedList.size();i++){
			WebService record = copyChangedList.get(i);
			WebService ws = serviceMap.get(record.getName());
			ws.setAllResponseTime(record.getAllResponseTime());
			ws.setNewAllResponseTime(record.getNewAllResponseTime());
			ws.setSelfResponseTime(record.getSelfResponseTime());
		}
		
	}
	
	//一种恢复机制
	public void recovery(){
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
	
	public void copy(){
		Set<String> key = serviceMap.keySet();
		String s;
		
        for (Iterator it = key.iterator(); it.hasNext();) {
            s = (String) it.next();           
            WebService ws = serviceMap.get(s);
                                    
            WebService copy = new WebService();

            copy.setAllResponseTime(ws.getAllResponseTime());
            copy.setCount(ws.getCount());
            copy.setCriticalParent(ws.getCriticalParent());	//
            copy.setInputs(ws.getInputs());
            copy.setName(ws.getName());
            copy.setNewAllResponseTime(ws.getNewAllResponseTime()); //
            copy.setOutputs(ws.getOutputs());
            copy.setSelfResponseTime(ws.getSelfResponseTime());
            copy.setSelfThroughput(ws.getSelfThroughput()); //
            
            serviceMap1.put(s, copy);
        }	
        
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
	
	public  void backward(List<String> outputsFromChallenge,Map<String,WebService> RPT,Map<String,WebService> serviceMap){
		//Map<String,ArrayList<String>> childParents = new HashMap<String,ArrayList<String>>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		
		List<WebService> parents = new ArrayList<WebService>();
		List<WebService> parents2 = new ArrayList<WebService>();
		Set<WebService> parentSet = new HashSet<WebService>();
				
		for(int i=0;i<outputsFromChallenge.size();i++){
			//if(RPT.get(outputsFromChallenge.get(i)).getName().equals("serv1859188453"))
				//System.out.println("存在2031");
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
				if(RPT.get(ws.getInputs().get(i)).getName().equals("serv1723601037"))
					System.out.println("存在201");
				if(parentSet.add(RPT.get(ws.getInputs().get(i)))){
					parents.add(RPT.get(ws.getInputs().get(i)));
					parents2.add(RPT.get(ws.getInputs().get(i)));		
				}
			}
			
		}
		
		System.out.println("**backward**");
		
		for(int i=0;i<parents2.size();i++){
			System.out.println(parents2.get(i).getName()+" "+parents2.get(i).getSelfResponseTime()+" "+parents2.get(i).getAllResponseTime());
			
			WebService ws = parents2.get(i);
			ArrayList<String> wsL = (ArrayList<String>)ws.getOutputs();
			for(int j = 0 ; j < outputsFromChallenge.size(); j++){
				
				String need = outputsFromChallenge.get(j);
				boolean flag = false;
				
				for(int k = 0; k <wsL.size(); k++){
					if(need.equals(wsL.get(k))){
						flag = true;
						break;
					}
				}
				
				if(flag){
					System.out.println(need + " 找到了");
				}
				
			}
			
		}
		
		System.out.println("Request");
		System.out.println("END-backward");
		
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
	
	public void recordBackWardForRPT1(){
		List<WebService> parents = new ArrayList<WebService>();
		//List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();
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
		recordBackWard=null;
		recordBackWard=recordBackWard+"**backward**"+"\n";
		for(int i=0;i<parents2.size();i++){
			recordBackWard=recordBackWard+parents2.get(i).getName()+" "+parents2.get(i).getSelfResponseTime()+" "+parents2.get(i).getAllResponseTime()+"\n";			
		}
		recordBackWard=recordBackWard+"Request"+"\n"+"**END-backward*"+"\n";		
		
		
		parents2.add(serviceMap.get("Request"));
		for(int i=parents2.size()-1;i>=0;i--){
			parentSet.clear();
			for(int j=0;j<parents2.get(i).getInputs().size();j++){
				WebService w = RPT.get(parents2.get(i).getInputs().get(j));
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
		
		for(int i=parents2.size()-1;i>0;i--){
			List<String> p = childParents.get(parents2.get(i).getName());
			recordBackWard=recordBackWard+parents2.get(i).getName()+"的parents是"+"\n";
			for(int k=0;k<p.size();k++){				
				recordBackWard=recordBackWard+p.get(k)+" ";
			}			
			recordBackWard=recordBackWard+"\n";
		}
		
	}
	
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
	
	public  void getOptimalWebServiceComposition(List<String> inputsFromChallenge, List<String> outputsFromChallenge,
			Map<String,List<WebService>> IIT,Map<String,String> map1,Map<String,List<String>> map2,Map<String,WebService> RPT){
			
			int sumOfRequest = outputsFromChallenge.size();
			
			WebService begin = new WebService();
			WebService end = new WebService();
			
			List<WebService> reachVertices = new ArrayList<WebService>();
			
			System.out.println("服务列表长度为 : " + this.serviceMap.size());
			System.out.println("终点需要的输入为 : " + outputsFromChallenge.toString());
			System.out.println("Relations 的长度为 : " + this.map1.size());
			System.out.println("conToInsts 的长度为 : " + this.map2.size());
			
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
			
			System.out.println("IIT 的长度为 : " + IIT.size());
			
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
				
				//------------------------------------------------------------------------------------------------
				WebService v = reachVertices.remove(0);
				
				sumOfCanUseWS++;
				
				for(Object output:v.getOutputs()){
					
					///*********填充OT*********
					List<String> matchList1 = findInstances(output.toString(),map1,map2);//所有能被output解决的inst	
					
					for(int i=0;i<matchList1.size();i++){
						List<WebService> l = OT.get(matchList1.get(i));
						if(l==null){
							l = new ArrayList<WebService>();
							l.add(v);
							OT.put(matchList1.get(i), l);
						}
						else{
							l.add(v);
						}
					}
				//--------------------------------------------------------------------------------------------------
					
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
						
						//---------------不知道什么卵意思--------------------
						if(!output.toString().equals(matchList.get(0)))//使matchList变成A1-A2-A3-A4
							for(int i=1;i<matchList.size();i++){
								if(output.toString().equals(matchList.get(i))){
									String st = matchList.get(0);
									matchList.set(0, matchList.get(i));
									matchList.set(i, st);
									break;
								}									
							}
						//--------------------------------------------------
						
						//solveMap.put(output.toString(), matchList);
						
						//System.out.println("matchList的大小："+matchList.size());
						boolean firstPlace=true;
						
						for(String inst : matchList){
							
							if(!RPT.containsKey(inst)){
								 RPT.put(inst, v);
								 RPT1.put(inst, v);
								 //System.out.println("新的inst*:"+inst);
							 }
							else{
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
								 
								 if(w.getName().equals("Request"))
									 System.out.println("还剩"+end.getCount()+"个元素");
								 
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
										 //System.out.println("服务"+w.getName()+"的AllRequestTime:"+w.getAllResponseTime());
										 /////////serv1027640201 serv404388610  404388610
										
								 }
							 }
							 
						}
						
					}
				}
			}
			if(!find)
				System.out.println("找不到");
			System.out.println("RPT.size()"+RPT.size());
			
			//System.out.println(enabledServices.size());
	}
	
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
		        		                      }
		        		                      else{
		        		                    	  List instances  =  new ArrayList();
		        		                    	  instances.add(foo.attributeValue("ID"));
		        		                    	  map2.put( foo1.attributeValue("resource"), instances);
		        		                      }
		    		                      }
		    		                      sumofIns++;
		    		                 }
		    		                System.out.println(sumofCon);
		    		                System.out.println(sumofIns);
		    		                System.out.println("end");    		             
		    		                System.out.println("test:"+map1.get("inst1814744254").toString());
		    		                System.out.println(map1.size());
		    		                System.out.println(map2.size());
		    		                
		    		                findInstances("inst1544797788",map1,map2);
		    		                
				        }catch(Exception ex){
				        	
				        }
			}
				
				 
		}
}
