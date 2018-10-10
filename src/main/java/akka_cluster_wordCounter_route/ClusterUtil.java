package akka_cluster_wordCounter_route;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;

public class ClusterUtil {
 public static void getAllCluster (ActorSystem system) {
		//可以通过Cluster.get（system）.state（）方法得到CurrentClusterState对象,
		//该对象中包含了当前Leader节点地址、所有节点列表、Unreachable节点列表等信息：
		Timer timer =new Timer();
		timer.schedule(new TimerTask() {		
			@Override
			public void run() {
				CurrentClusterState state=Cluster.get(system).state(); 
				System.out.println("Leader节点:"+state.getLeader()); 
				System.out.println("所有节点列表:"+state.getMembers()); 
				System.out.println("unreachable节点列表:"+state.getUnreachable());
			}
		}, 15000);
 }
 
 //立刻获得结果
 public static void getAllClusterInstantly (ActorSystem system) {
		//可以通过Cluster.get（system）.state（）方法得到CurrentClusterState对象,
		//该对象中包含了当前Leader节点地址、所有节点列表、Unreachable节点列表等信息：
				CurrentClusterState state=Cluster.get(system).state(); 
				System.out.println("Leader节点:"+state.getLeader()); 
				System.out.println("所有节点列表:"+state.getMembers()); 
				System.out.println("unreachable节点列表:"+state.getUnreachable());

}
 
}
