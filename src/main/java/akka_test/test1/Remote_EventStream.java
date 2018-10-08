package akka_test.test1;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.remote.AssociatedEvent;
import akka.remote.AssociationErrorEvent;
import akka.remote.DisassociatedEvent;
import scala.concurrent.duration.Duration;
//远程事件监听,触发条件？
public class Remote_EventStream {
	public static void main(String[] args) {
		//加载remoteSys.conf配置文件创建远程ActorSystem
		ActorSystem sys =ActorSystem.create("sys",ConfigFactory.load("remoteSys.conf"));
		ActorRef eventActor = sys.actorOf(Props.create(EventActor.class),"eventActor");
		sys.eventStream().subscribe(eventActor, AssociatedEvent.class);
		sys.eventStream().subscribe(eventActor, DisassociatedEvent.class);
		
	}
}

class EventActor extends UntypedActor {
	@Override
	public void onReceive(Object msg) throws Throwable {
		if(msg instanceof AssociationErrorEvent){
			AssociationErrorEvent aee=(AssociationErrorEvent)msg; 
			System.out.println(aee.getLocalAddress()+"-->"+aee.getRemoteAddress()+":"+aee.getCause());
			}else if(msg instanceof AssociatedEvent) {
				AssociatedEvent aee=(AssociatedEvent)msg; 
				System.out.println(aee.getLocalAddress()+"-->"+aee.getRemoteAddress());
			}else if(msg instanceof DisassociatedEvent) {
				DisassociatedEvent aee=(DisassociatedEvent)msg; 
				System.out.println(aee.getLocalAddress()+"-->"+aee.getRemoteAddress());
			}
	}
}