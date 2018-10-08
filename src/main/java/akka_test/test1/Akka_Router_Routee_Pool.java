package akka_test.test1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;

public class Akka_Router_Routee_Pool {
	public static void main(String[] args) {
		ActorSystem sys = ActorSystem.create("sys");
		ActorRef masterRouterActor = sys.actorOf(Props.create(MasterRouterActor.class),"masterRouterActor");
		ActorRef sender = sys.actorOf(Props.create(Sender.class));
		masterRouterActor.tell("hello a", sender);
		masterRouterActor.tell("hello b", sender);
		masterRouterActor.tell("hello c", sender);
	}
}


class Sender extends UntypedActor{
	@Override
	public void onReceive(Object arg0) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println(getSelf()+"  get you "+arg0+"  sender "+getSender());
	}
}

//消息中转处理器,不会改变sender
class MasterRouterActor extends UntypedActor { 	
	ActorRef router = null; 
	ActorRef router2 = null; 
	@Override    
	public void preStart() throws Exception { 
		//编码方式定义了pool类型的路由和Routee池的大小，并指定了Routee-Actor为TaskActor.class，这些Actor会自动成为router的子级
		router = getContext().actorOf(new RoundRobinPool(3).props(Props.create(TaskActor.class)), "taskActor");   
		System.out.println("router:"+router);  
		
		//由配置文件加载pool类型的路由，/masterRouterActor/taskActor2
		router2 = getContext().actorOf(FromConfig.getInstance().props(Props.create(TaskActor.class)),"taskActor2"); 
		System.out.println("router2:"+router2);
	}    
	@Override   
	public void onReceive(Object msg) throws Exception {  
		if(msg instanceof String) {
			//不改变原始发送者，相当于forward方式发送信息
			//router.tell(msg, getSender());  
			//由路由中转改变sender为自己，则会发送到路由中转的Actor
		//	router.tell(msg, getSelf()); 
			router2.tell(msg, getSender());
		}else if(msg instanceof Integer) {
			System.out.println(msg + "隐藏了原始发送者");
		}
	}
}

class TaskActor extends UntypedActor {
	@Override
	public void onReceive(Object msg) throws Exception { 
		System.out.println(getSelf() + "->" + msg+"-->"+getContext().parent());
		System.out.println(getSender());
		getSender().tell(1, getSelf());//消息会发送到最初的发送者，若由路由中转改变发送者，则会发送到路由中转的Actor
	} 
}