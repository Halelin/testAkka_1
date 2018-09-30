package akka_test.test1;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Actor_find {
	public static void main(String[] args) {
		ActorSystem sys = ActorSystem.create("sys");
		ActorRef lookup =sys.actorOf(Props.create(LookupActor.class),"lookup");		
		//lookup.tell("find", ActorRef.noSender());
		//ActorSelection select = sys.actorSelection("akka://sys/user/lookup/targetActor");
//		ActorSelection select = sys.actorSelection("akka.tcp://sys@127.0.0.1:2552/user/lookup/targetActor");
		//select.tell("find you", ActorRef.noSender());
	
	}
}

class TargetActor extends UntypedActor { 
	@Override  
	public void onReceive(Object msg) throws Exception {
		System.out.println("on");
		if(msg instanceof Identify) {
			System.out.println("inhere");
			ActorIdentity ai = (ActorIdentity) msg;   
			ActorRef ac = ai.getRef();
			if(ac!=null) {
				System.out.println("target receive: "+msg+""+ac);
			}
		}else {
			System.out.println("target receive: "+msg); 
		}
	} 
}

class LookupActor extends UntypedActor {
	private ActorRef target = null;
	
//	@Override
//	public void preStart() throws Exception {
//		// TODO Auto-generated method stub
//		target = getContext().actorOf(Props.create(TargetActor.class), "targetActor");
//	}
	{      
		target = getContext().actorOf(Props.create(TargetActor.class), "targetActor");
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {  
			if (msg instanceof String) {    
				if ("find".equals(msg)) {  
					ActorSelection as = getContext().actorSelection("targetActor");
					as.tell(new Identify("A001"), getSelf());
					}  
			} else if (msg instanceof ActorIdentity) {   
					ActorIdentity ai = (ActorIdentity) msg;    
					if (ai.correlationId().equals("A001")) {   
						ActorRef ref=ai.getRef(); 
						if(ref!=null) {    
							System.out.println("ActorIdentity: " + ai.correlationId() + ""+ ref);   
							ref.tell("hello target",getSelf());  
						}
					}    
			} else {    
					unhandled(msg);
			}
		}
}