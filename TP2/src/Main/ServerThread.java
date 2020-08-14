package Main;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.io.*;

import utils.FileCopy;
import utils.FilesMap;
import utils.Network;

public class ServerThread extends Thread {
	
   private Socket socket;
   
   public ServerThread(Socket socket) {
       this.socket = socket;
   }

   public void run() {
      
         try {       
        	 
   	   	  //System.out.println("count " + Network.counter);

        	 
	    	DataInputStream input = new DataInputStream(socket.getInputStream());
	    	DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        	        	
			String sentence = input.readUTF();
	    	String[] words = sentence.split("\\s+");
            //System.out.println(" ::: " + sentence);
    		String answ;
    		List<String> networkPeersList;
            switch (words[0]) {
            	case "REGISTER":
            		//register peer
            		System.out.println("Register command");
            		if (Network.Register(words[1])) {
                    	System.out.println("Registered peer " + socket.getRemoteSocketAddress().toString() + " : " + words[1]);
                    }
                    else {
                    	System.out.println("Already registered");
                    }
            		
            		answ = "Register";
            		output.writeUTF(answ);
            		break;
            	case "BACKUP":
            		//answer available peers for backup
            		System.out.println("Backup protocol");
            		
            		//ArrayList<String> peers;
            		String peers = "";
            		networkPeersList=Network.getPeers();
            		for (String peer : networkPeersList) {
            			//System.out.println(peer);
                	    if (!(peer.equals(words[1]))) {
                	    	peers = peers.concat(peer + " ");
                	    }
               	    }
            		//System.out.println(peers);
            		output.writeUTF(peers);
            		
            	    //output.writeUTF(Network.peers.get(0));
            	    
            	    
            	case "RESTORE":
            		if(FilesMap.exists(words[2])) {
        				
	            		ArrayList<String> allPeers=	FilesMap.getPeers(words[2]);
	            		
	            		String responseToClient="";
	            		for(String peerPort:allPeers) {
	            			String peerIP="localhost";	            			
	            			responseToClient+= peerIP+","+peerPort+" ";
	            		}	        		
	    		
	            		output.writeUTF(responseToClient);
	            		output.flush();
	        		}

            		break;            		
            	case "DELETE":
					//Fix replication degree
					FilesMap.removeFile(words[2]);
					String peersLit = "";
					networkPeersList=Network.getPeers();
            		for (String peer : networkPeersList) {
                	    if (!(peer.equals(words[1]))) {
                	    	peersLit = peersLit.concat(peer + " ");
                	    }
               	    }
            		output.writeUTF(peersLit);
            		break;
            		
            	case "RECLAIM":
            		String peer=words[1];
            		
            		for(int i=2;i<words.length;i++) {
            			int achievedReplication=FilesMap.getAchievedReplicationDegree(words[i]);
            			FilesMap.setAchievedReplicationDegree(words[i], achievedReplication-1);
            			FilesMap.removePeer(words[i], peer);
            			
            			System.out.println("File that was removed :"+words[i]);            			
            			System.out.println("Desired replication degree:"+FilesMap.getdesiredReplicationDegree(words[i]));
            			System.out.println("Achieved replication degree:"+FilesMap.getAchievedReplicationDegree(words[i]));
            			System.out.println("Peer that removed the file is:"+peer);
            			System.out.println("Peers that still store the file are:"+FilesMap.getPeers(words[i]));
            			
            			
            			if(FilesMap.getAchievedReplicationDegree(words[i])<FilesMap.getdesiredReplicationDegree(words[i])) {
            				
            				Socket peerSocket;
            				
            				DataOutputStream backupOutput;
            				
            				
            				ArrayList<String> peersThatStillStoreFile=FilesMap.getPeers(words[i]);
            				networkPeersList=Network.getPeers();
            				//initiate the backup protocol
							
								
								
							for(String p:peersThatStillStoreFile) {
								peerSocket=new Socket("localhost",Integer.parseInt(p));
								backupOutput = new DataOutputStream(peerSocket.getOutputStream());
								
								String backupCommand="RECLAIM_BACKUP" + " " + words[i] + " " + FilesMap.getdesiredReplicationDegree(words[i]);
								System.out.println("sending to peer "+p+ "to back up the file called "+words[i]);
								
								System.out.println("BEFORE SENDING:"+backupCommand);
								
								
								backupOutput.writeUTF(backupCommand);
								backupOutput.flush();
								
								backupOutput.close();
								peerSocket.close();
									
							}
								
									
            			}
        			}
            	break;
            	case "SAVED":


            			System.out.println("start SAVED");
            			
	            		//if file !exists -> create file
        				synchronized(FilesMap.class){
	            			if(!(FilesMap.exists(words[2]))) {
			            		ArrayList<String> peersList = new ArrayList<String>();
			            		peersList.add(words[1]);
			            		FileCopy fc = new FileCopy(words[2], peersList , Integer.parseInt(words[3]));
			            		FilesMap.addFile(words[2], fc);
			            		
		            		}
	            			
		            		//if file exists -> add peer to list of peers
	            			else if(FilesMap.getPeers(words[2]).contains(words[1])==false) {	//if peer isnt yet registered for that file
	            				FilesMap.addPeer(words[2], words[1]);
	            			}
	            				
        				}

	            		
	            		
	            		FilesMap.printFiles();

            		break;
            	case "PRINT":

            		FilesMap.printFiles();
            		
            		/*
            		String fil_name = words[2];
            		System.out.println("FM SIZE: " + FilesMap.files.size());
            		if(FilesMap.files.size()>0) {
	            		System.out.println("Des: " + FilesMap.getdesiredReplicationDegree(fil_name));
	            		System.out.println("Ach: " + FilesMap.getAchievedReplicationDegree(fil_name));
	            		if(FilesMap.getdesiredReplicationDegree(fil_name) > FilesMap.getAchievedReplicationDegree(fil_name)) {
	            			System.out.println("Dif: " + (FilesMap.getdesiredReplicationDegree(fil_name) - FilesMap.getAchievedReplicationDegree(fil_name)));
	            		}
            		}
            		*/            		
            		
            		break;
            	case "CHECKREPDEG":
            		System.out.println("CRD: " + words[2]);
            		String peer_id = words[1];
            		String file_name = words[2];
            		String rd = words[3];
            		String fp = words[4];
            		String initiator = words [5];
            		/*
            		System.out.println("Peers: " + Network.peers);
            		System.out.println("FM_SIZE: " + FilesMap.files.size());
            		System.out.println("NTW_SIZE: " + Network.peers.size());
            		System.out.println("Peer_id: " + peer_id);
            		if(Network.peers.size()>0){
            		System.out.println("Last: " + Network.peers.get(Network.peers.size()-1));}
            		*/

            		if(FilesMap.files.size()>0 && Network.peers.size()>0) {
            			while(Network.counter > 0) {
            				
            			
	            		//System.out.println("Des: " + FilesMap.getdesiredReplicationDegree(file_name));
	            		//System.out.println("Ach: " + FilesMap.getAchievedReplicationDegree(file_name));
	            		if(FilesMap.getdesiredReplicationDegree(file_name) > FilesMap.getAchievedReplicationDegree(file_name)) {
	            			System.out.println("Dif: " + (FilesMap.getdesiredReplicationDegree(file_name) - FilesMap.getAchievedReplicationDegree(file_name)));
	            			Socket peerSocket = new Socket("localhost", Integer.parseInt(initiator)); //TODO: improve for inet address
	            			Thread.sleep(1000);	
		        	    	DataOutputStream peerOutput = new DataOutputStream(peerSocket.getOutputStream());		        	    	
		        	    	peerOutput.writeUTF("BACKUP " + fp + " " + rd );
		        	    	Network.counter--;		        	    	
	            		}
            			}
            		}
            		    		
            		break;
            	case "PRINT1":
            		Network.PrintPeers();
            		break;
        		default:
            	    output.writeUTF("Command not available: " + words[0]);
            	    break;
            		}            
            
         } catch (IOException | InterruptedException ex) {
			 //EOF exception
         }
      }
   }

