package Main;
import java.io.*;
import java.net.*;

import utils.Network;

public class TestApp {
	
	public int port;
	
    public static void main(String [] args) {
		   	//if (args.length < 2) return;
		   	
		      String serverName = args[0];
		      int port = Integer.parseInt(args[1]); //peer to connect
		      String command = args[2];

		      try {
		    	 
		         Socket serverSocket = new Socket(serverName, port);
		         //System.out.println("TestApp connecting to " + serverName + " on port " + port + "\n");		         
		         
		         System.out.println("TestApp Connected to Peer " + serverSocket.getRemoteSocketAddress());
		         DataOutputStream output = new DataOutputStream(serverSocket.getOutputStream());
		         DataInputStream input = new DataInputStream(serverSocket.getInputStream());
		         
		         System.out.println(command);
		         if(command.trim().equals("PRINT")) {
		        	 System.out.println("ylla ya rb"); 
		         }
		         switch (command) {
		         case "PRINT":
						//args[0] -> IP
						//arga[1] -> peer port
						//args[2] -> Protocol
						//args[3] -> Space to be freed
						output.writeUTF(command);
					break;	 
		         case "PRINT1":
						//args[0] -> IP
						//arga[1] -> peer port
						//args[2] -> Protocol
						//args[3] -> Space to be freed
						output.writeUTF(command);
					break;	 
		         case "REGISTER":
		        		 output.writeUTF(command + " " + serverSocket.getLocalSocketAddress());
		        		 break;
		        	 case "BACKUP":
		        		Network.counter = 6;
		   		      	String opt1 = args[3]; //file
		   		      	String opt2 = args[4]; //replication degree
		        		output.writeUTF(command + " " + opt1 + " " + opt2);
						break;
		        	 case "RESTORE":
		        		 String fileName = args[3];
		        		 output.writeUTF("STARTRESTORE"+" "+fileName);
		        		 break;
					 case "DELETE":
						String file = args[3];//file
						output.writeUTF(command + " " + file);
						break;
					 case "RECLAIM":
						//args[0] -> IP
						//arga[1] -> peer port
						//args[2] -> Protocol
						//args[3] -> Space to be freed
						output.writeUTF(command + " " + args[3]);
						break;
					 
		        	default:		        		
		        		System.out.println("Invalid command: " + command);
		         }
		        	 
 
		         //String response = input.readUTF();
		         //System.out.println("From Server: " + response);
		         
		         serverSocket.close();
		            
		      } catch (IOException e) {
		         e.printStackTrace();
		      }
	   }	
	}

