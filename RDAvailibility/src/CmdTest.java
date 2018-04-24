import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class CmdTest {
	
	private static TrayIcon icon;
	private static SystemTray tray;
	public static ArrayList<Computer> computers;
	
	public static void main(String[] args) throws Exception {

		
		
		computers = new ArrayList<Computer>();
		loadComputers("ComputerList.txt");

		//new ComputerFrame();
		
		icon = new TrayIcon(new ImageIcon("bulb.gif").getImage(), "Test");
		tray = SystemTray.getSystemTray();
		tray.add(icon);
		
		while (true) {
			if ((System.currentTimeMillis() % 5000) == 0) {
				for (Computer computer : computers) {
					checkStatus(computer);
				}
			}
		}
	}
	
	public static void loadComputers(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String currentLine;
		while((currentLine = br.readLine()) != null){
			computers.add(new Computer(currentLine));
		}
		br.close();
	}
	
	public static void checkStatus(Computer computer) throws IOException{
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "wmic /node:\"" + computer.name + "\" computersystem get username");
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		
		//Ditch the response header
		r.readLine();
		r.readLine();
		//Get the user account
		line = r.readLine();
		if(line.contains("ERROR:")){
			if(computer.status != Computer.Status.UNRESPONSIVE){
				System.out.println(computer.name+" is not responding.");
				icon.displayMessage("Computer Unresponsive", computer.name+" is not responding.", TrayIcon.MessageType.ERROR);
				computer.status = Computer.Status.UNRESPONSIVE;
			}
		}else if(!line.contains("\\")){
			if(computer.status != Computer.Status.AVAILABLE){
				System.out.println(computer.name+" is now available.");
				icon.displayMessage("Computer Now Available", computer.name+" is now available.", TrayIcon.MessageType.INFO);
				computer.status = Computer.Status.AVAILABLE;
			}
		}else{
			if(computer.status != Computer.Status.INUSE){
				System.out.println(computer.name+" is in use by "+line);
				icon.displayMessage("Computer In Use", computer.name+" is in use by "+line, TrayIcon.MessageType.WARNING);
				computer.status = Computer.Status.INUSE;
			}
		}
		
	}
}