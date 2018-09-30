package akka_test.test1;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.util.ByteString;

class TcpServerDemo extends UntypedActor { 
	@Override
	public void preStart() throws Exception { 
		super.preStart();
		ActorRef tcpManager = Tcp.get(getContext().system()).manager();
		tcpManager.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("127.0.0.1", 1234), 100), getSelf()); 
	}    
	@Override   
	public void onReceive(Object msg) throws Exception {
			if(msg instanceof Bound){    
				Bound bound=(Bound)msg;     
				System.out.println("bound:"+bound);   
				/*..其他处理略..*/ 
			}else if(msg instanceof Connected){  
				Connected conn=(Connected)msg;   
				System.out.println("conn:"+conn);    
				ActorRef handler =  getContext().actorOf(Props.create(ServerHandler.class));  
				getSender().tell(TcpMessage.register(handler),getSelf());
			}
	}
}

class ServerHandler extends UntypedActor {
	@Override  
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Received) { 
			Received re = (Received) msg; 
			ByteString b = re.data();  
			String content = b.utf8String();
			System.out.println("server:"+content);  
			ActorRef conn=getSender();  
			conn.tell(TcpMessage.write(ByteString.fromString("Thanks")),getSelf());
		} else if (msg instanceof ConnectionClosed) {    
					System.out.println("Connection is closed "+msg); 
					getContext().stop(getSelf());  
		}else {  
					System.out.println("Other Tcp Server: " + msg); 
		}
	}
}