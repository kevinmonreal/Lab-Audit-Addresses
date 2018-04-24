
public class Computer {
	
	public String name;
	public String ip;
	public String mac;
	public String serviceTag;
	
	
	public Computer(String name) {
		this.name = name;
		this.ip = null;
		this.mac = null;
		this.serviceTag = null;
	}
	
	public void updateIP(String ipAddress) {
		ip = ipAddress;
	} 
	
	public void updateMAC(String macAddress) {
		mac = macAddress;
	}
	
	public void updateTag(String serviceTag) {
		this.serviceTag = serviceTag;
	} 
	
	public String getComputerName () {
		return name;
	}
	
	public String getIP() {
		return ip;
	}
	
	public String getMAC() {
		return mac;
	}
	
	public String getTag() {
		return serviceTag;
	} 
	
}