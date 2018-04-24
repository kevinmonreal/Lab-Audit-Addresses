
public class Computer {
	
	public String name;
	public Status status = Status.UNKNOWN;
	
	public Computer(String name){
		this.name = name;
	}
	
	public enum Status{
		UNKNOWN,
		AVAILABLE,
		INUSE,
		UNRESPONSIVE;
	}
}
