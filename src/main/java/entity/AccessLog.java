package entity;

public class AccessLog {
	
	private String ip;					
	private String time; 
	private String method;  
	private String resource;   
	private String state;	
	
	public AccessLog(String ip, String time, String method, String resource, String state) {
		super();
		this.ip = ip;
		this.time = time;
		this.method = method;
		this.resource = resource;
		this.state = state;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}   
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ip:"+ip+" time:"+time+" method:"+method+" resource:"+resource +" state:"+state;
		
	}
}
