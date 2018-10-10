package akka_cluster_wordCounter_route;

import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.routing.FromConfig;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;


public class FrontRouter {
	public static void main(String[] args) {
		
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=8877").withFallback(
				ConfigFactory .parseString("akka.cluster.roles = [wordFrontend]")).withFallback(ConfigFactory.load("countServiceCluster_route.conf"));
		
		ActorSystem sys = ActorSystem.create("sys",config);
//		ActorSystem sys = ActorSystem.create("sys");
//		ActorSystem sys = StartCountService.system;
		System.out.println("system" + sys.hashCode());
		
		ActorRef masterActor = sys.actorOf(Props.create(MasterRouterActor2.class),"masterActor");
		ClusterUtil.getAllClusterInstantly(sys);
		
		
		System.out.println("中转actor: "+ masterActor);
		ActorRef sender = sys.actorOf(Props.create(Sender.class));
		//获取Article
		List<Article> arts = ArticleUtil.getArticle("C:\\Users\\111\\Desktop\\testCounter");
		System.out.println(arts.size());
		
		
//		有问题！！！！！！！！！！！！！！！！！！！！！！需要延迟才能获得结果？？？？-->需要等待路由节点MasterRouterActor2加入集群后再tell
//		try {
//			Thread.sleep(25000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		ClusterUtil.getAllClusterInstantly(sys);
		
		
		
		ActorSelection ref =sys.actorSelection("akka.tcp://sys@127.0.0.1:8888/user/workerActor");//给workerActor发信息，确认路由是否已经加入集群
		System.out.println(ref);
		String result="";
			while(true) {
				//System.out.println("请等待……");
				Future<Object> fu = Patterns.ask(ref, "isready", 15000);   
				try {     
					result = (String) Await.result(fu, Duration.create(1000, "seconds")); 
				} catch (Exception ex) {
					ex.printStackTrace();           
				}          
				if (result.equals("ready")) {//当路由已经加入集群，跳出循环，进行下一步处理
					System.out.println("===========ready=============="); 
					for (Article article : arts) {
						masterActor.tell(article, sender);
					}
					break;         
				}       
			}
		}
}

class Sender extends UntypedActor{
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof CountResult) {
			CountResult r = (CountResult)msg;
			System.out.println("收到！"+r.getId()+" 字数 "+r.getCount());
		}
		
	}
}

//消息中转处理器,不会改变sender
class MasterRouterActor2 extends UntypedActor { 	
	 ActorRef router = null; 
	private String result ="";
	//使用group路由前，需要先定义Routee-Actor,在确定了Actor的层级关系或者path之后，需要将它们配置到conf文件中
	@Override    
	public void preStart() throws Exception {
		//定义Routee,在集群中已经定义
		//getContext().actorOf(Props.create(WordCountService.class), "workerActor");
		//定义Router
		router=getContext().actorOf(FromConfig.getInstance().props(),"router");		
		System.out.println("router :: "+router);
	}    
	@Override   
	public void onReceive(Object msg) throws Exception {  
		if(msg instanceof Article) {
			router.tell((Article)msg, getSender());
		}
//		else if(msg instanceof String) {
//			String cmd =((String)msg);
//			if(cmd.equals("serviceIsOK")) {
//				System.out.println("get serviceIsOK");
//				result ="serviceIsOK";
//			}else if(cmd.equals("isReady")){
//				System.out.println("comfirm isready?");
//				getSender().tell(result, getSelf());
//			}
//		}
		else {
			unhandled(msg);
		}
	}
}




