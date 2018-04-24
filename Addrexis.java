import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.String;
import javax.swing.ImageIcon;
import java.io.Writer;
import java.io.PrintWriter;
import java.awt.*;
import java.util.Scanner;

public class Addrexis {
	
	public static ArrayList<Computer> computers;
	
	public static void main(String[] args) throws Exception {
		//Create a computer list to store computer objects
		computers = new ArrayList<Computer>();
		
		Scanner scanner = new Scanner(System.in);
		String filename;
		String secondCommand;
		boolean goodFile = true;
		
		System.out.println("Addrexis 3.1.26: Revised Double Black Diamnond Edition");
		System.out.println("Created by KevinKorp. All rights reserved.");
		System.out.println();
		System.out.println("Lets load the computers!");
		
		do {
			System.out.print("Enter the name of the file (include .extension): ");
			filename = scanner.nextLine();
			String exit = "exit";
			
			try {
				loadComputers(filename);
			} catch (IOException ex) {
				goodFile = false;
				System.out.println("Error: Could not open the file");
				System.out.println("Type 'exit' to exit to program");
				System.out.println("Sucks to suck!");
				System.out.println();
			}
			
			if (filename.equals(exit)) {
				goodFile = true;
				System.out.println("No computers loaded. Goodbye!");
			}
			
			if (filename.equals("print")) {
				printComputers();
				writeToFile();
			}
		} while(goodFile == false);
		
		printComputers();
		
		writeToFile();
		
	}
	
	public static void loadComputers(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String currentLine;
		while((currentLine = br.readLine()) != null){
			computers.add(new Computer(currentLine));
		}
		br.close();
		
		System.out.println("Loading computers...");
		
		for (Computer computer : computers) {
			System.out.println("Loading computer " + computer.name + "...");
			loadIPAddress(computer);
			loadMacAddress(computer);
			loadServiceTag(computer);
		}
		
		System.out.println("Done!");
		System.out.println();
	}
	
	//This method returns the ip address (as a String) from a given computer
	public static void loadIPAddress(Computer computer) throws IOException{
		
		CMD cmdLine = new CMD();
		String ipAddress;
		String cmdIpAddress;
		
		String[] cmdResponse = cmdLine.write("wmic /node:\"" + computer.name + "\" nicconfig get IPAddress", true);
		
		if(cmdResponse.length == 1) {
			computer.updateIP("Computer Not Responding");
			return;
		}
		
		cmdIpAddress = cmdResponse[1];
		
		/* When the command is returned, the line is filled the other information besides the IP.
		 * We really just want the IP address so we must split the string to extract the IP.
		 * Since the IP's differ in length the subSequence method (which splits the string at given indexes)
		 * gets a little crazy because we have to find a specific character (,) and split if at -1 from there.
		*/
		if(cmdIpAddress.contains(",")) {
			ipAddress = cmdIpAddress.subSequence(2, cmdIpAddress.indexOf(',', 3) - 1).toString();
		}
		else if(cmdIpAddress.contains("}")) {
			ipAddress = cmdIpAddress.subSequence(2, cmdIpAddress.indexOf('}', 3) - 1).toString();
		}
		else {
			ipAddress = "Computer in Error State";
		}	
		
		computer.updateIP(ipAddress);
		
	}
	
	//This method returns the MAC address (as a string) from a given computer
	public static void loadMacAddress(Computer computer) throws IOException {
		
		CMD cmdLine = new CMD();
		String macAddress;
		
		String[] cmdResponse = cmdLine.write("wmic /node:\"" + computer.name + "\" nicconfig get MACAddress", true);
		
		if(cmdResponse.length == 1) {
			computer.updateMAC("Computer Not Responding");
			return;
		}
		
		macAddress = cmdResponse[1];
		
		if(macAddress.contains("Code")){
			macAddress = "Computer in Error State";
		}
		else if(macAddress.contains("ERROR:")){
			macAddress = "Computer in Error State";
		}
		
		computer.updateMAC(macAddress);
		
	}
	
	public static void loadServiceTag(Computer computer) throws IOException {
		
		//This is the wmic command that fetches a mac address
		ProcessBuilder tagBuilder = new ProcessBuilder("cmd.exe", "/c", "wmic /node:\"" + computer.name + "\" bios get serialnumber");
		tagBuilder.redirectErrorStream(true);
		
		
		CMD cmdLine = new CMD();
		String serviceTag;
		
		String[] cmdResponse = cmdLine.write("wmic /node:\"" + computer.name + "\" bios get serialnumber", true);
		
		if(cmdResponse.length == 1) {
			computer.updateTag("Computer Not Responding");
			return;
		}
		
		serviceTag = cmdResponse[1];
		
		if(serviceTag.contains("ERROR:")) {
			serviceTag = "Computer in Error State";
		}
		
		computer.updateTag(serviceTag);
		
	}
	
	
	
	public static void printComputers() throws IOException{
		
		for (Computer computer : computers) {
			System.out.println("Computer Name:	" + computer.getComputerName());
			System.out.println("IP Address: 	" + computer.getIP());
			System.out.println("MAC Address:	" + computer.getMAC());
			System.out.println("Service Tag: 	" + computer.getTag());
			System.out.println();
		}
		
	}
	
	public static void writeToFile () {
		try {
			PrintWriter writer = new PrintWriter("Addrexis.csv", "UTF-8");
			writer.println("Computer Name, IP Address, MAC Address, Service Tag, Audited by");
			for (Computer computer: computers) {
				writer.println(computer.getComputerName() + "," + computer.getIP() + "," +
					computer.getMAC() + "," + computer.getTag() + ",Addrexis");
			}
			writer.close();
			System.out.println("Addrexis file printed!");
			
		} catch (IOException ex) {
			System.out.println("Sucks to Suck!");
		}
	}
}