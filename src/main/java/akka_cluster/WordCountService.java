package akka_cluster;


import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberUp;

public class WordCountService extends UntypedActor {
	Cluster cluster = Cluster.get(getContext().system()); 
	@Override 
	public void preStart() {    
		cluster.subscribe(getSelf(), MemberUp.class);    
		} 
	@Override

public void postStop() {  
		cluster.unsubscribe(getSelf());  
		}    
	@Override
	public void onReceive(Object msg) throws Exception {  
		if (msg instanceof Article) {      
			System.out.println("当前节点:"+cluster.selfAddress()+",self="+getSelf()+ "正在处理……");    
			Article art = (Article) msg; 
			String content = art.getContent();    
			int word_count = content.split("").length;  
			getSender().tell(new CountResult(art.getId(), word_count),getSelf()); 
			}else if(msg instanceof MemberUp){  
				MemberUp mu=(MemberUp)msg;     
				Member m=mu.member();    
				if(m.hasRole("wordFrontend")){    
					getContext().actorSelection(m.address() + "/user/wordFrontService").tell ("serviceIsOK", getSelf());  
					}         
				System.out.println(m+" is up"); 
				}else{  
					unhandled(msg); 
					}   
		}
	}
