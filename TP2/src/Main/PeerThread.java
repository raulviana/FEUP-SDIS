package Main;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.lang.reflect.Array;

import utils.FileUtil;
import utils.FilesMap;
import utils.Network;

public class PeerThread extends Thread {
	
   private Socket clientSocket;
   private Socket serverSocket;
   private int myPort;
   private String serverName;
   private int serverPort;
   
   public PeerThread(Socket clientSocket, int port, String serverName, int serverPort) {
       this.clientSocket = clientSocket;
       this.serverName = serverName;
       this.serverPort = serverPort;
       this.myPort = port;
   }

   public void run() {
    
         try {
        	 
   	      	Socket serverSocket = new Socket(serverName, serverPort); //socket to communicate with server      
         	 
	    	DataInputStream servInput = new DataInputStream(serverSocket.getInputStream());
	    	DataOutputStream servOutput = new DataOutputStream(serverSocket.getOutputStream());
	    	
	    	DataInputStream clInput = new DataInputStream(clientSocket.getInputStream());
	    	DataOutputStream clOutput = new DataOutputStream(clientSocket.getOutputStream());
        	        	
			String sentence = clInput.readUTF(); //testAp command
	    	String[] words = sentence.split("\\s+");
            System.out.println(" :: Command " + words[0]);
    		String answ;
	    	
            switch (words[0]) {
            //testApp commands
            	case "REGISTER":
            		//register peer on server
            		servOutput.writeUTF("REGISTER " + String.valueOf(myPort));
            		break;
            		
            	case "RECLAIM_BACKUP":
            		//Ask Server for peer(s)
            		servOutput.writeUTF("BACKUP " + myPort + " " + words[2]);
            		String peerss = servInput.readUTF();
            		System.out.println("Server answered for backup: " + peerss);
            		//ArrayList<Integer> peers;
            		String[] peersLists = peerss.split("\\s+");
            		String fileToSend=myPort+"/"+"backedup files"+"/"+ words[1];
           		
            		for(int i=0; i<peersLists.length; i++)	//backing up in every registered peers
            		{
                		//Establish connection with peer(s)
	            		Socket peerSocket = new Socket("localhost", Integer.parseInt(peersLists[i])); //TODO: improve for inet address
	            		
	            		//Transfer data
	        	    	DataOutputStream peerOutput = new DataOutputStream(peerSocket.getOutputStream());
	        	    	
	        	    	File sendF = new File(fileToSend);
	        	    	peerOutput.writeUTF("SAVE " + String.valueOf(myPort) + " " + sendF.getName() + " " + words[2] +" "+ words[1]);      
	        	    	
	        	    	  
	        	    	
	        	    	File myFile = new File (fileToSend); 
	        	    	byte [] mybytearray  = new byte [(int)myFile.length()];
	        	    	FileInputStream fis = new FileInputStream(myFile);
	        	    	BufferedInputStream  bis = new BufferedInputStream(fis);
	        	        bis.read(mybytearray,0,mybytearray.length);
	        	        OutputStream os = peerSocket.getOutputStream();
	        	        System.out.println("Sending " + fileToSend + "(" + mybytearray.length + " bytes)");
	        	        os.write(mybytearray,0,mybytearray.length);
	        	        os.flush();
	        	        
	        	        
	        	        
	        	        if (bis != null) bis.close();
	        	        if (os != null) os.close();
	        	        if (peerSocket!=null) peerSocket.close();
            		}
            	
            		break;
            	case "BACKUP":  
            		
            		//Ask Server for peer(s)
            		servOutput.writeUTF("BACKUP " + myPort + " " + words[2]);
            		String peers = servInput.readUTF();
            		System.out.println("Server answered for backup: " + peers);
            		//ArrayList<Integer> peers;
            		String[] peersList = peers.split("\\s+");


            		String sendFile= words[1];
            		File sendF = new File(sendFile);
            		

            		for(int i=0; i<peersList.length; i++)	//backing up in every registered peers
            		{
                		//Establish connection with peer(s)
	            		Socket peerSocket = new Socket("localhost", Integer.parseInt(peersList[i])); //TODO: improve for inet address
	            		
	            		//Transfer data
	        	    	DataOutputStream peerOutput = new DataOutputStream(peerSocket.getOutputStream());

	        	    	//String sendFile= words[1];
	        	    	//File sendF = new File(sendFile);
	        	    	peerOutput.writeUTF("SAVE " + String.valueOf(myPort) + " " + sendF.getName() + " " + words[2] + " " + sendFile);        	    	

	        	    	File myFile = new File (sendFile); 
	        	    	byte [] mybytearray  = new byte [(int)myFile.length()];
	        	    	FileInputStream fis = new FileInputStream(myFile);
	        	    	BufferedInputStream  bis = new BufferedInputStream(fis);
	        	        bis.read(mybytearray,0,mybytearray.length);
	        	        OutputStream os = peerSocket.getOutputStream();
	        	        System.out.println("Sending " + sendFile + "(" + mybytearray.length + " bytes)");
	        	        os.write(mybytearray,0,mybytearray.length);
	        	        os.flush(); 
	        	        
	        	        /*
	        	        if(i==peersList.length-1) {
	                		Socket serverSocket2 = new Socket(serverName, serverPort);
	    	                new DataOutputStream(serverSocket2.getOutputStream()).writeUTF("CHECKREPDEG " + sendF.getName());
	    	                serverSocket2.close();
	        	        }
	        	        */
	        	        
	        	        
	        	        
	        	        
	        	        if (bis != null) bis.close();
	        	        if (os != null) os.close();
	        	        if (peerSocket!=null) peerSocket.close();
	        	                	        
            		}

            		
            		if(Peer.backedUpFiles.contains(sendFile)==false)
            			Peer.backedUpFiles.add(sendFile);
            		

            		servOutput.flush();
            		//servOutput.close();
            		//FilesMap.printFiles();

           		

            		break;
            	case "STARTRESTORE":
            		String fileName=words[1];
            		String command="RESTORE"+" "+myPort+" "+fileName;
            		
            		servOutput.writeUTF(command);
            		servOutput.flush();
            		
            		String serverResponse=servInput.readUTF();
            		
            		String[] allPeers=serverResponse.split(" ");
            		
            		Socket socket; //Socket to send requests To Peers
            		
            		DataInputStream input;
        	    	DataOutputStream output;
        	    	boolean peerDidReply=false;
        	    	byte[] networkBuffer= new byte[100000000];
        	    	byte[] fileContents=new byte[0];
        	    	
            		for(int i=0;i<allPeers.length;i++) {
            			
            			if(peerDidReply) 
            				break;
            			
            			String[]peerInfo=allPeers[i].split(",");
            			
            			
            			String peerIP=peerInfo[0];
            			String peerPort=peerInfo[1];
            			
            			socket= new Socket(InetAddress.getByName(peerIP),Integer.parseInt(peerPort));
            			
            			
            			input = new DataInputStream(socket.getInputStream());
            			output = new DataOutputStream(socket.getOutputStream());
            			
            			
            			
            			output.writeUTF(command);
            			output.flush();
            			
            			
            			
            			socket.setSoTimeout(4000);
            			
            			
            			peerDidReply=true;
            			try {
            			
            			
            			int fileSize=input.readInt();
            			
            			fileContents=new byte[fileSize];
            			
            			input.readFully(fileContents);
            			
            			
            			
            			System.out.println("RECEIVED: file size:"+fileSize);
            			
            			
            			}catch(SocketTimeoutException e) {peerDidReply=false;}
            			
            		}
            		
					if(peerDidReply) {
						
						String restoredFilesPath=myPort+"/"+"restored files";
					
						new File(restoredFilesPath).mkdirs();
						FileUtil.save(fileContents, restoredFilesPath+"/"+fileName);
					}
            		break;

            	case "RESTORE":
            		String fName=words[2];
            		String backedUpFilesPath=myPort+"/"+"backedup files";
            		
            		File file=new File(backedUpFilesPath+"/"+fName);
          		   	System.out.println("file path:"+backedUpFilesPath+"/"+fName);
          		   	System.out.println("file exists:"+file.exists());
            		
          		   	if(!file.exists()) 
          		   		return;
          		   
          		   	
          		   	networkBuffer=FileUtil.read(backedUpFilesPath+"/"+fName);
            		System.out.println("I am sending the file with size:"+networkBuffer.length);
          		   	
            		clOutput.writeInt(networkBuffer.length);
            		clOutput.flush();
            		
            		clOutput.write(networkBuffer,0,networkBuffer.length);
            		clOutput.flush();

            		break;            		
            	case "DELETE":
					//Inform server and contact all peers to delete this file
					String askDelete = "DELETE " + String.valueOf(myPort) + " " + words[1];
					System.out.println("askDelele: " + askDelete);
					servOutput.writeUTF(askDelete);
					String response = servInput.readUTF();
					String[] peersToContact = response.split("\\s+");
					
					for(int i = 0; i < peersToContact.length; i++){
						new PeerDeleteThread(peersToContact[i], words[1]).start();
					}
					break;
				case "DELETEFILE":
					File fot = new File (String.valueOf(myPort) + "/" + "backedup files/" + words[1]);
					if(fot.delete())
						{
						System.out.println("File " + words[1] + " in " + myPort + " deleted!");
						Peer.setCurrentStorageInBytes(FileUtil.calculateFolderSize(myPort+"/"+"backedup files"));
						}
					else System.out.println("File " + words[1] + " not deleted");
					break;
            	case "SAVE":
            		//save copy of backup_file
            		String peer_id = words[1];
            		String file_id = words[2];

            		String repDeg = words[3]; 
            	    
            		
            		if(Peer.backedUpFiles.contains(file_id))//the peer should never save files it has initiated a backup for
            			return;
            		
            		
            	    

            		
            		String fp = words[4];
            	    InputStream is = clientSocket.getInputStream();
            	    String recFile = String.valueOf(myPort) + "/" + "backedup files" + "/" + file_id;
            	    File f = new File (String.valueOf(myPort) + "/" + "backedup files");
      		      	f.mkdirs();
            	    

            	    int bytesRead, counter;
            	    byte [] bytearray  = new byte [6000000];
            	    bytesRead = is.read(bytearray,0,bytearray.length);
            	    counter = bytesRead;
            	    
            	    System.out.println("max Allowed storage (inside SAVE Case):"+Peer.maxAllowedStorageInBytes);
            	    System.out.println("current storage (inside SAVE Case):"+Peer.getCurrentStorageInBytes());
            	    
            	    do {
	        	         bytesRead = is.read(bytearray, counter, (bytearray.length-counter));
	        	         if(bytesRead >= 0) counter += bytesRead;
           	      	} while(bytesRead > -1);

            	    System.out.println("file size:"+counter);
            	    
            	    
            	    if(Peer.getCurrentStorageInBytes()+counter>Peer.maxAllowedStorageInBytes) {
            	    	System.out.println("this peer does not have the required space to save the file");
            	    	return;
            	    }

            	    FileOutputStream file_os = new FileOutputStream(recFile);
            	    BufferedOutputStream buf_os = new BufferedOutputStream(file_os);
            	    

	        	    buf_os.write(bytearray, 0 , counter);
	        	    buf_os.flush();
	        	    System.out.println("File downloaded: " + recFile);
	          		servOutput.writeUTF("SAVED " + myPort + " " + file_id + " " + repDeg);
            	    
              		Peer.setCurrentStorageInBytes(FileUtil.calculateFolderSize(myPort+"/"+"backedup files"));
              		System.out.println("current storage after saving the file (inside SAVE Case):"+Peer.getCurrentStorageInBytes());
              		              		  
              		  //check replication degree
              		  //System.out.println("Myport: " + myPort);
              		  Socket serverSocket2 = new Socket(serverName, serverPort);
              		  new DataOutputStream(serverSocket2.getOutputStream()).writeUTF("CHECKREPDEG " + myPort + " " + file_id + " " + repDeg + " " + fp + " " + peer_id);
              		  serverSocket2.close();              		  
            	      

            	      if (file_os != null) file_os.close();
            	      if (buf_os != null) buf_os.close();
            	      if (clientSocket != null) clientSocket.close();            	    
            	    break;
				case "RECLAIM":
					//words[0] -> Command
					//words[1] -> IP
					//words[2] -> PeerPort
					//words[3] -> Space to be freed
            		int allowedSpace =Integer.parseInt(words[1]) ;
            		
            		Peer.maxAllowedStorageInBytes=allowedSpace*1000;//multiplied by 1000 because the allowedSpace will be passed as KB from the professor's point of view
            		
            		System.out.println("max storage has been updated:"+Peer.maxAllowedStorageInBytes);
                    System.out.println("current storage:"+Peer.getCurrentStorageInBytes());
            		
            		String sourcePeer = words[1];
					String path = String.valueOf(myPort) + "/" + "backedup files/";
					String messageToServer = "RECLAIM"+" "+myPort;
					List<String>deletedFiles = new ArrayList<>();
            		//delete files
            		//if deletion = 0 -> remove all
					if(allowedSpace == 0) {
            			deletedFiles = utils.FileUtil.deleteAllFiles(path);
            		}
            		//else delete deletion number of files
            		else{
						deletedFiles = utils.FileUtil.deleteFiles(allowedSpace,myPort);
						
					} 
					
					
					
					if(deletedFiles.size()!=0){
						Peer.setCurrentStorageInBytes(FileUtil.calculateFolderSize(myPort+"/"+"backedup files"));
						
						System.out.println("current storage after deletion:"+Peer.getCurrentStorageInBytes());	
						for(String fPath : deletedFiles){
								messageToServer += " " + fPath;
							}
							
						//inform server of deleted files
						servOutput.writeUTF(messageToServer);
					}
            		
            	 	
            		break;
            	    
        		default :
            	    System.out.println("Instruction not available: " + words[0]);
            	    break;
		        
			}
            clientSocket.close();
            serverSocket.close();
         } catch (IOException ex) {
             System.out.println("Server exception: " + ex.getMessage());
             ex.printStackTrace();
         }
		}
}

