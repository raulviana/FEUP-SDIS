
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RemovedChunkReply implements Runnable{
    private String fileID;
    private String chunkNR;

    public RemovedChunkReply(String fileID, String chunkNR){
        this.fileID = fileID;
        this.chunkNR = chunkNR;
    }


	@Override
	public void run() {
        
        boolean hasChunk=false;
        int wantedReplicationDegree = 0;

        for(int i = 0; i< Peer.getStorage().getStoredChunks().size(); i++){
            if(Peer.getStorage().getStoredChunks().get(i).getFileID().equals(fileID) == true){
                hasChunk = true;
                wantedReplicationDegree = Peer.getStorage().getStoredChunks().get(i).get_RD_asked();
            } 
        }
        if(hasChunk){
            String fileID_chunkNR = fileID + '_' + chunkNR;
            if(Peer.getStorage().getStoredChunks().size() < wantedReplicationDegree){
                int chunkSize = 64000;
                byte[] buffer = new byte[chunkSize];
                byte[] body = new byte[chunkSize];
                File file = new File(Peer.getPeer_ID() + "/" + fileID + "_" + chunkNR);
                try (FileInputStream fis = new FileInputStream(file);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {

                    int bytesAmount;
                    while ((bytesAmount = bis.read(buffer)) > 0) {
                        body = Arrays.copyOf(buffer, bytesAmount);
                        buffer = new byte[chunkSize];
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                String header = "PUTCHUNK " + " " + Peer.getPeer_ID() + " " + fileID + " " + chunkNR + " " + wantedReplicationDegree + "\r\n\r\n";
                System.out.println("Sent " + "PUTCHUNK " + " " + Peer.getPeer_ID() + " " + fileID + " " + chunkNR + " " + wantedReplicationDegree);
            }
        }
	}



}
