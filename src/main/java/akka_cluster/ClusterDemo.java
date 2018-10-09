package akka_cluster;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;

public class ClusterDemo extends UntypedActor {
//	得到当前集群对象
	Cluster cluster = Cluster.get(getContext().system()); 
	@Override   
//	让当前Actor订阅UnreachableMember、MemberEvent事件，
	public void preStart() {
//		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),UnreachableMember.class,MemberEvent.class);
		cluster.subscribe(getSelf(),UnreachableMember.class,MemberEvent.class);//两种订阅事件方式区别？
		}   
	@Override  
	public void postStop() {
		cluster.unsubscribe(getSelf());
		}   
	@Override 
	public void onReceive(Object message) {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message; 
			Member m=mUp.member();    
			System.out.println(getSelf()+"-->Member "+m+"is Up: the role is "+m.roles());  
		} else if (message instanceof UnreachableMember) {
			UnreachableMember mUnreachable = (UnreachableMember) message; 
			Member m=mUnreachable.member();     
			System.out.println(getSelf()+"-->Member "+m+" detected as unreachable: the role is "+m.roles());   
		  } else if (message instanceof MemberRemoved) {
			  MemberRemoved mRemoved = (MemberRemoved) message;    
			  Member m=mRemoved.member();      
			  System.out.println(getSelf()+"-->Member "+m+" is Removed: the role is"+m.roles()); 
		  } else if (message instanceof MemberEvent) {
			  MemberEvent me=(MemberEvent)message;  
			  Member m=me.member();       
			  System.out.println(getSelf()+"-->MemverEvent: "+me+""+m.roles());  
		  } else {
			  System.out.println(getSelf()+"-->Other: "+message);   
			  unhandled(message);  
		 }   
	} 
}
