package utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Network {
	

	public static List<String> peers = new ArrayList<String>();

	
	
	public static int counter = 6;

    
    public static synchronized boolean Register(String name) throws UnknownHostException {

    	if(peers.contains(name)==false)
    		peers.add(name);

    	return true;
	}
	
	public static synchronized List<String> getPeers() {
		return peers;
	}
	
	public static synchronized void PrintPeers() {
		System.out.println("Registered Peers: ");
		 for(String peer : peers) {
	            System.out.println(peer);
	        }
	}
}
