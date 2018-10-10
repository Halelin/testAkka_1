package akka_cluster_wordCounter_route;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.routing.FromConfig;
//启动后端服务
public class StartCountService {
	//创建ActorSystem
	public static ActorSystem   system;
	public static void main(String[] args) {
		String port = args[0]; 
		String sysname=args[1];   
		System.out.println(port);
		System.out.println(sysname);
		//加载.conf并动态配置port
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+ port).withFallback(ConfigFactory.load("countServiceCluster_route.conf")); 
		system = ActorSystem.create(sysname, config);
		System.out.println("system" + system.hashCode());
		//启动集群节点,加入Actor
		ActorRef routeeRef= system.actorOf(Props.create(WordCountService.class),"workerActor");
		System.out.println(routeeRef);
		
		
//		try {
//			Thread .sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}



//class Sender extends UntypedActor{
//	@Override
//	public void onReceive(Object msg) throws Exception {
//		if(msg instanceof CountResult) {
//			CountResult r = (CountResult)msg;
//			System.out.println("收到！"+r.getId()+" 字数 "+r.getCount());
//		}
//		
//	}
//}
//
////消息中转处理器,不会改变sender
//class MasterRouterActor2 extends UntypedActor { 	
//	ActorRef router = null; 
//	//使用group路由前，需要先定义Routee-Actor,在确定了Actor的层级关系或者path之后，需要将它们配置到conf文件中
//	@Override    
//	public void preStart() throws Exception {
//		//定义Routee
//		//getContext().actorOf(Props.create(WordCountService.class), "workerActor");
//		//定义Router
//		router=getContext().actorOf(FromConfig.getInstance().props(),"router");		
//		System.out.println(router);
//	}    
//	@Override   
//	public void onReceive(Object msg) throws Exception {  
//		if(msg instanceof Article) {
//			router.tell((Article)msg, getSender());
//		}
//	}
//}
//
//



