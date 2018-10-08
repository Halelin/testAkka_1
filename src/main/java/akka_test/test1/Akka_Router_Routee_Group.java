package akka_test.test1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;

public class Akka_Router_Routee_Group {
	public static void main(String[] args) {
		ActorSystem sys = ActorSystem.create("sys");
		ActorRef masterActor = sys.actorOf(Props.create(MasterRouterActor2.class),"masterActor");
		System.out.println("中转actor: "+ masterActor);
		ActorRef sender = sys.actorOf(Props.create(Sender.class));
		masterActor.tell("hello a", sender);
		masterActor.tell("hello b", sender);
		masterActor.tell("hello c", sender);
		
		}
}



//消息中转处理器,不会改变sender
class MasterRouterActor2 extends UntypedActor { 	
	ActorRef router = null; 
	ActorRef router2 = null; 
	//使用group路由前，需要先定义Routee-Actor,在确定了Actor的层级关系或者path之后，需要将它们配置到conf文件中
	@Override    
	public void preStart() throws Exception {
		//定义Routee
		getContext().actorOf(Props.create(WorkTask.class), "wt1");
		getContext().actorOf(Props.create(WorkTask.class), "wt2");
		getContext().actorOf(Props.create(WorkTask.class), "wt3");
		router=getContext().actorOf(FromConfig.getInstance().props(),"router");		
	}    
	@Override   
	public void onReceive(Object msg) throws Exception {  
		if(msg instanceof String) {
			//不改变原始发送者，相当于forward方式发送信息
			router.tell(msg, getSender());  
			//由路由中转改变sender为自己，则会发送到路由中转的Actor
			//router.tell(msg, getSelf()); 
			//router2.tell(msg, ActorRef.noSender());
		}else if(msg instanceof Integer) {
			System.out.println(msg + "隐藏了原始发送者");
		}
	}
}

class WorkTask extends UntypedActor {
	@Override
	public void onReceive(Object msg) throws Exception { 
		System.out.println(getSelf() + "->" + msg+"-->"+getContext().parent());
		System.out.println("sender  "+getSender());
		getSender().tell(1, getSelf());//消息会发送到最初的发送者，若由路由中转改变发送者，则会发送到路由中转的Actor
	} 
}