package akka_cluster_wordCounter_route;

import java.io.Serializable;

/**  * 文章  */ 
public class Article implements Serializable{
	private String id;  
	private String content; 
	public String getId() {
		return id;   
	}   
	public void setId(String id) {
		this.id = id;  
	}   
	public String getContent() {
		return content;  
	}   
	public void setContent(String content) {
		this.content = content; 
	}
} 

