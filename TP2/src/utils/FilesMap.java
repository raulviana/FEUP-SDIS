package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class FilesMap {

	public static Map<String, FileCopy> files =  new Hashtable<String, FileCopy>();
	
	
	public static synchronized Boolean exists(String file) {
		return files.containsKey(file);
	}
	
	public static synchronized ArrayList<String> getPeers(String file) { 
		return files.get(file).peers;
	}
	
	public static synchronized int getdesiredReplicationDegree(String file) {
		return files.get(file).desiredReplicationDegree;
	}
	
	public static synchronized int getAchievedReplicationDegree(String file) {
		return files.get(file).achievedReplicationDegree;
	}
	
	
	public static synchronized void setAchievedReplicationDegree(String file,int achievedReplicationDegree) {
		files.get(file).achievedReplicationDegree=achievedReplicationDegree;
	}
	
	public static Boolean addFile(String file, FileCopy fileCopy) {
		files.put(file, fileCopy);
		return true;
	}
	
	public static synchronized void removeFile(String file) {
		files.remove(file);
	}
	
	public static void addPeer(String file, String peer) {
		files.get(file).peers.add(peer);

		files.get(file).calculateAchievedReplicationDegree();
	}
	
	public static synchronized void removePeer(String file,String peer) {
		ArrayList<String> peers=files.get(file).peers;
		peers.remove(peers.indexOf(peer));
	}
	
	
	public static synchronized void printFiles() {
		Set<String> keys = files.keySet();
		for (String key : keys) {
		    System.out.println ("Files: \n" + files.get(key).print());
		}
	}
		

		
}
	

