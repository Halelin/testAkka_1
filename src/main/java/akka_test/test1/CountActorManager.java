package akka_test.test1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;



import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Foreach;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Future;


public class CountActorManager {
    static Integer num = 0;
	public static void main(String[] args) {	
		System.out.println(num);
		ActorSystem sys =ActorSystem.create("sys");
		String[] total= {"hello","world"};
		Timeout timeout =new Timeout(2, TimeUnit.SECONDS);
		List<Future<Integer>> fuList = new ArrayList<Future<Integer>>();
		for(int i=0;i<total.length;i++) {
			ActorRef ref = sys.actorOf(Props.create(CountActor.class),"counter"+i);
			//ref.tell(total[i], sender);				
			Future<Integer> fu1 =(Future)Patterns.ask(ref, total[i], timeout);
			fuList.add(fu1);
			
			
//			Future<Object> fu1 =Patterns.ask(ref, total[i], timeout);			
//			Future<Integer> fu2 =fu1.map(new Mapper<Object, Integer>() {
//				@Override
//				public Integer apply(Object parameter) {					
//					num+=(Integer)parameter;					
//					return 0;
//				}
//			}, sys.dispatcher());			
			
		}
		Future<Iterable<Integer>> fs = Futures.sequence( fuList, sys.dispatcher());	
		System.out.println(fs+" fs "+fs.getClass());
		Future<Integer> fv = fs.map( new Mapper<Iterable<Integer>, Integer>() {
			@Override
			public Integer apply(Iterable<Integer> p) {
				System.out.println(p+""+p.getClass());
				for (Integer i : p) {
					num+=i;
				}
				System.out.println(num);
				return 8;
				
			}
		},sys.dispatcher());
		System.out.println(fv+"  fv "+ fv.getClass());
		
		
		
	}
}

class CountActor extends UntypedActor { 
	@Override 
	public void onReceive(Object msg ) throws Exception {     
		String message = (String) msg;   
		getSender().tell( message.length(), getSelf());  
		} 
}