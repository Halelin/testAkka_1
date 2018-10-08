package akka_test.test1;

import akka.actor.UntypedActor;

public class ActorDemo extends UntypedActor{

	@Override
	public void onReceive(Object msg) throws Throwable {
		// TODO Auto-generated method stub
		//getSender().tell("actorDemo: "+msg,getSelf());
		System.out.println("self : "+getSelf()+"actorDemo: "+msg);
	}

}
