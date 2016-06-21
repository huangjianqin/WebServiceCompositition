package pojo;

public class WS2NextWSNumBean implements Comparable<WS2NextWSNumBean>{
	private String wsName;
	private Integer nextWSNum = 0;
	
	public String getWsName() {
		return wsName;
	}
	public void setWsName(String wsName) {
		this.wsName = wsName;
	}
	public Integer getNextWSNum() {
		return nextWSNum;
	}
	public void setNextWSNum(Integer nextWSNum) {
		this.nextWSNum = nextWSNum;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wsName == null) ? 0 : wsName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WS2NextWSNumBean other = (WS2NextWSNumBean) obj;
		if (wsName == null) {
			if (other.wsName != null)
				return false;
		} else if (!wsName.equals(other.wsName))
			return false;
		return true;
	}
	public int compareTo(WS2NextWSNumBean o) {
		
		if(o != null){
			if(o.getNextWSNum() == null && this.nextWSNum != null){
				return 1;
			}
			if(o.getNextWSNum() != null && this.nextWSNum == null){
				return -1;
			}
			if(o.getNextWSNum() == null && this.nextWSNum == null){
				return 0;
			}
			
			if(this.nextWSNum > o.getNextWSNum()){
				return 1;
			}
			else if(this.nextWSNum == o.getNextWSNum()){
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
		return wsName + "," + nextWSNum;
	}
	
	
	
}
