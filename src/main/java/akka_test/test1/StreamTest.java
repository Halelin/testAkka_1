package akka_test.test1;

import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

public class StreamTest {
	public static void main(String[] args) {
			ActorSystem system = ActorSystem.create("sys"); 
			Materializer materializer = ActorMaterializer.create(system); 
//			Source<Integer, NotUsed> source = Source.range(1, 5);
//			Sink<Integer,CompletionStage<Done>> sink=Sink.foreach(System.out::println); 
//			RunnableGraph<NotUsed> graph=source.to(sink); 
//			graph.run(materializer);
			
//			List<String> list = new ArrayList<String>(); 
//			list.add("sh");
//			list.add("bj");
//			list.add("nj");
//			Source<String,NotUsed> s1=Source.from(list);
//			s1.runForeach(System.out::println, materializer);
			
			
////			4）使用FileIO API从文件中构建Source，可将文件内容作为流处理的输入。
//			try {
//				Source<ByteString, CompletionStage<IOResult>> source =
//						FileIO.fromPath(Paths.get("demo_in.txt"));
//				//输出是二进制byteString
////				Sink<ByteString,CompletionStage<Done>> sink1=Sink.foreach(System.out::println);
////				RunnableGraph<CompletionStage<IOResult>> graph=source.to(sink1);
////				graph.run(materializer);
//				
//				//输出是二进制byteString
////				source.runForeach(System.out::print, materializer);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			//1）使用Sink循环出每个元素
//			Source<Integer, NotUsed> source = Source.range(1, 5);//			
//			Sink<Integer,CompletionStage<Done>> sink1=Sink.foreach(System.out::println);
//			RunnableGraph<NotUsed> graph=source.to(sink1);
//			graph.run(materializer);
			
//			//利用source读入文件并利用sink写入文件实现文件的复制
//			Source<ByteString, CompletionStage<IOResult>> source = FileIO.fromPath(Paths.get("demo_in.txt"));
//			Sink<ByteString,CompletionStage<IOResult>> sink =FileIO.toPath(Paths.get("target/demo_in_2.txt"));
//			RunnableGraph<CompletionStage<IOResult>> graph = source.to(sink);
//			CompletionStage<IOResult> coms = graph.run(materializer);
//			coms.thenAccept(System.out::print);
			
			//构建Flow
//			Flow<String, Integer, NotUsed> flow = Flow.of(String.class).map(x -> { 
//				return Integer.parseInt(x) * 3; 
//				});
//			Sink<Integer, CompletionStage<Done>> sink = Sink.foreach(System.out::println); 
//			//list是一个包含5个元素的集合，代码略...
//			List<String> list = new ArrayList<String>(); 
//			list.add("1");
//			list.add("3");				
//			Source.from(list).runWith(flow.to(sink), materializer);
//			Source.from(list).via(flow).runWith(sink, materializer);

	
			
			//构建Flow实现文本文件的读取并打印到控制台
			//实现方式一：					
			Source<ByteString, CompletionStage<IOResult>> source =FileIO.fromPath(Paths.get("demo_in.txt"));
			Flow<ByteString, String, NotUsed> flow = Flow.of(ByteString.class).map(x -> { 
				System.out.println(x.utf8String());				
				byte[] b =x.toArray();	
				//return new String(b,"utf8");
				return x.utf8String();
			});
			Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::print); 
			//flow连接方式一
			source.via(flow).runWith(sink, materializer);
			//flow连接方式二
			//source.runWith(flow.to(sink), materializer);
			
			
	}
}
