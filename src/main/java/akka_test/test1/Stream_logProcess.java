package akka_test.test1;

import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.Supervision;
import akka.stream.Supervision.Directive;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import entity.AccessLog;
import scala.Function1;


public class Stream_logProcess {
	public static void main(String[] args) {
		//获得日志数据源
		Source<ByteString, CompletionStage<IOResult>> source =
				FileIO.fromPath(Paths.get("access_log.txt")); 
		//写入目标日志文件
		Sink<ByteString, CompletionStage<IOResult>> sink =
						FileIO.toPath(Paths.get("demo_out.txt")); 
		
		//用于测试的sink,直接输出String
		Sink<String, CompletionStage<Done>> sink2 =Sink.foreach(System.out::println);
		
		//创建自定义错误监管检测
		Function<Throwable, Supervision.Directive> decider = err-> {  
			if (err instanceof ArrayIndexOutOfBoundsException){ 
				return Supervision.resume();//：当流程序抛出ArrayIndexOutOfBoundsException时，让它丢弃错误数据并继续运行，
			}else{     
				return Supervision.stop(); //若出现其他错则停止
			}
		};
		
			
		
		//获得materializer用来分配actor
		ActorSystem system = ActorSystem.create("sys");
		//使用默认的错误监管策略
		//Materializer materializer = ActorMaterializer.create(system);
		//添加自定义的错误监管策略
		Materializer materializer = ActorMaterializer.create( ActorMaterializerSettings.create(system).withSupervisionStrategy(decider),system);
		
		//将source由非结构化ByteString处理结构化ByteString，再转换为为String,避免数据读取不完整
		Flow<ByteString, String, NotUsed> flowToString =
				Framing. delimiter(ByteString.fromString("\r\n"), 100).map(x ->x.utf8String());
		
		//若直接使用非结构化的字节流会出现错误
//		Flow<ByteString,String,NotUsed> flowToString2 = Flow.of(ByteString.class).map(x->x.utf8String());
		
		//将String处理为实体类//会按照delimiter拆分的帧来循环处理？
		Flow<String, AccessLog, NotUsed> flowToAccess = 
				Flow.of(String.class).map(x -> {  
					//System.out.println(x);
					String[] datas = x.split(" "); 
//					System.out.println(Arrays.toString(datas));
					String ip = datas[0];					
					String time = datas[1]; 
					String method = datas[2];  
					String resource = datas[3];   
					String state = datas[4];   
					AccessLog access = new AccessLog(ip, time, method,resource, state); 
//					System.out.println(access.toString());
					return access;
				});
		
		//将实体类处理为ByteString便于写入文件
		Flow<AccessLog, ByteString, NotUsed> flowToByte =
				Flow. of(AccessLog.class).map(x -> {
//					System.out.println(x.toString());
					return ByteString.fromString(x.toString() + "\r\n");
				});//在每条记录末尾加上回车
		
		//日志过滤Flow
		Flow<AccessLog,AccessLog,NotUsed> filter = Flow.of(AccessLog.class).filter(x->x.getState().equals("405"));
		
		//只实现复制日志
//		RunnableGraph<CompletionStage<IOResult>> graph2 = source.via(flowToString).via(flowToAccess).via(flowToByte).to(sink);		
//		graph2.run(materializer);
		
		//实现对日志的过滤
		RunnableGraph<CompletionStage<IOResult>> graph2 = source.via(flowToString).via(flowToAccess).via(filter).via(flowToByte).to(sink);
		CompletionStage<IOResult> c =  graph2.run(materializer);
		System.out.println(c);
	}
}
