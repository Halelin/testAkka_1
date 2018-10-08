package akka_cluster;

import java.util.Timer;
import java.util.TimerTask;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;

public class TestCluster1 {
	public static void main(String[] args) {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=2555").withFallback(ConfigFactory.load("appcluster.conf"));  
		ActorSystem system = ActorSystem.create("sys", config); 
		//启动当前2555节点
		system.actorOf(Props.create(ClusterDemo.class),"scListener_JoinDemo"); 
		Cluster cluster = Cluster.get(system);  		
		//将当前节点加入2551所在集群
		Address address = new Address("akka.tcp", "sys", "127.0.0.1",2551);  
		cluster.join(address);
		
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
}
