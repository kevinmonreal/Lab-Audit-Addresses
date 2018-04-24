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

public class CmdTest {
	
	public static ArrayList<Computer> computers;
	
	public static void main(String[] args) throws Exception {
		//Create a computer list to store computer objects
		computers = new ArrayList<Computer>();
		
		Scanner scanner = new Scanner(System.in);
		String filename;
		String secondCommand;
		boolean goodFile = true;
		
		System.out.println("Addrexis 3.1.26:Revised Double Balck Diamnond Edition");
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
				System.out.println();
			}
			
			if (filename.equals(exit)) {
				goodFile = true;
				System.out.println("No computers loaded. Goodbye!");
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
		}
	}
	
	//This method returns the ip address (as a String) from a given computer
	public static void loadIPAddress(Computer computer) throws IOException{
		
		//This is the wmic command that fetches a ip address
		ProcessBuilder ipBuilder = new ProcessBuilder("cmd.exe", "/c", "wmic /node:\"" + computer.name + "\" nicconfig get IPAddress");
		ipBuilder.redirectErrorStream(true);
		Process ip = ipBuilder.start();
		BufferedReader ipr = new BufferedReader(new InputStreamReader(ip.getInputStream()));
		
		String bufferIPline;
		String ipAddress;
		
		//Skip empty header lines from the command we want the 5Th line
		ipr.readLine();
		ipr.readLine();
		ipr.readLine();
		ipr.readLine();
		bufferIPline = ipr.readLine();
		
		boolean comma = bufferIPline.contains(",");
		if (comma == false) {
			return;
		}
		
		/* When the command is returned, the line is filled the other information besides the IP.
		 * We really just want the IP address so we must split the string to extract the IP.
		 * Since the IP's differ in length the subSequence method (which splits the string at given indexes)
		 * gets a little crazy because we have to find a specific character (,) and split if at -1 from there.
		*/
		ipAddress = bufferIPline.subSequence(2, bufferIPline.indexOf(',', 3) - 1).toString();
		
		computer.updateIP(ipAddress);
		
	}
	
	//This method returns the MAC address (as a string) from a given computer
	public static void loadMacAddress(Computer computer) throws IOException {
		
		//This is the wmic command that fetches a mac address
		ProcessBuilder macBuilder = new ProcessBuilder("cmd.exe", "/c", "wmic /node:\"" + computer.name + "\" nicconfig get MACAddress");
		macBuilder.redirectErrorStream(true);
		Process mac = macBuilder.start();
		BufferedReader macr = new BufferedReader(new InputStreamReader(mac.getInputStream()));
		
		String bufferMACline;
		String macAddress;
		
		//Skip empty header lines from the command we want the 5Th line
		macr.readLine();
		macr.readLine();
		macr.readLine();
		macr.readLine();
		macAddress = macr.readLine();
		
		boolean comma = macAddress.contains("=");
		if (comma == true) {
			return;
		}
		
		computer.updateMAC(macAddress);
		
	}
	
	public static void printComputers() throws IOException{
		
		for (Computer computer : computers) {
			System.out.println(computer.getComputerName());
			System.out.println(computer.getIP());
			System.out.println(computer.getMAC());
			System.out.println();
		}
		
	}
	
	public static void writeToFile () {
		try {
			PrintWriter writer = new PrintWriter("Addrexis.csv", "UTF-8");
			for (Computer computer: computers) {
				writer.println(computer.getComputerName() + "," + computer.getIP() + "," +
					computer.getMAC());
			}
			writer.close();
		} catch (IOException ex) {
			System.out.println("Sucks to Suck!");
		}
	}
}