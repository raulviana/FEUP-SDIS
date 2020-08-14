import java.util.ArrayList;

public class PutChunkOKReplyThread implements Runnable {


    private String version;
    private String fileID;
    private String chunkNR;

    PutChunkOKReplyThread(String version, String fileId, String chunkNr){
        this.version = version;
        this.fileID = fileId;
        this.chunkNR = chunkNr;    
    }

    @Override
    public void run() {
       
        String return_putchunk_message = "STORED " + version + " " + String.valueOf(Peer.getPeer_ID()) + " " + fileID + " " + chunkNR + "\r\n\r\n";
        System.out.println("SENT: " + return_putchunk_message);
        Peer.getMC().sendMessage(return_putchunk_message.getBytes());
    }
}