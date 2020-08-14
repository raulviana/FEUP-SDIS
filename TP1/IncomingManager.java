
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class IncomingManager implements Runnable {

    private byte[] msg;

    public IncomingManager(byte[] message) {
        this.msg = message;
    }

    public void run() {
        // prepare message

        String strmessage = new String(this.msg, 0, this.msg.length);
        String trimMessage = strmessage.trim();
        String[] messageList = trimMessage.split(" ");

        // process message
        switch (messageList[0]) {
            case "PUTCHUNK":
                putchunk();
                break;
            case "STORED":
                stored();
                break;
            case "GETCHUNK":
                restore(messageList);
                break;
            case "CHUNK":
                chunk();
                break;
            case "DELETE":
                delete(messageList);
                break;
            case "REMOVED":
                removed(messageList);
        }

    }

    private void removed(String[] messageList) {
        List<byte[]> headerPlusBody = decepateHeaderBody();
        byte[] header = headerPlusBody.get(0);
        byte[] body = headerPlusBody.get(1);

        // transform header in list to facilitate
        String strHeader = new String(header, 0, header.length);
        String trimHeader = strHeader.trim();
        String[] headerList = trimHeader.split(" ");
        // headerList[1] = version
        // headerList[2] = senderID
        // headerList[3] = fileID
        // headerList[4] = cunhkNr
        if(Peer.getPeer_ID() != Integer.valueOf(messageList[2])){
            
            Peer.getStorage().decStoredOccurrences(messageList[3], Integer.valueOf(headerList[4]));
            System.out.println("RECEIVED: " + headerList[1] + " " + headerList[2] + " " + headerList[3] + " " + headerList[4]);
            Random random = new Random();
            Peer.getExecutor().schedule(new RemovedChunkReply(headerList[3], headerList[4]), random.nextInt(401), TimeUnit.MILLISECONDS);
        }
    }

    private void delete(String[] messageList) {
        // messageList[1] = version
        // messageList[2] = senderID
        // messageList[3] = fileID

        if(Peer.getPeer_ID() != Integer.valueOf(messageList[2])){
            Peer.getStorage().deleteChunks(messageList[3]);
            System.out.println("RECEIVED: " + messageList[0] + " " + messageList[1] + " " + messageList[2] + " " + messageList[3]);
        }           
    }

    private void chunk() {
        List<byte[]> headerPlusBody = decepateHeaderBody();
        byte[] header = headerPlusBody.get(0);
        byte[] body = headerPlusBody.get(1);

        // transform header in list to facilitate
        String strHeader = new String(header, 0, header.length);
        String trimHeader = strHeader.trim();
        String[] headerList = trimHeader.split(" ");
        // headerList[1] = version
        // headerList[2] = senderID
        // headerList[3] = fileID
        // headerList[4] = cunhkNr
        Chunk chunk = new Chunk(Integer.parseInt(headerList[4]), headerList[3], body, body.length);
        if (Peer.getPeer_ID() != Integer.valueOf(headerList[2])) {// this peer is not the original initiator peer
            
            System.out.println("RECEIVED: CHUNK " + headerList[1] + " " + headerList[2] + " " + headerList[3] + " " + headerList[4]);

            Peer.getStorage().addToStoredChunks(chunk);

            storeRestoredChunk(chunk);
        }
        else{//this is the original initiator peer
            Peer.getStorage().getProcessingChunks().add(chunk);
            for (int i = 0; i < Peer.getStorage().getFileInfos().size(); i++){
                if(Peer.getStorage().getFileInfos().get(i).getID().equals(headerList[3]) ){
                    if(Peer.getStorage().getProcessingChunks().size() == Peer.getStorage().getFileInfos().get(i).getChunks().size()){//Se todos os chunks já foram recebidos
                        reconstructFile(headerList[2], Peer.getStorage().getFileInfos().get(i).getFile().getPath());
                        Peer.getStorage().cleanProcessingChunks();
                        break;
                    }
                } 
            } 
        }
    }

    private void storeRestoredChunk(Chunk chunk) {
        byte[] body = chunk.getData();
        String filename = Peer.get_accessPoint() + "/" + chunk.getFileID() + "_"
                + Integer.toString(chunk.getID());
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
    
            FileOutputStream fout = new FileOutputStream(filename);
            fout.write(body);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void putchunk() {
        List<byte[]> headerPlusBody = decepateHeaderBody();
        byte[] header = headerPlusBody.get(0);
        byte[] body = headerPlusBody.get(1);

        // transform header in list to facilitate
        String strHeader = new String(header, 0, header.length);
        String trimHeader = strHeader.trim();
        String[] headerList = trimHeader.split(" ");
        String directory = Peer.get_accessPoint();
        // headerList[1] = version
        // headerList[2] = senderID
        // headerList[3] = fileID
        // headerList[4] = cunhkNr
        // headerList[5] = replicationDegree
        String code = headerList[3] + "_" + headerList[4];
        //save chunk
        if(Integer.parseInt(headerList[2]) == Peer.getPeer_ID()){
            System.out.println("ALERT: Won't save chunks of my own files");
            return;
        }
        if(Peer.getStorage().getSpaceAvailable() < body.length){
            System.out.println("Not enough space to store this chunk");
            return;
        }
        
        Chunk chunk = new Chunk(Integer.parseInt(headerList[4]), headerList[3], Integer.parseInt(headerList[5]), body.length);
        
        if(! Peer.getStorage().addToStoredChunks(chunk)){
            System.out.println("Chunk already in this peer");
            return;
        }
        else{
            try {
                //saves chunk in disk
                Files.createDirectories(Paths.get(directory));
                String filename = directory + "/" + code;
                FileOutputStream fileOut = new FileOutputStream(filename);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                
                out.writeObject(body);
                out.close();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //updates storage variables
            Peer.getStorage().decAvailableSpace(body.length);

            Peer.getStorage().addRemote(headerList[3], headerList[4]);

            Random random = new Random();
            Integer delay = random.nextInt(401);
            Peer.getExecutor().schedule(new PutChunkOKReplyThread(headerList[1], headerList[3], headerList[4]), delay, TimeUnit.MILLISECONDS); 
        }
    }

    private void stored(){//GETCHUNK message
        List<byte[]> headerPlusBody = decepateHeaderBody();
        byte[] header = headerPlusBody.get(0);

        // transform header in list to facilitate
        String strHeader = new String(header, 0, header.length);
        String trimHeader = strHeader.trim();
        String[] headerList = trimHeader.split(" ");
        // headerList[1] = version
        // headerList[2] = senderID
        // headerList[3] = fileID
        // headerList[4] = cunhkNr
        // headerList[5] = replicationDegree

        if(Peer.getPeer_ID() != Integer.parseInt(headerList[2])){
            Peer.getStorage().addRemote(headerList[3], headerList[4]);
            System.out.println("RECEIVED: STORED " + headerList[2] + " " + headerList[3] + " " + headerList[4]);
        }
    }

    private void restore(String[] messageList){
        // messageList[1] = version
        // messageList[2] = senderID
        // messageList[3] = fileID
        // messageList[4] = cunhkNr
        if(Peer.getPeer_ID() != Integer.parseInt(messageList[2])){
            Random random = new Random();
            System.out.println("RECEIVED: GETCHUNK " + messageList[1] + " " + messageList[2] + " " + messageList[3] + " " + messageList[4]);
            Peer.getExecutor().schedule(new RestoreChunkReply(messageList[2], messageList[3], Integer.parseInt(messageList[4])), random.nextInt(401), TimeUnit.MILLISECONDS);
        }

    }

    private List<byte[]> decepateHeaderBody(){
        int i;
        //search for the end of header
        for(i =0; i < this.msg.length - 4; i++){
            if(this.msg[i] == 0xD && this.msg[i+1] == 0xA && this.msg[i+2] == 0xD && this.msg[i+3] == 0xA){
                break;//end of header found
            }
        }
        byte[] header = Arrays.copyOfRange(this.msg, 0, i);
        byte[] body = Arrays.copyOfRange(this.msg, i + 4, this.msg.length);
        List<byte[]> headerbody = new ArrayList<>();
        headerbody.add(header);
        headerbody.add(body);
        return headerbody;
    }


    private void reconstructFile(String senderID, String file_path) {
        int number = 1;
        String filename = file_path;

        
        while(number <= Peer.getStorage().getProcessingChunks().size()){
          
            for(Chunk chunk : Peer.getStorage().getProcessingChunks()){
            
                if(chunk.getID() == number){
                     
                    try {
                        
                        File file = new File(filename);
                        FileOutputStream fout = new FileOutputStream(file, true);
                        fout.write(chunk.getData(), 0, chunk.getData().length);
                        fout.flush();
                        fout.close();
                    }
                    catch (IOException e) {
                            e.printStackTrace();
                    }
                number += 1;
                break;
                }
                
            }
        }
    }
}