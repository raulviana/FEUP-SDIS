
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class RestoreChunkReply implements Runnable {
    private String senderID;
    private String fileID;
    private int chunkNR;

    public RestoreChunkReply(String senderID, String fileID, int chunkNR) {
        this.senderID = senderID;
        this.fileID = fileID;
        this.chunkNR = chunkNR;
    }

    @Override
    public void run() {

        for (int i = 0; i < Peer.getStorage().getStoredChunks().size(); i++) {
            if (sameChunk(Peer.getStorage().getStoredChunks().get(i).getFileID(),
                    Peer.getStorage().getStoredChunks().get(i).getID())) {
                String version = "1.0";
                String header = "CHUNK " + version + " " + this.senderID + " " + this.fileID + " " + this.chunkNR
                        + "\r\n\r\n";
                
                try {
                    byte[] msgHeader = header.getBytes("US-ASCII");
                   
                    String chunkPath = Peer.get_accessPoint() + "/" + fileID + "_" + chunkNR;

                    Path path = Paths.get(chunkPath);
                  
                    byte[] body = Files.readAllBytes(path);
                    byte[] correctedBody = Arrays.copyOfRange(body, 27, body.length);
                    
                    /*
                    Magical number 27:
                        The chunk was being read with extra 27 bytes of gibberish in its beginning. After lots of trying and error,
                         we couldn't find the origin of this inconsistency. This is just a patch to make it work. Further work will
                          be done to explain the problem.
                    */

                    byte[] message = new byte[msgHeader.length + correctedBody.length];
                    System.arraycopy(msgHeader, 0, message, 0, msgHeader.length);
                    System.arraycopy(correctedBody, 0, message, msgHeader.length, correctedBody.length);

                    Peer.getMDR().sendMessage(message);
                    System.out.println("SENT: " + header);
                    } 
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }   
        }
    }

    private boolean sameChunk(String fileID, int chunkNR){
        if (this.fileID.equals(fileID) && this.chunkNR == chunkNR) return true;
        else return false;
    } 
}
