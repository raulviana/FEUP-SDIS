package Main;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import utils.FileCopy;
import utils.FilesMap;
import utils.Network;

public class Server {
	
	/*
	
	int SERVER_PORT = 8080;

	System.setProperty("javax.net.ssl.trustStore", "truststore");
	System.setProperty("javax.net.ssl.keyStore", "server.keys");
	System.setProperty("javax.net.ssl.keyStorePassword", "123456");
	
	SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault(); 
	
	SSLServerSocket serverSocket = null;
	try
	{
		serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
	}
	catch (IOException e)
	{
	}
	
	System.out.println("SSLServerSocket is listening on port: " + port);	
	
	serverSocket.setNeedClientAuth(true); // Client auth required 
	
	while(true) {
		try
		{
			SSLSocket socket = (SSLSocket) serverSocket.accept();
			System.out.println(socket.getLocalAddress());
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	 */
	
	public static void main(String[] args) {
	   	  int port =  Integer.parseInt(args[0]);
	   	  

	        try (ServerSocket serverSocket = new ServerSocket(port)) { 
	            System.out.println("Server is listening on port " + port + "\n");
	 
	            while (true) {
	                Socket socket = serverSocket.accept();
	                System.out.println("New client connected in " + socket.getRemoteSocketAddress().toString());               
	                
	                
	                new ServerThread(socket).start();
			        //Network.PrintPeers();
	            }
	            
	        } catch (IOException ex) {
	            System.out.println("Server exception: " + ex.getMessage());
	            ex.printStackTrace();
	        }
	}

}
