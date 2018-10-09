package akka_cluster_wordCounter_route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ArticleUtil {
	public static List<Article> getArticle(String directory) {
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
		return arts;
//		Timeout timeout = new Timeout(Duration.create(3, TimeUnit.SECONDS)); 
//		for (Article art : arts) {
//			Patterns.ask(ref, art, timeout).onSuccess(new OnSuccess<Object>() {   
//				@Override           
//				public void onSuccess(Object res) throws Throwable {//接收后端服务处理成功后的信息
//					 CountResult cr = (CountResult) res;    
//	                 System.out.println("文件"+cr.getId() + ",单词数:" + cr.getCount());    
//		        }         
//			}, system.dispatcher());  
//		}   
	}
	
}

