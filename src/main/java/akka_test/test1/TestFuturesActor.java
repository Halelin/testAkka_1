package akka_test.test1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class TestFuturesActor {   
	public static void main(String[] args) {  
		ActorSystem system = ActorSystem. create( "sys"); 
		ActorRef ref = system .actorOf(Props.create(FutureActor. class ),     "fuActor" );
		Timeout timeout = new Timeout(Duration.create(3, "seconds" )); 
		Future<Object> future = Patterns. ask( ref, "hello future  ", timeout);  
		
//		try {   //同步方法
//			String replymsg = (String) Await.result( future, timeout.duration());
//		System. out .println(replymsg );  
//		} catch (Exception e ) {     
//			e.printStackTrace();     }    
		Future<String> f2 =future.map(new Mapper<Object, String>() {
			 @Override     
			 public String apply(Object parameter ) {  
				 System.out.println(parameter);
				 return ((String)parameter ).toUpperCase();  
				 } 	
		}, system.dispatcher());
		
		f2.onSuccess(new OnSuccess() {  
			@Override    
			public void onSuccess(Object msg) throws Throwable {  
				System. out .println( "receive: "+ msg); 
				}
			}, system .dispatcher()); 

		f2 .onFailure( new OnFailure() { 
			@Override   
			public void onFailure(Throwable ex ) throws Throwable { 
				if (ex instanceof AskTimeoutException) {      
					System. out .println( "超时异常");  
					} else {        
						System. out .println( "其他异常 "+ ex);    
						}   
				} 
			}, system .dispatcher());

		
		
		
//		future.onSuccess(new OnSuccess<Object>() {  
//					@Override    
//					public void onSuccess(Object msg) throws Throwable {  
//						System. out .println( "receive: "+ msg); 
//						}
//					}, system .dispatcher()); 
//		
//		future .onFailure( new OnFailure() { 
//			@Override   
//			public void onFailure(Throwable ex ) throws Throwable { 
//				if (ex instanceof AskTimeoutException) {      
//					System. out .println( "超时异常");  
//					} else {        
//						System. out .println( "其他异常 "+ ex);    
//						}   
//				} 
//			}, system .dispatcher());
		
		
		
	}	
} 




class FutureActor extends UntypedActor { 
	@Override   
	public void onReceive(Object msg ) throws Exception {
		//Thread.sleep(4000);   
		getSender().tell( getSelf()+"reply  "+msg+getContext() , getSelf());   
		} 
	
}
