package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;






import pojo.OptimalParent;
import pojo.WebService;
import ui.MyFrame;


public class Main {
	public static void main(String[] args) throws IOException {
		
		MyFrame window = new MyFrame();
		//window.frame.setVisible(true);
		if(true) return;
		Map<String,String> map1 = new HashMap();//map1:类-父类，实例-类
		Map<String,List<String>> map2 = new HashMap();//map2:类-实例组
		Map<String,WebService> serviceMap = new HashMap<String,WebService>();
		Map<String,WebService> RPT = new HashMap<String,WebService>();//元素和哪个服务提供
		Map<String,List<WebService>> IIT = new HashMap<String,List<WebService>>();//inst***(input)-List
	    List<String> inputsFromChallenge = new ArrayList<String>();
	    List<String> outputsFromChallenge = new ArrayList<String>();
	    
	    constructMap1Map2(map1,map2);
	    constructWebServiceList(serviceMap);
	    getQos(serviceMap);
	    constructIIT(serviceMap,IIT);
	    getChanllenge(inputsFromChallenge,outputsFromChallenge);
	    
	    getOptimalWebServiceComposition(inputsFromChallenge,outputsFromChallenge,
	    		IIT,map1,map2,RPT);
	    backward(outputsFromChallenge,RPT,serviceMap);
	    
	    
	}	
	
	public static void backward(List<String> outputsFromChallenge,Map<String,WebService> RPT,Map<String,WebService> serviceMap){
		List<WebService> parents = new ArrayList<WebService>();
		List<Integer> layerList = new ArrayList<Integer>(); 
		List<WebService> parents2 = new ArrayList<WebService>();
		Set<WebService> parentSet = new HashSet<WebService>();
		for(int i=0;i<outputsFromChallenge.size();i++){
			if(parentSet.add(RPT.get(outputsFromChallenge.get(i)))){
				parents.add(RPT.get(outputsFromChallenge.get(i)));
				parents2.add(RPT.get(outputsFromChallenge.get(i)));
			}
			//System.out.println(RPT.get(outputsFromChallenge.get(i)));
		}
		//System.out.println("layerList.size="+layerList.size());
		
		while(!parents.isEmpty()){
			System.out.println("kkk");
			Collections.sort(parents);
			Collections.sort(parents2);
			WebService  ws = parents.get(parents.size()-1);
			//System.out.println(ws);
			for(int i=0;i<ws.getInputs().size();i++){
				if(parentSet.add(RPT.get(ws.getInputs().get(i)))){
					parents.add(RPT.get(ws.getInputs().get(i)));
					parents2.add(RPT.get(ws.getInputs().get(i)));		
				}
			}
			parents.remove(parents.size()-1);
		}
		System.out.println("**backward**");
		for(int i=0;i<parents2.size();i++){
			System.out.println(parents2.get(i).getName()+" ");
		}
		System.out.println("Request");
		System.out.println("END-backward");
	}
	
	public static void getChanllenge(List<String> inputsFromChallenge,List<String> outputsFromChallenge){
		File xmlFile = new File("Challenge.wsdl");  
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
	
	public static void constructIIT(Map<String,WebService>serviceMap,Map<String,List<WebService>> IIT){
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
	
	public static void getOptimalWebServiceComposition(List<String> inputsFromChallenge, List<String> outputsFromChallenge,
			Map<String,List<WebService>> IIT,Map<String,String> map1,Map<String,List<String>> map2,Map<String,WebService> RPT){
			int sumOfRequest = outputsFromChallenge.size();
			WebService begin = new WebService();
			WebService end = new WebService();
			List<WebService> reachVertices = new ArrayList<WebService>();
			
			
			begin.setOutputs(inputsFromChallenge);
			begin.setName("Provide");
			begin.setInputs(new ArrayList<WebService>());
			begin.setSelfResponseTime(0);
			begin.setAllResponseTime(0);
			end.setName("Request");
			end.setInputs(outputsFromChallenge);
			
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
			
			reachVertices.add(begin);
			end.setCount(sumOfRequest);
			System.out.println(end.getCount());
			while(reachVertices.size()!=0){
				Collections.sort(reachVertices);
				System.out.println("************");
				for(WebService ww:reachVertices){
					System.out.println(ww.getName()+"的返回时间"+ww.getAllResponseTime());
				}
				System.out.println("************");
				
				
				WebService v = reachVertices.remove(0);
				for(Object output:v.getOutputs()){
					if(!RPT.containsKey(output.toString())){//新的inst
						RPT.put(output.toString(),v);
						System.out.println("新的inst:"+output.toString());
						//String con = map1.get(output).toString();
						List<String> matchList = findInstances(output.toString(),map1,map2);//所以能被output解决的inst						
						
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
							if(!firstPlace && RPT.containsKey(inst)) continue;
							firstPlace=false;
							 List<WebService> haveSameInstWebServiceList = IIT.get(inst);
							 if(haveSameInstWebServiceList==null){
								 //System.out.println("没有任何服务需要："+inst);
								 continue;
							 }
							 if(!RPT.containsKey(inst)){
								 RPT.put(inst, v);
								 System.out.println("新的inst*:"+inst);
							 }
							 System.out.println("有"+haveSameInstWebServiceList.size()+"个服务需要"+inst);
							 for(WebService w: haveSameInstWebServiceList){						
								 w.subCount();
								 if(w.getName().equals("Request")) System.out.println("还剩"+end.getCount()+"个元素");
								 if(w.getCount()==0){
									 w.setAllResponseTime(v.getAllResponseTime()+w.getSelfResponseTime());									
									 if(end.getCount()==0){
										 System.out.println("找到了全部要求的元素");
										 System.out.println(end.getAllResponseTime());
										 return;
									 }else{
										 reachVertices.add(w);
										 System.out.println("服务"+w.getName()+"可用了");
										 System.out.println("服务"+w.getName()+"的AllRequestTime:"+w.getAllResponseTime());
									 }
								 }
							 }
							 
						}
						
					}
				}
			}
			System.out.println("找不到");
			
	}
	public static void getQos(Map<String,WebService> serviceMap){
		File xmlFile = new File("Servicelevelagreements.wsla");  
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
	public static void constructWebServiceList(Map<String,WebService> serviceMap){
		File xmlFile = new File("services.xml");  
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
	public static void constructMap1Map2(Map map1,Map map2){
		//map1:类-父类，实例-类
		//map2:类-实例组
		File xmlFile = new File("Taxonomy.owl");  
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
	public static List<String> findInstances(String instance,Map<String,String> map1,Map<String,List<String>> map2){
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
	
}
