package akka_test.test1;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import scala.concurrent.duration.Duration;


public class Akka_Router_Routee {
	public static  ActorSystem sys =ActorSystem.create("sys");
	public static void main(String[] args) {	
		
		ActorRef router = sys.actorOf(Props.create(RouterTaskActor.class),"router");
		//监控rutee状态
		ActorRef Router_watcher = sys.actorOf(Props.create(Router_watcher.class),"Router_watcher");		
		
		router.tell("Hello A", ActorRef.noSender());
		router.tell("Hello B", ActorRef.noSender());
		router.tell("Hello C", ActorRef.noSender());
		
		//3秒后重新发送信息到router，由于已经删除一个routee，所以会报错
		sys.scheduler().scheduleOnce(Duration.create(3, "s"), router, "Hello D",sys.dispatcher(), null);
	}
}

class RouteeActor extends UntypedActor{    
	@Override    
	public void onReceive(Object msg) throws Exception {
		System.out.println(getSelf()+"-->"+msg);
	}
}

class RouterTaskActor extends UntypedActor{  
	private  Router router;  
	public static List<Routee> listRoutee=new ArrayList<Routee>(); 
	@Override   
	public void preStart() throws Exception {  
		//List<Routee> listRoutee=new ArrayList<Routee>(); 
		for(int i=0;i<3;i++) {   
			ActorRef ref=getContext().actorOf(Props.create(RouteeActor.class),"routeeActor"+i);
			//System.out.println("routeeActor"+i+"ref ::: "+ ref);
			listRoutee.add(new ActorRefRoutee(ref)); 
		}   
		router=new Router(new RoundRobinRoutingLogic(),listRoutee);  
	} 
	@Override    
	public void onReceive(Object msg) throws Exception { 
			router.route(msg,getSender());
			
	} 
}

//watch每个Routee-Actor的生命周期
class Router_watcher extends UntypedActor {
	@Override
	public void preStart() throws Exception {
		ActorSelection routeeRef0 =getContext().actorSelection("akka://sys/user/router/routeeActor0");
		ActorSelection routeeRef1 =getContext().actorSelection("akka://sys/user/router/routeeActor1");
		ActorSelection routeeRef2 =getContext().actorSelection("akka://sys/user/router/routeeActor2");
		routeeRef0.tell(new Identify("000"), getSelf());
		routeeRef1.tell(new Identify("001"), getSelf());
		routeeRef2.tell(new Identify("002"), getSelf());
		
	}
	
	@Override
	public void onReceive(Object msg) throws Throwable {
		// TODO Auto-generated method stub
		if(msg instanceof String) {
			if(((String)msg).equals("findAll")) {
				ActorSelection routeeRef0 =getContext().actorSelection("akka://sys/user/router/routeeActor0");
				ActorSelection routeeRef1 =getContext().actorSelection("akka://sys/user/router/routeeActor1");
				ActorSelection routeeRef2 =getContext().actorSelection("akka://sys/user/router/routeeActor2");
				routeeRef0.tell(new Identify("000"), getSelf());
				routeeRef1.tell(new Identify("001"), getSelf());
				routeeRef2.tell(new Identify("002"), getSelf());
			}
		}
		if(msg instanceof Terminated) {
			Terminated t = (Terminated)msg;	
			String routeeString = t.getActor().toString();
			System.out.println(routeeString+"停止了");

		}
		if(msg instanceof ActorIdentity) {//watch各个routee			
			ActorIdentity ac = (ActorIdentity) msg;
//			if(ac.correlationId().equals("000")) {
//				System.out.println("000 "+ ac.getRef());				
//			}else if(ac.correlationId().equals("001")){
//				System.out.println("001 "+ ac.getRef());
//			}else if(ac.correlationId().equals("002")){
//				System.out.println("002 "+ ac.getRef());
//			}
			//找到routee的ActorRef
			ActorRef ref = ac.getRef();
			if(ref!=null) {
				getContext().watch(ref);
				System.out.println("监听"+ref+"中");
				if(ac.correlationId().equals("000")) {
					System.out.println(getSelf());
					Akka_Router_Routee.sys.scheduler().scheduleOnce(Duration.create(1, "s"), ref, PoisonPill.getInstance(),Akka_Router_Routee.sys.dispatcher(), getSelf());
					
				}
			}
		}
	}	
}





//class ActorRefFinder extends UntypedActor{
//	@Override
//	public void onReceive(Object msg) throws Throwable {
//		System.out.println("in");
//		if(msg instanceof  String) {	
//			System.out.println("here");
//			ActorSelection se =getContext().actorSelection((String)msg);	
//			se.tell(new Identify("xxx"), getSelf());			
//		}
//		// TODO Auto-generated method stub
//		if(msg instanceof ActorIdentity) {
//			ActorIdentity ac = (ActorIdentity) msg;
//			ActorRef ref = ac.getRef();
//			if(ref!=null) {
//				System.out.println(getSender()+"    sender"+" ref  "+ref);
//				getSender().tell(ref, getSelf());
//			}
//		}
//		if(msg instanceof ActorRef) {
//			System.out.println("瞎几把发"+msg);
//		}
//		
//	}
//}