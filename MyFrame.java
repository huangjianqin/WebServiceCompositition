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
	private int realSum;//ʵ�ʿ����õ�ws����
	private int sumOfCanUseWS;//���ɴ��ws,��count=0
	
	//Ϊ�˲���10����ͬ�����ݶ����Ƶ����ݽṹ
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
	     recordCompare=new StringBuilder();
	 	 recordRealSum=new StringBuilder();
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
				String  str1 = JOptionPane.showInputDialog("���Զ����飿");
				if(str1!=null){
					String  str2 = JOptionPane.showInputDialog("������ٸ������qos��");
					if(str2!=null){							  
					     recordTime1.setLength(0);
					     recordTime2.setLength(0);
					     recordCompare.setLength(0);
					 	 recordRealSum.setLength(0);
						
						count=Integer.parseInt(str1);	//���������
						sumOfChangedService=Integer.parseInt(str2);	//�ı�ķ�������
						
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
			    System.out.println("�ɴ�ķ�����:"+sumOfCanUseWS);
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
				//System.out.println("continuousQuery���� :"+endMili);
				//System.out.println("myStratege�ܺ�ʱΪ:"+(endMili-startMili)+"����");			
				return;
			}	
		}else{
			if(sum<=600){
				//long startMili=System.currentTimeMillis();
				continuousQuery4(qosChangedList);
				//long endMili=System.currentTimeMillis();
				//System.out.println("continuousQuery���� :"+endMili);
				//System.out.println("myStratege�ܺ�ʱΪ:"+(endMili-startMili)+"����");			
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
		
		continousQos = serviceMap.get("Request").getAllResponseTime();		

		
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
	
	public void testManyTime() throws IOException{
		
		copy();	//����RPT1��serviceMap
		
		int time=0;		
		while(time<count){			
			time++;
			System.out.println("��"+time+"��");	
			if(time==1){
				recordTime1.append("continuousQuery����ʱ"+System.getProperty("line.separator"));
				recordTime2.append("reQuery����ʱ"+System.getProperty("line.separator"));
				recordCompare.append("�Ƚ�"+System.getProperty("line.separator"));
				recordRealSum.append("ʵ����Ҫ���µķ������"+System.getProperty("line.separator"));
			}
			randomTest();					
		}
		
		
		//������
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
			
			System.out.println("��"+time+"��");	
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
		
		System.out.println("����1ʤ:" + this.win);
		System.out.println("����1 2 ƽ:" + this.ping);
		System.out.println("����1��:" + (this.count - this.win - this.ping));
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

	//��ʱû�õ���
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
	
	//��ʱû���õ���
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
	
	//
	public void continuousQuery4(List<WebService> qosChangedList){
		//�������������û����Ҫ��inst������parent
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
//				System.out.println(PQ.get(i).getName()+"��min��:"+min);				
//				//recordSomething=recordSomething+PQ.get(i).getName()+"��min��:"+min+"\n";
//			}
//			recordSomething=recordSomething+"******"+"\n";
//			System.out.println("*********");
			
		    first = PQ.remove(0);
									
		    if(first.getName().equals("Request")){
				first.setAllResponseTime(first.getNewAllResponseTime());
		    	//System.out.println("******����Request******");		
				System.out.println("���µ����յ� �� ��ʱ�յ��AllQosΪ: " + first.getAllResponseTime());
				continue;
		    	//break;
		    }
		    
		    List<WebService> successorList;
			if(first.getNewAllResponseTime() < first.getAllResponseTime()){
				
				//System.out.println("qos��С");
				
				for(int i=0;i<first.getOutputs().size();i++){
					
					String output = first.getOutputs().get(i).toString();
					List<String> matchList = solveMap.get(output);
					
					if(matchList==null){
						matchList = findInstances(output,map1,map2);
						System.out.println("���solveMap");
						solveMap.put(output, matchList);
					}
					
					
					for(int k=0;k<matchList.size();k++){
						String inst = matchList.get(k);
						
						//������û��Ҫ��inst�ĸ���
						if(IIT.get(inst)==null){
							continue;
						} 
							
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
							
							//����ú�̵�ɴ�
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
								}
								
								else{
									if(successor.getNewAllResponseTime()>0){
										//System.out.println("kk������");
										//�����ʶû�����ݼ���ȱ��
										
										if(successor.getNewAllResponseTime()!=criticalParentQos+successor.getSelfResponseTime()){
										//	recordSomething=recordSomething+successor.getName()+" "+criticalParent.getName()+"��allqosΪ"+criticalParentQos+" "+successor.getNewAllResponseTime()+"--->"+criticalParentQos+successor.getSelfResponseTime()+"\n";
											successor.setNewAllResponseTime(criticalParentQos+successor.getSelfResponseTime());
											PQ.add(successor);			//newQos!=allQos:��WSΪaffected����							
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
			
			//qos�仵�����
			else{	
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
						
						//������û��Ҫ��inst�ĸ���
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
								}
								else{
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
										//System.out.println("������������̵��QOSû�д���0 : " + successor.getNewAllResponseTime());
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
		
		
		System.out.println("�յ��ʱ��newQosΪ�� " + serviceMap.get("Request").getNewAllResponseTime());
		System.out.println("�յ��ʱ��allQosΪ�� " + serviceMap.get("Request").getAllResponseTime());
		System.out.println("��ʱ��requryQosΪ�� " + this.requeryQos);
		System.out.println("��ʱ��continousQosΪ�� " + this.continousQos);
		
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
		
	
	public void reQuery(){//�ĳ��ҵ���request��ֱ�ӽ���
		
	    	//System.out.println("reqQuery��ʼ: "+startMili);
		
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
				if(odd){
					if(w.getSelfResponseTime()-newQos<=0) continue;
					else newQos = (int)w.getSelfResponseTime()-newQos;
				}else{
					newQos = (int)w.getSelfResponseTime()+newQos;
				}
				//System.out.println(w.getName()+" oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);			
				
				
				
				if(wsSet.contains(w)) continue;
				if(w.getCount()!=0){
					//�Ѳ��ɴ��Ҳ���ȥ
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
					
					//�Ѳ��ɴ�Ĳ����ȥ
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
			double t1;//����1��ʱ��,ʵ�ʽṹ
			double t2;//����2��ʱ��,��̬
			double t3;//���ܵ�ʱ��
			
			
			for(int i=0;i<10;i++){//���10��
				//��RPT1��serviceMap1
				recovery();				
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����				
				strategy(1,copy,realSum,qosChangedList);				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");
												
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t1=totalTime/8;
						
			recordTime1.append(totalTime/8+System.getProperty("line.separator"));
			
			/*****************Stratege2*******************/
			for(int i=0;i<10;i++){//���10��
				//��RPT1��serviceMap1
				recovery();				
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����				
				strategy(2,copy,realSum,qosChangedList);				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");
												
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t2=totalTime/8;
						
			recordTime2.append(totalTime/8+System.getProperty("line.separator"));
			
			//*********************������һ��*****************/
			for(int i=0;i<10;i++){//���10��
				//��RPT1��serviceMap1
				recovery();				
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����				
				reQuery();				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");											
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
			
			
			//long startMili=System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����
			//System.out.println("continuousQuery��ʼ: "+startMili);
						
			//ѡ�����myStratege		
			//strategy(1,copy,realSum,qosChangedList);
			
			//ѡ�����oldStratge
			//strategy(2,copy,realSum,qosChangedList);
			
			//long endMili=System.currentTimeMillis();
			//System.out.println("continuousQuery���� :"+endMili);
			//System.out.println("myStratege�ܺ�ʱΪ:"+(endMili-startMili)+"����");
			
												
		
		//System.out.println("continuous���Ž���:"+continousQos);
		//reQuery();
		System.out.println("qos�ı��˵ķ��������"+copy);
		System.out.println("ʵ����Ҫ���µ�ws(count!=0):�ĸ�����"+realSum);
		
	}
	
	
	public void randomTest(){
		WebService w=null;
		
		List<WebService> qosChangedList = new ArrayList<WebService>();	//Qos�ı��˵�ws����
		Set<WebService> wsSet = new HashSet<WebService>();	//wsSet,����Ч��
		Map<String,Double> newQosMap = new HashMap<String,Double>();
		List<WebService> copyChangedList = new ArrayList<WebService>();  //�޹ؽ�Ҫ�ı���
		
		int sum;
		realSum=0;
		
			sum=sumOfChangedService;
			
			while(sum>0){
				Random random = new Random();
				int r = random.nextInt(testServiceList.size());	//����ѡȡһ��ws�ı�
				
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
				}	//����ǳ�ʼ�ڵ� ���� �� ���սڵ� �� �򲻽��д���
				
				//odd ��������ȷ�� ��WS��QOS�Ǳ�� ���� �仵~~
				boolean odd=false;
				odd = random.nextInt(100)%2 !=0 ? true:false;
				
				//int newQos = (int)(random.nextDouble()*w.getSelfResponseTime()*2);
				
				int newQos = (int)(random.nextDouble()*30);
				
				//if(newQos==0) continue;
				
				//odd Ϊtrue ʱ �� Qos���
				if(odd){
					if(w.getSelfResponseTime()-newQos<=0) continue;
					else newQos = (int)w.getSelfResponseTime()-newQos;
				}
				//����Qos�仵
				else{
					newQos = (int)w.getSelfResponseTime()+newQos;
				}
				
				//System.out.println(w.getName()+" oldQos:"+w.getSelfResponseTime()+" newQos:"+newQos);			
				
				
				if(wsSet.contains(w)) continue;
				
				if(w.getCount()!=0){
					//�Ѳ��ɴ��Ҳ���ȥ
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
					
					//�Ѳ��ɴ�Ĳ����ȥ
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
			double t1;//������ѯ��ʱ��
			double t2;//�ز��ʱ��
			
			
			for(int i=0;i<10;i++){//���10��
				//��RPT1��serviceMap1
				recovery();
				recoveryWithNewQos(copyChangedList);
				startMili=System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����				
				continuousQuery4(qosChangedList);				
				endMili=System.currentTimeMillis();
				timeList.add(new Double(endMili-startMili));
				System.out.println("continuousQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}					
			
			t1=totalTime/8;
			
			//System.out.println("continuousQuery���� :"+endMili);
			System.out.println("continuousQueryTotal�ܺ�ʱΪ:"+totalTime/8+"����");
			
			recordTime1.append(totalTime/8+System.getProperty("line.separator"));
			
			System.out.println("continuous���Ž���:"+continousQos);
			
			totalTime=0;
			timeList.clear();
			
			for(int i=0;i<10;i++){
				startMili=System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����						
				reQuery();
				endMili=System.currentTimeMillis();
				System.out.println("reQuery�ܺ�ʱΪ:"+(endMili-startMili)+"����");
				timeList.add(new Double(endMili-startMili));			
			}
			
			sortDoubleList(timeList);
			for(int i=1;i<=8;i++){
				totalTime=totalTime+timeList.get(i);
			}	
			
			t2=totalTime/8;
			
			//��¼ʱ���С
			if(t1<t2){
				recordCompare.append(1+System.getProperty("line.separator"));
			}
			else 
				if(t1==t2) recordCompare.append(0+System.getProperty("line.separator"));
				else recordCompare.append(-1+System.getProperty("line.separator"));
			
			System.out.println("���Ž��qos:"+requeryQos);
			
			recordTime2.append(totalTime/8+System.getProperty("line.separator"));
			
			System.out.println("reQueryTotal�ܺ�ʱΪ:"+totalTime/8+"����");
			
			if(requeryQos==continousQos){
				System.out.println("ǰ����ȷ");		
			}else{
				System.out.println("ǰ��bubububububu��ȷ");		
			}
			
			//��¼ʵ����Ҫ���µ�ws(count!=0):�ĸ���
			recordRealSum.append(realSum+System.getProperty("line.separator"));
			
			System.out.println("ʵ����Ҫ���µ�ws(count!=0):�ĸ�����"+realSum);
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
	
	//һ�ָֻ�����
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
				//System.out.println("����2031");
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
					System.out.println("����201");
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
					System.out.println(need + " �ҵ���");
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
//			System.out.println(parents2.get(i).getName()+"��parents��");
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
			recordBackWard=recordBackWard+parents2.get(i).getName()+"��parents��"+"\n";
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
	
	public  void getOptimalWebServiceComposition(List<String> inputsFromChallenge, List<String> outputsFromChallenge,
			Map<String,List<WebService>> IIT,Map<String,String> map1,Map<String,List<String>> map2,Map<String,WebService> RPT){
			
			int sumOfRequest = outputsFromChallenge.size();
			
			WebService begin = new WebService();
			WebService end = new WebService();
			
			List<WebService> reachVertices = new ArrayList<WebService>();
			
			System.out.println("�����б���Ϊ : " + this.serviceMap.size());
			System.out.println("�յ���Ҫ������Ϊ : " + outputsFromChallenge.toString());
			System.out.println("Relations �ĳ���Ϊ : " + this.map1.size());
			System.out.println("conToInsts �ĳ���Ϊ : " + this.map2.size());
			
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
			
			System.out.println("IIT �ĳ���Ϊ : " + IIT.size());
			
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
				
				//------------------------------------------------------------------------------------------------
				WebService v = reachVertices.remove(0);
				
				sumOfCanUseWS++;
				
				for(Object output:v.getOutputs()){
					
					///*********���OT*********
					List<String> matchList1 = findInstances(output.toString(),map1,map2);//�����ܱ�output�����inst	
					
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
						
						//---------------��֪��ʲô����˼--------------------
						if(!output.toString().equals(matchList.get(0)))//ʹmatchList���A1-A2-A3-A4
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
						
						//System.out.println("matchList�Ĵ�С��"+matchList.size());
						boolean firstPlace=true;
						
						for(String inst : matchList){
							
							if(!RPT.containsKey(inst)){
								 RPT.put(inst, v);
								 RPT1.put(inst, v);
								 //System.out.println("�µ�inst*:"+inst);
							 }
							else{
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
								 
								 if(w.getName().equals("Request"))
									 System.out.println("��ʣ"+end.getCount()+"��Ԫ��");
								 
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
										 //System.out.println("����"+w.getName()+"��AllRequestTime:"+w.getAllResponseTime());
										 /////////serv1027640201 serv404388610  404388610
										
								 }
							 }
							 
						}
						
					}
				}
			}
			if(!find)
				System.out.println("�Ҳ���");
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
