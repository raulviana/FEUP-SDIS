import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class RepeatDeleteMessage implements Runnable {

    private String header;
    private int time;
    private int counter;

    public RepeatDeleteMessage(String header, int time) {
        this.header = header;
        this.time = time;
        this.counter = 1;
    }

    @Override
    public void run() {
        System.out.println("SEND: " + header);
        try {
            Peer.getMDB().sendMessage(header.getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.counter += 1;
        if(this.counter < 3) {
            Peer.getExecutor().schedule(this, this.time, TimeUnit.SECONDS);
        }
    }
}
