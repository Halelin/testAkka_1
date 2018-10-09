package akka_cluster_wordCounter_route;

import java.io.Serializable;

/**  * 单词统计结果  */
public class CountResult implements Serializable{
	private String id;   
	private int count;   
	public CountResult() { }  
	public CountResult(String id, int count) {  
		this.id = id;   
		this.count = count;  
	}
	public String getId() { 
		return id; 
	}  
	public void setId(String id) { 
		this.id = id;    
	}    
	public int getCount() {    
		return count;    
	}    
	public void setCount(int count) { 
		this.count = count; 
	}
}