package pojo;

import java.util.ArrayList;
import java.util.List;


public class WebService implements Comparable<Object>,Cloneable{
	private String name;
	private double selfResponseTime;
	private double selfThroughput;
	private List inputs;
	private List outputs;
	private int count;
	private double allResponseTime;
	private double newAllResponseTime;
	private WebService criticalParent; 
	
	public WebService getCriticalParent() {
		return criticalParent;
	}
	public void setCriticalParent(WebService criticalParent) {
		this.criticalParent = criticalParent;
	}
	public double getNewAllResponseTime() {
		return newAllResponseTime;
	}
	public void setNewAllResponseTime(double newAllResponseTime) {
		this.newAllResponseTime = newAllResponseTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public double getSelfResponseTime() {
		return selfResponseTime;
	}
	public void setSelfResponseTime(double selfResponseTime) {
		this.selfResponseTime = selfResponseTime;
	}
	public double getSelfThroughput() {
		return selfThroughput;
	}
	public void setSelfThroughput(double selfThroughput) {
		this.selfThroughput = selfThroughput;
	}
	public double getAllResponseTime() {
		return allResponseTime;
	}
	public void setAllResponseTime(double allResponseTime) {
		this.allResponseTime = allResponseTime;
	}
	public List getInputs() {
		return inputs;
	}
	public void setInputs(List inputs) {
		this.inputs = inputs;
	}
	public List getOutputs() {
		return outputs;
	}
	public void setOutputs(List outputs) {
		this.outputs = outputs;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		if(this.getAllResponseTime()>((WebService)arg0).getAllResponseTime()) return 1;
		else 
			if(this.getAllResponseTime()==((WebService)arg0).getAllResponseTime())
				return 0;
			else return -1;
	}
	public void subCount(){
		this.setCount(this.count-1);
	}
	
	@Override
	public String toString() {
		return "WebService [name=" + name + ", selfResponseTime=" + selfResponseTime + ", inputs=" + inputs
				+ ", outputs=" + outputs + ", count=" + count + ", allResponseTime=" + allResponseTime
				+ ", newAllResponseTime=" + newAllResponseTime + "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		WebService copy = new WebService();
		
		copy.setName(this.name);
		copy.setSelfResponseTime(selfResponseTime);
		copy.setAllResponseTime(this.allResponseTime);
		copy.setNewAllResponseTime(this.newAllResponseTime);
		if(inputs != null){
			copy.setInputs(new ArrayList<String>(inputs));
		}
		if(outputs != null){
			copy.setOutputs(new ArrayList<String>(outputs));
		}
		
		copy.setCount(this.getCount());
		
		return copy;
	}
	
	public String getInfo(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.name + "," + this.selfResponseTime + ","  + this.allResponseTime);
		
		if(inputs != null){
			sb.append(",");
			for(int i = 0; i < inputs.size(); i++){
				if(i != (inputs.size() - 1)){
					sb.append(inputs.get(i) + ":");
				}
				else{
					sb.append(inputs.get(i));
				}
			}
		}
		
		if(outputs != null){
			sb.append(",");
			for(int i = 0; i < outputs.size(); i++){
				if(i != (outputs.size() - 1)){
					sb.append(outputs.get(i) + ":");
				}
				else{
					sb.append(outputs.get(i));
				}
			}
		}
		
		return sb.toString();
	}
	
}
