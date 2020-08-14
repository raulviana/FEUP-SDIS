package utils;

import java.util.ArrayList;
import java.util.Collections;

public class FileCopy {
	
	public String fileName;
	public ArrayList<String> peers = new ArrayList<String>();
	public int desiredReplicationDegree;
	public int achievedReplicationDegree;

	public FileCopy(String fileName, ArrayList<String> peers, int desiredReplicationDegree) {
		this.fileName = fileName;
		
		for(String peer: peers) {
			this.peers.add(peer);
		}
		
		
		this.desiredReplicationDegree = desiredReplicationDegree;
		calculateAchievedReplicationDegree();
	}
	
	public void calculateAchievedReplicationDegree() {
		
		achievedReplicationDegree = peers.size();
	}
	
	public String print() {
		return "Name: " + fileName + "| Peers: " + peers+ "| Desired rep_deg: " + String.valueOf(desiredReplicationDegree) + "| Achieved rep_deg: " + String.valueOf(achievedReplicationDegree);
		
	}
}
