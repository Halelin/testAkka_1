package akka_cluster_wordCounter;

import java.util.Timer;
import java.util.TimerTask;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
//启动后端服务
public class StartCountService {
	public static void main(String[] args) {
		String port = args[0]; 
		String sysname=args[1];   
		System.out.println(port);
		System.out.println(sysname);
		//加载.conf并动态配置port
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+ port).withFallback(ConfigFactory.load("countServiceCluster.conf")); 
		System.out.println(config);
		//创建ActorSystem
		ActorSystem system = ActorSystem.create(sysname, config);   
		//启动集群节点
		system.actorOf(Props.create(WordCountService.class),"countServiceCluster"+port);
		
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
