package akka_cluster_wordCounter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;

public class WordFrontService extends UntypedActor {//前端服务，普通Actor，需要加入集群中
	private List<ActorRef> wordCountServices = new ArrayList<ActorRef>(); 
//	任务索引
	private int jobCounter = 0;   
	@Override    
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Article) {
			jobCounter++;   
			Article art = (Article) msg;  
			// 通过取余选择后端节点  
			int serviceNodeIndex = jobCounter % wordCountServices.size();     
			System.out.println("选择节点:"+serviceNodeIndex);    
			wordCountServices.get(serviceNodeIndex).forward(art, getContext());
		} else if (msg instanceof String) {
			String cmd = (String) msg;     
			if (cmd.equals("serviceIsOK")) {
				// 后端Service已就绪       
				ActorRef backendSender = getSender();   
				System.out.println(backendSender+"可用");  
				wordCountServices.add(backendSender);	
				getContext().watch(backendSender);
			}else if(cmd.equals("isready")){//判断客户端准备就绪
				if(!wordCountServices.isEmpty()){//后端准备就绪
					getSender().tell("ready",getSelf());  
				}else{
					getSender().tell("notready",getSelf());   
				}   	
			}   
		}else if (msg instanceof Terminated) {
			// 当后端Service终止时，将其从后端列表中移除  
			Terminated ter = (Terminated) msg;    
			System.out.println("移除了"+ter.getActor());   
			wordCountServices.remove(ter.getActor());
			
			//查看是否将集群节点移除？未移除？？？？
			CurrentClusterState state=Cluster.get(getContext().system()).state(); 
			System.out.println("Leader节点:"+state.getLeader()); 
			System.out.println("所有节点列表:"+state.getMembers()); 
			System.out.println("unreachable节点列表:"+state.getUnreachable());
		} else {
			unhandled(msg);    
		}
	}
}
/**  * 文章  */ 
class Article implements Serializable{
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

/**  * 单词统计结果  */
class CountResult implements Serializable{
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