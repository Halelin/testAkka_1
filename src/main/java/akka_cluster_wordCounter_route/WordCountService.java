package akka_cluster_wordCounter_route;


import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;

//文字统计后端
public class WordCountService extends UntypedActor {//后端服务，集群Actor
	Cluster cluster = Cluster.get(getContext().system()); 
	@Override 
	public void preStart() {    
		cluster.subscribe(getSelf(), MemberUp.class);    
	} 
	@Override

	public void postStop() {  
		System.out.println("killed :" + getSelf());
		cluster.unsubscribe(getSelf());  
		//查看是否将集群节点移除？未移除？？？？
		CurrentClusterState state=cluster.state(); 
		System.out.println("Leader节点:"+state.getLeader()); 
		System.out.println("所有节点列表:"+state.getMembers()); 
		System.out.println("unreachable节点列表:"+state.getUnreachable());
	}    
	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Article) {  
			System.out.println("当前节点:"+cluster.selfAddress()+",self="+getSelf()+ "正在处理……");    
			Article art = (Article) msg; 
			String content = art.getContent();    
			int word_count = content.split("").length; //拆分文章后統計字母個數 
			getSender().tell(new CountResult(art.getId(), word_count),getSelf()); 
		}else if(msg instanceof MemberUp){
System.out.println("self ::: " +  getSelf());
			MemberUp mu=(MemberUp)msg;     
			Member m=mu.member();    
			System.out.println(m+" is up"); 
		}else{
			unhandled(msg); 
		}   
	}
}
