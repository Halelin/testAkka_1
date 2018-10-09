package akka_cluster_wordCounter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
//需要先启动前端服务，再启动后端服务，因为后端会tell前端，向前端注册自己
//可以将不同节点的端口号作为main函数的参数。下面代码可以用来启动前端服务：
public class StartFrontend {
	static String directory;
	static ActorRef ref;
	static ActorSystem system ;
	
	public static void main(String[] args) {
		String port=args[0];  //前端服务端口号
		 directory=args[1]; //文本文件路径
		//从启动参数加载配置文件.conf
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(
		ConfigFactory .parseString("akka.cluster.roles = [wordFrontend]")).withFallback(ConfigFactory.load("countServiceCluster.conf"));
		system = ActorSystem.create("sys", config); //注意前端服务要注册到集群中，sys名称要一致
		
		//启动前端服务
		 ref = system.actorOf(Props.create(WordFrontService.class),"wordFrontService"); 
		 System.out.println(ref);
		 System.out.println(ref.path());
		 
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				 //客户端服务
				userRequese();
			}
		}, 25000);
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				//销毁一个集群节点
				ActorSelection ac = system.actorSelection("akka.tcp://sys@127.0.0.1:2551/user/countServiceCluster2551");
				ac.tell(PoisonPill.getInstance(), ActorRef.noSender());
			}
		}, 30000);
		
	} 
	
	public static void userRequese(){
		//客户端服务
				System.out.println("frontRef :  "+ref);
				String result = "";    
				while (true) {
					Future<Object> fu = Patterns.ask(ref, "isready", 1000);   
					try {     
						result = (String) Await.result(fu, Duration.create(1000, "seconds")); 
					} catch (Exception ex) {
						ex.printStackTrace();           
					}          
					if (result.equals("ready")) {//当后端准备就绪，跳出循环，进行下一步处理
						System.out.println("===========ready==============");     
						break;         
					}       
				} 
				List<Article> arts = new ArrayList<Article>();   
				File dir = new File(directory);    
				File[] files = dir.listFiles(); //读取路径下所有文件信息
				try {
					for (File file : files) {//将路径下所有文本文件包装成Article后加入List
						Article art = new Article(); 
						art.setId(file.getName());         
						StringBuffer contentBf = new StringBuffer();  
						BufferedReader reader = new BufferedReader(new FileReader(file));  
						String line = reader.readLine();   
						while (line != null) {
							contentBf.append(line);       
							line = reader.readLine();
						}                
						reader.close();     
						art.setContent(contentBf.toString());   
						arts.add(art);            
					}       
				} catch (IOException ex) {
					ex.printStackTrace(); 
				}       
				Timeout timeout = new Timeout(Duration.create(3, TimeUnit.SECONDS)); 
				for (Article art : arts) {
					Patterns.ask(ref, art, timeout).onSuccess(new OnSuccess<Object>() {   
						@Override           
						public void onSuccess(Object res) throws Throwable {//接收后端服务处理成功后的信息
							 CountResult cr = (CountResult) res;    
			                 System.out.println("文件"+cr.getId() + ",单词数:" + cr.getCount());    
				        }         
					}, system.dispatcher());  
				}   
	}
	
}


