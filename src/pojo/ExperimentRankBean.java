package pojo;

import java.util.ArrayList;
import java.util.List;

public class ExperimentRankBean implements Comparable<ExperimentRankBean>{
	private List<WS2NextWSNumBean> ws2NextWSNumBeans;
	private Integer rank20Num = 0;
	private Integer PQ = 0;
	
	public ExperimentRankBean() {
		ws2NextWSNumBeans = new ArrayList<WS2NextWSNumBean>();
	}
	
	public List<WS2NextWSNumBean> getWs2NextWSNumBeans() {
		return ws2NextWSNumBeans;
	}
	public void setWs2NextWSNumBeans(List<WS2NextWSNumBean> ws2NextWSNumBeans) {
		this.ws2NextWSNumBeans = ws2NextWSNumBeans;
	}
	
	
	public Integer getRank20Num() {
		return rank20Num;
	}

	public void setRank20Num(Integer rank20Num) {
		this.rank20Num = rank20Num;
	}

	public Integer getPQ() {
		return PQ;
	}
	public void setPQ(Integer pQ) {
		PQ = pQ;
	}
	public int compareTo(ExperimentRankBean o) {
		if(o != null){
			if(this.PQ > o.getPQ()){
				return 1;
			}
			else if(this.PQ == o.getPQ()){
				return 0;
			}
			else {
				return -1;
			}
		}
		
		return -1;
	}

	@Override
	public String toString() {
		return "ws2NextWSNumBeans=" + ws2NextWSNumBeans + ", rank20Num=" + rank20Num + ", PQ=" + PQ;
	}
	
	
	
}
