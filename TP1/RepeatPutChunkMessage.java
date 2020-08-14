import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RepeatPutChunkMessage implements Runnable {

    private byte[] message;
    private int time;
    private String key;
    private int replication_degree;
    private int counter;

    public RepeatPutChunkMessage(byte[] message, int time, String fileID, int chunkNR, int replication_degree){
        this.message = message;
        this.time = time;
        this.key = fileID + "_" + chunkNR;
        this.replication_degree = replication_degree;
        this.counter = 1;
    }

    @Override
    public void run(){
        int remoteOcurrences = Peer.getStorage().getRemoteOcurrences().get(this.key);

        if(remoteOcurrences < replication_degree){
            System.out.println("SENT PUTCHUNK tries: " + counter);
            Peer.getMDB().sendMessage(message);
            this.time = this.time * 2;
            this.counter += 1;

            if(this.counter < 6) {
                Peer.getExecutor().schedule(this, this.time, TimeUnit.SECONDS);
            }
        }
    }

}
