package Main;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import utils.FileUtil;
import utils.Network;

public class Peer {
	
	/*
	 System.setProperty("javax.net.ssl.trustStore", "truststore");
			System.setProperty("javax.net.ssl.keyStore", "client.keys");
			System.setProperty("javax.net.ssl.keyStorePassword", "123456");
			
			SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault(); 
			
			int portToConnect = 8080;
			
			try
			{
				SSLSocket socket = (SSLSocket) sf.createSocket(InetAddress.getByName("localhost"), portToConnect);
			}
			catch (IOException e)
			{				
					
				}
	 */
	
	public int port;
	
	public static long maxAllowedStorageInBytes=Integer.MAX_VALUE;
	private static long currentStorageInBytes;
	public static ArrayList<String> backedUpFiles=new ArrayList<String>();

	
		public static void setCurrentStorageInBytes(long n) {
			currentStorageInBytes=n;
		}
		public static long getCurrentStorageInBytes() {
			return currentStorageInBytes;
		}
	   public static void main(String [] args) throws UnknownHostException, IOException {
		   
		   
		   
		   	
		      String serverName = args[0];
		      int  serverPort = Integer.parseInt(args[1]);
		      int port = Integer.parseInt(args[2]);
		   
		      
		      setCurrentStorageInBytes(FileUtil.calculateFolderSize(port+"/"+"backedup files"));
		      
		      		     	      
		      //Peer as server
		      try (ServerSocket myServerSocket = new ServerSocket(port)) { //socket to listen (TestApp) 
		            System.out.println("Peer " + myServerSocket.getLocalSocketAddress() + " is listening on port " + port + "\n");
		 
		            Socket serverSocket = new Socket(serverName, port);
	                new DataOutputStream(serverSocket.getOutputStream()).writeUTF("REGISTER"+" "+port);
	                serverSocket.close();

		            
		            while (true) {
		                Socket mySocket = myServerSocket.accept(); //when testapp connects
		                new PeerThread(mySocket, port, serverName, serverPort).start();
		                }
		      } catch (IOException e) {
			         e.printStackTrace();
			      }
	             	  
	}
}
