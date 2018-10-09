package akka_cluster_wordCounter_route;

import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;


public class FrontRouter {
	public static void main(String[] args) {
//		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=8890").withFallback(ConfigFactory.load("countServiceCluster_route.conf")); 
//		ActorSystem sys = ActorSystem.create("sys",config);
		
		
		ActorRef masterActor = StartCountService.system.actorOf(Props.create(MasterRouterActor2.class),"masterActor");
		System.out.println("中转actor: "+ masterActor);
		ActorRef sender = StartCountService.system.actorOf(Props.create(Sender.class));
		//获取Article
		List<Article> arts = ArticleUtil.getArticle("C:\\Users\\111\\Desktop\\testCounter");
		System.out.println(arts.size());
		
		
		//有问题！！！！！！！！！！！！！！！！！！！！！！需要延迟才能获得结果？？？？
//		try {
//			Thread.sleep(15000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		for (Article article : arts) {
			masterActor.tell(article, sender);
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
	//使用group路由前，需要先定义Routee-Actor,在确定了Actor的层级关系或者path之后，需要将它们配置到conf文件中
	@Override    
	public void preStart() throws Exception {
		//定义Routee
		//getContext().actorOf(Props.create(WordCountService.class), "workerActor");
		//定义Router
		router=getContext().actorOf(FromConfig.getInstance().props(),"router");		
		System.out.println(router);
	}    
	@Override   
	public void onReceive(Object msg) throws Exception {  
		if(msg instanceof Article) {
			router.tell((Article)msg, getSender());
		}
	}
}




