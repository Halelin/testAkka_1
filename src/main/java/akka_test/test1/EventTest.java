package akka_test.test1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.japi.LookupEventBus;
public class  EventTest{
	public static void main(String[] args) {
		ActorSystem sys = ActorSystem.create("sys");
		ActorRef eventSubActor =sys.actorOf(Props.create(ActorDemo.class));
		
		System.out.println(eventSubActor.path());
		//新建EventBus   
		EventBusDemo bus= new EventBusDemo(); 
		//订阅info与warn事件
		bus.subscribe( eventSubActor , "info" ); 
		bus.subscribe( eventSubActor , "warn" ); 
		//发布info事件  
		bus.publish( new Event("info" ,"Hello EventBus" )); 
		//取消订阅    
		//bus.unsubscribe( eventSubActor ,"warn" );
		//发布warn事件  
		bus.publish( new Event("warn" ,"Oh No" ));
	}
}

class Event{    
	private String type; 
	private String message;  
	
	public Event( String type, String message) {
		this .type = type ; 
		this .message = message ;  
		}     
	
	public String getType() {  
		return type ;  
		}    
	
	public String getMessage() {   
		return message ; 
		} 
}

class EventBusDemo extends LookupEventBus<Event, ActorRef, String>{  
	@Override    
	public String classify(Event event) {  
		return event .getType();  
		}    
	
	@Override   
	public int compareSubscribers(ActorRef ref1 , ActorRef ref2 ) {		
		return ref1.compareTo( ref2);  
		}  
	
	@Override 
	public void publish(Event event, ActorRef ref) {  
		ref.tell( event.getMessage(),ActorRef. noSender());  
		//ref.tell( event.getMessage(),ActorRef. noSender());
	}   
	/**     * 期望的classify数,一般设置为2的n次幂     */   
	@Override  
	public int mapSize() {  
		return 8;   
	}
}





