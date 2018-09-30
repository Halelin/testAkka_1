package akka_test.test1;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.remote.RemoteScope;

public class Akka_Remote {
	public static void main(String[] args) {
		ActorSystem sys = ActorSystem.create("sys");
		ActorRef rmtActor =sys.actorOf(Props.create(ActorDemo.class),"rmtActor");
		//启动本远程ActorSystem
		ActorSelection selection = sys.actorSelection("akka.tcp://sys@127.0.0.1:2552/user/rmtActor"); 
		selection.tell("hello creater", ActorRef.noSender());
		
		//基于conf配置创建远程Actor
		ActorRef ref=sys.actorOf(Props.create(ActorDemo.class),"rmtCrtActor2");		
		ref.tell("this is rmtCrtActor2",ActorRef.noSender());
		System.out.println(ref.path());
		
		//基于远程部署API withDeploy创建远程Actor
		Address addr = new Address("akka.tcp", "sys", "127.0.0.1", 2553);
		ActorRef ref2=
				sys.actorOf(Props.create(ActorDemo.class). withDeploy(new Deploy(new RemoteScope(addr))),"refActor2");
		ref2.tell("this is ref2", ActorRef.noSender());
		System.out.println(ref2.path());
	}
}


class RmtActor extends UntypedActor{
	@Override
	public void onReceive(Object msg) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println(msg);
	}
}