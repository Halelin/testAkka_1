package akka_cluster_wordCounter_route;


import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;

//文字统计后端
public class WordCountService extends UntypedActor {//后端服务，集群Actor
	Cluster cluster = Cluster.get(getContext().system()); 
	
	int ok =0;
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
			ClusterUtil.getAllClusterInstantly(getContext().system());
			
			if(m.hasRole("wordFrontend")){  //过滤出前端服务的集群节点
				ok=1;
				System.out.println("ok  "+ok);
//				System.out.println("inner address  "+ m.address());
//				System.out.println(m.address()+"  m.address ");
//				//tell前端服务Actor ,此次启动的集群节点已经可用
//				System.out.println("tell ok ： "+getContext().actorSelection(m.address() + "/user/masterActor"));
//				getContext().actorSelection(m.address() + "/user/masterActor").tell ("serviceIsOK", getSelf());
			}
			
		}else if(msg instanceof String) {
			String cmd =(String )msg;
			if(cmd.equals("isready")){//
				System.out.println("确认中……ok  "+ok);
				if(ok==1){//
					getSender().tell("ready",getSelf());  
					ok=0;
				}else{
					getSender().tell("notready",getSelf());   
				}   	
			}
		}
		else{
			unhandled(msg); 
		}   
	}
}
